/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *  
 *  Paintroid: An image manipulation application for Android, part of the
 *  Catroid project and Catroid suite of software.
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package at.tugraz.ist.paintroid;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import at.tugraz.ist.paintroid.dialog.DialogAbout;
import at.tugraz.ist.paintroid.listener.DrawingSurfaceListener;
import at.tugraz.ist.paintroid.tools.Tool;
import at.tugraz.ist.paintroid.tools.Tool.ToolType;
import at.tugraz.ist.paintroid.tools.implementation.StampTool;
import at.tugraz.ist.paintroid.ui.Toolbar;
import at.tugraz.ist.paintroid.ui.button.ToolbarButton;
import at.tugraz.ist.paintroid.ui.implementation.DrawingSurfaceImplementation;
import at.tugraz.ist.paintroid.ui.implementation.PerspectiveImplementation;
import at.tugraz.ist.paintroid.ui.implementation.ToolbarImplementation;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class MainActivity extends MenuFileActivity {

	public static final String EXTRA_INSTANCE_FROM_CATROBAT = "EXTRA_INSTANCE_FROM_CATROBAT";
	public static final String EXTRA_ACTION_BAR_HEIGHT = "EXTRA_ACTION_BAR_HEIGHT";

	private static final int EXTRA_SELECTED_TOOL_DEFAULT_VALUE = -1;

	protected DrawingSurfaceListener mDrawingSurfaceListener;
	protected Toolbar mToolbar;

	protected boolean mToolbarIsVisible = true;
	protected boolean mOpenedWithCatroid;
	private Menu mMenu = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		String catroidPicturePath = null;
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			catroidPicturePath = extras.getString(getString(R.string.extra_picture_path_catroid));
		}
		if (catroidPicturePath != null) {
			mOpenedWithCatroid = true;
		}

		initPaintroidStatusBar();
		setContentView(R.layout.main);

		PaintroidApplication.DRAWING_SURFACE = (DrawingSurfaceImplementation) findViewById(R.id.drawingSurfaceView);
		PaintroidApplication.CURRENT_PERSPECTIVE = new PerspectiveImplementation(
				((SurfaceView) PaintroidApplication.DRAWING_SURFACE).getHolder());
		mDrawingSurfaceListener = new DrawingSurfaceListener();
		mToolbar = new ToolbarImplementation(this, mOpenedWithCatroid);

		((View) PaintroidApplication.DRAWING_SURFACE).setOnTouchListener(mDrawingSurfaceListener);

		ComponentName componentName = getIntent().getComponent();
		String className = componentName.getShortClassName();
		boolean isMainActivityPhoto = className.equals(getString(R.string.activity_alias_photo));
		if (mOpenedWithCatroid && isMainActivityPhoto) {
			takePhoto();
		}
		if (mOpenedWithCatroid && catroidPicturePath.length() > 0) {
			loadBitmapFromFileAndRun(new File(catroidPicturePath), new RunnableWithBitmap() {
				@Override
				public void run(Bitmap bitmap) {
					PaintroidApplication.DRAWING_SURFACE.resetBitmap(bitmap);
				}
			});
		} else {
			initialiseNewBitmap();
		}

	}

	private void initPaintroidStatusBar() {
		getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		getSupportActionBar().setCustomView(R.layout.status_bar);
		if (mOpenedWithCatroid) {
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		} else {
			getSupportActionBar().setDisplayShowHomeEnabled(false);
			getSupportActionBar().setDisplayShowTitleEnabled(false);
		}
		getSupportActionBar().setDisplayShowCustomEnabled(true);
	}

	@Override
	protected void onDestroy() {
		((DrawingSurfaceImplementation) PaintroidApplication.DRAWING_SURFACE).recycleBitmap();
		PaintroidApplication.COMMAND_MANAGER.resetAndClear();
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getSupportMenuInflater().inflate(R.menu.main_menu, menu);
		mMenu = menu;
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
			case R.id.menu_item_tools:
				openToolDialog();
				return true;
			case R.id.menu_item_primary_tool_attribute_button:
				if (PaintroidApplication.CURRENT_TOOL != null) {
					PaintroidApplication.CURRENT_TOOL
							.attributeButtonClick(ToolbarButton.ToolButtonIDs.BUTTON_ID_PARAMETER_BOTTOM_1);
				}
				return true;
			case R.id.menu_item_secondary_tool_attribute_button:
				if (PaintroidApplication.CURRENT_TOOL != null) {
					PaintroidApplication.CURRENT_TOOL
							.attributeButtonClick(ToolbarButton.ToolButtonIDs.BUTTON_ID_PARAMETER_BOTTOM_2);
				}
				return true;
			case R.id.menu_item_quit:
				showSecurityQuestionBeforeExit();
				return true;
			case R.id.menu_item_about:
				DialogAbout about = new DialogAbout(this);
				about.show();
				return true;
			case R.id.menu_item_hide_menu:
				setFullScreen(mToolbarIsVisible);
				return true;
			case android.R.id.home:
				if (mOpenedWithCatroid) {
					showSecurityQuestionBeforeExit();
				}
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		MenuItem hideMenuButton = menu.findItem(R.id.menu_item_hide_menu);
		if (mToolbarIsVisible) {
			hideMenuButton.setTitle(R.string.menu_hide_menu);
		} else {
			setFullScreen(false);
			return false;
		}
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public void onBackPressed() {
		if (!mToolbarIsVisible) {
			setFullScreen(false);

		} else if (PaintroidApplication.CURRENT_TOOL.getToolType() == ToolType.BRUSH) {
			showSecurityQuestionBeforeExit();
		} else {
			switchTool(ToolType.BRUSH);
		}

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != Activity.RESULT_OK) {
			Log.d(PaintroidApplication.TAG, "onActivityResult: result not ok, most likely a dialog hast been canceled");
			return;
		}

		switch (requestCode) {
			case REQ_TOOLS_DIALOG:
				handleToolsDialogResult(data);
				break;
			case REQ_IMPORTPNG:
				Uri selectedGalleryImage = data.getData();
				String imageFilePath = at.tugraz.ist.paintroid.FileIO.getRealPathFromURI(this, selectedGalleryImage);
				importPngToFloatingBox(imageFilePath);
				break;
			case REQ_FINISH:
				finish();
				break;
			default:
				super.onActivityResult(requestCode, resultCode, data);
		}
	}

	public void openToolDialog() {
		Intent intent = new Intent(this, ToolsDialogActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
		intent.putExtra(EXTRA_INSTANCE_FROM_CATROBAT, mOpenedWithCatroid);
		intent.putExtra(EXTRA_ACTION_BAR_HEIGHT, getSupportActionBar().getHeight());
		startActivityForResult(intent, REQ_TOOLS_DIALOG);
		overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
	}

	private void handleToolsDialogResult(Intent data) {
		int selectedToolButtonId = data.getIntExtra(ToolsDialogActivity.EXTRA_SELECTED_TOOL,
				EXTRA_SELECTED_TOOL_DEFAULT_VALUE);

		if (selectedToolButtonId <= EXTRA_SELECTED_TOOL_DEFAULT_VALUE) {
			Log.e(PaintroidApplication.TAG, "selected tool id is smaller" + EXTRA_SELECTED_TOOL_DEFAULT_VALUE);
			return;
		}

		if (ToolType.values().length > selectedToolButtonId) {
			ToolType tooltype = ToolType.values()[selectedToolButtonId];
			switch (tooltype) {
				case REDO:
					PaintroidApplication.COMMAND_MANAGER.redo();
					break;
				case UNDO:
					PaintroidApplication.COMMAND_MANAGER.undo();
					break;
				case IMPORTPNG:
					importPng();
					break;
				default:
					switchTool(tooltype);
					break;
			}
		}
	}

	private void importPng() {
		Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
		startActivityForResult(intent, REQ_IMPORTPNG);
	}

	private void switchTool(ToolType changeToToolType) {
		Paint tempPaint = new Paint(PaintroidApplication.CURRENT_TOOL.getDrawPaint());
		Tool tool = Utils.createTool(changeToToolType, this, PaintroidApplication.DRAWING_SURFACE);
		if (tool != null) {
			mToolbar.setTool(tool);
			PaintroidApplication.CURRENT_TOOL = tool;
			PaintroidApplication.CURRENT_TOOL.setDrawPaint(tempPaint);
			MenuItem primaryAttributeItem = mMenu.findItem(R.id.menu_item_primary_tool_attribute_button);
			MenuItem secondaryAttributeItem = mMenu.findItem(R.id.menu_item_secondary_tool_attribute_button);
			primaryAttributeItem.setIcon(tool
					.getAttributeButtonResource(ToolbarButton.ToolButtonIDs.BUTTON_ID_PARAMETER_BOTTOM_1));
			secondaryAttributeItem.setIcon(tool
					.getAttributeButtonResource(ToolbarButton.ToolButtonIDs.BUTTON_ID_PARAMETER_BOTTOM_2));
		}
	}

	private void importPngToFloatingBox(String filePath) {
		switchTool(ToolType.STAMP);
		loadBitmapFromFileAndRun(new File(filePath), new RunnableWithBitmap() {
			@Override
			public void run(Bitmap bitmap) {
				if (PaintroidApplication.CURRENT_TOOL instanceof StampTool) {
					StampTool tool = (StampTool) PaintroidApplication.CURRENT_TOOL;
					tool.addBitmap(bitmap);
				} else {
					Log.e(PaintroidApplication.TAG,
							"importPngToFloatingBox: Current tool is no StampTool, but StampTool required");
				}
			}
		});
	}

	private void showSecurityQuestionBeforeExit() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		if (mOpenedWithCatroid) {
			builder.setMessage(getString(R.string.closing_catroid_security_question));
			builder.setCancelable(true);
			builder.setPositiveButton(R.string.closing_catroid_security_question_use_picture,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int id) {
							exitToCatroid();
						}
					});
			builder.setNegativeButton(R.string.closing_catroid_security_question_discard_picture,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int id) {
							finish();
						}
					});
		} else {
			builder.setMessage(R.string.closing_security_question);
			builder.setCancelable(true);
			builder.setPositiveButton(R.string.closing_security_question_yes, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int id) {
					finish();
				}
			});
			builder.setNegativeButton(R.string.closing_security_question_not, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int id) {
					dialog.cancel();
				}
			});
		}
		AlertDialog alert = builder.create();
		alert.show();
	}

	private void exitToCatroid() {
		String pictureFileName = getString(R.string.temp_picture_name);
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			String catroidPictureName = extras.getString(getString(R.string.extra_picture_name_catroid));
			if (catroidPictureName != null) {
				if (catroidPictureName.length() > 0) {
					pictureFileName = catroidPictureName;
				}
			}
		}
		File file = FileIO.saveBitmap(MainActivity.this, PaintroidApplication.DRAWING_SURFACE.getBitmap(),
				pictureFileName);

		Intent resultIntent = new Intent();

		if (file != null) {
			Bundle bundle = new Bundle();
			bundle.putString(getString(R.string.extra_picture_path_catroid), file.getAbsolutePath());
			resultIntent.putExtras(bundle);
			setResult(RESULT_OK, resultIntent);
		} else {
			setResult(RESULT_CANCELED, resultIntent);
		}
		finish();
	}

	private void setFullScreen(boolean isFullScreen) {
		if (isFullScreen) {
			getSupportActionBar().hide();
			mToolbarIsVisible = false;
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		} else {
			getSupportActionBar().show();
			mToolbarIsVisible = true;
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}
	}

}
