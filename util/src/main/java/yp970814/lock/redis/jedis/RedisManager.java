package yp970814.lock.redis.jedis;

import redis.clients.jedis.BinaryClient;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.SortingParams;
import redis.clients.util.SafeEncoder;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Author yuanping970814@163.com
 * @Date 2023-10-01 16:22
 */
public class RedisManager {

    private static RedisContext redisContext = null;

    public static synchronized void init(RedisContext rc) {
        redisContext = rc;
    }

    private RedisManager() {
    }

    /**
     * 操作Key的方法
     */
    public static Keys KEYS = new Keys();
    /**
     * 对存储结构为String类型的操作
     */
    public static Strings STRINGS = new Strings();
    /**
     * 对存储结构为List类型的操作
     */
    public static Lists LISTS = new Lists();
    /**
     * 对存储结构为Set类型的操作
     */
    public static Sets SETS = new Sets();
    /**
     * 对存储结构为HashMap类型的操作
     */
    public static Hash HASH = new Hash();
    /**
     * 对存储结构为Set(排序的)类型的操作
     */
    public static SortSet SORTSET = new SortSet();

    /**
     * 设置过期时间
     *
     * @param key
     * @param seconds
     * @author ruan 2013-4-11
     */
    public void expire(String key, int seconds) {
        if (seconds <= 0) {
            return;
        }
        try (Jedis jedis = redisContext.getJedis()) {
            jedis.expire(key, seconds);
        }

    }

    public void expire(String key, int seconds, int DBindex) {
        if (seconds <= 0) {
            return;
        }
        try (Jedis jedis = redisContext.getJedis(DBindex)) {
            jedis.expire(key, seconds);
        }
    }

    /**
     * 设置默认过期时间
     *
     * @param key
     * @author ruan 2013-4-11
     */
    public void expire(String key) {
        expire(key, redisContext.getExpired());
    }


    //*******************************************Keys*******************************************//
    public static class Keys {

        /**
         * 清空所有key
         */
        public String flushAll() {
            try (Jedis jedis = redisContext.getJedis()) {
                String stata = jedis.flushAll();
                return stata;
            }
        }

        /**
         * 更改key
         *
         * @param oldkey
         * @param newkey
         * @return 状态码
         */
        public String rename(String oldkey, String newkey) {
            return rename(SafeEncoder.encode(oldkey),
                    SafeEncoder.encode(newkey));
        }

        /**
         * 更改key,仅当新key不存在时才执行
         *
         * @param oldkey
         * @param newkey
         * @return 状态码
         */
        public long renamenx(String oldkey, String newkey) {
            try (Jedis jedis = redisContext.getJedis()) {
                long status = jedis.renamenx(oldkey, newkey);
                return status;
            }

        }

        /**
         * 更改key
         *
         * @param oldkey
         * @param newkey
         * @return 状态码
         */
        public String rename(byte[] oldkey, byte[] newkey) {
            try (Jedis jedis = redisContext.getJedis()) {
                String status = jedis.rename(oldkey, newkey);
                return status;
            }

        }

        /**
         * 设置key的过期时间，以秒为单位
         *
         * @param key
         * @param seconds 时间  ,已秒为单位
         * @return 影响的记录数
         */
        public long expired(String key, int seconds) {
            try (Jedis jedis = redisContext.getJedis()) {
                long count = jedis.expire(key, seconds);
                return count;
            }
        }

        public long expired(String key, int seconds, int DBindex) {
            try (Jedis jedis = redisContext.getJedis(DBindex)) {
                long count = jedis.expire(key, seconds);
                return count;
            }
        }

        /**
         * 设置key的过期时间,它是距历元（即格林威治标准时间 1970 年 1 月 1 日的 00:00:00，格里高利历）的偏移量。
         *
         * @param key
         * @param timestamp 时间  ,已秒为单位
         * @return 影响的记录数
         */
        public long expireAt(String key, long timestamp) {
            try (Jedis jedis = redisContext.getJedis();) {
                long count = jedis.expireAt(key, timestamp);
                return count;
            }
        }

        /**
         * 查询key的过期时间
         *
         * @param key
         * @return 以秒为单位的时间表示
         */
        public long ttl(String key) {
            try (Jedis jedis = redisContext.getJedis()) {
                long len = jedis.ttl(key);
                return len;
            }

        }

        /**
         * 取消对key过期时间的设置
         *
         * @param key
         * @return 影响的记录数
         */
        public long persist(String key) {
            try (Jedis jedis = redisContext.getJedis()) {
                long count = jedis.persist(key);
                return count;
            }

        }

        /**
         * 删除keys对应的记录,可以是多个key
         *
         * @param keys
         * @return 删除的记录数
         */
        public long del(String... keys) {
            try (Jedis jedis = redisContext.getJedis()) {
                long count = jedis.del(keys);
                return count;
            }
        }

        public long del(int DBindex, String... keys) {
            try (Jedis jedis = redisContext.getJedis(DBindex)) {
                long count = jedis.del(keys);
                return count;
            }
        }

        /**
         * 删除keys对应的记录,可以是多个key
         *
         * @param keys
         * @return 删除的记录数
         */
        public long del(byte[]... keys) {
            try (Jedis jedis = redisContext.getJedis()) {
                long count = jedis.del(keys);
                return count;
            }

        }

        /**
         * 判断key是否存在
         *
         * @param key
         * @return boolean
         */
        public boolean exists(String key) {
            try (Jedis jedis = redisContext.getJedis()) {
                boolean existed = jedis.exists(key);
                return existed;
            }
        }

