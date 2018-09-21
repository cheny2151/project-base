import com.cheney.app.ApplicationContext;
import com.cheney.redis.StrRedisClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ApplicationContext.class)
public class TestForApp {

    @Resource(name = "strRedisClient")
    private StrRedisClient redisClient;

    @Test
    public void test() {
        redisClient.setValue("test", "success");
        System.out.println(redisClient.getValue("test"));
    }

}
