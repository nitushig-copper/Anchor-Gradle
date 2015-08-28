package coppermobile.googlesearch;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;

public class AddressResult implements Search,Serializable {
	
	/**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    private ArrayList<AddressComponent> addressComponents;
    private String formattedAddress = null;
    private String locationType = null;
    private ArrayList<String> types;
    private String searchCountryCode = "";
    private String searchCountry = "";
    private String searchCity = "";
    private String searchCityCode = "";
    private String searchState = "";
    private String searchStateCode = "";
    private double latitude;
    private double longitude;
    private boolean isValid = true;


    public AddressResult(JSONObject resultObj) {
	
		try {
			this.formattedAddress = resultObj.getString("formatted_address");
			this.locationType = resultObj.getJSONObject("geometry").getString("location_type");
			this.latitude = resultObj.getJSONObject("geometry").getJSONObject("location").getDouble("lat");
			this.longitude = resultObj.getJSONObject("geometry").getJSONObject("location").getDouble("lng");
			
			this.addressComponents = new ArrayList<AddressComponent>();
			JSONArray addresCom = resultObj.getJSONArray("address_components");
			for (int i = 0; i < addresCom.length(); i++) {
				JSONObject addComOBj = addresCom.getJSONObject(i);
				this.addressComponents.add(new AddressComponent(addComOBj));
			}
			
			JSONArray typeObj = resultObj.getJSONArray("types");
			types = new ArrayList<String>();
			for (int i = 0; i < typeObj.length(); i++) {
				types.add(typeObj.getString(i));	
			}
			
			AddressComponent localityAddCom = getAddressComponentType("locality");
			
			//not in used.
			//AddressComponent	level3addCom = getAddressComponentType("administrative_area_level_3");
			
			AddressComponent	level2addCom = getAddressComponentType("administrative_area_level_2");
			
			AddressComponent	level1addCom = getAddressComponentType("administrative_area_level_1");
			
			AddressComponent countryComponent = getAddressComponentType("country");
				
			if(localityAddCom!=null){
				this.searchCity = localityAddCom.getLongName();
				this.searchCityCode = localityAddCom.getShortName();
			}
			
			if(this.searchCity==null) {
				this.searchCity = level2addCom.getLongName();
				this.searchCityCode = localityAddCom.getShortName();
			}
			
			if(level1addCom!=null) {
				this.searchState = level1addCom.getLongName();
				this.searchStateCode = level1addCom.getShortName();
			}
			
			if(countryComponent==null) {
				isValid = false; 
			} else {
				this.searchCountryCode = countryComponent.getShortName();
				this.searchCountry = countryComponent.getLongName();		
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public AddressResult() {
	}

	private AddressComponent getAddressComponentType(String type) {

		for (Iterator iterator = this.addressComponents.iterator(); iterator.hasNext();) {
			AddressComponent	localComAdd = (AddressComponent) iterator.next();
			if(localComAdd.getTypes().contains(type)) {
                return localComAdd;
			}
		}
		return null;
	}
	
	
	
	public boolean isValid() {
		return isValid;
	}

	public ArrayList<AddressComponent> getAddressComponents() {
		return addressComponents;
	}

	public String getFormattedAddress() {
		return formattedAddress;
	}

	public String getLocationType() {
		return locationType;
	}

	public ArrayList<String> getTypes() {
		return types;
	}
	@Override
	public double getLatitude() {
		return latitude;
	}
	@Override
	public double getLongitude() {
		return longitude;
	}


    @Override
    public String toString() {

        if (this.searchCity.equals("")) {
            if (this.searchStateCode.equals("")) {
                return this.searchCountry;
            } else {
                return this.searchStateCode + ", " + this.searchCountry;
            }
        }

        if (this.searchStateCode.equals("")) {
            if (this.searchCity.equals("")) {
                return this.searchCountry;
            } else {
                return this.searchCity + ", " + this.searchCountry;
            }
        } else {
            return this.searchCity + ", " + this.searchStateCode + ", " + this.searchCountry;
        }
    }

	@Override
	public boolean isGoogleSearch() {
		return true;
	}

    @Override
    public String getSearchCity() {
        if (this.searchCity.equals("")) {
            return this.searchCountry;
        }
        return this.searchCity;
    }

	@Override
	public String getSearchCountry() {
		return this.searchCountry;
	}

	@Override
	public int getSearchCityId() {
		return 0;
	}

	@Override
	public String getSearchCountryCode() {
		return this.searchCountryCode;
	}

	@Override
	public String getSearchState() {
		return searchState;
	}

	@Override
	public String getSearchStateCode() {
		return searchStateCode;
	}

    @Override
    public String getSearchCityCode() {

        if (this.searchCityCode.equals("")) {
            return this.searchCountryCode;
        }
        return searchCityCode;
    }

	public void setSearchCountryCode(String searchCountryCode) {
		this.searchCountryCode = searchCountryCode;
	}

	public void setSearchCountry(String searchCountry) {
		this.searchCountry = searchCountry;
	}

	public void setSearchCity(String searchCity) {
		this.searchCity = searchCity;
	}

	public void setSearchCityCode(String searchCityCode) {
		this.searchCityCode = searchCityCode;
	}

	public void setSearchState(String searchState) {
		this.searchState = searchState;
	}

	public void setSearchStateCode(String searchStateCode) {
		this.searchStateCode = searchStateCode;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	
	
	
}
