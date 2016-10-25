package com.gengzc.dao.util;

import java.io.Serializable;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;

@Repository("dbDao")
public class DBDao<T, PK extends Serializable> extends HibernateDaoSupport{

}
