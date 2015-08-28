package coppermobile.googlesearch;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class AddressComponent implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String longName = "";
	private String shortName = "";
	private ArrayList<String>  types;
	
	public AddressComponent(JSONObject jsonObj) {
		
		try {
			if(!jsonObj.isNull("long_name"))
				this.longName = jsonObj.getString("long_name");
			
			if(!jsonObj.isNull("short_name"))
				this.shortName = jsonObj.getString("short_name");
			
			JSONArray typeObj = jsonObj.getJSONArray("types");
			types = new ArrayList<String>();
			
			for (int i = 0; i < typeObj.length(); i++) {
				types.add( typeObj.getString(i));
			}
			
		} catch (Exception e) {
			e.getStackTrace();
		}		
	}
	
	public String getLongName() {
		return longName;
	}
	
	public String getShortName() {
		return shortName;
	}
	
	public ArrayList<String> getTypes() {
		return types;
	}
}
	