package com.gengzc.dao.hibernateDaoSupport;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.SQLQuery;

import com.gengzc.util.exception.CloudDBException;

public abstract interface Dao<T, PK extends Serializable>{
  public abstract void flush()
    throws CloudDBException;

  public abstract void saveOrUpdate(T paramT) throws CloudDBException;

  public abstract void update(T paramT) throws CloudDBException;

  public abstract Serializable save(T paramT) throws CloudDBException;

  public abstract void delete(T paramT) throws CloudDBException;

  public abstract void delete(Class<T> paramClass, PK paramPK)
		  throws CloudDBException;

  public abstract void deleteAll(Collection<T> paramCollection)
		  throws CloudDBException;

  public abstract int batchExecute(String paramString, Object[] paramArrayOfObject) throws CloudDBException;

  public abstract int batchExecute(String paramString, Map<String, ?> paramMap)
		  throws CloudDBException;

  public abstract T get(Class<T> paramClass, PK paramPK)
		  throws CloudDBException;

  public abstract <X> X findUnique(String paramString, Object[] paramArrayOfObject)
		  throws CloudDBException;

  public abstract <X> X findUnique(String paramString, Map<String, ?> paramMap)
		  throws CloudDBException;

  public abstract <X> List<X> find(String paramString, Object[] paramArrayOfObject)
		  throws CloudDBException;

  public abstract <X> List<X> find(String paramString, Map<String, ?> paramMap)
		  throws CloudDBException;
  
  public abstract Query createQuery(String paramString, Object[] paramArrayOfObject)
		  throws CloudDBException;

  public abstract Query createQuery(String paramString, Map<String, ?> paramMap)
		  throws CloudDBException;

  public abstract <X> X findUniqueBySqlSentence(String paramString, Object[] paramArrayOfObject)
		  throws CloudDBException;

  public abstract <X> X findUniqueBySqlSentence(String paramString, Map<String, ?> paramMap)
		  throws CloudDBException;

  public abstract <X> List<X> findBySqlSentence(String paramString, Object[] paramArrayOfObject)
		  throws CloudDBException;

  public abstract <X> List<X> findBySqlSentence(String paramString, Map<String, ?> paramMap)
		  throws CloudDBException;

  public abstract Query createQuerySentence(String paramString, Object[] paramArrayOfObject)
		  throws CloudDBException;

  public abstract Query createQuerySentence(String paramString, Map<String, ?> paramMap)
    throws CloudDBException;

  public abstract SQLQuery createSqlQuery(String paramString, Object[] paramArrayOfObject)
		  throws CloudDBException;

  public abstract SQLQuery createSqlQuery(String paramString, Map<String, ?> paramMap)
		  throws CloudDBException;

  public abstract Query findBySql(String paramString)
		  throws CloudDBException;

  public abstract void deleteBySql(String paramString)
		  throws CloudDBException;

  public abstract int batchExecuteBySql(String paramString, Object[] paramArrayOfObject)
		  throws CloudDBException;

  public abstract int batchExecuteBySql(String paramString, Map<String, ?> paramMap)
		  throws CloudDBException;

  public abstract List<T> findAll(Class<T> paramClass)
		  throws CloudDBException;

  public abstract <X> X findOneByTemplate(String paramString, Object[] paramArrayOfObject)
		  throws CloudDBException;

  public abstract <X> List<X> findListByTemplate(String paramString, Object[] paramArrayOfObject)
		  throws CloudDBException;
}