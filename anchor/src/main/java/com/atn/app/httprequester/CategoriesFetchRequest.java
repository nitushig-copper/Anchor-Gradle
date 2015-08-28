package com.atn.app.httprequester;

import java.nio.charset.Charset;
import java.util.ArrayList;

import org.apache.http.client.methods.HttpGet;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

import com.atn.app.httprequester.AnchorHttpRequest.AnchorHttpResponceListener;
import com.atn.app.httprequester.AnchorHttpRequest.Method;
import com.atn.app.pool.UserDataPool;
import com.atn.app.provider.Atn;
import com.atn.app.utils.AtnUtils;
import com.atn.app.utils.HttpUtility;
import com.atn.app.utils.JsonUtils;
import com.atn.app.utils.SharedPrefUtils;

public class CategoriesFetchRequest extends AtnHttpRequest {
	
	//json keys
	public static final String DATA = "data";
	public static final String BUSINESS_CATEGORY = "BusinessCategory";
	
	public static final String CATEGORY_ID = "id";
	public static final String NAME = "name";
	public static final String FS_ID = "fs_id";
	public static final String FS_NAME = "fs_name";
	public static final String PARENT_ID = "parent_id";
	public static final String ORDER = "order";
	
	public CategoriesFetchRequest(Context context) {
		super(context);
	}
	
	
	
	private String getCategoryUrl() {
		Uri.Builder uri = HttpUtility.buildGetMethodWithAppParams();
		uri.appendPath("catList");
		return uri.build().toString();
	}
	
	private String forceUtf8Coding(String input) {
		AtnUtils.logE("Before Adding >> "+input);
		input = input.replace("é", "e");
		AtnUtils.logE("Before Adding >> "+input);
		return input;
		}
	