        public boolean exists(String key, int DBindex) {
            try (Jedis jedis = redisContext.getJedis(DBindex)) {
                boolean exis = jedis.exists(key);
                return exis;
            }
        }

        /**
         * 对List,Set,SortSet进行排序,如果集合数据较大应避免使用这个方法
         *
         * @param key
         * @return List<String> 集合的全部记录
         **/
        public List<String> sort(String key) {
            try (Jedis jedis = redisContext.getJedis()) {
                List<String> list = jedis.sort(key);
                return list;
            }
        }

        /**
         * 对List,Set,SortSet进行排序或limit
         *
         * @param key
         * @param parame 定义排序类型或limit的起止位置.
         * @return List<String> 全部或部分记录
         **/
        public List<String> sort(String key, SortingParams parame) {
            try (Jedis jedis = redisContext.getJedis()) {
                List<String> list = jedis.sort(key, parame);
                return list;
            }

        }

        /**
         * 返回指定key存储的类型
         *
         * @param key
         * @return String string|list|set|zset|hash
         **/
        public String type(String key) {
            try (Jedis jedis = redisContext.getJedis()) {
                String type = jedis.type(key);
                return type;
            }
        }

        /**
         * 查找所有匹配给定的模式的键
         *
         * @param
         */
        public Set<String> keys(String pattern) {
            try (Jedis jedis = redisContext.getJedis()) {
                Set<String> set = jedis.keys(pattern);
                return set;
            }

        }
    }

    //*******************************************Sets*******************************************//
    public static class Sets {

        /**
         * 向Set添加一条记录，如果member已存在返回0,否则返回1
         *
         * @param key
         * @param member
         * @return 操作码, 0或1
         */
        public long sadd(String key, String member) {
            try (Jedis jedis = redisContext.getJedis()) {
                return jedis.sadd(key, member);
            }

        }


        public long sadd(String key, String... members) {
            try (Jedis jedis = redisContext.getJedis()) {
                return jedis.sadd(key, members);
            }

        }

        public long sadd(String key, String member, int DBindex) {
            try (Jedis jedis = redisContext.getJedis(DBindex)) {
                return jedis.sadd(key, member);
            }
        }

        public long sadd(byte[] key, byte[] member) {
            try (Jedis jedis = redisContext.getJedis()) {
                return jedis.sadd(key, member);
            }
        }

        public long sadd(byte[] key, byte[] member, int DBindex) {
            try (Jedis jedis = redisContext.getJedis(DBindex)) {
                return jedis.sadd(key, member);
            }

        }

        /**
         * 获取给定key中元素个数
         *
         * @param key
         * @return 元素个数
         */
        public long scard(String key) {
            try (Jedis jedis = redisContext.getJedis()) {
                return jedis.scard(key);
            }
        }

        public long scard(String key, int DBindex) {
            try (Jedis jedis = redisContext.getJedis(DBindex)) {
                return jedis.scard(key);
            }
        }

        /**
         * 返回从第一组和所有的给定集合之间的差异的成员
         *
         * @param keys
         * @return 差异的成员集合
         */
        public Set<String> sdiff(String... keys) {
            try (Jedis jedis = redisContext.getJedis()) {
                return jedis.sdiff(keys);
            }
        }

        public Set<String> sdiff(int DBindex, String... keys) {
            try (Jedis jedis = redisContext.getJedis(DBindex)) {
                return jedis.sdiff(keys);
            }
        }

        /**
         * 这个命令等于sdiff,但返回的不是结果集,而是将结果集存储在新的集合中，如果目标已存在，则覆盖。
         *
         * @param newkey 新结果集的key
         * @param keys   比较的集合
         * @return 新集合中的记录数
         **/
        public long sdiffstore(int DBindex, String newkey, String... keys) {
            try (Jedis jedis = redisContext.getJedis(DBindex)) {
                return jedis.sdiffstore(newkey, keys);
            }

        }

        public long sdiffstore(String newkey, String... keys) {
            try (Jedis jedis = redisContext.getJedis()) {
                return jedis.sdiffstore(newkey, keys);
            }
        }

        /**
         * 返回给定集合交集的成员,如果其中一个集合为不存在或为空，则返回空Set
         *
         * @param keys
         * @return 交集成员的集合
         **/
        public Set<String> sinter(String... keys) {
            try (Jedis jedis = redisContext.getJedis()) {
                return jedis.sinter(keys);
            }
        }

        public Set<String> sinter(int DBindex, String... keys) {
            try (Jedis jedis = redisContext.getJedis(DBindex)) {
                return jedis.sinter(keys);
            }

        }

        /**
         * 这个命令等于sinter,但返回的不是结果集,而是将结果集存储在新的集合中，如果目标已存在，则覆盖。
         *
         * @param newkey 新结果集的key
         * @param keys   比较的集合
         * @return 新集合中的记录数
         **/
        public long sinterstore(String newkey, String... keys) {
            try (Jedis jedis = redisContext.getJedis()) {
                return jedis.sinterstore(newkey, keys);
            }
        }

        public long sinterstore(int DBindex, String newkey, String... keys) {
            try (Jedis jedis = redisContext.getJedis(DBindex)) {
                return jedis.sinterstore(newkey, keys);
            }
        }

        /**
         * 确定一个给定的值是否存在
         *
         * @param key
         * @param member 要判断的值
         * @return 存在返回1，不存在返回0
         **/
        public boolean sismember(String key, String member) {
            try (Jedis jedis = redisContext.getJedis()) {
                return jedis.sismember(key, member);
            }

        }

