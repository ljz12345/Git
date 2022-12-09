import java.io.*;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Scanner;



public class mygit {
    public static void main(String[] args) throws Exception {
        if(args == null | args.length <= 0){
            System.out.println("mygit命令输入有误");
            return;
        }

        if(args[0].equals("init")){
            init();
            //init部分结束
        } else if(args[0].equals("add")){
            if(args[1].equals(".")){
                add_all();
            } else {
                add(args[1],1);
            }
            //add部分结束
        } else if(args[0].equals("commit")){
            if(args.length < 3){
                System.out.println("Please provide commit massage");
            } else {
                commit(args[2]);
            }
            //commit部分结束
        }else if(args[0].equals("log")){
            log();
            //log部分结束
        } else if(args[0].equals("rm")){
            if(args[1].equals("--cached")){
                remove(args[2], 0);
            } else {
                remove(args[1], 1);
            }

            //log部分结束
        } else {
            System.out.println("还不支持该命令");
        }
    }


    public static void init() throws IOException {
        //1. 创建.git和object文件夹
        String property = System.getProperty("user.dir"); //property便是当前所在文件夹的绝对路径
        File dir = new File(property + "/.git/objects");

        //判断并且创建objects文件
        if(!dir.exists()){  //如果objects文件还不存在
            new File(property + File.separator + "/.git/objects").mkdirs();//创建.git和object文件夹
            System.out.println("创建.git和object文件夹");
        } else {
            System.out.println(".git和object文件夹已存在");
        }

        //2. 创建空的index文件
        File index_dir = new File(property + "/.git/objects/index");  //index文件的路径及文件名
        if(!index_dir.exists()){  //如果还没创建index文件
            FileOutputStream fos = new FileOutputStream(index_dir);  //则创建空的index文件
            System.out.println("空的index文件创建成功");
            fos.close();  //清空缓存区
        } else {
            System.out.println("index文件已存在");
        }

        //3. 创建空的HEAD文件
        File head_dir = new File(property + "/.git/objects/HEAD");  //HEAD文件的路径及文件名
        if(!head_dir.exists()){  //如果还没创建HEAD文件
            FileOutputStream fos = new FileOutputStream(head_dir);  //创建空的HEAD文件
            System.out.println("空的HEAD文件创建成功");
            fos.close();    //清空缓存区
        } else {
            System.out.println("HEAD文件已存在");
        }
    }


    public static void add(String filename, int option) throws Exception {
        System.out.println("----对" + filename + "文件执行add操作----");
        File f1 = new File(filename);
        //从暂存区中删除不存在于工作区的文件
        if(option == 1){  //option == 1 代表命令行输入的不是add. 而是add [文件名]
            if(!f1.exists()){
                //System.out.println("文件 "+ filename + " 不存在于工作区");
                Index.dltIdx(filename);
                return;
            }
        }

        //1. 读取被add的文件
        FileInputStream in = new FileInputStream(filename);  //问题：输入流的括号里是File和String类型都可以吗
        byte[] content = new byte[1024];
        in.read(content);   //返回读取到的字节数，并且把读到的字节流放进content里

        //2. 获取该文件的哈希值
        String hash = getHashOfByteArray(content);

        //3. 检查blob是否已存在，存在则无事发生，不存在则创建一个
        // 遍历objects目录，看该文件对应的以哈希值命名的blob文件是否已经存在
        String property = System.getProperty("user.dir"); //property便是当前所在文件夹的绝对路径
        File obj_dir = new File(property + "/.git/objects");
        int mark = 0;
        File[] files = obj_dir.listFiles();
        for(File f:files){   //遍历objects目录
            if(f.isFile()){
                String name = f.getName();   //获取文件名
                if(hash.equals(name)){       //如果obj文件夹里面有文件名等于目前文件的哈希值
                    mark += 1;
                }
            }
        }
        if(mark == 0){    //如果obj文件夹里没有对应的blob文件，则写入一个blob
            Blob.writeBlob(content,hash);

        }
        Index.updateIdx(filename, hash);  //修改index文件内容
    }

    public static void add_all() throws Exception {
        String property = System.getProperty("user.dir"); //property便是当前所在文件夹的绝对路径
        File dir = new File(property);
        File[] files = dir.listFiles();
        //对所有文件执行add操作
        for(File f:files){   //遍历工作区目录下的文件
            if(f.isFile()){  //如果不是文件夹
                String name = f.getName();   //获取文件名
                add(name,0);    //option == 0 代表命令行输入的是add [.] ，需要判断index每个键值是否存在于工作区
            }
        }

        Index.dltIdx();

    }

