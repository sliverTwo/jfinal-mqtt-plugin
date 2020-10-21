# jfinal Mqtt消息插件
## 简介
jfinal-mqtt-plugin是jfinal的mqtt消息插件插件,目前基于eclipse开源的paho项目。

## MAVEN导入
```
<dependency>
	<groupId>com.iipcloud</groupId>
	<artifactId>jfinal-mqtt-plugin</artifactId>
	<version>1.0.0</version>
</dependency>
```

## 示例
####  配置文件
```
#JFinal-mqtt插件配置文件

#MQTT Broker连接地址 默认地址:tcp://127.0.0.1:1883
mqtt.brokerURL=tcp://192.168.0.188:1883

#MQTT ClientId 默认值:"jf_mq_p_"+System.nanoTime()
mqtt.clientId=

#MQTT Client连接MQTT Broker时使用的用户名，密码 默认不设置
mqtt.userName=username
mqtt.password=password

#是否手动发送消息应答 该设置只在Qos=1有效 默认false
mqtt.manualAcks=false

#是否自动重连 默认false
mqtt.automaticReconnection=true

#自动检测连接重连时间 默认5s 该参数只在mqtt.automaticReconnection=生效
mqtt.reConnectionTimeInterval=5

#是否清理session，false时可接收离线消息 true忽略离线消息 默认true
mqtt.cleanSession=true

#连接MQTT Broker超时时间 默认:30s
mqtt.connectionTimeout=60

#MQTT 心跳时间间隔 默认60s
mqtt.keepAliveInterval=30

#使用MQTT协议的版本 默认3.1.1
mqtt.version=3.1.1

#MQTT最大连接数 默认:10
mqtt.maxConnections=10

#MQTT SSL配置文件地址
mqtt.sslProperties=

#MQTT 消息保存方式 如果不设置，默认保存在内存中，设置了则保存着指定的目录下
mqtt.stroageDir=

# 启用默认的全局回调
mqtt.enableDefaultCallback = false
# 同时发送的最大消息数
mqtt.maxInflight = 1000
```

### 方法介绍
> 主要有发布、订阅、取消订阅以及手动确认的方法
1. boolean subscribe(String topic, int qos, IMqttMessageListener messageListener)
2. boolean subscribe(String topic, int qos, IMqttMessageListener messageListener, long timeout)
3. sub(String topic, int qos, IMqttMessageListener messageListener)
4. sub(String topic, int qos, IMqttMessageListener messageListener, long timeout)
5. unsub(String topic)
3. boolean unsubscribe(String topic)
4. publish(String topic, Kv paylod, int qos, boolean retained)
5. publish(String topic, byte[] payload, int qos, boolean retained)
6. publish(String topic, byte[] payload, int qos, boolean retained, long timeout)
7. pub(String topic, byte[] payload, int qos, boolean retained)
8. pub(String topic, byte[] payload, int qos, boolean retained, long timeout)
9. pub(String topic, Kv paylod, int qos, boolean retained)
7. ack(MqttMessage message)

### 参数说明

|参数名			|类型					|说明																																				|
|--				|--						|--																																					|
|topic			|String					|发布或订阅的消息主题																																|
|qos			|int					|消息的质量 MqttKit.QOS_AT_MOST_ONCE（最多一次） QOS_AT_LEAST_ONCE(最少一次) QOS_EXACTLY_ONCE(只有一次) 注:消息质量取的是发布者和订阅者中最低的一个	|
|messageListener|IMqttMessageListener	|收到指定主题消息后的回调																															|
|paylod			|byte或者Kv				|发送的消息内容																																		|
|retained		|boolean				|是否持久化 如果设为true 服务器会将该消息发送给当前的订阅者，还会将这个消息推送给新订阅这个题注的订阅者												|
|timeout		|long					| 超时时间																																			|
|message		|MqttMessage			|消息体																																				|


