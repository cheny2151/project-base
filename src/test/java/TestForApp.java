import com.cheney.ApplicationContext;
import com.cheney.redis.lock.RedisLock;
import com.cheney.redis.lock.awaken.AwakenRedisLock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Base64Utils;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ApplicationContext.class)
public class TestForApp {

    @Test
    public void test() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        ArrayList<Callable<String>> threads = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            threads.add(() -> {
                try (RedisLock lock = new AwakenRedisLock("test")) {
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
        RedisLock redisLock = new AwakenRedisLock("test:test");
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
        try (RedisLock simpleRedisLock = new AwakenRedisLock("test:test")) {
            boolean b;
            b = simpleRedisLock.tryLock(10, 10, TimeUnit.SECONDS);
            System.out.println("first lock result:" + b);
            Thread.sleep(5 * 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
