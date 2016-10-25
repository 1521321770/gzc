package util;

//随机生成一组uuid

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.TimeUnit;
public class Test extends RecursiveAction{
    static int ll=0;
    static int ai=5;
    FileWriter fwr=null;
     int[][] s=null;  
    static String[] array={"0","1","2","3","4","5","6","7","8","9","a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z"};
     public Test(int[][] s,FileWriter fwr){
         this.s=s;
         this.fwr=fwr;
     }
    protected void compute() {
        if(test()){
            ddd(s,fwr);
        }else{
            for(int i=0;i<s[0].length;i++){
                if(s[1][i]-s[0][i]>ai){
                    int[][] a=new int[2][s[0].length];
                    int[][] b=new int[2][s[0].length];
                    for(int p=0;p<a.length;p++){
                        for(int q=0;q<a[p].length;q++){
                            a[p][q]=s[p][q];
                            b[p][q]=s[p][q];
                        }
                    }
                    a[0][i]=s[0][i];
                    a[1][i]=(s[0][i]+s[1][i])/2;
                    b[0][i]=(s[0][i]+s[1][i])/2+1;
                    b[1][i]=s[1][i];
                    invokeAll(new Test(a,fwr),new Test(b,fwr));
                   break;
                }
            }
        }     
    }
    private boolean test(){
        for(int i=0;i<s[0].length;i++){
            if(s[1][i]-s[0][i]>ai){
                return false;
            }         
        }     
        return true;
    } 
    private void ddd(int[][] s,FileWriter fwr){
        HashMap<Integer,String> map=new HashMap<Integer,String>();
        StringBuilder sb=null;
        int n=0;
        int tm=0;
        String sss=null;
        while(map.entrySet().size()<270){
            n=0;
            tm=0;
            sb=new StringBuilder();
            for(int m=0;m<13;m++){
                if(m%2==1){
                    tm=(int)(Math.random()*(s[1][n]-s[0][n]))+s[0][n];
                    sb.append(array[tm]);
                }else{
                    tm=(int)(Math.random()*array.length);
                    sb.append(array[tm]);
                } 
            }
            sss=sb.toString();
            if(map.get(sss.hashCode())==null){
                map.put(sss.hashCode(), sss); 
                try {
                    fwr.write(sss+"\r\n");
                    fwr.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }     
        }     
    } 
    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws IOException, InterruptedException{
        FileWriter fw=new FileWriter(new File("F://111.txt"),true);
        int[][] k={{0,0,0,0,0,0},{array.length,array.length,array.length,array.length,array.length,array.length}};
        Test sort = new Test(k,fw);
        ForkJoinPool fjpool = new ForkJoinPool();
        fjpool.submit(sort);
        fjpool.shutdown();
        fjpool.awaitTermination(12000,TimeUnit.SECONDS);
        fw.close();
    }
}