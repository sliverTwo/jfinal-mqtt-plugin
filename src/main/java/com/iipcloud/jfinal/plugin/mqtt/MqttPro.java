/**
 * <p>Title: MqttPro.java</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2019</p>
 * <p>Company: www.iipcloud.com</p>
 * 
 * @author 肖晓霖
 * @date 2019年6月6日
 * @version 1.0
 */
package com.iipcloud.jfinal.plugin.mqtt;

import java.util.Properties;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;

import com.jfinal.kit.Kv;
import com.jfinal.kit.LogKit;
import com.jfinal.kit.Prop;
import com.jfinal.kit.StrKit;

/**
 * <p>Title: MqttPro</p>
 * <p>Description: </p>
 * 
 * @author 肖晓霖
 * @email 18142611739@163.com
 * @date 2019年6月6日
 */
public class MqttPro {
    private MqttConfig config;
    /** scheduler 自动重连的任务调度器 */
    private ScheduledExecutorService scheduler;
    /** client 异步MQTT Client */
    private MqttAsyncClient client;
    /** options MQTT Cleint连接配置 */
    private MqttConnectOptions options;

    /**
     * 订阅主题
     * 
     * @param topic           String 主题
     * @param qos             int 消息质量
     * @param messageListener 收到消息是时IMqttMessageListener回调接口
     * @throws MqttException 订阅失败时 触发该异常
     */
    public boolean subscribe(String topic, int qos, IMqttMessageListener messageListener) throws MqttException {
        return subscribe(topic, qos, messageListener, 0);
    }

    /**
     * 订阅主题
     * 
     * @param topic           String 主题
     * @param qos             int 消息质量
     * @param messageListener 收到消息是时IMqttMessageListener回调接口
     * @param timeout         回调接口 耗时时间 单位:毫秒
     * @throws MqttException 订阅失败时 或订阅超时触发该异常
     */
    public boolean subscribe(String topic, int qos, IMqttMessageListener messageListener, long timeout) throws MqttException {
        if (timeout <= -1 || timeout > Long.MAX_VALUE) {
            throw new IllegalArgumentException("订阅回调函数超时时间不能小于0 且 大于 Long.MAX_VALUE");
        }
        if (!client.isConnected()) {
            client.reconnect();
        }
        IMqttToken token = client.subscribe(topic, qos, messageListener);
        if (timeout <= 0) {
            token.waitForCompletion();
        } else {
            token.waitForCompletion(timeout);
        }
        return token.isComplete();
    }

    /**
     * 取消订阅
     * 
     * @param topic String 需要被取消主题
     * @return boolean true:取消成功 false:取消失败
     * @throws MqttException
     */
    public boolean unsubscribe(String topic) throws MqttException {
        IMqttToken token = client.unsubscribe(topic);
        token.waitForCompletion();
        return token.isComplete();
    }

    /**
     * 发布主题
     * 
     * @param topic    String 主题
     * @param payload  byte[] 消息内容
     * @param qos      int 消息质量
     * @param retained 是否持久化 如果设为true 服务器会将该消息发送给当前的订阅者，还会降这个消息推送给新订阅这个题注的订阅者
     * @throws MqttException
     * @throws MqttPersistenceException
     */
    public boolean publish(String topic, byte[] payload, int qos, boolean retained) throws MqttPersistenceException, MqttException {
        return publish(topic, payload, qos, retained, 0);
    }

    /**
     * 发布主题
     * 
     * @param topic    String 主题
     * @param payload  byte[] 消息内容
     * @param qos      int 消息质量
     * @param retained 是否持久化 如果设为true 服务器会将该消息发送给当前的订阅者，还会将这个消息推送给新订阅这个题注的订阅者
     * @param timeout  发布超时时间
     * @throws MqttException
     * @throws MqttPersistenceException
     */
    public boolean publish(String topic, byte[] payload, int qos, boolean retained, long timeout) throws MqttPersistenceException, MqttException {
        if (timeout <= -1 || timeout > Long.MAX_VALUE) {
            throw new IllegalArgumentException("发布回调函数超时时间不能小于0 且 大于 Long.MAX_VALUE");
        }
        if (!client.isConnected()) {
            client.reconnect();
        }
        IMqttToken token = client.publish(topic, payload, qos, retained);
        if (timeout <= 0) {
            token.waitForCompletion();
        } else {
            token.waitForCompletion(timeout);
        }
        return token.isComplete();
    }

    /**
     * 告诉 MQTT Broker 我已经收到这条消息
     * 关于这个方法我得多说两句，使用这个的前提条件:
     * 1、MqttPlugin.manualAcks 必须为true
     * 2、消息质量 Qos 必须为 1
     * 只有满足这两个条件时，调用该方法才有用，不满足这两个条件，调也是白调
     * 不是我限制，paho-mqtt-client 底层就是这么限制的，我也没辙
     * 
     * @param message 消息体
     * @throws MqttException
     */
    public void ack(MqttMessage message) throws MqttException {
        if (this.config.isManualAcks() && message.getQos() == MqttKit.QOS_AT_LEAST_ONCE) {
            client.messageArrivedComplete(message.getId(), message.getQos());
        }
    }

    public boolean pub(String topic, Kv paylod, int qos, boolean retained) throws MqttPersistenceException, MqttException {
        return publish(topic, paylod.toJson().getBytes(), qos, retained, 0);
    }

