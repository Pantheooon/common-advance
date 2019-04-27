#### 1.运行机制
##### yarn通过两类长期运行的守护进程提供自己的核心服务:管理集群上资源使用的资源管理器(resource manager),运行在集群中所有节点上且能够启动和监控
##### 容器(container)的节点管理器(node manager).容器用户执行特定应用程序的进程,过程如下:
![](https://img-blog.csdn.net/20180112114246411?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvdTAxMTMzMTQzMA==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

##### 在hadoop中一次MR合称为job,map/reduce过程称为task,yarn的作用就是将map或者reduce过程分散到不同机器上去执行,这里就涉及到资源分配和任务调度的问题.
##### 大致流程如下
##### 1.客户端向resource manager申请资源,运行mr程序
##### 2.resource manager找到这样一个容器来运行mr
##### 3.节点运行期间也可以向resource manager申请资源
##### 4.定期发送心跳

#### 2.mr1和yarn的比较P82

|mr1 | yarn |
|---|---|
|Jobtracker | 资源管理器,application master,时间轴服务器 |
|Tasktracker | 节点管理器 |
|Slot | 容器 |

#### 3.调度
##### FIFO调度:满足先进先出的规则开始作业,不适合共享集群
##### 容量调度:一个专门队列保证小任务一提交就可以作业,但这个队列容量是专门保留的,牺牲了集群资源利用率
##### 公平调度:会在所有运行的作业之间动态平衡资源