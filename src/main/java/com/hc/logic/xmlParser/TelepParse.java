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

import com.hc.logic.config.TelepConfig;



public class TelepParse implements ParseXml{

	private List<TelepConfig> telepList = null;
	private TelepConfig telep = null;


	@Override
	public void parse(File file) {
		SAXReader reader = new SAXReader();
		try {
			Document document = reader.read(file);
			Element teleps = document.getRootElement(); //获得<scenes>
			Iterator sceneIt = teleps.elementIterator();
			
			telepList = new ArrayList<>();
			while(sceneIt.hasNext()) {
				telep = new TelepConfig();
				Element sceneElement = (Element)sceneIt.next(); //获得<scene>
				List<Attribute> attributes = sceneElement.attributes();
				//遍历<scene>标签的属性
				for(Attribute attribute: attributes) {
					if(attribute.getName().equals("id")) {
						String id = attribute.getValue(); //获得scene id
						telep.setTeleId(Integer.parseInt(id));
					}
				}
				
				Iterator sIt = sceneElement.elementIterator();
				while(sIt.hasNext()) {
					Element child = (Element)sIt.next();
					String nodeName = child.getName();
					if(nodeName.equals("description")) {
						telep.setDescription(child.getStringValue());
					}
				}
				
				telepList.add(telep);
				telep = null;
				
			}
		}catch(DocumentException e) {
			e.printStackTrace();
		}

	}

	
	public TelepConfig getTelepConfigById(int id) {
		for(TelepConfig tc : telepList) {
			if(tc.getTeleId() == id) {
				return tc;
			}
		}
		return null;
	}
	
}
