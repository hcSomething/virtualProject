package com.hc.logic.achieve;

public enum TargetType {

	Null(null),
	/** 打怪*/
	AttackMonster(new AttackMonstTarget()),
	/** 采集 */
	Collect(new SerchGoodsTarget()),
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
	/**
	 * 通过任务id获得任务的目标类型
	 * @param tid
	 * @return
	 */
	public static int getTargetTypeById(int tid) {
		TargetType[] values = TargetType.values();
		TargetType tarType = values[tid];
		return tarType.ordinal();
	}
}
