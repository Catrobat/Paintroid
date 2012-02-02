package at.tugraz.ist.paintroid.ui.button;

import java.util.Observable;
import java.util.Observer;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.TextView;
import at.tugraz.ist.paintroid.R;
import at.tugraz.ist.paintroid.tools.Tool;
import at.tugraz.ist.paintroid.ui.Toolbar;

public class AttributeButton extends TextView implements OnClickListener, OnLongClickListener, Observer {

	protected Toolbar toolbar;
	protected int buttonNumber;

	public AttributeButton(Context context) {
		super(context);
		init(context);
	}

	public AttributeButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public AttributeButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	protected void init(Context context) {
		this.setOnClickListener(this);
		this.setOnLongClickListener(this);
		switch (this.getId()) {
		case R.id.btn_Parameter1:
			buttonNumber = 1;
			break;
		case R.id.btn_Parameter2:
			buttonNumber = 2;
			break;
		default:
			buttonNumber = 0;
			break;
		}
	}

	public void setToolbar(Toolbar toolbar) {
		this.toolbar = toolbar;
		((Observable) toolbar).addObserver(this);
		update((Observable) toolbar, null);
	}

	@Override
	public boolean onLongClick(View view) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onClick(final View view) {
		final Tool currentTool = toolbar.getCurrentTool();
		currentTool.attributeButtonClick(buttonNumber);
	}

	@Override
	public void update(Observable observable, Object argument) {
		if (observable instanceof Toolbar) {
			Observable tool = (Observable) toolbar.getCurrentTool();
			tool.addObserver(this);
		}
		final Tool currentTool = toolbar.getCurrentTool();
		int resource = currentTool.getAttributeButtonResource(buttonNumber);
		if (resource == 0) {
			int color = currentTool.getAttributeButtonColor(buttonNumber);
			this.setBackgroundColor(color);
		} else {
			this.setBackgroundResource(resource);
		}
	}
}
