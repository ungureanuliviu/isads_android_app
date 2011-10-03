package com.liviu.apps.iasianunta.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;

public class Convertor {
	
	// Constants
	private static final String TAG = "Convertor";
	
	public Convertor() {}
	
	public static String toString(Object obj){			
		if(obj == null)
			return "this object is null";
		
		String data		= "";
		Class<?> c 		= obj.getClass();				
		Field[] fields 	= c.getDeclaredFields();
		int 	modifiers;
		data += "\n\n====== " + c.getName() + " ========= \n";
		
		for(Field f : fields){
			f.setAccessible(true);
			modifiers = f.getModifiers();
			if(!Modifier.isFinal(modifiers)){
				try {													
					if(f.getType().equals(String.class))
						data += "\n" + f.getName() + ": " + ((String)f.get(obj));
					else if(f.getType().equals(int.class) || f.getType().equals(Integer.class))
						data += "\n" + f.getName() + ": " + f.getInt(obj);
					else if(f.getType().equals(long.class) || f.getType().equals(Long.class))
						data += "\n" + f.getName() + ": " + f.getLong(obj);
					else if(f.getType().equals(Double.class) || f.getType().equals(double.class))
						data += "\n" + f.getName() + ": " + f.getDouble(obj);
					else if(f.getType().equals(Boolean.class) || f.getType().equals(boolean.class))
						data += "\n" + f.getName() + ": " + f.getBoolean(obj);		
				}
				catch (IllegalArgumentException e) {
					e.printStackTrace();
				}
				catch (IllegalAccessException e) {
					e.printStackTrace();
				}		
			}
		}
		
		return data;
	}	
	
	public static JSONObject toJson(Object obj, boolean pConvertArraylists){				
		String data		= "";
		Class<?> c 		= obj.getClass();				
		Field[] fields 	= c.getDeclaredFields();
		data += "\n====== " + c.getName() + " ========= \n";
		JSONObject json = new JSONObject();
		
		
		for(Field f : fields){
			f.setAccessible(true);
			try {													
				if(f.getType().equals(String.class))
					json.put(f.getName(), (String)f.get(obj));							
				else if(f.getType().equals(int.class) || f.getType().equals(Integer.class))
					json.put(f.getName(), f.getInt(obj));
				else if(f.getType().equals(long.class) || f.getType().equals(Long.class))
					json.put(f.getName(), f.getLong(obj));
				else if(f.getType().equals(Double.class) || f.getType().equals(double.class))
					json.put(f.getName(), f.getDouble(obj));
				else if(f.getType().equals(Boolean.class) || f.getType().equals(boolean.class))
					json.put(f.getName(), f.getBoolean(obj));
				else {
					Type type = f.getGenericType();
					if(type instanceof ParameterizedType){
						 ParameterizedType pt = (ParameterizedType) type;  
			             System.out.println("raw type: " + pt.getRawType());  
			             /*
			             if(pt.getRawType().equals(java.util.ArrayList.class) && pConvertArraylists == true){
			            	 Console.debug(TAG, "We have an arraylist");
			            	 for (Type t : pt.getActualTypeArguments()) {  
				                 Console.debug(TAG, "    " + t + " session: " + Session.class);  
				                 if(t.equals(Session.class)){
				                	 ArrayList<Session>list = (ArrayList<Session>)f.get(obj);
				                	 
				                	 if(list != null){
					                	 JSONArray jsonArray = new JSONArray();
					                	 for(int i = 0; i < list.size(); i++){
					                		 jsonArray.put(Convertor.toJson(list.get(i), false));
					                	 }
					                	 json.put(f.getName(), jsonArray);
				                	 }
				                	 else
				                		 Console.debug(TAG, "list is null in Converter.toJson");
					                		
				                 }
				             }			            	 
			             }	
			             */
					}
				}
			}
			catch (IllegalArgumentException e) {
				e.printStackTrace();
				json = null;
			}
			catch (IllegalAccessException e) {
				e.printStackTrace();
				json = null;
			} catch (JSONException e) {
				e.printStackTrace();
				json = null;
			}													
		}
		
		if(json != null)
			Console.debug(TAG, json.toString());
		return json;
	}		
}
