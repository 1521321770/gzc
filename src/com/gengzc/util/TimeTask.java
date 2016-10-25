package com.gengzc.util;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class TimeTask extends TimerTask{

	private Timer timer = new Timer();
	private Calendar calendar = Calendar.getInstance();

	public void start() {
        timer.scheduleAtFixedRate(this, new Date(), 1000 * 5);        
    }

	@Override
	public void run() {
		delFiles( );    
        if(calendar.get(Calendar.HOUR_OF_DAY) == 17 && calendar.get(Calendar.MINUTE) == 15){
            this.cancel();
        }
	}

	private static void delFiles() {
        System.out.println("----");
    }

	public static void main(String[] args) {
    	TimeTask fm = new TimeTask();
        fm.start();
    }
}
