package yp970814.redis;

import java.io.*;
import java.util.*;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.*;

/**
 * @Author yuanping970814@163.com
 * @Date 2024-04-14 13:39
 */
public class RedisClusterDao {

    class ListTranscoder<T extends Serializable> {
        public void close(Closeable closeable) {
            if (closeable != null) {
                try {
                    closeable.close();
                } catch (Exception e) {
                }
            }
        }

        @SuppressWarnings("unchecked")
        public List<T> deserialize(byte[] in) {
            List<T> list = new ArrayList<>();
            ByteArrayInputStream bis = null;
            ObjectInputStream is = null;
            try {
                if (in != null) {
                    bis = new ByteArrayInputStream(in);
                    is = new ObjectInputStream(bis);
                    while (true) {
                        T m = (T) is.readObject();
                        if (m == null) {
                            break;
                        }

                        list.add(m);

                    }
                    is.close();
                    bis.close();
                }
            } catch (IOException e) {
            } catch (ClassNotFoundException e) {
            } finally {
                close(is);
                close(bis);
            }
            return list;
        }

        @SuppressWarnings("unchecked")
        public byte[] serialize(Object value) {
            if (value == null)
                throw new NullPointerException("Can't serialize null");

            List<T> values = (List<T>) value;

            byte[] results = null;
            ByteArrayOutputStream bos = null;
            ObjectOutputStream os = null;

            try {
                bos = new ByteArrayOutputStream();
                os = new ObjectOutputStream(bos);
                for (T m : values) {
                    os.writeObject(m);
                }

                // os.writeObject(null);
                os.close();
                bos.close();
                results = bos.toByteArray();
            } catch (IOException e) {
                throw new IllegalArgumentException("Non-serializable object", e);
            } finally {
                close(os);
                close(bos);
            }
            return results;
        }
    }

    private static Logger logger = LoggerFactory.getLogger(RedisClusterDao.class);

    public static ShardedJedisPool shardedJedisPool = null;

    private static JedisSentinelPool jedisPool = null;

    private static String sentinel;
    private static String masterName;
    private static String password;
    private static int timeout;
    private static String[] sentinels;
    private static Set<String> sentinelSets = new HashSet<String>();
    private static int database = 0;

    static {
        ResourceBundle bundle = ResourceBundle.getBundle("redis");
        sentinel = bundle.getString("redis.pool.sentinel");
        masterName = bundle.getString("redis.pool.mastername");
        timeout = Integer.parseInt(bundle.getString("redis.pool.timeout"));
        password = bundle.getString("redis.pool.pass");
        database = Integer.parseInt(bundle.getString("redis.pool.database"));

        sentinels = sentinel.split(",");
        for(String sentinel : sentinels){
            sentinelSets.add(sentinel);
        }
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(100);
        jedisPoolConfig.setMaxIdle(100);
        jedisPoolConfig.setMinIdle(5);

        if(StringUtils.isEmpty(password)){
            jedisPool = new JedisSentinelPool(masterName, sentinelSets, jedisPoolConfig);
        }else{
            jedisPool = new JedisSentinelPool(masterName, sentinelSets, jedisPoolConfig, timeout, password, database);
        }
        logger.info("Current master: " + jedisPool.getCurrentHostMaster().toString());
    }

    public void destroy() {
        if (jedisPool != null)
            jedisPool.destroy();
    }

