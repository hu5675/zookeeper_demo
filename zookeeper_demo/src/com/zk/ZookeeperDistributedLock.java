package com.zk;


import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.CountDownLatch;

/**
 * 描述:
 * ZookeeperDistributedLock
 *
 * @author mars
 * @create 2018-09-14 下午2:08
 */
public class ZookeeperDistributedLock {


    private ZooKeeper zk;

    private String root = "/locks";

    private String lockName;

    private ThreadLocal<String> nodeId = new ThreadLocal<>();

    private CountDownLatch connectedSignal = new CountDownLatch(1);

    private final static int sessionTimeout = 3000;

    private final static byte[] data = new byte[0];

    public ZookeeperDistributedLock(String config, String lockName) {
        this.lockName = lockName;
        try {
            zk = new ZooKeeper(config, sessionTimeout, new Watcher() {
                @Override
                public void process(WatchedEvent event) {
                    if (event.getState() == Event.KeeperState.SyncConnected) {
                        connectedSignal.countDown();
                    }
                }
            });
            connectedSignal.await();
            Stat stat = zk.exists(root, false);
            if (null == stat) {
                zk.create(root, data, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
    }

    class LockWatcher implements Watcher {
        private CountDownLatch latch = null;

        public LockWatcher(CountDownLatch latch) {
            this.latch = latch;
        }

        @Override
        public void process(WatchedEvent event) {
            if (event.getType() == Event.EventType.NodeDeleted) {
                latch.countDown();
            }
        }
    }

    public void lock() {
        try {
            String myNode = zk.create(root + "/" + lockName, data, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
            System.out.println("线程：" + Thread.currentThread().getId() + "创建节点：" + myNode);
            List<String> subNodes = zk.getChildren(root, false);
            TreeSet<String> sortedNodes = new TreeSet<>();
            for (String node : subNodes) {
                sortedNodes.add(root + "/" + node);
            }

            String smallNode = sortedNodes.first();
            String preNode = sortedNodes.lower(myNode);

            if (myNode.equals(smallNode)) {
                System.out.println("线程：" + Thread.currentThread().getId() + "节点：" + myNode + ",获取锁");
                this.nodeId.set(myNode);
                return;
            }

            CountDownLatch latch = new CountDownLatch(1);
            Stat stat = zk.exists(preNode, new LockWatcher(latch));
            if (stat != null) {
                latch.await();
                nodeId.set(myNode);
                System.out.println("线程：" + Thread.currentThread().getId() + "节点：" + myNode + ",获取锁");
                latch = null;
            }

        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void unlock() {
        System.out.println("线程：" + Thread.currentThread().getId() + "节点：" + nodeId.get() + ",释放锁");
        if (null != nodeId) {
            try {
                zk.delete(nodeId.get(), -1);
                nodeId.remove();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (KeeperException e) {
                e.printStackTrace();
            }
        }
    }
}