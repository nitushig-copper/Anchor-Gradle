/**
 * @Copyright CopperMobile 2014
 * */
package com.atn.app.datamodels;

import java.io.Serializable;
/**
 * Anchor tips model
 * */
public class AtnPromotion extends AtnOfferData implements Serializable {

	@Deprecated
	public class PromotionStatus {
		// public static final int Shared = 0;
		public static final int Added = 1;
		public static final int Claimed = 2;
		public static final int Redeemed = 4;
		public static final int Expired = 5;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String PROMOTION = "Promotion";

	public static final String PROMOTION_ID = "id";
	public static final String PROMOTION_TITLE = "title";
	public static final String PROMOTION_DETAIL = "details";
	public static final String PROMOTION_IS_GROUP = "is_group";
	public static final String PROMOTION_TYPE = "type";
	public static final String PROMOTION_START_DATE = "start_date";
	public static final String PROMOTION_END_DATE = "end_date";
	public static final String PROMOTION_IS_NOTIFIED = "is_notified";
	public static final String PROMOTION_LOGO = "logo";
	public static final String PROMOTION_DAYS = "days";
	public static final String PROMOTION_IS_DUPLICATE = "is_duplicate";
	public static final String PROMOTION_STATUS = "status";
	public static final String PROMOTION_SHARED = "shared";
	public static final String PROMOTION_ACCEPTED = "accepted";
	public static final String PROMOTION_IMAGE_SMALL = "image_nonretina";
	public static final String PROMOTION_IMAGE_LARGE = "image_retina";
	public static final String PROMOTION_EXPIRE_TIME = "count_time";
	
	public static final String VISIT_PROMOTION_ID = "promotion_id";
	

	private String promotionId;
	private String promotionTitle;
	private String promotionDetail;
	private String startDate;
	private String endDate;
	private String promotionLogoUrl;
	private String promotionDays;
	private int promotionStatus;
	private String promotionImageLargeUrl;
	private String promotionImageSmallUrl;
	private boolean isGroup;
	private boolean isNotified;
	private boolean isDuplicate;
	private boolean isShared;
	private boolean isAccepted;
	private boolean isRedeemed;

	private PromotionType promotionType;
	private String ageMax;
	private String ageMin;
	private String sex;
	private String businessId;
	private String couponExpiryDate;
	private String promotionCreated;
	private String promotionGroupCount;
	private String promotionModified;

	public AtnPromotion() {
	}

	/**
	 * @return the isRedeemed
	 */
	public boolean isRedeemed() {
		return isRedeemed;
	}

	/**
	 * @return the ageMax
	 */
	public String getAgeMax() {
		return ageMax;
	}

	/**
	 * @return the ageMin
	 */
	public String getAgeMin() {
		return ageMin;
	}

	/**
	 * @return the sex
	 */
	public String getSex() {
		return sex;
	}

	/**
	 * @return the businessId
	 */
	public String getBusinessId() {
		return businessId;
	}

	/**
	 * @return the couponExpiryDate
	 */
	public String getCouponExpiryDate() {
		return couponExpiryDate;
	}

	/**
	 * @return the promotionCreated
	 */
	public String getPromotionCreated() {
		return promotionCreated;
	}

	/**
	 * @return the promotionGroupCount
	 */
	public String getPromotionGroupCount() {
		return promotionGroupCount;
	}

	/**
	 * @return the promotionModified
	 */
	public String getPromotionModified() {
		return promotionModified;
	}

	/**
	 * @param isRedeemed
	 *            the isRedeemed to set
	 */
	public void setRedeemed(boolean value) {
		this.isRedeemed = value;
	}

	/**
	 * @param ageMax
	 *            the ageMax to set
	 */
	public void setAgeMax(String ageMax) {
		this.ageMax = ageMax;
	}

	/**
	 * @param ageMin
	 *            the ageMin to set
	 */
	public void setAgeMin(String ageMin) {
		this.ageMin = ageMin;
	}

	/**
	 * @param sex
	 *            the sex to set
	 */
	public void setSex(String sex) {
		this.sex = sex;
	}

	/**
	 * @param businessId
	 *            the businessId to set
	 */
	public void setBusinessId(String businessId) {
		this.businessId = businessId;
	}

	/**
	 * @param couponExpiryDate
	 *            the couponExpiryDate to set
	 */
	public void setCouponExpiryDate(String couponExpiryDate) {
		this.couponExpiryDate = couponExpiryDate;
	}

	/**
	 * @param promotionCreated
	 *            the promotionCreated to set
	 */
	public void setPromotionCreated(String promotionCreated) {
		this.promotionCreated = promotionCreated;
	}

