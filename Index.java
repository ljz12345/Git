import java.io.*;
import java.util.HashMap;
import java.util.Set;

public class Index implements Serializable {
    private static final long serialVersionUID = 1L;  //序列号，用于序列化与反序列化

    /**---------------数据域------------------*/
    private String type = "Index";  //标明这个对象是Index类型
    private HashMap<String, String> map;


    /**---------------方法域------------------*/
    //Getter
    public HashMap<String, String> getMap() {
        return map;
    }


    //构造方法：初始化Index对象时，要初始化一个哈希表
    public Index() {
        map = new HashMap<>();
        //System.out.println(map.get(filename));
    }

    public void addIdx(String filename, String hash) {  //添加一个映射进入哈希表
        map.put(filename,hash);
        //System.out.println(map.get(filename));
    }



    //把哈希表的键值全部取出，并且转化为字符串数组
    public String[] get_K_ary() {
        return map.keySet().toArray(new String[map.keySet().size()]);
    }

    public static void updateIdx(String filename, String hash) throws Exception{
        String property = System.getProperty("user.dir"); //property便是当前所在文件夹的绝对路径
        File index_dir = new File(property + "/.git/objects/index");

        FileInputStream in = new FileInputStream(filename);
        //System.out.print(index_dir.length());

        if(index_dir.length() == 0){  //如果Index还是个空文件，则初始化一个index对象
            Index index = new Index();  //初始化index对象
            System.out.println("index文件目前为空");
            //直接把新映射写入哈希表
            index.addIdx(filename, hash);  //向index对象加入第一个映射
            System.out.println("现在的index哈希表是：" + index.map);  //把index的哈希表打印出来

            //创建文件输出流，把index对象序列化到index文件中
            FileOutputStream fos = new FileOutputStream(index_dir);   //用文件输出流，打开index_dir这个文件
            ObjectOutputStream oos = new ObjectOutputStream(fos);    //创建一个对象输出流，准备向上一行打开的文件写对象
            oos.writeObject(index);

            //清理缓存区
            fos.close();
            oos.close();
        } else {   //如果Index文件非空，则需要先打开（反序列化），再向index对象中加入内容
            FileInputStream fis = new FileInputStream(index_dir);    //用文件输入流，打开index_dir这个文件
            ObjectInputStream ois = new ObjectInputStream(fis);    //新建对象输入流，准备读取index_dir文件中的对象
            Index index =(Index)ois.readObject();    //读取index文件中原有的内容，也就是把index反序列化出来
            System.out.println("原本的index哈希表是：" + index.map);    //把index对象的哈希表打印出来

            //把新的映射加入哈希表
            index.addIdx(filename, hash);

            //创建文件输出流，把index对象序列化到index文件中
            FileOutputStream fos = new FileOutputStream(index_dir);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            System.out.println("现在的index哈希表是：" + index.map);  //把index的哈希表打印出来
            System.out.println("暂存区的文件数量为：" + index.map.size() + "个\n");
            oos.writeObject(index);

            //清理缓存区
            fos.close();
            oos.close();
            fis.close();
            ois.close();
        }

        in.close();
    }

    public static void dltIdx(String filename) {
        String property = System.getProperty("user.dir"); //property便是当前所在文件夹的绝对路径
        File index_dir = new File(property + "/.git/objects/index");    //新建index_dir这个文件

        try{
            //先把index读出来
            FileInputStream fis = new FileInputStream(index_dir);    //用文件输入流，打开index_dir这个文件
            ObjectInputStream ois = new ObjectInputStream(fis);    //新建对象输入流，准备读取index_dir文件中的对象
            Index index =(Index)ois.readObject();    //读取index文件中原有的内容，也就是把index反序列化出来

            //然后判断该文件是否已不存在于工作区
            int mark1 = 0;
            File work_dir = new File(property);
            File[] all_files = work_dir.listFiles();
            for(File f:all_files){   //遍历工作区目录,检查暂存区中的文件是否在工作区已被删除
                if(f.isFile()){   //这个判断是不必要的，但是在进阶版有可能需要区分文件和文件夹
                    if(filename.equals(f.getName())) {   //如果暂存区里面的键值（即文件名）对应的工作区文件存在
                        mark1 += 1;
                    }
                }
            }
            if(mark1 == 0){    //如果文件在工作区已不存在
                System.out.println("文件 " + filename + " 在工作区中不存在");
                if (!index.map.containsKey(filename)) {    //判断hashmap中是否存在该文件的一对映射，若不存在，则打印异常信息
                    System.out.println("文件 " + filename + " 的信息在暂存区中不存在");
                } else {    //若存在
                    index.map.remove(filename);    //删除映射
                    System.out.println("文件 " + filename + " 的信息本来存在于暂存区中，现已从暂存区中移除");
                    System.out.println("删除元素后的index哈希表是：" + index.map);  //把index的哈希表打印出来
                    System.out.println("包含的文件数量为：" + index.map.size() + "个\n");
                }
            }

            //创建文件输出流，把修改过的index对象序列化到index文件中，相当于更新了index文件
            FileOutputStream fos = new FileOutputStream(index_dir);
            ObjectOutputStream oos = new ObjectOutputStream(fos);

            oos.writeObject(index);

            //清理缓存区
            fos.close();
            oos.close();
            fis.close();
            ois.close();
        } catch (FileNotFoundException e) {
            System.out.println(index_dir + "文件未找到");
        } catch (IOException e){
            System.out.println("IO错误");
        } catch (ClassNotFoundException e){
            System.out.println("未找到Index类");
        }

        /*
        String[] idx_ary = index.get_K_ary();  //取暂存区中的所有文件名
        int mark1 = 0;
        File work_dir = new File(property);
        File[] all_files = work_dir.listFiles();
        for(String Key:idx_ary){
            for(File f:all_files){   //遍历工作区目录,检查暂存区中的文件是否在工作区已被删除
                if(f.isFile()){   //这个判断是不必要的，但是在进阶版有可能需要区分文件和文件夹
                    if(Key.equals(f.getName())) {   //如果暂存区里面的键值（即文件名）对应的工作区文件存在
                        mark1 += 1;
                    }
                }
            }
            if(mark1 == 0){    //如果发现暂存区文件在工作区已不存在
                System.out.println("文件“" + Key + "”在工作区中已被删除");
                this.map.remove(filename);
                System.out.println("已从暂存区中移除文件”" + Key + "”的信息");
            }
        }
         */
    }

