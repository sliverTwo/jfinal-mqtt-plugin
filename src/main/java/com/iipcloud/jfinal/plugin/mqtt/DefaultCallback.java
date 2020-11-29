package com.iipcloud.jfinal.plugin.mqtt;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import com.iipcloud.jfinal.plugin.mqtt.MqttPro.ListenerWrapper;
import com.jfinal.log.Log;

public class DefaultCallback implements MqttCallback {
    private static Log logger = Log.getLog(DefaultCallback.class);
    private MqttPro mqttPro;

    public DefaultCallback(MqttPro mqttPro) {
        super();
        this.mqttPro = mqttPro;
    }

    @Override
    public void connectionLost(Throwable cause) {
        // 连接丢失后，一般在这里面进行重连
        new Thread(() -> {
            logger.info("[MQTT] 连接断开，1s之后尝试重连...", cause);
            MqttAsyncClient client = mqttPro.getClient();
            boolean reconnecting = false;
            for (int i = 1; i < 1000; i++) {
                try {
                    if (client.isConnected()) {
                        break;
                    }
                    Thread.sleep(1000);
                    boolean needReconnect = !mqttPro.config.isAutomaticReconnection() && !reconnecting && !client.isConnected();
                    if (needReconnect) {
                        logger.info("开始重连...");
                        client.reconnect();
                        reconnecting = true;
                    }
                } catch (Exception e) {
                    logger.info("mqtt重连失败,继续重连,reason:" + e.getMessage(), e);
                    continue;
                }
            }
            reconnecting = false;
            logger.info("开始重新订阅主题");
            for (ListenerWrapper l : mqttPro.callbacks.values()) {
                try {
                    mqttPro.subscribe(l.getTopic(), l.getQos(), l.getListener(), l.getTimeout());
                } catch (MqttException e) {
                    logger.error("主题订阅失败,topic:%s,reason:%s", l.getTopic(), e.getMessage(), e);
                }
            }
            logger.info("主题重新订阅完成");
        }).start();
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        logger.info("主题:" + topic);
        logger.info("message:" + message);
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
    }

}
