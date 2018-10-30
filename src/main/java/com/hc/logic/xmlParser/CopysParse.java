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

@Component
public class CopysParse implements ParseXml{

	private List<CopysConfig> copysList = null;
	private CopysConfig copys = null;

	public CopysParse() {
		File file = new File("config/copys.xml");
		parse(file);
	}
	
	@Override
	public void parse(File file) {
		SAXReader reader = new SAXReader();
		try {
			Document document = reader.read(file);
			Element cop = document.getRootElement(); //获得<scenes>
			Iterator sceneIt = cop.elementIterator();
			
			copysList = new ArrayList<>();
			while(sceneIt.hasNext()) {
				copys = new CopysConfig();
				Element sceneElement = (Element)sceneIt.next(); //获得<scene>
				List<Attribute> attributes = sceneElement.attributes();
				//遍历<scene>标签的属性
				for(Attribute attribute: attributes) {
					if(attribute.getName().equals("id")) {
						String id = attribute.getValue(); //获得scene id
						copys.setId(Integer.parseInt(id));
					}
				}
				
				Iterator sIt = sceneElement.elementIterator();
				while(sIt.hasNext()) {
					Element child = (Element)sIt.next();
					String nodeName = child.getName();
					if(nodeName.equals("name")) {
						copys.setName(child.getStringValue());
					}else if(nodeName.equals("description")) {
						copys.setDescription(child.getStringValue());
					}else if(nodeName.equals("times")) {
						String sti = child.getStringValue();
						copys.setTimes(Integer.parseInt(sti));
					}else if(nodeName.equals("condition")) {
						String scond = child.getStringValue();
						copys.setCondition(Integer.parseInt(scond));
					}else if(nodeName.equals("place")) {
						String sPl = child.getStringValue();
						copys.setPlace(Integer.parseInt(sPl));
					}else if(nodeName.equals("continueT")) {
						String sCo = child.getStringValue();
						copys.setContinueT(Integer.parseInt(sCo));
					}else if(nodeName.equals("bosses")) {
						copys.setsBosses(child.getStringValue());
					}else if(nodeName.equals("reward")) {
						copys.setsRewords(child.getStringValue());
					}
				}
				
				copysList.add(copys);
				copys.convert();
				
				copys = null;
			}
		}catch(DocumentException e) {
			e.printStackTrace();
		}

	}
	
	/**
	 * 根据副本id获得副本配置
	 * @param id
	 * @return
	 */
	public CopysConfig getCopysConfById(int id) {
		for(CopysConfig copyCo : copysList) {
			if(copyCo.getId() == id)
				return copyCo;
		}
		return null;
	}
}
