import com.cheney.javaconfig.spring.BeanConfig;
import com.cheney.javaconfig.spring.RootConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * 单元测试类
 */
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {RootConfig.class})
@ActiveProfiles("test")
public class TestJunits {

    @PersistenceContext
    public EntityManager manager;

    @Resource(name = "profilesBean")
    private String profile;

    @Test
    public void test() {
//        Object[] result = (Object[]) manager.createNativeQuery("select * from auth_user where id = 1").getSingleResult();
//        System.out.println(result[0]);
        System.out.println(profile);
    }
}
