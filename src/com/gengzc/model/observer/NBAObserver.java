package com.gengzc.model.observer;

public class NBAObserver extends Observer{

	public NBAObserver(String name, Subject sub) {
		super(name, sub);
	}

	@Override
	public void update() {
		System.out.println(sub.getSubJectState() + "," + name + "关闭NBA直播，继续工作");
	}

}
