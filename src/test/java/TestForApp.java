import com.cheney.ApplicationContext;
import com.cheney.javaconfig.MyBeanRegistrar;
import com.cheney.redis.lock.RedisLock;
import com.cheney.redis.lock.awaken.MultiPathRedisLock;
import com.cheney.redis.lock.awaken.ReentrantRedisLock;
import com.cheney.redis.lock.awaken.SecondLevelRedisLock;
import com.cheney.redis.rateLimit.RateLimiter;
import com.cheney.utils.SpringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Base64Utils;

import java.util.ArrayList;
import java.util.concurrent.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ApplicationContext.class)
public class TestForApp {

    @Test
    public void test() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        ArrayList<Callable<String>> threads = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            threads.add(() -> {
                try (RedisLock lock = new ReentrantRedisLock("test")) {
                    if (lock.tryLock(5, 5, TimeUnit.SECONDS)) {
                        Thread.sleep(1000);
                        System.out.println("线程完成");
                    } else {
                        System.out.println("线程失败");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return "success";
            });
        }
        executorService.invokeAll(threads);
    }


    @Test
    public void test2() {
        byte[] encode = Base64Utils.encode("tradeAdmin:pw123456".getBytes());
        System.out.println(new String(encode));
        byte[] decode = Base64Utils.decodeFromString(new String(encode));
        System.out.println(new String(decode));
    }

    @Test
    public void test3() throws InterruptedException {
        RedisLock redisLock = new ReentrantRedisLock("test:test");
        try {
            boolean b;
            b = redisLock.tryLock(100, 20, TimeUnit.SECONDS);
            System.out.println("first lock result:" + b);
            b = redisLock.tryLock(100, 20, TimeUnit.SECONDS);
            System.out.println("second lock result:" + b);
            Thread.sleep(5 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            redisLock.unLock();
            Thread.sleep(20 * 1000);
            redisLock.unLock();
        }
    }

    @Test
    public void test4() throws InterruptedException {
        try (RedisLock simpleRedisLock = new ReentrantRedisLock("test:test")) {
            boolean b;
            b = simpleRedisLock.tryLock(10, 10, TimeUnit.SECONDS);
            System.out.println("first lock result:" + b);
            Thread.sleep(5 * 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test5() {
        try (RedisLock redisLock = SecondLevelRedisLock.secondLevelLock("test:test", "child")) {
            if (redisLock.tryLock(100, 20, TimeUnit.SECONDS)) {
                System.out.println("first lock:success");
                Thread.sleep(1000);
                try (RedisLock redisLockChild = SecondLevelRedisLock.secondLevelLock("test:test", "child2")) {
                    boolean b = redisLockChild.tryLock(5, 20, TimeUnit.SECONDS);
                    System.out.println("second lock result:" + b);
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void rateLimit() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        RateLimiter rateLimiter = new RateLimiter("test", 10, 10);
        CountDownLatch countDownLatch = new CountDownLatch(10);
        for (int i = 0; i < 10; i++) {
            executorService.submit(() -> {
                System.out.println(rateLimiter.tryAcquire(4, 2, TimeUnit.SECONDS));
                countDownLatch.countDown();
            });
        }
        countDownLatch.await();
    }

    @Test
    public void multiPathLock() {
        ArrayList<String> paths = new ArrayList<>();
        paths.add("test");
        paths.add("test1");
        ArrayList<String> paths2 = new ArrayList<>();
        paths2.add("test2");
        paths2.add("test4");
        try (RedisLock redisLock = new MultiPathRedisLock("test:test", paths)) {
            if (redisLock.tryLock(100, 20, TimeUnit.SECONDS)) {
                System.out.println("first lock:success");
                Thread.sleep(1000);
                try (RedisLock redisLockChild = new MultiPathRedisLock("test:test", paths2)) {
                    boolean b = redisLockChild.tryLock(5, 20, TimeUnit.SECONDS);
                    System.out.println("second lock result:" + b);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testForRegistrar() {
        MyBeanRegistrar bean = SpringUtils.getBean(MyBeanRegistrar.BEAN_NAME, MyBeanRegistrar.class);
        System.out.println(bean.getProperty());
        System.out.println(bean.getRedisTemplate());
    }
}
