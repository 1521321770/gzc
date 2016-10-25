package com.gengzc.bean;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "account")
public class Account implements Serializable {

	private static final long serialVersionUID = 1L;
	/**
	 * 主键（UUID）
	 */
	@Id
	@GenericGenerator(name = "systemUUId", strategy = "uuid")
	@GeneratedValue(generator = "systemUUId")
	private String id;

	/**
	 * 用户名
	 */
	@Column(name = "username")
	private String username;

	/**
	 * 密码
	 */
	@Column(name = "password")
	private String password;

	/**
	 * 生日
	 */
	@Column(name = "birthday")
	private Date birthday;

	/**
	 * 电子邮件
	 */
	@Column(name = "email")
	private String email;

	public Account(String username, String password) {
		this.username = username;
		this.password = password;
	}

	public Account() {
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String toString() {
		return "id:" + id + " username:" + username + " password:" + password
				+ " birthday:" + birthday + " email:" + email;
	}

}