        public boolean sismember(int DBindex, String key, String member) {
            try (Jedis jedis = redisContext.getJedis(DBindex)) {
                return jedis.sismember(key, member);
            }
        }

        /**
         * 返回集合中的所有成员
         *
         * @param key
         * @return 成员集合
         */
        public Set<String> smembers(String key) {
            try (Jedis jedis = redisContext.getJedis()) {
                return jedis.smembers(key);
            }
        }

        public Set<String> smembers(String key, int DBindex) {
            try (Jedis jedis = redisContext.getJedis(DBindex)) {
                return jedis.smembers(key);
            }

        }

        public Set<byte[]> smembers(byte[] key) {
            try (Jedis jedis = redisContext.getJedis()) {
                return jedis.smembers(key);
            }
        }

        public Set<byte[]> smembers(byte[] key, int DBindex) {
            try (Jedis jedis = redisContext.getJedis(DBindex)) {
                return jedis.smembers(key);
            }
        }

        /**
         * 将成员从源集合移出放入目标集合 <br/>
         * 如果源集合不存在或不包哈指定成员，不进行任何操作，返回0<br/>
         * 否则该成员从源集合上删除，并添加到目标集合，如果目标集合中成员已存在，则只在源集合进行删除
         *
         * @param srckey 源集合
         * @param dstkey 目标集合
         * @param member 源集合中的成员
         * @return 状态码，1成功，0失败
         */
        public long smove(String srckey, String dstkey, String member) {
            try (Jedis jedis = redisContext.getJedis()) {
                return jedis.smove(srckey, dstkey, member);
            }
        }

        public long smove(String srckey, String dstkey, String member, int DBindex) {
            try (Jedis jedis = redisContext.getJedis(DBindex)) {
                return jedis.smove(srckey, dstkey, member);
            }
        }

        /**
         * 从集合中删除成员
         *
         * @param key
         * @return 被删除的成员
         */
        public String spop(String key) {
            try (Jedis jedis = redisContext.getJedis()) {
                return jedis.spop(key);
            }
        }

        public String spop(String key, int DBindex) {
            try (Jedis jedis = redisContext.getJedis(DBindex)) {
                return jedis.spop(key);
            }
        }

        /**
         * 从集合中删除指定成员
         *
         * @param key
         * @param member 要删除的成员
         * @return 状态码，成功返回1，成员不存在返回0
         */
        public long srem(String key, String member) {
            try (Jedis jedis = redisContext.getJedis()) {
                return jedis.srem(key, member);
            }
        }

        public long srem(String key, String member, int DBindex) {
            try (Jedis jedis = redisContext.getJedis(DBindex)) {
                return jedis.srem(key, member);
            }

        }

        /**
         * 合并多个集合并返回合并后的结果，合并后的结果集合并不保存<br/>
         *
         * @param keys
         * @return 合并后的结果集合
         */
        public Set<String> sunion(String... keys) {
            try (Jedis jedis = redisContext.getJedis()) {
                return jedis.sunion(keys);
            }

        }

        public Set<String> sunion(int DBindex, String... keys) {
            try (Jedis jedis = redisContext.getJedis(DBindex)) {
                return jedis.sunion(keys);
            }
        }

        /**
         * 合并多个集合并将合并后的结果集保存在指定的新集合中，如果新集合已经存在则覆盖
         *
         * @param newkey 新集合的key
         * @param keys   要合并的集合
         **/
        public long sunionstore(String newkey, String... keys) {
            try (Jedis jedis = redisContext.getJedis()) {
                return jedis.sunionstore(newkey, keys);
            }
        }

        public long sunionstore(int DBindex, String newkey, String... keys) {
            try (Jedis jedis = redisContext.getJedis(DBindex)) {
                return jedis.sunionstore(newkey, keys);
            }

        }
    }

    //*******************************************SortSet*******************************************//
    public static class SortSet {

        /**
         * 向集合中增加一条记录,如果这个值已存在，这个值对应的权重将被置为新的权重
         *
         * @param key
         * @param score  权重
         * @param member 要加入的值，
         * @return 状态码 1成功，0已存在member的值
         */
        public long zadd(String key, double score, String member) {
            try (Jedis jedis = redisContext.getJedis()) {
                return jedis.zadd(key, score, member);
            }
        }

        public long zadd(String key, Map<String, Double> scoreMembers) {
            try (Jedis jedis = redisContext.getJedis()) {
                return jedis.zadd(key, scoreMembers);
            }
        }

        /**
         * 获取集合中元素的数量
         *
         * @param key
         * @return 如果返回0则集合不存在
         */
        public long zcard(String key) {
            try (Jedis jedis = redisContext.getJedis()) {
                return jedis.zcard(key);
            }
        }

        /**
         * 获取指定权重区间内集合的数量
         *
         * @param key
         * @param min 最小排序位置
         * @param max 最大排序位置
         */
        public long zcount(String key, double min, double max) {
            try (Jedis jedis = redisContext.getJedis()) {
                return jedis.zcount(key, min, max);
            }
        }

        /**
         * 获得set的长度
         *
         * @param key
         * @return
         */
        public long zlength(String key) {
            long len = 0;
            Set<String> set = zrange(key, 0, -1);
            len = set.size();
            return len;
        }

        /**
         * 权重增加给定值，如果给定的member已存在
         *
         * @param key
         * @param score  要增的权重
         * @param member 要插入的值
         * @return 增后的权重
         */
        public double zincrby(String key, double score, String member) {
            try (Jedis jedis = redisContext.getJedis()) {
                return jedis.zincrby(key, score, member);
            }

        }

