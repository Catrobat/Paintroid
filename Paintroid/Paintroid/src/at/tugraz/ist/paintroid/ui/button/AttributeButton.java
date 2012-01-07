package at.tugraz.ist.paintroid.ui.button;

import java.util.Observable;
import java.util.Observer;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint.Cap;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.TextView;
import at.tugraz.ist.paintroid.R;
import at.tugraz.ist.paintroid.dialog.DialogBrushPicker;
import at.tugraz.ist.paintroid.dialog.DialogBrushPicker.OnBrushChangedListener;
import at.tugraz.ist.paintroid.dialog.colorpicker.ColorPickerDialog;
import at.tugraz.ist.paintroid.dialog.colorpicker.ColorPickerDialog.OnColorPickedListener;
import at.tugraz.ist.paintroid.helper.Toolbar;
import at.tugraz.ist.paintroid.tools.Tool;

import com.google.inject.Inject;

public class AttributeButton extends TextView implements OnClickListener, OnLongClickListener, Observer {
	@Inject
	private Toolbar toolbar;

	public AttributeButton(Context context) {
		super(context);
		this.setOnClickListener(this);
		this.setOnLongClickListener(this);
	}

	@Override
	public boolean onLongClick(View view) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onClick(final View view) {
		final Tool currentTool = toolbar.getCurrentTool();
		switch (this.getId()) {
		case R.id.btn_Parameter1:
			switch (currentTool.getToolType()) {
			case MAGIC:
			case CURSOR:
			case BRUSH:
			case PIPETTE:
				OnColorPickedListener colorPickerListener = new OnColorPickedListener() {
					@Override
					public void colorChanged(int color) {
						if (color == Color.TRANSPARENT) {
							Log.d("PAINTROID", "Transparent set");
							view.setBackgroundResource(R.drawable.transparent_64);
						}
						currentTool.getDrawPaint().setColor(color);
					}
				};
				ColorPickerDialog colorpicker = new ColorPickerDialog(this.getContext(), colorPickerListener,
						currentTool.getDrawPaint().getColor());
				colorpicker.show();
				break;
			case FLOATINGBOX:
				// Rotate left
				// TODO
				// if (!((FloatingBoxTool) currentTool).rotate(-90)) {
				// Toast toast = Toast.makeText(this.getContext(),
				// R.string.warning_floating_box_rotate,
				// Toast.LENGTH_SHORT);
				// toast.show();
				// }
				break;
			}
			break;
		case R.id.btn_Parameter2:
			switch (currentTool.getToolType()) {
			case BRUSH:
			case CURSOR:
				OnBrushChangedListener mStroke = new OnBrushChangedListener() {
					@Override
					public void setCap(Cap cap) {
						currentTool.getDrawPaint().setStrokeCap(cap);
					}

					@Override
					public void setStroke(int stroke) {
						currentTool.getDrawPaint().setStrokeWidth(stroke);
					}
				};

				DialogBrushPicker strokepicker = new DialogBrushPicker(this.getContext(), mStroke);
				strokepicker.show();
				break;
			case FLOATINGBOX:
				// Rotate right
				// TODO
				// if (!((FloatingBoxTool) currentTool).rotate(90)) {
				// Toast toast = Toast.makeText(activity, R.string.warning_floating_box_rotate,
				// Toast.LENGTH_SHORT);
				// toast.show();
				// }
			}
			break;
		}
	}

	@Override
	public void update(Observable observable, Object argument) {
		final Tool currentTool = toolbar.getCurrentTool();
		switch (this.getId()) {
		case R.id.btn_Parameter1:
			switch (currentTool.getToolType()) {
			case BRUSH:
				this.setBackgroundColor(currentTool.getDrawPaint().getColor());
			}
		case R.id.btn_Parameter2:
			switch (currentTool.getToolType()) {
			case BRUSH:
				int strokeWidth = (int) currentTool.getDrawPaint().getStrokeWidth();
				switch (currentTool.getDrawPaint().getStrokeCap()) {
				case SQUARE:
					switch (strokeWidth) {

					case 1:
						this.setBackgroundResource(R.drawable.rect_1_32);
						break;
					case 5:
						this.setBackgroundResource(R.drawable.rect_2_32);
						break;
					case 15:
						this.setBackgroundResource(R.drawable.rect_3_32);
						break;
					case 25:
						this.setBackgroundResource(R.drawable.rect_4_32);
						break;
					}
					break;
				case ROUND:
					switch (strokeWidth) {

					case 1:
						this.setBackgroundResource(R.drawable.circle_1_32);
						break;
					case 5:
						this.setBackgroundResource(R.drawable.circle_2_32);
						break;
					case 15:
						this.setBackgroundResource(R.drawable.circle_3_32);
						break;
					case 25:
						this.setBackgroundResource(R.drawable.circle_4_32);
						break;
					}
					break;
				default:
					break;
				}
			}
		}
	}
}
