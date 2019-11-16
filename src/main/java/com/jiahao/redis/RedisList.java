package com.jiahao.redis;

import redis.clients.jedis.Jedis;

import javax.swing.*;

/**
 * redis存储string
 */
public class RedisList {

    public static void main(String[] args) {
        Jedis jedis = new Jedis("127.0.0.1",6379);
        //add
        jedis.lpush("name","加厚啊哈");
        jedis.lpush("age","22");
        jedis.lpush("date","46");

        //delete
        jedis.lrem("name",0,"加厚啊哈");

        //findById
        jedis.lindex("age",1);

        //update
        jedis.lset("age",1,"22");

        //findAll
        jedis.lrange("age",0,-1);

    }
}
