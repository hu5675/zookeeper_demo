package com.zk;

import org.apache.zookeeper.KeeperException;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
        String hosts = "localhost:2181";
        //创建组
//        CreateGroup createGroup = new CreateGroup();
////        createGroup.connect(hosts);
////        createGroup.create("mars");
////        createGroup.close();

        //加入组
//        JoinGroup joinGroup = new JoinGroup();
//        joinGroup.connect(hosts);
//        joinGroup.join("mars","bboymars");
//        joinGroup.close();

//        Thread.sleep(Long.MAX_VALUE);

        //列出组成员
//        ListGroup listGroup = new ListGroup();
//        listGroup.connect(hosts);
//        listGroup.list("");
//        listGroup.close();

        //删除组成员
//        DeleteGroup deleteGroup = new DeleteGroup();
//        deleteGroup.connect(hosts);
//        deleteGroup.delete("mars");
//        deleteGroup.close();


//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                ConfigUpdater configUpdater = null;
//                try {
//                    configUpdater = new ConfigUpdater(hosts);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                try {
//                    configUpdater.run();
//                } catch (KeeperException e) {
//                    e.printStackTrace();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
//
//
//        ConfigWatcher configWatcher = new ConfigWatcher(hosts);
//        configWatcher.displayConfig();
//
//        Thread.sleep(Long.MAX_VALUE);


//        Runnable task1 = new Runnable() {
//            public void run() {
//                DistributedLock lock = null;
//                try {
//                    lock = new DistributedLock(hosts, "test1");
//                    //lock = new DistributedLock(hosts,"test2");
//                    lock.lock();
//                    Thread.sleep(3000);
//                    System.out.println("===Thread " + Thread.currentThread().getId() + " running");
//                } catch (Exception e) {
//                    e.printStackTrace();
//                } finally {
//                    if (lock != null)
//                        lock.unlock();
//                }
//
//            }
//
//        };
//        new Thread(task1).start();
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e1) {
//            e1.printStackTrace();
//        }

        ConcurrentTest.ConcurrentTask[] tasks = new ConcurrentTest.ConcurrentTask[60];
        for (int i = 0; i < tasks.length; i++) {
            ConcurrentTest.ConcurrentTask task3 = new ConcurrentTest.ConcurrentTask() {
                public void run() {
                    ZookeeperDistributedLock lock = null;
                    try {
                        lock = new ZookeeperDistributedLock(hosts, "test2");
                        lock.lock();
                        System.out.println("Thread " + Thread.currentThread().getId() + " running");
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        lock.unlock();
                    }

                }
            };
            tasks[i] = task3;
        }
        new ConcurrentTest(tasks);
    }
}
