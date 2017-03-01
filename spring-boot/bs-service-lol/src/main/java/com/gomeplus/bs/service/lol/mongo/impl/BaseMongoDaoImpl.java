package com.gomeplus.bs.service.lol.mongo.impl;
import java.util.List;

import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.gomeplus.bs.interfaces.lol.entity.IncIds;
import com.gomeplus.bs.service.lol.mongo.BaseMongoDao;
import com.gomeplus.bs.service.lol.mongo.entity.PageBean;
import com.gomeplus.bs.service.lol.mongo.entity.PageParam;
import com.gomeplus.bs.service.lol.util.ReflectUtil;



/**
 * 
 * @Description Mongo 底层实现类
 * @param <T>
 */
public abstract class BaseMongoDaoImpl<T> implements BaseMongoDao<T> {
	
	
    protected MongoTemplate mongoTemplate; 
	
	/**
	 * @Description 通过条件查询实体(集合)
	 * @return
	 */
	@Override
	public List<T> findAllObjects() {
		 return (List<T>) mongoTemplate.findAll(this.getEntityClass());  
	}
	
	/**
	 * @Description 查询所有实体
	 * @param query
	 * @return
	 */
	@Override
	public List<T> findAll(Query query) {
		return (List<T>) mongoTemplate.find(query,this.getEntityClass());  
	}
	
	/**
	 * @Description 通过一定的条件查询一个实体  
	 * @param query
	 * @return
	 */
	@Override
	public T findOne(Query query) {
		return (T) mongoTemplate.findOne(query,this.getEntityClass());
	}
	
	/**
	 * @Description 通过条件查询更新数据 
	 * @param query
	 * @param update
	 */
	@Override
	public T update(Query query, Update update) {
		return mongoTemplate.findAndModify(query, update, this.getEntityClass()); 
	}
	
	@Override
	public T updateById(String id, Update update) {
		return mongoTemplate.findAndModify(new Query(Criteria.where("id").is(id)), update, this.getEntityClass());
	}
	
	/**
	 * @Description 通过条件查询更新数据 
	 * @param query
	 * @param update
	 */
	@Override
	public void updateMulti(Query query, Update update) {
		mongoTemplate.updateMulti(query, update, this.getEntityClass());
	}
	
	/**
	 * @Description 保存一个对象到mongodb  
	 * @param entity
	 * @return
	 */
	@Override
	public T save(T entity) {
		mongoTemplate.insert(entity);
		return entity;
	}
	
    /**
     * @Description 通过ID获取记录 
     * @param id
     * @return
     */
	@Override
	public T findById(String id) {
		 return mongoTemplate.findById(id, this.getEntityClass());  
	}
		
	/**
	 * @Description 删除
	 */
	public T findAndRemove(Query query){
		return this.mongoTemplate.findAndRemove(query, this.getEntityClass());
	}
	
    /**
     * @Description 通过ID获取记录,并且指定了集合名(表的意思) 
     * @param id
     * @param collectionName
     * @return
     */
	@Override
	public T findById(String id, String collectionName) {
		 return mongoTemplate.findById(id, this.getEntityClass(), collectionName);  
	}
	
    /**
     * @Description 分页查询 
     * @param page
     * @param query
     * @return
     */
	@Override
	public List<T> findPage(PageParam page, Query query) {
        int pageNumber = page.getPageNum();
        int pageSize = page.getNumPerPage();
        return this.findAll( query.skip((pageNumber - 1) * pageSize).limit(pageSize));
	}
	
	 /**
     * @Description 分页查询 
     * @param page 分页参数
     * @param query 查询条件
     * @return
     */
    @Override
    public PageBean findPageForPageBean(PageParam page, Query query) {
        PageBean pageBean=new PageBean();
        //获取总数
        pageBean.setTotalCount(count(query));
        // 当前页数
        int pageNumber = page.getPageNum();
        // 每页记录数
        int pageSize = page.getNumPerPage();
        pageBean.setCurrentPage(pageNumber);
        pageBean.setNumPerPage(pageSize);
        pageBean.setRecordList(this.findAll( query.skip((pageNumber - 1) * pageSize).limit(pageSize)));
        return pageBean;
    }
	
    /**
     * @Description 创建映射
     */
	public void createCollection() {
        if (!mongoTemplate.collectionExists(this.getEntityClass())) {
            mongoTemplate.createCollection(this.getEntityClass());
        }
    }
    /**
     * @Description 删除映射
     */
	public void dropCollection() {
        if (mongoTemplate.collectionExists(this.getEntityClass())) {
            mongoTemplate.dropCollection(this.getEntityClass());
        }
	 }
	
	@Override
	public String getNewId(String collectionName) {	
		FindAndModifyOptions options = new FindAndModifyOptions();
		options.upsert(true);
		options.returnNew(true);
		Update incUpdate = new Update().inc("incId", 1);
		Query where = Query.query(Criteria.where("name").is(collectionName));
		IncIds ids = mongoTemplate.findAndModify(where, incUpdate, options, IncIds.class);		
		return ids.getIncId().toString();
	}
	
    /**
     * 
     * @Description 求数据总和  
     * @param query
     * @return
     */
	@Override
	public int count(Query query) {
		return (int) mongoTemplate.count(query, this.getEntityClass()); 
	}
	
	private Class<T> getEntityClass(){  
        return ReflectUtil.getSuperClassGenricType(getClass());  
    }

	protected abstract void setMongoTemplate(MongoTemplate mongoTemplate);  
	
	/**
     * @Description 删除--批量
     */
    public List<T> findAllAndRemove(Query query){
        return this.mongoTemplate.findAllAndRemove(query, this.getEntityClass());
    }
}