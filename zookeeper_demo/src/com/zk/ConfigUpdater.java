package com.zk;

import org.apache.zookeeper.KeeperException;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class ConfigUpdater {

    public static final String PATH = "/config";

    private ActiveKeyValueStore store;
    private Random random = new Random();

    public ConfigUpdater(String hosts) throws IOException, InterruptedException {
        store = new ActiveKeyValueStore();
        store.connect(hosts);
    }

    public void run() throws KeeperException, InterruptedException {
        while (true) {
            if (true) {
                String value = random.nextInt(100) + "";
                store.write(PATH, value);
                TimeUnit.SECONDS.sleep(random.nextInt(10));
            }else{
                break;
            }
        }
    }
}
