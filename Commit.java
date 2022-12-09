import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

class Commit implements Serializable {
    /**---------------数据域------------------*/
    private static final long serialVersionUID = 3L;

    private String type = "Commit";  //标明这个对象是Commit类型

    private String lastCmt = "";  

    private String id;  //保存文件名，便于id被HEAD取用

    private String TreeId;  //保存此次commit生成的tree的id

    private String cmtTime;  //保存此次commit的时间

    private String Message;  //保存commit的message

    private HashMap<String, String> map;

    /**---------------方法域------------------*/
    public Commit() {   //构造方法
        map = new HashMap<>();
        //System.out.println(map.get(filename));
    }

    //Getters
    public String getId() {
        return id;
    }

    public String getLastCmt() {
        return lastCmt;
    }

    public String getCmtTime() {
        return cmtTime;
    }

    public String getMessage() {
        return Message;
    }

    //Setters
    public void setLastCmt(String lastId) throws IOException, ClassNotFoundException {
        this.lastCmt = lastId;
    }

    public void setTreeId(String treeId) {
        TreeId = treeId;
    }

    public String getTreeId() {
        return TreeId;
    }

    public void setCmtTime() {
        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
        Date date = new Date(System.currentTimeMillis());
        //System.out.println(formatter.format(date));
        this.cmtTime = formatter.format(date);
    }

    public void setMessage(String message) {
        Message = message;
    }



    public void writeCommit() throws Exception {
        String property = System.getProperty("user.dir"); //property便是当前所在文件夹的绝对路径

        //把cmt对象序列化到object文件夹内，文件暂时命名为"cmt_temp"
        File obj_dir = new File(property + "/.git/objects");
        FileOutputStream fos = new FileOutputStream(obj_dir+File.separator + "cmt_temp");  //创建commit文件
        ObjectOutputStream oos = new ObjectOutputStream(fos);   //用ObjectOutputStream直接把commit对象写入commit文件
        oos.writeObject(this);

        //读取commit文件，根据commit文件的内容生成哈希值，然后用哈希值重命名。后续可以整理为readFile函数，返回字节流
        File temp_cmt = new File(property + "/.git/objects/cmt_temp");  //读取cmt_temp文件
        FileInputStream is = new FileInputStream(temp_cmt);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer1 = new byte[1024];
        int len;
        while((len = is.read(buffer1)) != -1){  //把文件内容读入字节流buffer1里
            baos.write(buffer1,0,len);
        }

        //释放资源
        oos.close();
        fos.close();
        is.close();
        baos.close();

        //根据cmt文件内容的字节流生成cmt文件的哈希值，然后用哈希值重命名
        String hashName = mygit.getHashOfByteArray(buffer1);
        this.id = hashName;  //保存id信息
        File hash_cmt = new File(property + "/.git/objects/" + hashName);
        if(!temp_cmt.renameTo(hash_cmt)){
            System.out.println("Commit file rename failed");
        }
    }
}
