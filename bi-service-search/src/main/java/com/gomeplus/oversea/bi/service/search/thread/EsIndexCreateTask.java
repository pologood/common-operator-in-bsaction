package com.gomeplus.oversea.bi.service.search.thread;

import java.util.List;

import lombok.extern.slf4j.Slf4j;

import com.gomeplus.oversea.bi.service.search.client.ItemRemoteClient;
import com.gomeplus.oversea.bi.service.search.dao.es.ItemIndexDao;
import com.gomeplus.oversea.bi.service.search.entity.Item;
import com.gomeplus.oversea.bi.service.search.entity.ItemSearchVo;
import com.gomeplus.oversea.bi.service.search.utils.Paging;

@Slf4j
public class EsIndexCreateTask implements Runnable {

	private ItemRemoteClient itemRemoteClient;
	
	private ItemIndexDao itemIndexDao;

	private Integer startPageNum;

	public EsIndexCreateTask() {
		super();
	}


	public EsIndexCreateTask(ItemRemoteClient itemRemoteClient,
			ItemIndexDao itemIndexDao, Integer startPageNum) {
		super();
		this.itemRemoteClient = itemRemoteClient;
		this.itemIndexDao = itemIndexDao;
		this.startPageNum = startPageNum;
	}


	@Override
	public void run() {
		Paging<Item> paging = itemRemoteClient.findItemsForSearch(1, startPageNum,1000);
		List<Item> list = paging.getData();
		if (list != null && list.size() > 0) {
			for (Item item : list) {
				ItemSearchVo searchVo = new ItemSearchVo();
				try {
					searchVo.setId(item.getId());
					searchVo.setName(item.getName());
					searchVo.setCategoryId(item.getCategoryId());
					searchVo.setOriginPrice(item.getOriginPrice());
					searchVo.setSellingPrice(item.getSellingPrice());
					searchVo.setStatus(item.getStatus());
					searchVo.setSource(item.getSource());
					searchVo.setCreateDate(item.getCreatedAt());
					itemIndexDao.add(searchVo);
				} catch (Exception e) {
					log.error(" item create es index error , item [{}], searchVo [{}],error ",item,searchVo,e);
					continue;
				}
			}
		}
		paging = null;
		list.clear();
	}

	public ItemRemoteClient getItemRemoteClient() {
		return itemRemoteClient;
	}

	public void setItemRemoteClient(ItemRemoteClient itemRemoteClient) {
		this.itemRemoteClient = itemRemoteClient;
	}

	public Integer getStartPageNum() {
		return startPageNum;
	}

	public void setStartPageNum(Integer startPageNum) {
		this.startPageNum = startPageNum;
	}

}
