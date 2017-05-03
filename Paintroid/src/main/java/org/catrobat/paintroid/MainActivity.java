/**
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2015 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 * <p/>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import org.catrobat.paintroid.command.Command;
import org.catrobat.paintroid.command.implementation.CommandManagerImplementation;
import org.catrobat.paintroid.command.implementation.LayerCommand;
import org.catrobat.paintroid.command.implementation.LoadCommand;
import org.catrobat.paintroid.dialog.CustomAlertDialogBuilder;
import org.catrobat.paintroid.dialog.DialogAbout;
import org.catrobat.paintroid.dialog.DialogTermsOfUseAndService;
import org.catrobat.paintroid.dialog.IndeterminateProgressDialog;
import org.catrobat.paintroid.dialog.InfoDialog;
import org.catrobat.paintroid.dialog.InfoDialog.DialogType;
import org.catrobat.paintroid.dialog.LayersDialog;
import org.catrobat.paintroid.dialog.colorpicker.ColorPickerDialog;
import org.catrobat.paintroid.listener.BrushPickerView;
import org.catrobat.paintroid.listener.DrawingSurfaceListener;
import org.catrobat.paintroid.listener.LayerListener;
import org.catrobat.paintroid.tools.Tool;
import org.catrobat.paintroid.tools.ToolFactory;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.tools.implementation.BaseTool;
import org.catrobat.paintroid.tools.implementation.ImportTool;
import org.catrobat.paintroid.ui.BottomBar;
import org.catrobat.paintroid.ui.DrawingSurface;
import org.catrobat.paintroid.ui.Perspective;
import org.catrobat.paintroid.ui.TopBar;
import org.catrobat.paintroid.ui.button.LayersAdapter;

import java.io.File;


public class MainActivity extends NavigationDrawerMenuActivity implements  NavigationView.OnNavigationItemSelectedListener  {

	public static final String EXTRA_INSTANCE_FROM_CATROBAT = "EXTRA_INSTANCE_FROM_CATROBAT";
	public static final String EXTRA_ACTION_BAR_HEIGHT = "EXTRA_ACTION_BAR_HEIGHT";
	protected DrawingSurfaceListener mDrawingSurfaceListener;
	protected BottomBar mBottomBar;
	protected TopBar mTopBar;

	protected boolean mToolbarIsVisible = true;
	ActionBarDrawerToggle actionBarDrawerToggle;
	DrawerLayout drawerLayout;
	private ListView mLayerSideNavList;
	private NavigationView mLayerSideNav;
	public LayersAdapter mLayersAdapter;
	private InputMethodManager mInputMethodManager;
	private boolean mIsKeyboardShown = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {


		Configuration config = getApplicationContext().getResources().getConfiguration();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
			if (config.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL)
				PaintroidApplication.isRTL = true;
		}


		ColorPickerDialog.init(this);
		IndeterminateProgressDialog.init(this);

		BrushPickerView.init(this);

		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		initActionBar();
		mInputMethodManager =  (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

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
				PaintroidApplication.scaleImage = false;
			}
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			getSupportActionBar().setDisplayShowHomeEnabled(true);
		} else {
			PaintroidApplication.openedFromCatroid = false;
		}
		PaintroidApplication.orientation = getResources().getConfiguration().orientation;
		PaintroidApplication.drawingSurface = (DrawingSurface) findViewById(R.id.drawingSurfaceView);
		PaintroidApplication.perspective = new Perspective(
				((SurfaceView) PaintroidApplication.drawingSurface).getHolder());
		mDrawingSurfaceListener = new DrawingSurfaceListener();
		mBottomBar = new BottomBar(this);
		mTopBar = new TopBar(this, PaintroidApplication.openedFromCatroid);
		mLayerSideNav = (NavigationView) findViewById(R.id.nav_view_layer);
		mLayerSideNavList = (ListView) findViewById(R.id.nav_layer_list);
		mLayersAdapter = new LayersAdapter(this, PaintroidApplication.openedFromCatroid,
				PaintroidApplication.drawingSurface.getBitmapCopy());

		int colorPickerBackgroundColor = PaintroidApplication.colorPickerInitialColor;
		ColorPickerDialog.getInstance().setInitialColor(colorPickerBackgroundColor);


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
							handleAndAssignImage(bitmap);
						}

						private void handleAndAssignImage(Bitmap bitmap) {
							initialiseNewBitmap();
							Command command = new LoadCommand(bitmap);
							PaintroidApplication.commandManager.commitCommandToLayer(
									new LayerCommand(LayerListener.getInstance().getCurrentLayer()), command);

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

		LayerListener.init(this, mLayerSideNav, PaintroidApplication.drawingSurface.getBitmapCopy());

		if(!((CommandManagerImplementation) PaintroidApplication.commandManager).isCommandManagerInitialized())
			initCommandManager();

		initNavigationDrawer();
		initKeyboardIsShownListener();
	}

	private void initCommandManager() {
		PaintroidApplication.commandManager = new CommandManagerImplementation();

		//((CommandManagerImplementation) PaintroidApplication.commandManager)
		//		.setRefreshLayerDialogListener(LayersDialog.getInstance());

		((CommandManagerImplementation) PaintroidApplication.commandManager)
				.setUpdateTopBarListener(mTopBar);

		((CommandManagerImplementation) PaintroidApplication.commandManager)
				.addChangeActiveLayerListener(LayerListener.getInstance());

		((CommandManagerImplementation) PaintroidApplication.commandManager)
				.setLayerEventListener(LayerListener.getInstance().getAdapter());


		PaintroidApplication.commandManager.commitAddLayerCommand(
				new LayerCommand(LayerListener.getInstance().getAdapter().getLayer(0)));

		((CommandManagerImplementation) PaintroidApplication.commandManager).setInitialized(true);

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

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

		setSupportActionBar(toolbar);


		getSupportActionBar().setDisplayShowTitleEnabled(false);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);

		actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				drawerLayout.requestLayout();
			}
			public void onDrawerClosed(View drawerView) {
				super.onDrawerClosed(drawerView);
			}
		};

		drawerLayout.setDrawerListener(actionBarDrawerToggle);

		actionBarDrawerToggle.syncState();

	}

	@Override
	public void onDetachedFromWindow() {
		IndeterminateProgressDialog.getInstance().dismiss();
		super.onDetachedFromWindow();
	}

	@Override
	protected void onDestroy() {
		PaintroidApplication.drawingSurface.recycleBitmap();
		PaintroidApplication.currentTool.changePaintStrokeCap(Cap.ROUND);
		PaintroidApplication.currentTool.changePaintStrokeWidth(25);
		PaintroidApplication.isPlainImage = true;
		PaintroidApplication.savedPictureUri = null;
		PaintroidApplication.saveCopy = false;

		((CommandManagerImplementation) PaintroidApplication.commandManager).storeCommandLists();
		((CommandManagerImplementation) PaintroidApplication.commandManager).setInitialized(false);

		IndeterminateProgressDialog.getInstance().dismiss();
		ColorPickerDialog.getInstance().dismiss();
		super.onDestroy();
	}


	@Override
	public boolean onNavigationItemSelected(MenuItem item) {

		switch (item.getItemId()) {
			case R.id.nav_back_to_pocket_code:
				showSecurityQuestionBeforeExit();
				drawerLayout.closeDrawers();
				return true;
			case R.id.nav_save_image:
				SaveTask saveTask = new SaveTask(this);
				saveTask.execute();
				drawerLayout.closeDrawers();
				return true;
			case R.id.nav_save_duplicate:
				PaintroidApplication.saveCopy = true;
				SaveTask saveCopyTask = new SaveTask(this);
				saveCopyTask.execute();
				drawerLayout.closeDrawers();
				return true;
			case R.id.nav_open_image:
				onLoadImage();
				drawerLayout.closeDrawers();
				return true;
			case R.id.nav_new_image:
				saveImage();
				drawerLayout.closeDrawers();
				return true;
			case R.id.nav_fullscreen_mode:
				setFullScreen(true);
				drawerLayout.closeDrawers();
				return true;
			case R.id.nav_exit_fullscreen_mode:
				setFullScreen(false);
				drawerLayout.closeDrawers();
				return true;
			case R.id.nav_tos:
				DialogTermsOfUseAndService termsOfUseAndService = new DialogTermsOfUseAndService();
				termsOfUseAndService.show(getSupportFragmentManager(),
						"termsofuseandservicedialogfragment");
				drawerLayout.closeDrawers();
				return true;
			case R.id.nav_help:
				Intent intent = new Intent(this, WelcomeActivity.class);
				intent.setFlags(1);
				startActivity(intent);
				drawerLayout.closeDrawers();
				finish();
				return true;
			case R.id.nav_about:
				DialogAbout about = new DialogAbout();
				about.show(getSupportFragmentManager(), "aboutdialogfragment");
				drawerLayout.closeDrawers();
				return true;
      case R.id.nav_lang:
        Intent language = new Intent(this, Multilingual.class);
        startActivity(language);
        finish();
        return true;
		}

		return true;
	}

	@Override
	public void onBackPressed() {
		if (!mToolbarIsVisible) {
			setFullScreen(false);
		} else if (PaintroidApplication.currentTool.getToolOptionsAreShown()) {
			PaintroidApplication.currentTool.toggleShowToolOptions();
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
		Paint tempPaint = new Paint(PaintroidApplication.currentTool.getDrawPaint());
		if (tool != null) {
			mBottomBar.setTool(tool);
			PaintroidApplication.currentTool = tool;
			PaintroidApplication.currentTool.setDrawPaint(tempPaint);
		}
	}

	private void showSecurityQuestionBeforeExit() {
		if (PaintroidApplication.isSaved
				|| (LayerListener.getInstance().getAdapter().getLayers().size() == 1)
				&& PaintroidApplication.isPlainImage
				&& !PaintroidApplication.commandManager.checkIfDrawn()) {
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

		NavigationView mNavigationView = (NavigationView) findViewById(R.id.nav_view);
		mNavigationView.setNavigationItemSelectedListener(this);
		LinearLayout mToolOptions = (LinearLayout) findViewById(R.id.main_tool_options);

		if (isFullScreen) {
			PaintroidApplication.currentTool.hide(true);
			getSupportActionBar().hide();
			LinearLayout bottomBarLayout = (LinearLayout) findViewById(R.id.main_bottom_bar);
			if(PaintroidApplication.orientation == Configuration.ORIENTATION_LANDSCAPE)
			{
				LinearLayout mToolbarContainer = (LinearLayout)(findViewById(R.id.toolbar_container));
				mToolbarContainer.setVisibility(View.GONE);
			}
			bottomBarLayout.setVisibility(View.GONE);
			mToolbarIsVisible = false;
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
			getWindow().clearFlags(
					WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

			mNavigationView.getMenu().findItem(R.id.nav_exit_fullscreen_mode).setVisible(true);
			mNavigationView.getMenu().findItem(R.id.nav_fullscreen_mode).setVisible(false);

		} else {
			getSupportActionBar().show();
			LinearLayout bottomBarLayout = (LinearLayout) findViewById(R.id.main_bottom_bar);
			if(PaintroidApplication.orientation == Configuration.ORIENTATION_LANDSCAPE)
			{
				LinearLayout mToolbarContainer = (LinearLayout)(findViewById(R.id.toolbar_container));
				mToolbarContainer.setVisibility(View.VISIBLE);
			}
			bottomBarLayout.setVisibility(View.VISIBLE);
			mToolbarIsVisible = true;
			getWindow().addFlags(
					WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

			mNavigationView.getMenu().findItem(R.id.nav_exit_fullscreen_mode).setVisible(false);
			mNavigationView.getMenu().findItem(R.id.nav_fullscreen_mode).setVisible(true);
		}
	}

	private void initNavigationDrawer()
	{
		NavigationView mNavigationView = (NavigationView) findViewById(R.id.nav_view);
		mNavigationView.setNavigationItemSelectedListener(this);

		if(!PaintroidApplication.openedFromCatroid)
			mNavigationView.getMenu().removeItem(R.id.nav_back_to_pocket_code);

		if(PaintroidApplication.perspective.getFullscreen())
			mNavigationView.getMenu().findItem(R.id.nav_fullscreen_mode).setVisible(false);
		else
			mNavigationView.getMenu().findItem(R.id.nav_exit_fullscreen_mode).setVisible(false);
	}


	public boolean isKeyboardShown() {
		return mIsKeyboardShown;
	}
	public void hideKeyboard() {
		mInputMethodManager.hideSoftInputFromWindow(getWindow().getDecorView().getRootView().getWindowToken(), 0);
	}

	private void initKeyboardIsShownListener() {
		final View activityRootView = findViewById(R.id.main_layout);
		activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				int heightDiff = activityRootView.getRootView().getHeight() - activityRootView.getHeight();
				if(heightDiff > 300) {
					mIsKeyboardShown = true;
				}
				else {
					mIsKeyboardShown = false;
				}

			}
		});
	}

}
