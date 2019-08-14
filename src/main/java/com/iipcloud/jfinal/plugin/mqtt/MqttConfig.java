/**
 * <p>Title: Config.java</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2019</p>
 * <p>Company: www.iipcloud.com</p>
 * 
 * @author 肖晓霖
 * @date 2019年6月6日
 * @version 1.0
 */
package com.iipcloud.jfinal.plugin.mqtt;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;

import com.jfinal.kit.LogKit;
import com.jfinal.kit.Prop;
import com.jfinal.kit.StrKit;

/**
 * <p>Title: Config</p>
 * <p>Description: </p>
 * 
 * @author 肖晓霖
 * @email 18142611739@163.com
 * @date 2019年6月6日
 */
public class MqttConfig {
    public static final String MQTT_VERSION_3_1_1 = "3.1.1";
    public static final String MQTT_VERSION_3_1 = "3.1";
    public static final int DEFAULT_RECONNECT_TIME_INTERVAL = 5;

    /** brokerURL MQTT服务器连接地址 */
    private String brokerURL;
    /** clientId 当前客户端ID */
    private String clientId;
    /** userName 连接MQTT的用户名 */
    private String userName;
    /** password 连接MQTT的密码 */
    private String password;
    /** manualAcks 是否手动应答 ,默认为false */
    private boolean manualAcks = false;
    /** automaticReconnection 是否自动重连 默认false */
    private boolean automaticReconnection = false;
    /** cleanSession 是否保持session，true无法接受离线消息， false可以接受离线消息 */
    private boolean cleanSession = true;
    /** reConnectionTimeInterval 重试重连的时间间隔 */
    private int reConnectionTimeInterval = 5;
    /** connectionTimeout 连接超时时间 */
    private int connectionTimeout = 30;
    /** keepAliveInterval 心跳时间 */
    private int keepAliveInterval = 60;
    /** version 可选版本3.0 3.1.1 */
    private String version = "3.1.1";
    /** maxConnections 最大连接数 业务量大的话 建议增加这个设置 */
    private int maxConnections = 10;
    /** sslProperties MQTT SSL链接配置 */
    private Properties sslProperties;
    /**
     * stroageDir
     * 对于消息质量为1或者2的消息必须在client和server进行存储，以保证消息服务质量
     * Paho-mqtt-client 默认存储在内存中，如果设置了stroageDir，则可以将mqtt的消息序列化指定的目录之下
     */
    private String stroageDir;
    // 下面是遗嘱消息的设置
    /** willTopic 将遗嘱消息发布到那个主题 */
    private String willTopic;
    /** willPayload 遗嘱消息内容 */
    private String willPayload;
    /** willQos 遗嘱消息内容质量 */
    private int willQos = 2;
    /** willretained 是否保留这个遗嘱消息 */
    private boolean willretained = false;

    /** enableDefaultCallback 启用默认的回调 */
    private boolean enableDefaultCallback = false;

    /** maxInflight 同时发送的消息最大容量 */
    private int maxInflight;

    public MqttConfig(Prop prop) {
        super();
        this.brokerURL = prop.get("mqtt.brokerURL", "tcp://127.0.0.1:1883");
        this.clientId = prop.get("mqtt.clientId", "jf_mq_p_" + System.nanoTime());
        if (StrKit.isBlank(this.clientId)) {
            this.clientId = "jf_mq_p_" + System.nanoTime();
        }
        this.userName = prop.get("mqtt.userName", null);
        this.password = prop.get("mqtt.password", null);
        this.manualAcks = prop.getBoolean("mqtt.manualAcks", false);
        this.automaticReconnection = prop.getBoolean("mqtt.automaticReconnection", false);
        this.cleanSession = prop.getBoolean("mqtt.cleanSession", true);
        this.connectionTimeout = prop.getInt("mqtt.connectionTimeout", 30);
        this.keepAliveInterval = prop.getInt("mqtt.keepAliveInterval", 60);
        this.version = prop.get("mqtt.version", "3.1.1");
        this.maxConnections = prop.getInt("mqtt.maxConnections", 10);
        this.stroageDir = prop.get("mqtt.stroageDir", null);
        this.reConnectionTimeInterval = prop.getInt("mqtt.reConnectionTimeInterval", DEFAULT_RECONNECT_TIME_INTERVAL);
        this.enableDefaultCallback = prop.getBoolean("mqtt.enableDefaultCallback", false);
        this.enableDefaultCallback = prop.getBoolean("mqtt.enableDefaultCallback", false);
        this.maxInflight = prop.getInt("mqtt.maxInflight", MqttConnectOptions.MAX_INFLIGHT_DEFAULT);
        String sslPropPath = prop.get("mqtt.sslProperties");
        if (StrKit.notBlank(sslPropPath)) {
            Properties p = new Properties();
            try {
                p.load(new FileInputStream(sslPropPath));
                this.sslProperties = p;
            } catch (FileNotFoundException e) {
                LogKit.error("MQTT SSL配置文件未找到,请检查文件[" + sslPropPath + "]是否存在", e);
            } catch (IOException e) {
                LogKit.error("MQTT SSL配置文件[" + sslPropPath + "]读取失败", e);
            }
        }
    }

