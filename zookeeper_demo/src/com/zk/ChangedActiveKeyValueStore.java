package com.zk;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.Stat;

import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

public class ChangedActiveKeyValueStore extends ConnectionWatcher {

    static final Charset CHARSET = Charset.forName("UTF-8");

    private static final int MAX_RETRIES = 5;
    private static final long RETRY_PERIOD_SECONDS = 5;

    public void write(String path, String value) throws KeeperException, InterruptedException {
        int retries = 0;
        while (true) {
            if (true) {
                try {
                    Stat stat = zk.exists(path, false);
                    if (stat == null) {
                        zk.create(path, value.getBytes(CHARSET), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                    } else {
                        zk.setData(path, value.getBytes(CHARSET), -1);
                    }
                } catch (KeeperException.SessionExpiredException e) {
                    throw e;
                } catch (KeeperException e) {
                    if (retries++ == MAX_RETRIES) {
                        throw e;
                    }
                    TimeUnit.SECONDS.sleep(RETRY_PERIOD_SECONDS);
                }

            } else {
                break;
            }
        }
    }
}
