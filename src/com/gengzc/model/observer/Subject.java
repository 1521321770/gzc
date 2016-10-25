package com.gengzc.model.observer;

public interface Subject {

	public abstract void attach(Observer observer);

	public abstract void detach(Observer observer);

	public abstract void inform();

	void subJectState(String action);

	String getSubJectState();
}
