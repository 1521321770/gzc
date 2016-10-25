package com.gengzc.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;


public class ZipCompress {
	
	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		InputStream is = null;
		try {
			ZipFile zip = new ZipFile("F:/ѹ���ļ�1.zip", "GBK");
			Enumeration<? extends ZipEntry> entrys = zip.getEntries();
			ZipEntry ze = null;
			while(entrys.hasMoreElements()){
				ze = entrys.nextElement();
				File f = new File("F:/" + File.separator + ze.getName());

				if(ze.isDirectory()){
					f.mkdirs();
					continue;
				}

				File f1 = new File(f.getParent());
				if(!f1.exists()){
					f1.mkdirs();
				}

				FileOutputStream out = new FileOutputStream(f);
				is = zip.getInputStream(ze);

				int length = 0;
				byte[] data = new byte[(int) StorageUtil.MB];
				while((length = is.read(data)) > 0){
					out.write(data, 0, length);
				}
				out.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
