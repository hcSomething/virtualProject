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

import com.hc.logic.config.LevelConfig;

@Component
public class LevelParse implements ParseXml{

	private List<LevelConfig> levelList = null;
	private LevelConfig level = null;

	public LevelParse() {
		String url = "config/level.xml";
		File file = new File(url);
		parse(file);
	}

	@Override
	public void parse(File file) {
		SAXReader reader = new SAXReader();
		try {
			Document document = reader.read(file);
			Element levels = document.getRootElement(); //获得<scenes>
			Iterator sceneIt = levels.elementIterator();
			
			levelList = new ArrayList<>();
			while(sceneIt.hasNext()) {
				level = new LevelConfig();
				Element sceneElement = (Element)sceneIt.next(); //获得<scene>
				List<Attribute> attributes = sceneElement.attributes();
				//遍历<scene>标签的属性
				for(Attribute attribute: attributes) {
					if(attribute.getName().equals("id")) {
						String id = attribute.getValue(); //获得scene id
						level.setId(Integer.parseInt(id));
					}
				}
				
				Iterator sIt = sceneElement.elementIterator();
				while(sIt.hasNext()) {
					Element child = (Element)sIt.next();
					String nodeName = child.getName();
					if(nodeName.equals("exp")) {
						String sExp = child.getStringValue(); 
						level.setExp(Integer.parseInt(sExp));
					}else if(nodeName.equals("hp")) {
						String sHp = child.getStringValue(); 
						level.setHp(Integer.parseInt(sHp));
					}else if(nodeName.equals("mp")) {
						String sMp = child.getStringValue(); 
						level.setMp(Integer.parseInt(sMp));
					}else if(nodeName.equals("uHp")) {
						String sUhp = child.getStringValue(); 
						level.setuHp(Integer.parseInt(sUhp));
					}else if(nodeName.equals("uMp")) {
						String sUmp = child.getStringValue();
						level.setuMp(Integer.parseInt(sUmp));
					}else if(nodeName.equals("attack")) {
						String sAtt = child.getStringValue();
						level.setlAttack(Integer.parseInt(sAtt));
					}
				}
				
				levelList.add(level);
				level = null;
				
			}
		}catch(DocumentException e) {
			e.printStackTrace();
		}

	}
	
	/**
	 * 通过id也就是等级，获得等级配置
	 * @param id
	 * @return
	 */
	public LevelConfig getLevelConfigById(int id) {
		
		for(LevelConfig lc : levelList) {
			if(lc.getId() == id) {
				return lc;
			}
		}
		
		return null;
	}
	

}
