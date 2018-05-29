package com.zk;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

import java.io.IOException;

public class ConfigWatcher implements Watcher {
    private  ActiveKeyValueStore store;

    public ConfigWatcher(String hosts) throws IOException, InterruptedException {
        store = new ActiveKeyValueStore();
        store.connect(hosts);
    }

    @Override
    public void process(WatchedEvent event) {
        if (event.getType() == Event.EventType.NodeDataChanged){
            try {
                displayConfig();
            }catch (InterruptedException e) {
                System.err.println("Interrupted. exiting. ");
                e.printStackTrace();
            } catch (KeeperException e) {
                e.printStackTrace();
                System.out.printf("KeeperExceptions. Exiting.\n", e);
            }
        }
    }

    public void  displayConfig() throws KeeperException, InterruptedException {
        String value = store.read(ConfigUpdater.PATH,this);
        System.out.printf("Read %s as %s\n",ConfigUpdater.PATH,value);
    }
}
