package com.example.chapter7.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.*;
import org.springframework.data.redis.connection.RedisZSetCommands.Range;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import redis.clients.jedis.Jedis;

import java.util.*;

/**
 * @author DX
 * @version 1.0
 * @date 2021/5/6 17:25
 */
@Controller
@RequestMapping("/redis")
public class RedisController {

    @Autowired
    private RedisTemplate redisTemplate = null;

    @Autowired
    private StringRedisTemplate stringRedisTemplate = null;

    @RequestMapping("/stringAndHash")
    @ResponseBody
    public Map<String, Object> testStringAndHash() {
        redisTemplate.opsForValue().set("key1", "value1");
        redisTemplate.opsForValue().set("int_key", "1");
        stringRedisTemplate.opsForValue().set("int", "1");
        // 使用运算
        stringRedisTemplate.opsForValue().increment("int", 1);
        // 获取底层Jedis连接
        Jedis jedis = (Jedis) stringRedisTemplate.getConnectionFactory().getConnection().getNativeConnection();
        // 减1操作，这个命令RedisTemplate不支持，所以先获取底层的连接再操作
        jedis.decr("int");
        Map<String, String> hash = new HashMap<>();
        hash.put("field1", "value1");
        hash.put("field2", "value2");
        // 存入一个散列数据类型
        stringRedisTemplate.opsForHash().putAll("hash", hash);
        // 新增一个字段
        stringRedisTemplate.opsForHash().put("hash", "field3", "value3");
        // 绑定散列操作的key， 这样可以连续对同一个散列数据类型进行操作
        BoundHashOperations<String, Object, Object> hashOps = stringRedisTemplate.boundHashOps("hash");
        // 删除两个字段
        hashOps.delete("field1", "field2");
        // 新增一个字段
        hashOps.put("field4", "value5");
        Map<String, Object> map = new HashMap<>();
        map.put("success", true);
        return map;
    }

    @RequestMapping("/list")
    @ResponseBody
    public Map<String, Object> testList() {
        stringRedisTemplate.opsForList().leftPushAll("list1", "v2", "v4", "v6", "v8", "v10");
        stringRedisTemplate.opsForList().rightPushAll("list2", "v1", "v2", "v3", "v4", "v5", "v6");
        BoundListOperations listOps = stringRedisTemplate.boundListOps("lsit2");
        Object result1 = listOps.rightPop();
        Object result2 = listOps.index(1);
        listOps.leftPush("v0");
        Long size = listOps.size();
        List elements = listOps.range(0, size - 2);
        Map<String, Object> map = new HashMap<>();
        map.put("success", true);
        return map;
    }

    @RequestMapping("/set")
    @ResponseBody
    public Map<String, Object> testSet() {
        stringRedisTemplate.opsForSet().add("set1", "v1", "v1", "v2", "v3", "v4", "v5");
        stringRedisTemplate.opsForSet().add("set2", "v2", "v4", "v6", "v8");
        // 绑定set1集合操作
        BoundSetOperations<String, String> setOps = stringRedisTemplate.boundSetOps("set1");
        // 增加两个元素
        setOps.add("v6", "v7");
        // 删除两个元素
        setOps.remove("v1", "v7");
        // 返回所有元素
        Set set1 = setOps.members();
        // 求成员数
        Long size = setOps.size();
        // 求交集
        Set<String> inter = setOps.intersect("set2");
        // 求交集，并用新集合inter保存
        setOps.intersectAndStore("set2", "inter");
        // 求差集
        Set<String> diff = setOps.diff("set2");
        // 求差集，并且用新集合diff保存
        setOps.diffAndStore("set2", "diff");
        // 求并集
        Set<String> union = setOps.union("set2");
        // 求并集，并且用新集合union保存
        setOps.unionAndStore("set2", "union");
        Map<String, Object> map = new HashMap<>();
        map.put("success", true);
        return map;
    }

    @RequestMapping("/zset")
    @ResponseBody
    public Map<String, Object> testZset() {
        Set<TypedTuple<String>> typedTupleSet = new HashSet<>();
        for (int i = 1; i < 9; i++) {
            // 分数
            double score = i * 0.1;
            // 创建一个TypedTuple对象，存入值和分数
            TypedTuple<String> typedTuple = new DefaultTypedTuple<String>("value" + i, score);
            typedTupleSet.add(typedTuple);
        }
        // 往有序集合插入元素
        stringRedisTemplate.opsForZSet().add("zset1", typedTupleSet);
        // 绑定zset1有序集合操作
        BoundZSetOperations<String, String> zSetOps = stringRedisTemplate.boundZSetOps("zset1");
        // 增加一个元素
        zSetOps.add("value10", 0.26);
        Set<String> setRange = zSetOps.range(1, 6);
        // 按分数排序获取有序集合
        Set<String> setScore = zSetOps.rangeByScore(0.2, 0.6);
        // 定义值范围
        Range range = new Range();
        // 大于value3
        range.gt("value3");
        // 大于等于value3
        // range.gte("value3");
        // 小于value8
        // range.lt("value8");
        // 小于等于8
        range.lte("value8");
        // 按值排序，这个排序是按字符串排序
        Set<String> setLex = zSetOps.rangeByLex(range);
        // 删除元素
        zSetOps.remove("value9", "value2");
        // 求分数
        Double score = zSetOps.score("value8");
        // 在下标区间下，按分数排序，同时返回value和score
        Set<TypedTuple<String>> rangeSet = zSetOps.rangeWithScores(1, 6);
        // 在分数区间下，按分数排序，同时返回value和score
        Set<TypedTuple<String>> scoreSet = zSetOps.rangeByScoreWithScores(1, 6);
        // 按从大到小排序
        Set<String> reverseSet = zSetOps.reverseRange(2, 8);
        Map<String, Object> map = new HashMap<>();
        map.put("success", true);
        return map;
    }

