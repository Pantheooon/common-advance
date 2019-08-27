### hadoop的单机安装
##### http://hadoop.apache.org/docs/stable/hadoop-project-dist/hadoop-common/SingleCluster.html
#### 1.配置sshkey ssh-keygen -t rsa
#### 2.安装java环境,并配置环境变量
#### 3.修改配置文件,默认web管理界面地址:localhost:50070
##### 3.1 etc/hadoop/core-site.xml,配置hdfs的地址,如果需要绑定公网,则配置hdfs://0.0.0.0:9000
```xml
<configuration>
    <property>
        <name>fs.defaultFS</name>
        <value>hdfs://localhost:9000</value>
    </property>
</configuration>
```
##### 3.2 etc/hadoop/hdfs-site.xml,配置hdfs复制的副本数,设置namenode文件地址,设置datanode地址,默认存tmp,重启会丢失
```xml
<configuration>
    <property>
        <name>dfs.replication</name>
        <value>1</value>
    </property>
      <property>
            <name>dfs.namenode.name.dir</name>
            <value>/usr/hadoop/hdfs/namenode</value>
        </property>
        <property>
            <name>dfs.datanode.data.dir</name>
            <value>/usr/hadoop/hdfs/datanode</value>
        </property>

</configuration>
```

#### 4.格式化hdfs bin/hdfs namenode -format
#### 5.sbin/start-dfs.sh启动hdfs,这个时候会有三个进程,一个是namenode,一个datanode,还有一个secondarynamenode
#### 6.配置yarn 默认管理localhost:8088
##### 6.1 etc/hadoop/mapred-site.xml,配置MapReduce调度框架的名称
```xml
<configuration>
    <property>
        <name>mapreduce.framework.name</name>
        <value>yarn</value>
    </property>
</configuration>
```
##### 6.2 etc/hadoop/yarn-site.xml,配置shuffle
```xml
<configuration>
    <property>
        <name>yarn.nodemanager.aux-services</name>
        <value>mapreduce_shuffle</value>
    </property>
</configuration>
```
#### 7.启动yarn sbin/start-yarn.sh web管理界面端口默认为8088


