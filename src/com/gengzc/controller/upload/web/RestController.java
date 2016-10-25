package com.gengzc.controller.upload.web;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URISyntaxException;
import java.util.Enumeration;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.gengzc.controller.propertiesController.PropertiesIOUtil;

/**
 *REST控制器（在前端浏览器与后端REST API的交互中起管道的作用）.
 *
 *@author gao.fei
 */
public class RestController extends Action{
    /**
     *Log4j日志记录对象.
     */
    private static final Logger LOGGER = Logger.getLogger(RestController.class);
    /**
     *HTTP请求模式对象.
     */
    private static SchemeRegistry schemeRegistry = new SchemeRegistry();
    /**
     *HTTP连接端口号.
     */
    private static final int DEFAULT_PORT_NO = 8080;
    /**
     *最大并发连接数.
     */
    private static final int MAX_TOTAL = 8192, MAX_PER_ROUTE = 1024;
    static {
        schemeRegistry.register(new Scheme("http", DEFAULT_PORT_NO, PlainSocketFactory.getSocketFactory()));
    }
    /**
     *HttpClient连接池管理器.
     */
    private static PoolingClientConnectionManager cm = new PoolingClientConnectionManager(schemeRegistry);
    static {
        cm.setDefaultMaxPerRoute(MAX_PER_ROUTE);
        cm.setMaxTotal(MAX_TOTAL);
    }
    /**
     *HttpClient.
     */
    public static HttpClient client = new DefaultHttpClient(cm);
    /**
     *HTTP请求、读取超时时间.
     */
    private static final int HTTP_CONNECTION_TIMEOUT = 30 *1000, // 请求超时时间
            HTTP_SO_TIMEOUT = 30 *1000; // 读取超时时间
    static {
        client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, HTTP_CONNECTION_TIMEOUT);
        client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, HTTP_SO_TIMEOUT);
    }
    /**
     *REST API地址和方法.
     */
    public static final String REST_URL = "restUrl", METHOD = "method", TIME_OUT_CONTROL = "timeoutControl",
            POLLING = "polling", REMOTE_IP = "remoteIp", REMOTE_PORT = "remotePort";
    /**
     *HTTP方法.
     */
    public static final String GET = "get", POST = "post", PUT = "put", DELETE = "delete";
    /**
     *请求内容和期望得到的内容格式KEY值.
     */
    public static final String KEY_CONTENT_TYPE = "Content-Type", KEY_ACCEPT = "Accept";
    /**
     *请求内容格式.
     */
    public static final String CONTENT_TYPE = "application/json; charset=utf-8";
    /**
     *期望得到内容的格式Json.
     */
    public static final String ACCEPT = "application/json; charset=utf-8";
    /**
     *期望得到内容的格式XML.
     */
    public static final String ACCEPT_XML = "application/xml; charset=utf-8";

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) {
        res.setHeader("Pragma", "no-cache");
        res.setHeader("Cache-Control", "no-cache");
        res.setHeader("Expires", "0");
        res.setHeader("Content-Type", "text/xml; charset=utf-8");
        res.setCharacterEncoding("utf-8");
        try {
            Utils.showAllRequestParams(req);
            this.doAction(mapping, form, req, res);
        } catch (IOException e) {
            LOGGER.error("IOException occured");
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     *界面逻辑处理函数,强制在子类中重写.
     *
     *@param mapping
     *           映射关系MAP
     *@param form
     *           表单数据
     *@param req
     *           请求
     *@param res
     *           响应
     *@throws IOException
     *            读写异常
     */
    protected void doAction(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res)
            throws IOException {
        JSONObject paramMap = HttpRequstParamHelper.getParams(req);
        // 获得本机IP
        String hostAddr = req.getRemoteAddr();
        if ((hostAddr == null || hostAddr.length() == 0 || "unknown".equalsIgnoreCase(hostAddr)
                || "0:0:0:0:0:0:0:1".equals(hostAddr) || "127.0.0.1".equals(hostAddr))) {
            // 根据网卡取本机配置的IP
            Enumeration<NetworkInterface> allNetInterfaces;
            try {
                allNetInterfaces = NetworkInterface.getNetworkInterfaces();
                InetAddress inet = InetAddress.getLocalHost();
                while (allNetInterfaces.hasMoreElements()) {
                    NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
                    if (netInterface.isLoopback() || netInterface.isVirtual() || !netInterface.isUp()) {
                        continue;
                    }
                    Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
                    while (addresses.hasMoreElements()) {
                        inet = (InetAddress) addresses.nextElement();
                        if (inet != null && inet instanceof Inet4Address) {
                            hostAddr = inet.getHostAddress();
                            break;
                        }
                    }
                    if (hostAddr != null && !"127.0.0.1".equals(hostAddr) && !"".equals(hostAddr)) {
                        break;
                    }
                }
            } catch (SocketException e) {
                LOGGER.error(e.getMessage(), e);
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }

        String restUrl = paramMap.getString(REST_URL);
        if (StringUtils.isNotBlank(restUrl)) {
            if (!StringUtils.startsWith(restUrl, "/")) {
                restUrl = "/" + restUrl;
            }
            String[] splitUrl = StringUtils.split(restUrl, '/');
            String moduleName = splitUrl[0];

            System.out.println("----------" + moduleName +"---------");
            String serviceAddr = PropertiesIOUtil.getValue(moduleName);

            String[] addrArray = serviceAddr.split(":");
            String ip = addrArray[0];
            int port = DEFAULT_PORT_NO;
            if (addrArray.length > 1) {
                try {
                    port = Integer.parseInt(addrArray[1]);
                } catch (Exception e) {
                    port = DEFAULT_PORT_NO;
                    LOGGER.error(moduleName + "PORT_NO invalid");
                }
            }
            URIBuilder builder = new URIBuilder();
            // 远程认证
            try {
                String remoteIP = paramMap.getString(REMOTE_IP);
                String remotePort = paramMap.getString(REMOTE_PORT);
                if (StringUtils.isNotEmpty(remoteIP)) {
                    ip = remoteIP;
                }
                if (StringUtils.isNotEmpty(remotePort)) {
                    port = Integer.parseInt(remotePort);
                }
            } catch (JSONException e) {
                LOGGER.error("JSONException:" + e.getMessage());
            }

            builder.setScheme("http").setHost(ip).setPort(port).setPath(restUrl);
            String method = paramMap.getString(METHOD);
            HttpRequestBase request = null;
            
            // 根据方法类型发送相应的HTTP请求
            if (StringUtils.equalsIgnoreCase(method, GET)) {
                request = this.buildGetReq(paramMap, builder);
            } else if (StringUtils.equalsIgnoreCase(method, POST)) {
                request = this.buildPostReq(req, paramMap, builder);
            } else if (StringUtils.equalsIgnoreCase(method, PUT)) {
                request = this.buildPutReq(req, paramMap, builder);
            } else if (StringUtils.equalsIgnoreCase(method, DELETE)) {
                request = this.buildDeleteReq(paramMap, builder);
            }
            
            if (request != null) {
                long startTime = System.currentTimeMillis();
                request.addHeader(KEY_CONTENT_TYPE, CONTENT_TYPE); // 请求内容格式、编码
                request.addHeader(KEY_ACCEPT, ACCEPT); // 期望的响应内容格式、编码
                request.addHeader("X-Forwarded-For", hostAddr); // 本机IP地址
                
                // 会话时间
                Object polling = paramMap.get(POLLING);
                if (Boolean.TRUE.equals(polling) || "true".equalsIgnoreCase((String) polling)) {
                    request.addHeader("auth-keep", "false");
                }
                
                String language = Utils.getStrFromSession(req, "language");
                request.addHeader("language", language);
                Object timeoutControl = paramMap.get(TIME_OUT_CONTROL);
                if (!Boolean.TRUE.equals(timeoutControl) && !"true".equalsIgnoreCase((String) timeoutControl)) {
                    request.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 0);
                    request.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 0);
                }
                
                try {
                    HttpResponse response = client.execute(request);
                    HttpEntity resEntity = response.getEntity();
                    String responseContent = EntityUtils.toString(resEntity);
                    
                    // 处理HTTP响应
                    if (response != null && response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                        // REST 请求成功
                        // 将响应结果直接写入Servlet响应中，返回给浏览器
                        // resEntity.writeTo(res.getOutputStream());
                        res.getWriter().write(responseContent);
                    } else {
                        // REST 请求失败
                        JSONObject json = new JSONObject();
                        json.put("flag", false);
                        StatusLine status = response.getStatusLine();
                        json.put("errCode",
                                "<b><font color='red'>REST call Failed</font></b> : " + status.getStatusCode() + " - "
                                        + status.getReasonPhrase());
                        
      LOGGER.error("REST call Failed : "  + status.getStatusCode() + " - " 
              + status.getReasonPhrase() + "\n" + "Method : " + request.getMethod() + "\n"
              + "REST URL : " + request.getURI() + "\n");
      
                        res.getWriter().write(json.toString());
                        EntityUtils.consume(resEntity);
                    }
                } catch (ClientProtocolException e) {
                    LOGGER.error("ClientProtocolException:" + e.getMessage());
                } catch (IOException e) {
                    LOGGER.error("IOException:" + e.getMessage());
                    // REST 请求失败
                    JSONObject json = new JSONObject();
                    json.put("flag", false);
                    json.put("errCode", "reqTimeout");
                    
      LOGGER.error("请求超时，请稍后重试。\n" + "Method : " + request.getMethod() + "\n"
              + "REST URL : " + request.getURI() + "\n");
      
                    try {
                        res.getWriter().write(json.toString());
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                } finally {
                    long endTime = System.currentTimeMillis();
                }
            } else {
                LOGGER.error("invalid method, not in (get, post, put, delete)");
            }
        } else {
            LOGGER.error("restUrl was not set");
        }
    }

    /**
     *构建HTTP GET 请求.
     *
     *@param paramMap
     *           参数列表
     *@param builder
     *           URL builder
     *
     *@return HTTP GET 请求
     */
    @SuppressWarnings("unchecked")
    private HttpGet buildGetReq(JSONObject paramMap, URIBuilder builder) {
        HttpGet get = new HttpGet();
        Object dataObj = paramMap.get("data");
        if (dataObj != null && dataObj instanceof JSONObject) {
            JSONObject data = (JSONObject) dataObj;
            if (!data.isNullObject()) {
                Object paramObj = data.get("param");
                if (paramObj != null && paramObj instanceof JSONObject) {
                    JSONObject param = (JSONObject) paramObj;
                    if (!param.isNullObject()) {
                        Set<String> keySet = param.keySet();
                        for (String key : keySet) {
                            builder.setParameter(key, param.getString(key));
                        }
                    }
                }
            }
        }
        try {
            get.setURI(builder.build());
        } catch (URISyntaxException e) {
            LOGGER.error("URISyntaxException:" + e.getMessage());
        }
        return get;
    }

    /**
     *构建HTTP POST 请求.
     *
     *@param req
     *           HTTP请求
     *@param paramMap
     *           参数列表
     *@param builder
     *           URL builder
     *
     *@return HTTP POST 请求
     */
    @SuppressWarnings("unchecked")
    private HttpPost buildPostReq(HttpServletRequest req, JSONObject paramMap, URIBuilder builder) {
        HttpPost post = new HttpPost();
        try {
            Object dataObj = paramMap.get("data");
            if (dataObj != null && dataObj instanceof JSONObject) {
                JSONObject data = (JSONObject) dataObj;
                if (data != null && !data.isNullObject()) {
                    Object paramObj = data.get("param");
                    if (paramObj != null && paramObj instanceof JSONObject) {
                        JSONObject param = (JSONObject) paramObj;
                        if (!param.isNullObject()) {
                            Set<String> keySet = param.keySet();
                            for (String key : keySet) {
                                builder.setParameter(key, param.getString(key));
                            }
                        }
                    }
                }
                if (!data.isNullObject()) {
                    Object body = data.get("body");
                    if (body != null) {
                        StringEntity reqEntity = new StringEntity(body.toString(), req.getCharacterEncoding());
                        post.setEntity(reqEntity);
                    }
                }
            }
            post.setURI(builder.build());
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("UnsupportedEncodingException:" + e.getMessage());
        } catch (URISyntaxException e) {
            LOGGER.error("URISyntaxException:" + e.getMessage());
        }
        return post;
    }

    /**
     *构建HTTP PUT 请求.
     *
     *@param req
     *           HTTP请求
     *@param paramMap
     *           参数列表
     *@param builder
     *           URL builder
     *
     *@return HTTP PUT 请求
     */
    @SuppressWarnings("unchecked")
    private HttpPut buildPutReq(HttpServletRequest req, JSONObject paramMap, URIBuilder builder) {
        HttpPut put = new HttpPut();
        try {
            Object dataObj = paramMap.get("data");
            if (dataObj != null && dataObj instanceof JSONObject) {
                JSONObject data = (JSONObject) dataObj;
                if (!data.isNullObject()) {
                    Object paramObj = data.get("param");
                    if (paramObj != null && paramObj instanceof JSONObject) {
                        JSONObject param = (JSONObject) paramObj;
                        if (!param.isNullObject()) {
                            Set<String> keySet = param.keySet();
                            for (String key : keySet) {
                                builder.setParameter(key, param.getString(key));
                            }
                        }
                    }
                }
                if (!data.isNullObject()) {
                    Object body = data.get("body");
                    if (body != null) {
                        StringEntity reqEntity = new StringEntity(body.toString(), req.getCharacterEncoding());
                        put.setEntity(reqEntity);
                    }
                }
            }
            put.setURI(builder.build());
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("UnsupportedEncodingException:" + e.getMessage());
        } catch (URISyntaxException e) {
            LOGGER.error("URISyntaxException:" + e.getMessage());
        }
        return put;
    }

    /**
     *构建HTTP DELETE 请求.
     *
     *@param paramMap
     *           参数列表
     *@param builder
     *           URL builder
     *
     *@return HTTP DELETE 请求
     */
    @SuppressWarnings("unchecked")
    private HttpDelete buildDeleteReq(JSONObject paramMap, URIBuilder builder) {
        HttpDelete delete = new HttpDelete();
        JSONObject data = paramMap.getJSONObject("data");
        if (data != null && !data.isNullObject()) {
            Object paramObj = data.get("param");
            if (paramObj != null && paramObj instanceof JSONObject) {
                JSONObject param = (JSONObject) paramObj;
                if (!param.isNullObject()) {
                    Set<String> keySet = param.keySet();
                    for (String key : keySet) {
                        builder.setParameter(key, param.getString(key));
                    }
                }
            }
        }
        try {
            delete.setURI(builder.build());
        } catch (URISyntaxException e) {
            LOGGER.error("URISyntaxException:" + e.getMessage());
        }
        return delete;
    }

    /**
     *获取HttpClient连接池对象.
     *
     *@return HttpClient连接池对象
     */
    public static synchronized PoolingClientConnectionManager getConnectionManager() {
        return RestController.cm;
    }
    
}

