package com.draik.mobileorg.OrgData;

import android.app.Application;
import android.content.Context;

import com.draik.mobileorg.Services.SyncService;

public class MobileOrgApplication extends Application {
    
	private static MobileOrgApplication instance;
	
    @Override
    public void onCreate() {
    	instance = this;
		SyncService.startAlarm(getApplicationContext());
        super.onCreate();
    }
    
    public static Context getContext() {
    	return instance;
    }
}
