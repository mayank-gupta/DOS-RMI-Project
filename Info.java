//package main;

public class Info {
	int id;
	String machineType;
	int  priority;
	long beginTime;
	long endTime;
	int value;
	boolean priorityInherited;
	public Info(int id, String machineType, int priority, long beginTime,
			long endTime, int value) {
		super();
		this.id = id;
		this.machineType = machineType;
		this.priority = priority;
		this.beginTime = beginTime;
		this.endTime = endTime;
		this.value = value;
		this.priorityInherited = false;
	}
	/**
	 * @return the priority
	 */
	public int getPriority() {
		return priority;
	}
	/**
	 * @param priority the priority to set
	 */
	public void setPriority(int priority) {
		this.priority = priority;
	}
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}
	/**
	 * @return the machineType
	 */
	public String getMachineType() {
		return machineType;
	}
	/**
	 * @param machineType the machineType to set
	 */
	public void setMachineType(String machineType) {
		this.machineType = machineType;
	}
	/**
	 * @return the beginTime
	 */
	public long getBeginTime() {
		return beginTime;
	}
	/**
	 * @param beginTime the beginTime to set
	 */
	public void setBeginTime(long beginTime) {
		this.beginTime = beginTime;
	}
	/**
	 * @return the endTime
	 */
	public long getEndTime() {
		return endTime;
	}
	/**
	 * @param endTime the endTime to set
	 */
	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}
	/**
	 * @return the value
	 */
	public int getValue() {
		return value;
	}
	/**
	 * @param value the value to set
	 */
	public void setValue(int value) {
		this.value = value;
	}
	
}
