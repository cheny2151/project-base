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
        ArrayList<Integer> integers = new ArrayList<>();
        integers.add(1);
        integers.add(2);
        List<Admin> id = adminMapper.findList(FilterFactory.create(FilterFactory.in("id",integers)),null);
        System.out.println(id);
    }

    @Test
    public void test2() {
        Pageable pageable = new Pageable();
        pageable.setFilters(FilterFactory.create(FilterFactory.le("createDate",new Date()),FilterFactory.like("username","test"),FilterFactory.eq("username","test1")));
        Page<Admin> page = adminMapper.findPage(pageable);
        System.out.println(page.getPageNumber());
        System.out.println(page.getPageSize());
        System.out.println(page.getTotal());
        System.out.println(page.getContent());
    }

}
