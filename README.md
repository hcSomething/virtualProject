# virtualProject

huang chao.

游戏启动：先启动服务端GameStart  在启动客户端MyClient

现在能进行注册，登陆，进入场景，场景查询等操作
         
可以允许多个客户端同时连接    

玩家可以攻击怪物，怪物也可以攻击人。怪物以一定的频率攻击玩家。
可以有多个玩家攻击一个怪物，一个怪物也可以攻击多个玩家
但每次只能攻击一个玩家，默认攻击第一个攻击它的玩家。当被攻击的玩家逃跑(传送至别的场景)，或者
怪物被击杀，玩家停止受到攻击。
更改了数据存储方式。

	 **指令：
	       输入IP，端口                                 ip 127.0.0.1 4001
		   
	       注册：                                       register 用户名 密码
		   登陆：                                       login 用户名 密码 
		   *进入世界：                                  enterWorld
		   在哪个地图：                                 mapInfo
		   这个地图中有些什么：                         allthing
		   进行传送：                                   transfer  目标场景id
		   与某个NPC对话（要么接取任务，要么完成任务）：npcTalk  npc的id
		   获得怪物信息                                 dMonst   怪物id
		   所有技能                                     allSkill
		   攻击怪物：                                   attackM  skillId 怪物id   
		   返回玩家自身状态                             pState
		   查看背包                                     bag
		   添加物品(装备，丹药)                         addGood 物品id 数量 
		  删除物品                                     delGood  物品id 数量
		  使用丹药                                     useGood 物品id        		   
		   学习技能                                     lSkill 技能id 
		   穿着装备                                     equip 物品id
		   卸下装备                                     dEquip 物品id
		   查看所有装备                                 allEquip 
		   进入副本                                     eCopy 副本id		   
		   进入商店                                     store 页面
		   购买物品                                     buy 物品id 数量 
		   全服聊天                                     allchat [内容]
		   私聊                                         chat 玩家名 内容
		   邮件                      email [玩家名 内容]|[页面]|[r 序号]|[d 序号]                                              
		   攻击玩家                                     attackP 技能id 玩家名
		   进行pk                  pk 
		   
		   副本组队                                     group 
		   
