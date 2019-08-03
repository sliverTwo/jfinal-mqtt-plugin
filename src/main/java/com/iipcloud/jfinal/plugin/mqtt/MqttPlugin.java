package com.iipcloud.jfinal.plugin.mqtt;

import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.IPlugin;

/**
 * Jfinal MQTT 插件
 * 使用.eclipse.paho.client.mqttv3作为客户端
 * @author Dean
 * @author sliver
 */
public class MqttPlugin implements IPlugin {
    static final String MAIN_CONFIG = "main";

    private String configName;
    private Prop prop;
    private MqttPro mqttPro;

    public MqttPlugin(String configFile) {
        this(MAIN_CONFIG, configFile);
    }

    public MqttPlugin(String configName, String configFile) {
        super();
        Prop prop = PropKit.use(configFile);
        this.configName = configName;
        this.prop = prop;
    }

    public MqttPlugin(Prop prop) {
        this(MAIN_CONFIG, prop);
    }

    public MqttPlugin(String configName, Prop prop) {
        super();
        this.configName = configName;
        this.prop = prop;
    }

    @Override
    public boolean start() {
        this.mqttPro = new MqttPro(this.prop);
        if(this.mqttPro.start()) {
            MqttKit.init(configName, mqttPro);
            return true;
        }
        return false;
    }

    @Override
    public boolean stop() {
        if(this.mqttPro == null) {
            return true;
        }
        return this.mqttPro.stop();
    }

    /**
     * <p>Title: setWill</p>
     * <p>Description: 设置遗嘱消息</p>
     * @param topic
     * @param payload
     * @param qos
     * @param retainedd
     */
    public void setWill(String topic, String payload, int qos, boolean retainedd) {
        this.mqttPro.setWill(topic, payload, qos, retainedd);
    }

    /**
     * @return the configName
     */
    public String getConfigName() {
        return configName;
    }
}