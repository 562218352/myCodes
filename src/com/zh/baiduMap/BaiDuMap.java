package com.zh.baiduMap;

import android.app.Application;

public class BaiDuMap extends Application {
	@Override
	public void onCreate() {
		super.onCreate();
		//AsyncTaskSocket.setLog();
		//LogGlobal.allowAllClass();
		//LogGlobal.addShowClasss(AsyncTaskSocket.class);
		//LogGlobal.addHideClasss(AsyncTaskSocket.class);	
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
	}
}
