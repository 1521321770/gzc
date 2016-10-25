package com.gengzc.controller.geng;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.gengzc.bean.Software;
import com.gengzc.controller.common.Result;
import com.gengzc.controller.propertiesController.PropertiesIOUtil;
import com.gengzc.service.gzcService.SoftwareService;
import com.gengzc.util.Files;
import com.gengzc.util.OperationResult;
import com.gengzc.util.exception.UploadHandler;

@Controller
@RequestMapping("/gzc")
public class UploadController {
	
	private static Log log = LogFactory.getLog(UploadController.class);
	
	@Autowired
	private SoftwareService softwareService;
	
	@ResponseBody
	@RequestMapping(value = "/upload.do", method = RequestMethod.POST)
    public OperationResult upload(HttpServletRequest request,
    		@RequestParam(value = "chunk", required = true) int chunk,
    		@RequestParam(value = "chunkSize") long chunkSize,
    		@RequestParam(value = "folderId") String folderId,
    		@RequestParam(value = "fileName") String fileName,  		
    		@RequestParam(value = "size") long size,
    		@RequestParam(value = "count") String count,
    		@RequestParam(value = "isFinished") boolean isFinished,
    		@RequestParam(value = "description", defaultValue = "") String description){
		
        try {
        	log.debug("isFinished: " + isFinished);
        	String rootPath = PropertiesIOUtil.getValue("path");
        	fileName = new String(fileName.getBytes("ISO-8859-1"), "UTF-8");
        	
        	File filePath  = new File(Files.toPath(rootPath, fileName));
        	InputStream inputStream = request.getInputStream();
        	UploadHandler.uploadFile(inputStream, filePath, chunk, chunkSize);
        	if (isFinished) {
        		Software software = new Software(fileName, size, description);
        		software.setName(fileName);
        		software.setCategory(0);
        		software.setFolderId("");
        		software.setUploaderId("");
        		software.setUploaderName("");
        		Serializable id = softwareService.save(software);
        		System.out.println("--------->" + id);
        	}
        	
        	return Result.sendResult("200", true, "上传成功");
        } catch(IOException e) {
    		e.printStackTrace();
    		log.error(e.getMessage(), e);
    		return Result.sendResult(e.getMessage(), false, e.getMessage());
    	} catch(Exception e) {
    		e.printStackTrace();
    		log.error(e.getMessage(), e);
    		return Result.sendResult(e.getMessage(), false, e.getMessage());
    	}
    }
}
