package com.iipcloud.jfinal.plugin.mqtt;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;

import com.jfinal.kit.Kv;

/**
 * <p>Title: MqttKit</p>
 * <p>Description: MQTT 消息处理工具
 * 注意事项:使用apollo服务器时,以/开头的消息无法订阅到
 * </p>
 * @author Dean
 * @author 肖晓霖
 * @date 2019年3月6日
 */
public class MqttKit {
    static final String MAIN_CONFIG = "main";
    /**
     * QOS_AT_MOST_ONCE 最多一次，有可能重复或丢失
     * MQTT 消息质量
     */
    public static final int QOS_AT_MOST_ONCE = 0;
    /**
     * QOS_AT_LEAST_ONCE 至少一次，有可能重复
     * 应答过程: Client[Qos=1,DUP=0/*重复次数*\/,MessageId=x --->PUBLISH--> Server收到后，存储Message，发布，删除，向Client回发PUBACK
     * 如果你需要向MQTT broker确认你已经处理的消息，建议将消息设为质量设为QOS_AT_LEAST_ONCE 然后手动应答消息
     * MQTT 消息质量
     */
    public static final int QOS_AT_LEAST_ONCE = 1;
    /**
     * QOS_EXACTLY_ONCE 只有一次，确保消息只到达一次（用于比较严格的计费系统）
     * MQTT 消息质量
     */
    public static final int QOS_EXACTLY_ONCE = 2;
    static Map<String, MqttPro> proMap = new HashMap<>();

    static MqttPro mqttPro = null;

    /**
     * @param configName
     * @param mailPro
     */
    static void init(String configName, MqttPro mqttPro) {
        if(proMap.get(configName) != null) {
            throw new RuntimeException(configName + "配置的Mail已经存在！");
        }
        proMap.put(configName, mqttPro);
        if(MAIN_CONFIG.equals(configName)) {
            MqttKit.mqttPro = mqttPro;
        }
    }

    /**
     * @param configName
     * @return
     */
    public static MqttPro use(String configName) {
        MqttPro mqttPro = proMap.get(configName);
        if(mqttPro == null) {
            throw new RuntimeException(configName + "配置的Mail不存在！");
        }
        return mqttPro;
    }

    public static MqttAsyncClient getClient() {
        return mqttPro.getClient();
    }

    /**
     * 订阅主题
     * @param topic String 主题
     * @param qos int 消息质量
     * @param messageListener 收到消息是时IMqttMessageListener回调接口
     * @throws MqttException 订阅失败时 触发该异常
     */
    public static boolean subscribe(String topic, int qos, IMqttMessageListener messageListener) throws MqttException {
        return subscribe(topic, qos, messageListener, 0);
    }

    /**
     * 订阅主题
     * @param topic String 主题
     * @param qos int 消息质量
     * @param messageListener 收到消息是时IMqttMessageListener回调接口
     * @param timeout 回调接口 耗时时间 单位:毫秒
     * @throws MqttException 订阅失败时 或订阅超时触发该异常
     */
    public static boolean subscribe(String topic, int qos, IMqttMessageListener messageListener, long timeout) throws MqttException {
        return mqttPro.subscribe(topic, qos, messageListener, timeout);
    }

    /**
     * 取消订阅
     * @param topic String 需要被取消主题
     * @return boolean true:取消成功 false:取消失败
     * @throws MqttException
     */
    public static boolean unsubscribe(String topic) throws MqttException {
        return mqttPro.unsubscribe(topic);
    }

    /**
     * 发布主题
     * @param topic String 主题
     * @param payload byte[] 消息内容
     * @param qos int 消息质量
     * @param retained 是否持久化 如果设为true 服务器会将该消息发送给当前的订阅者，还会降这个消息推送给新订阅这个题注的订阅者
     * @throws MqttException
     * @throws MqttPersistenceException
     */
    public static boolean publish(String topic, byte[] payload, int qos, boolean retained) throws MqttPersistenceException, MqttException {
        return publish(topic, payload, qos, retained, 0);
    }

    /**
     * 发布主题
     * @param topic String 主题
     * @param payload byte[] 消息内容
     * @param qos int 消息质量
     * @param retained 是否持久化 如果设为true 服务器会将该消息发送给当前的订阅者，还会将这个消息推送给新订阅这个题注的订阅者
     * @param timeout 发布超时时间
     * @throws MqttException
     * @throws MqttPersistenceException
     */
    public static boolean publish(String topic, byte[] payload, int qos, boolean retained, long timeout) throws MqttPersistenceException, MqttException {
        return mqttPro.publish(topic, payload, qos, retained, timeout);
    }

    /**
     * 告诉 MQTT Broker 我已经收到这条消息
     * 关于这个方法我得多说两句，使用这个的前提条件:
     * 1、MqttPlugin.manualAcks 必须为true
     * 2、消息质量 Qos 必须为 1
     * 只有满足这两个条件时，调用该方法才有用，不满足这两个条件，调也是白调
     * 不是我限制，paho-mqtt-client 底层就是这么限制的，我也没辙
     * @param message 消息体
     * @throws MqttException
     */
    public static void ack(MqttMessage message) throws MqttException {
        mqttPro.ack(message);
    }

    public static boolean publish(String topic, Kv paylod, int qos, boolean retained) throws MqttPersistenceException, MqttException {
        return publish(topic, paylod.toJson().getBytes(), qos, retained, 0);
    }
}