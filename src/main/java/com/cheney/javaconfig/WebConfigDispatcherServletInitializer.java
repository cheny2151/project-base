package com.cheney.javaconfig;

import com.cheney.javaconfig.security.WebSecurityConfig;
import com.cheney.javaconfig.spring.RootConfig;
import com.cheney.javaconfig.springMVC.WebServletConfig;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import javax.servlet.*;

/**
 * servlet容器配置 (代替web.xml)
 */
public class WebConfigDispatcherServletInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        //root context
        AnnotationConfigWebApplicationContext rootContext =
                new AnnotationConfigWebApplicationContext();
        rootContext.register(RootConfig.class);
        //default profile
        rootContext.getEnvironment().setDefaultProfiles("dev");
        servletContext.addListener(new ContextLoaderListener(rootContext));

        //mvc context
        AnnotationConfigWebApplicationContext webContext = new AnnotationConfigWebApplicationContext();
        webContext.register(WebServletConfig.class);
        webContext.setServletContext(servletContext);

        DispatcherServlet ds = new DispatcherServlet(webContext);
        ds.setThrowExceptionIfNoHandlerFound(true);
        ServletRegistration.Dynamic servlet = servletContext.addServlet("cherry", ds);
        servlet.addMapping("/");
//        servlet.addMapping("*.service");
        servlet.setLoadOnStartup(1);

        //order Listener
        //RequestContextListener
        servletContext.addListener(RequestContextListener.class);
    }

    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class[]{RootConfig.class,WebSecurityConfig.class};
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class[]{WebServletConfig.class};
    }

    @Override
    protected String[] getServletMappings() {
        return new String[]{"/"};
    }

    @Override
    protected Filter[] getServletFilters() {
        CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
        characterEncodingFilter.setEncoding("utf-8");
        characterEncodingFilter.setForceEncoding(true);
        return new Filter[]{characterEncodingFilter};
    }

    @Override
    protected void customizeRegistration(ServletRegistration.Dynamic registration) {
        registration.setInitParameter("throwExceptionIfNoHandlerFound", "true");
    }

}
