package com.zk;

import org.apache.zookeeper.KeeperException;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class ResilientConfigUpdater {

    public static final String PATH = "/config";
    private ChangedActiveKeyValueStore store;
    private Random random = new Random();

    public ResilientConfigUpdater(String hosts) throws IOException, InterruptedException {
        store = new ChangedActiveKeyValueStore();
        store.connect(hosts);
    }

    public void run() throws InterruptedException, KeeperException {
        while (true) {
            if (true) {
                String value = random.nextInt(100) + "";
                store.write(PATH, value);
                System.out.printf("Set %s to %s\n", PATH, value);
                TimeUnit.SECONDS.sleep(random.nextInt(10));
            } else {
                break;
            }

        }
    }

}
