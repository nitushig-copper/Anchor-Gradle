package com.atn.app.listener;

import com.atn.app.fragments.AtnBaseFragment;


public interface AddFragmentListener {
	public void addToBackStack(AtnBaseFragment fragment);
	//return true if fragment is pop out from stack
	public boolean popBackStack(AtnBaseFragment fragment);
}
