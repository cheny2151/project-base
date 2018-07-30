import com.cheney.dao.mybatis.AdminMapper;
import com.cheney.dao.mybatis.UserMapper;
import com.cheney.entity.dto.Admin;
import com.cheney.javaconfig.spring.RootConfig;
import com.cheney.system.filter.FilterFactory;
import com.cheney.system.page.Page;
import com.cheney.system.page.Pageable;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.annotation.Resource;
import java.util.ArrayList;
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
        ArrayList<Integer> integers = new ArrayList<>();
        integers.add(1);
        integers.add(2);
        List<Admin> id = adminMapper.findList(FilterFactory.create(FilterFactory.notLike("username", "test1")));
        System.out.println(id);
    }

    @Test
    public void test2() {
        Page<Admin> page = adminMapper.findPage(new Pageable());
        System.out.println(page);
    }

}
