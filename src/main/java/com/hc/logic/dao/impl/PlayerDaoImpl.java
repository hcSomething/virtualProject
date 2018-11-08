package com.hc.logic.dao.impl;

import java.util.List;

import org.hibernate.*;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.*;

import com.hc.logic.dao.PlayerDao;
import com.hc.logic.domain.PlayerEntity;

/**
 * 对数据库的实际操作
 * @author hc
 *
 */
public class PlayerDaoImpl{
	
	public void insert(Object o) {
		
		System.out.println("插入******");
		Session session = SessionUtil.getSession();
		Transaction ts;
		try {
			ts = session.beginTransaction();
			session.save(o);
			ts.commit();
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			session.close();
		}
		


	}
	
	
	public void delete(Object o) {
		System.out.println("删除***");	
		Session session = SessionUtil.getSession();
		Transaction ts;
		try {
			ts = session.beginTransaction();
			session.delete(o);
			ts.commit();
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			session.close();
		}

	}
	
	
	public void update(Object o) {
		System.out.println("更新******");
		Session session = SessionUtil.getSession();
		Transaction ts;
		try {
			ts = session.beginTransaction();
			session.update(o);
			ts.commit();
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			session.close();
		}

		
	}
	
	//from PlayerEntity
	public  List find(String hql){
		Session session = SessionUtil.getSession();
        //Transaction ts = session.beginTransaction();
        
		//这里的查询语句以后可以用参数传进来，这个方法就可以复用了,
		//这里用的时hql
		Transaction ts;
		Query query;  //这里用的不是表名，而是表对应的类名
		List plaers = null;

		try {
			ts = session.beginTransaction();
			query = session.createQuery(hql);
			plaers = query.list();
			ts.commit();
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			session.close();
		}


		return plaers;

	}
	
	public  List find(String hql, String para){
		Session session = SessionUtil.getSession();
        //Transaction ts = session.beginTransaction();
        
		//这里的查询语句以后可以用参数传进来，这个方法就可以复用了,
		//这里用的时hql
		Transaction ts;
		Query query;  //这里用的不是表名，而是表对应的类名
		List plaers = null;

		try {
			ts = session.beginTransaction();
			query = session.createQuery(hql)
					.setParameter("name", para);
			plaers = query.list();
			ts.commit();
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			session.close();
		}


		return plaers;

	}

}

