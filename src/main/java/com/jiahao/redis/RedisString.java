package com.jiahao.redis;

import redis.clients.jedis.Jedis;

/**
 * redis存储string
 */
public class RedisString {

    public static void main(String[] args) {
        Jedis jedis = new Jedis("127.0.0.1",6379);
        jedis.set("name","贾昊昊");    //存数据
        String name = jedis.get("name");    //取数据
        jedis.del("name");      //delete
        System.out.println(name);

    }
}
