package com.gengzc.model.observer;

public class Test {
	public static void main(String[] args) {
		BossSubject boss = new BossSubject();

		StockObserver one = new StockObserver("张三", boss);

		NBAObserver two = new NBAObserver("李四", boss);

		boss.attach(one);
		boss.attach(two);

		boss.subJectState("大王回来了");

		boss.inform();
	}
}
