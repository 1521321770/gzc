package com.gengzc.bean;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name="t_gzc_model")
public class Model {

	/**
	 * Ö÷¼ü£¨UUID£©
	 */
	@Id
	@GenericGenerator(name="systemUUId", strategy="uuid")
	@GeneratedValue(generator = "systemUUId")
	private String id;

	@Column(name="map_key", length=40, nullable=true)
	private String key;

	@Column(name="map_value", length=40, nullable=true)
	private int value;

	public Model(String key, int value){
		this.key = key;
		this.value = value;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public int getValue() {
		return value;
	}
	public void setValue(int value) {
		this.value = value;
	}
}
