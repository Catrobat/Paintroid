package org.catrobat.paintroid.dialog.layerchooser;

import org.catrobat.paintroid.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class LayerRowAdapter extends ArrayAdapter<LayerRow> {
	Context context;
	int layoutResourceId;
	LayerRow data[] = null;

	public LayerRowAdapter(Context context, int layoutResourceId,
			LayerRow[] data) {
		super(context, layoutResourceId, data);
		this.layoutResourceId = layoutResourceId;
		this.context = context;
		this.data = data;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		LayerRowHolder holder = null;

		if (row == null) {
			LayoutInflater inflater = (LayoutInflater) getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater.inflate(layoutResourceId, parent, false);

			holder = new LayerRowHolder();
			holder.thumbnail = (ImageView) row.findViewById(R.id.thumbnail);
			holder.layerTitle = (TextView) row.findViewById(R.id.layerTitle);
			holder.eyeIcon = (ImageView) row.findViewById(R.id.eyeIcon);

			row.setTag(holder);
		} else {
			holder = (LayerRowHolder) row.getTag();
		}

		LayerRow mLayerRow = data[position];
		holder.layerTitle.setText(mLayerRow.name);
		holder.thumbnail.setImageResource(mLayerRow.icon);
		if (mLayerRow.visible) {
			holder.eyeIcon.setImageResource(R.drawable.ic_menu_view);
		} else {
			holder.eyeIcon.setImageResource(R.drawable.ic_menu_no_view);
		}

		return row;
	}

	static class LayerRowHolder {
		ImageView thumbnail;
		TextView layerTitle;
		ImageView eyeIcon;
	}
}
