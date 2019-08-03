/**
 * <p>Title: MqttPluginTest.java</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2019</p>
 * <p>Company: www.iipcloud.com</p>
 * @author 肖晓霖
 * @date 2019年6月6日
 * @version 1.0
 */
package com.iipcloud.jfinal.plugin.mqtt.test;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import com.iipcloud.jfinal.plugin.mqtt.MqttKit;
import com.iipcloud.jfinal.plugin.mqtt.MqttPlugin;
import com.jfinal.config.Plugins;
import com.jfinal.plugin.IPlugin;

/**
 * <p>Title: MqttPluginTest</p>
 * <p>Description: </p>
 * @author 肖晓霖
 * @email 18142611739@163.com
 * @date 2019年6月6日
 */
public class MqttPluginTest {
    private static Plugins plugins = new Plugins();

    @BeforeClass
    public static void start() {
        MqttPlugin mqttPlugin = new MqttPlugin("mqtt.properties");
        
        plugins.add(mqttPlugin);
        for (int i = 0; i < 10; i++) {
            plugins.add(new MqttPlugin(String.valueOf(i),"mqtt.properties"));
        }
        for (IPlugin plugin : plugins.getPluginList()) {
            plugin.start();
        }
    }

    public void test() throws MqttException {
        MqttKit.subscribe("$queue/test", 0, new IMqttMessageListener() {
            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                System.out.println(message);
            }
        });
    }

    public static void main(String[] args) {
        start();
        ThreadPoolExecutor pubPool = new ThreadPoolExecutor(1, 1, 1, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
        ThreadPoolExecutor subPool = new ThreadPoolExecutor(10, 10, 1, TimeUnit.SECONDS, new LinkedBlockingQueue<>(), new ThreadFactory() {
            int i = 0;

            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "消费者" + i++);
            }
        });
        pubPool.execute(() -> {
            int i = 0;
            for (;;) {
                try {
                    MqttKit.publish("test", String.valueOf(i).getBytes(), 0, false);
                    System.out.println(i++);
                    Thread.sleep(1000);
                } catch (Exception e) {
                    stop();
                    e.printStackTrace();
                    System.exit(-1);
                    break;
                }
            }
        });
        for (int i = 0; i < 5; i++) {
            final int n = i;
            subPool.execute(() -> {
                try {
                    MqttKit.use(String.valueOf(n)).subscribe("$queue/test", 2, new IMqttMessageListener() {
                        @Override
                        public void messageArrived(String topic, MqttMessage message) throws Exception {
                            System.out.println(Thread.currentThread().getName() + "收到消息:" + message);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

        }
    }

    @AfterClass
    public static void stop() {
        for (IPlugin plugin : plugins.getPluginList()) {
            plugin.stop();
        }
    }
}
