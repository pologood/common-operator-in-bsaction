package com.gomeplus.bs.service.lolengine.timeline;



import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.gomeplus.bs.interfaces.lol.entity.PublishContent;
import com.gomeplus.bs.interfaces.lol.vo.mq.PraiseMqVo;
import com.gomeplus.bs.service.lolengine.LOLEngineApplication;
import com.gomeplus.bs.service.lolengine.publishcontent.dao.PublishContentDao;
import com.gomeplus.bs.service.lolengine.rabbitmq.analysis.PraiseMqAnalysis;
import com.gomeplus.bs.service.lolengine.timeline.dao.TimeLineDao;

/**
 *
 * @author yanyuyu
 * @date 2016年12月23日
 */
@SuppressWarnings("deprecation")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = LOLEngineApplication.class)
public class TimeLineTest {

    @Autowired
    private TimeLineDao timeLineDao;

    @Autowired
    private PublishContentDao publishContentDao;

    @Autowired
    private PraiseMqAnalysis praiseMqAnalysis;

    @Test
    public void update() {


        PraiseMqVo praiseMqVo = new PraiseMqVo();
        praiseMqVo.setId("372");
        praiseMqVo.setIsDelete(false);
        praiseMqVo.setCreateTime(new Date());
        praiseMqVo.setUpdateTime(new Date());
        praiseMqVo.setUserId(1177L);
        praiseMqVo.setIsFirst(true);
        praiseMqVo.setEntryId("13");
        praiseMqVo.setEntryUserId(1177L);

        praiseMqAnalysis.dealPraiseEvent("CREATE", praiseMqVo);
    }

    @Test
    public void add() {
        Long timeLineOwner = 9528L;
        // Query query = new Query(Criteria.where("isPublic").is(true).and("isDelete").is(false));
        // List<PublishContent> contentList = publishContentDao.findAll(query);
        // TimeLine line = null;
        // for (PublishContent content : contentList) {
        // Date current = new Date();
        // line = new TimeLine();
        // line.setCreateTime(current);
        // line.setEntryCreateTime(content.getCreateTime());
        // line.setEntryId(content.getId());
        // line.setFriendId(content.getUserId());
        // line.setId(String.valueOf(timeLineDao.getNewId(Constants.CollectionName.TIME_LINE)));
        // line.setState(0);
        // line.setUpdateTime(current);
        // line.setUserId(timeLineOwner);
        //
        // timeLineDao.save(line);
        //
        // Long userId = 9528L;
        // AggregationOperation match = Aggregation.match(Criteria.where("userId").is(userId));
        // AggregationOperation group = Aggregation.group("userId").count().as("count");
        // // Aggregation.project(fields)
        //
        // Aggregation agg = Aggregation.newAggregation(match, group);
        // AggregationResults<OutputObject> result = timeLineDao.aggregate(agg);
        //
        // List<OutputObject> list = result.getMappedResults();
        //
        // System.out.println(list);
        Map<String, String> map = new HashMap<String, String>();
        generateMap(map);

        System.out.println("123");

    }

    /**
     * @Description TODO
     * @author mojianli
     * @date 2017年2月7日 下午6:39:33
     * @param map
     */
    private void generateMap(Map<String, String> entryContentMap) {
        String entryContent = "";
        String entryImg = "";

        PublishContent publishContent = publishContentDao.findById("5");
        Object[] components = publishContent.getComponents();
        if (null != components) {
            for (int i = 0; i < components.length; i++) {
                LinkedHashMap<String, Object> map = (LinkedHashMap<String, Object>) components[i];
                if (null != map && map.containsKey("type")) {
                    String type = String.valueOf(map.get("type"));
                    // 判断话题包含图片、商品或者视频
                    if ("image".equals(type) && StringUtils.isBlank(entryImg)) {
                        entryImg = String.valueOf(map.get("url"));
                    } else if ("text".equals(type) && StringUtils.isBlank(entryContent)) {
                        entryContent = String.valueOf(map.get("text"));
                    }
                }
            }
        }
        entryContentMap.put("entryContent", entryContent);
        entryContentMap.put("entryImg", entryImg);

    }
}


