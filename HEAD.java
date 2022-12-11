import java.io.*;
import java.util.HashMap;
import java.util.Stack;

public class HEAD implements Serializable {
    private static final long serialVersionUID = 3L;
    private String cmtId = "";

    public String getCmtId() {
        return cmtId;
    }

    public void setCmtId(String cmtId) {
        this.cmtId = cmtId;
    }

    public static String getLastCmt() throws IOException, ClassNotFoundException {  //用于打开HEAD文件并且取走cmtId
        String property = System.getProperty("user.dir"); //property便是当前所在文件夹的绝对路径
        File HEAD_dir = new File(property + "/.git/objects/HEAD");

        if(HEAD_dir.length() == 0) {   //如果HEAD还是个空文件，说明没有上次cmt
            return "";
        } else {  //说明有上次cmt
            //创建文件输入流，读取HEAD文件
            FileInputStream fis = new FileInputStream(HEAD_dir);
            ObjectInputStream ois = new ObjectInputStream(fis);
            HEAD head = (HEAD) ois.readObject();  //读取HEAD文件原有的内容，反序列化
            return head.cmtId;
        }
    }

    public static void writeHEAD(String hash_id) throws IOException, ClassNotFoundException {
        String property = System.getProperty("user.dir"); //property便是当前所在文件夹的绝对路径
        File HEAD_dir = new File(property + "/.git/objects/HEAD");

        if(HEAD_dir.length() == 0) { //如果HEAD还是个空文件
            HEAD head = new HEAD();
            //System.out.println("head文件目前为空");

            head.setCmtId(hash_id);   //HEAD指针设成新id

            //创建文件输出流，把head对象序列化到HEAD文件中
            FileOutputStream fos = new FileOutputStream(HEAD_dir);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(head);

            //释放资源
            fos.close();
            oos.close();
        } else {  //如果HEAD文件非空，则需要先打开（反序列化），再向head对象中加入内容
            //创建文件输入流，读取HEAD文件
            FileInputStream fis = new FileInputStream(HEAD_dir);
            ObjectInputStream ois = new ObjectInputStream(fis);
            HEAD head = (HEAD) ois.readObject();  //读取HEAD文件原有的内容，反序列化

            head.setCmtId(hash_id); //HEAD指针设成新id

            //创建文件输出流，把head对象序列化到HEAD文件中
            FileOutputStream fos = new FileOutputStream(HEAD_dir);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(head);
            //System.out.println("HEAD file length is " + HEAD_dir.length());

            fos.close();
            oos.close();
            ois.close();
        }
    }

}


