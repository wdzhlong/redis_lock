package redis.lock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.core.types.Expiration;

import javax.annotation.Resource;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * @author: zhenghailong
 * @date: 2019/10/2 21:49
 * @modified By:
 * @description:
 */
public class RedisLock {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedisTemplate redisTemplate;

    public boolean lock(String lockKey,String value){
        return stringRedisTemplate.opsForValue().setIfAbsent(lockKey,value,10,TimeUnit.SECONDS);
    }

    public boolean unlock(String lockKey,String value){
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        DefaultRedisScript<String> redisScript = new DefaultRedisScript<>();
        redisScript.setResultType(String.class);
        redisScript.setScriptText(script);
        Boolean result = (Boolean)this.redisTemplate.execute(redisScript,redisTemplate.getStringSerializer(),redisTemplate.getStringSerializer(), Collections.singletonList(lockKey),value);
        return result == null ? false : result;
    }
}
