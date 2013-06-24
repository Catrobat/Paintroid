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
			holder.imgIcon = (ImageView) row.findViewById(R.id.thumbnail1);
			holder.txtTitle = (TextView) row.findViewById(R.id.label1);

			row.setTag(holder);
		} else {
			holder = (LayerRowHolder) row.getTag();
		}

		LayerRow mLayerRow = data[position];
		holder.txtTitle.setText(mLayerRow.name);
		holder.imgIcon.setImageResource(mLayerRow.icon);

		return row;
	}

	static class LayerRowHolder {
		ImageView imgIcon;
		TextView txtTitle;
	}
}
