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

import com.hc.logic.config.SkillConfig;

@Component
public class SkillParse implements ParseXml{
	
	private List<SkillConfig> skillList = null;
	private SkillConfig skill = null;

	public SkillParse() {
		String url = "config/skills.xml";
		File file = new File(url);
		parse(file);
	}

	@Override
	public void parse(File file) {
		SAXReader reader = new SAXReader();
		try {
			Document document = reader.read(file);
			Element skills = document.getRootElement(); //获得<scenes>
			Iterator sceneIt = skills.elementIterator();
			
			skillList = new ArrayList<>();
			while(sceneIt.hasNext()) {
				skill = new SkillConfig();
				Element sceneElement = (Element)sceneIt.next(); //获得<scene>
				List<Attribute> attributes = sceneElement.attributes();
				//遍历<scene>标签的属性
				for(Attribute attribute: attributes) {
					if(attribute.getName().equals("id")) {
						String id = attribute.getValue(); //获得scene id
						skill.setSkillId(Integer.parseInt(id));
					}
				}
				
				Iterator sIt = sceneElement.elementIterator();
				while(sIt.hasNext()) {
					Element child = (Element)sIt.next();
					String nodeName = child.getName();
					if(nodeName.equals("name")) {
						skill.setName(child.getStringValue());
					}else if(nodeName.equals("description")) {
						skill.setDescription(child.getStringValue());
					}else if(nodeName.equals("cd")) {
						String scd = child.getStringValue();
						skill.setCd(Integer.parseInt(scd));
					}else if(nodeName.equals("attack")) {
						String satt = child.getStringValue();
						skill.setAttack(Integer.parseInt(satt));
					}else if(nodeName.equals("protect")) {
						String spo = child.getStringValue();
						skill.setProtect(Integer.parseInt(spo));
					}else if(nodeName.equals("cure")) {
						String scur = child.getStringValue();
						skill.setCure(Integer.parseInt(scur));
					}else if(nodeName.equals("mp")) {
						String smp = child.getStringValue();
						skill.setMp(Integer.parseInt(smp));
					}else if(nodeName.equals("continue")) {
						String scon = child.getStringValue();
						skill.setContinueT(Integer.parseInt(scon));
					}else if(nodeName.equals("weapon")) {
						String swe = child.getStringValue();
						skill.setWeapon(Integer.parseInt(swe));;
					}else if(nodeName.equals("scope")) {
						String sScop = child.getStringValue();
						skill.setScope(Integer.parseInt(sScop));
					}else if(nodeName.equals("dizziness")) {
						String sdi = child.getStringValue();
						skill.setDizziness(Integer.parseInt(sdi));
					}else if(nodeName.equals("profession")) {
						String sprof = child.getStringValue();
						skill.setProfession(Integer.parseInt(sprof));
					}else if(nodeName.equals("summon")) {
						String ssumm = child.getStringValue();
						skill.setSummonBoss(Integer.parseInt(ssumm));
					}
				}
				
				skillList.add(skill);
				skill = null;
			}
			
			//System.out.println("skillList " + skillList.toString());
		}catch(DocumentException e) {
			e.printStackTrace();
		}

	}
	
	/**
	 * 根据技能id获得技能配置
	 * @param id
	 * @return
	 */
	public SkillConfig getSkillConfigById(int id) {
		for(SkillConfig sc : skillList) {
			if(sc.getSkillId() == id) {
				return sc;
			}
		}
		return null;
	}
	
	
	
	
}
