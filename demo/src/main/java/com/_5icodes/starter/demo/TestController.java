package com._5icodes.starter.demo;

import com._5icodes.starter.common.exception.BizRuntimeException;
import com._5icodes.starter.common.utils.JsonUtils;
import com._5icodes.starter.monitor.cache.key.CacheKeyUtils;
import com.alicp.jetcache.Cache;
import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.Cached;
import com.alicp.jetcache.anno.CreateCache;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping
@Validated
@Slf4j
public class TestController {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private TestFeign testFeign;
    @Autowired
    private DefaultMQProducer defaultMqProducer;
    @Autowired
    private KafkaTemplate kafkaTemplate;

    @Value("${testApollo:123}")
    private String testApollo;

    @GetMapping("/kafka")
    public void testKafka(String topic) throws ExecutionException, InterruptedException {
        ListenableFuture future = kafkaTemplate.send(topic, "testKafkaProducerKey", "test");
        future.get();
    }

    @SneakyThrows
    @GetMapping("/rocketmq")
    public void testRocketmq(String topic) {
        Message message = new Message();
        message.setTopic(topic);
        message.setBody("test".getBytes());
        defaultMqProducer.send(message, new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("onSuccess: {}", JsonUtils.toJson(sendResult));
            }

            @Override
            public void onException(Throwable e) {
                log.error("onException:", e);
            }
        });
    }

    @GetMapping("/apollo")
    public String testApollo() {
        return testApollo;
    }

    @GetMapping("/feign")
    public Object testFeign() {
        return testFeign.getTest();
    }

    @PostMapping("/test")
    public Object test() {
        redisTemplate.opsForValue().set(CacheKeyUtils.just("123"), "123");
        redisTemplate.opsForValue().get(CacheKeyUtils.just("123"));
        userCache.put("test", "1");
        return userCache.get("test");
    }

    @CreateCache(expire = 600, name = "userCache:", cacheType = CacheType.LOCAL)
    private Cache<String, String> userCache;

    @PostMapping("/method")
    @Cached(cacheType = CacheType.REMOTE)
    public CustomBean method(@RequestBody CustomBean test) {
        log.info(JsonUtils.toJson(test));
        log.info(JsonUtils.toJson(new Date()));
        return test;
    }

    @PostMapping("/testCustomFeign")
    public void testCustomFeign() {
        log.info("testCustomFeign");
    }

    @PostMapping("/testException")
    public void testException() {
        throw new BizRuntimeException(CodeMsgEnums.TOKEN_OVERTIME);
    }

    @PostMapping("/testExceptionWithArgs")
    public void testExceptionWithArgs() {
        throw new BizRuntimeException(CodeMsgEnums.NULL, "TEST");
    }

    //will throw ConstraintViolationException
    //will throw MethodArgumentTypeMismatchException
    @PostMapping("/valid/post")
    public String postArgsValid(@NotNull(message = "用户名不能为空") Integer username) {
        return null;
    }

    //will throw MethodArgumentNotValidException
    @PostMapping("/valid/post1")
    public String postArgsValid1(@Valid @RequestBody User user) {
        return null;
    }

    //will throw ConstraintViolationException
    //will throw HttpMessageNotReadableException
    @PostMapping("/valid/post2")
    public String postArgsValid2(@Valid @RequestBody User user, BindingResult bindingResult) {
        return null;
    }

    //will throw MissingRequestHeaderException
    @PostMapping("/valid/post3")
    public String postArgsValid3(@RequestHeader String username) {
        return null;
    }

    //will throw IllegalArgumentException
    @PostMapping("/valid/post4")
    public String postArgsValid4() {
        Date day = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = df.format(day);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM");
        String format = dateFormat.format(date);
        System.out.println(format);
        return null;
    }

    //will throw IllegalStateException
    @PostMapping("/valid/post5")
    public String postArgsValid5(HttpServletResponse response) throws IOException {
        OutputStream os = response.getOutputStream();
        PrintWriter out = response.getWriter();
        return null;
    }

    //will throw MissingServletRequestParameterException
    //will throw HttpRequestMethodNotSupportedException
    @PostMapping("/valid/post6")
    public String postArgsValid6(@RequestParam String username) {
        return null;
    }

    @Data
    private static class User {
        @NotNull(message = "用户名不能为空")
        private String username;
    }
}