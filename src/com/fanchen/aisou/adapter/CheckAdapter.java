package com.fanchen.aisou.adapter;

import java.util.List;

import com.fanchen.aisou.R;

import android.content.Context;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

public class CheckAdapter extends BaseListAdapter<Boolean> {
	private SparseArray<View> lmap = new SparseArray<View>();
	private List<String> names;

	public CheckAdapter(Context context) {
		super(context);
	}

	public CheckAdapter(Context context, List<Boolean> mList) {
		super(context, mList);
	}
	
	public CheckAdapter(Context context,List<String> names, List<Boolean> checkedItem) {
		super(context, checkedItem);
		this.names = names;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (lmap.get(position) == null) {
			convertView = mLayoutInflater.inflate(R.layout.dialog_multichoice_item, null);
			lmap.put(position, convertView);
			TextView nameTextView = get(convertView, R.id.contact_name);
			CheckBox checkBox = get(convertView, R.id.chk_selectone);
			if (names != null && names.size() > 0) {
				String name = (String) names.get(position);
				nameTextView.setText(name);
				checkBox.setChecked(mList.get(position));
			}
		}else{
			convertView = lmap.get(position);
		}
		return convertView;
	}

	public List<Boolean> getCheckedItem() {
		for (int i = 0; i < getCount(); i++) {
			View view = this.getView(i, null, null);
			CheckBox box = (CheckBox) view.findViewById(R.id.chk_selectone);
			mList.set(i, box.isChecked());
		}
		return mList;
	}

}