    public static void rm_dltIdx(String filename) {    //专门对应于rm --cached的情况
        String property = System.getProperty("user.dir"); //property便是当前所在文件夹的绝对路径
        File index_dir = new File(property + "/.git/objects/index");    //新建index_dir这个文件

        try {
            //先把index读出来
            FileInputStream fis = new FileInputStream(index_dir);    //用文件输入流，打开index_dir这个文件
            ObjectInputStream ois = new ObjectInputStream(fis);    //新建对象输入流，准备读取index_dir文件中的对象
            Index index = (Index) ois.readObject();    //读取index文件中原有的内容，也就是把index反序列化出来


            if (!index.map.containsKey(filename)) {    //判断hashmap中是否存在该文件的一对映射，若不存在，则打印异常信息
                System.out.println("文件 " + filename + " 的信息在暂存区中不存在");
            } else {    //若存在
                index.map.remove(filename);    //删除映射
                System.out.println("文件 " + filename + " 的信息本来存在于暂存区中，现已从暂存区中移除");
                System.out.println("删除元素后的index哈希表是：" + index.map);  //把index的哈希表打印出来
                System.out.println("包含的文件数量为：" + index.map.size() + "个\n");
            }

            //创建文件输出流，把修改过的index对象序列化到index文件中，相当于更新了index文件
            FileOutputStream fos = new FileOutputStream(index_dir);
            ObjectOutputStream oos = new ObjectOutputStream(fos);

            oos.writeObject(index);

            //清理缓存区
            fos.close();
            oos.close();
            fis.close();
            ois.close();
        } catch (FileNotFoundException e) {
            System.out.println(index_dir + "文件未找到");
        } catch (IOException e) {
            System.out.println("IO错误");
        } catch (ClassNotFoundException e) {
            System.out.println("未找到Index类");
        }
    }

    public static void dltIdx() throws IOException, ClassNotFoundException {
        System.out.println("----检查暂存区中的所有文件----");
        String property = System.getProperty("user.dir"); //property便是当前所在文件夹的绝对路径
        File index_dir = new File(property + "/.git/objects/index");    //新建index_dir这个文件

        //先把index读出来
        FileInputStream fis = new FileInputStream(index_dir);    //用文件输入流，打开index_dir这个文件
        ObjectInputStream ois = new ObjectInputStream(fis);    //新建对象输入流，准备读取index_dir文件中的对象
        Index index =(Index)ois.readObject();    //读取index文件中原有的内容，也就是把index反序列化出来

        String[] idx_ary = index.get_K_ary();  //取暂存区中的所有文件名（取哈希集合，然后转化成字符串数组）
        int mark1 = 0;
        File work_dir = new File(property);
        File[] all_files = work_dir.listFiles();
        for(String Key:idx_ary){
            for(File f:all_files){   //遍历工作区目录,检查暂存区中的文件是否在工作区已被删除
                if(f.isFile()){   //这个判断是不必要的，在进阶版中区分文件和文件夹的时候才有用
                    if(Key.equals(f.getName())) {   //如果暂存区里面的键值（即文件名）对应的工作区文件存在
                        mark1 += 1;    //标志：发现了重名的文件
                    }
                }
            }
            if(mark1 == 0){    //如果文件在工作区已不存在
                System.out.println("文件 " + Key + " 在工作区中不存在");
                if (!index.map.containsKey(Key)) {    //判断hashmap中是否存在该文件的一对映射，若不存在，则打印异常信息
                    System.out.println("文件 " + Key + " 的信息在暂存区中不存在");
                } else {    //若存在
                    index.map.remove(Key);    //删除映射
                    System.out.println("文件 " + Key + " 的信息本来存在于暂存区中，现已从暂存区中移除");
                }
            }
        }

        System.out.println("现在的index哈希表是：" + index.map);  //把index的哈希表打印出来
        System.out.println("包含的文件数量为：" + index.map.size() + "个\n");
    }
}
