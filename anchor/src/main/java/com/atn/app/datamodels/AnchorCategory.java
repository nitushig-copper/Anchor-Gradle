/**
 * @Copyright CopperMobile 2014
 * */
package com.atn.app.datamodels;

import android.content.Context;

import com.atn.app.httprequester.AnchorHttpRequest;
import com.atn.app.httprequester.AnchorHttpRequest.Method;
import com.atn.app.pool.UserDataPool;
import com.atn.app.utils.HttpUtility;
import com.atn.app.utils.JsonUtils;

public class AnchorCategory {

	public interface CategoryType {
		int ALL = 1;
		int DONE_BUTTON = 2;
		int CANCEL_BUTTON = 3;
	}

	public int id;
	public String name;
	// parent id.
	public int categoryId;
	public String foursquareId;
	public String fourSquareName;
	// on 1, off 0
	public int status = 0;
	// @transient
	public int categoryType;

	// return category id if its not zero. zero means its parent.
	public int getCategoryId() {
		if (categoryId != 0)
			return categoryId;
		return id;
	}

	@Override
	public boolean equals(Object o) {
		return ((AnchorCategory) o).foursquareId.equals(this.foursquareId);
	}

	// try to save on server
	public static void saveCategoryOnServer(Context context, String catId) {
		AnchorHttpRequest anchorRequest = new AnchorHttpRequest(context,
				HttpUtility.buildBaseUri().appendPath("setcategory"),
				Method.POST, null);
		anchorRequest.addText(JsonUtils.CategoryKeys.USER_ID, UserDataPool
				.getInstance().getUserDetail().getUserId());
		anchorRequest.addText(JsonUtils.CategoryKeys.CATEGORIES, catId);
		anchorRequest.execute();
	}

}
