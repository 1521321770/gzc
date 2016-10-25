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
 *REST����������ǰ�����������REST API�Ľ�������ܵ������ã�.
 *
 *@author gao.fei
 */
public class RestController extends Action{
    /**
     *Log4j��־��¼����.
     */
    private static final Logger LOGGER = Logger.getLogger(RestController.class);
    /**
     *HTTP����ģʽ����.
     */
    private static SchemeRegistry schemeRegistry = new SchemeRegistry();
    /**
     *HTTP���Ӷ˿ں�.
     */
    private static final int DEFAULT_PORT_NO = 8080;
    /**
     *��󲢷�������.
     */
    private static final int MAX_TOTAL = 8192, MAX_PER_ROUTE = 1024;
    static {
        schemeRegistry.register(new Scheme("http", DEFAULT_PORT_NO, PlainSocketFactory.getSocketFactory()));
    }
    /**
     *HttpClient���ӳع�����.
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
     *HTTP���󡢶�ȡ��ʱʱ��.
     */
    private static final int HTTP_CONNECTION_TIMEOUT = 30 *1000, // ����ʱʱ��
            HTTP_SO_TIMEOUT = 30 *1000; // ��ȡ��ʱʱ��
    static {
        client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, HTTP_CONNECTION_TIMEOUT);
        client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, HTTP_SO_TIMEOUT);
    }
    /**
     *REST API��ַ�ͷ���.
     */
    public static final String REST_URL = "restUrl", METHOD = "method", TIME_OUT_CONTROL = "timeoutControl",
            POLLING = "polling", REMOTE_IP = "remoteIp", REMOTE_PORT = "remotePort";
    /**
     *HTTP����.
     */
    public static final String GET = "get", POST = "post", PUT = "put", DELETE = "delete";
    /**
     *�������ݺ������õ������ݸ�ʽKEYֵ.
     */
    public static final String KEY_CONTENT_TYPE = "Content-Type", KEY_ACCEPT = "Accept";
    /**
     *�������ݸ�ʽ.
     */
    public static final String CONTENT_TYPE = "application/json; charset=utf-8";
    /**
     *�����õ����ݵĸ�ʽJson.
     */
    public static final String ACCEPT = "application/json; charset=utf-8";
    /**
     *�����õ����ݵĸ�ʽXML.
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
     *�����߼�������,ǿ������������д.
     *
     *@param mapping
     *           ӳ���ϵMAP
     *@param form
     *           ������
     *@param req
     *           ����
     *@param res
     *           ��Ӧ
     *@throws IOException
     *            ��д�쳣
     */
    protected void doAction(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res)
            throws IOException {
        JSONObject paramMap = HttpRequstParamHelper.getParams(req);
        // ��ñ���IP
        String hostAddr = req.getRemoteAddr();
        if ((hostAddr == null || hostAddr.length() == 0 || "unknown".equalsIgnoreCase(hostAddr)
                || "0:0:0:0:0:0:0:1".equals(hostAddr) || "127.0.0.1".equals(hostAddr))) {
            // ��������ȡ�������õ�IP
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
            // Զ����֤
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
            
            // ���ݷ������ͷ�����Ӧ��HTTP����
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
                request.addHeader(KEY_CONTENT_TYPE, CONTENT_TYPE); // �������ݸ�ʽ������
                request.addHeader(KEY_ACCEPT, ACCEPT); // ��������Ӧ���ݸ�ʽ������
                request.addHeader("X-Forwarded-For", hostAddr); // ����IP��ַ
                
                // �Ựʱ��
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
                    
                    // ����HTTP��Ӧ
                    if (response != null && response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                        // REST ����ɹ�
                        // ����Ӧ���ֱ��д��Servlet��Ӧ�У����ظ������
                        // resEntity.writeTo(res.getOutputStream());
                        res.getWriter().write(responseContent);
                    } else {
                        // REST ����ʧ��
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
                    // REST ����ʧ��
                    JSONObject json = new JSONObject();
                    json.put("flag", false);
                    json.put("errCode", "reqTimeout");
                    
      LOGGER.error("����ʱ�����Ժ����ԡ�\n" + "Method : " + request.getMethod() + "\n"
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
     *����HTTP GET ����.
     *
     *@param paramMap
     *           �����б�
     *@param builder
     *           URL builder
     *
     *@return HTTP GET ����
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
     *����HTTP POST ����.
     *
     *@param req
     *           HTTP����
     *@param paramMap
     *           �����б�
     *@param builder
     *           URL builder
     *
     *@return HTTP POST ����
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
     *����HTTP PUT ����.
     *
     *@param req
     *           HTTP����
     *@param paramMap
     *           �����б�
     *@param builder
     *           URL builder
     *
     *@return HTTP PUT ����
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
     *����HTTP DELETE ����.
     *
     *@param paramMap
     *           �����б�
     *@param builder
     *           URL builder
     *
     *@return HTTP DELETE ����
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
     *��ȡHttpClient���ӳض���.
     *
     *@return HttpClient���ӳض���
     */
    public static synchronized PoolingClientConnectionManager getConnectionManager() {
        return RestController.cm;
    }
    
}

