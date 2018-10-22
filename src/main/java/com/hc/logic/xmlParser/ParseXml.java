package com.hc.logic.xmlParser;

import java.io.File;

/**
 * xml解析
 * 
 * 主要有，场景，npc，怪物，传送阵等的配置文件的解析
 * @author hc
 *
 */
public interface ParseXml {

	/**
	 * 解析file文件中的xml文件的内容
	 * @param file
	 */
	public void parse(File file);
}
