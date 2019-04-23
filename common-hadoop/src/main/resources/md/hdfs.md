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
##### 1.备份那些组成文件系统元数据持久状态的文件,hadoop通过配置使namenode再多个文件系统上保存元数据,写操作是实时的,且原子.
##### 一般配置为讲数据写入本地磁盘同时,写入一个远程挂载的文件网络系统
##### 2.运行一个辅助namenode,也就是secondarynamenode,其作用是定期合并编辑日志和命名空间镜像,
##### 流程就是namenode启动时会从本地的命名空间中拉去数据到内存中,而secondarynamenode也维护了一份这样的数据
##### 并定期更新,将数据合并到namenode的命名空间,如果namenode一旦出现问题,会重新加载这些数据,那数据丢失的概率性就会降低


#### 3.块缓存
##### 对于访问频繁的文件,其块会被显示地缓存在datanode的内存中,以对外块缓存形式存在,默认,一个块仅缓存在一个datanode内存中

#### 4.联邦hdfs
##### 数据的目录位置信息由namenode保存在内存中,如果数据过多,则内存会成为瓶颈.2.x中添加联邦hdfs,将namenode维护的目录信息划分的更加细致
##### 如/usr目录是一个namenode,/data目录是一个namenode,联邦环境下,每个namenode维护一个命名空间卷,由命名空间的元数据和一个数据块池组成
##### 数据块池包含该命名空间下所有的数据块,命名空间卷互相独立,之间不会通信,一个挂了不会影响另一个.

#### 5.hdfs高可用  P48(有点懵逼)
##### hadoop2配置了active-standby namenode,当active失效,standby开始接管

#### 6.常见的命令

#### 7.hadoop文件系统
##### hdfs是hadoop文件系统的一个实现,也存在其他的文件系统,客户端中FileSystem存在几个具体实现如下:

文件系统 | uri |  实现|描述  
-|-|-|
local | file | LocalFileSystem |使用客户端校验和的本地磁盘文件系统
hdfs | hdfs | DistributedFileSystem |hadoop的分布式文件系统
webhdfs | Webhdfs | WebHdfsFileSystem |基于http的文件系统,提供对hdfs的认证读/写访问
secureWebHdfs | swebhdfs | SWebHdfsFileSystem |webhdfs的https版本
har | har | HarFileSystem |基于其他文件系统之上用于文件存档的文件系统
                  .......










