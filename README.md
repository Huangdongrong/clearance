# **跨境清关系统

#### 介绍
这是一个海关清关（进口商品）程序，用来服务跨境电商（暂时只对接了有赞），对接快递服务商（申通/韵达）以及海关的公服平台（成都）， 实现货品的清关发送。
使用场景是:
1. 连接有赞跨境电商商户，拉取商户订单；
2. 连接申通/韵达快递国际接口，能够自动生成快递单号；
3. 连接成都海关公服平台，推送订单、支付单、运单、清关单，实现跨境货品清关。

阿里云服务器89元/年，双12年末特惠，爆款产品限时1折 : [点我进入](https://www.aliyun.com/minisite/goods?userCode=rge3fwmt "点我进入")

#### 软件架构
软件的主要框架使用了springboot的框架，为了快速开发减少重复发明轮子，使用了[RuoYi](https://gitee.com/y_project/RuoYi "RuoYi")后台管理框架。

#### 内置功能

1.  系统管理（用户、角色、菜单、部门、岗位、参数、通知公告、日志）
2.  系统监控（在线用户、定时任务、数据监控、服务监控）
3.  清关信息（添加订单、有赞清关、库存盘点）
4.  平台店铺（店铺列表、订单列表、人工推单）

#### 演示图

![1.](http://dengbao-file.oss-cn-beijing.aliyuncs.com/qingma/1.png "1")

![2](http://dengbao-file.oss-cn-beijing.aliyuncs.com/qingma/2.png "2")

![3](http://dengbao-file.oss-cn-beijing.aliyuncs.com/qingma/3.png "3")

![4](http://dengbao-file.oss-cn-beijing.aliyuncs.com/qingma/4.png "4")

![5](http://dengbao-file.oss-cn-beijing.aliyuncs.com/qingma/5.png "5")

![6](http://dengbao-file.oss-cn-beijing.aliyuncs.com/qingma/6.png "6")

![7](http://dengbao-file.oss-cn-beijing.aliyuncs.com/qingma/7.png "7")

#### 需要注意

** 因为本系统对接多个对外系统，涉及到用户权限的隐私问题，所以配置文件被改动过；数据表的结构在，但是数据被清理掉了；公布出来给需要做清关系统的朋友做个参考吧。本系统已经线上运行，代码上是没有问题的，发现问题应该是把配置文件的值做了清洗的原因。**

#### 联系作者

![微信/QQ联系方式](http://dengbao-file.oss-cn-beijing.aliyuncs.com/saaas.png "微信/QQ联系方式")