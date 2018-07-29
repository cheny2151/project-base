import com.cheney.dao.mybatis.AdminMapper;
import com.cheney.dao.mybatis.UserMapper;
import com.cheney.entity.dto.Admin;
import com.cheney.entity.dto.AuthUser;
import com.cheney.javaconfig.spring.RootConfig;
import com.cheney.system.filter.FilterFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

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

    @Resource(name = "adminMapper")
    private AdminMapper adminMapper;

    @Resource(name = "profilesBean")
    private String profile;

    @Test
    public void test() {
        List<Admin> id = adminMapper.findList(FilterFactory.createFilterList(FilterFactory.isNotNull("id")));
        System.out.println(id);
    }

    @Test
    public void test2() {
        List<Admin> all = adminMapper.findAll();
        System.out.println(all);
    }

}
