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

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.FileProvider;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import org.catrobat.paintroid.command.Command;
import org.catrobat.paintroid.command.CommandFactory;
import org.catrobat.paintroid.command.CommandManager;
import org.catrobat.paintroid.command.implementation.AsyncCommandManager;
import org.catrobat.paintroid.command.implementation.DefaultCommandFactory;
import org.catrobat.paintroid.command.implementation.DefaultCommandManager;
import org.catrobat.paintroid.common.CommonFactory;
import org.catrobat.paintroid.contract.LayerContracts;
import org.catrobat.paintroid.contract.MainActivityContracts;
import org.catrobat.paintroid.listener.BottomBarScrollListener;
import org.catrobat.paintroid.model.LayerModel;
import org.catrobat.paintroid.model.MainActivityModel;
import org.catrobat.paintroid.presenter.LayerPresenter;
import org.catrobat.paintroid.presenter.MainActivityPresenter;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.tools.implementation.BaseTool;
import org.catrobat.paintroid.ui.BottomBarHorizontalScrollView;
import org.catrobat.paintroid.ui.DrawingSurface;
import org.catrobat.paintroid.ui.KeyboardListener;
import org.catrobat.paintroid.ui.LayerAdapter;
import org.catrobat.paintroid.ui.LayerNavigator;
import org.catrobat.paintroid.ui.MainActivityInteractor;
import org.catrobat.paintroid.ui.MainActivityNavigator;
import org.catrobat.paintroid.ui.Perspective;
import org.catrobat.paintroid.ui.dragndrop.DragAndDropListView;
import org.catrobat.paintroid.ui.viewholder.BottomBarViewHolder;
import org.catrobat.paintroid.ui.viewholder.DrawerLayoutViewHolder;
import org.catrobat.paintroid.ui.viewholder.LayerMenuViewHolder;
import org.catrobat.paintroid.ui.viewholder.NavigationViewViewHolder;
import org.catrobat.paintroid.ui.viewholder.TopBarViewHolder;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.catrobat.paintroid.common.Constants.PAINTROID_PICTURE_NAME;
import static org.catrobat.paintroid.common.Constants.PAINTROID_PICTURE_PATH;

