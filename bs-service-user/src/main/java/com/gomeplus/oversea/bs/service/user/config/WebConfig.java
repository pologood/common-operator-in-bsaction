package com.gomeplus.oversea.bs.service.user.config;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class WebConfig extends WebMvcConfigurerAdapter {
	 @Override
	    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
	        converters.add(createFastJsonHttpMessageConverter());
	        super.configureMessageConverters(converters);
	    }
	   private MappingJackson2HttpMessageConverter createFastJsonHttpMessageConverter() {
	        MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
	        ObjectMapper objectMapper = new ObjectMapper();
	        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
	        jsonConverter.setObjectMapper(objectMapper);
	        return jsonConverter;
	    }
}