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

import com.hc.frame.Scene;
import com.hc.logic.config.SceneConfig;



/**
 * 加载配置场景配置文件，
 * 并将其他加载的配置文件放入这个类的相应自段中
 * @author hc
 *
 */
public class SceneParse implements ParseXml{
	//所有配置文件中的场景配置
	private List<SceneConfig> sceneList = null;
	private SceneConfig scene = null;
	//所有场景，并且已经初始化
	//private List<Scene> allScene = new ArrayList<>();
	
	MonstParse monsters;
	NpcParse npcs;
	TelepParse teleps;


	public SceneParse() {
		File file = new File("config/scenes.xml");
		parse(file);
		init();
	}
	
	@Override
	public void parse(File file) {
		SAXReader reader = new SAXReader();
		try {
			Document document = reader.read(file);
			Element scenes = document.getRootElement(); //获得<scenes>
			Iterator sceneIt = scenes.elementIterator();
			
			sceneList = new ArrayList<>();
			while(sceneIt.hasNext()) {
				scene = new SceneConfig();
				Element sceneElement = (Element)sceneIt.next(); //获得<scene>
				List<Attribute> attributes = sceneElement.attributes();
				//遍历<scene>标签的属性
				for(Attribute attribute: attributes) {
					if(attribute.getName().equals("id")) {
						String id = attribute.getValue(); //获得scene id
						scene.setSceneId(Integer.parseInt(id));
					}
				}
				
				Iterator sIt = sceneElement.elementIterator();
				while(sIt.hasNext()) {
					Element child = (Element)sIt.next();
					String nodeName = child.getName();
					if(nodeName.equals("name")) {
						scene.setName(child.getStringValue());
					}else if(nodeName.equals("description")) {
						scene.setDescription(child.getStringValue());
					}else if(nodeName.equals("monst")) {
						scene.setMonst(child.getStringValue());
					}else if(nodeName.equals("npc")) {
						scene.setNpc(child.getStringValue());
					}else if(nodeName.equals("teleport")) {
						scene.setTeleport(child.getStringValue());
					}
				}
				
				sceneList.add(scene);
				
				//需要将String类型的id集合转化为int类型
				scene.parseString();
				scene = null;
			}
		}catch(DocumentException e) {
			e.printStackTrace();
		}

	}
	
	/**
	 * 加载怪物，npc，传送阵等配置文件
	 */
	public void init() {
		monsters = new MonstParse();
		npcs = new NpcParse();
		teleps = new TelepParse();
		
		monsters.parse(new File("config/monst.xml"));
		npcs.parse(new File("config/npc.xml"));
		teleps.parse(new File("config/teleports.xml"));
		
		
	}
	
	
	
	
	
	/**
	 * 通过场景id获得场景
	 */
	public SceneConfig getSceneById(int sceneId) {
		for(SceneConfig sConfig : sceneList) {
			if(sConfig.getSceneId() == sceneId) {
				return sConfig;
			}
		}
		return null;
	}
	
	/**
	 * 获得所有场景配置
	 */
	public List<SceneConfig> getAllSceneConfig(){
		return sceneList;
	}

	
	
	public MonstParse getMonsters() {
		return monsters;
	}

	public NpcParse getNpcs() {
		return npcs;
	}

	public TelepParse getTeleps() {
		return teleps;
	}

	
	
	
}
