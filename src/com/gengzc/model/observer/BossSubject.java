package com.gengzc.model.observer;

import java.util.ArrayList;
import java.util.List;

public class BossSubject implements Subject{

	private List<Observer> observers = new ArrayList<Observer>();

	private String action;

	public void attach(Observer observer) {
		observers.add(observer);
	}

	public void detach(Observer observer) {
		observers.remove(observer);
	}

	@Override
	public void inform() {
		for (Observer observer:observers) {
			observer.update();
		}
	}

	@Override
	public void subJectState(String action) {
		this.action = action;
	}

	@Override
	public String getSubJectState() {
		return action;
	}


}
