package com.gengzc.controller.upload.web;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import com.gengzc.controller.propertiesController.PropertiesIOUtil;

import net.sf.json.JSONObject;

/**
 * @author gengzhichao
 */
public class UploadServlet extends HttpServlet {

    /**
     * serialVersionUID.
     */
    private static final long serialVersionUID = 1L;
    /** RESP_SUCCESS. */
    private static final String RESP_SUCCESS = "{\"jsonrpc\" : \"2.0\","
            + " \"result\" : \"success\", \"id\" : \"id\"}";
    /** RESP_ERROR. */
    private static final String RESP_ERROR = "{\"jsonrpc\" : \"2.0\"," + " \"error\" : {\"code\": 101,"
            + " \"message\": \"Failed to open input stream.\"}, " + "\"id\" : \"id\"}";
    /** JSON. */
    public static final String JSON = "application/json";
    /**
     * HTTP���Ӷ˿ں�.
     */
    private static final int DEFAULT_PORT_NO = 8080;
    /**
     * Log4j��־��¼����.
     */
    private static final Logger LOGGER = Logger.getLogger(UploadServlet.class);
    /**
     * chunk.
     */
    private int chunk;
    /**
     * chunks.
     */
    private int chunks;
    /**
     * chunkSize.
     */
    private Long chunkSize;
    /**
     * name.
     */
    private String name;

    /**
     * parentFolderId.
     */
    private String parentFolderId;
    /**
     * size.
     */
    private Long size;
    
    /**
     * �ϴ��ļ��ĸ���
     */
    private int count;

    /**
     * Handles an HTTP POST request from Plupload.
     *
     * @param request
     *            The HTTP request
     * @param resp
     *            The HTTP response
     * @throws ServletException
     *          @throws ServletException if has error
     * @throws IOException
     *          @throws IOException if has error
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse resp) throws ServletException, IOException {
    	String responseString = RESP_SUCCESS;
        boolean finishFlag = false;
        boolean isMultipart = ServletFileUpload.isMultipartContent(request);
        if (isMultipart) {
            ServletFileUpload upload = new ServletFileUpload();
            try {
                String serviceAddr = PropertiesIOUtil.getValue("servletIp");
                String[] addrArray = serviceAddr.split(":");
                String ip = addrArray[0];
                int port = DEFAULT_PORT_NO;
                if (addrArray.length > 1) {
                    try {
                        port = Integer.parseInt(addrArray[1]);
                    } catch (Exception e) {
                        port = DEFAULT_PORT_NO;
                        LOGGER.error("PORT_NO invalid");
                    }
                }
                
                FileItemIterator iter = upload.getItemIterator(request);
                while (iter.hasNext()) {
                    FileItemStream item = iter.next();
                    InputStream input = item.openStream();

                    // Handle a form field.
                    if (item.isFormField()) {
                        String fileName = item.getFieldName();
                        String value = Streams.asString(input);

                        System.out.println("fileName-->" + fileName);
                        System.out.println("value-->" + value);
                        System.out.println("------------------\n");
                        
                        if ("name".equals(fileName)) {
                        	value = new String(value.getBytes("GBK"), "UTF-8");
                            this.name = value;
                        } else if ("chunks".equals(fileName)) {
                            this.chunks = Integer.parseInt(value);
                        } else if ("chunk".equals(fileName)) {
                            this.chunk = Integer.parseInt(value);
                        } else if ("chunkSize".equals(fileName)) {
                            this.chunkSize = Long.parseLong(value);
                        } else if ("parentFolderId".equals(fileName)) {
                            this.parentFolderId = value;
                        } else if ("size".equals(fileName)) {
                            this.size = Long.parseLong(value);
                        }
                    } else {
                        // ����REST���������
                        URIBuilder builder = new URIBuilder();
                        String restapi = "/gengzc/gzc/upload.do";
                        builder.setScheme("http").setHost(ip).setPort(port).setPath(restapi);
                        builder.setParameter("chunk", String.valueOf(this.chunk));
                        builder.setParameter("chunkSize", String.valueOf(this.chunkSize));
                        builder.setParameter("fileName", this.name);
                        builder.setParameter("folderId", this.parentFolderId);
                        builder.setParameter("size", String.valueOf(this.size));
                        builder.setParameter("count", String.valueOf(count));
                        count ++;
                        
                        System.out.println("�ϴ�ͳ�ƣ� " + count);
                        System.out.println("this.chunk:"+this.chunk);
                        System.out.println("this.chunks:"+this.chunks);
                        if (this.chunk == this.chunks - 1) {
                            finishFlag = true;
                            count = 0;
                        }
                        builder.setParameter("isFinished", String.valueOf(finishFlag));

                        LOGGER.info("�����URL:" + builder.build());
                        LOGGER.info("folder:" + this.parentFolderId + ", name:" + this.name + ",size:" + this.size);
                        LOGGER.info("chunk:" + this.chunk + ", chunkSize:" + this.chunkSize + ",chunks:" + this.chunks);
                        HttpPost post = new HttpPost();
                        post.setURI(builder.build());
                        post.addHeader(RestController.KEY_CONTENT_TYPE, "application/json; " + "charset=iso-8859-1");
                        post.addHeader(RestController.KEY_ACCEPT, RestController.ACCEPT); // �������Ӧ���ݸ�ʽ������

                        InputStreamEntity entity = new InputStreamEntity(input, -1);
                        entity.setChunked(true);
                        post.setEntity(entity);

                        HttpResponse response = RestController.client.execute(post);
                        HttpEntity resentity =  response.getEntity();
                        JSONObject result = new JSONObject();
                        result = JSONObject.fromObject(EntityUtils.toString(resentity));
                        LOGGER.info("result.toString():" + result.toString());
                    }
                }
            } catch (Exception e) {
                responseString = RESP_ERROR;
                e.printStackTrace();
            }
        } else {
            responseString = RESP_ERROR;
        }
        resp.setContentType(JSON);
        byte[] responseBytes = responseString.getBytes();
        resp.setContentLength(responseBytes.length);
        ServletOutputStream output = resp.getOutputStream();
        output.write(responseBytes);
        output.flush();
    }
}

