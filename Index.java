import java.io.*;
import java.util.*;

public class Index implements Serializable {
    private static final long serialVersionUID = 1L;  //序列号，用于序列化与反序列化

    private String type = "Index";  //标明这个对象是Index类型
    private HashMap<String, String> map;

    private HashSet<String> treeSet;



    //Getter
    public HashMap<String, String> getMap() {
        return map;
    }


    //构造方法：初始化Index对象时，要初始化一个哈希表
    public Index() {
        map = new HashMap<>();
        treeSet = new HashSet<>();

        //System.out.println(map.get(filename));
    }

    private void addIdx(String filename, String hash) {  //添加一个映射进入哈希表
        map.put(filename,hash);
        //System.out.println(map.get(filename));
    }

    private void addTreeSet(String dir_name) {  //添加一个映射进入哈希集合
        treeSet.add(dir_name);
        //System.out.println(map.get(filename));
    }

    //把哈希表的键值全部取出，并且转化为字符串数组
    public String[] get_K_ary() {
        return map.keySet().toArray(new String[map.keySet().size()]);
    }

    private void printMap(){
        System.out.println("现在的index哈希表是：");
        System.out.printf("%-30s\t%-60s\n", "FILE NAME", "HASH");
        Set<String> sets = map.keySet();
        String[] arr =sets.toArray(new String[map.keySet().size()]);
        Arrays.sort(arr);
        for(String fName : arr) {
            String hash = map.get(fName);
            System.out.printf("%-30s\t%-60s\n", fName, hash);
        }
        System.out.println("暂存区的文件数量为：" + map.size() + "个\n");
    }

    public static void OutPrintMap(){
        String property = System.getProperty("user.dir"); //property便是当前所在文件夹的绝对路径
        File index_dir = new File(property + "/.git/objects/index");
        try{
            //打开文件
            FileInputStream fis = new FileInputStream(index_dir);    //用文件输入流，打开index_dir这个文件
            ObjectInputStream ois = new ObjectInputStream(fis);    //新建对象输入流，准备读取index_dir文件中的对象
            Index index =(Index)ois.readObject();    //读取index文件中原有的内容，也就是把index反序列化出来

            //遍历打印哈希集合中的元素
            System.out.println("现在的index哈希集合是：");
            System.out.printf("%-60s\n", "DIRECTORY NAME");
            String[] treeArr = index.treeSet.toArray(new String[index.treeSet.size()]);
            Arrays.sort(treeArr);
            for(String dirName : treeArr) {
                String hash = index.map.get(dirName);
                System.out.printf("%-60s\n", dirName);
            }
            System.out.println("暂存区的文件夹数量为：" + index.treeSet.size() + "个\n");

            //遍历打印哈希表中的元素
            System.out.println("现在的index哈希表是：");
            System.out.printf("%-30s\t%-60s\n", "FILE NAME", "HASH");
            Set<String> sets = index.map.keySet();
            String[] arr =sets.toArray(new String[index.map.keySet().size()]);
            Arrays.sort(arr);
            for(String fName : arr) {
                String hash = index.map.get(fName);
                System.out.printf("%-30s\t%-60s\n", fName, hash);
            }
            System.out.println("暂存区的文件数量为：" + index.map.size() + "个\n");

            fis.close();
            ois.close();
        } catch(FileNotFoundException e) {
            System.out.println("index文件不存在");
        } catch(ClassNotFoundException e) {
            System.out.println("index类不存在");
        } catch(IOException e) {
            System.out.println("IO异常");
        }

    }



    public static void updatefIdx(String filename, String hash) throws Exception{
        String property = System.getProperty("user.dir"); //property便是当前所在文件夹的绝对路径
        File index_dir = new File(property + "/.git/objects/index");

        //System.out.print(index_dir.length());

        if(index_dir.length() == 0){  //如果Index还是个空文件，则初始化一个index对象
            Index index = new Index();  //初始化index对象
            System.out.println("index文件目前为空");
            //直接把新映射写入哈希表
            index.addIdx(filename, hash);  //向index对象加入第一个映射
            //System.out.println("现在的index哈希表是：");
            //index.printMap();    //把index的哈希表打印出来

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
            //System.out.println("原本的index哈希表是：" + index.map);    //把index对象的哈希表打印出来

            //把新的映射加入哈希表
            index.addIdx(filename, hash);

            //创建文件输出流，把index对象序列化到index文件中
            FileOutputStream fos = new FileOutputStream(index_dir);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            //System.out.println("现在的index哈希表是：" + index.map);  //把index的哈希表打印出来
            //System.out.println("现在的index哈希表是：");
            //index.printMap();    //把index的哈希表打印出来
            //System.out.println("暂存区的文件数量为：" + index.map.size() + "个\n");
            oos.writeObject(index);

            //清理缓存区
            fos.close();
            oos.close();
            fis.close();
            ois.close();
        }

    }

