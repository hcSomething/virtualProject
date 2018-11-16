package com.hc.logic.achieve;

public enum TargetType {

	Null(null),
	/** 打怪*/
	AttackMonster(new AttackMonstTarget()),
	/** 采集 */
	Collect(new AttackMonstTarget()),
	/** 通过副本 */
	PassCopys(new PassCopyTarget());
	
	private Target target;
	
	private TargetType(Target target) {
		this.target = target;
	}

	public Target getTarget() {
		return target;
	}

	public void setTarget(Target target) {
		this.target = target;
	}
	
	/**
	 * 通过任务类型id获得相应的任务目标
	 * @param tid
	 * @return
	 */
	public static Target getTargetById(int tid) {
		TargetType[] values = TargetType.values();
		TargetType tarType = values[tid];
		return tarType.getTarget();
	}
}
