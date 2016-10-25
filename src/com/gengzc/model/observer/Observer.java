package com.gengzc.model.observer;

/**
 * 抽象观察者类，为所有具体观察者定义一个接口，在得到通知时更新自己.
 * @author Administrator
 *
 */
public abstract class Observer {
    protected String name;

    protected Subject sub;

    public Observer(String name, Subject sub){
    	this.name = name;
    	this.sub = sub;
    }

    public abstract void update();
}
