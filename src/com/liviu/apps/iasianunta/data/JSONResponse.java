package com.liviu.apps.iasianunta.data;

import org.json.JSONException;
import org.json.JSONObject;

public class JSONResponse extends JSONObject{
	public JSONResponse(String jsonString) throws JSONException {
		super(jsonString);
	}

	public boolean isSuccess(){
		try {
			if(getInt("is_success") == 1)
				return true;
			else
				return false;
		} catch (JSONException e) {
			e.printStackTrace();
			return false;
		}
	}
}
