package com.gomeplus.bs.interfaces.lol.entity;

import java.io.Serializable;

import lombok.Data;
import lombok.ToString;

@ToString
@Data
public class IncIds implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1806262397389970442L;
	
	private String name;
	
	private Long incId;
}
