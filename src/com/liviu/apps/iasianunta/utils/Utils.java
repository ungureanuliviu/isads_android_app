package com.liviu.apps.iasianunta.utils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

	public class Utils {
	public static final int	EARTH_RADIUS_KM = 6371;		
	public static final String PAYPAL_DONATE_LINK = "https://www.paypal.com/ro/cgi-bin/webscr?cmd=_flow&SESSION=xAB09h6sQxdJQYokCoNq2hjC6vnFek2AP0HEs0NSJnxBSCXx1-L8fMzIbSG&dispatch=5885d80a13c0db1f8e263663d3faee8d1e83f46a36995b3856cef1e18897ad75";
	public static final String GA_ID = "UA-16744167-7";
	public static String roundTwoDecimals(double d) {
    	DecimalFormat twoDForm = new DecimalFormat("0.00");
	
    	return twoDForm.format(d);
	}
	
	public static Long now() {
	    Date d = new Date();	    
	    return d.getTime();	        
	}
   
	/**
	 * Calculate the distance between 2 points relative to latitude
	 * and longitude
	 * @param lat1
	 * @param lon1
	 * @param lat2
	 * @param lon2
	 * @return distance(in Km)
	 */
	public static double distanceKm(double lat1, double lon1, double lat2, double lon2) {
		double lat1Rad = Math.toRadians(lat1);
		double lat2Rad = Math.toRadians(lat2);
		double deltaLonRad = Math.toRadians(lon2 - lon1);

		return Math.acos(Math.sin(lat1Rad) * Math.sin(lat2Rad) + Math.cos(lat1Rad) * Math.cos(lat2Rad) * Math.cos(deltaLonRad)) * EARTH_RADIUS_KM;
	}
    
	public static int thisMonth(){		
		return Integer.parseInt(Utils.formatDate(System.currentTimeMillis(), "M"));
	}
	
	public static String formatDate(long pTimeStamp, String pattern){
		String 			 	defaultPattern	= "E, dd MMM yyyy HH:mm:ss";
		SimpleDateFormat	formatter		= null;
		
		try{
			formatter = new SimpleDateFormat(pattern);			
		}
		catch (Exception e) {
			//e.printStackTrace();
			formatter = new SimpleDateFormat(defaultPattern);
		}
		
		return formatter.format(new Date(pTimeStamp));
		
	}	
	
	public static String formatTime(long pTime){
		String time = "";
        int seconds = (int) (pTime / 1000);
        int minutes = seconds / 60;
        int hours   = minutes / 60;
        
        if(hours > 0){
        	if(hours < 10)
        		time += "0";
        	time += Integer.toString(hours) + "h ";
        	
        	/*
        	if(hours == 1)
        		timeMessage += " hour ";        		        	
        	else
        		timeMessage += " hours ";
        	*/
        }
        
        if(minutes > 0){
        	if(minutes < 10)
        		time += "0";
        	
        	time += Integer.toString(minutes % 60) + "min ";
        	
        	/*
        	if(minutes == 1)
        		timeMessage += " minute ";
        	else
        		timeMessage += " minutes ";        		
        	*/
        }
        
        seconds = seconds % 60;
        
        if(seconds > 0){
        	if(seconds < 10)
        		time += "0";
        	
        	time += Integer.toString(seconds) + "sec";
        	
        	/*
        	if(seconds == 1)
        		timeMessage += " second ";
        	else
        		timeMessage += " seconds ";
        	*/        	        
        }		
        
        return time;
	}
}
