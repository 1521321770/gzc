package com.gengzc.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import de.innosystec.unrar.Archive;
import de.innosystec.unrar.exception.RarException;
import de.innosystec.unrar.rarfile.FileHeader;

public class RARUtil {

	public static void main(String[] args) {
		try {
			unrar("F:\\啊啊啊.rar","F:\\333");
		} catch (RarException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void unrar(String srcPath,String unrarPath) throws RarException, IOException, Exception{
		File srcFile = new File(srcPath);
		if(null == unrarPath || "".equals(unrarPath)){
			unrarPath = srcFile.getParentFile().getPath();
		}
		// 保证文件夹路径最后是"/"或者"\"
		char lastChar = unrarPath.charAt(unrarPath.length() - 1);
		if (lastChar != '/' && lastChar != '\\') {
			unrarPath += File.separator;
		}
		unrar(srcFile, unrarPath);
	}

	private static void unrar(File srcFile,String unrarPath){
        FileOutputStream fileOut = null;
        Archive rarfile = null;
        try{
	        rarfile = new Archive(srcFile);
	        FileHeader fh = rarfile.nextFileHeader();
	        while(fh!=null){
	        	String entrypath = "";
	        	if(fh.isUnicode()){//解決中文乱码
	        		entrypath = fh.getFileNameW().trim();
	        	}else{
	        		entrypath = fh.getFileNameString().trim();
	        	}
	        	entrypath = entrypath.replaceAll("\\\\", "/");
	        	
	        	File file = new File(unrarPath + entrypath);

	        	if(fh.isDirectory()){
	        		file.mkdirs();
	        	}else{
	            	File parent = file.getParentFile();
	                if(parent!=null && !parent.exists()){
	                    parent.mkdirs();
	                }
	                fileOut = new FileOutputStream(file);
	                rarfile.extractFile(fh, fileOut);
	                fileOut.close();
	        	}
	        	fh = rarfile.nextFileHeader();
	        }
	        rarfile.close();

        } catch (Exception e) {
        	System.out.println("rar文件解压错误！");
			e.printStackTrace();
		} finally {
			if (fileOut != null) {
				try {
					fileOut.close();
					fileOut = null;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (rarfile != null) {
				try {
					rarfile.close();
					rarfile = null;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}


}
