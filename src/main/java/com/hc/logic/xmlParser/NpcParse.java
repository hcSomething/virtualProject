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

import com.hc.logic.config.NpcConfig;


@Component
public class NpcParse implements ParseXml{

	private List<NpcConfig> npcList = null;
	private NpcConfig npc = null;

	public NpcParse() {
		
		File file = new File("config/npc.xml");
		parse(file);
	}
	

	@Override
	public void parse(File file) {
		SAXReader reader = new SAXReader();
		try {
			Document document = reader.read(file);
			Element npcs = document.getRootElement(); //获得<scenes>
			Iterator sceneIt = npcs.elementIterator();
			
			npcList = new ArrayList<>();
			while(sceneIt.hasNext()) {
				npc = new NpcConfig();
				Element sceneElement = (Element)sceneIt.next(); //获得<scene>
				List<Attribute> attributes = sceneElement.attributes();
				//遍历<scene>标签的属性
				for(Attribute attribute: attributes) {
					if(attribute.getName().equals("id")) {
						String id = attribute.getValue(); //获得scene id
						npc.setNpcId(Integer.parseInt(id));
					}
				}
				
				Iterator sIt = sceneElement.elementIterator();
				while(sIt.hasNext()) {
					Element child = (Element)sIt.next();
					String nodeName = child.getName();
					if(nodeName.equals("name")) {
						npc.setName(child.getStringValue());
					}else if(nodeName.equals("description")) {
						npc.setDescription(child.getStringValue());
					}else if(nodeName.equals("task")) {
						npc.setTask(child.getStringValue());
					}
				}
				
				npcList.add(npc);
				npc = null;
				
			}
		}catch(DocumentException e) {
			e.printStackTrace();
		}

	}
	
	/**
	 * 通过npc的id获得npcConfig
	 * @param id
	 * @return
	 */
	public NpcConfig getNpcConfigById(int id) {
		for(NpcConfig nc : npcList) {
			if(nc.getNpcId() == id) {
				return nc;
			}
		}
		return null;
	}

}