        /**
         * 返回指定位置的集合元素,0为第一个元素，-1为最后一个元素
         *
         * @param key
         * @param start 开始位置(包含)
         * @param end   结束位置(包含)
         * @return Set<String>
         */
        public Set<String> zrange(String key, int start, int end) {
            try (Jedis jedis = redisContext.getJedis()) {
                return jedis.zrange(key, start, end);
            }

        }

        /**
         * 返回指定权重区间的元素集合
         *
         * @param key
         * @param min 上限权重
         * @param max 下限权重
         * @return Set<String>
         */
        public Set<String> zrangeByScore(String key, double min, double max) {
            try (Jedis jedis = redisContext.getJedis()) {
                return jedis.zrangeByScore(key, min, max);
            }
        }

        /**
         * 获取指定值在集合中的位置，集合排序从低到高
         *
         * @param key
         * @param member
         * @param
         * @return long 位置
         */
        public long zrank(String key, String member) {
            try (Jedis jedis = redisContext.getJedis()) {
                return jedis.zrank(key, member);
            }
        }

        /**
         * 获取指定值在集合中的位置，集合排序从高到低
         *
         * @param key
         * @param member
         * @return long 位置
         * @see
         */
        public long zrevrank(String key, String member) {
            try (Jedis jedis = redisContext.getJedis()) {
                return jedis.zrevrank(key, member);
            }
        }

        /**
         * 从集合中删除成员
         *
         * @param key
         * @param member
         * @return 返回1成功
         */
        public long zrem(String key, String member) {
            try (Jedis jedis = redisContext.getJedis()) {
                return jedis.zrem(key, member);
            }
        }

        /**
         * 删除
         *
         * @param key
         * @return
         */
        public long zrem(String key) {
            try (Jedis jedis = redisContext.getJedis()) {
                return jedis.del(key);
            }
        }

        /**
         * 删除给定位置区间的元素
         *
         * @param key
         * @param start 开始区间，从0开始(包含)
         * @param end   结束区间,-1为最后一个元素(包含)
         * @return 删除的数量
         */
        public long zremrangeByRank(String key, int start, int end) {
            try (Jedis jedis = redisContext.getJedis()) {
                return jedis.zremrangeByRank(key, start, end);
            }
        }

        /**
         * 删除给定权重区间的元素
         *
         * @param key
         * @param min 下限权重(包含)
         * @param max 上限权重(包含)
         * @return 删除的数量
         */
        public long zremrangeByScore(String key, double min, double max) {
            try (Jedis jedis = redisContext.getJedis()) {
                return jedis.zremrangeByScore(key, min, max);
            }
        }

        /**
         * 获取给定区间的元素，原始按照权重由高到低排序
         *
         * @param key
         * @param start
         * @param end
         * @return Set<String>
         */
        public Set<String> zrevrange(String key, int start, int end) {
            try (Jedis jedis = redisContext.getJedis()) {
                return jedis.zrevrange(key, start, end);
            }
        }

        /**
         * 获取给定值在集合中的权重
         *
         * @param key
         * @param
         * @return double 权重
         */
        public double zscore(String key, String memebr) {
            try (Jedis jedis = redisContext.getJedis()) {
                Double score = jedis.zscore(key, memebr);
                if (score != null)
                    return score;
                return 0;
            }

        }
    }

    //*******************************************Hash*******************************************//
    public static class Hash {

        /**
         * 从hash中删除指定的存储
         *
         * @param key
         * @param fieid 存储的名字
         * @return 状态码，1成功，0失败
         */
        public long hdel(String key, String fieid) {
            try (Jedis jedis = redisContext.getJedis()) {
                return jedis.hdel(key, fieid);
            }

        }

        public long hdel(String key) {
            try (Jedis jedis = redisContext.getJedis()) {
                return jedis.del(key);
            }

        }

        /**
         * 测试hash中指定的存储是否存在
         *
         * @param key
         * @param fieid 存储的名字
         * @return 1存在，0不存在
         */
        public boolean hexists(String key, String fieid) {
            try (Jedis jedis = redisContext.getJedis()) {
                return jedis.hexists(key, fieid);
            }
        }

        /**
         * 返回hash中指定存储位置的值
         *
         * @param key
         * @param fieid 存储的名字
         * @return 存储对应的值
         */
        public String hget(String key, String fieid) {
            try (Jedis jedis = redisContext.getJedis()) {
                return jedis.hget(key, fieid);
            }
        }

        public byte[] hget(byte[] key, byte[] fieid) {
            try (Jedis jedis = redisContext.getJedis()) {
                return jedis.hget(key, fieid);
            }
        }

        /**
         * 以Map的形式返回hash中的存储和值
         *
         * @param key
         * @return Map<Strinig, String>
         */
        public Map<String, String> hgetAll(String key) {
            try (Jedis jedis = redisContext.getJedis()) {
                Map<String, String> map = jedis.hgetAll(key);
                return map;
            }
        }

        /**
         * 添加一个对应关系
         *
         * @param key
         * @param fieid
         * @param value
         * @return 状态码 1成功，0失败，fieid已存在将更新，也返回0
         **/
        public long hset(String key, String fieid, String value) {
            try (Jedis jedis = redisContext.getJedis()) {
                return jedis.hset(key, fieid, value);
            }
        }

        public long hset(String key, String fieid, byte[] value) {
            try (Jedis jedis = redisContext.getJedis()) {
                return jedis.hset(key.getBytes(), fieid.getBytes(), value);
            }
        }

