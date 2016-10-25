package com.gengzc.util;

import java.io.*;
import java.util.Enumeration;

import org.apache.tools.zip.*;

import de.innosystec.unrar.Archive;
import de.innosystec.unrar.rarfile.FileHeader;

public class ZipAndRarUtils {
	private static final int BUFFEREDSIZE = 1024;

	 /**
	  * 解压缩
	  */
	 public static void deCompress(String sourceFile, String destDir)
	   throws Exception {
	  // 保证文件夹路径最后是"/"或者"\"
	  char lastChar = destDir.charAt(destDir.length() - 1);
	  if (lastChar != '/' && lastChar != '\\') {
	   destDir += File.separator;
	  }
	  // 根据类型，进行相应的解压缩
	  String type = sourceFile.substring(sourceFile.lastIndexOf(".") + 1);
	  if (type.equals("zip")) {
	   ZipAndRarUtils.unzip(sourceFile, destDir);
	  } else if (type.equals("rar")) {
	   ZipAndRarUtils.unrar(sourceFile, destDir);
	  } else {
	   throw new Exception("只支持zip和rar格式的压缩包！");
	  }
	 }

	 /**
	  * @SuppressWarnings({ "rawtypes", "resource" }) public static String
	  *                     unzip(File zipFile, String unzipDir, String
	  *                     zipFileName) throws IOException { ZipFile zf = new
	  *                     ZipFile(zipFile); Enumeration enu = zf.entries();
	  *                     String result = ""; while (enu.hasMoreElements()) {
	  *                     ZipEntry entry = (ZipEntry) enu.nextElement(); String
	  *                     name = entry.getName(); //
	  *                     如果解压entry是目录，直接生成目录即可，不用写入，如果是文件，要将文件写入 String
	  *                     pathParent = unzipDir + "/" + zipFileName; if (!(new
	  *                     File(pathParent).exists())) new
	  *                     File(pathParent).mkdirs(); String path = pathParent +
	  *                     "/" + name; result = result + path + "<br/>
	  *                     "; File file = new File(path); if
	  *                     (entry.isDirectory()) { file.mkdir(); } else { //
	  *                     建议使用如下方式创建流和读取字节，不然会有乱码 InputStream is =
	  *                     zf.getInputStream(entry); byte[] buf1 = new
	  *                     byte[1024]; int len; if (!file.exists()) {
	  *                     file.getParentFile().mkdirs(); file.createNewFile();
	  *                     } OutputStream out = new FileOutputStream(file);
	  *                     while ((len = is.read(buf1)) > 0) { String buf = new
	  *                     String(buf1, 0, len); out.write(buf1, 0, len); } } }
	  *                     result = "文件解压成功，解压文件：/n" + result; return result; }
	  **/

	 /**
	  * 解压zip包的内容到指定的目录下，可以处理其文件夹下包含子文件夹的情况
	  * 
	  * @param zipFilename
	  *            要解压的zip包文件
	  * @param outputDirectory
	  *            解压后存放的目录
	  * 
	  */
	 public static void unzip(String zipFilename, String outputDirectory)
	   throws Exception {
	  File outFile = new File(outputDirectory);
	  if (!outFile.exists()) {
	   outFile.mkdirs();
	  }

	  ZipFile zipFile = new ZipFile(zipFilename);
//	  ZipFile zipFile1 = new ZipFile(zipFilename, "GBK");
	  @SuppressWarnings("rawtypes")
	  Enumeration en = zipFile.getEntries();
	  ZipEntry zipEntry = null;
	  while (en.hasMoreElements()) {
	   zipEntry = (ZipEntry) en.nextElement();
	   if (zipEntry.isDirectory()) {
	    // mkdir directory
	    String dirName = zipEntry.getName();
	    // System.out.println("=dirName is:=" + dirName + "=end=");
	    dirName = dirName.substring(0, dirName.length() - 1);
	    File f = new File(outFile.getPath() + File.separator + dirName);
	    f.mkdirs();
	   } else {
	    // unzip file
	    String strFilePath = outFile.getPath() + File.separator
	      + zipEntry.getName();
	    File f = new File(strFilePath);

	    // 判断文件不存在的话，就创建该文件所在文件夹的目录
	    if (!f.exists()) {
	     String[] arrFolderName = zipEntry.getName().split("/");
	     String strRealFolder = "";
	     for (int i = 0; i < (arrFolderName.length - 1); i++) {
	      strRealFolder += arrFolderName[i] + File.separator;
	     }
	     strRealFolder = outFile.getPath() + File.separator
	       + strRealFolder;
	     File tempDir = new File(strRealFolder);
	     // 此处使用.mkdirs()方法，而不能用.mkdir()
	     tempDir.mkdirs();
	    }
	    // the codes remedified by can_do on 2010-07-02 =end=
	    f.createNewFile();
	    InputStream in = zipFile.getInputStream(zipEntry);
	    FileOutputStream out = new FileOutputStream(f);
	    try {
	     int c;
	     byte[] by = new byte[BUFFEREDSIZE];
	     while ((c = in.read(by)) != -1) {
	      out.write(by, 0, c);
	     }
	     // out.flush();
	    } catch (IOException e) {
	     throw e;
	    } finally {
	     out.close();
	     in.close();
	    }
	   }
	  }
	 }

	 /**
	  * 解压rar格式压缩包。
	  * 对应的是java-unrar-0.3.jar，但是java-unrar-0.3.jar又会用到commons-logging-1.1.1.jar
	  */
	 private static void unrar(String sourceRar, String destDir)
	   throws Exception {
	  Archive archive = null;
	  FileOutputStream fos = null;
	  try {
		  archive = new Archive(new File(sourceRar));
		  FileHeader fh = archive.nextFileHeader();
		  while (fh != null) {
			  if (!fh.isDirectory()) {
				  String compressFileName;
				  if(fh.isUnicode()){//解決中文乱码
					  compressFileName = fh.getFileNameW().trim();
				  }else{
					  compressFileName = fh.getFileNameString().trim();
				  }
				  
	    	  if (File.separator.equals("/")) {
	    		  compressFileName = compressFileName.replaceAll("\\\\", "/");
	    	  } else {
	    		  compressFileName = compressFileName.replaceAll("\\\\", "/");
	    	  }
	    	  
	    	  File file = new File(destDir + compressFileName);
	    	  
	    	  // 2创建文件夹
	    	  if (!file.getParentFile().exists() || !file.getParentFile().isDirectory()) {
	    		  file.getParentFile().mkdirs();
	    	  }
	    	  // 3解压缩文件
	    	  fos = new FileOutputStream(file);
	    	  archive.extractFile(fh, fos);
	    	  fos.close();
			  }
			  fh = archive.nextFileHeader();
	   		}
		  	archive.close();
	  	} catch (Exception e) {
	  		throw e;
	  	} finally {
	  		if (fos != null) {
	  			try {
	  				fos.close();
	  				fos = null;
	  			} catch (Exception e) {
	  				e.printStackTrace();
	  			}
	  		}
	  		if (archive != null) {
	  			try {
	  				archive.close();
	  				archive = null;
	  			} catch (Exception e) {
	  				e.printStackTrace();
	  			}
	  		}
	  	}
	 	}

	 public static void main(String[] args) {
		ZipAndRarUtils zip = new ZipAndRarUtils();
		try {
			zip.deCompress("E:\\啊啊啊.rar", "E:\\1111");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