	/**
	 * 
	 * ***/
	public boolean loadCategories() {
		
		String result  = executeRequest(new HttpGet(getCategoryUrl()));
		try {
			JSONObject jsonObject = new JSONObject(result);
			if(JsonUtils.resultCode(jsonObject)==0) {
				
				//delete all here.
				if(!jsonObject.isNull(JsonUtils.RESPONSE)&&!jsonObject.getJSONObject(JsonUtils.RESPONSE).isNull(DATA)) {
					JSONArray responce = jsonObject.getJSONObject(JsonUtils.RESPONSE).getJSONArray(DATA);
					ArrayList<ContentProviderOperation> operationList = new ArrayList<ContentProviderOperation>();
					for (int i = 0; i < responce.length(); i++) {
						JSONObject categoryObj = responce.getJSONObject(i).getJSONObject(BUSINESS_CATEGORY);
						if(categoryObj!=null) {
							
							try {
								ContentProviderOperation opr = ContentProviderOperation
										.newInsert(Atn.Category.CONTENT_URI)
										.withValue(Atn.Category.ANCHOR_CATEGORY_ID, categoryObj.getInt(CATEGORY_ID))
										.withValue(Atn.Category.NAME, forceUtf8Coding(categoryObj.getString(NAME)))
										.withValue(Atn.Category.PARENT_CATEGORY_ID, categoryObj.getInt(PARENT_ID))
										.withValue(Atn.Category.FS_CATEGORY_ID, categoryObj.getString(FS_ID))
										.withValue(Atn.Category.FS_NAME, categoryObj.getString(FS_NAME))
										.withValue(Atn.Category.CATEGORY_ORDER, categoryObj.getInt(ORDER))
										.build();
								operationList.add(opr);
							} catch (Exception e) {
								 e.printStackTrace();
							}
						}
					}
					
					if(operationList.size()>0) {
						Atn.Category.insertBatch(getContext(), operationList);
						SharedPrefUtils.saveCategoryDate(getContext());
					}
				} else {
					this.error  = "Catetogries Not Found!";
				}
			} else {
				//setErrorFromJson(jsonObject);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return true;
	}
	
	////
	public void loadAllCatReview() {

		Uri.Builder uri = HttpUtility.buildBaseUri();
		uri.appendPath("hashTags");
		String result = executeRequest(new HttpGet(uri.build().toString()));
		try {
			JSONObject jsonObject = new JSONObject(result);
			if (JsonUtils.resultCode(jsonObject)== 0) {

				if(!jsonObject.isNull(JsonUtils.RESPONSE)&&!jsonObject.getJSONObject(JsonUtils.RESPONSE).isNull(DATA)) {
					JSONArray responce = jsonObject.getJSONObject(JsonUtils.RESPONSE).getJSONArray(DATA);
					
					//clean default tags.
					Atn.ReviewTable.deleteDefaultCategoryTags(getContext());
					
					ArrayList<ContentProviderOperation> operationList = new ArrayList<ContentProviderOperation>();
					for (int i = 0; i < responce.length(); i++) {
						JSONObject reviewObj = responce.getJSONObject(i).getJSONObject("BusinessTags");
						if(reviewObj!=null) {
							
							ContentValues values = new ContentValues();
							int id = reviewObj.getInt(JsonUtils.ReviewTagKey.ID);
							values.put(Atn.ReviewTable.TAG_ID, id);
							if (!reviewObj.isNull(JsonUtils.ReviewTagKey.TAG_NAME))
								values.put(Atn.ReviewTable.NAME, reviewObj.getString(JsonUtils.ReviewTagKey.TAG_NAME));

							if (!reviewObj.isNull(JsonUtils.ReviewTagKey.TAG_TYPE))
								values.put(Atn.ReviewTable.TAG_TYPE, reviewObj.getInt(JsonUtils.ReviewTagKey.TAG_TYPE));

							if (!reviewObj.isNull(JsonUtils.ReviewTagKey.ANCHOR_CATEGORY_ID))
								values.put(Atn.ReviewTable.ANCHOR_CATEGORY_ID, reviewObj.getInt(JsonUtils.ReviewTagKey.ANCHOR_CATEGORY_ID));
							
							 values.put(Atn.ReviewTable.FS_VENUE_ID, Atn.ReviewTable.ANCHOR_FS_ID_VALUE);
							 
							operationList.add(ContentProviderOperation
									.newInsert(Atn.ReviewTable.CONTENT_URI)
									.withValues(values).build());
							 //Atn.ReviewTable.insertOrUpdate(getContext(), values);
						}
					}
					Atn.ReviewTable.insertBatch(getContext(), operationList);
				} 
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public static void loadUserCategory(final Context context) {
		
		if(!UserDataPool.getInstance().isUserLoggedIn()) return ;
		
		AnchorHttpRequest anchorRequest = new AnchorHttpRequest(context, HttpUtility.buildBaseUri().appendPath("getcategory"), Method.POST, new AnchorHttpResponceListener() {
			@Override
			public void onSuccessInBackground(JSONObject jsonObject) {
				
				AtnUtils.log(jsonObject.toString());
				if(JsonUtils.resultCode(jsonObject)==JsonUtils.ANCHOR_SUCCESS) {	
					
						try {
							JSONArray userCatObj = jsonObject.getJSONObject(JsonUtils.RESPONSE).getJSONArray(JsonUtils.DATA);
							for (int i = 0; i < userCatObj.length(); i++) {
								
								JSONObject categoryObj = userCatObj.getJSONObject(i).getJSONObject(BUSINESS_CATEGORY);
								
								ContentValues values = new ContentValues();
								values.put(Atn.Category.ANCHOR_CATEGORY_ID, categoryObj.getInt(CATEGORY_ID));
								values.put(Atn.Category.NAME, categoryObj.getString(NAME));
								values.put(Atn.Category.PARENT_CATEGORY_ID, 0);
								Atn.Category.insertOrUpdate(context, values);
							}
							
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
			}
			@Override
			public void onSuccess(JSONObject jsonObject) {
				AtnUtils.log(jsonObject.toString());
			}
			@Override
			public void onError(Exception ex) {
				AtnUtils.log("Category load error"+ex.getLocalizedMessage());
			}
		});
		anchorRequest.addText(JsonUtils.VenuePicUpload.USER_ID,UserDataPool.getInstance().getUserDetail().getUserId());
		anchorRequest.execute();
	}
	
	
	

}
