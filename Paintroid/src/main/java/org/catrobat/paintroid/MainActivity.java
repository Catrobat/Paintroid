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
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
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
import org.catrobat.paintroid.common.Constants;
import org.catrobat.paintroid.dialog.CustomAlertDialogBuilder;
import org.catrobat.paintroid.dialog.DialogAbout;
import org.catrobat.paintroid.dialog.DialogTermsOfUseAndService;
import org.catrobat.paintroid.dialog.IndeterminateProgressDialog;
import org.catrobat.paintroid.dialog.InfoDialog;
import org.catrobat.paintroid.dialog.InfoDialog.DialogType;
import org.catrobat.paintroid.dialog.colorpicker.ColorPickerDialog;
import org.catrobat.paintroid.iotasks.CreateFileAsync;
import org.catrobat.paintroid.iotasks.LoadImageAsync;
import org.catrobat.paintroid.iotasks.SaveImageAsync;
import org.catrobat.paintroid.model.LayerModel;
import org.catrobat.paintroid.tools.Tool;
import org.catrobat.paintroid.tools.ToolFactory;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.tools.implementation.BaseTool;
import org.catrobat.paintroid.tools.implementation.ImportTool;
import org.catrobat.paintroid.ui.BottomBar;
import org.catrobat.paintroid.ui.DrawingSurface;
import org.catrobat.paintroid.ui.LayerAdapter;
import org.catrobat.paintroid.ui.LayerMenuViewHolder;
import org.catrobat.paintroid.ui.LayerNavigator;
import org.catrobat.paintroid.ui.LayerPresenter;
import org.catrobat.paintroid.ui.Perspective;
import org.catrobat.paintroid.ui.ToastFactory;
import org.catrobat.paintroid.ui.TopBar;
import org.catrobat.paintroid.ui.dragndrop.DragAndDropListView;

