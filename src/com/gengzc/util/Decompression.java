package com.gengzc.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.apache.tools.zip.ZipOutputStream;

import de.innosystec.unrar.Archive;

public class Decompression {
	private   static   final   int  BUFFEREDSIZE =  1024 ;  
    
	/**
     * 解压zip格式的压缩文件到当前文件夹
     * @param zipFileName
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public synchronized void unzipFile(String zipFileName) throws Exception {
        try {
            File f = new File(zipFileName);
            ZipFile zipFile = new ZipFile(zipFileName);
            if((!f.exists()) && (f.length() <= 0)) {
                throw new Exception("要解压的文件不存在!");
            }
            String strPath, gbkPath, strtemp;
            File tempFile = new File(f.getParent());
            strPath = tempFile.getAbsolutePath();
            Enumeration e = zipFile.getEntries();
            while(e.hasMoreElements()){
                ZipEntry zipEnt = (ZipEntry) e.nextElement();
                gbkPath=zipEnt.getName();
                if(zipEnt.isDirectory()){
                    strtemp = strPath + "/" + gbkPath;
                    File dir = new File(strtemp);
                    dir.mkdirs();
                    continue;
                } else {
                    //读写文件
                    InputStream is = zipFile.getInputStream(zipEnt);
                    BufferedInputStream bis = new BufferedInputStream(is);
                    gbkPath=zipEnt.getName();
                    strtemp = strPath + "/" + gbkPath;
                 
                    //建目录
                    String strsubdir = gbkPath;
                    for(int i = 0; i < strsubdir.length(); i++) {
                        if(strsubdir.substring(i, i + 1).equalsIgnoreCase("/")) {
                            String temp = strPath + "/" + strsubdir.substring(0, i);
                            File subdir = new File(temp);
                            if(!subdir.exists())
                            subdir.mkdir();
                        }
                    }
                    FileOutputStream fos = new FileOutputStream(strtemp);
                    BufferedOutputStream bos = new BufferedOutputStream(fos);
                    int c;
                    while((c = bis.read()) != -1) {
                        bos.write((byte) c);
                    }
                    bos.close();
                    fos.close();
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * 解 压zip格式的压缩文件到指定位置  
     * @param zipFileName 压 缩文件  
     * @param extPlace 解 压目录  
     * @throws Exception  
     */   
    @SuppressWarnings ( "unchecked" )  
    public   synchronized   void  unzip(String zipFileName, String extPlace)  throws  Exception {  
        try  {  
            (new  File(extPlace)).mkdirs();  
            File f = new  File(zipFileName);  
            ZipFile zipFile = new  ZipFile(zipFileName);  
            if ((!f.exists()) && (f.length() <=  0 )) {  
                throw   new  Exception( "要解压的文件不存在!" );  
            }  
            String strPath, gbkPath, strtemp;  
            File tempFile = new  File(extPlace);  
            strPath = tempFile.getAbsolutePath();  
            Enumeration e = zipFile.getEntries();  
            while (e.hasMoreElements()){  
                ZipEntry zipEnt = (ZipEntry) e.nextElement();  
                gbkPath=zipEnt.getName();  
                if (zipEnt.isDirectory()){  
                    strtemp = strPath + File.separator + gbkPath;  
                    File dir = new  File(strtemp);  
                    dir.mkdirs();  
                    continue ;  
                } else  {  
                    //读写文件   
                    InputStream is = zipFile.getInputStream(zipEnt);  
                    BufferedInputStream bis = new  BufferedInputStream(is);  
                    gbkPath=zipEnt.getName();  
                    strtemp = strPath + File.separator + gbkPath;  
                  
                    //建目录   
                    String strsubdir = gbkPath;  
                    for ( int  i =  0 ; i < strsubdir.length(); i++) {  
                        if (strsubdir.substring(i, i +  1 ).equalsIgnoreCase( "/" )) {  
                            String temp = strPath + File.separator + strsubdir.substring(0 , i);  
                            File subdir = new  File(temp);  
                            if (!subdir.exists())  
                            subdir.mkdir();  
                        }  
                    }  
                    FileOutputStream fos = new  FileOutputStream(strtemp);  
                    BufferedOutputStream bos = new  BufferedOutputStream(fos);  
                    int  c;  
                    while ((c = bis.read()) != - 1 ) {  
                        bos.write((byte ) c);  
                    }  
                    bos.close();  
                    fos.close();  
                }  
            }  
        } catch (Exception e) {  
            e.printStackTrace();  
            throw  e;  
        }  
    }  
      
