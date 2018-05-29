package com.zk;

import org.apache.zookeeper.KeeperException;

import java.util.List;

public class ListGroup extends ConnectionWatcher {

    public void list(String groupNmae) throws KeeperException, InterruptedException {
        String path = "/" + groupNmae;
        try {
            List<String> children = zk.getChildren(path, false);
            if (children.isEmpty()) {
                System.out.printf("No memebers in group %s\n", groupNmae);
                System.exit(1);
            }
            for (String child : children) {
                System.out.println(child);
            }
        } catch (KeeperException.NoNodeException e) {
            System.out.printf("Group %s does not exist \n", groupNmae);
            System.exit(1);
        }
    }
}
