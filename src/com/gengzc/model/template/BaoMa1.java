package com.gengzc.model.template;


public class BaoMa1 extends BaoMaTemplate{
	
	//��������
	public void start() {
		System.out.println("����H1����...");
	}
	 
	//ͣ��
	public void stop() {
		System.out.println("����H1ͣ��...");
	}
	
	 //Ҫ������
	private boolean alarmFlag = true;
	
	//H1�ͺŵı�������
	public void alarm() {
		System.out.println("����H1����...");
	}
	
	protected boolean isAlarm() {
		return this.alarmFlag;
	}
	
	public void setAlarm(boolean isAlarm){
		this.alarmFlag = isAlarm;
	}
	 
	//���������
	public void engineBoom() {
		System.out.println("����H1����������������...");
	}
	 
	//��������
/*	public void run(){
		//�ȷ�������
		this.start();
	 
		//���濪ʼ����
		this.engineBoom();
	 
		//Ȼ��Ϳ�ʼ���ˣ��ܵĹ���������һ������·���Ͱ�����
		this.alarm();
	 
		//����Ŀ�ĵؾ�ͣ��
		this.stop();
	}*/
}
