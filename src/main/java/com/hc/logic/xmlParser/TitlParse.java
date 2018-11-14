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

import com.hc.logic.config.CopysConfig;
import com.hc.logic.config.TitleConfig;

@Component
public class TitlParse implements ParseXml{

	private List<TitleConfig> titlesList = null;
	private TitleConfig titles = null;

	public TitlParse() {
		File file = new File("config/titles.xml");
		parse(file);
	}

	@Override
	public void parse(File file) {
		SAXReader reader = new SAXReader();
		try {
			Document document = reader.read(file);
			Element cop = document.getRootElement(); //获得<scenes>
			Iterator sceneIt = cop.elementIterator();
			
			titlesList = new ArrayList<>();
			while(sceneIt.hasNext()) {
				titles = new TitleConfig();
				Element sceneElement = (Element)sceneIt.next(); //获得<scene>
				List<Attribute> attributes = sceneElement.attributes();
				//遍历<scene>标签的属性
				for(Attribute attribute: attributes) {
					if(attribute.getName().equals("id")) {
						String id = attribute.getValue(); //获得scene id
						titles.setId(Integer.parseInt(id));
					}
				}
				
				Iterator sIt = sceneElement.elementIterator();
				while(sIt.hasNext()) {
					Element child = (Element)sIt.next();
					String nodeName = child.getName();
					if(nodeName.equals("name")) {
						titles.setName(child.getStringValue());
					}else if(nodeName.equals("allow")) {
						titles.setAllow(Integer.parseInt(child.getStringValue()));
					}else if(nodeName.equals("promotion")) {
						titles.setPromotion(Integer.parseInt(child.getStringValue()));
					}else if(nodeName.equals("donate")) {
						titles.setDonate(Integer.parseInt(child.getStringValue()));
					}else if(nodeName.equals("exp")) {
						titles.setExp(Integer.parseInt(child.getStringValue()));
					}else if(nodeName.equals("acquire")) {
						titles.setAcquire(Integer.parseInt(child.getStringValue()));
					}
				}
				
				titlesList.add(titles);
				
				titles = null;
			}
		}catch(DocumentException e) {
			e.printStackTrace();
		}

	}
	
	/**
	 * 通过id获得相应的职位配置
	 * @param id
	 * @return
	 */
	public TitleConfig getTCByid(int id) {
		for(TitleConfig tc : titlesList){
			if(tc.getId() == id) {
				return tc;
			}
		}
		return null;
	}

	
}
