import java.io.*;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) throws IOException {
        String property = System.getProperty("user.dir"); //property便是当前所在文件夹的绝对路径
        String serv_dir = property + "/.git/server";

        //启动服务器，指定端口号为8888，等待客户的连接
        ServerSocket ss = new ServerSocket(8888);
        ServerSocket ss1 = new ServerSocket(8887);

        System.out.println("端口号1: 8888");
        System.out.println("端口号2: 8887");
        System.out.println("本机IP地址: " + Inet4Address.getLocalHost().getHostAddress());
        System.out.println("远程仓库路径: " + serv_dir);


        while(true){    //不断重复接收文件名和文件
            //用socket接收文件名
            Socket socket = ss.accept();
            //创建输入流，接收客户端发来的数据
            InputStream in = socket.getInputStream();
            byte[] b = new byte[1024];
            int len = in.read(b);   //返回读取到的字节数
            String fName = new String(b, 0, len);
            System.out.println("文件 " + fName + " 接收成功!");
            in.close();

            //用socket1接收文件内容
            Socket socket1 = ss1.accept();
            InputStream in1 = socket1.getInputStream();
            File file = new File(serv_dir + File.separator + fName);
            int len1 = 0;
            FileOutputStream fos = new FileOutputStream(file);    //用输出流把下载的文件写入硬盘
            while ((len1 = in1.read(b, 0, b.length)) != -1) {    //读到文件末尾后字节数就会变成-1
                fos.write(b, 0, len1);  //输出流把文件内容写进本地
                fos.flush();  //清缓存
            }
            //释放资源
            fos.close();
        }
    }
}
