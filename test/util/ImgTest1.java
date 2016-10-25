package util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import sun.awt.shell.ShellFolder;

//用java如何获取应用程序安装后的图标
public class ImgTest1 {

    static String sourcePath = "C:/Users/Administrator/Desktop/img/source";
    static String targetPath = "C:/Users/Administrator/Desktop/img/target/";
      public static void main(String[] args) {
          File file = new File(sourcePath);
          printFileName(file);
    }

    public static void printFileName(File f) {
        File[] files = f.listFiles();  
        for (File file : files) {  
            if(file.isDirectory()){  
                printFileName(file);  
            }else{
                String fileName = file.getName();  
                String path = targetPath + fileName.substring(0, fileName.indexOf("."));
                toImg(file, path);
            }  
        }  
    }

    public static void toImg(File sourcePath, String targetPath) {
        try {
//            提取图标的应用程序
//            File file = new File(filePath);
            OutputStream inStream = new FileOutputStream(new File(targetPath + ".png")); //图标保存地址
            BufferedImage www =   (BufferedImage)((ImageIcon) toIcon(sourcePath)).getImage();
            ImageIO.write(www, "png", inStream);  
            inStream.flush();
            inStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }  
    }

    public static Icon toIcon(File file) throws FileNotFoundException {
        ShellFolder shellFolder = ShellFolder.getShellFolder(file);  
        Icon icon = new ImageIcon(shellFolder.getIcon(true));  
        return icon;  
    }
}
