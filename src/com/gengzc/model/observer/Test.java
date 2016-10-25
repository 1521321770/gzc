package com.gengzc.model.observer;

public class Test {
	public static void main(String[] args) {
		BossSubject boss = new BossSubject();

		StockObserver one = new StockObserver("����", boss);

		NBAObserver two = new NBAObserver("����", boss);

		boss.attach(one);
		boss.attach(two);

		boss.subJectState("����������");

		boss.inform();
	}
}
