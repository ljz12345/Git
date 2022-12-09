# Git
Java course project

init  
判断工作区中（当前路径）是否存在.git目录，已存在则打印提示信息；  
创建.git目录，在其中创建objects目录用于blob、tree、commit等对象，命名均为其hash值；  
创建Index对象序列化到.git目录下用于储存 文件名-hash值的对应条目（初始为空）；  
创建HEAD文件储存最近一次的commit id（即commit对象的hash值，初始为空）。  

Blob类  	
一个blob对象记录了一份文件的内容，以文件内容的hash值命名  

add	  
在index对象中添加/修改/删除<文件名, hash值> 条目；  
创建对应blob对象序列化到objects文件夹下；  
当add的文件，只存在于暂存区（index），而不存在于工作区时，在暂存区中删除对应条目；  
输入add . 时，对工作区的全部文件进行一次add操作；  
输入add . 时，将只存在于暂存区中，而不存在于工作区的文件记录，从index中删除  

Tree类	  
tree对象内容与index一致  

commit	  
将index中所有条目生成tree对象序列化到objects文件夹下；  
打印本次commit相对上一次commit的文件变动情况（增加、删除、修改）  
将commit对象序列化到objects文件夹下，commit对象包括以下属性：上一次的commit id、本次commit所生成tree对象id，message、commit时间  
更新HEAD文件中的commit id。  

rm	  
在index对象中删除对应条目，在工作区中删除该文件  
rm --cached：仅删除index中对应条目  

log  
从HEAD文件中读到最近一次的commit id，若HEAD为空打印提示信息。  
反序列化对应的commit对象，打印commit id，message，commit时间，读出该commit中存放的前一次commit id，  
反复执行直到打印完第一次commit的内容。  

reset	  
取出args[] 中的commit id，以及reset模式，其中mixed是默认模式，  
判断objects文件夹中是否存在对应的commit对象，  
reset --soft：修改HEAD文件内容为给定commit id，  
reset --mixed：在3的基础上，重置暂存区到对应commit，  
reset --hard：在4的基础上，重置工作区与暂存区内容一致。  
