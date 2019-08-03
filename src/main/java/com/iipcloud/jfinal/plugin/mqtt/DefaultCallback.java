package com.iipcloud.jfinal.plugin.mqtt;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import com.jfinal.kit.LogKit;

public class DefaultCallback implements MqttCallback {
    private MqttAsyncClient client;

    public DefaultCallback(MqttAsyncClient client) {
        super();
        this.client = client;
    }

    @Override
    public void connectionLost(Throwable cause) {
        // 连接丢失后，一般在这里面进行重连
        LogKit.info("[MQTT] 连接断开，10S之后尝试重连...");
        // 打印连接原因
        cause.printStackTrace();
        for (int i = 1; i < 1000; i++) {
            try {
                Thread.sleep(10000);
                if (client.isConnected()) {
                    break;
                }
                LogKit.info("mqtt第" + i + "次重连开始...");
                client.reconnect();
                break;
            } catch (Exception e) {
                LogKit.info("mqtt重连失败,继续重连");
                e.printStackTrace();
                continue;
            }
        }
        LogKit.info("mqtt重连成功");
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        LogKit.info("主题:" + topic);
        LogKit.info("message:" + message);
        
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
    }

}
