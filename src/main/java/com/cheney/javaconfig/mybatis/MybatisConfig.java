package com.cheney.javaconfig.mybatis;

import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;
import java.io.IOException;

/**
 * mybatis配置
 * 参考 ：https://www.jianshu.com/p/8fcefc4d724b
 */
//@Configuration
@MapperScan(basePackages = "com.cheney.dao.mDao", sqlSessionTemplateRef = "sqlSessionTemplate") //注解形式扫描dao接口
public class MybatisConfig {

    /**
     * 创建mapper.xml 与dao接口对应的代理工厂
     *
     * @param dataSource 数据库源
     * @return SqlSessionFactoryBean
     * @throws IOException
     */
    @Autowired
    @Bean(name = "sqlSessionFactory")
    public SqlSessionFactoryBean sqlSessionFactory(DataSource dataSource) throws IOException {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);
        sqlSessionFactoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mybatis/*.xml"));
        return sqlSessionFactoryBean;
    }

    @Autowired
    @Bean(name = "sqlSessionTemplate")
    public SqlSessionTemplate sqlSessionTemplate(DataSource dataSource) throws Exception {
        SqlSessionTemplate sqlSessionTemplate = new SqlSessionTemplate(sqlSessionFactory(dataSource).getObject());
        return sqlSessionTemplate;
    }

    /**
     * 配置方式 扫描dao接口
     */
   /* @Bean(name = "mapperScannerConfigurer")
    public MapperScannerConfigurer mapperScannerConfigurer(){
        MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
        mapperScannerConfigurer.setSqlSessionFactoryBeanName("sqlSessionFactory");
        mapperScannerConfigurer.setBasePackage("com.cheney.dao.mDao");
        return mapperScannerConfigurer;
    }*/

}