    public MqttConfig() {
        super();
    }

    public MqttConfig(String brokerURL, String clientId, String userName, String password, boolean manualAcks, boolean automaticReconnection, boolean cleanSession, int reConnectionTimeInterval, int connectionTimeout, int keepAliveInterval, String version, int maxConnections, Properties sslProperties, String stroageDir, String willTopic, String willPayload, int willQos, boolean willretained, boolean enableDefaultCallback) {
        super();
        this.brokerURL = brokerURL;
        this.clientId = clientId;
        this.userName = userName;
        this.password = password;
        this.manualAcks = manualAcks;
        this.automaticReconnection = automaticReconnection;
        this.cleanSession = cleanSession;
        this.reConnectionTimeInterval = reConnectionTimeInterval;
        this.connectionTimeout = connectionTimeout;
        this.keepAliveInterval = keepAliveInterval;
        this.version = version;
        this.maxConnections = maxConnections;
        this.sslProperties = sslProperties;
        this.stroageDir = stroageDir;
        this.willTopic = willTopic;
        this.willPayload = willPayload;
        this.willQos = willQos;
        this.willretained = willretained;
        this.enableDefaultCallback = enableDefaultCallback;
    }

    /**
     * <p>Title: </p>
     * <p>Description: </p>
     * 
     * @param brokerURL
     * @param clientId
     * @param userName
     * @param password
     * @param manualAcks
     * @param automaticReconnection
     * @param cleanSession
     * @param reConnectionTimeInterval
     * @param connectionTimeout
     * @param keepAliveInterval
     * @param version
     * @param maxConnections
     * @param sslProperties
     * @param stroageDir
     * @param willTopic
     * @param willPayload
     * @param willQos
     * @param willretained
     * @param enableDefaultCallback
     * @param maxInflight
     */
    public MqttConfig(String brokerURL, String clientId, String userName, String password, boolean manualAcks, boolean automaticReconnection, boolean cleanSession, int reConnectionTimeInterval, int connectionTimeout, int keepAliveInterval, String version, int maxConnections, Properties sslProperties, String stroageDir, String willTopic, String willPayload, int willQos, boolean willretained, boolean enableDefaultCallback, int maxInflight) {
        super();
        this.brokerURL = brokerURL;
        this.clientId = clientId;
        this.userName = userName;
        this.password = password;
        this.manualAcks = manualAcks;
        this.automaticReconnection = automaticReconnection;
        this.cleanSession = cleanSession;
        this.reConnectionTimeInterval = reConnectionTimeInterval;
        this.connectionTimeout = connectionTimeout;
        this.keepAliveInterval = keepAliveInterval;
        this.version = version;
        this.maxConnections = maxConnections;
        this.sslProperties = sslProperties;
        this.stroageDir = stroageDir;
        this.willTopic = willTopic;
        this.willPayload = willPayload;
        this.willQos = willQos;
        this.willretained = willretained;
        this.enableDefaultCallback = enableDefaultCallback;
        this.maxInflight = maxInflight;
    }

    /**
     * @return the brokerURL
     */
    public String getBrokerURL() {
        return brokerURL;
    }

    /**
     * @param brokerURL the brokerURL to set
     */
    public void setBrokerURL(String brokerURL) {
        this.brokerURL = brokerURL;
    }

    /**
     * @return the clientId
     */
    public String getClientId() {
        return clientId;
    }

    /**
     * @param clientId the clientId to set
     */
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    /**
     * @return the userName
     */
    public String getUserName() {
        return userName;
    }

    /**
     * @param userName the userName to set
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the manualAcks
     */
    public boolean isManualAcks() {
        return manualAcks;
    }

    /**
     * @param manualAcks the manualAcks to set
     */
    public void setManualAcks(boolean manualAcks) {
        this.manualAcks = manualAcks;
    }

    /**
     * @return the automaticReconnection
     */
    public boolean isAutomaticReconnection() {
        return automaticReconnection;
    }

    /**
     * @param automaticReconnection the automaticReconnection to set
     */
    public void setAutomaticReconnection(boolean automaticReconnection) {
        this.automaticReconnection = automaticReconnection;
    }

    /**
     * @return the cleanSession
     */
    public boolean isCleanSession() {
        return cleanSession;
    }

