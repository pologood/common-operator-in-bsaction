# dubbor使用

---

[TOC]


## 一、快速起步

### 1.1 maven 依赖 
```
<!--parent，指定springboot版本号等-->
<parent>
	<groupId>com.gomeplus</groupId>
	<artifactId>bs-spring-boot-parent</artifactId>
	<version>1.0-SNAPSHOT</version>
</parent>
<properties>
    <start-class>com.gomeplus.bs.service.xxx.xxxApplication</start-class>
</properties>
<dependencies>
    <!--1.框架依赖-->
    <dependency>
        <groupId>com.gomeplus</groupId>
        <artifactId>bs-framework-dubbor</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>
    <!--2.api-->
    <dependency>
        <groupId>com.gomeplus</groupId>
        <artifactId>bs-interface-xxxx</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>
</dependencies>  

<!--插件依赖-->
<build>
        <plugins>
            <plugin>
                <groupId>org.codehaus.mojo</groupId>
                <artifactId>appassembler-maven-plugin</artifactId>
            </plugin>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
</build>

```
### 1.2 建包

包名由maven的groupId+artifactId   
比如：  

`com.gomeplus.bs.service.permission`

### 1.3 启动函数

```
@SpringBootApplication
@ImportResource("config/spring-boot.xml")//固定名
@ComponentScan("com.gomeplus.bs") //固定顶级包名
public class PermissionApplication { // 启动函数名可根据业务命名:xxxApplication
    public static void main(String[] args) {

        long starTime = System.currentTimeMillis();
        SpringApplication.run(PermissionApplication.class,args);
        long endTime=System.currentTimeMillis();
        long Time=endTime-starTime;
        System.out.println("\n启动时间:"+ Time/1000 +"秒");
        System.out.println("...............................................................");
        System.out.println("..................Service starts successfully..................");
        System.out.println("...............................................................");

    }
}
```
### 1.4 配置文件

在`src/main/resouces`下新建`application.properties`
用于配置环境无关且不变配置
```
spring.config.name=bs-service-permission
server.port = 8586
dubbo.port=41001
spring.profiles.active=dev
```

在`src/main/resouces`下新建`application-dev.properties`
用于配置环境相关配置：mysql，redis等，例如:
```
# redis
spring.redis.password=gome123456
spring.redis.pool.max-active=20
spring.redis.pool.max-idle=8
spring.redis.cluster.nodes = 10.125.2.36:7000,10.125.2.36:7001,10.125.2.36:7002,10.125.2.36:7003,10.125.2.36:7004,10.125.2.36:7005

# zookeeper
zookeeperAddress=10.125.31.218:2181
```

继续在目录下新建3个配置：  
* `logback.xml`:日志配置；  
* `config/spring-boot.xml`：import其他配置文件  
* `config/dubbo-provider.xml`：dubbo提供者配置  

dubbo-provider.xml示例：
```
 <dubbo:application name="${spring.config.name}" />
<dubbo:registry id="central" address="${dubboRegistry}" protocol="zookeeper"/>
<dubbo:protocol serialization="nativejava" name="dubbo" port="${dubbo.port}"
                threads="${dubboThreads}" heartbeat="${dubboHeartBeats}"/>

<dubbo:provider filter="-exception" />
```

### 1.5 启动
1.3 的启动函数右键即可启动，成功后；浏览器访问:`localhost:port/health`,如有数据，说明搭建成功；如果debug启动，IDE会自动加载被修改的文件，不需要再重启；

## 二、第一个dubbo+RESTful接口
### 2.1 定接口
示例：`user资源`  
```
public interface UserResource {
    public UserVo doGet(Long id) throws Exception;
}
```

1. 接口名：资源(名词)Resource
2. 方法名：doGet表示`@GET`，不建议命名为getUser
3. 异常：如果不知道实现具体会抛何种异常，可以throws Exception

