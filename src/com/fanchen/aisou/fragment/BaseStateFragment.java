package com.fanchen.aisou.fragment;

import android.os.Bundle;

public abstract class BaseStateFragment extends BaseFragment {
	protected Bundle savedState;
	  
	  
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (!restoreStateFromArguments()) {
            onFirstTimeLaunched();
        }
    }
  
    protected void onFirstTimeLaunched() {
  
    }
  
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveStateToArguments();
    }
  
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        saveStateToArguments();
    }
  
  
    private void saveStateToArguments() {
        if (getView() != null)
            savedState = saveState();
        if (savedState != null) {
            Bundle b = getArguments();
            b.putBundle("internalSavedViewState8954201239547", savedState);
        }
    }
    private boolean restoreStateFromArguments() {
        Bundle b = getArguments();
        savedState = b.getBundle("internalSavedViewState8954201239547");
        if (savedState != null) {
            restoreState();
            return true;
        }
        return false;
    }
  
    private void restoreState() {
        if (savedState != null) {
            onRestoreState(savedState);
        }
    }
  
    protected void onRestoreState(Bundle savedInstanceState) {
  
    }
  
    private Bundle saveState() {
        Bundle state = new Bundle();
        onSaveState(state);
        return state;
    }
  
    protected void onSaveState(Bundle outState) {
  
    }

}
