package com.atn.app.webservices;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.atn.app.datamodels.AtnPromotion;
import com.atn.app.datamodels.AtnPromotion.PromotionType;
import com.atn.app.datamodels.AtnRegisteredVenueData;
import com.atn.app.datamodels.UserDetail;
import com.atn.app.pool.UserDataPool;
import com.atn.app.utils.AtnUtils;
import com.atn.app.utils.HttpUtility;
import com.atn.app.webservices.WebserviceType.ServiceType;

public class MyDealsWebService extends WebserviceBase{

	
	private final static String ATN_MY_DEALS = "/user/promotions";
	private String userId;
	
	
	private MyDealsWebServiceListener mMyDealsWebServiceListener;
	
	public MyDealsWebService(String userId) 
	{
		super(HttpUtility.BASE_SERVICE_URL + ATN_MY_DEALS);
		setRequestType(RequestType.GET);
		setWebserviceType(ServiceType.MY_DEAL);
		setWebserviceListener(mWebserviceListener);
		
		this.userId = userId;
	}
	
	public void setMyDealsWebServiceListener(MyDealsWebServiceListener listener)
	{
		mMyDealsWebServiceListener = listener;
	}
	
	
	WebserviceListener mWebserviceListener = new WebserviceListener()
	{
		
		@Override
		public void onSetUrlError(ServiceType serviceType, Exception ex)
		{
			if(mMyDealsWebServiceListener != null)
			{
				if(serviceType == ServiceType.ADD_DEALS)
				{
					mMyDealsWebServiceListener.onFailed(WebserviceError.URL_ERROR, ex.getMessage());
				}
			}
		}
		
		@Override
		public void onServiceResult(ServiceType serviceType, String result)
		{
			if(mMyDealsWebServiceListener != null)
			{
				if(serviceType == ServiceType.ADD_DEALS)
				{
					mMyDealsWebServiceListener.onSuccess(getAtnMyDealsData(result));
				}
			}
		}
		
		@Override
		public void onServiceError(ServiceType serviceType, int errorCode, String errorMessage)
		{
			if(mMyDealsWebServiceListener != null)
			{
				if(serviceType == ServiceType.ADD_DEALS)
				{
					mMyDealsWebServiceListener.onFailed(errorCode, errorMessage);
				}
			}
		}
		
		@Override
		public void onNoInternet(ServiceType serviceType, Exception ex)
		{
			if(mMyDealsWebServiceListener != null)
			{
				if(serviceType == ServiceType.ADD_DEALS)
				{
					mMyDealsWebServiceListener.onFailed(WebserviceError.INTERNET_ERROR, ex.getMessage());
				}
			}
		}
	};
	
	
	
	/**
	 * Returns the parsed list of all ATN registered venue details using specified response from the server.
	 * It parses the ATN business details and add the ATN promotion details to that business if available.
	 *  
	 * @param response data received from the server.
	 * @return Collection of ATN registered venue details.
	 */
	
