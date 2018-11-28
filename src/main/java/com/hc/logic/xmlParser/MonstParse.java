package com.hc.logic.xmlParser;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.stereotype.Component;

import com.hc.logic.config.MonstConfig;
import com.hc.logic.config.SceneConfig;


@Component
public class MonstParse implements ParseXml{
	
	private List<MonstConfig> monstList = null;   //普通怪物, 包括副本中和场景中的boss
	private MonstConfig monst = null;

	public MonstParse() {
		
		File file = new File("config/monst.xml");
		parse(file);
	}
	
	
	@Override
	public void parse(File file) {
		SAXReader reader = new SAXReader();
		try {
			Document document = reader.read(file);
			Element monsts = document.getRootElement(); //获得<scenes>
			Iterator sceneIt = monsts.elementIterator();
			
			monstList = new ArrayList<>();
			while(sceneIt.hasNext()) {
				monst = new MonstConfig();
				Element sceneElement = (Element)sceneIt.next(); //获得<scene>
				List<Attribute> attributes = sceneElement.attributes();
				//遍历<scene>标签的属性
				for(Attribute attribute: attributes) {
					if(attribute.getName().equals("id")) {
						String id = attribute.getValue(); //获得scene id
						monst.setMonstId(Integer.parseInt(id));
					}
				}
				
				Iterator sIt = sceneElement.elementIterator();
				while(sIt.hasNext()) {
					Element child = (Element)sIt.next();
					String nodeName = child.getName();
					if(nodeName.equals("name")) {
						monst.setName(child.getStringValue());
					}else if(nodeName.equals("description")) {
						monst.setDescription(child.getStringValue());
					}else if(nodeName.equals("hp")) {
						String ss = child.getStringValue();
						monst.setHp(Integer.parseInt(ss));
					}else if(nodeName.equals("attack")) {
						String at = child.getStringValue();
						monst.setAttack(Integer.parseInt(at));
					}else if(nodeName.equals("exp")) {
						String sE = child.getStringValue();
						monst.setExp(Integer.parseInt(sE));
					}else if(nodeName.equals("skills")) {
						monst.setSkiStr(child.getStringValue());
					}else if(nodeName.equals("gold")){
						String sGol = child.getStringValue();
						monst.setGold(Integer.parseInt(sGol));
					}else if(nodeName.equals("attackP")) {
						String sAtt = child.getStringValue();
						monst.setAttackP(Integer.parseInt(sAtt));
					}else if(nodeName.equals("revive")) {
						monst.setRevive(Integer.parseInt(child.getStringValue()));
					}
				}
				
				
				monstList.add(monst);
				monst.convert();
				
				monst = null;
				
			}
		}catch(DocumentException e) {
			e.printStackTrace();
		}

	}
	
	/**
	 * 根据怪物id获得怪物/boss配置
	 * @param id
	 * @return
	 */
	public MonstConfig getMonstConfgById(int id) {
		for(MonstConfig mc : monstList) {
			if(mc.getMonstId() == id) {
				return mc;
			}
		}
		return null;
	}
	
	
	
}
