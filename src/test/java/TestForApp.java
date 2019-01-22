import com.cheney.app.ApplicationContext;
import com.cheney.redis.client.StrRedisClient;
import com.cheney.redis.lock.SimpleRedisLock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Base64Utils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ApplicationContext.class)
public class TestForApp {

    @Resource(name = "strRedisClient")
    private StrRedisClient redisClient;

    @Test
    public void test() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        ArrayList<Callable<String>> threads = new ArrayList<>();
        for (int i = 0; i <= 10; i++) {
            threads.add(() -> {
                try (SimpleRedisLock lock = new SimpleRedisLock("test")) {
                    lock.tryLock(6, 5, TimeUnit.SECONDS);
                    Thread.sleep(500);
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
//        SimpleRedisLockForLettuce simpleRedisLock = new SimpleRedisLockForLettuce("test:test");
        SimpleRedisLock simpleRedisLock = new SimpleRedisLock("test:test");
        try {
            boolean b;
            b = simpleRedisLock.tryLock(100, 10, TimeUnit.SECONDS);
            System.out.println("first lock result:" + b);
            Thread.sleep(5 * 1000);
            b = simpleRedisLock.tryLock(100, 20, TimeUnit.SECONDS);
            System.out.println("second lock result:" + b);
            Thread.sleep(5 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            simpleRedisLock.unLock();
            Thread.sleep(20 * 1000);
            simpleRedisLock.unLock();
        }
    }

    @Test
    public void test4() throws InterruptedException {
//        SimpleRedisLockForLettuce simpleRedisLock = new SimpleRedisLockForLettuce("test:test");
        SimpleRedisLock simpleRedisLock = new SimpleRedisLock("test:test");
        try {
            boolean b;
            b = simpleRedisLock.tryLock(1000, 10, TimeUnit.SECONDS);
            System.out.println("first lock result:" + b);
            Thread.sleep(5 * 1000);
//            b = simpleRedisLock.tryLock(100, 20, TimeUnit.SECONDS);
//            System.out.println("second lock result:" + b);
            Thread.sleep(5 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            simpleRedisLock.unLock();
            Thread.sleep(20 * 1000);
            simpleRedisLock.unLock();
        }
    }


}
