import java.io.*;
import java.net.Socket;



public class Client {
    public static void push() throws IOException {
        String property = System.getProperty("user.dir"); //property便是当前所在文件夹的绝对路径
        File obj_dir = new File(property + "/.git/objects");

        System.out.println("准备连接host: 127.0.0.1  port: 8888, 用于传输文件名");
        System.out.println("准备连接host: 127.0.0.1  port: 8887, 用于传输文件内容");

        File[] files = obj_dir.listFiles();//列出所有文件
        for(File f:files) {   //遍历objects目录
            if (f.isFile()) {
                Socket cltSocket = new Socket("127.0.0.1", 8888);    //指定要连接的服务器，需要同时指定服务器的IP & port
                String fName = f.getName();   //获取文件名
                OutputStream out = cltSocket.getOutputStream();
                out.write(fName.getBytes());
                out.flush();
                out.close();
                cltSocket.close();
                //System.out.println("文件名 " + fName + " 发送成功!");

                Socket newSocket = new Socket("127.0.0.1", 8887);
                //获取到输出流，给客户端发送数据
                OutputStream outf = newSocket.getOutputStream();
                try {
                    File file = new File(obj_dir +File.separator + fName);
                    byte[] b = new byte[1024];
                    int len = 0;
                    FileInputStream fis = new FileInputStream(file);  //用输入流来读取文件，读取后写入out
                    while ((len = fis.read(b, 0, b.length)) != -1) {//读到文件末尾后字节数就会变成-1
                        outf.write(b, 0, len);//把字节数组输出到和客户端之间的数据通路上
                    }
                    outf.flush();
                    System.out.println("文件 " + fName + " 发送完成");
                    outf.close();
                    newSocket.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }

    }


}
