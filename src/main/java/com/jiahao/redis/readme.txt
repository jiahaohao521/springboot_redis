NOSQL(Not Only SQL) 非关系型的数据库
	Redis: 热卖产品  Key Value
	MangoDB: 文档 商品的描述信息 text bigtext blob clob
	hadoop:HDFS: 图片 文件
	Neo4J:地理信息 GPS
=======================================
以前缓存的问题：
	必须得完整的搞清楚缓存的机制还有数据的缓存的可能性和范围。

以前的脏读问题：
	<sql namespace="com.woniu.mapper.UserinfoMapper">
	<cache>
	Mybatis的二级缓存对NameSpace命名空间而言.当维护这个命名空间下的任意数据的时候，
	就会导致当前整个NameSpace下的所有的缓存的失效。

	AS:
		UserinfoMappper
			mapper.insert();
			mapper.delete();
			mapper.update();
		会导致当前整个NameSpace下的二级缓存全部失效。

	就因为这个特性会造成数据的脏读：
		关联结果
		关联嵌套
			总之都会查询出相关表。 查用户的时候===>自动查询了车表。
			线程1：查A的 A(B). 他的命名空间都是A。
			线程2：修改B。会导致B的命名空间下的所有缓存失效。重新查询数据库。
			线程1：此时A里面包含的B,则是脏读数据。

缓存穿透：
	缓存中没有，数据库也没有。这个时候对这个数据来做查询。
	比如 /findByid?uid=-1
	每次首先都会查询缓存，没有命中，每次都会查询数据库。
	每次都会进行数据库的IO。如果这个请求的数量过大。有可能导致数据库坍塌。
	解决方案：
		1：简单粗暴  从数据库查不到值。把查不到的值放入缓存中。
		2：布隆过滤器


缓存击穿：
	缓存中没有，但是数据库有。
	<cache flushInterval="5000" ev>
	有一个条数据 Userinfo(id=3)数据，缓存有超时时间
	size="5000" flushInterval="5000" eviction="LRU" readOnly="false",
	有一种可能性：
		在超时的瞬间。有个线程会同时查询二级缓存，均没有命中，
		全部查询数据库，有可能导致IO堵塞。

	解决方案：
		1：设置数据永不过期。
		2：如果发现数据没有在缓存中，而要进行数据库查询的时候，进行同步操作。

缓存雪崩：
	<cache flushInterval="5000">
	有可能哟好多张表，超期时间都是一致的。有可能在同一个事件，整个项目的大部分缓存，
	全部失效。全部项目都有可能重新查询数据库，导致IO堵塞，系统宕机。
	解决方案：
		1：设置超时时间不一致的，一定范围内的随机数。












=======================================
Redis:
	基于Key Value的非关系型数据库。默认基于内存，并可以做持久化的NOSQL。
	以前，无法面对大数据量，高并发条件下的数据IO.
	数据库每秒无法需要完成数据库的10W次读写。
	Redis:  11W读  8W写。
	Redis是单线程的。


一：Redis的基础使用
	redis有5种数据类型：
		String
		List
		Set
		ZSet
		Hash

	Redis默认有16个数据库。默认使用第一个。

========================
		Jedis jedis = new Jedis("127.0.0.1", 6379);
		System.out.println(jedis.ping());
========================
String类型的CRUD了。
Jedis jedis = new Jedis("127.0.0.1", 6379);
		//insert
		jedis.set("stname", "panfeng");
		//find
		System.out.println(jedis.get("stname"));
		//update
		jedis.set("stname", "gaopeng");

		//delete
		//jedis.del("stname");
		System.out.println(jedis.get("stname"));

		jedis.expire("stname", 3);

		Thread.sleep(3000);

		System.out.println(jedis.get("stname"));

		//find
		System.out.println(jedis.get("stname"));
========================
List的CRUD
		Jedis jedis = new Jedis("127.0.0.1", 6379);
		jedis.flushDB();//清空当前库
		jedis.flushAll();//清空所有库

		//add
		jedis.rpush("persons", "高鹏");
		jedis.rpush("persons", "潘峰");
		jedis.rpush("persons", "王斌");
		jedis.rpush("persons", "张涛");

		//delete
		jedis.lrem("persons", 0, "王斌");

		//findById
		System.out.println(jedis.lindex("persons", 2));;

		//update
		jedis.lset("persons", 2, "潘凤");//

		//list
		//System.out.println(jedis.lrange("persons", 0, 2));
		//System.out.println(jedis.lrange("persons", 0, jedis.llen("persons")));
		System.out.println(jedis.lrange("persons", 0, -1));
========================
SEt
	Jedis jedis = new Jedis("127.0.0.1", 6379);
		jedis.flushDB();//清空当前库
		jedis.flushAll();//清空所有库

		//add
		jedis.sadd("persons", "潘凤");
		jedis.sadd("persons", "潘小凤");
		jedis.sadd("persons", "潘大凤");
		jedis.sadd("persons", "潘二凤");

		//delete
		//jedis.srem("persons", "潘大凤");

		//findAll
		System.out.println(jedis.smembers("persons"));

//		//
//		System.out.println(jedis.spop("persons"));
//
//		System.out.println(jedis.smembers("persons"));

		System.out.println(jedis.scard("persons"));
========================
Zset
	Jedis jedis = new Jedis("127.0.0.1", 6379);
		jedis.flushDB();//清空当前库
		jedis.flushAll();//清空所有库

		//add
		jedis.zadd("persons", 160, "高鹏");
		jedis.zadd("persons", 190, "潘凤");
		jedis.zadd("persons", 150, "王斌");
		jedis.zadd("persons", 185, "陈籍");

		//delete
		jedis.zrem("persons", "高鹏");

		//findAll
		System.out.println(jedis.zrange("persons", 0, -1));

========================
Jedis jedis = new Jedis("127.0.0.1", 6379);
		jedis.flushDB();//清空当前库
		jedis.flushAll();//清空所有库
		//add
		jedis.hset("person", "name","潘凤");
		jedis.hset("person", "sex","randome");
		jedis.hset("person", "age","耄耋");
		jedis.hset("person", "wife","翠花");

		jedis.hdel("person", "name");


		System.out.println(jedis.hget("person", "name"));

		System.out.println(jedis.hkeys("person"));

		System.out.println(jedis.hvals("person"));
========================
SpringBOOT和Redis的整合：

========================
========================
========================
应用1：
	短信验证手机：

	文本框：
		手机号：文本框    发送
		验证码：文本框

		点下发送：Ajax或者Java发送验证码到用户手机中去了。
			验证码存服务器的哪里。

		method1:
			static Map<TEL,Object[验证码和时间]>
			添加数据

			验证验证码的时候：
				客户端： 手机号  用户输入的验证码
				====》服务器的Map中找Key(手机号)
					先比对时间，看是否超期了，然后再比对验证码。

			Map里面的数据会越来越多。这个问题怎么解决？

			应该用定时器来做：
				Javad的Timer来做
				Spring Quartz来做

		method2:
			你的验证码直接放在Redis里面。
			String:  key(Tel) value(验证码)
				expire:60秒

			验证验证码的时候：
				客户端： 手机号  用户输入的验证码
				====》服务器的Map中找Key(手机号)
					然后再比对验证码,看对不对。
			
========================
