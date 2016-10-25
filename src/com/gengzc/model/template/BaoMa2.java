package com.gengzc.model.template;


public class BaoMa2 extends BaoMaTemplate{
	
	//汽车发动
	public void start() {
		System.out.println("宝马H2发动...");
	}
		 
	//停车
	public void stop() {
		System.out.println("宝马H2停车...");
		 
	}
	
	//不要响喇叭
	private boolean alarmFlag = false;
	
	//H2型号的宝马车鸣笛
	public void alarm() {
		System.out.println("宝马H2鸣笛...");
	}
		 
	protected boolean isAlarm() {
		return this.alarmFlag;
	}
	
	public void setAlarm(boolean isAlarm){
		this.alarmFlag = isAlarm;
	}
	
	//引擎轰鸣声
	public void engineBoom() {
		System.out.println("宝马H2引擎声音是这样的...");
	}
		 
	//开动起来
/*	public void run(){
		 
		//先发动汽车
		this.start();
		
		//引擎开始轰鸣
		this.engineBoom();
		 
		//然后就开始跑了，跑的过程中遇到一条狗挡路，就按喇叭
		this.alarm();
		 
		//到达目的地就停车
		this.stop();
		}*/
}