public class MainActivity extends AppCompatActivity implements MainActivityContracts.MainView,
		CommandManager.CommandListener {
	public static final String TAG = MainActivity.class.getSimpleName();
	private static final String IS_FULLSCREEN_KEY = "isFullscreen";
	private static final String IS_SAVED_KEY = "isSaved";
	private static final String IS_OPENED_FROM_CATROID_KEY = "isOpenedFromCatroid";
	private static final String WAS_INITIAL_ANIMATION_PLAYED = "wasInitialAnimationPlayed";
	private static final String SAVED_PICTURE_URI_KEY = "savedPictureUri";
	private static final String CAMERA_IMAGE_URI_KEY = "cameraImageUri";
	private static final String APP_FRAGMENT_KEY = "customActivityState";

	@VisibleForTesting
	public MainActivityContracts.Model model;
	private LayerPresenter layerPresenter;
	private DrawingSurface drawingSurface;
	private CommandManager commandManager;
	private LayerContracts.Model layerModel;
	private MainActivityContracts.Presenter presenter;
	private DrawerLayoutViewHolder drawerLayoutViewHolder;
	private Handler handler = new Handler();
	private KeyboardListener keyboardListener;
	private PaintroidApplicationFragment appFragment;

	@Override
	public MainActivityContracts.Presenter getPresenter() {
		return presenter;
	}

	@Override
	public DisplayMetrics getDisplayMetrics() {
		return getResources().getDisplayMetrics();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.PocketPaintTheme);
		super.onCreate(savedInstanceState);

		getAppFragment();
		appFragment.setCacheDir(getCacheDir());
		appFragment.setCheckeredBackgroundBitmap(BitmapFactory.decodeResource(getResources(), R.drawable
				.pocketpaint_checkeredbg));
		setContentView(R.layout.activity_pocketpaint_main);

		onCreateGlobals();

		onCreateMainView();
		onCreateLayerMenu();
		onCreateDrawingSurface();

		presenter.onCreateTool();

		if (savedInstanceState == null) {
			Intent intent = getIntent();
			String picturePath = intent.getStringExtra(PAINTROID_PICTURE_PATH);
			String pictureName = intent.getStringExtra(PAINTROID_PICTURE_NAME);
			presenter.initializeFromCleanState(picturePath, pictureName);
		} else {
			boolean isFullScreen = savedInstanceState.getBoolean(IS_FULLSCREEN_KEY, false);
			boolean isSaved = savedInstanceState.getBoolean(IS_SAVED_KEY, false);
			boolean isOpenedFromCatroid = savedInstanceState.getBoolean(IS_OPENED_FROM_CATROID_KEY, false);
			boolean wasInitialAnimationPlayed = savedInstanceState.getBoolean(WAS_INITIAL_ANIMATION_PLAYED, false);
			Uri savedPictureUri = savedInstanceState.getParcelable(SAVED_PICTURE_URI_KEY);
			Uri cameraImageUri = savedInstanceState.getParcelable(CAMERA_IMAGE_URI_KEY);

			presenter.restoreState(isFullScreen, isSaved, isOpenedFromCatroid,
					wasInitialAnimationPlayed, savedPictureUri, cameraImageUri);
			setLayoutDirection();
		}

		commandManager.addCommandListener(this);

		presenter.finishInitialize();
	}

	private void getAppFragment() {
		FragmentManager fragmentManager = getSupportFragmentManager();
		appFragment = (PaintroidApplicationFragment) fragmentManager.findFragmentByTag(APP_FRAGMENT_KEY);
		if (appFragment == null) {
			appFragment = new PaintroidApplicationFragment();
			fragmentManager.beginTransaction().add(appFragment, APP_FRAGMENT_KEY).commit();
		}
	}

	private void onCreateGlobals() {
		if (appFragment.getLayerModel() == null) {
			appFragment.setLayerModel(new LayerModel());
		}
		layerModel = appFragment.getLayerModel();
		if (appFragment.getCommandManager() == null) {
			DisplayMetrics metrics = getResources().getDisplayMetrics();

			CommandFactory commandFactory = new DefaultCommandFactory();
			CommandManager synchronousCommandManager = new DefaultCommandManager(new CommonFactory(), layerModel);
			commandManager = new AsyncCommandManager(synchronousCommandManager, layerModel);
			Command initCommand = commandFactory.createInitCommand(metrics.widthPixels, metrics.heightPixels);
			commandManager.setInitialStateCommand(initCommand);
			commandManager.reset();
			appFragment.setCommandManager(commandManager);
		} else {
			commandManager = appFragment.getCommandManager();
		}
	}

	private void onCreateMainView() {
		DrawerLayout drawerLayout = findViewById(R.id.pocketpaint_drawer_layout);
		ViewGroup topBarLayout = findViewById(R.id.pocketpaint_layout_top_bar);
		View bottomBarLayout = findViewById(R.id.pocketpaint_main_bottom_bar);
		NavigationView navigationView = findViewById(R.id.pocketpaint_nav_view);

		drawerLayoutViewHolder = new DrawerLayoutViewHolder(drawerLayout);
		TopBarViewHolder topBarViewHolder = new TopBarViewHolder(topBarLayout);
		BottomBarViewHolder bottomBarViewHolder = new BottomBarViewHolder(bottomBarLayout);
		NavigationViewViewHolder navigationDrawerViewHolder = new NavigationViewViewHolder(navigationView);

		MainActivityContracts.Navigator navigator = new MainActivityNavigator(this);
		MainActivityContracts.Interactor interactor = new MainActivityInteractor();
		model = new MainActivityModel();
		presenter = new MainActivityPresenter(this, model, navigator, interactor, topBarViewHolder,
				bottomBarViewHolder, drawerLayoutViewHolder, navigationDrawerViewHolder, commandManager);

		keyboardListener = new KeyboardListener(drawerLayout);
		setTopBarListeners(topBarViewHolder);
		setBottomBarListeners(bottomBarViewHolder);
		setNavigationViewListeners(navigationDrawerViewHolder);
	}

	private void onCreateLayerMenu() {
		ViewGroup layerLayout = findViewById(R.id.pocketpaint_layer_side_nav_menu);
		DragAndDropListView layerListView = findViewById(R.id.pocketpaint_layer_side_nav_list);

		LayerMenuViewHolder layerMenuViewHolder = new LayerMenuViewHolder(layerLayout);
		LayerNavigator layerNavigator = new LayerNavigator(getApplicationContext());

		layerPresenter = new LayerPresenter(layerModel, layerListView, layerMenuViewHolder,
				commandManager, new DefaultCommandFactory(), layerNavigator);
		LayerAdapter layerAdapter = new LayerAdapter(layerPresenter);
		layerPresenter.setAdapter(layerAdapter);
		layerListView.setPresenter(layerPresenter);
		layerListView.setAdapter(layerAdapter);

		layerPresenter.refreshLayerMenuViewHolder();
		setLayerMenuListeners(layerMenuViewHolder);
	}

	private void onCreateDrawingSurface() {
		drawingSurface = findViewById(R.id.pocketpaint_drawing_surface_view);
		drawingSurface.setLayerModel(layerModel);

		DisplayMetrics metrics = getResources().getDisplayMetrics();
		appFragment.setDrawingSurface(drawingSurface);
		appFragment.setPerspective(new Perspective(drawingSurface.getHolder().getSurfaceFrame(), metrics.density));
	}

	private void setLayerMenuListeners(LayerMenuViewHolder layerMenuViewHolder) {
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
	}

	private void setTopBarListeners(TopBarViewHolder topBar) {
		topBar.undoButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				presenter.undoClicked();
			}
		});
		topBar.redoButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				presenter.redoClicked();
			}
		});
		topBar.colorButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				presenter.showColorPickerClicked();
			}
		});
		topBar.layerButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				presenter.showLayerMenuClicked();
			}
		});
	}

	private void setBottomBarListeners(final BottomBarViewHolder viewHolder) {
		List<ToolType> toolTypes = Arrays.asList(ToolType.values());
		for (final ToolType type : toolTypes) {
			View toolButton = viewHolder.layout.findViewById(type.getToolButtonID());
			if (toolButton == null) {
				continue;
			}

			toolButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					presenter.toolClicked(type);
				}
			});
		}

		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			View next = viewHolder.layout.findViewById(R.id.pocketpaint_bottom_next);
			View previous = viewHolder.layout.findViewById(R.id.pocketpaint_bottom_previous);
			BottomBarHorizontalScrollView horizontalScrollView = (BottomBarHorizontalScrollView) viewHolder.scrollView;
			horizontalScrollView.setScrollStateListener(new BottomBarScrollListener(previous, next));
		}
	}

	private void setNavigationViewListeners(NavigationViewViewHolder navigationDrawerViewHolder) {
		navigationDrawerViewHolder.navigationView.setNavigationItemSelectedListener(
				new NavigationView.OnNavigationItemSelectedListener() {
					@Override
					public boolean onNavigationItemSelected(@NonNull final MenuItem item) {
						drawerLayoutViewHolder.closeDrawer(GravityCompat.START, true);
						handler.postDelayed(new Runnable() {
							@Override
							public void run() {
								MainActivity.this.onNavigationItemSelected(item);
							}
						}, 250);
						return true;
					}
				});
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		if (hasFocus) {
			presenter.gotFocus();
		}
	}

	private void setLayoutDirection() {
		if (VERSION.SDK_INT > Build.VERSION_CODES.N_MR1) {
			Configuration config = getResources().getConfiguration();
			getWindow().getDecorView().setLayoutDirection(config.getLayoutDirection());
		}
	}

	@Override
	public void initializeActionBar(boolean isOpenedFromCatroid) {
		Toolbar toolbar = findViewById(R.id.pocketpaint_toolbar);
		setSupportActionBar(toolbar);

		boolean showHome = model.isOpenedFromCatroid();
		ActionBar supportActionBar = getSupportActionBar();
		if (supportActionBar != null) {
			supportActionBar.setDisplayShowTitleEnabled(false);
			supportActionBar.setDisplayHomeAsUpEnabled(true);
			supportActionBar.setHomeButtonEnabled(true);
			supportActionBar.setDisplayShowHomeEnabled(showHome);
		}

		ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,
				drawerLayoutViewHolder.drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
		actionBarDrawerToggle.setDrawerSlideAnimationEnabled(false);
		actionBarDrawerToggle.syncState();
	}

	@Override
	public void commandPreExecute() {
		presenter.onCommandPreExecute();
	}

	@Override
	public void commandPostExecute() {
		if (!isFinishing()) {
			layerPresenter.invalidate();
			presenter.onCommandPostExecute();
		}
	}

	@Override
	protected void onDestroy() {
		commandManager.removeCommandListener(this);

		if (isFinishing()) {
			BaseTool.reset();
			commandManager.shutdown();
			appFragment.setCurrentTool(null);
			appFragment.setCommandManager(null);
			appFragment.setLayerModel(null);
		}

		super.onDestroy();
	}

	private void onNavigationItemSelected(@NonNull MenuItem item) {
		int i = item.getItemId();
		if (i == R.id.pocketpaint_nav_back_to_pocket_code) {
			presenter.backToPocketCodeClicked();
		} else if (i == R.id.pocketpaint_nav_export) {
			presenter.saveCopyClicked();
		} else if (i == R.id.pocketpaint_nav_save_image) {
			presenter.saveImageClicked();
		} else if (i == R.id.pocketpaint_nav_save_duplicate) {
			presenter.saveCopyClicked();
		} else if (i == R.id.pocketpaint_nav_open_image) {
			presenter.loadImageClicked();
		} else if (i == R.id.pocketpaint_nav_new_image) {
			presenter.newImageClicked();
		} else if (i == R.id.pocketpaint_nav_fullscreen_mode) {
			presenter.enterFullscreenClicked();
		} else if (i == R.id.pocketpaint_nav_exit_fullscreen_mode) {
			presenter.exitFullscreenClicked();
		} else if (i == R.id.pocketpaint_nav_help) {
			presenter.showHelpClicked();
		} else if (i == R.id.pocketpaint_nav_about) {
			presenter.showAboutClicked();
		} else {
			throw new IllegalArgumentException();
		}
	}

	@Override
	protected void onSaveInstanceState(@NonNull Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putBoolean(IS_FULLSCREEN_KEY, model.isFullScreen());
		outState.putBoolean(IS_SAVED_KEY, model.isSaved());
		outState.putBoolean(IS_OPENED_FROM_CATROID_KEY, model.isOpenedFromCatroid());
		outState.putBoolean(WAS_INITIAL_ANIMATION_PLAYED, model.wasInitialAnimationPlayed());
		outState.putParcelable(SAVED_PICTURE_URI_KEY, model.getSavedPictureUri());
		outState.putParcelable(CAMERA_IMAGE_URI_KEY, model.getCameraImageUri());
	}

	@Override
	public void onBackPressed() {
		presenter.onBackPressed();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		presenter.handleActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		presenter.handlePermissionRequestResults(requestCode, permissions, grantResults);
	}

	@Override
	public void forwardActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public boolean isKeyboardShown() {
		return keyboardListener.isSoftKeyboardVisible();
	}

	@Override
	public void refreshDrawingSurface() {
		drawingSurface.refreshDrawingSurface();
	}

	@Override
	public void enterFullScreen() {
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
	}

	@Override
	public void exitFullScreen() {
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
	}

	@Override
	public File getExternalDirPictureFile() {
		File image = null;
		try {
			File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
			image = File.createTempFile(FileIO.getDefaultFileName(), ".jpg", storageDir);
			image.deleteOnExit();
		} catch (IOException e) {
			Log.e(TAG, "Exception while creating tmp image file.");
		}
		return image;
	}

	@Override
	public Uri getFileProviderUriFromFile(File file) {
		String authority = getPackageName() + ".paintroid.fileprovider";
		return FileProvider.getUriForFile(this, authority, file);
	}

	@Override
	public Uri getUriFromFile(File file) {
		return Uri.fromFile(file);
	}

	@Override
	public void hideKeyboard() {
		InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		if (inputMethodManager != null) {
			View rootView = getWindow().getDecorView().getRootView();
			inputMethodManager.hideSoftInputFromWindow(rootView.getWindowToken(), 0);
		}
	}
}
