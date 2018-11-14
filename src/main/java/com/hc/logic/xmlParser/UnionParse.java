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
import com.hc.logic.config.UnionConfig;

@Component
public class UnionParse implements ParseXml{

	private List<UnionConfig> unionsList = null;
	private UnionConfig union = null;

	public UnionParse() {
		File file = new File("config/unions.xml");
		parse(file);
	}

	@Override
	public void parse(File file) {
		SAXReader reader = new SAXReader();
		try {
			Document document = reader.read(file);
			Element cop = document.getRootElement(); //获得<scenes>
			Iterator sceneIt = cop.elementIterator();
			
			unionsList = new ArrayList<>();
			while(sceneIt.hasNext()) {
				union = new UnionConfig();
				Element sceneElement = (Element)sceneIt.next(); //获得<scene>
				List<Attribute> attributes = sceneElement.attributes();
				//遍历<scene>标签的属性
				for(Attribute attribute: attributes) {
					if(attribute.getName().equals("id")) {
						String id = attribute.getValue(); //获得scene id
						union.setId(Integer.parseInt(id));
					}
				}
				
				Iterator sIt = sceneElement.elementIterator();
				while(sIt.hasNext()) {
					Element child = (Element)sIt.next();
					String nodeName = child.getName();
					if(nodeName.equals("name")) {
						union.setName(child.getStringValue());
					}else if(nodeName.equals("exp")) {
						union.setExp(Integer.parseInt(child.getStringValue()));
					}else if(nodeName.equals("num")) {
						String snu = child.getStringValue();
						union.setNum(Integer.parseInt(snu));
					}else if(nodeName.equals("sign")) {
						union.setSign(Integer.parseInt(child.getStringValue()));
					}else if(nodeName.equals("warehouse")) {
						union.setWarehouse(Integer.parseInt(child.getStringValue()));
					}else if(nodeName.equals("group")) {
						union.setGroup(Integer.parseInt(child.getStringValue()));
					}
				}				
				unionsList.add(union);
				
				union = null;
			}
		}catch(DocumentException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 通过工会等级id获得相应的工会配置
	 * @param id
	 * @return
	 */
	public UnionConfig getUCByid(int id) {
		for(UnionConfig uc : unionsList) {
			if(uc.getId() == id) {
				return uc;
			}
		}
		return null;
	}
	

}
