package com.hotely5d.config;



import com.hotely5d.interceptor.JWTInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MyWebAppConfigurer implements WebMvcConfigurer {
    @Value("${upload.path}")
    private String uploadPath;//文件保存位置
    @Value("${upload.staticAccessPath}")
    private String staticAccessPath;//

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {//静态资源放行
        registry.addResourceHandler(staticAccessPath).addResourceLocations(uploadPath);
        registry.addResourceHandler(staticAccessPath).addResourceLocations(uploadPath);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new JWTInterceptor())//注册jwt拦截器
                .addPathPatterns("/**") //拦截
                .excludePathPatterns(//放行
                        "/system/login"
                        ,"/system/register"
                        ,"/images/**"
                        ,"/notice/**"
                        ,"/message/**"
                        ,"/room/**"
                        ,"/category/**"
                        ,"/file/**"
                        ,"/system/getHotel",
                        "/system/adminRegister",
                        "/admin/addHotel");
    }

}

