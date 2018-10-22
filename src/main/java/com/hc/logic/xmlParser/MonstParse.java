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

import com.hc.logic.config.MonstConfig;
import com.hc.logic.config.SceneConfig;



public class MonstParse implements ParseXml{
	
	private List<MonstConfig> monstList = null;
	private MonstConfig monst = null;


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
					}
				}
				
				monstList.add(monst);
				monst = null;
				
			}
		}catch(DocumentException e) {
			e.printStackTrace();
		}

	}
	
	
	public MonstConfig getMonstConfgById(int id) {
		for(MonstConfig mc : monstList) {
			if(mc.getMonstId() == id) {
				return mc;
			}
		}
		return null;
	}
	
	
}
