package com.gomeplus.oversea.bs.service.user.client;
import com.gomeplus.oversea.bs.service.user.dto.user.UserIdDto;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by baixiangzhu on 2017/2/21.
 */
@FeignClient(name = "bs-service-userid-generator")
public interface UserIdClient {

    /**
     * 获取用户ID
     * @return
     */
    @RequestMapping("userId/next")
    UserIdDto getUserId();

}
