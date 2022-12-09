import java.io.*;
import java.util.HashMap;

class Tree implements Serializable {
    /**---------------数据域------------------*/
    private static final long serialVersionUID = 2L;

    private String type = "Tree";  //标明这个对象是Tree类型

    private String id;  //保存文件名，便于id被commit取用

    private HashMap<String, String> map;


    /**---------------方法域------------------*/
    public Tree() {   //构造方法
        map = new HashMap<>();
        //System.out.println(map.get(filename));
    }

    //Getters
    public String getType() {
        return type;
    }  //获取这个文件的类型

    public HashMap<String, String> getMap() {
        return map;
    }   //获取map，当数据域为私有域时有用，共有时直接tree.map就完事了

    public String getId() {
        return id;
    }

    public void writeTree() throws Exception{
        //读取index文件（即暂存区）里面的哈希表，直接copy进tree对象的哈希表
        String property = System.getProperty("user.dir"); //property便是当前所在文件夹的绝对路径
        File index_dir = new File(property + "/.git/objects/index");
        FileInputStream fis = new FileInputStream(index_dir);
        ObjectInputStream ois = new ObjectInputStream(fis);
        Index index =(Index)ois.readObject();  //读取index文件内容

        //把index的哈希表复制进tree的哈希表
        this.map = index.getMap();


        //打印出tree的内容
        System.out.println("tree对象内容为：" + this.map);  //把tree里面的哈希表打印出来

        //把tree对象序列化到object文件夹内，文件暂时命名为"tr_temp"
        File obj_dir = new File(property + "/.git/objects");
        FileOutputStream fos = new FileOutputStream(obj_dir+File.separator + "tr_temp");  ////创建Tree文件
        ObjectOutputStream oos = new ObjectOutputStream(fos); //用ObjectOutputStream直接把tree对象写入tree文件
        oos.writeObject(this);

        //读取tree文件，根据tree文件的内容生成哈希值，然后用哈希值重命名。后续可以整理为readFile函数，返回字节流
        File temp_tree = new File(property + "/.git/objects/tr_temp");
        FileInputStream is = new FileInputStream(temp_tree);
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

        //根据tree文件内容的字节流生成tree文件的哈希值，然后用哈希值重命名
        String hashName = mygit.getHashOfByteArray(buffer1);
        this.id = hashName;  //保存id信息
        File hash_tree = new File(property + "/.git/objects/" + hashName);


        if(!temp_tree.renameTo(hash_tree)){
            System.out.println("Tree file rename failed");
        }


        /*
        //把当前目录下所有文件和文件夹都生成映射写入哈希表
        for(File f:files){
            if(f.isFile()){   //只考虑了工作区只有文件、没有文件夹的情况
                String filename = f.getName(); //获取文件名
                FileInputStream is = new FileInputStream(f);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int len;

                //把文件内容读入字节流
                while((len = is.read(buffer)) != -1){
                    baos.write(buffer,0,len);//把读到的字节流写入buffer里
                }
                //根据文件内容的字节流生成哈希值
                String hash = HashUtils.getHashOfByteArray(buffer);

                //把新的映射加入tree这个对象
                tree.addMap(filename,hash);

                //释放资源
                is.close();
                baos.close();
            }

            if(f.isDirectory()){
                //Do something
            }//这是考虑到工作区有文件夹的情况时需要写的，简易版本不用谢

        }
        */
    }


}