    @RequestMapping("/multi")
    @ResponseBody
    public Map<String, Object> testMulti() {
        redisTemplate.opsForValue().set("key1", "value1");
        List list = (List)redisTemplate.execute((RedisOperations operations) -> {
            // 设置要监控的key1
            operations.watch("key1");
            // 开启事务，在exec命令执行前，全部都只是进入队列
            operations.multi();
            operations.opsForValue().set("key2", "value2");
            // operations.opsForValue().increment("key1", 1);
            // 获取值将为null，因为redis只是把命令放入队列
            Object value2 = operations.opsForValue().get("key2");
            System.out.println("命令在队列，所以value为null【" + value2 + "】");
            operations.opsForValue().set("key3", "value3");
            Object value3 = operations.opsForValue().get("key3");
            System.out.println("命令在队列，所以value为null【" + value3 + "】");
            // 执行exec命令，将先判别key1是否在监控后被修改过，如果是则不执行事务，否则就执行事务
            return operations.exec();
        });
        System.out.println(list);
        Map<String, Object> map = new HashMap<>();
        map.put("success", true);
        return map;
    }

    @RequestMapping("/pipeline")
    @ResponseBody
    public Map<String, Object> testPipeline() {
        long start = System.currentTimeMillis();
        List lsit = (List)redisTemplate.executePipelined((RedisOperations oprations) -> {
           for (int i=1; i<=100000; i++) {
               oprations.opsForValue().set("pipeline_" + i, "value_" + i);
               String value = (String) oprations.opsForValue().get("pipeline_" + i);
               if (i == 100000) {
                   System.out.println("命令只是进入队列，所以值为空【" + value + "】");
               }
           }
           return null;
        });
        Long end = System.currentTimeMillis();
        System.out.println("耗时：" + (end - start) + "毫秒。");
        Map<String, Object> map = new HashMap<>();
        map.put("success", true);
        return map;
    }

    @RequestMapping("/lua")
    @ResponseBody
    public Map<String, Object> testLua() {
        DefaultRedisScript<String> stringDefaultRedisScript = new DefaultRedisScript<>();
        // 设置脚本
        stringDefaultRedisScript.setScriptText("return'Hello Redis'");
        // 定义返回类型。注意：如果没有这个定义，Spring不会返回结果
        stringDefaultRedisScript.setResultType(String.class);
        RedisSerializer stringSerializer = redisTemplate.getStringSerializer();
        // 执行Lua脚本
        String str = (String) redisTemplate.execute(stringDefaultRedisScript, stringSerializer, stringSerializer, null);
        Map<String, Object> map = new HashMap<>();
        map.put("str", str);
        return map;
    }

    @RequestMapping("/lua2")
    @ResponseBody
    public Map<String, Object> testLua2(String key1, String key2, String value1, String value2) {
        // 定义Lua脚本
        String lua = "redis.call('set', KEYS[1],ARGV[1]) \n"
                + "redis.call('set', KEYS[2],ARGV[2]) \n"
                + "local str1 = redis.call('get', KEYS[1]) \n"
                + "local str2 = redis.call('get', KEYS[2]) \n"
                + "if str1 == str2 then \n"
                + "return 1 \n"
                + "end \n"
                + "return 0 \n";
        System.out.println(lua);
        // 结果返回为Long
        DefaultRedisScript<Long> longDefaultRedisScript = new DefaultRedisScript<>();
        longDefaultRedisScript.setScriptText(lua);
        longDefaultRedisScript.setResultType(Long.class);
        // 采用字符串序列化器
        RedisSerializer stringSerializer = redisTemplate.getStringSerializer();
        // 定义key参数
        List<String> keyList = new ArrayList<>();
        keyList.add(key1);
        keyList.add(key2);
        // 传递两个参数值，第一个序列化器是key的序列化器，第二个序列化器是参数的序列化器
        Long result = (Long) redisTemplate.execute(longDefaultRedisScript, stringSerializer, stringSerializer, keyList, value1, value2);
        Map<String, Object> map = new HashMap<>();
        map.put("result", result);
        return map;
    }

}
