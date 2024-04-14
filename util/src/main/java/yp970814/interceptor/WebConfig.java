package yp970814.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Author yuanping970814@163.com
 * @Date 2024-04-14 11:43
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private UserTokenInterceptor userTokenInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(this.userTokenInterceptor)/*.excludePathPatterns(swaggerExcludes)*/.addPathPatterns("/apis/**");
    }

}