	public ArrayList<AtnRegisteredVenueData> getAtnMyDealsData(String response)
	{
		ArrayList<AtnRegisteredVenueData> atnRegisteredVenueData = new ArrayList<AtnRegisteredVenueData>();
		
		AtnRegisteredVenueData atnVenueData;
		
		try
		{
			JSONArray jsonArray = new JSONArray(response);
			JSONObject dataObject = null;
			
			for(int i=0; i<jsonArray.length(); i++)
			{
				dataObject = (JSONObject) jsonArray.get(i);
				
				if(dataObject == null)
				{
					return null;
				}
				
				
				atnVenueData = new AtnRegisteredVenueData();
				
				/**
				 * Parsing JSON data to AtnRegisteredVenueData object. 
				 */
				if(!dataObject.isNull(AtnRegisteredVenueData.BUSINESS))
				{
					
					if(!dataObject.getJSONObject(AtnRegisteredVenueData.BUSINESS).isNull(AtnRegisteredVenueData.BUSINESS_ID))
					{
						atnVenueData.setBusinessId(dataObject.getJSONObject(AtnRegisteredVenueData.BUSINESS).getString(AtnRegisteredVenueData.BUSINESS_ID));
					}
					
					if(!dataObject.getJSONObject(AtnRegisteredVenueData.BUSINESS).isNull(AtnRegisteredVenueData.BUSINESS_NAME))
					{
						atnVenueData.setBusinessName(dataObject.getJSONObject(AtnRegisteredVenueData.BUSINESS).getString(AtnRegisteredVenueData.BUSINESS_NAME));
					}
					
					if(!dataObject.getJSONObject(AtnRegisteredVenueData.BUSINESS).isNull(AtnRegisteredVenueData.BUSINESS_STREET))
					{
						atnVenueData.setBusinessStreet(dataObject.getJSONObject(AtnRegisteredVenueData.BUSINESS).getString(AtnRegisteredVenueData.BUSINESS_STREET));
					}
					
					if(!dataObject.getJSONObject(AtnRegisteredVenueData.BUSINESS).isNull(AtnRegisteredVenueData.BUSINESS_CITY))
					{
						atnVenueData.setBusinessCity(dataObject.getJSONObject(AtnRegisteredVenueData.BUSINESS).getString(AtnRegisteredVenueData.BUSINESS_CITY));
					}
					
					if(!dataObject.getJSONObject(AtnRegisteredVenueData.BUSINESS).isNull(AtnRegisteredVenueData.BUSINESS_STATE))
					{
						atnVenueData.setBusinessState(dataObject.getJSONObject(AtnRegisteredVenueData.BUSINESS).getString(AtnRegisteredVenueData.BUSINESS_STATE));
					}
					
					if(!dataObject.getJSONObject(AtnRegisteredVenueData.BUSINESS).isNull(AtnRegisteredVenueData.BUSINESS_ZIP))
					{
						atnVenueData.setBusinessZip(dataObject.getJSONObject(AtnRegisteredVenueData.BUSINESS).getString(AtnRegisteredVenueData.BUSINESS_ZIP));
					}
					
					if(!dataObject.getJSONObject(AtnRegisteredVenueData.BUSINESS).isNull(AtnRegisteredVenueData.BUSINESS_LAT))
					{
						atnVenueData.setBusinessLat(dataObject.getJSONObject(AtnRegisteredVenueData.BUSINESS).getString(AtnRegisteredVenueData.BUSINESS_LAT));
					}
					
					if(!dataObject.getJSONObject(AtnRegisteredVenueData.BUSINESS).isNull(AtnRegisteredVenueData.BUSINESS_LNG))
					{
						atnVenueData.setBusinessLng(dataObject.getJSONObject(AtnRegisteredVenueData.BUSINESS).getString(AtnRegisteredVenueData.BUSINESS_LNG));
					}
					
					if(!dataObject.getJSONObject(AtnRegisteredVenueData.BUSINESS).isNull(AtnRegisteredVenueData.BUSINESS_PHONE))
					{
						atnVenueData.setBusinessPhone(dataObject.getJSONObject(AtnRegisteredVenueData.BUSINESS).getString(AtnRegisteredVenueData.BUSINESS_PHONE));
					}
					
					if(!dataObject.getJSONObject(AtnRegisteredVenueData.BUSINESS).isNull(AtnRegisteredVenueData.BUSINESS_FB_LINK))
					{
						atnVenueData.setBusinessFacebookLink(dataObject.getJSONObject(AtnRegisteredVenueData.BUSINESS).getString(AtnRegisteredVenueData.BUSINESS_FB_LINK));
					}
					
					if(!dataObject.getJSONObject(AtnRegisteredVenueData.BUSINESS).isNull(AtnRegisteredVenueData.BUSINESS_FB_LINK_ID))
					{
						atnVenueData.setBusinessFacebookLinkId(dataObject.getJSONObject(AtnRegisteredVenueData.BUSINESS).getString(AtnRegisteredVenueData.BUSINESS_FB_LINK_ID));
					}
					
					if(!dataObject.getJSONObject(AtnRegisteredVenueData.BUSINESS).isNull(AtnRegisteredVenueData.BUSINESS_FS_VENUE_LINK))
					{
						atnVenueData.setBusinessFoursquareVenueLink(dataObject.getJSONObject(AtnRegisteredVenueData.BUSINESS).getString(AtnRegisteredVenueData.BUSINESS_FS_VENUE_LINK));
					}
					
					if(!dataObject.getJSONObject(AtnRegisteredVenueData.BUSINESS).isNull(AtnRegisteredVenueData.BUSINESS_FS_VENUE_ID))
					{
						atnVenueData.setBusinessFoursquareVenueId(dataObject.getJSONObject(AtnRegisteredVenueData.BUSINESS).getString(AtnRegisteredVenueData.BUSINESS_FS_VENUE_ID));
					}
					
					if(!dataObject.getJSONObject(AtnRegisteredVenueData.BUSINESS).isNull(AtnRegisteredVenueData.BUSINESS_IMAGE))
					{
						atnVenueData.setBusinessImageUrl(dataObject.getJSONObject(AtnRegisteredVenueData.BUSINESS).getString(AtnRegisteredVenueData.BUSINESS_IMAGE));
					}
					
					if(!dataObject.getJSONObject(AtnRegisteredVenueData.BUSINESS).isNull(AtnRegisteredVenueData.BUSINESS_FAVORITE))
					{
						atnVenueData.setFavorited(false);
					}
					else
					{
						atnVenueData.setFavorited(true);
					}
					
					if(!dataObject.getJSONObject(AtnRegisteredVenueData.BUSINESS).isNull(AtnRegisteredVenueData.BUSINESS_SUBSCRIBE))
					{
						atnVenueData.setSubscribed(false);
					}
					else
					{
						atnVenueData.setSubscribed(true);
					}
				
					/**
					 * After parsing data check whether there are any promotions available or not. If promotions
					 * are available then parse the promotion details and add promotion details to the specified
					 * ATN venue.
					 */
					
					JSONArray promotionArray = dataObject.getJSONArray(AtnPromotion.PROMOTION);
					
					if(promotionArray.length() > 0)
					{
						for(int j=0; j<promotionArray.length(); j++)
						{
							AtnPromotion promotionDetail = getAtnPromotion(atnVenueData.getBusinessId(), promotionArray.getJSONObject(j));
							
							if(promotionDetail != null)
							{
								atnVenueData.addPromotion(promotionDetail);
							}
						}
					}
				}
				atnRegisteredVenueData.add(atnVenueData);
			}
			
			return atnRegisteredVenueData;
		}
		catch(JSONException ex)
		{
			ex.printStackTrace();
			
			return null;
		}
		
	}
	
	
	/**
	 * Returns the parsed AtnPromotion data from the JSON data.
	 * 
	 * @param promotionObject JSON data of promotion
	 * @return AtnPromotion
	 */
	public AtnPromotion getAtnPromotion(String businessId, JSONObject promotionObject)
	{
		AtnPromotion atnPromotion = null;

		try
		{
			JSONObject dataObject = promotionObject;
			
			if(dataObject == null)
			{
				return null;
			}
			
			atnPromotion = new AtnPromotion();
			
			atnPromotion.setBusinessId(businessId);
			
			/**
			 * Parsing the promotion details to AtnPromotion.
			 * 
			 */
			
			if(!dataObject.isNull(AtnPromotion.PROMOTION_ID))
			{
				atnPromotion.setPromotionId(dataObject.getString(AtnPromotion.PROMOTION_ID));
			}
			
			if(!dataObject.isNull(AtnPromotion.PROMOTION_TITLE))
			{
				atnPromotion.setPromotionTitle(dataObject.getString(AtnPromotion.PROMOTION_TITLE));
			}
			
			if(!dataObject.isNull(AtnPromotion.PROMOTION_DETAIL))
			{
				atnPromotion.setPromotionDetail(dataObject.getString(AtnPromotion.PROMOTION_DETAIL));
			}
			
			if(!dataObject.isNull(AtnPromotion.PROMOTION_EXPIRE_TIME))
			{
				atnPromotion.setCouponExpiryDate(dataObject.getString(AtnPromotion.PROMOTION_EXPIRE_TIME));
			}
			
			
			if(!dataObject.isNull(AtnPromotion.PROMOTION_TYPE))
			{
				int type = dataObject.getInt(AtnPromotion.PROMOTION_TYPE);
				
				switch(type)
				{
					case 2:
						atnPromotion.setPromotionType(PromotionType.Event);
						break;
					
					default:
						atnPromotion.setPromotionType(PromotionType.Offer);
						break;
				}
			}
			
			
			if (!dataObject.isNull(AtnPromotion.PROMOTION_IS_GROUP)) {
				if (dataObject.getInt(AtnPromotion.PROMOTION_IS_GROUP) > 0) {
					atnPromotion.setGroup(true);
				} else {
					atnPromotion.setGroup(false);
				}
			}
			
			if(!dataObject.isNull(AtnPromotion.PROMOTION_START_DATE)){
				atnPromotion.setStartDate(dataObject.getString(AtnPromotion.PROMOTION_START_DATE));
			}
			
			if(!dataObject.isNull(AtnPromotion.PROMOTION_END_DATE)){
				atnPromotion.setEndDate(dataObject.getString(AtnPromotion.PROMOTION_END_DATE));
			}
			
			if(!dataObject.isNull(AtnPromotion.PROMOTION_IS_NOTIFIED)){
				atnPromotion.setNotified(dataObject.getBoolean(AtnPromotion.PROMOTION_IS_NOTIFIED));
			}
			
			if(!dataObject.isNull(AtnPromotion.PROMOTION_LOGO)){
				atnPromotion.setPromotionLogoUrl(dataObject.getString(AtnPromotion.PROMOTION_LOGO));
			}
			
			if(!dataObject.isNull(AtnPromotion.PROMOTION_DAYS)){
				atnPromotion.setPromotionDays(dataObject.getString(AtnPromotion.PROMOTION_DAYS));
			}
			
			if(!dataObject.isNull(AtnPromotion.PROMOTION_IS_DUPLICATE)){
				atnPromotion.setDuplicate(dataObject.getBoolean(AtnPromotion.PROMOTION_IS_DUPLICATE));
			}
			
			/**
			 * Check the promotion's current status whether it is opened/shared/claimed/redeemed or expired.
			 */
			if(!dataObject.isNull(AtnPromotion.PROMOTION_STATUS))
			{
				atnPromotion.setPromotionStatus(AtnUtils.getInt(dataObject.getString(AtnPromotion.PROMOTION_STATUS)));
			}
			
			
			if (!dataObject.isNull(AtnPromotion.PROMOTION_SHARED)) {
				atnPromotion.setShared(true);
			} else {
				atnPromotion.setShared(false);
			}
			
		
			/**
			 * Check the device DPI type and then set the image url of promotion.
			 */
			switch (AtnUtils.getDeviceType()) {
			case LDPI:
				if (!dataObject.isNull(AtnPromotion.PROMOTION_IMAGE_SMALL)) {
					atnPromotion.setPromotionImageSmallUrl(dataObject.getString(AtnPromotion.PROMOTION_IMAGE_SMALL));
				}
				break;
			case MDPI:
				if (!dataObject.isNull(AtnPromotion.PROMOTION_IMAGE_SMALL)) {
					atnPromotion.setPromotionImageSmallUrl(dataObject.getString(AtnPromotion.PROMOTION_IMAGE_SMALL));
				}
				break;
			default:
				if (!dataObject.isNull(AtnPromotion.PROMOTION_IMAGE_LARGE)) {
					atnPromotion.setPromotionImageLargeUrl(dataObject.getString(AtnPromotion.PROMOTION_IMAGE_LARGE));
				}
				break;
			}
		
				
			return atnPromotion;
		}
		catch(JSONException ex){
			ex.printStackTrace();
			return null;
		}
		
	}
	
	/**
	 * Asynchronously calls the web service to get the my Total Deals . Response of this web service can
	 * be captured from web service listener.
	 */
	
	public void getMyDeals()
	{
		try
		{
			ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();

			if(userId != null)
			{
				nameValuePair.add(new BasicNameValuePair(UserDetail.USER_ID, userId));
				
				setPostData(nameValuePair);
				doRequestAsync();	
			}
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	
	public WebserviceResponse getMyDealsSynchronous() {
		try {
			ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
			if (userId != null) {
				nameValuePair.add(new BasicNameValuePair(UserDetail.USER_ID,UserDataPool.getInstance().getUserDetail().getUserId()));
				setPostData(nameValuePair);
				return doRequestSynch();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
}
