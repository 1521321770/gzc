package com.gengzc.util.exception;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.gengzc.util.StorageUtil;

public class UploadHandler {
	
	private static Log log = LogFactory.getLog(UploadHandler.class);
	
	private static final int BUF_SIZE = (int) StorageUtil.MB;
	
	public static void uploadFile(InputStream inputStream, File filePath,
			int chunk, long chunkSize) throws IOException {
		
		RandomAccessFile rf = null;
		try {
	    	rf = new RandomAccessFile(filePath, "rw");
	    	long skep = (long) (chunk)*chunkSize;
	    	skep = skep > 0 ? skep : 0;
	    	rf.seek(skep);
	    	byte[] buffer = new byte[BUF_SIZE];
	    	int len = 0;
	    	while ((len = inputStream.read(buffer)) > 0){
	    		rf.write(buffer, 0, len);
	    	}
	    } catch(IOException e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
			throw new IOException(e.getMessage(), e);
		} finally {
			if (null != inputStream) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
					log.error(e.getMessage(), e);
					throw new IOException(e.getMessage(), e);
				}
			}
			if( null != rf) {
				try {
					rf.close();
				} catch (IOException e) {
					e.printStackTrace();
					log.error(e.getMessage(), e);
					throw new IOException(e.getMessage(), e);
				}
			}
		}
	}
}
