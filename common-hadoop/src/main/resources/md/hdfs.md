#### 1.数据块
##### hdfs中存储的块大小默认为128m,会将大文件进行切分,这样的好处在于:
###### 1.便于分布式存储
###### 2.简化存储管理,无需涉及底层
###### 3.方便备份,提高可用性

#### 2.namenode和datanode
##### 管理节点(namenode)-工作节点(datanode)的方式运行
##### namenode:管理文件系统的命名空间,维护着文件系统树及整颗树内的文件和目录.
##### datanode:文件系统的工作节点,需要存储并检索数据块,定期向namenode发送他们所存储的块的列表

##### 如果namenode出现问题,会造成数据全部丢失(无法知道数据存储位置),对此的容错hadoop有两套机制
###### 1.备份那些组成文件系统元数据持久状态的文件,hadoop通过配置使namenode再多个文件系统上保存元数据,写操作是实时的,且原子.
###### 一般配置为讲数据写入本地磁盘同时,写入一个远程挂载的文件网络系统
###### 2.运行一个辅助namenode,也就是secondarynamenode,其作用是定期合并编辑日志和命名空间镜像,
###### 流程就是namenode启动时会从本地的命名空间中拉去数据到内存中,而secondarynamenode也维护了一份这样的数据
###### 并定期更新,将数据合并到namenode的命名空间,如果namenode一旦出现问题,会重新加载这些数据,那数据丢失的概率性就会降低


#### 3.块缓存
##### 对于访问频繁的文件,其块会被显示地缓存在datanode的内存中,以对外块缓存形式存在,默认,一个块仅缓存在一个datanode内存中

#### 4.联邦hdfs
##### 数据的目录位置信息由namenode保存在内存中,如果数据过多,则内存会成为瓶颈.2.x中添加联邦hdfs,将namenode维护的目录信息划分的更加细致
##### 如/usr目录是一个namenode,/data目录是一个namenode,联邦环境下,每个namenode维护一个命名空间卷,由命名空间的元数据和一个数据块池组成
##### 数据块池包含该命名空间下所有的数据块,命名空间卷互相独立,之间不会通信,一个挂了不会影响另一个.

#### 5.hdfs高可用  P48(有点懵逼)
##### hadoop2配置了active-standby namenode,当active失效,standby开始接管

#### 6.常用的命令

#### 7.hadoop文件系统
##### hdfs是hadoop文件系统的一个实现,也存在其他的文件系统,客户端中FileSystem存在几个具体实现如下:

|文件系统 | uri |  实现|描述|  
|---|---|---|---|
|local | file | LocalFileSystem |使用客户端校验和的本地磁盘文件系统|
|hdfs | hdfs | DistributedFileSystem |hadoop的分布式文件系统|
|webhdfs | Webhdfs | WebHdfsFileSystem |基于http的文件系统,提供对hdfs的认证读/写访问|
|secureWebHdfs | swebhdfs | SWebHdfsFileSystem |webhdfs的https版本|
|har | har | HarFileSystem |基于其他文件系统之上用于文件存档的文件系统|
                  .......
                  


#### 8.java接口
```
urlTest方法
InputStream in =new URL("hdfs://host:port/path").openStream();
通过这种形式hdfs协议去获取流文件处理的关键点在于URL.setURLStreamHandlerFactory这个方法.这个方法里可以
设置具体协议解析的情况,而hdfs的factory为FsUrlStreamHandlerFactory,由其创建FsUrlStreamHandler,拿到FsUrlConnection
但是这个url类中的factory为静态属性,所以整个应用只能设置一次,hadoop提供了第二种方法来获取流文件.
```

```
通过FileSystem的api,FileSystem的get方法提供了几个重载,获取分布式hdfs系统的文件,如果获取本地,可以通过
FileSystem.getLocal方法.FileSystem.get方法其实返回的对象实例就是FileSystem,根据不协议信息返回不同的FileSystem
如果传入的是hdfs的,那返回的即DistruibutedFileSyetem.如果本地则LocalFileSystem,FileSyetm的open方法为钩子方法,留给各个子类
去实现相应获取流的细节.

FileSystem.open方法返回的为FSDataInputStream,它支持送任意位置访问数据,也就是实现的Seekable接口下的方法
```

#### 9.读取文件的流程(hdfs为例)
##### 1.客户端调用open方法拿到DistributedFileSystem,由DistributedFileSystem访问namenode,namenode告知DistributedFileSystem
##### 数据的最佳dataNode地址
##### 2.客户端和datanode交互,获取数据文件,如果客户端本身是一个datanode,则客户端将会从保存有相应数据块副本的本地datanode读取数据
##### 3.如果在和datanode读取数据过程中,datanode故障,则客户端会尝试从最邻近的datanode读取数据,并会记录故障datanode位置,不会读取该节点后续的数据
##### 如果发现有顺坏的块,会从相近的datanode重新读取,并将此信息告知namenode

##### 在此过程中,对于距离客户端的最佳datanode地址,主要考虑带宽因素,同一机器上的数据读取肯定是要快于跨机房的,hadoop设置的等级如下
###### 1.同一节点上的进程
###### 2.同一机架上的不同节点
###### 3.同一数据中心中不同机架上的节点
###### 4.不同数据中心中的节点









