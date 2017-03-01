package com.gomeplus.bs.service.lol.mongo;

import java.util.List;

import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.gomeplus.bs.service.lol.mongo.entity.PageBean;
import com.gomeplus.bs.service.lol.mongo.entity.PageParam;



/**
 * mongo dao基本操作
 */
public interface BaseMongoDao<T> {
	
	/**
	 * 
	 * @Description 通过条件查询实体(集合)
	 * @return
	 */
	public List<T> findAllObjects();
	/**
	 * @Description 查询所有实体
	 * @param query
	 * @return
	 */
	public List<T> findAll(Query query);
	/**
	 * 
	 * @Description 通过一定的条件查询一个实体  
	 * @param query
	 * @return
	 */
	public T findOne(Query query);  
	/**
	 * @Description 通过条件查询更新数据 
	 * @param query
	 * @param update
	 * @return 实体
	 */
	public T update(Query query, Update update); 
	/**
	 * @Description 通过条件查询更新数据(更新多个)
	 * @param query
	 * @param update
	 * @return true?false
	 */
	public void updateMulti(Query query, Update update);
	/**
	 * @Description 保存一个对象到mongodb  
	 * @param entity
	 * @return
	 */
    public T save(T entity) ; 
    /**
     * @Description 通过ID获取记录 
     * @param id
     * @return
     */
    public T findById(String id);  
    
    /**
     * @Description 查找并且删除
     */
    public T findAndRemove(Query query);
    /**
     * @Description 通过ID获取记录,并且指定了集合名(表的意思) 
     * @param id
     * @param collectionName
     * @return
     */
    public T findById(String id, String collectionName); 
    /**
     * @Description 分页查询 
     * @param page
     * @param query
     * @return
     */
    public List<T> findPage(PageParam page,Query query);
    /**
     * @Description 分页查询 
     * @param page 分页参数
     * @param query 查询条件
     * @return
     */
    public PageBean findPageForPageBean(PageParam page,Query query);  
    /**
     * @Description 创建映射
     */
    public void createCollection();
    /**
     * @Description 删除映射
     */
    public void dropCollection();
    /**
     * 
     * @Description 求数据总和  
     * @param query
     * @return
     */
    public int count(Query query);  
    
    /**
     * @Description 查找并且删除--批量
     */
    public List<T> findAllAndRemove(Query query);
    
    /**
     * @Description 得到mongo collection上的自增ID 
     * @author yanyuyu
     * @date 2016年12月15日 下午5:18:27
     * @param collectionName
     * @return
     */
	public String getNewId(String collectionName);
    
    /**
     * @Description 根据id更新 
     * @param query
     * @param update
     * @return
     */
    public T updateById(String id, Update update);
}