    public static void commit(String msg) throws Exception {
        //1. 生成Tree文件
        Tree tree = new Tree(); //初始化Tree类的对象tree，准备往里加入映射
        tree.writeTree();  //读取index文件的哈希表，生成tree对象，然后写入Tree文件。多功能集中于一个函数，后续可以尝试封装

        //2. 生成Commit文件
        Commit cmt = new Commit(); //初始化Commit类的对象commit，准备往里加入信息
        cmt.setLastCmt(HEAD.getLastCmt());  //
        cmt.setTreeId(tree.getId());  //从tree对象提取id信息，存入blob对象
        cmt.setCmtTime();  //设置提交时间
        cmt.setMessage(msg);  //设置message
        cmt.writeCommit();  //把commit对象写入文件



        //3. 打印增删改情况
        String property = System.getProperty("user.dir"); //property便是当前所在文件夹的绝对路径

        //先把上一次commit读出来
        File LastCmt = new File(property + File.separator + "/.git/objects/" + HEAD.getLastCmt());
        FileInputStream fis = new FileInputStream(LastCmt);    //用文件输入流，打开上一次的commit文件
        ObjectInputStream ois = new ObjectInputStream(fis);    //新建对象输入流，准备读取commit文件中的对象
        Commit Lstcmt = (Commit) ois.readObject();    //读取commit文件中的commit对象

        //再把上一次的Tree读出来
        File LastTree = new File(property + File.separator + "/.git/objects/" + Lstcmt.getTreeId());
        FileInputStream fis1 = new FileInputStream(LastTree);    //用文件输入流，打开上一次的Tree文件
        ObjectInputStream ois1 = new ObjectInputStream(fis1);    //新建对象输入流，准备读取Tree文件中的对象
        Tree LstTree = (Tree) ois1.readObject();    //读取Tree文件中的Tree对象

        //对比两次Tree的区别
        HashMap<String, String> thismap = tree.getMap();
        HashMap<String, String> Lstmap = LstTree.getMap();

        //取tree的哈希表中的所有文件名（取哈希集合，然后转化成字符串数组）
        String[] this_file_list = thismap.keySet().toArray(new String[thismap.keySet().size()]);
        String[] Lst_file_list = Lstmap.keySet().toArray(new String[Lstmap.keySet().size()]);

        System.out.println("本次commit与上次commit对比:");
        for(String Key1:this_file_list){
            int mark_retain = 0;
            for(String Key0:Lst_file_list){   //遍历新旧tree各自的哈希表
                if(Key1.equals(Key0)) {   //如果新tree里面的文件名在旧tree中存在
                    mark_retain += 1;    //标志：该文件不是新增、且没被删除
                    if(!thismap.get(Key1).equals(Lstmap.get(Key0))){    //如果该文件名在两次tree的哈希表中对应的哈希值相同
                        System.out.println(Key1 + " 内容被修改");
                    }
                }
            }
            if(mark_retain == 0){
                System.out.println(Key1 + " 是新增的文件");
            }
        }

        for(String Key0:Lst_file_list){
            int mark_retain = 0;
            for(String Key1:this_file_list){   //遍历新旧tree各自的哈希表
                if(Key0.equals(Key1)) {   //如果新tree里面的文件名在旧tree中存在
                    mark_retain += 1;    //标志：该文件不是新增、且没被删除
                }
            }
            if(mark_retain == 0){
                System.out.println(Key0 + " 被删除");
            }
        }

        //4. 修改HEAD文件，把这次commit文件的id加入
        HEAD.writeHEAD(cmt.getId());
    }

    public static void log() throws IOException, ClassNotFoundException {
        String property = System.getProperty("user.dir"); //property便是当前所在文件夹的绝对路径

        //log任务1: 读取HEAD文件，从中读取最近的一次commit_id
        File HEAD_dir = new File(property + "/.git/objects/HEAD");
        FileInputStream is = new FileInputStream(HEAD_dir);
        ObjectInputStream ois = new ObjectInputStream(is);
        HEAD head =(HEAD) ois.readObject();   //从HEAD文件中反序列化出head对象

        //释放资源
        is.close();
        ois.close();

        //log任务2: 反序列化对应的commit对象，打印最近的一次commit_id，message，commit时间
        String Nearest_cmt = head.getCmtId();    //读取最近的一次commit_id

        while(!Nearest_cmt.equals("")){   //循环打印出历次commit的信息
            System.out.print("COMMIT ID: " + Nearest_cmt + "    ");    //打印commit id

            //打开对应的commit文件
            File cmt_dir = new File(property + "/.git/objects/" + Nearest_cmt);  //打开对应的commit文件

            //从Commit文件中反序列化出cmt对象
            FileInputStream is1 = new FileInputStream(cmt_dir);
            ObjectInputStream ois1 = new ObjectInputStream(is1);
            Commit cmt =(Commit) ois1.readObject();

            //打印message
            System.out.print("MESSAGE: " + cmt.getMessage() + "    ");

            //打印commit时间
            System.out.print("COMMIT TIME: " + cmt.getCmtTime() + "\n");

            is1.close();
            ois1.close();
            Nearest_cmt = cmt.getLastCmt();  //再取上一次commit id
        }

    }

    public static void remove(String f_name, int status) throws IOException, ClassNotFoundException {
        String property = System.getProperty("user.dir"); //property便是当前所在文件夹的绝对路径
        File dir = new File(property + File.separator + "/" + f_name);

        if(status == 1){
            //判断若文件存在于工作区，则删除该文件
            if(dir.exists()){
                if(dir.delete()){
                    System.out.println("file " + f_name + " deleted");
                } else {
                    System.out.println("file delete operation failed");
                }
            } else {  //异常处理
                System.out.println("工作区未找到文件" + f_name + "!");
            }

            //从index删除这个文件的映射
            Index.dltIdx(f_name);

        } else {    //仅删除index中的对应条目，不删除工作区文件
            //从index删除这个文件的映射
            Index.rm_dltIdx(f_name);
        }



    }

    public static String getHashOfByteArray(byte[] content) throws Exception{
        MessageDigest complete = MessageDigest.getInstance("SHA-1");
        complete.update(content);
        byte[] sha1 = complete.digest();

        String hashValue = "";
        for(int j = 0; j < sha1.length; j++) {
            hashValue += Integer.toString((sha1[j]>>4)&0x0F, 16) + Integer.toString(sha1[j]&0x0F, 16);
        }
        return hashValue;
    }

    public static void reset_soft() {

    }
}













