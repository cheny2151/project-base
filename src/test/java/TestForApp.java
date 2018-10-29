import com.cheney.app.ApplicationContext;
import com.cheney.redis.StrRedisClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Base64Utils;
import sun.misc.BASE64Encoder;

import javax.annotation.Resource;

//@RunWith(SpringRunner.class)
//@SpringBootTest(classes = ApplicationContext.class)
public class TestForApp {

    @Resource(name = "strRedisClient")
    private StrRedisClient redisClient;

    @Test
    public void test() {
        redisClient.setValue("test", "success");
        System.out.println(redisClient.getValue("test"));
    }

    @Test
    public void test2() {
        byte[] encode = Base64Utils.encode("tradeAdmin:pw123456".getBytes());
        System.out.println(new String(encode));
        byte[] decode = Base64Utils.decodeFromString(new String(encode));
        System.out.println(new String(decode));
    }

}