        /**
         * 添加对应关系，只有在fieid不存在时才执行
         *
         * @param key
         * @param fieid
         * @param value
         * @return 状态码 1成功，0失败fieid已存
         **/
        public long hsetnx(String key, String fieid, String value) {
            try (Jedis jedis = redisContext.getJedis()) {
                return jedis.hsetnx(key, fieid, value);
            }
        }

        /**
         * 获取hash中value的集合
         *
         * @param key
         * @return List<String>
         */
        public List<String> hvals(String key) {
            try (Jedis jedis = redisContext.getJedis()) {
                return jedis.hvals(key);
            }
        }

        /**
         * 在指定的存储位置加上指定的数字，存储位置的值必须可转为数字类型
         *
         * @param key
         * @param fieid 存储位置
         * @param value 要增加的值,可以是负数
         * @return 增加指定数字后，存储位置的值
         */
        public long hincrby(String key, String fieid, long value) {
            try (Jedis jedis = redisContext.getJedis()) {
                return jedis.hincrBy(key, fieid, value);
            }
        }

        /**
         * 返回指定hash中的所有存储名字,类似Map中的keySet方法
         *
         * @param key
         * @return Set<String> 存储名称的集合
         */
        public Set<String> hkeys(String key) {
            try (Jedis jedis = redisContext.getJedis()) {
                return jedis.hkeys(key);
            }
        }

        /**
         * 获取hash中存储的个数，类似Map中size方法
         *
         * @param key
         * @return long 存储的个数
         */
        public long hlen(String key) {
            try (Jedis jedis = redisContext.getJedis()) {

                return jedis.hlen(key);
            }
        }

        /**
         * 根据多个key，获取对应的value，返回List,如果指定的key不存在,List对应位置为null
         *
         * @param key
         * @param fieids 存储位置
         * @return List<String>
         */
        public List<String> hmget(String key, String... fieids) {
            try (Jedis jedis = redisContext.getJedis()) {
                return jedis.hmget(key, fieids);
            }
        }

        public List<byte[]> hmget(byte[] key, byte[]... fieids) {
            try (Jedis jedis = redisContext.getJedis()) {

                return jedis.hmget(key, fieids);
            }
        }

        /**
         * 添加对应关系，如果对应关系已存在，则覆盖
         *
         * @param key
         * @param map 对应关系
         * @return 状态，成功返回OK
         */
        public String hmset(String key, Map<String, String> map) {
            try (Jedis jedis = redisContext.getJedis()) {
                return jedis.hmset(key, map);
            }
        }

        /**
         * 添加对应关系，如果对应关系已存在，则覆盖
         *
         * @param key
         * @param
         * @return 状态，成功返回OK
         */
        public String hmset(byte[] key, Map<byte[], byte[]> map) {
            try (Jedis jedis = redisContext.getJedis()) {
                return jedis.hmset(key, map);
            }
        }

    }


    //*******************************************Strings*******************************************//
    public static class Strings {

        /**
         * <p>判断key是否存在</p>
         *
         * @param key
         * @return true OR false
         */
        public Boolean exists(String key) {
            try (Jedis jedis = redisContext.getJedis()) {
                return jedis.exists(key);
            }
        }

        /**
         * 根据key获取记录
         *
         * @param key
         * @return 值
         */
        public String get(String key) {
            try (Jedis jedis = redisContext.getJedis()) {
                return jedis.get(key);
            }
        }

        public String get(String key, int DBindex) {
            try (Jedis jedis = redisContext.getJedis()) {
                return jedis.get(key);
            }
        }

        /**
         * 根据key获取记录
         *
         * @param key
         * @return 值
         */
        public byte[] get(byte[] key) {
            try (Jedis jedis = redisContext.getJedis()) {
                return jedis.get(key);
            }
        }

        public byte[] get(byte[] key, int DBindex) {
            try (Jedis jedis = redisContext.getJedis(DBindex)) {
                return jedis.get(key);
            }
        }


        /**
         * 添加有过期时间的记录
         *
         * @param key
         * @param seconds 过期时间，以秒为单位
         * @param value
         * @return String 操作状态
         */
        public String setEx(String key, int seconds, String value) {
            try (Jedis jedis = redisContext.getJedis()) {
                return jedis.setex(key, seconds, value);
            }
        }

        public String setEx(String key, int seconds, String value, int DBindex) {
            try (Jedis jedis = redisContext.getJedis(DBindex)) {
                return jedis.setex(key, seconds, value);
            }
        }

        /**
         * 添加有过期时间的记录
         *
         * @param key
         * @param seconds 过期时间，以秒为单位
         * @param value
         * @return String 操作状态
         */
        public String setEx(byte[] key, int seconds, byte[] value) {
            try (Jedis jedis = redisContext.getJedis()) {
                return jedis.setex(key, seconds, value);
            }

        }

        public String setEx(byte[] key, int seconds, byte[] value, int DBindex) {
            try (Jedis jedis = redisContext.getJedis(DBindex)) {
                return jedis.setex(key, seconds, value);
            }
        }

        /**
         * 添加一条记录，仅当给定的key不存在时才插入
         *
         * @param key
         * @param value
         * @return long 状态码，1插入成功且key不存在，0未插入，key存在
         */
        public long setnx(String key, String value) {
            try (Jedis jedis = redisContext.getJedis()) {
                return jedis.setnx(key, value);
            }
        }

        public long setnx(String key, String value, int DBindex) {

            try (Jedis jedis = redisContext.getJedis(DBindex)) {
                return jedis.setnx(key, value);
            }
        }