### 2.2  接口实现
#### 2.2.1 RESTfull
```
@RestController("userResource")
@RequestMapping("/permission/user")
public class UserResouceImpl implements UserResource{
    
    @RequestMapping(method = GET)
	@Override
	public UserVo doGet(@RequestParam Long id) throws Exception {
        ....	
	}
}
```
1. 实现接口
2. @RestController("userResource")：rest注解，并指定bean的名字（接口名第一个字母小写）
3. @RequestMapping：指定mapping路径。一般为两级，`模块/资源`
4. @RequestMapping(method = GET) ：表示GET请求，也可以用`@GetMapping`
5. 参数绑定：@RequestParam，@RequestBody，@PublicParam(name = "userID")获取公参，不写注解默认匹配URL参数
6. 异常：REST状态码（异常）

```  
throw new C422Exception(RESTfulExceptionConstant.CHECK_REQUEST_PARAM_FAILED_MSG).debugMessage("模块名"+module+"格式不正确");  

```

这个异常的返回：
```
httpcode:422
body:
{
    "message": "参数校验失败",
    "data":{},
    "debug":"模块名admin格式不正确"
}
```
7. error节点

```
throw new C422Exception("参数校验失败").debugMessage("模块名"+module+"格式不正确").error(object); 
```
`object`会作为error节点的数据；

#### 2.2.2 dubbo 实现
2.2.1 中作为restfull的实现，也是dubbo的实现，现在，只需要在`dubbo-provider.xml`中配置如：
```
<dubbo:service  registry="central" retries="0" interface="com.gomeplus.bs.service.xxx.service.UserResource" ref="userResource"/>
```
配置好启动后，可以通过dubbo-admin查看是否注册到zk

## 三、集成mybatis+mysql 
### 3.1 maven
```
 <!--4.mybatis-->
<dependency>
    <groupId>org.mybatis.spring.boot</groupId>
    <artifactId>mybatis-spring-boot-starter</artifactId>
    <version>1.1.1</version>
</dependency>

<!-- jdbc -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-jdbc</artifactId>
</dependency>
```

### 3.2 配置数据源指定mybatis配置
在`application.properties`中添如下配置：
```
# mysql
spring.datasource.url=${spring.datasource.xxx.url}
spring.datasource.username=${spring.datasource.xxx.username}
spring.datasource.password=${spring.datasource.xxx.password}

# mybatis
mybatis.config-location=classpath:mybatis/mybatis-config.xml
```
在`application-dev.properties`中添如下配置：
```
# recharge-mysql
spring.datasource.xxx.url=jdbc:mysql://bj01-ops-mysm01.dev.gomeplus.com:3306/bs-xxxx?useUnicode=true&characterEncoding=UTF8
spring.datasource.xxx.username=develop
spring.datasource.xxx.password=ZQ2yGUJfE2
```
其他mybatis配置不变，可参考以往项目

### 3.3 使用sqlSession

在数据访问层注入即可：
```
@Autowired
private SqlSession sqlSession;
```
以使用sqlSession、SqlSessionTemplate等。详细可以参考spring-mybatis整合使用。

## 四、集成redis
### 4.1 maven依赖
```
<dependency>
    <groupId>com.gomeplus</groupId>
    <artifactId>bs-common-dubbor-redis</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

### 4.2 配置
在application.xml配置
```
# redis
spring.redis.password=gome123456
spring.redis.pool.max-active=20
spring.redis.pool.max-idle=8
spring.redis.cluster.nodes = 10.125.2.36:7000,10.125.2.36:7001,10.125.2.36:7002,10.125.2.36:7003,10.125.2.36:7004,10.125.2.36:7005
```
### 4.3 使用
注入
```
@Autowired
private StringRedisTemplate stringRedisTemplate;

```
详细使用参考spring-data-redis;

## 五、包命名规范

### 5.1 项目名
* bs-service-xxx
* bs-interfaces-xxx
* bs-common-xxx
* bs-framework-xxx
* bs-{api|admin}-web-xxx

### 5.2 maven
`groupId`:com.gomeplus  
`artifactId`:和项目名一致

### 5.3 包

`groupId+artifactId ('-' 转 '.')`

## 六 、静态文件
如果需要支持html等静态文件，可以在resouce目录下新建public或者dist,把静态文件放入即可；

如果需要指定welcome-file,需要在配置文件中添加如下配置:

    dubbor.web.welcome-file= app/index.html