    public static void updateDirIdx(String dir_name) throws Exception{
        String property = System.getProperty("user.dir"); //property便是当前所在文件夹的绝对路径
        File index_dir = new File(property + "/.git/objects/index");

        //System.out.print(index_dir.length());

        if(index_dir.length() == 0){  //如果Index还是个空文件，则初始化一个index对象
            Index index = new Index();  //初始化index对象
            System.out.println("index文件目前为空");
            //直接把新映射写入哈希表
            index.addTreeSet(dir_name);  //向index对象加入第一个映射
            //System.out.println("现在的index哈希表是：");
            //index.printMap();    //把index的哈希表打印出来

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
            //System.out.println("原本的index哈希表是：" + index.map);    //把index对象的哈希表打印出来

            //把新的映射加入哈希表
            if(index.treeSet == null){
                index.treeSet = new HashSet<>();
            }
            index.addTreeSet(dir_name);

            //创建文件输出流，把index对象序列化到index文件中
            FileOutputStream fos = new FileOutputStream(index_dir);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            //System.out.println("现在的index哈希表是：" + index.map);  //把index的哈希表打印出来
            //System.out.println("现在的index哈希表是：");
            //index.printMap();    //把index的哈希表打印出来
            //System.out.println("暂存区的文件数量为：" + index.map.size() + "个\n");
            oos.writeObject(index);

            //清理缓存区
            fos.close();
            oos.close();
            fis.close();
            ois.close();
        }

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
            int mark = 0;
            File work_dir = new File(property);
            File[] all_files = work_dir.listFiles();
            for(File f:all_files){   //遍历工作区目录,检查暂存区中的文件是否在工作区已被删除
                String name = f.getName();
                if(f.isFile()){   //这个判断是不必要的，但是在进阶版有可能需要区分文件和文件夹
                    if(filename.equals(f.getName())) {   //如果暂存区里面的键值（即文件名）对应的工作区文件存在
                        mark += 1;
                    }
                } else if (f.isDirectory() && !name.equals(".git")) {    //如果发现子文件夹
                    mark += searchFile(name,filename);    //递归搜索目标文件是否存在于工作区
                }
            }
            if(mark == 0){    //如果文件在工作区已不存在
                System.out.println("文件 " + filename + " 在工作区中不存在");
                if (!index.map.containsKey(filename)) {    //判断hashmap中是否存在该文件的一对映射，若不存在，则打印异常信息
                    System.out.println("文件 " + filename + " 的信息在暂存区中不存在");
                } else {    //若存在
                    index.map.remove(filename);    //删除映射
                    System.out.println("文件 " + filename + " 的信息本来存在于暂存区中，现已从暂存区中移除");
                    System.out.println("删除元素后的index哈希表是：");
                    index.printMap();    //把index的哈希表打印出来

                    //System.out.println("包含的文件数量为：" + index.map.size() + "个\n");


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
                System.out.println("删除元素后的index哈希表是：");  //把index的哈希表打印出来
                index.printMap();    //把index的哈希表打印出来
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


        File work_dir = new File(property);
        File[] all_files = work_dir.listFiles();
        for(String Key:idx_ary){
            int mark = 0;
            for(File f:all_files){   //遍历工作区目录,检查暂存区中的文件是否在工作区已被删除
                String name = f.getName();
                if(f.isFile()){   //这个判断是不必要的，在进阶版中区分文件和文件夹的时候才有用
                    if(Key.equals(name)) {   //如果暂存区里面的键值（即文件名）对应的工作区文件存在
                        mark += 1;    //标志：发现了重名的文件
                        //System.out.println("在工作区找到了" + name);
                    }
                } else if (f.isDirectory() && !name.equals(".git")) {    //如果发现子文件夹
                    mark += searchFile(name,Key);    //递归搜索目标文件是否存在于工作区
                }
            }

            //System.out.println(Key + "的mark = " + mark);

            if(mark == 0){    //如果文件在工作区已不存在
                System.out.println("文件 " + Key + " 在工作区中不存在");
                if (!index.map.containsKey(Key)) {    //判断hashmap中是否存在该文件的一对映射，若不存在，则打印异常信息
                    System.out.println("文件 " + Key + " 的信息在暂存区中不存在");
                } else {    //若存在
                    index.map.remove(Key);    //删除映射
                    System.out.println("文件 " + Key + " 的信息本来存在于暂存区中，现已从暂存区中移除");
                }
            }
        }

        //System.out.println("现在的index哈希表是：");
        index.printMap();    //把index的哈希表打印出来
        //System.out.println("包含的文件数量为：" + index.map.size() + "个\n");

        //创建文件输出流，把修改过的index对象序列化到index文件中，相当于更新了index文件
        FileOutputStream fos = new FileOutputStream(index_dir);
        ObjectOutputStream oos = new ObjectOutputStream(fos);

        oos.writeObject(index);

        //清理缓存区
        fos.close();
        oos.close();
        fis.close();
        ois.close();

    }

    public static int searchFile(String relativePath, String Key){
        int addMark = 0;
        String property = System.getProperty("user.dir"); //property便是当前所在文件夹的绝对路径
        File dir = new File(property + File.separator + relativePath);
        File[] files = dir.listFiles();

        for(File f:files){   //遍历工作区目录,检查暂存区中的文件是否在工作区已被删除
            String name = relativePath + File.separator + f.getName();
            if(f.isFile()){   //这个判断是不必要的，在进阶版中区分文件和文件夹的时候才有用
                if(Key.equals(name)) {   //如果暂存区里面的键值（即文件名）对应的工作区文件存在
                    addMark += 1;    //标志：发现了重名的文件
                    //System.out.println("在工作区子文件夹找到了" + name);
                }
            } else if (f.isDirectory() && !name.equals(".git")) {
                addMark += searchFile(name, Key);
            }
        }
        return addMark;
    }

}