        /**
         * 添加记录,如果记录已存在将覆盖原有的value
         *
         * @param key
         * @param value
         * @return 状态码
         */
        public String set(String key, String value) {
            return set(SafeEncoder.encode(key), SafeEncoder.encode(value));
        }

        public String set(String key, String value, int DBindex) {
            return set(SafeEncoder.encode(key), SafeEncoder.encode(value), DBindex);
        }

        /**
         * 添加记录,如果记录已存在将覆盖原有的value
         *
         * @param key
         * @param value
         * @return 状态码
         */
        public String set(String key, byte[] value) {
            return set(SafeEncoder.encode(key), value);
        }

        public String set(String key, byte[] value, int DBindex) {
            return set(SafeEncoder.encode(key), value, DBindex);
        }

        /**
         * 添加记录,如果记录已存在将覆盖原有的value
         *
         * @param key
         * @param value
         * @return 状态码
         */
        public String set(byte[] key, byte[] value) {
            try (Jedis jedis = redisContext.getJedis()) {
                return jedis.set(key, value);
            }
        }

        public String set(byte[] key, byte[] value, int DBindex) {
            try (Jedis jedis = redisContext.getJedis(DBindex)) {
                return jedis.set(key, value);
            }
        }

        /**
         * 从指定位置开始插入数据，插入的数据会覆盖指定位置以后的数据<br/>
         * 例:String str1="123456789";<br/>
         * 对str1操作后setRange(key,4,0000)，str1="123400009";
         *
         * @param key
         * @param offset
         * @param value
         * @return long value的长度
         */
        public long setRange(String key, long offset, String value) {
            try (Jedis jedis = redisContext.getJedis()) {
                return jedis.setrange(key, offset, value);
            }
        }

        public long setRange(String key, long offset, String value, int DBindex) {
            try (Jedis jedis = redisContext.getJedis(DBindex)) {
                return jedis.setrange(key, offset, value);
            }
        }

        /**
         * <p>删除指定的key,也可以传入一个包含key的数组</p>
         *
         * @param keys 一个key  也可以使 string 数组
         * @return 返回删除成功的个数
         */
        public Long del(String... keys) {
            try (Jedis jedis = redisContext.getJedis()) {
                return jedis.del(keys);
            }
        }

        /**
         * 在指定的key中追加value
         *
         * @param key
         * @param value
         * @return long 追加后value的长度
         **/
        public long append(String key, String value) {
            try (Jedis jedis = redisContext.getJedis()) {
                return jedis.append(key, value);
            }
        }

        public long append(String key, String value, int DBindex) {
            try (Jedis jedis = redisContext.getJedis(DBindex)) {
                return jedis.append(key, value);
            }

        }

        /**
         * 将key对应的value减去指定的值，只有value可以转为数字时该方法才可用
         *
         * @param key
         * @param number 要减去的值
         * @return long 减指定值后的值
         */
        public long decrBy(String key, long number) {
            try (Jedis jedis = redisContext.getJedis()) {

                return jedis.decrBy(key, number);
            }
        }

        public long decrBy(String key, long number, int DBindex) {
            try (Jedis jedis = redisContext.getJedis(DBindex)) {
                return jedis.decrBy(key, number);
            }

        }

        /**
         * <b>可以作为获取唯一id的方法</b><br/>
         * 将key对应的value加上指定的值，只有value可以转为数字时该方法才可用
         *
         * @param key
         * @param number 要减去的值
         * @return long 相加后的值
         */
        public long incrBy(String key, long number) {
            try (Jedis jedis = redisContext.getJedis()) {
                return jedis.incrBy(key, number);
            }

        }

        public long incrBy(String key, long number, int DBindex) {
            try (Jedis jedis = redisContext.getJedis(DBindex)) {
                return jedis.incrBy(key, number);
            }
        }

        /**
         * 对指定key对应的value进行截取
         *
         * @param key
         * @param startOffset 开始位置(包含)
         * @param endOffset   结束位置(包含)
         * @return String 截取的值
         */
        public String getrange(String key, long startOffset, long endOffset) {
            try (Jedis jedis = redisContext.getJedis()) {

                return jedis.getrange(key, startOffset, endOffset);
            }
        }

        public String getrange(String key, long startOffset, long endOffset, int DBindex) {
            try (Jedis jedis = redisContext.getJedis(DBindex)) {

                return jedis.getrange(key, startOffset, endOffset);
            }
        }

        /**
         * 获取并设置指定key对应的value<br/>
         * 如果key存在返回之前的value,否则返回null
         *
         * @param key
         * @param value
         * @return String 原始value或null
         */
        public String getSet(String key, String value) {
            try (Jedis jedis = redisContext.getJedis()) {

                return jedis.getSet(key, value);
            }

        }

        public String getSet(String key, String value, int DBindex) {
            try (Jedis jedis = redisContext.getJedis(DBindex)) {
                return jedis.getSet(key, value);
            }
        }

        /**
         * 批量获取记录,如果指定的key不存在返回List的对应位置将是null
         *
         * @param keys
         * @return List<String> 值得集合
         */
        public List<String> mget(String... keys) {
            try (Jedis jedis = redisContext.getJedis()) {

                return jedis.mget(keys);
            }
        }

        public List<String> mget(int DBindex, String... keys) {
            try (Jedis jedis = redisContext.getJedis(DBindex)) {
                return jedis.mget(keys);
            }
        }

