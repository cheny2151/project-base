import com.cheney.javaconfig.spring.RootConfig;
import com.cheney.service.UserService;
import com.cheney.system.order.OrderFactory;
import com.cheney.system.page.Pageable;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.annotation.Resource;

/**
 * 单元测试类
 */
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {RootConfig.class})
@ActiveProfiles("dev")
public class TestJunits {

    @Resource(name = "userServiceImpl")
    private UserService userService;

    @Resource(name = "profilesBean")
    private String profile;

    @Test
    public void test() {
        Pageable pageable = new Pageable();
        pageable.getOrders().add(OrderFactory.asc("createDate"));
        System.out.println(userService.findPage(pageable).getContent().size());
    }
}
