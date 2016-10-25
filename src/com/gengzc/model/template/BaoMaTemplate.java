package com.gengzc.model.template;

public abstract class BaoMaTemplate {
	
	/*
	* ���ȣ����ģ��Ҫ�ܹ��������������������ҡ���������ǵ�������������
	* ��Ҫ�ܹ����������������ʵ��Ҫ��ʵ��������
	*/
	public abstract void start();
	 
	//�ܷ������ǻ�Ҫ��ͣ�������ǲ����汾��
	public abstract void stop();
	 
	//���Ȼ���������ǵενУ��������ٽ�
	public abstract void alarm();
	
	//���ӷ�����Ĭ�������ǻ����
	protected boolean isAlarm(){
		return true;
	}
	 
	//������¡¡���죬�������Ǽٵ�
	public abstract void engineBoom();
	 
	//��ģ��Ӧ�û��ܰɣ���������Ƶģ����ǵ�����������֮Ҫ����
	public final void run(){
		//�ȷ�������
		start();
			 
		//���濪ʼ����
		engineBoom();
			 
		//Ȼ��Ϳ�ʼ���ˣ��ܵĹ���������һ������·���Ͱ�����
//		alarm();
		if(isAlarm()){
			alarm();
		}
			 
		//����Ŀ�ĵؾ�ͣ��
		stop();
	}
	
	public final void run1(){
		
		//���濪ʼ����
		engineBoom();
				
		//�ȷ�������
		start();
			 
		//����Ŀ�ĵؾ�ͣ��
		stop();
		
		//Ȼ��Ϳ�ʼ���ˣ��ܵĹ���������һ������·���Ͱ�����
		alarm();
//		if(isAlarm()){
//			alarm();
//		}
			 
	}
	
	public final void run2(){
		
		//���濪ʼ��
		engineBoom();
				
		engineBoom();

		engineBoom();

		engineBoom();

			 
	}
}
