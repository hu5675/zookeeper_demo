package com.zk;

import com.sun.org.apache.xml.internal.dtm.ref.sax2dtm.SAX2DTM2;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Collections;

/**
 * 描述:
 * RedisDistributedLock
 *
 * @author mars
 * @create 2018-09-14 下午3:05
 */
public class RedisDistributedLock {

    private static final String LOCK_SUCCESS = "OK";
    private static final String SET_IF_NOT_EXIST = "NX";
    private static final String SET_WITH_EXPIRE_TIME = "PX";
    private static final Long RELEASE_SUCCESS = 1L;

    private static void validParam(JedisPool jedisPool, String lockKey, String requestId, int expireTime) {
        if (null == jedisPool) {
            throw new IllegalArgumentException("jedisPool obj is null");
        }

        if (null == lockKey || "".equals(lockKey)) {
            throw new IllegalArgumentException("lock key is blank");
        }

        if (null == requestId && ".".equals(requestId)) {
            throw new IllegalArgumentException("requestId obj is null");
        }

        if (expireTime < 0) {
            throw new IllegalArgumentException("expireTime is not allowed less zero");
        }
    }

    public static boolean tryLock(JedisPool jedisPool, String lockKey, String requestId, int expireTime) {
        validParam(jedisPool, lockKey, requestId, expireTime);

        Jedis jedis;

        jedis = jedisPool.getResource();
        String result = jedis.set(lockKey, requestId, SET_IF_NOT_EXIST, SET_WITH_EXPIRE_TIME, expireTime);
        if (LOCK_SUCCESS.equals(result)) {
            return true;
        }
        return false;

    }

    public static void lock(JedisPool jedisPool, String lockKey, String requestId, int expireTime) {
        validParam(jedisPool, lockKey, requestId, expireTime);
        while (true) {
            if (tryLock(jedisPool, lockKey, requestId, expireTime)) {
                return;
            }
        }
    }

    public static boolean unlock(JedisPool jedisPool, String lockKey, String requestId, int expireTime) {
        validParam(jedisPool, lockKey, requestId, expireTime);
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";

        Jedis jedis = null;
        try {

            jedis = jedisPool.getResource();
            Object result = jedis.eval(script, Collections.singletonList(lockKey),
                    Collections.singletonList(requestId));

            if (RELEASE_SUCCESS.equals(result)) {
                return true;
            }

        } catch (Exception e) {
            throw e;
        } finally {
            if (null != jedis) {
                jedis.close();
            }
        }

        return false;
    }
}