    /**
     * @param cleanSession the cleanSession to set
     */
    public void setCleanSession(boolean cleanSession) {
        this.cleanSession = cleanSession;
    }

    /**
     * @return the reConnectionTimeInterval
     */
    public int getReConnectionTimeInterval() {
        return reConnectionTimeInterval;
    }

    /**
     * @param reConnectionTimeInterval the reConnectionTimeInterval to set
     */
    public void setReConnectionTimeInterval(int reConnectionTimeInterval) {
        if (reConnectionTimeInterval < 0 || reConnectionTimeInterval > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("MQTT Client重连时间间隔不能小于0 且 大于 Integer.MAX_VALUE");
        }
        this.reConnectionTimeInterval = reConnectionTimeInterval;
    }

    /**
     * @return the connectionTimeout
     */
    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    /**
     * @param connectionTimeout the connectionTimeout to set
     */
    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    /**
     * @return the keepAliveInterval
     */
    public int getKeepAliveInterval() {
        return keepAliveInterval;
    }

    /**
     * @param keepAliveInterval the keepAliveInterval to set
     */
    public void setKeepAliveInterval(int keepAliveInterval) {
        this.keepAliveInterval = keepAliveInterval;
    }

    /**
     * @return the version
     */
    public String getVersion() {
        return version;
    }

    /**
     * @param version the version to set
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * @return the maxConnections
     */
    public int getMaxConnections() {
        return maxConnections;
    }

    /**
     * @param maxConnections the maxConnections to set
     */
    public void setMaxConnections(int maxConnections) {
        this.maxConnections = maxConnections;
    }

    /**
     * @return the sslProperties
     */
    public Properties getSslProperties() {
        return sslProperties;
    }

    /**
     * @param sslProperties the sslProperties to set
     */
    public void setSslProperties(Properties sslProperties) {
        this.sslProperties = sslProperties;
    }

    /**
     * @return the stroageDir
     */
    public String getStroageDir() {
        return stroageDir;
    }

    /**
     * @param stroageDir the stroageDir to set
     */
    public void setStroageDir(String stroageDir) {
        this.stroageDir = stroageDir;
    }

    /**
     * @return the willTopic
     */
    public String getWillTopic() {
        return willTopic;
    }

    /**
     * @param willTopic the willTopic to set
     */
    public void setWillTopic(String willTopic) {
        this.willTopic = willTopic;
    }

    /**
     * @return the willPayload
     */
    public String getWillPayload() {
        return willPayload;
    }

    /**
     * @param willPayload the willPayload to set
     */
    public void setWillPayload(String willPayload) {
        this.willPayload = willPayload;
    }

    /**
     * @return the willQos
     */
    public int getWillQos() {
        return willQos;
    }

    /**
     * @param willQos the willQos to set
     */
    public void setWillQos(int willQos) {
        this.willQos = willQos;
    }

    /**
     * @return the willretained
     */
    public boolean isWillretained() {
        return willretained;
    }

    /**
     * @param willretained the willretained to set
     */
    public void setWillretained(boolean willretained) {
        this.willretained = willretained;
    }

    /**
     * @return the enableDefaultCallback
     */
    public boolean isEnableDefaultCallback() {
        return enableDefaultCallback;
    }

    /**
     * @param enableDefaultCallback the enableDefaultCallback to set
     */
    public void setEnableDefaultCallback(boolean enableDefaultCallback) {
        this.enableDefaultCallback = enableDefaultCallback;
    }

    /**
     * @return the maxInflight
     */
    public int getMaxInflight() {
        return maxInflight;
    }

    /**
     * @param maxInflight the maxInflight to set
     */
    public void setMaxInflight(int maxInflight) {
        this.maxInflight = maxInflight;
    }

    /**
     * (non-Javadoc)
     * Title: toString
     * Description:
     * 
     * @return
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "MqttConfig [brokerURL=" + brokerURL + ", clientId=" + clientId + ", userName=" + userName + ", password=" + password + ", manualAcks=" + manualAcks + ", automaticReconnection=" + automaticReconnection + ", cleanSession=" + cleanSession + ", reConnectionTimeInterval=" + reConnectionTimeInterval + ", connectionTimeout=" + connectionTimeout + ", keepAliveInterval=" + keepAliveInterval + ", version=" + version + ", maxConnections=" + maxConnections + ", sslProperties=" + sslProperties + ", stroageDir=" + stroageDir + ", willTopic="
                + willTopic + ", willPayload=" + willPayload + ", willQos=" + willQos + ", willretained=" + willretained + ", enableDefaultCallback=" + enableDefaultCallback + ", maxInflight=" + maxInflight + "]";
    }

}
