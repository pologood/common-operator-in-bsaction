package com.gomeplus.oversea.bi.service.item.conf;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * rabbitMQ配置
 * 2017/2/14
 */
@RefreshScope
@Configuration
@EnableRabbit
public class RabbitMQConfig {  
	@Value("${spring.rabbitmq.host}")  
    private String host;  
      
    @Value("${spring.rabbitmq.port}")  
    private String port;  
      
    @Value("${spring.rabbitmq.username}")  
    private String username;  
    
    @Value("${spring.rabbitmq.password}")  
    private String password;  
    
    @Value("${spring.rabbitmq.virtualHost}")  
    private String virtualHost;  
    /**
     * 配置链接信息
     * @return
     */
    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(host, Integer.valueOf(port));

        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        connectionFactory.setVirtualHost(virtualHost);
        connectionFactory.setPublisherConfirms(true);
        return connectionFactory;
    }
    
    @Bean  
    public AmqpAdmin amqpAdmin() {  
        return new RabbitAdmin(connectionFactory());  
    }  

}
