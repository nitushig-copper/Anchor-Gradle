/**
 * @Copyright CopperMobile 2014
 * */
package com.atn.app.datamodels;

//tag Model class
public class AnchorTag {

	public interface Type {
		int NORMAL = 0;
		int DONE = 1;
		int CANCEL = 2;
		// /total
	}

	public int tagId;
	public long count;
	public String name;
	public int type;
	public int myCount;

	public AnchorTag(int count, String name, int type) {
		this.count = count;
		this.name = name;
		this.type = type;
	}

	//increase tag count or decrease if my count is zero
	public void setMyCount(int myCount) {
		if (myCount == 0) {
			this.count = this.count - 1;
		} else {
			this.count = this.count + 1;
		}
		this.myCount = myCount;
	}

}
