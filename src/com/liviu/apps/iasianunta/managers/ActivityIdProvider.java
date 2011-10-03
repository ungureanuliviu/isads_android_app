package com.liviu.apps.iasianunta.managers;

import java.util.HashMap;

public class ActivityIdProvider {

	// constants
	private final String TAG = "ActivityIDProvider";
	
	private static ActivityIdProvider 			instance;
	private static int				  			lastId;
	private static HashMap<Integer, Class<?>> 	activities;
	
	private ActivityIdProvider(){
		lastId 		= 0;
		activities 	= new HashMap<Integer, Class<?>>();
	}
	
	public static ActivityIdProvider getInstance(){
		if(instance == null){
			instance = new ActivityIdProvider();
			return instance;
		}
		else
			return instance;
	}
	
	public int getNewId(Class<?> activity){
		lastId++;		
		activities.put(lastId, activity);
		return lastId;
	}

	public static Class<?> getActivity(int activityId) {
		return activities.get(activityId);
	}
}
