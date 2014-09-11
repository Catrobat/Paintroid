/**
 *  Paintroid: An image manipulation application for Android.
 *  Copyright (C) 2010-2014 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid;

import java.io.File;

import org.catrobat.paintroid.dialog.BrushPickerDialog;
import org.catrobat.paintroid.dialog.CustomAlertDialogBuilder;
import org.catrobat.paintroid.dialog.DialogAbout;
import org.catrobat.paintroid.dialog.DialogTermsOfUseAndService;
import org.catrobat.paintroid.dialog.IndeterminateProgressDialog;
import org.catrobat.paintroid.dialog.InfoDialog;
import org.catrobat.paintroid.dialog.InfoDialog.DialogType;
import org.catrobat.paintroid.dialog.ToolsDialog;
import org.catrobat.paintroid.dialog.colorpicker.ColorPickerDialog;
import org.catrobat.paintroid.listener.DrawingSurfaceListener;
import org.catrobat.paintroid.tools.Tool;
import org.catrobat.paintroid.tools.ToolFactory;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.tools.implementation.ImportTool;
import org.catrobat.paintroid.ui.BottomBar;
import org.catrobat.paintroid.ui.DrawingSurface;
import org.catrobat.paintroid.ui.Perspective;
import org.catrobat.paintroid.ui.TopBar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class MainActivity extends OptionsMenuActivity {

	public static final String EXTRA_INSTANCE_FROM_CATROBAT = "EXTRA_INSTANCE_FROM_CATROBAT";
	public static final String EXTRA_ACTION_BAR_HEIGHT = "EXTRA_ACTION_BAR_HEIGHT";
	protected DrawingSurfaceListener mDrawingSurfaceListener;
	protected TopBar mTopBar;
	protected BottomBar mBottomBar;

	protected boolean mToolbarIsVisible = true;
	private Menu mMenu = null;
	private static final int ANDROID_VERSION_ICE_CREAM_SANDWICH = 14;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		ColorPickerDialog.init(this);
		BrushPickerDialog.init(this);
		ToolsDialog.init(this);
		IndeterminateProgressDialog.init(this);

		/**
		 * EXCLUDED PREFERENCES FOR RELEASE /*SharedPreferences
		 * sharedPreferences = PreferenceManager
		 * .getDefaultSharedPreferences(this); String languageString =
		 * sharedPreferences.getString(
		 * getString(R.string.preferences_language_key), "nolang");
		 * 
		 * if (languageString.equals("nolang")) {
		 * Log.e(PaintroidApplication.TAG, "no language preference exists"); }
		 * else { Log.i(PaintroidApplication.TAG, "load language: " +
		 * languageString); Configuration config =
		 * getBaseContext().getResources() .getConfiguration(); config.locale =
		 * new Locale(languageString);
		 * getBaseContext().getResources().updateConfiguration(config,
		 * getBaseContext().getResources().getDisplayMetrics()); }
		 */

		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		// setDefaultPreferences();
		initActionBar();

		PaintroidApplication.catroidPicturePath = null;
		String catroidPicturePath = null;
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			catroidPicturePath = extras
					.getString(getString(R.string.extra_picture_path_catroid));

			Log.d(PaintroidApplication.TAG, "catroidPicturePath: "
					+ catroidPicturePath);
		}
		if (catroidPicturePath != null) {
			PaintroidApplication.openedFromCatroid = true;
			if (!catroidPicturePath.equals("")) {
				PaintroidApplication.catroidPicturePath = catroidPicturePath;
			}
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			getSupportActionBar().setDisplayShowHomeEnabled(true);
		} else {
			PaintroidApplication.openedFromCatroid = false;
		}

		PaintroidApplication.drawingSurface = (DrawingSurface) findViewById(R.id.drawingSurfaceView);
		PaintroidApplication.perspective = new Perspective(
				((SurfaceView) PaintroidApplication.drawingSurface).getHolder());
		mDrawingSurfaceListener = new DrawingSurfaceListener();
		mTopBar = new TopBar(this, PaintroidApplication.openedFromCatroid);
		mBottomBar = new BottomBar(this);

		PaintroidApplication.drawingSurface
				.setOnTouchListener(mDrawingSurfaceListener);

		if (PaintroidApplication.openedFromCatroid
				&& catroidPicturePath != null
				&& catroidPicturePath.length() > 0) {
			loadBitmapFromUriAndRun(Uri.fromFile(new File(catroidPicturePath)),
					new RunnableWithBitmap() {
						@SuppressLint("NewApi")
						@Override
						public void run(Bitmap bitmap) {
							if (!bitmap.hasAlpha()) {

								if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
									bitmap.setHasAlpha(true);
								} else {
									bitmap = addAlphaChannel(bitmap);
								}
							}
							PaintroidApplication.drawingSurface
									.resetBitmap(bitmap);
						}

						private Bitmap addAlphaChannel(Bitmap src) {
							int width = src.getWidth();
							int height = src.getHeight();
							Bitmap dest = Bitmap.createBitmap(width, height,
									Bitmap.Config.ARGB_8888);

							int[] pixels = new int[width * height];
							src.getPixels(pixels, 0, width, 0, 0, width, height);
							dest.setPixels(pixels, 0, width, 0, 0, width,
									height);

							src.recycle();
							return dest;
						}
					});

		} else {
			initialiseNewBitmap();
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		checkIfLoadBitmapFailed();
	}

	public void checkIfLoadBitmapFailed() {
		if (loadBitmapFailed) {
			loadBitmapFailed = false;
			new InfoDialog(DialogType.WARNING,
					R.string.dialog_loading_image_failed_title,
					R.string.dialog_loading_image_failed_text).show(
					getSupportFragmentManager(), "loadbitmapdialogerror");
		}
	}

	private void initActionBar() {

		getSupportActionBar().setCustomView(R.layout.top_bar);
		getSupportActionBar().setDisplayShowHomeEnabled(false);
		getSupportActionBar().setDisplayShowCustomEnabled(true);
		if (Build.VERSION.SDK_INT < ANDROID_VERSION_ICE_CREAM_SANDWICH) {
			Bitmap bitmapActionBarBackground = Bitmap.createBitmap(1, 1,
					Config.ARGB_8888);
			bitmapActionBarBackground.eraseColor(getResources().getColor(
					R.color.custom_background_color));
			Drawable drawable = new BitmapDrawable(getResources(),
					bitmapActionBarBackground);
			getSupportActionBar().setBackgroundDrawable(drawable);
			getSupportActionBar().setSplitBackgroundDrawable(drawable);
		}
	}

	@Override
	public void onDetachedFromWindow() {
		IndeterminateProgressDialog.getInstance().dismiss();
		super.onDetachedFromWindow();
	}

	@Override
	protected void onDestroy() {

		PaintroidApplication.commandManager.resetAndClear();
		PaintroidApplication.drawingSurface.recycleBitmap();
		ColorPickerDialog.getInstance().setInitialColor(
				getResources().getColor(R.color.color_chooser_black));
		PaintroidApplication.currentTool.changePaintStrokeCap(Cap.ROUND);
		PaintroidApplication.currentTool.changePaintStrokeWidth(25);
		PaintroidApplication.isPlainImage = true;
		PaintroidApplication.savedPictureUri = null;
		PaintroidApplication.saveCopy = false;

		ToolsDialog.getInstance().dismiss();
		IndeterminateProgressDialog.getInstance().dismiss();
		ColorPickerDialog.getInstance().dismiss();
		// BrushPickerDialog.getInstance().dismiss(); // TODO: how can there
		// ever be a null pointer exception?
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		mMenu = menu;
		PaintroidApplication.menu = mMenu;
		MenuInflater inflater = getSupportMenuInflater();
		if (PaintroidApplication.openedFromCatroid) {
			inflater.inflate(R.menu.main_menu_opened_from_catroid, menu);
		} else {
			inflater.inflate(R.menu.main_menu, menu);
		}

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.menu_item_back_to_catroid:
			showSecurityQuestionBeforeExit();
			return true;
		case R.id.menu_item_terms_of_use_and_service:
			DialogTermsOfUseAndService termsOfUseAndService = new DialogTermsOfUseAndService();
			termsOfUseAndService.show(getSupportFragmentManager(),
					"termsofuseandservicedialogfragment");
			return true;
		case R.id.menu_item_about:
			DialogAbout about = new DialogAbout();
			about.show(getSupportFragmentManager(), "aboutdialogfragment");
			return true;
		case R.id.menu_item_hide_menu:
			setFullScreen(mToolbarIsVisible);
			return true;
		case android.R.id.home:
			if (PaintroidApplication.openedFromCatroid) {
				showSecurityQuestionBeforeExit();
			}
			return true;
			/* EXCLUDE PREFERENCES FOR RELEASE */
			// case R.id.menu_item_preferences:
			// Intent intent = new Intent(this, SettingsActivity.class);
			// intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
			// startActivity(intent);
			// return false;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		if (mToolbarIsVisible == false) {
			setFullScreen(false);
			return true;
		}
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public void onBackPressed() {
		if (!mToolbarIsVisible) {
			setFullScreen(false);

		} else if (PaintroidApplication.currentTool.getToolType() == ToolType.BRUSH) {
			showSecurityQuestionBeforeExit();
		} else {
			switchTool(ToolType.BRUSH);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != Activity.RESULT_OK) {
			Log.d(PaintroidApplication.TAG,
					"onActivityResult: result not ok, most likely a dialog hast been canceled");
			return;
		}

		switch (requestCode) {
		case REQUEST_CODE_IMPORTPNG:
			Uri selectedGalleryImageUri = data.getData();
			Tool tool = ToolFactory.createTool(this, ToolType.IMPORTPNG);
			switchTool(tool);

			loadBitmapFromUriAndRun(selectedGalleryImageUri,
					new RunnableWithBitmap() {
						@Override
						public void run(Bitmap bitmap) {
							if (PaintroidApplication.currentTool instanceof ImportTool) {
								((ImportTool) PaintroidApplication.currentTool)
										.setBitmapFromFile(bitmap);

							} else {
								Log.e(PaintroidApplication.TAG,
										"importPngToFloatingBox: Current tool is no ImportTool as required");
							}
						}
					});

			break;
		case REQUEST_CODE_FINISH:
			finish();
			break;
		default:
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	private void importPng() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("image/*");
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
		startActivityForResult(intent, REQUEST_CODE_IMPORTPNG);
	}

	public synchronized void switchTool(ToolType changeToToolType) {

		switch (changeToToolType) {
		case REDO:
			PaintroidApplication.commandManager.redo();
			break;
		case UNDO:
			PaintroidApplication.commandManager.undo();
			break;
		case IMPORTPNG:
			importPng();
			break;
		default:
			Tool tool = ToolFactory.createTool(this, changeToToolType);
			switchTool(tool);
			break;
		}

	}

	public synchronized void switchTool(Tool tool) {
		Paint tempPaint = new Paint(
				PaintroidApplication.currentTool.getDrawPaint());
		if (tool != null) {
			mTopBar.setTool(tool);
			mBottomBar.setTool(tool);
			PaintroidApplication.currentTool = tool;
			PaintroidApplication.currentTool.setDrawPaint(tempPaint);
		}
	}

	private void showSecurityQuestionBeforeExit() {
		if (PaintroidApplication.isSaved
				|| !PaintroidApplication.commandManager.hasCommands()
				&& PaintroidApplication.isPlainImage) {
			finish();
			return;
		} else {
			AlertDialog.Builder builder = new CustomAlertDialogBuilder(this);
			if (PaintroidApplication.openedFromCatroid) {
				builder.setTitle(R.string.closing_catroid_security_question_title);
				builder.setMessage(R.string.closing_security_question);
				builder.setPositiveButton(R.string.save_button_text,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								exitToCatroid();
							}
						});
				builder.setNegativeButton(R.string.discard_button_text,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								finish();
							}
						});
			} else {
				builder.setTitle(R.string.closing_security_question_title);
				builder.setMessage(R.string.closing_security_question);
				builder.setPositiveButton(R.string.save_button_text,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								saveFileBeforeExit();
								finish();
							}
						});
				builder.setNegativeButton(R.string.discard_button_text,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								finish();
							}
						});
			}
			builder.setCancelable(true);
			AlertDialog alert = builder.create();
			alert.show();
		}
	}

	private void saveFileBeforeExit() {
		saveFile();
	}

	private void exitToCatroid() {
		String pictureFileName = getString(R.string.temp_picture_name);

		if (PaintroidApplication.catroidPicturePath != null) {
			pictureFileName = PaintroidApplication.catroidPicturePath;
		} else {
			Bundle extras = getIntent().getExtras();
			if (extras != null) {
				String catroidPictureName = extras
						.getString(getString(R.string.extra_picture_name_catroid));
				if (catroidPictureName != null
						&& catroidPictureName.length() > 0) {
					pictureFileName = catroidPictureName;
				}
			}
			pictureFileName = FileIO.createNewEmptyPictureFile(this,
					pictureFileName).getAbsolutePath();
		}

		Intent resultIntent = new Intent();

		if (FileIO.saveBitmap(MainActivity.this,
				PaintroidApplication.drawingSurface.getBitmapCopy(),
				pictureFileName)) {
			Bundle bundle = new Bundle();
			bundle.putString(getString(R.string.extra_picture_path_catroid),
					pictureFileName);
			resultIntent.putExtras(bundle);
			setResult(RESULT_OK, resultIntent);
		} else {
			setResult(RESULT_CANCELED, resultIntent);
		}
		finish();
	}

	private void setFullScreen(boolean isFullScreen) {
		PaintroidApplication.perspective.setFullscreen(isFullScreen);
		if (isFullScreen) {
			getSupportActionBar().hide();
			LinearLayout bottomBarLayout = (LinearLayout) findViewById(R.id.main_bottom_bar);
			bottomBarLayout.setVisibility(View.GONE);
			mToolbarIsVisible = false;
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
			getWindow().clearFlags(
					WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		} else {
			getSupportActionBar().show();
			LinearLayout bottomBarLayout = (LinearLayout) findViewById(R.id.main_bottom_bar);
			bottomBarLayout.setVisibility(View.VISIBLE);
			mToolbarIsVisible = true;
			getWindow().addFlags(
					WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}
	}

	/* EXCLUDE PREFERENCES FOR RELEASE */
	// private void setDefaultPreferences() {
	// PreferenceManager
	// .setDefaultValues(this, R.xml.preferences_tools, false);
	// }

}