        /**
         * 批量存储记录
         *
         * @param keysvalues 例:keysvalues="key1","value1","key2","value2";
         * @return String 状态码
         */
        public String mset(String... keysvalues) {
            try (Jedis jedis = redisContext.getJedis()) {
                return jedis.mset(keysvalues);
            }
        }

        public String mset(int DBindex, String... keysvalues) {
            try (Jedis jedis = redisContext.getJedis(DBindex)) {

                return jedis.mset(keysvalues);
            }
        }

        /**
         * 获取key对应的值的长度
         *
         * @param key
         * @return value值得长度
         */
        public long strlen(String key) {
            try (Jedis jedis = redisContext.getJedis()) {

                return jedis.strlen(key);
            }
        }

        public long strlen(String key, int DBindex) {
            try (Jedis jedis = redisContext.getJedis(DBindex)) {
                return jedis.strlen(key);
            }
        }


    }


    //*******************************************Lists*******************************************//
    public static class Lists {


        /**
         * <p>判断key是否存在</p>
         *
         * @param key
         * @return true OR false
         */
        public Boolean exists(String key) {
            try (Jedis jedis = redisContext.getJedis()) {

                return jedis.exists(key);
            }
        }

        /**
         * List长度
         *
         * @param key
         * @return 长度
         */
        public long llen(String key) {
            return llen(SafeEncoder.encode(key));
        }

        /**
         * List长度
         *
         * @param key
         * @return 长度
         */
        public long llen(byte[] key) {
            try (Jedis jedis = redisContext.getJedis()) {
                return jedis.llen(key);
            }
        }

        /**
         * 覆盖操作,将覆盖List中指定位置的值
         *
         * @param key
         * @param index 位置
         * @param value 值
         * @return 状态码
         */
        public String lset(byte[] key, int index, byte[] value) {
            try (Jedis jedis = redisContext.getJedis()) {
                return jedis.lset(key, index, value);
            }

        }

        /**
         * 覆盖操作,将覆盖List中指定位置的值
         *
         * @param key
         * @param index 位置
         * @param value 值
         * @return 状态码
         */
        public String lset(String key, int index, String value) {
            return lset(SafeEncoder.encode(key), index,
                    SafeEncoder.encode(value));
        }

        /**
         * 在value的相对位置插入记录
         *
         * @param key
         * @param
         * @param pivot 相对位置的内容
         * @param value 插入的内容
         * @return 记录总数
         */
        public long linsert(String key, BinaryClient.LIST_POSITION where, String pivot,
                            String value) {
            return linsert(SafeEncoder.encode(key), where,
                    SafeEncoder.encode(pivot), SafeEncoder.encode(value));
        }

        /**
         * 在指定位置插入记录
         *
         * @param key
         * @param
         * @param pivot 相对位置的内容
         * @param value 插入的内容
         * @return 记录总数
         */
        public long linsert(byte[] key, BinaryClient.LIST_POSITION where, byte[] pivot,
                            byte[] value) {
            try (Jedis jedis = redisContext.getJedis()) {
                return jedis.linsert(key, where, pivot, value);
            }
        }

        /**
         * 获取List中指定位置的值
         *
         * @param key
         * @param index 位置
         * @return 值
         **/
        public String lindex(String key, int index) {
            return SafeEncoder.encode(lindex(SafeEncoder.encode(key), index));
        }

        /**
         * 获取List中指定位置的值
         *
         * @param key
         * @param index 位置
         * @return 值
         **/
        public byte[] lindex(byte[] key, int index) {
            try (Jedis jedis = redisContext.getJedis()) {
                return jedis.lindex(key, index);
            }
        }

        /**
         * 将List中的第一条记录移出List
         *
         * @param key
         * @return 移出的记录
         */
        public String lpop(String key) {
            return SafeEncoder.encode(lpop(SafeEncoder.encode(key)));
        }

        /**
         * 将List中的第一条记录移出List
         *
         * @param key
         * @return 移出的记录
         */
        public byte[] lpop(byte[] key) {
            try (Jedis jedis = redisContext.getJedis()) {
                return jedis.lpop(key);
            }
        }

        /**
         * 将List中最后第一条记录移出List
         *
         * @param key
         * @return 移出的记录
         */
        public String rpop(String key) {
            try (Jedis jedis = redisContext.getJedis()) {
                return jedis.rpop(key);
            }
        }

        /**
         * 向List尾部追加记录
         *
         * @param key
         * @param value
         * @return 记录总数
         */
        public long lpush(String key, String value) {
            return lpush(SafeEncoder.encode(key), SafeEncoder.encode(value));
        }

        /**
         * 向List头部追加记录
         *
         * @param key
         * @param value
         * @return 记录总数
         */
        public long rpush(String key, String value) {
            try (Jedis jedis = redisContext.getJedis()) {
                return jedis.rpush(key, value);
            }

        }

        /**
         * 向List头部追加记录，带数据库
         *
         * @param key
         * @param value
         * @return 记录总数
         */
        public long rpush(String key, String value, int DBindex) {
            try (Jedis jedis = redisContext.getJedis(DBindex)) {
                return jedis.rpush(key, value);
            }

        }

        /**
         * 向List尾部追加记录，带数据库
         *
         * @param key
         * @param value
         * @return 记录总数
         */
        public long lpush(String key, String value, int DBindex) {
            try (Jedis jedis = redisContext.getJedis(DBindex)) {
                return jedis.lpush(SafeEncoder.encode(key), SafeEncoder.encode(value));
            }
        }


        /**
         * 向List头部追加记录
         *
         * @param key
         * @param value
         * @return 记录总数
         */
        public long rpush(byte[] key, byte[] value) {
            try (Jedis jedis = redisContext.getJedis()) {
                return jedis.rpush(key, value);
            }
        }


