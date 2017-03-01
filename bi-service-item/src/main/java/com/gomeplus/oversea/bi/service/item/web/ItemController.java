package com.gomeplus.oversea.bi.service.item.web;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gomeplus.oversea.bi.service.item.entity.Item;
import com.gomeplus.oversea.bi.service.item.service.ItemReadService;
import com.gomeplus.oversea.bi.service.item.service.ItemWriteService;
import com.gomeplus.oversea.bi.service.item.utils.Paging;
import com.gomeplus.oversea.bi.service.item.vo.ItemDetailVo;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;

/**
 * 2017/2/13
 * 商品API
 */
@RestController
@Slf4j
@RequestMapping("/item")
public class ItemController {

    @Autowired
    private ItemWriteService itemWriteService;
    @Autowired
    private ItemReadService itemReadService;
    
    /**
     * 商品信息接口
     * @param id 商品主键
     * @return
     */
    @HystrixCommand (commandProperties = {@HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "3000")},
    				threadPoolProperties = {@HystrixProperty (name = "coreSize", value = "100")})
    @RequestMapping(value="/item", method = RequestMethod.GET)
    public ItemDetailVo findItemDetailById(@RequestParam(required=true) String id) {
        return itemReadService.findItemDetailById(id);
    }
    
    @RequestMapping(value="/item", method = RequestMethod.PUT)
    public Boolean updateItem(@RequestBody Item item) {
    	log.info("Update item:{}", item);
    	return itemWriteService.update(item);
    }
    
    @RequestMapping(value="/items/search", method = RequestMethod.GET)
    public Paging<Item> findItemsForSearch(Integer status, Integer pageNum, Integer pageSize) {
    	return itemReadService.findItemsForSearch(status, pageNum, pageSize);
    }
    
}
