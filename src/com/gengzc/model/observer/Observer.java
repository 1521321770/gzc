package com.gengzc.model.observer;

/**
 * ����۲����࣬Ϊ���о���۲��߶���һ���ӿڣ��ڵõ�֪ͨʱ�����Լ�.
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