import java.io.File;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

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
	private DrawingSurface drawingSurface;
	private NavigationView navigationView;
	private View bottomBarLayout;
	private View toolbarContainer;
	private MenuItem navigationMenuExitFullscreen;
	private MenuItem navigationMenuEnterFullscreen;
	private LayerPresenter layerPresenter;
	private CommandFactory commandFactory = new DefaultCommandFactory();

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
			setLayoutDirection();
			PaintroidApplication.currentTool.resetInternalState(Tool.StateChange.NEW_IMAGE_LOADED);
		}

		CommandManager commandManager = PaintroidApplication.commandManager;
		commandManager.addCommandListener(layerPresenter);
		commandManager.addCommandListener(this);

		initActionBar();
		initNavigationDrawer();
		initKeyboardIsShownListener();
		setFullScreen(false);
	}

	private void onCreateGlobals() {
		// init model
		if (PaintroidApplication.layerModel == null) {
			PaintroidApplication.layerModel = new LayerModel();
		}

		if (PaintroidApplication.commandManager == null) {

			DisplayMetrics metrics = getResources().getDisplayMetrics();

			CommandManager synchronousCommandManager = new DefaultCommandManager(new CommonFactory(), PaintroidApplication.layerModel);
			CommandManager commandManager = new AsyncCommandManager(synchronousCommandManager, PaintroidApplication.layerModel);
			Command initCommand = commandFactory.createInitCommand(metrics.widthPixels, metrics.heightPixels);
			commandManager.setInitialStateCommand(initCommand);
			commandManager.reset();
			PaintroidApplication.commandManager = commandManager;
		}
		// end init
	}

	private void onCreateTool() {
		Bundle bundle = new Bundle();
		if (PaintroidApplication.currentTool == null) {
			PaintroidApplication.currentTool = ToolFactory.createTool(this, ToolType.BRUSH);
			PaintroidApplication.currentTool.startTool();
		} else {
			Paint paint = PaintroidApplication.currentTool.getDrawPaint();
			PaintroidApplication.currentTool.leaveTool();
			PaintroidApplication.currentTool.onSaveInstanceState(bundle);
			PaintroidApplication.currentTool = ToolFactory.createTool(this, PaintroidApplication.currentTool.getToolType());
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

		ColorPickerDialog.init(this);
		IndeterminateProgressDialog.init(this);

		ViewGroup layerLayout = findViewById(R.id.layer_side_nav_menu);
		LayerMenuViewHolder layerMenuViewHolder = new LayerMenuViewHolder(layerLayout);

		Resources resources = getResources();
		Configuration configuration = resources.getConfiguration();
		DisplayMetrics metrics = resources.getDisplayMetrics();
		LayerNavigator navigator = new LayerNavigator(this);

		layerPresenter = new LayerPresenter(PaintroidApplication.layerModel, layerListView,
				layerMenuViewHolder, PaintroidApplication.commandManager, commandFactory, navigator);
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

		drawingSurface.setLayerModel(PaintroidApplication.layerModel);

		PaintroidApplication.drawingSurface = drawingSurface;
		PaintroidApplication.perspective = new Perspective(drawingSurface.getHolder().getSurfaceFrame(), metrics.density);

		LinearLayout toolsLayout = findViewById(R.id.tools_layout);
		View scrollView = findViewById(R.id.bottom_bar_scroll_view);

		onCreateTool();
		bottomBar = new BottomBar(this, metrics.density, configuration.orientation, bottomBarLayout, toolsLayout, scrollView);
		topBar = new TopBar(this);
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus) {
			bottomBar.startAnimation();
		}
	}

	private void setLayoutDirection() {
		if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1) {
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
			supportActionBar.setDisplayShowHomeEnabled((extras == null ? null : extras.getString(PAINTROID_PICTURE_PATH)) != null);
		}

		ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
				toolbar, R.string.drawer_open, R.string.drawer_close);
		actionBarDrawerToggle.setDrawerSlideAnimationEnabled(false);
		actionBarDrawerToggle.syncState();
	}

	@Override
	public void commandPreExecute() {
		IndeterminateProgressDialog.getInstance().show();
	}

	@Override
	public void commandPostExecute() {
		if (!isFinishing()) {
			isSaved = false;
			PaintroidApplication.currentTool.resetInternalState(Tool.StateChange.RESET_INTERNAL_STATE);
			drawingSurface.refreshDrawingSurface();
			topBar.refreshButtons();

			IndeterminateProgressDialog.getInstance().dismiss();
		}
	}

	@Override
	protected void onDestroy() {
		PaintroidApplication.commandManager.removeCommandListener(layerPresenter);
		PaintroidApplication.commandManager.removeCommandListener(this);

		if (isFinishing()) {
			BaseTool.reset();
			PaintroidApplication.currentTool = null;
			PaintroidApplication.commandManager.shutdown();
			PaintroidApplication.commandManager = null;
			PaintroidApplication.layerModel = null;
			savedPictureUri = null;

			IndeterminateProgressDialog.finishInstance();
			ColorPickerDialog.finishInstance();
		} else {
			IndeterminateProgressDialog.dismissInstance();
			ColorPickerDialog.dismissInstance();
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
				DialogTermsOfUseAndService termsOfUseAndService = new DialogTermsOfUseAndService();
				termsOfUseAndService.show(getSupportFragmentManager(), Constants.TOS_DIALOG_FRAGMENT_TAG);
				break;
			case R.id.nav_help:
				Intent intent = new Intent(this, WelcomeActivity.class);
				intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
				startActivity(intent);
				break;
			case R.id.nav_about:
				DialogAbout about = new DialogAbout();
				about.show(getSupportFragmentManager(), Constants.ABOUT_DIALOG_FRAGMENT_TAG);
				break;
			case R.id.nav_lang:
				drawerLayout.closeDrawer(Gravity.START, false);
				Intent language = new Intent(this, MultilingualActivity.class);
				startActivityForResult(language, REQUEST_CODE_LANGUAGE);
				break;
		}

		drawerLayout.closeDrawers();
		return true;
	}

	@Override
	public void onBackPressed() {
		if (isFullScreen) {
			setFullScreen(false);
		} else if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
			drawerLayout.closeDrawer(Gravity.START);
		} else if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
			drawerLayout.closeDrawer(Gravity.END);
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
				Tool tool = ToolFactory.createTool(this, ToolType.IMPORTPNG);
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

	public void switchTool(ToolType changeToToolType) {
		if (changeToToolType == ToolType.IMPORTPNG) {
			Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
			intent.setType("image/*");
			setNewDocumentFlags(intent);
			startActivityForResult(intent, REQUEST_CODE_IMPORTPNG);
			return;
		}

		Tool tool = ToolFactory.createTool(this, changeToToolType);
		switchTool(tool);
	}

	public void switchTool(Tool tool) {
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
			finish();
		} else {
			@StringRes
			final int title = openedFromCatroid
					? R.string.closing_catroid_security_question_title
					: R.string.closing_security_question_title;
			@SaveImageRequestCode
			final int requestCode = openedFromCatroid
					? SAVE_IMAGE_EXIT_CATROID
					: SAVE_IMAGE_FINISH;

			new CustomAlertDialogBuilder(this)
					.setTitle(title)
					.setMessage(R.string.closing_security_question)
					.setPositiveButton(R.string.save_button_text,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int id) {
									new SaveImageAsync(MainActivity.this, requestCode, savedPictureUri, false).execute();
								}
							})
					.setNegativeButton(R.string.discard_button_text,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int id) {
									finish();
								}
							})
					.show();
		}
	}

	private void setFullScreen(boolean isFullScreen) {
		PaintroidApplication.perspective.setFullscreen(isFullScreen);
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

	public boolean isKeyboardShown() {
		return isKeyboardShown;
	}

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

	boolean imageUnchanged() {
		return !PaintroidApplication.commandManager.isUndoAvailable();
	}

	protected void onLoadImage() {
		if (imageUnchanged() || isSaved) {
			startLoadImageIntent();
		} else {

			AlertDialog.Builder alertLoadDialogBuilder = new CustomAlertDialogBuilder(this);
			alertLoadDialogBuilder
					.setTitle(R.string.menu_load_image)
					.setMessage(R.string.dialog_warning_new_image)
					.setCancelable(true)
					.setPositiveButton(R.string.save_button_text,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int id) {
									new SaveImageAsync(MainActivity.this, SAVE_IMAGE_LOAD_NEW, savedPictureUri, false).execute();
								}
							})
					.setNegativeButton(R.string.discard_button_text,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int id) {
									startLoadImageIntent();
								}
							});
			alertLoadDialogBuilder.show();
		}
	}

	private void startLoadImageIntent() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("image/*");
		setNewDocumentFlags(intent);
		startActivityForResult(intent, REQUEST_CODE_LOAD_PICTURE);
	}

	private static void setNewDocumentFlags(Intent intent) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
		} else {
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
		}
	}

	protected void newImage() {
		if (imageUnchanged() && !openedFromCatroid || isSaved) {
			chooseNewImage();
		} else {
			new CustomAlertDialogBuilder(this)
					.setTitle(R.string.menu_new_image)
					.setMessage(R.string.dialog_warning_new_image)
					.setCancelable(true)
					.setPositiveButton(R.string.save_button_text,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int id) {
									new SaveImageAsync(MainActivity.this, SAVE_IMAGE_CHOOSE_NEW, savedPictureUri, false).execute();
								}
							})
					.setNegativeButton(R.string.discard_button_text,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int id) {
									chooseNewImage();
								}
							})
					.show();
		}
	}

	protected void chooseNewImage() {
		new CustomAlertDialogBuilder(this)
				.setTitle(R.string.menu_new_image)
				.setItems(R.array.new_image, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
							case 0:
								onNewImage();
								break;
							case 1:
								onNewImageFromCamera();
								break;
						}
					}
				})
				.show();
	}

	private void onNewImage() {
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		PaintroidApplication.commandManager.setInitialStateCommand(
				commandFactory.createInitCommand(metrics.widthPixels, metrics.heightPixels));
		PaintroidApplication.commandManager.reset();
		initializeNewBitmap();
	}

	private void onNewImageFromCamera() {
		new CreateFileAsync(this, CREATE_FILE_TAKE_PHOTO, null).execute();
	}

	private void showSaveErrorDialog() {
		InfoDialog.newInstance(DialogType.WARNING,
				R.string.dialog_error_sdcard_text,
				R.string.dialog_error_save_title).show(
				getSupportFragmentManager(), Constants.SAVE_DIALOG_FRAGMENT_TAG);
	}

	private void showLoadErrorDialog() {
		InfoDialog.newInstance(DialogType.WARNING,
				R.string.dialog_loading_image_failed_title,
				R.string.dialog_loading_image_failed_text).show(
				getSupportFragmentManager(), Constants.LOAD_DIALOG_FRAGMENT_TAG);
	}

	private void initializeNewBitmap() {
		PaintroidApplication.perspective.resetScaleAndTranslation();
		PaintroidApplication.currentTool.resetInternalState(Tool.StateChange.NEW_IMAGE_LOADED);

		savedPictureUri = null;

		PaintroidApplication.drawingSurface.refreshDrawingSurface();
	}

	public void onCreateFilePostExecute(@CreateFileRequestCode int requestCode, Uri uri) {
		if (uri == null) {
			showSaveErrorDialog();
			return;
		}

		switch (requestCode) {
			case CREATE_FILE_DEFAULT:
				savedPictureUri = uri;
				break;
			case CREATE_FILE_TAKE_PHOTO:
				cameraImageUri = uri;
				Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
				intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri);
				setNewDocumentFlags(intent);
				startActivityForResult(intent, REQUEST_CODE_TAKE_PICTURE);
				break;
		}
	}

	public void onLoadImagePreExecute(@LoadImageRequestCode int requestCode) {
		IndeterminateProgressDialog.getInstance().show();
	}

	public void onLoadImagePostExecute(@LoadImageRequestCode int requestCode, Uri uri, Bitmap bitmap) {
		IndeterminateProgressDialog.getInstance().dismiss();
		if (bitmap == null) {
			showLoadErrorDialog();
			return;
		}

		switch (requestCode) {
			case LOAD_IMAGE_DEFAULT:
				PaintroidApplication.commandManager.setInitialStateCommand(
						commandFactory.createInitCommand(bitmap));
				PaintroidApplication.commandManager.reset();
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
				PaintroidApplication.commandManager.setInitialStateCommand(
						commandFactory.createInitCommand(bitmap));
				PaintroidApplication.commandManager.reset();
				savedPictureUri = uri;
				cameraImageUri = null;
				break;
		}
	}

	public void onSaveImagePostExecute(@SaveImageRequestCode int requestCode, Uri uri, boolean savedAsCopy) {
		IndeterminateProgressDialog.getInstance().dismiss();

		if (uri == null) {
			showSaveErrorDialog();
			return;
		}

		if (savedAsCopy) {
			ToastFactory.makeText(this, R.string.copy, Toast.LENGTH_LONG).show();
		} else {
			ToastFactory.makeText(this, R.string.saved, Toast.LENGTH_LONG).show();
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
				startLoadImageIntent();
				break;
			case SAVE_IMAGE_EXIT_CATROID:
				Intent resultIntent = new Intent();
				resultIntent.putExtra(Constants.PAINTROID_PICTURE_PATH, uri.getPath());
				setResult(RESULT_OK, resultIntent);
				finish();
				break;
		}
	}

	public void onSaveImagePreExecute(@SaveImageRequestCode int requestCode) {
		IndeterminateProgressDialog.getInstance().show();
	}
}
