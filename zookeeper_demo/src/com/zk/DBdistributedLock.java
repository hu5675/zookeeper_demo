//package com.zk;
//
///**
// * 描述:
// * DBdistributedLock
// *
// * @author mars
// * @create 2018-09-14 下午3:29
// */
//public class DBdistributedLock {
//
//    private DataSource dataSource;
//
//    private static final String cmd = "select * from lock where uid = 1 for update";
//
//    public DBdistributedLock(DataSource ds) {
//        this.dataSource = ds;
//    }
//
//    public static interface CallBack {
//        public void doAction();
//    }
//
//    public void lock(CallBack callBack) {
//        Connection conn = null;
//        PreparedStatement stmt = null;
//        ResultSet rs = null;
//
//        try {
//            //try get lock
//            System.out.println(Thread.currentThread().getName() + " begin try lock");
//            conn = dataSource.getConnection();
//            conn.setAutoCommit(false);
//            stmt = conn.prepareStatement(cmd);
//            rs = stmt.executeQuery();
//
//            //do business thing
//            callBack.doAction();
//
//            //release lock
//            conn.commit();
//            System.out.println(Thread.currentThread().getName() + " release lock");
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//
//        } finally {
//
//            if (null != conn) {
//                try {
//                    conn.close();
//                } catch (SQLException e) {
//                    e.printStackTrace();
//                }
//            }
//
//        }
//    }
//}