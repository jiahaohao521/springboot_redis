package com.jiahao.cache;

import org.apache.ibatis.cache.Cache;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;

import javax.security.auth.callback.Callback;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MyCache implements Cache {

    private String id;

    private RedisTemplate redisTemplate;

    private ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    public MyCache(String id) throws Exception {
        if(id == null){
            throw new Exception("输入失败");
        }
        this.id = id;
        redisTemplate = ApplicationContextHolder.get("redisTemplate");
        System.out.println(id + ":" + redisTemplate + "!!!!!!!!!!!!!!!!!!!!!!!!");
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void putObject(Object o, Object o1) {
        readWriteLock.writeLock().lock();   //加锁
        //new Random().nextInt(10), TimeUnit.MINUTES：表示失效时间随机产生，单位为分钟
        try {
            redisTemplate.opsForValue().set(o,o1,new Random().nextInt(10), TimeUnit.MINUTES);
        }finally {
            readWriteLock.writeLock().unlock();     //解锁
        }
    }

    @Override
    public Object getObject(Object o) {
        //加读锁
        readWriteLock.readLock().lock();
        Object o1 = "";
        try {
           o1 = redisTemplate.opsForValue().get(o);
        }finally {
            readWriteLock.readLock().unlock();
        }
        return o1;
    }

    @Override
    public Object removeObject(Object o) {
        redisTemplate.opsForValue().set(o,null);    //如果查询数据库没有值，则放入redis，并设值为空，为了防止缓存穿透
        return null;
    }

    @Override
    public void clear() {
        /*
            之前insert update delete都会使二级缓存全部清除（只会清除该命名空间下的缓存，会引起脏读）
            redisTemplate.getConnectionFactory().getConnection().flushDb();(会导致redis缓存全部清除)
            该方法由mybatis进行管理，所以需要回调函数，让spring调取该方法
         */
        redisTemplate.execute(new RedisCallback(){

            @Override
            public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {
                redisConnection.flushDb();;
                return null;
            }
        });


    }

    //获取缓存的大小
    @Override
    public int getSize() {
        int size = (int)redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {
                return redisConnection.dbSize();
            }
        });
        return size;
    }
}
