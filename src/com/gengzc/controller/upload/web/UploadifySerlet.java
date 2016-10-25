package com.gengzc.controller.upload.web;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;

public class UploadifySerlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//�ϴ��ļ��ı���·��
	protected String configPath = "attached/";

	protected String dirTemp = "attached/temp/";
	
	protected String dirName = "file";
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		 this.doPost(request, response);
	}

	 
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html; charset=UTF-8");
		PrintWriter out = response.getWriter();
		
		//�ļ�����Ŀ¼·��
		String savePath = this.getServletContext().getRealPath("/") + configPath;
		
		// ��ʱ�ļ�Ŀ¼ 
		String tempPath = this.getServletContext().getRealPath("/") + dirTemp;
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
		String ymd = sdf.format(new Date());
		savePath += "/" + ymd + "/";
		//�����ļ���
		File dirFile = new File(savePath);
		if (!dirFile.exists()) {
			dirFile.mkdirs();
		}
		
		tempPath += "/" + ymd + "/";
		//������ʱ�ļ���
		File dirTempFile = new File(tempPath);
		if (!dirTempFile.exists()) {
			dirTempFile.mkdirs();
		}
		
		DiskFileItemFactory  factory = new DiskFileItemFactory();
		factory.setSizeThreshold(20 * 1024 * 1024); //�趨ʹ���ڴ泬��5Mʱ����������ʱ�ļ����洢����ʱĿ¼�С�   
		factory.setRepository(new File(tempPath)); //�趨�洢��ʱ�ļ���Ŀ¼��   

		ServletFileUpload upload = new ServletFileUpload(factory);
		upload.setHeaderEncoding("UTF-8");
		
		
		 
		try {
			List items = upload.parseRequest(request);
			Iterator itr = items.iterator();
			
			String name = "";
			String qq = "";
			while (itr.hasNext()){
            	FileItemStream item = (FileItemStream) itr.next();
                InputStream input = item.openStream();
                String fileName = item.getFieldName();
                String value = Streams.asString(input);
                System.out.println("fileName-->" + fileName);
                System.out.println("value-->" + value);
                System.out.println("------------------\n");
            }
			while (itr.hasNext()) {
				FileItem item = (FileItem) itr.next();
				String fileName = item.getName();
				long fileSize = item.getSize();
				if (!item.isFormField()) {
					String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
					
					SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
					String newFileName = df.format(new Date()) + "_" + new Random().nextInt(1000) + "." + fileExt;
					try{
						File uploadedFile = new File(savePath, newFileName);
	                    OutputStream os = new FileOutputStream(uploadedFile);
	                    InputStream is = item.getInputStream();
	                    byte buf[] = new byte[1024];//�����޸� 1024 ����߶�ȡ�ٶ�
	                    int length = 0;  
	                    while( (length = is.read(buf)) > 0 ){  
	                        os.write(buf, 0, length);  
	                    }  
	                    //�ر���  
	                    os.flush();
	                    os.close();  
	                    is.close();  
	                    System.out.println("�ϴ��ɹ���·����"+savePath+"/"+newFileName);
	                    out.print("1");
					}catch(Exception e){
						e.printStackTrace();
					}
				}else {
					String filedName = item.getFieldName();
					if (filedName.equals("userName")) {
						name = item.getString();
					}else {
						qq = item.getString();
					}
					System.out.println("FieldName��"+filedName);
					System.out.println("String��"+item.getString());
					//System.out.println("String()��"+item.getString(item.getName()));
					System.out.println("==============="); 
				}			
			} 
			
		} catch (FileUploadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		out.flush();
		out.close();
	}

}
