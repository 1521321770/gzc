package util;

import java.io.File;

//输出文件夹下的所有文件
public class Test1 {
    public static void main(String[] args) {
        String path = "C:/Users/Administrator/Desktop/img/source";
        File f = new File(path);
        printFileName(f);
    }

    public static void printFileName(File f) {
        File[] files = f.listFiles();
//        System.out.println(Arrays.toString(files));
        for (File file : files) {  
            if(file.isDirectory()){  
                printFileName(file);  
            }else{  
                String fileName = file.getName();
                System.out.println("文件名称:" + fileName);  
            }  
        }  
    }  
}
