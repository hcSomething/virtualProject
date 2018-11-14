package com.hc.logic.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.hc.logic.dao.impl.PlayerDaoImpl;
import com.hc.logic.union.Union;

@Entity
public class UnionEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;

	@Column
	private String name;       //工会名
	
	@Column
	private long createTime;    //工会创立时间
	
	@Column
	private String originator;   //工会创始人
	
	@Column
	private int grade;            //工会等级
	
	@Column
	private int exp;             //工会经验
	
	@Column
	private int pnum;              //工会中的人数
	
	//工会仓库物品
	@OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
	@Fetch(FetchMode.SUBSELECT)
	private List<GoodsEntity> goods = new ArrayList<>(); 
	
	@Column
	private int gold;  //仓库中的金币
	
	@Transient
	private Union union;
	
	public UnionEntity() {
		
	}
	public UnionEntity(String uname, String pname) {
		this.name = uname;
		this.originator = pname;
		this.pnum = 1;   //创建工会时，工会人数默认为1
		this.grade = 1;
		this.createTime = System.currentTimeMillis();
	}
	
	/**
	 * 获得union
	 * @return
	 */
	synchronized public Union getUnion() {
		if(union == null) {
			String hql = "select u.goods from UnionEntity u "
					+ " where u.originator like : name";
			this.goods = new PlayerDaoImpl().find(hql, originator);
			//System.out.println("-------------初始化goods-------" + (goods==null));
			union = new Union(this);
		}
		return union;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public String getOriginator() {
		return originator;
	}

	public void setOriginator(String originator) {
		this.originator = originator;
	}

	public int getGrade() {
		return grade;
	}

	public void setGrade(int grade) {
		this.grade = grade;
	}
	public List<GoodsEntity> getGoods() {
		return goods;
	}
	public void setGoods(List<GoodsEntity> goods) {
		this.goods = goods;
	}
	public void delGoods(GoodsEntity ge) {
		this.goods.remove(ge);
	}
	public GoodsEntity delGoods(int gid) {
		for(GoodsEntity ge : goods) {
			if(ge.geteId() == gid)
				goods.remove(ge);
				return ge;
		}
		return null;
	}
	public int getExp() {
		return exp;
	}
	public void setExp(int exp) {
		this.exp = exp;
	}
	public int getPnum() {
		return pnum;
	}
	public void setPnum(int pnum) {
		this.pnum = pnum;
	}

	public int getGold() {
		return gold;
	}
	public void setGold(int gold) {
		this.gold = gold;
	}
	
	
}
