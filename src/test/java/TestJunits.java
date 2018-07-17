import com.cheney.dao.UserMapper;
import com.cheney.entity.dto.AuthUser;
import com.cheney.javaconfig.spring.RootConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 单元测试类
 */
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {RootConfig.class})
@ActiveProfiles("dev")
public class TestJunits {

    @Resource(name = "userMapper")
    private UserMapper userMapper;

    @Resource(name = "profilesBean")
    private String profile;

    @Test
    public void test() {
        AuthUser authUser = new AuthUser();
        authUser.setUsername("test1234");
        authUser.setLastPasswordReset(new Date());
        authUser.setPassword("13254khasgfkh");
        authUser.setOriginId(12323L);
        userMapper.persist(authUser);
    }
}