    public synchronized void set(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.set(key, value);
        } catch (Exception e) {
            logger.error(e+"");
        } finally {
            jedis.close();
        }
    }

    public synchronized String get(String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.get(key);
        } catch (Exception e) {
            logger.error(e+"");

            return null;
        } finally {
            jedis.close();
        }
    }

    public synchronized Boolean exists(String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.exists(key);
        } catch (Exception e) {
            logger.error(e+"");

            return null;
        } finally {
            jedis.close();
        }
    }

    public synchronized String lset(String key, long index, String value) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.lset(key, index, value);
        } catch (Exception e) {
            logger.error(e+"");

            return null;
        } finally {
            jedis.close();
        }
    }

    public synchronized String lset(String key, long index, Object value) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            ObjectMapper mapper = new ObjectMapper();
            String objectJson = mapper.writeValueAsString(value);
            return jedis.lset(key, index, objectJson);
        } catch (Exception e) {
            logger.error(e+"");

        } finally {
            jedis.close();
        }
        return null;
    }

    public synchronized long del(String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.del(key);
        } catch (Exception e) {
            logger.error(e+"");
            return 0;
        } finally {
            jedis.close();
        }
    }

    public synchronized void lpush(String key, String strings) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.lpush(key, strings);
        } catch (Exception e) {
            logger.error(e+"");
        } finally {
            jedis.close();
        }
    }

    /**
     * @param key
     * @param count
     * @param value
     *
     *            ???redis list ????????
     */
    public synchronized void lrem(String key, int count, String value) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.lrem(key, count, value);
        } catch (Exception e) {
            logger.error(e+"");
        } finally {
            jedis.close();
        }
    }

    public synchronized void lpush(String key, Object value) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String objectJson = mapper.writeValueAsString(value);
            lpush(key, objectJson);
        } catch (Exception e) {
            logger.error(e+"");
        }
    }

    public synchronized String rpop(String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.rpop(key);
        } catch (Exception e) {
            logger.error(e+"");
        } finally {
            jedis.close();
        }
        return null;
    }

    public synchronized List<String> brpop(String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.brpop(key);
        } catch (Exception e) {
            logger.error(e+"");
        } finally {
            jedis.close();
        }
        return null;
    }

    public synchronized <T> T rpop(String key, Class<T> clazz) {
        Jedis jedis = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            jedis = jedisPool.getResource();
            String objectJson = jedis.rpop(key);
            if (objectJson == null)
                return null;
            return mapper.readValue(objectJson, clazz);
        } catch (Exception e) {
            logger.error(e+"");

        } finally {
            jedis.close();
        }
        return null;
    }

    public synchronized List<String> lrange(String key, int start, int end) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.lrange(key, start, end);
        } catch (Exception e) {
            logger.error(e+"");

        } finally {
            jedis.close();
        }
        return null;
    }

    // ????±?start-end???????????
    public synchronized String ltrim(String key, int start, int end) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.ltrim(key, start, end);
        } catch (Exception e) {
            logger.error(e+"");
        } finally {
            jedis.close();
        }
        return null;
    }

    public synchronized List<String> getList(String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.lrange(key, 0, -1);
        } catch (Exception e) {
            logger.error(e+"");
        } finally {
            jedis.close();
        }
        return null;
    }

    public synchronized <T> List<T> getList(String key, Class<T> clazz) {
        List<T> result = new ArrayList<T>();
        try {
            List<String> list = lrange(key, 0, -1);
            for (String s : list) {
                ObjectMapper mapper = new ObjectMapper();
                result.add(mapper.readValue(s, clazz));
            }
        } catch (Exception e) {
            logger.error(e+"");
        }
        return result;
    }

    public synchronized <T> List<T> getList(String key, Class<T> clazz, int size) {
        List<T> result = new ArrayList<T>();
        try {
            List<String> list = lrange(key, 0, size);
            for (String s : list) {
                ObjectMapper mapper = new ObjectMapper();
                result.add(mapper.readValue(s, clazz));
            }
        } catch (Exception e) {
            logger.error(e+"");
        }
        return result;
    }

    public synchronized void addSet(String key, Set<String> set) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            for (String members : set) {
                jedis.sadd(key, members);
            }
        } catch (Exception e) {
            logger.error(e+"");
        } finally {
            jedis.close();
        }
    }

    public synchronized void addZset(String key, Double score,String member) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.zadd(key, score, member);
        } catch (Exception e) {
            logger.error(e+"");
        } finally {
            jedis.close();
        }
    }

    public synchronized void deleteZset(String key,String member) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.zrem(key, member);
        } catch (Exception e) {
            logger.error(e+"");
        } finally {
            jedis.close();
        }
    }

    public synchronized Set<String> getZset(String key,long start,long end) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            Set<String>  res = jedis.zrange(key, start, end);
            return res;
        } catch (Exception e) {
            logger.error(e+"");
            return null;
        } finally {
            jedis.close();
        }
    }

    public synchronized Long zSetSize(String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            Long res = jedis.zcard(key);
            return res;
        } catch (Exception e) {
            logger.error(e+"");
            return null;
        } finally {
            jedis.close();
        }
    }


    /**
     * ????????????
     *
     * @param key
     * @param value
     * @return
     */
    public synchronized void set(String key, Object value) {

        ObjectMapper mapper = new ObjectMapper();
        String objectJson = "";
        try {
            objectJson = mapper.writeValueAsString(value);
            set(key, objectJson);
        } catch (JsonGenerationException e) {
            logger.error(e+"");
        } catch (JsonMappingException e) {
            logger.error(e+"");
        } catch (IOException e) {
            logger.error(e+"");
        }
    }

    /**
     * redis set ?????????
     * @param key ???
     * @param sec ?????????
     * @param value ?
     */
    public synchronized void setex(String key,int sec,String value){
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.setex(key,sec,value);
        } catch (Exception e) {
            logger.error(e+"");
        } finally {
            jedis.close();
        }
    }




    /**
     * ????key ???????
     *
     * @param key
     * @return
     */
    public synchronized <T> T get(String key, Class<T> clazz) {
        try {
            String value = get(key);
            // value?null???readValue????
            if (value == null) {
                return null;
            }
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(value, clazz);
        } catch (Exception e) {
            logger.error(e+"");
            return null;
        }
    }

    /**
     * @param key
     * @param clazz
     *
     *            ???????? List<T>,???????????
     *
     * @return ?????
     */
    public synchronized <T> List<T> getObjectList(String key, Class<T> clazz) {
        try {

            logger.info("key=" + key);
            String value = get(key);
            if (value == null)
                return null;
            ObjectMapper mapper = new ObjectMapper();
            JavaType javaType = mapper.getTypeFactory().constructParametricType(List.class, clazz);
            return mapper.readValue(value, javaType);
        } catch (Exception e) {
            logger.error(e+"");
            return null;
        }
    }

    /**
     * @param channel
     * @param message
     *            ????
     */
    public synchronized void publish(String channel, String message) {
        Jedis jedis = null;
        try {
            jedis =  jedisPool.getResource();
            jedis.publish(channel, message);
        } catch (Exception e) {
            logger.error(e+"");
        } finally {
            jedis.close();
        }
    }

    /**
     * @param channel
     * @param value
     *            ????
     */
    public synchronized void publish(String channel, Object value) {
        Jedis jedis = null;
        try {
            jedis =  jedisPool.getResource();
            ObjectMapper mapper = new ObjectMapper();
            jedis.publish(channel, mapper.writeValueAsString(value));
        } catch (Exception e) {
            logger.error(e+"");
        } finally {
            jedis.close();
        }
    }

    /**
     * @param jedisPubSub
     * @param channels
     *            ????
     */
    public synchronized void subscribe(JedisPubSub jedisPubSub, String... channels) {
        Jedis jedis = null;
        try {
            jedis =  jedisPool.getResource();
            jedis.subscribe(jedisPubSub, channels);
        } catch (Exception e) {
            logger.error(e+"");
        } finally {
            jedis.close();
        }
    }

    public synchronized <T extends Serializable> void setObjectList(String key, List<T> value) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            ListTranscoder<T> transcoder = new ListTranscoder<T>();
            jedis.set(key.getBytes(), transcoder.serialize(value));
        } catch (Exception e) {
            logger.error(e+"");

        } finally {
            jedis.close();
        }
    }

    public synchronized <T extends Serializable> List<T> getObjectList(String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            byte[] value = jedis.get(key.getBytes());
            if (value == null)
                return null;
            ListTranscoder<T> transcoder = new ListTranscoder<T>();
            return transcoder.deserialize(value);
        } catch (Exception e) {
            logger.error(e+"");

            return null;
        } finally {
            jedis.close();
        }
    }

    public synchronized long incr(String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            if (jedis != null) {
                return jedis.incr(key);
            }
            return 0;
        } catch (Exception e) {
            logger.error(e+"");
            return 0;
        } finally {
            jedis.close();
        }
    }

}