    public MqttPro(Prop prop) {
        super();
        this.config = new MqttConfig(prop);
    }

    /**
     * 使用线程池定时检查client连接状态并重连client
     */
    private void reConnection() {
        this.scheduler = new ScheduledThreadPoolExecutor(1, new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "mqttCheckThread");
            }
        });
        this.scheduler.scheduleAtFixedRate(() -> {
            if (!client.isConnected()) {
                try {
                    client.reconnect();
                    LogKit.debug("MQTT Client 尝试重连结果" + (this.client.isConnected() ? "重连成功" : "重连失败，继续尝试"));
                } catch (MqttException e) {
                    LogKit.error("MQTT Client重连失败", e);
                } finally {
                }
            }
        }, 0, this.config.getReConnectionTimeInterval() * 1000, TimeUnit.MILLISECONDS);
    }

    public boolean start() {
        // 获取实例化mqtt client
        try {
            String stroageDir = this.config.getStroageDir();
            String clientId = this.config.getClientId();
            String brokerURL = this.config.getBrokerURL();
            if (StrKit.isBlank(stroageDir)) {
                this.client = new MqttAsyncClient(brokerURL, clientId, null);
            } else {
                this.client = new MqttAsyncClient(brokerURL, clientId, new MqttDefaultFilePersistence(stroageDir));
            }
        } catch (MqttException e) {
            LogKit.error("MQTT Client Plugin 初始化Client失败", e);
            return false;
        }
        this.client.setManualAcks(this.config.isManualAcks());
        // 启用默认回调
        if (this.config.isEnableDefaultCallback()) {
            this.client.setCallback(new DefaultCallback(this.client));
        }
        this.options = new MqttConnectOptions();
        this.options.setAutomaticReconnect(this.config.isAutomaticReconnection());
        this.options.setCleanSession(this.config.isCleanSession());
        this.options.setConnectionTimeout(this.config.getConnectionTimeout());
        this.options.setKeepAliveInterval(this.config.getKeepAliveInterval());
        this.options.setMaxInflight(this.config.getMaxInflight());

        // 设置mqtt版本信息
        String version = this.config.getVersion();
        if (StrKit.notBlank(version)) {

            if (MqttConfig.MQTT_VERSION_3_1_1.equals(version)) {
                this.options.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1_1);
            } else if (MqttConfig.MQTT_VERSION_3_1.equals(version)) {
                this.options.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1);

            } else {
                this.options.setMqttVersion(MqttConnectOptions.MQTT_VERSION_DEFAULT);
            }
        } else {
            this.options.setMqttVersion(MqttConnectOptions.MQTT_VERSION_DEFAULT);
        }

        // 设置用户名密码
        String userName = this.config.getUserName();
        String password = this.config.getPassword();
        if (StrKit.notBlank(userName) && StrKit.notBlank(password)) {
            this.options.setUserName(userName);
            this.options.setPassword(password.toCharArray());
        }

        // 设置遗嘱消息
        String willTopic = this.config.getWillTopic();
        String willPayload = this.config.getWillPayload();
        if (StrKit.notBlank(willTopic) && StrKit.notBlank(willPayload)) {
            this.options.setWill(willTopic, willPayload.getBytes(), this.config.getWillQos(), this.config.isWillretained());
        }

        // 设置SSL配置文件
        Properties sslProperties = this.config.getSslProperties();
        if (sslProperties != null && !sslProperties.isEmpty() && sslProperties.values().size() > 0) {
            this.options.setSSLProperties(sslProperties);
        }

        try {
            IMqttToken token = this.client.connect(this.options);
            token.waitForCompletion(this.config.getConnectionTimeout() * 1000);
            // 默认回调中，断线会自动重连
            if (this.config.isAutomaticReconnection() && !this.config.isEnableDefaultCallback()) {
                this.reConnection();
            }
        } catch (MqttSecurityException e) {
            LogKit.error("MQTT Clinet连接MQTT Broker连接信息验证失败,请检查连接配置信息", e);
            return false;
        } catch (MqttException e) {
            LogKit.error("MQTT Clinet连接MQTT Broker连接失败", e);
            return false;
        }
        return true;
    }

    public boolean stop() {
        if (this.config.isAutomaticReconnection() && this.scheduler != null && !this.scheduler.isShutdown()) {
            this.scheduler.shutdownNow();
        }
        if (this.client != null && this.client.isConnected()) {
            try {
                this.client.disconnect();
            } catch (MqttException e) {
                LogKit.error("释放MQTT Client连接失败", e);
                return false;
            }
        }
        return true;
    }

    /**
     * <p>Title: setWill</p>
     * <p>Description: 设置遗嘱消息</p>
     * 
     * @param topic
     * @param payload
     * @param qos
     * @param retainedd
     */
    public void setWill(String topic, String payload, int qos, boolean retainedd) {
        this.config.setWillTopic(topic);
        this.config.setWillPayload(payload);
        this.config.setWillQos(qos);
        this.config.setWillretained(retainedd);
    }

    public MqttPro(MqttAsyncClient client) {
        super();
        this.client = client;
    }

    /**
     * @return the client
     */
    public MqttAsyncClient getClient() {
        return client;
    }

    /**
     * <p>Title: setCallback</p>
     * <p>Description: </p>
     * 
     * @date 2019年6月6日
     * @param callback
     */
    public void setCallback(MqttCallback callback) {
        this.client.setCallback(callback);
    }

}
