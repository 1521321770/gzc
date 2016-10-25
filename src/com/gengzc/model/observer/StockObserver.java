package com.gengzc.model.observer;

public class StockObserver extends Observer{

	public StockObserver(String name, Subject sub) {
		super(name, sub);
	}

	@Override
	public void update() {
		System.out.println(sub.getSubJectState() + "," + name + "�رչ�Ʊ���飬��������");
	}

}
