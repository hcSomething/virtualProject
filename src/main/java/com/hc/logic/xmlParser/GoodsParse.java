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

import com.hc.logic.config.GoodsConfig;


public class GoodsParse implements ParseXml{

	private List<GoodsConfig> goodslList = null;
	private GoodsConfig goods = null;

	public GoodsParse() {
		String url = "config/goods.xml";
		File file = new File(url);
		parse(file);
	}

	@Override
	public void parse(File file) {
		SAXReader reader = new SAXReader();
		try {
			Document document = reader.read(file);
			Element levels = document.getRootElement(); //���<scenes>
			Iterator sceneIt = levels.elementIterator();
			
			goodslList = new ArrayList<>();
			while(sceneIt.hasNext()) {
				goods = new GoodsConfig();
				Element sceneElement = (Element)sceneIt.next(); //���<scene>
				List<Attribute> attributes = sceneElement.attributes();
				//����<scene>��ǩ������
				for(Attribute attribute: attributes) {
					if(attribute.getName().equals("id")) {
						String id = attribute.getValue(); //���scene id
						goods.setId(Integer.parseInt(id));
					}
				}
				
				Iterator sIt = sceneElement.elementIterator();
				while(sIt.hasNext()) {
					Element child = (Element)sIt.next();
					String nodeName = child.getName();
					if(nodeName.equals("type")) {
						String sTy = child.getStringValue(); 
						goods.setTypeId(Integer.parseInt(sTy));
					}else if(nodeName.equals("name")) {
						goods.setName(child.getStringValue());
					}else if(nodeName.equals("description")) {
						goods.setDescription(child.getStringValue());
					}else if(nodeName.equals("continue")) {
						String sCon = child.getStringValue(); 
						goods.setContinueT(Integer.parseInt(sCon));
					}else  if(nodeName.equals("hp")) {
						String sHp = child.getStringValue(); 
						goods.setHp(Integer.parseInt(sHp));
					}else if(nodeName.equals("mp")) {
						String sMp = child.getStringValue(); 
						goods.setMp(Integer.parseInt(sMp));
					}else if(nodeName.equals("protect")) {
						String sPro = child.getStringValue(); 
						goods.setProtect(Integer.parseInt(sPro));;
					}else if(nodeName.equals("attack")) {
						String sAtt = child.getStringValue();
						goods.setAttack(Integer.parseInt(sAtt));
					}else if(nodeName.equals("superposition")) {
						String sSupo = child.getStringValue();
						goods.setSuperposi(Integer.parseInt(sSupo));
					}else if(nodeName.equals("price")) {
						String sPric = child.getStringValue();
						goods.setPrice(Integer.parseInt(sPric));
					}
				}
				
				goodslList.add(goods);
				goods = null;
				
			}
		}catch(DocumentException e) {
			e.printStackTrace();
		}

	}
	
	
	/**
	 * ͨ����Ʒid�������Ʒ������
	 * @param id
	 * @return
	 */
	public GoodsConfig getGoodsConfigById(int id) {
		for(GoodsConfig gc : goodslList) {
			if(gc.getId() == id) {
				return gc;
			}
		}
		return null;
	}

	
}