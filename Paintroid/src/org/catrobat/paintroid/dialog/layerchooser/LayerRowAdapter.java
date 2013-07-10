package org.catrobat.paintroid.dialog.layerchooser;

import java.util.ArrayList;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class LayerRowAdapter extends ArrayAdapter<LayerRow> {
	Context context;
	int layoutResourceId;
	ArrayList<LayerRow> data = null;

	public LayerRowAdapter(Context context, int layoutResourceId,
			ArrayList<LayerRow> layer_data) {
		super(context, layoutResourceId, layer_data);
		this.layoutResourceId = layoutResourceId;
		this.context = context;
		this.data = layer_data;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
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

		Bitmap scaled = getCanvasThumbnail(48, 64);

		LayerRow mLayerRow = data.get(position);
		holder.layerTitle.setText(mLayerRow.name);
		holder.layerTitle.setClickable(true);
		holder.layerTitle.setOnClickListener(new View.OnClickListener() {

			@TargetApi(11)
			@Override
			public void onClick(View v) {
				AlertDialog.Builder alert;

				if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
					alert = new AlertDialog.Builder(context);

				} else {
					alert = new AlertDialog.Builder(context,
							AlertDialog.THEME_HOLO_DARK);
				}

				alert.setTitle(R.string.layer_change_name_title);
				alert.setMessage(R.string.layer_change_name_message);

				// Set an EditText view to get user input
				final EditText input = new EditText(context);

				input.setTextColor(context.getResources().getColor(
						R.color.dialog_edittext_color));
				input.setHintTextColor(context.getResources().getColor(
						R.color.dialog_edittext_color_hint));
				input.setCursorVisible(true);

				alert.setView(input);

				alert.setPositiveButton(R.string.ok,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int whichButton) {

								data.get(position).name = input.getText()
										.toString();
								notifyDataSetChanged();
							}
						});

				alert.setNegativeButton(R.string.cancel,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int whichButton) {
								// Canceled.
							}
						});

				alert.show();
			}
		});
		if (scaled != null) {
			holder.thumbnail.setImageBitmap(scaled);
		}

		holder.eyeIcon.setClickable(true);
		holder.eyeIcon.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				data.get(position).visible = !data.get(position).visible;
				notifyDataSetChanged();
			}
		});

		if (data.get(position).visible) {
			holder.eyeIcon.setImageResource(R.drawable.ic_menu_view);
		} else {
			holder.eyeIcon.setImageResource(R.drawable.ic_menu_no_view);
		}

		if (data.get(position).selected) {
			row.setBackgroundResource(R.color.abs__holo_blue_light);
		} else {
			row.setBackgroundResource(R.color.dialog_background_pre_v14_color);
		}
		return row;
	}

	private Bitmap getCanvasThumbnail(int i, int j) {
		Bitmap mBitmapTest = PaintroidApplication.drawingSurface
				.getBitmapCopy();

		if (mBitmapTest == null) {
			return null;
		} else {

			return Bitmap.createScaledBitmap(mBitmapTest, j, j, true);
		}
	}

	static class LayerRowHolder {
		ImageView thumbnail;
		TextView layerTitle;
		ImageView eyeIcon;
	}
}