    /**  
     * 解 压zip格式的压缩文件到指定位置  
     * @param zipFileName 压 缩文件  
     * @param extPlace 解 压目录  
     * @throws Exception  
     */   
    @SuppressWarnings ( "unchecked" )  
    public   synchronized   void  unzip(String zipFileName, String extPlace, boolean  whether)  throws  Exception {  
        try  {  
            (new  File(extPlace)).mkdirs();  
            File f = new  File(zipFileName);  
            ZipFile zipFile = new  ZipFile(zipFileName);  
            if ((!f.exists()) && (f.length() <=  0 )) {  
                throw   new  Exception( "要解压的文件不存在!" );  
            }  
            String strPath, gbkPath, strtemp;  
            File tempFile = new  File(extPlace);  
            strPath = tempFile.getAbsolutePath();  
            Enumeration e = zipFile.getEntries();  
            while (e.hasMoreElements()){  
                ZipEntry zipEnt = (ZipEntry) e.nextElement();  
                gbkPath=zipEnt.getName();  
                if (zipEnt.isDirectory()){  
                    strtemp = strPath + File.separator + gbkPath;  
                    File dir = new  File(strtemp);  
                    dir.mkdirs();  
                    continue ;  
                } else  {  
                    //读写文件   
                    InputStream is = zipFile.getInputStream(zipEnt);  
                    BufferedInputStream bis = new  BufferedInputStream(is);  
                    gbkPath=zipEnt.getName();  
                    strtemp = strPath + File.separator + gbkPath;  
                  
                    //建目录   
                    String strsubdir = gbkPath;  
                    for ( int  i =  0 ; i < strsubdir.length(); i++) {  
                        if (strsubdir.substring(i, i +  1 ).equalsIgnoreCase( "/" )) {  
                            String temp = strPath + File.separator + strsubdir.substring(0 , i);  
                            File subdir = new  File(temp);  
                            if (!subdir.exists())  
                            subdir.mkdir();  
                        }  
                    }  
                    FileOutputStream fos = new  FileOutputStream(strtemp);  
                    BufferedOutputStream bos = new  BufferedOutputStream(fos);  
                    int  c;  
                    while ((c = bis.read()) != - 1 ) {  
                        bos.write((byte ) c);  
                    }  
                    bos.close();  
                    fos.close();  
                }  
            }  
        } catch (Exception e) {  
            e.printStackTrace();  
            throw  e;  
        }  
    }  
    /**  
     * 压 缩zip格式的压缩文件  
     * @param inputFilename 压 缩的文件或文件夹及详细路径  
     * @param zipFilename 输 出文件名称及详细路径  
     * @throws IOException  
     */   
    public   synchronized   void  zip(String inputFilename, String zipFilename)  throws  IOException {  
        zip(new  File(inputFilename), zipFilename);  
    }  
      
    /**  
     * 压 缩zip格式的压缩文件  
     * @param inputFile 需 压缩文件  
     * @param zipFilename 输 出文件及详细路径  
     * @throws IOException  
     */   
    public   synchronized   void  zip(File inputFile, String zipFilename)  throws  IOException {  
        ZipOutputStream out = new  ZipOutputStream( new  FileOutputStream(zipFilename));  
        try  {  
            zip(inputFile, out, "" );  
        } catch  (IOException e) {  
            throw  e;  
        } finally  {  
            out.close();  
        }  
    }  
      
    /**  
     * 压 缩zip格式的压缩文件  
     * @param inputFile 需 压缩文件  
     * @param out 输 出压缩文件  
     * @param base 结 束标识  
     * @throws IOException  
     */   
    @SuppressWarnings ( "unused" )  
    private   synchronized   void  zip(File inputFile, ZipOutputStream out, String base)  throws  IOException {  
        if  (inputFile.isDirectory()) {  
            File[] inputFiles = inputFile.listFiles();  
            out.putNextEntry(new  ZipEntry(base +  "/" ));  
            base = base.length() == 0  ?  ""  : base +  "/" ;  
            for  ( int  i =  0 ; i < inputFiles.length; i++) {  
                zip(inputFiles[i], out, base + inputFiles[i].getName());  
            }  
        } else  {  
            if  (base.length() >  0 ) {  
                out.putNextEntry(new  ZipEntry(base));  
            } else  {  
                out.putNextEntry(new  ZipEntry(inputFile.getName()));  
            }  
            FileInputStream in = new  FileInputStream(inputFile);  
            try  {  
                int  c;  
                byte [] by =  new   byte [BUFFEREDSIZE];  
                while  ((c = in.read(by)) != - 1 ) {  
                    out.write(by, 0 , c);  
                }  
            } catch  (IOException e) {  
                throw  e;  
            } finally  {  
                in.close();  
            }  
        }  
    }  
    /**  
     * 解 压rar格式的压缩文件到指定目录下  
     * @param rarFileName 压 缩文件  
     * @param extPlace 解 压目录  
     * @throws Exception  
     */   
    public   synchronized   void  unrar(String rarFileName, String extPlace)  throws  Exception{  
//        try  {  
//            (new  File(extPlace)).mkdirs();  
//            // 构建测解压缩类   
//            Archive archive = new Archive();  
//            archive.setEnabledLog(false );  //不输出 日志   
//            // 设置rar文件   
//            archive.setFile(rarFileName);  
//            archive.setExtractPath(extPlace);  
//            archive.extractAllFile();  
//        } catch  (Exception e) {  
//        	e.printStackTrace();
//        }  
    }
    
    public static void main(String[] args) {
        Decompression decompression=new Decompression();
        try {
            decompression.unzipFile("c:/test.zip");
            decompression.unzip("c:/test.zip","c:/test22");
            decompression.zip("c:/test22", "c:/test222.zip");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
