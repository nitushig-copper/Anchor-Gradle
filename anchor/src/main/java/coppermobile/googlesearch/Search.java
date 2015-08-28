package coppermobile.googlesearch;

public interface Search 
{
	public boolean isGoogleSearch();
	
	public int getSearchCityId();
	
	public String getSearchCity();
	/**
	 * shortName
	 * */
	public String getSearchCityCode();
	public String getSearchState();
	public String getSearchStateCode();
	public String getSearchCountry();
	public String getSearchCountryCode();
	public double getLatitude();
	public double getLongitude();
	public String toString();
}
