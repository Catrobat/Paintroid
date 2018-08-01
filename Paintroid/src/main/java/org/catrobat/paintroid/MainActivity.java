/*
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2015 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.catrobat.paintroid.command.Command;
import org.catrobat.paintroid.command.CommandFactory;
import org.catrobat.paintroid.command.CommandManager;
import org.catrobat.paintroid.command.implementation.AsyncCommandManager;
import org.catrobat.paintroid.command.implementation.DefaultCommandFactory;
import org.catrobat.paintroid.command.implementation.DefaultCommandManager;
import org.catrobat.paintroid.common.CommonFactory;
import org.catrobat.paintroid.contract.LayerContracts;
import org.catrobat.paintroid.contract.MainActivityContracts;
import org.catrobat.paintroid.dialog.colorpicker.ColorPickerDialog;
import org.catrobat.paintroid.iotasks.CreateFileAsync;
import org.catrobat.paintroid.iotasks.LoadImageAsync;
import org.catrobat.paintroid.iotasks.SaveImageAsync;
import org.catrobat.paintroid.model.LayerModel;
import org.catrobat.paintroid.tools.Tool;
import org.catrobat.paintroid.tools.ToolFactory;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.tools.implementation.BaseTool;
import org.catrobat.paintroid.tools.implementation.DefaultToolFactory;
import org.catrobat.paintroid.tools.implementation.ImportTool;
import org.catrobat.paintroid.ui.BottomBar;
import org.catrobat.paintroid.ui.DrawingSurface;
import org.catrobat.paintroid.ui.LayerAdapter;
import org.catrobat.paintroid.ui.LayerMenuViewHolder;
import org.catrobat.paintroid.ui.LayerNavigator;
import org.catrobat.paintroid.ui.LayerPresenter;
import org.catrobat.paintroid.ui.MainActivityNavigator;
import org.catrobat.paintroid.ui.Perspective;
import org.catrobat.paintroid.ui.TopBar;
import org.catrobat.paintroid.ui.dragndrop.DragAndDropListView;

import java.io.File;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static org.catrobat.paintroid.common.Constants.COLOR_PICKER_DIALOG_TAG;
import static org.catrobat.paintroid.common.Constants.PAINTROID_PICTURE_NAME;
import static org.catrobat.paintroid.common.Constants.PAINTROID_PICTURE_PATH;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
		SaveImageAsync.SaveImageCallback, LoadImageAsync.LoadImageCallback, CreateFileAsync.CreateFileCallback,
		BottomBar.BottomBarCallback, CommandManager.CommandListener {
	private static final String TAG = MainActivity.class.getSimpleName();
	private static final int REQUEST_CODE_IMPORTPNG = 1;
	private static final int REQUEST_CODE_LOAD_PICTURE = 2;
	private static final int REQUEST_CODE_FINISH = 3;
	private static final int REQUEST_CODE_TAKE_PICTURE = 4;
	private static final int REQUEST_CODE_LANGUAGE = 5;

	private static final int SAVE_IMAGE_DEFAULT = 0;
	private static final int SAVE_IMAGE_CHOOSE_NEW = 1;
	private static final int SAVE_IMAGE_LOAD_NEW = 2;
	private static final int SAVE_IMAGE_FINISH = 3;
	private static final int SAVE_IMAGE_EXIT_CATROID = 4;

	private static final int LOAD_IMAGE_DEFAULT = 0;
	private static final int LOAD_IMAGE_IMPORTPNG = 1;
	private static final int LOAD_IMAGE_CATROID = 2;

	private static final int CREATE_FILE_DEFAULT = 0;
	private static final int CREATE_FILE_TAKE_PHOTO = 1;

	private static final String IS_FULLSCREEN_KEY = "isFullscreen";

	public static boolean isSaved = false;
	@VisibleForTesting
	public static Uri savedPictureUri = null;
	@VisibleForTesting
	public TopBar topBar;
	@VisibleForTesting
	public boolean openedFromCatroid;
	private Uri cameraImageUri;
	private boolean isFullScreen;
	private boolean isKeyboardShown;
	private BottomBar bottomBar;
	private Bundle toolBundle = new Bundle();
	private DrawerLayout drawerLayout;
	private NavigationView navigationView;
	private View bottomBarLayout;
	private View toolbarContainer;
	private MenuItem navigationMenuExitFullscreen;
	private MenuItem navigationMenuEnterFullscreen;
	private LayerPresenter layerPresenter;
	private MainActivityContracts.Navigator mainActivityNavigator;

	private boolean resetPerspectiveAfterNextCommand;

	private CommandFactory commandFactory = new DefaultCommandFactory();
	private ToolFactory toolFactory = new DefaultToolFactory();

	private DrawingSurface drawingSurface;
	private CommandManager commandManager;
	private LayerContracts.Model layerModel;
	private Perspective perspective;

	@IntDef({SAVE_IMAGE_DEFAULT,
			SAVE_IMAGE_CHOOSE_NEW,
			SAVE_IMAGE_LOAD_NEW,
			SAVE_IMAGE_FINISH,
			SAVE_IMAGE_EXIT_CATROID})
	@Retention(RetentionPolicy.SOURCE)
	@interface SaveImageRequestCode {
	}

	@IntDef({LOAD_IMAGE_DEFAULT,
			LOAD_IMAGE_IMPORTPNG,
			LOAD_IMAGE_CATROID})
	@Retention(RetentionPolicy.SOURCE)
	@interface LoadImageRequestCode {
	}

	@IntDef({CREATE_FILE_DEFAULT,
			CREATE_FILE_TAKE_PHOTO})
	@Retention(RetentionPolicy.SOURCE)
	@interface CreateFileRequestCode {
	}

	@IntDef({REQUEST_CODE_IMPORTPNG,
			REQUEST_CODE_LOAD_PICTURE,
			REQUEST_CODE_FINISH,
			REQUEST_CODE_TAKE_PICTURE,
			REQUEST_CODE_LANGUAGE})
	@Retention(RetentionPolicy.SOURCE)
	@interface RequestCode {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		MultilingualActivity.setToChosenLanguage(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		onCreateGlobals();
		onCreateView();

		if (savedInstanceState == null) {
			String picturePath = getIntent().getStringExtra(PAINTROID_PICTURE_PATH);
			String pictureName = getIntent().getStringExtra(PAINTROID_PICTURE_NAME);
			openedFromCatroid = picturePath != null;
			if (openedFromCatroid) {
				File imageFile = new File(picturePath);
				if (imageFile.exists()) {
					savedPictureUri = Uri.fromFile(imageFile);
					new LoadImageAsync(this, LOAD_IMAGE_CATROID, savedPictureUri).execute();
				} else {
					new CreateFileAsync(this, CREATE_FILE_DEFAULT, pictureName).execute();
				}
			} else {
				initializeNewBitmap();
			}
		} else {
			Fragment colorPickerDialogFragment = getSupportFragmentManager()
					.findFragmentByTag(COLOR_PICKER_DIALOG_TAG);
			if (colorPickerDialogFragment != null) {
				ColorPickerDialog dialog = (ColorPickerDialog) colorPickerDialogFragment;
				dialog.addOnColorPickedListener(new ColorPickerDialog.OnColorPickedListener() {
					@Override
					public void colorChanged(int color) {
						PaintroidApplication.currentTool.changePaintColor(color);
					}
				});
				dialog.addOnColorPickedListener(topBar);
			}

			isFullScreen = savedInstanceState.getBoolean(IS_FULLSCREEN_KEY, false);

			setLayoutDirection();
			PaintroidApplication.currentTool.resetInternalState(Tool.StateChange.NEW_IMAGE_LOADED);
		}

		commandManager.addCommandListener(layerPresenter);
		commandManager.addCommandListener(this);

		initActionBar();
		initNavigationDrawer();
		initKeyboardIsShownListener();
		setFullScreen(isFullScreen);
	}

	private void onCreateGlobals() {
		if (PaintroidApplication.layerModel == null) {
			PaintroidApplication.layerModel = new LayerModel();
		}
		layerModel = PaintroidApplication.layerModel;

		if (PaintroidApplication.commandManager == null) {
			DisplayMetrics metrics = getResources().getDisplayMetrics();

			CommandManager synchronousCommandManager = new DefaultCommandManager(new CommonFactory(), layerModel);
			commandManager = new AsyncCommandManager(synchronousCommandManager, layerModel);
			Command initCommand = commandFactory.createInitCommand(metrics.widthPixels, metrics.heightPixels);
			commandManager.setInitialStateCommand(initCommand);
			commandManager.reset();
			PaintroidApplication.commandManager = commandManager;
		} else {
			commandManager = PaintroidApplication.commandManager;
		}
	}

	private void onCreateTool() {
		Bundle bundle = new Bundle();
		if (PaintroidApplication.currentTool == null) {
			PaintroidApplication.currentTool = toolFactory.createTool(this, ToolType.BRUSH);
			PaintroidApplication.currentTool.startTool();
		} else {
			Paint paint = PaintroidApplication.currentTool.getDrawPaint();
			PaintroidApplication.currentTool.leaveTool();
			PaintroidApplication.currentTool.onSaveInstanceState(bundle);
			PaintroidApplication.currentTool = toolFactory.createTool(this, PaintroidApplication.currentTool.getToolType());
			PaintroidApplication.currentTool.onRestoreInstanceState(bundle);
			PaintroidApplication.currentTool.startTool();
			PaintroidApplication.currentTool.setDrawPaint(paint);
		}
	}

	private void onCreateView() {
		drawerLayout = findViewById(R.id.drawer_layout);
		drawingSurface = findViewById(R.id.drawingSurfaceView);
		navigationView = findViewById(R.id.nav_view);
		bottomBarLayout = findViewById(R.id.main_bottom_bar);
		toolbarContainer = findViewById(R.id.layout_top_bar);
		DragAndDropListView layerListView = findViewById(R.id.layer_side_nav_list);

		Menu navigationViewMenu = navigationView.getMenu();
		navigationMenuExitFullscreen = navigationViewMenu.findItem(R.id.nav_exit_fullscreen_mode);
		navigationMenuEnterFullscreen = navigationViewMenu.findItem(R.id.nav_fullscreen_mode);

		ViewGroup layerLayout = findViewById(R.id.layer_side_nav_menu);
		LayerMenuViewHolder layerMenuViewHolder = new LayerMenuViewHolder(layerLayout);

		Resources resources = getResources();
		Configuration configuration = resources.getConfiguration();
		DisplayMetrics metrics = resources.getDisplayMetrics();
		LayerNavigator navigator = new LayerNavigator(this);

		layerPresenter = new LayerPresenter(layerModel, layerListView, layerMenuViewHolder,
				commandManager, commandFactory, navigator);
		LayerAdapter layerAdapter = new LayerAdapter(layerPresenter);
		layerPresenter.setAdapter(layerAdapter);
		layerListView.setPresenter(layerPresenter);
		layerListView.setAdapter(layerAdapter);

		layerPresenter.refreshLayerMenuViewHolder();

		layerMenuViewHolder.layerAddButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				layerPresenter.addLayer();
			}
		});

		layerMenuViewHolder.layerDeleteButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				layerPresenter.removeLayer();
			}
		});

		drawingSurface.setLayerModel(layerModel);

		PaintroidApplication.drawingSurface = drawingSurface;
		perspective = new Perspective(drawingSurface.getHolder().getSurfaceFrame(), metrics.density);
		PaintroidApplication.perspective = perspective;

		LinearLayout toolsLayout = findViewById(R.id.tools_layout);
		View scrollView = findViewById(R.id.bottom_bar_scroll_view);

		onCreateTool();
		bottomBar = new BottomBar(this, metrics.density, configuration.orientation, bottomBarLayout, toolsLayout, scrollView);
		mainActivityNavigator = new MainActivityNavigator(this);
		topBar = new TopBar(this, mainActivityNavigator);
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus) {
			bottomBar.startAnimation();
		}
	}

	private void setLayoutDirection() {
		if (VERSION.SDK_INT > Build.VERSION_CODES.N_MR1) {
			Configuration config = getResources().getConfiguration();
			getWindow().getDecorView().setLayoutDirection(config.getLayoutDirection());
		}
	}

	private void initActionBar() {
		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		ActionBar supportActionBar = getSupportActionBar();
		if (supportActionBar != null) {
			supportActionBar.setDisplayShowTitleEnabled(false);
			supportActionBar.setDisplayHomeAsUpEnabled(true);
			supportActionBar.setHomeButtonEnabled(true);
			Bundle extras = getIntent().getExtras();
			boolean showHome = extras != null && extras.getString(PAINTROID_PICTURE_PATH) != null;
			supportActionBar.setDisplayShowHomeEnabled(showHome);
		}

		ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
				toolbar, R.string.drawer_open, R.string.drawer_close);
		actionBarDrawerToggle.setDrawerSlideAnimationEnabled(false);
		actionBarDrawerToggle.syncState();
	}

	@Override
	public void commandPreExecute() {
		mainActivityNavigator.showIndeterminateProgressDialog();
	}

	@Override
	public void commandPostExecute() {
		if (!isFinishing()) {

			if (resetPerspectiveAfterNextCommand) {
				resetPerspectiveAfterNextCommand = false;
				perspective.resetScaleAndTranslation();
			}

			isSaved = false;
			PaintroidApplication.currentTool.resetInternalState(Tool.StateChange.RESET_INTERNAL_STATE);
			drawingSurface.refreshDrawingSurface();
			topBar.refreshButtons();

			mainActivityNavigator.dismissIndeterminateProgressDialog();
		}
	}

	@Override
	protected void onDestroy() {
		commandManager.removeCommandListener(layerPresenter);
		commandManager.removeCommandListener(this);

		if (isFinishing()) {
			BaseTool.reset();
			commandManager.shutdown();

			PaintroidApplication.currentTool = null;
			PaintroidApplication.commandManager = null;
			PaintroidApplication.layerModel = null;
			savedPictureUri = null;
		}

		super.onDestroy();
	}

	@Override
	public boolean onNavigationItemSelected(@NonNull MenuItem item) {

		switch (item.getItemId()) {
			case R.id.nav_back_to_pocket_code:
				showSecurityQuestionBeforeExit();
				break;
			case R.id.nav_export:
				new SaveImageAsync(this, SAVE_IMAGE_DEFAULT, null, true).execute();
				break;
			case R.id.nav_save_image:
				new SaveImageAsync(this, SAVE_IMAGE_DEFAULT, savedPictureUri, false).execute();
				break;
			case R.id.nav_save_duplicate:
				new SaveImageAsync(this, SAVE_IMAGE_DEFAULT, null, true).execute();
				break;
			case R.id.nav_open_image:
				onLoadImage();
				break;
			case R.id.nav_new_image:
				newImage();
				break;
			case R.id.nav_fullscreen_mode:
				setFullScreen(true);
				break;
			case R.id.nav_exit_fullscreen_mode:
				setFullScreen(false);
				break;
			case R.id.nav_tos:
				mainActivityNavigator.showTermsOfServiceDialog();
				break;
			case R.id.nav_help:
				mainActivityNavigator.startWelcomeActivity();
				break;
			case R.id.nav_about:
				mainActivityNavigator.showAboutDialog();
				break;
			case R.id.nav_lang:
				drawerLayout.closeDrawer(Gravity.START, false);
				mainActivityNavigator.startLanguageActivity(REQUEST_CODE_LANGUAGE);
				break;
		}

		drawerLayout.closeDrawers();
		return true;
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putBoolean(IS_FULLSCREEN_KEY, isFullScreen);
	}

	@Override
	public void onBackPressed() {
		if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
			drawerLayout.closeDrawer(Gravity.START);
		} else if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
			drawerLayout.closeDrawer(Gravity.END);
		} else if (isFullScreen) {
			setFullScreen(false);
		} else if (PaintroidApplication.currentTool.getToolOptionsAreShown()) {
			PaintroidApplication.currentTool.toggleShowToolOptions();
		} else if (PaintroidApplication.currentTool.getToolType() != ToolType.BRUSH) {
			switchTool(ToolType.BRUSH);
		} else {
			showSecurityQuestionBeforeExit();
		}
	}

	@Override
	public void onActivityResult(@RequestCode int requestCode, int resultCode, Intent data) {
		if (resultCode != Activity.RESULT_OK) {
			Log.d(TAG, "onActivityResult: result not ok, most likely a dialog hast been canceled");
			return;
		}
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		int maxWidth = metrics.widthPixels;
		int maxHeight = metrics.heightPixels;
		switch (requestCode) {
			case REQUEST_CODE_IMPORTPNG:
				Uri selectedGalleryImageUri = data.getData();
				Tool tool = toolFactory.createTool(this, ToolType.IMPORTPNG);
				switchTool(tool);
				new LoadImageAsync(this, LOAD_IMAGE_IMPORTPNG, maxWidth, maxHeight, selectedGalleryImageUri).execute();
				break;
			case REQUEST_CODE_FINISH:
				finish();
				break;
			case REQUEST_CODE_LANGUAGE:
				recreate();
				break;
			case REQUEST_CODE_LOAD_PICTURE:
				new LoadImageAsync(this, LOAD_IMAGE_DEFAULT, maxWidth, maxHeight, data.getData()).execute();
				break;
			case REQUEST_CODE_TAKE_PICTURE:
				new LoadImageAsync(this, LOAD_IMAGE_DEFAULT, maxWidth, maxHeight, cameraImageUri).execute();
				break;
			default:
				super.onActivityResult(requestCode, resultCode, data);
		}
	}

	@Override
	public void switchTool(ToolType changeToToolType) {
		if (changeToToolType == ToolType.IMPORTPNG) {
			mainActivityNavigator.startImportImageActivity(REQUEST_CODE_IMPORTPNG);
			return;
		}

		Tool tool = toolFactory.createTool(this, changeToToolType);
		switchTool(tool);
	}

	private void switchTool(Tool tool) {
		if (tool == null) {
			return;
		}

		Tool currentTool = PaintroidApplication.currentTool;
		Paint tempPaint = currentTool.getDrawPaint();

		currentTool.leaveTool();
		if (currentTool.getToolType() == tool.getToolType()) {
			currentTool.onSaveInstanceState(toolBundle);
			PaintroidApplication.currentTool = tool;
			bottomBar.setTool(tool);
			tool.onRestoreInstanceState(toolBundle);
		} else {
			toolBundle = new Bundle();
			bottomBar.setTool(tool);
			PaintroidApplication.currentTool = tool;
		}
		tool.startTool();
		tool.setDrawPaint(tempPaint);
	}

	private void showSecurityQuestionBeforeExit() {
		if (imageUnchanged() || isSaved) {
			mainActivityNavigator.finishActivity();
		} else if (openedFromCatroid) {
			mainActivityNavigator.showSaveBeforeReturnToCatroidDialog(SAVE_IMAGE_EXIT_CATROID, savedPictureUri);
		} else {
			mainActivityNavigator.showSaveBeforeFinishDialog(SAVE_IMAGE_FINISH, savedPictureUri);
		}
	}

	private void setFullScreen(boolean isFullScreen) {
		perspective.setFullscreen(isFullScreen);
		navigationMenuExitFullscreen.setVisible(isFullScreen);
		navigationMenuEnterFullscreen.setVisible(!isFullScreen);

		if (isFullScreen) {
			PaintroidApplication.currentTool.hide();
			toolbarContainer.setVisibility(View.GONE);
			bottomBarLayout.setVisibility(View.GONE);
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		} else {
			toolbarContainer.setVisibility(View.VISIBLE);
			bottomBarLayout.setVisibility(View.VISIBLE);
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}
		this.isFullScreen = isFullScreen;
	}

	private void initNavigationDrawer() {
		navigationView.setNavigationItemSelectedListener(this);

		Menu navigationViewMenu = navigationView.getMenu();
		if (openedFromCatroid) {
			navigationViewMenu.removeItem(R.id.nav_save_image);
			navigationViewMenu.removeItem(R.id.nav_save_duplicate);
		} else {
			navigationViewMenu.removeItem(R.id.nav_back_to_pocket_code);
			navigationViewMenu.removeItem(R.id.nav_export);
		}
	}

	@Override
	public boolean isKeyboardShown() {
		return isKeyboardShown;
	}

	@Override
	public void hideKeyboard() {
		InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		if (inputMethodManager != null) {
			View rootView = getWindow().getDecorView().getRootView();
			inputMethodManager.hideSoftInputFromWindow(rootView.getWindowToken(), 0);
		}
	}

	private void initKeyboardIsShownListener() {
		final View activityRootView = findViewById(R.id.main_layout);
		activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				int heightDiff = activityRootView.getRootView().getHeight() - activityRootView.getHeight();
				isKeyboardShown = heightDiff > 300;
			}
		});
	}

	private boolean imageUnchanged() {
		return !commandManager.isUndoAvailable();
	}

	private void onLoadImage() {
		if (imageUnchanged() || isSaved) {
			loadNewImage();
		} else {
			mainActivityNavigator.showSaveBeforeLoadImageDialog(SAVE_IMAGE_LOAD_NEW, savedPictureUri);
		}
	}

	public void loadNewImage() {
		mainActivityNavigator.startLoadImageActivity(REQUEST_CODE_LOAD_PICTURE);
	}

	private void newImage() {
		if (imageUnchanged() && !openedFromCatroid || isSaved) {
			chooseNewImage();
		} else {
			mainActivityNavigator.showSaveBeforeNewImageDialog(SAVE_IMAGE_CHOOSE_NEW, savedPictureUri);
		}
	}

	public void chooseNewImage() {
		mainActivityNavigator.showChooseNewImageDialog();
	}

	public void onNewImage() {
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		resetPerspectiveAfterNextCommand = true;
		Command initCommand = commandFactory.createInitCommand(metrics.widthPixels, metrics.heightPixels);
		commandManager.setInitialStateCommand(initCommand);
		commandManager.reset();
	}

	public void onNewImageFromCamera() {
		new CreateFileAsync(this, CREATE_FILE_TAKE_PHOTO, null).execute();
	}

	private void initializeNewBitmap() {
		PaintroidApplication.currentTool.resetInternalState(Tool.StateChange.NEW_IMAGE_LOADED);
		savedPictureUri = null;
	}

	@Override
	public void onCreateFilePostExecute(@CreateFileRequestCode int requestCode, Uri uri) {
		if (uri == null) {
			mainActivityNavigator.showSaveErrorDialog();
			return;
		}

		switch (requestCode) {
			case CREATE_FILE_DEFAULT:
				savedPictureUri = uri;
				break;
			case CREATE_FILE_TAKE_PHOTO:
				cameraImageUri = uri;
				mainActivityNavigator.startTakePictureActivity(REQUEST_CODE_TAKE_PICTURE, cameraImageUri);
				break;
		}
	}

	@Override
	public void onLoadImagePreExecute(@LoadImageRequestCode int requestCode) {
	}

	@Override
	public void onLoadImagePostExecute(@LoadImageRequestCode int requestCode, Uri uri, Bitmap bitmap) {
		if (bitmap == null) {
			mainActivityNavigator.showLoadErrorDialog();
			return;
		}

		switch (requestCode) {
			case LOAD_IMAGE_DEFAULT:
				resetPerspectiveAfterNextCommand = true;
				commandManager.setInitialStateCommand(commandFactory.createInitCommand(bitmap));
				commandManager.reset();
				savedPictureUri = null;
				cameraImageUri = null;
				break;
			case LOAD_IMAGE_IMPORTPNG:
				if (PaintroidApplication.currentTool instanceof ImportTool) {
					((ImportTool) PaintroidApplication.currentTool).setBitmapFromFile(bitmap);
				} else {
					Log.e(TAG, "importPngToFloatingBox: Current tool is no ImportTool as required");
				}
				break;
			case LOAD_IMAGE_CATROID:
				resetPerspectiveAfterNextCommand = true;
				commandManager.setInitialStateCommand(commandFactory.createInitCommand(bitmap));
				commandManager.reset();
				savedPictureUri = uri;
				cameraImageUri = null;
				break;
		}
	}

	@Override
	public void onSaveImagePostExecute(@SaveImageRequestCode int requestCode, Uri uri, boolean savedAsCopy) {
		mainActivityNavigator.dismissIndeterminateProgressDialog();

		if (uri == null) {
			mainActivityNavigator.showSaveErrorDialog();
			return;
		}

		if (savedAsCopy) {
			mainActivityNavigator.showToast(R.string.copy, Toast.LENGTH_LONG);
		} else {
			mainActivityNavigator.showToast(R.string.saved, Toast.LENGTH_LONG);
			savedPictureUri = uri;
			isSaved = true;
		}
		switch (requestCode) {
			case SAVE_IMAGE_CHOOSE_NEW:
				chooseNewImage();
				break;
			case SAVE_IMAGE_DEFAULT:
				break;
			case SAVE_IMAGE_FINISH:
				finish();
				return;
			case SAVE_IMAGE_LOAD_NEW:
				mainActivityNavigator.startLoadImageActivity(REQUEST_CODE_LOAD_PICTURE);
				break;
			case SAVE_IMAGE_EXIT_CATROID:
				mainActivityNavigator.returnToPocketCode(uri.getPath());
				break;
		}
	}

	@Override
	public void onSaveImagePreExecute(@SaveImageRequestCode int requestCode) {
		mainActivityNavigator.showIndeterminateProgressDialog();
	}
}
