package com.gomeplus.bs.framework.dubbor.configs;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gomeplus.bs.framework.dubbor.annotations.resolvers.PublicParamResolver;
import com.gomeplus.bs.framework.dubbor.filter.CommonFilter;
import com.gomeplus.bs.framework.dubbor.interceptor.CommonInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.StringUtils;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>mvc适配器</p>
 * @author zhaozhou
 * @version 1.0
 * @since 1.0
 */
@Configuration
public class WebConfig extends WebMvcConfigurerAdapter {

    private static final String[] CLASSPATH_RESOURCE_LOCATIONS = {
            "classpath:/dist/", "classpath:/public/" };

    @Value("${dubbor.web.welcome-file:index.html}")
    private String welcomeFile;

    @Autowired
    private PublicParamResolver publicParamResolver;

    @Autowired
    private CommonInterceptor commonInterceptor;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(publicParamResolver);
    }

    //    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(commonInterceptor);
//    }
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        if (!registry.hasMappingForPattern("/webjars/**")) {
            registry.addResourceHandler("/webjars/**").addResourceLocations(
                    "classpath:/META-INF/resources/webjars/");
        }
        if (!registry.hasMappingForPattern("/**")) {
            registry.addResourceHandler("/**").addResourceLocations(
                    CLASSPATH_RESOURCE_LOCATIONS);
        }
    }
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(createFastJsonHttpMessageConverter());
        super.configureMessageConverters(converters);
    }

    /**
     *jackson个性化配置
     * @return MappingJackson2HttpMessageConverter
     */
    private MappingJackson2HttpMessageConverter createFastJsonHttpMessageConverter() {
        MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        jsonConverter.setObjectMapper(objectMapper);
        return jsonConverter;
    }
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        if(!StringUtils.isEmpty(welcomeFile) || !"index.html".equals(welcomeFile)){
            registry.addViewController("/").setViewName("forward:"+welcomeFile);
            registry.setOrder( Ordered.HIGHEST_PRECEDENCE );
        }
        super.addViewControllers( registry );
    }

    @Bean
    public FilterRegistrationBean filterRegistrationBean(){
        CommonFilter commonFilter=new CommonFilter();
        FilterRegistrationBean registrationBean=new FilterRegistrationBean();
        registrationBean.setFilter(commonFilter);
        registrationBean.setEnabled(true);
        List<String> urlPatterns=new ArrayList<String>();
        urlPatterns.add("/*");//拦截路径，可以添加多个
        registrationBean.setUrlPatterns(urlPatterns);
        registrationBean.setOrder(1);
        return registrationBean;
    }
}
