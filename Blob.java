import java.io.*;

class Blob implements Serializable {
    /**---------------数据域------------------*/
    private static final long serialVersionUID = 134252411453L;

    private String type = "Blob";  //标明这个对象是Blob类型

    private byte[] content;
    private long length;
    private String hash;


    /**---------------方法域------------------*/
    public Blob(byte[] content, long length, String hash) {
        this.content = content;
        this.length = length;
        this.hash = hash;
    }

    public byte[] getContent() {
        return content;
    }  //这些方法都是因为数据域是私有域所以才有用的。


    public void setContent(byte[] content) {
        this.content = content;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    @Override
    public String toString() {
        return "Blob{" +
                "content=" + new String(content) +
                ", length=" + length +
                ", hash='" + hash + '\'' +
                '}';
    }

    public static void writeBlob(byte[] content, String hash) throws Exception{

        String property = System.getProperty("user.dir"); //property便是当前所在文件夹的绝对路径
        File dir = new File(property + "/.git/objects");

        Blob blob = new Blob(content, content.length, hash);
        //FileOutputStream FOS = new FileOutputStream(SHA1 + ".txt");       //可以抛出FileNotFoundException异常
        FileOutputStream fos = new FileOutputStream(dir+File.separator+hash);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(blob);
        oos.close();
    }

    public static void readBlob(String filepath) throws Exception{
        FileInputStream fis = new FileInputStream(filepath);
        ObjectInputStream ois = new ObjectInputStream(fis);
        Blob blob =(Blob) ois.readObject();
        fis.close();
        ois.close();
        System.out.println(blob);
    }
}

//