	/**
	 * @param promotionGroupCount
	 *            the promotionGroupCount to set
	 */
	public void setPromotionGroupCount(String promotionGroupCount) {
		this.promotionGroupCount = promotionGroupCount;
	}

	/**
	 * @param promotionModified
	 *            the promotionModified to set
	 */
	public void setPromotionModified(String promotionModified) {
		this.promotionModified = promotionModified;
	}

	public enum PromotionType implements Serializable {
		Event, Offer
	}

	public void setPromotionType(PromotionType type) {
		promotionType = type;
	}

	public PromotionType getPromotionType() {
		return promotionType;
	}

	/**
	 * @return the promotionId
	 */
	public String getPromotionId() {
		return promotionId;
	}

	/**
	 * @return the promotionTitle
	 */
	public String getPromotionTitle() {
		return promotionTitle;
	}

	/**
	 * @return the mTipDetail
	 */
	public String getPromotionDetail() {
		return promotionDetail;
	}

	/**
	 * @return the startDate
	 */
	public String getStartDate() {
		return startDate;
	}

	/**
	 * @return the endDate
	 */
	public String getEndDate() {
		return endDate;
	}

	/**
	 * @return the promotionLogoUrl
	 */
	public String getPromotionLogoUrl() {
		return promotionLogoUrl;
	}

	/**
	 * @return the promotionDays
	 */
	public String getPromotionDays() {
		return promotionDays;
	}

	/**
	 * @return the promotionStatus
	 */
	public int getPromotionStatus() {
		return promotionStatus;
	}

	/**
	 * @return the promotionImageLarge
	 */
	public String getPromotionImageLargeUrl() {
		return promotionImageLargeUrl;
	}

	/**
	 * @return the promotionImageSmall
	 */
	public String getPromotionImageSmallUrl() {
		return promotionImageSmallUrl;
	}

	/**
	 */
	public boolean isGroup() {
		return isGroup;
	}

	/**
	 */
	public boolean isNotified() {
		return isNotified;
	}

	/**
	 */
	public boolean isDuplicate() {
		return isDuplicate;
	}

	/**
	 */
	public boolean isShared() {
		return isShared;
	}

	/**
	 */
	public boolean isAccepted() {
		return isAccepted;
	}

	/**
	 * @param promotionId
	 *            the promotionId to set
	 */
	public void setPromotionId(String promotionId) {
		this.promotionId = promotionId;
	}

	/**
	 * @param promotionTitle
	 *            the promotionTitle to set
	 */
	public void setPromotionTitle(String promotionTitle) {
		this.promotionTitle = promotionTitle;
	}

	/**
	 * @param mTipDetail
	 *            the mTipDetail to set
	 */
	public void setPromotionDetail(String promotionDetail) {
		this.promotionDetail = promotionDetail;
	}

	/**
	 * @param startDate
	 *            the startDate to set
	 */
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	/**
	 * @param endDate
	 *            the endDate to set
	 */
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	/**
	 * @param promotionLogoUrl
	 *            the promotionLogoUrl to set
	 */
	public void setPromotionLogoUrl(String promotionLogoUrl) {
		this.promotionLogoUrl = promotionLogoUrl;
	}

	/**
	 * @param promotionDays
	 *            the promotionDays to set
	 */
	public void setPromotionDays(String promotionDays) {
		this.promotionDays = promotionDays;
	}

	/**
	 * @param promotionStatus
	 *            the promotionStatus to set
	 */
	public void setPromotionStatus(int promotionStatus) {
		this.promotionStatus = promotionStatus;
	}

	/**
	 * @param promotionImageLarge
	 *            the promotionImageLarge to set
	 */
	public void setPromotionImageLargeUrl(String promotionImageLargeUrl) {
		this.promotionImageLargeUrl = promotionImageLargeUrl;
	}

	/**
	 * @param promotionImageSmall
	 *            the promotionImageSmall to set
	 */
	public void setPromotionImageSmallUrl(String promotionImageSmallUrl) {
		this.promotionImageSmallUrl = promotionImageSmallUrl;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setGroup(boolean value) {
		this.isGroup = value;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setNotified(boolean value) {
		this.isNotified = value;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setDuplicate(boolean value) {
		this.isDuplicate = value;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setShared(boolean value) {
		this.isShared = value;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setAccepted(boolean value) {
		this.isAccepted = value;
	}

	@Override
	public boolean equals(Object o) {
		if (((AtnOfferData) o).getDataType() == VenueType.TIPS) {
			return ((AtnPromotion) o).getPromotionId().equals(
					this.getPromotionId());
		}
		return false;
	}

	@Override
	public int getDataType() {
		return VenueType.TIPS;
	}
}
