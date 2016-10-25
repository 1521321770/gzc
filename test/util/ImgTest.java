package util;

import java.awt.FlowLayout;
import java.io.File;
import java.io.FileNotFoundException;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.filechooser.FileSystemView;

import sun.awt.shell.ShellFolder;

//java可使用FileSystemView和ShellFolder类获取文件的小图标和大图标
public class ImgTest {
    public static void main(String[] args) {
//        String filePath = "D:/OS资源目录.png";
//      String filePath = "D:/OS资源目录.png";
//      String filePath = "D:/apache-tomcat-7.0.64.rar";
//      String filePath = "D:/暴风影音/BaofengPlatform.exe";
//      String filePath = "D:/暴风影音/BFDesktopIcon64.dll";
      String filePath = "D:/34.exe";
        File f = new File( filePath );
        JFrame frm = new JFrame();
        frm.setSize(300, 200);
        frm.setLocationRelativeTo( null );
        frm.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frm.setVisible( true );
        frm.setLayout(new FlowLayout( 10, 10, FlowLayout.LEADING));

        JLabel sl = new JLabel("小图标");
        frm.add(sl);
        JLabel bl = new JLabel("大图标");
        frm.add(bl);

        sl.setIcon(getSmallIcon(f));
        bl.setIcon(getBigIcon(f));
    }

    /**
     * 获取小图标
     * @param f
     * @return
     */
    private static Icon getSmallIcon(File f) {
        if (f != null && f.exists()) {
            FileSystemView fsv = FileSystemView.getFileSystemView();
            return(fsv.getSystemIcon(f));
        }
        return(null);
    }


    /**
     * 获取大图标
     * @param f
     * @return
     */
    private static Icon getBigIcon(File f) {
        if (f != null && f.exists()) {
            try {
                ShellFolder sf = ShellFolder.getShellFolder(f);
                return(new ImageIcon(sf.getIcon(true)));
            } catch ( FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return(null);
    }
}