        /**
         * 向List头部追加记录
         *
         * @param key
         * @param value
         * @return 记录总数
         */
        public long rpush(String key, String... value) {
            try (Jedis jedis = redisContext.getJedis()) {
                return jedis.rpush(key, value);
            }
        }

        /**
         * 向List中追加记录
         *
         * @param key
         * @param value
         * @return 记录总数
         */
        public long lpush(byte[] key, byte[] value) {
            try (Jedis jedis = redisContext.getJedis()) {
                return jedis.lpush(key, value);
            }
        }

        /**
         * 获取指定范围的记录，可以做为分页使用
         *
         * @param key
         * @param start
         * @param end
         * @return List
         */
        public List<String> lrange(String key, long start, long end) {
            try (Jedis jedis = redisContext.getJedis()) {
                return jedis.lrange(key, start, end);
            }
        }


        /**
         * 获取指定范围的记录，可以做为分页使用
         *
         * @param key
         * @param start
         * @param end
         * @return List
         */
        public List<String> lrange(String key, long start, long end, int DBindex) {
            try (Jedis jedis = redisContext.getJedis(DBindex)) {

                return jedis.lrange(key, start, end);
            }
        }

        /**
         * 获取指定范围的记录，可以做为分页使用
         *
         * @param key
         * @param start
         * @param end   如果为负数，则尾部开始计算
         * @return List
         */
        public List<byte[]> lrange(byte[] key, int start, int end) {
            try (Jedis jedis = redisContext.getJedis()) {

                return jedis.lrange(key, start, end);
            }
        }

        /**
         * 删除List中c条记录，被删除的记录值为value
         *
         * @param key
         * @param c     要删除的数量，如果为负数则从List的尾部检查并删除符合的记录
         * @param value 要匹配的值
         * @return 删除后的List中的记录数
         */
        public long lrem(byte[] key, int c, byte[] value) {
            try (Jedis jedis = redisContext.getJedis()) {

                return jedis.lrem(key, c, value);
            }
        }

        /**
         * 删除List中c条记录，被删除的记录值为value
         *
         * @param key
         * @param c     要删除的数量，如果为负数则从List的尾部检查并删除符合的记录
         * @param value 要匹配的值
         * @return 删除后的List中的记录数
         */
        public long lrem(String key, int c, String value) {
            return lrem(SafeEncoder.encode(key), c, SafeEncoder.encode(value));
        }

        /**
         * 删除List中c条记录，被删除的记录值为value,带数据库编号
         *
         * @param key
         * @param c     要删除的数量，如果为负数则从List的尾部检查并删除符合的记录
         * @param value 要匹配的值
         * @return 删除后的List中的记录数
         */
        public long lrem(String key, int c, String value, int DBindex) {
            try (Jedis jedis = redisContext.getJedis(DBindex)) {
                return jedis.lrem(SafeEncoder.encode(key), c, SafeEncoder.encode(value));
            }

        }

        /**
         * 算是删除吧，只保留start与end之间的记录
         *
         * @param key
         * @param start 记录的开始位置(0表示第一条记录)
         * @param end   记录的结束位置（如果为-1则表示最后一个，-2，-3以此类推）
         * @return 执行状态码
         */
        public String ltrim(byte[] key, int start, int end) {
            try (Jedis jedis = redisContext.getJedis()) {
                return jedis.ltrim(key, start, end);
            }
        }

        /**
         * 算是删除吧，只保留start与end之间的记录
         *
         * @param key
         * @param start 记录的开始位置(0表示第一条记录)
         * @param end   记录的结束位置（如果为-1则表示最后一个，-2，-3以此类推）
         * @return 执行状态码
         */
        public String ltrim(String key, int start, int end) {
            return ltrim(SafeEncoder.encode(key), start, end);
        }


        public long lpushx(String key, String... value) {
            try (Jedis jedis = redisContext.getJedis()) {
                return jedis.lpushx(key, value);
            }

        }


    }

    public static String getLock(String lock) {

        return getLock(lock, 10000);
    }


    /**
     * 获取锁
     *
     * @param lock    锁的key
     * @param expired 默认超时时间，毫秒
     * @return
     */
    public static String getLock(String lock, long expired) {
        Jedis jedis = null;
        try {
            jedis = redisContext.getJedis();
            return jedis.set(lock, "1", "NX", "PX", expired);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }


    public static Long releaseLock(String lock) {
        Jedis jedis = null;
        try {
            jedis = redisContext.getJedis();
            return jedis.del(lock);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    /**
     * @param jedis      redis客户端
     * @param lockKey    锁
     * @param requestId  请求表示
     * @param expireTime 过期时间
     * @return
     */
    public static boolean tryGetDistributedLock(Jedis jedis, String lockKey, String requestId, int expireTime) {
        String result = jedis.set(lockKey, requestId, "NX", "PX", expireTime);
        if ("OK".equals(result)) {
            return true;
        }
        return false;
    }

    private static final Long RELEASE_SUCCESS = 1L;

    /**
     * @param jedis     redis客户端
     * @param lockKey   锁
     * @param requestId 请求表示
     * @return
     */
    public static boolean releaseDistributedLock(Jedis jedis, String lockKey, String requestId) {
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        Object result = jedis.eval(script, Collections.singletonList(lockKey), Collections.singletonList(requestId));
        if (RELEASE_SUCCESS.equals(result)) {
            return true;
        }
        return false;
    }

}
