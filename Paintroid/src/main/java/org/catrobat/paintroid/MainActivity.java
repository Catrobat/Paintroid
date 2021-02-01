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
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.catrobat.paintroid.command.Command;
import org.catrobat.paintroid.command.CommandFactory;
import org.catrobat.paintroid.command.CommandManager;
import org.catrobat.paintroid.command.implementation.AsyncCommandManager;
import org.catrobat.paintroid.command.implementation.DefaultCommandFactory;
import org.catrobat.paintroid.command.implementation.DefaultCommandManager;
import org.catrobat.paintroid.common.CommonFactory;
import org.catrobat.paintroid.contract.LayerContracts;
import org.catrobat.paintroid.contract.MainActivityContracts;
import org.catrobat.paintroid.controller.DefaultToolController;
import org.catrobat.paintroid.listener.PresenterColorPickedListener;
import org.catrobat.paintroid.model.LayerModel;
import org.catrobat.paintroid.model.MainActivityModel;
import org.catrobat.paintroid.presenter.LayerPresenter;
import org.catrobat.paintroid.presenter.MainActivityPresenter;
import org.catrobat.paintroid.tools.ContextCallback;
import org.catrobat.paintroid.tools.ToolPaint;
import org.catrobat.paintroid.tools.ToolReference;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.tools.Workspace;
import org.catrobat.paintroid.tools.implementation.BaseToolWithShape;
import org.catrobat.paintroid.tools.implementation.DefaultContextCallback;
import org.catrobat.paintroid.tools.implementation.DefaultToolFactory;
import org.catrobat.paintroid.tools.implementation.DefaultToolPaint;
import org.catrobat.paintroid.tools.implementation.DefaultToolReference;
import org.catrobat.paintroid.tools.implementation.DefaultWorkspace;
import org.catrobat.paintroid.tools.options.ToolOptionsViewController;
import org.catrobat.paintroid.ui.DrawingSurface;
import org.catrobat.paintroid.ui.KeyboardListener;
import org.catrobat.paintroid.ui.LayerAdapter;
import org.catrobat.paintroid.ui.LayerNavigator;
import org.catrobat.paintroid.ui.MainActivityInteractor;
import org.catrobat.paintroid.ui.MainActivityNavigator;
import org.catrobat.paintroid.ui.Perspective;
import org.catrobat.paintroid.ui.dragndrop.DragAndDropListView;
import org.catrobat.paintroid.ui.tools.DefaultToolOptionsViewController;
import org.catrobat.paintroid.ui.viewholder.BottomBarViewHolder;
import org.catrobat.paintroid.ui.viewholder.BottomNavigationViewHolder;
import org.catrobat.paintroid.ui.viewholder.DrawerLayoutViewHolder;
import org.catrobat.paintroid.ui.viewholder.LayerMenuViewHolder;
import org.catrobat.paintroid.ui.viewholder.TopBarViewHolder;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.widget.TooltipCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;

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
	@VisibleForTesting
	public Perspective perspective;
	@VisibleForTesting
	public Workspace workspace;
	@VisibleForTesting
	public LayerContracts.Model layerModel;
	@VisibleForTesting
	public CommandManager commandManager;
	@VisibleForTesting
	public ToolPaint toolPaint;
	@VisibleForTesting
	public ToolReference toolReference;
	@VisibleForTesting
	public ToolOptionsViewController toolOptionsViewController;

	private LayerPresenter layerPresenter;
	private DrawingSurface drawingSurface;
	private MainActivityContracts.Presenter presenter;
	private DrawerLayoutViewHolder drawerLayoutViewHolder;
	private KeyboardListener keyboardListener;
	private PaintroidApplicationFragment appFragment;
	private DefaultToolController defaultToolController;
	private BottomNavigationViewHolder bottomNavigationViewHolder;
	private CommandFactory commandFactory;

	private Runnable deferredRequestPermissionsResult;

	@Override
	public MainActivityContracts.Presenter getPresenter() {
		return presenter;
	}

	@Override
	public DisplayMetrics getDisplayMetrics() {
		return getResources().getDisplayMetrics();
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (deferredRequestPermissionsResult != null) {
			Runnable runnable = deferredRequestPermissionsResult;
			deferredRequestPermissionsResult = null;

			runnable.run();
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.PocketPaintTheme);
		super.onCreate(savedInstanceState);

		getAppFragment();
		PaintroidApplication.cacheDir = getCacheDir();

		setContentView(R.layout.activity_pocketpaint_main);

		onCreateGlobals();
		onCreateMainView();
		onCreateLayerMenu();
		onCreateDrawingSurface();

		presenter.onCreateTool();

		Intent receivedIntent = getIntent();
		String receivedAction = receivedIntent.getAction();
		String receivedType = receivedIntent.getType();

		if (receivedAction != null && receivedType != null && (receivedAction.equals(Intent.ACTION_SEND) || receivedAction.equals(Intent.ACTION_EDIT)) && receivedType.startsWith("image/")) {
			Uri receivedUri = receivedIntent
					.getParcelableExtra(Intent.EXTRA_STREAM);

			if (receivedUri == null) {
				receivedUri = receivedIntent.getData();
			}

			Objects.requireNonNull(receivedUri);
			Bitmap receivedBitmap = null;

			try {
				FileIO.filename = "image";
				receivedBitmap = FileIO.getBitmapFromUri(getContentResolver(), receivedUri);
			} catch (IOException e) {
				Log.e("Can not read", "Unable to retrieve Bitmap from Uri");
			}

			commandManager.setInitialStateCommand(commandFactory.createInitCommand(receivedBitmap));
			commandManager.reset();
			model.setSavedPictureUri(null);
			model.setCameraImageUri(null);
			workspace.resetPerspective();

			presenter.initializeFromCleanState(null, null);
		} else if (savedInstanceState == null) {
			Intent intent = getIntent();
			String picturePath = intent.getStringExtra(PAINTROID_PICTURE_PATH);
			String pictureName = intent.getStringExtra(PAINTROID_PICTURE_NAME);
			presenter.initializeFromCleanState(picturePath, pictureName);
		} else {
			boolean isFullscreen = savedInstanceState.getBoolean(IS_FULLSCREEN_KEY, false);
			boolean isSaved = savedInstanceState.getBoolean(IS_SAVED_KEY, false);
			boolean isOpenedFromCatroid = savedInstanceState.getBoolean(IS_OPENED_FROM_CATROID_KEY, false);
			boolean wasInitialAnimationPlayed = savedInstanceState.getBoolean(WAS_INITIAL_ANIMATION_PLAYED, false);
			Uri savedPictureUri = savedInstanceState.getParcelable(SAVED_PICTURE_URI_KEY);
			Uri cameraImageUri = savedInstanceState.getParcelable(CAMERA_IMAGE_URI_KEY);

			presenter.restoreState(isFullscreen, isSaved, isOpenedFromCatroid,
					wasInitialAnimationPlayed, savedPictureUri, cameraImageUri);
		}

		commandManager.addCommandListener(this);

		presenter.finishInitialize();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_pocketpaint_more_options, menu);
		presenter.removeMoreOptionsItems(menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		int i = item.getItemId();
		if (i == R.id.pocketpaint_options_export) {
			presenter.saveCopyClicked(true);
		} else if (i == R.id.pocketpaint_options_save_image) {
			presenter.saveImageClicked();
		} else if (i == R.id.pocketpaint_options_save_duplicate) {
			presenter.saveCopyClicked(false);
		} else if (i == R.id.pocketpaint_options_open_image) {
			presenter.loadImageClicked();
		} else if (i == R.id.pocketpaint_options_new_image) {
			presenter.newImageClicked();
		} else if (i == R.id.pocketpaint_options_discard_image) {
			presenter.discardImageClicked();
		} else if (i == R.id.pocketpaint_options_fullscreen_mode) {
			presenter.enterFullscreenClicked();
		} else if (i == R.id.pocketpaint_options_rate_us) {
			presenter.rateUsClicked();
		} else if (i == R.id.pocketpaint_options_help) {
			presenter.showHelpClicked();
		} else if (i == R.id.pocketpaint_options_about) {
			presenter.showAboutClicked();
		} else if (i == android.R.id.home) {
			presenter.backToPocketCodeClicked();
		} else if (i == R.id.pocketpaint_share_image_button) {
			presenter.shareImageClicked();
		} else if (i == R.id.pocketpaint_options_feedback) {
			presenter.sendFeedback();
		} else {
			return false;
		}
		return true;
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

			commandFactory = new DefaultCommandFactory();
			CommandManager synchronousCommandManager = new DefaultCommandManager(new CommonFactory(), layerModel);
			commandManager = new AsyncCommandManager(synchronousCommandManager, layerModel);
			Command initCommand = commandFactory.createInitCommand(metrics.widthPixels, metrics.heightPixels);
			commandManager.setInitialStateCommand(initCommand);
			commandManager.reset();
			appFragment.setCommandManager(commandManager);
		} else {
			commandManager = appFragment.getCommandManager();
		}
		if (appFragment.getToolPaint() == null) {
			appFragment.setToolPaint(new DefaultToolPaint(getApplicationContext()));
		}
		toolPaint = appFragment.getToolPaint();
		if (appFragment.getCurrentTool() == null) {
			appFragment.setCurrentTool(new DefaultToolReference());
		}
		toolReference = appFragment.getCurrentTool();
	}

	public String getName() {
		return this.getPackageName();
	}

	private void onCreateMainView() {
		Context context = this;
		DrawerLayout drawerLayout = findViewById(R.id.pocketpaint_drawer_layout);
		ViewGroup topBarLayout = findViewById(R.id.pocketpaint_layout_top_bar);
		View bottomBarLayout = findViewById(R.id.pocketpaint_main_bottom_bar);
		View bottomNavigationView = findViewById(R.id.pocketpaint_main_bottom_navigation);

		toolOptionsViewController = new DefaultToolOptionsViewController(this);
		drawerLayoutViewHolder = new DrawerLayoutViewHolder(drawerLayout);
		TopBarViewHolder topBarViewHolder = new TopBarViewHolder(topBarLayout);
		BottomBarViewHolder bottomBarViewHolder = new BottomBarViewHolder(bottomBarLayout);
		bottomNavigationViewHolder = new BottomNavigationViewHolder(bottomNavigationView, getResources().getConfiguration().orientation, getApplicationContext());

		perspective = new Perspective(layerModel.getWidth(), layerModel.getHeight());
		workspace = new DefaultWorkspace(layerModel, perspective, new DefaultWorkspace.Listener() {
			@Override
			public void invalidate() {
				drawingSurface.refreshDrawingSurface();
			}
		});
		MainActivityContracts.Navigator navigator = new MainActivityNavigator(this, toolReference);
		MainActivityContracts.Interactor interactor = new MainActivityInteractor();
		model = new MainActivityModel();
		ContextCallback contextCallback = new DefaultContextCallback(context);
		this.defaultToolController = new DefaultToolController(toolReference, toolOptionsViewController,
				new DefaultToolFactory(), commandManager, workspace, toolPaint, contextCallback);
		UserPreferences preferences = new UserPreferences(getPreferences(Context.MODE_PRIVATE));

		presenter = new MainActivityPresenter(this, this, model, workspace,
				navigator, interactor, topBarViewHolder, bottomBarViewHolder, drawerLayoutViewHolder,
				bottomNavigationViewHolder, new DefaultCommandFactory(), commandManager, perspective, defaultToolController, preferences);
		defaultToolController.setOnColorPickedListener(new PresenterColorPickedListener(presenter));

		keyboardListener = new KeyboardListener(drawerLayout);
		setTopBarListeners(topBarViewHolder);
		setBottomBarListeners(bottomBarViewHolder);
		setBottomNavigationListeners(bottomNavigationViewHolder);
		setActionBarToolTips(topBarViewHolder, context);
	}

	private void onCreateLayerMenu() {
		ViewGroup layerLayout = findViewById(R.id.pocketpaint_layer_side_nav_menu);
		DragAndDropListView layerListView = findViewById(R.id.pocketpaint_layer_side_nav_list);

		LayerMenuViewHolder layerMenuViewHolder = new LayerMenuViewHolder(layerLayout);
		LayerNavigator layerNavigator = new LayerNavigator(getApplicationContext());
		layerPresenter = new LayerPresenter(layerModel, layerListView, layerMenuViewHolder,
				commandManager, new DefaultCommandFactory(), layerNavigator);
		LayerAdapter layerAdapter = new LayerAdapter(layerPresenter);
		presenter.setLayerAdapter(layerAdapter);
		layerPresenter.setAdapter(layerAdapter);
		layerListView.setPresenter(layerPresenter);
		layerListView.setAdapter(layerAdapter);

		layerPresenter.refreshLayerMenuViewHolder();
		setLayerMenuListeners(layerMenuViewHolder);
	}

	private void onCreateDrawingSurface() {
		drawingSurface = findViewById(R.id.pocketpaint_drawing_surface_view);
		drawingSurface.setArguments(layerModel, perspective, toolReference, toolOptionsViewController);
		layerPresenter.setDrawingSurface(drawingSurface);

		appFragment.setPerspective(perspective);
		layerPresenter.setDefaultToolController(defaultToolController);
		layerPresenter.setbottomNavigationViewHolder(bottomNavigationViewHolder);
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

	private void setActionBarToolTips(TopBarViewHolder topBar, Context context) {
		TooltipCompat.setTooltipText(topBar.undoButton, context.getString(R.string.button_undo));
		TooltipCompat.setTooltipText(topBar.redoButton, context.getString(R.string.button_redo));
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
		topBar.checkmarkButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				BaseToolWithShape tool = (BaseToolWithShape) toolReference.get();
				tool.onClickOnButton();
			}
		});
	}

	private void setBottomBarListeners(final BottomBarViewHolder viewHolder) {
		ToolType[] toolTypes = ToolType.values();
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
	}

	private void setBottomNavigationListeners(final BottomNavigationViewHolder viewHolder) {
		viewHolder.getBottomNavigationView().setOnNavigationItemSelectedListener(
				new BottomNavigationView.OnNavigationItemSelectedListener() {
					@Override
					public boolean onNavigationItemSelected(@NonNull MenuItem item) {
						if (item.getItemId() == R.id.action_tools) {
							presenter.actionToolsClicked();
						} else if (item.getItemId() == R.id.action_current_tool) {
							presenter.actionCurrentToolClicked();
						} else if (item.getItemId() == R.id.action_color_picker) {
							presenter.showColorPickerClicked();
						} else if (item.getItemId() == R.id.action_layers) {
							presenter.showLayerMenuClicked();
						} else {
							return false;
						}
						return true;
					}
				});
	}

	@Override
	public void initializeActionBar(boolean isOpenedFromCatroid) {
		Toolbar toolbar = findViewById(R.id.pocketpaint_toolbar);
		setSupportActionBar(toolbar);

		ActionBar supportActionBar = getSupportActionBar();
		if (supportActionBar != null) {
			supportActionBar.setDisplayShowTitleEnabled(!isOpenedFromCatroid);
			supportActionBar.setDisplayHomeAsUpEnabled(isOpenedFromCatroid);
			supportActionBar.setHomeButtonEnabled(true);
			supportActionBar.setDisplayShowHomeEnabled(false);
		}
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
			commandManager.shutdown();
			appFragment.setCurrentTool(null);
			appFragment.setCommandManager(null);
			appFragment.setLayerModel(null);
		}

		super.onDestroy();
	}

	@Override
	protected void onSaveInstanceState(@NonNull Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putBoolean(IS_FULLSCREEN_KEY, model.isFullscreen());
		outState.putBoolean(IS_SAVED_KEY, model.isSaved());
		outState.putBoolean(IS_OPENED_FROM_CATROID_KEY, model.isOpenedFromCatroid());
		outState.putBoolean(WAS_INITIAL_ANIMATION_PLAYED, model.wasInitialAnimationPlayed());
		outState.putParcelable(SAVED_PICTURE_URI_KEY, model.getSavedPictureUri());
		outState.putParcelable(CAMERA_IMAGE_URI_KEY, model.getCameraImageUri());
	}

	@Override
	public void onBackPressed() {
		FragmentManager supportFragmentManager = getSupportFragmentManager();
		if (supportFragmentManager.isStateSaved()) {
			super.onBackPressed();
		} else if (!supportFragmentManager.popBackStackImmediate()) {
			presenter.onBackPressed();
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		presenter.handleActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void superHandleActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {

		if (VERSION.SDK_INT == Build.VERSION_CODES.M) {
			deferredRequestPermissionsResult = new Runnable() {
				@Override
				public void run() {
					presenter.handleRequestPermissionsResult(requestCode, permissions, grantResults);
				}
			};
		} else {
			presenter.handleRequestPermissionsResult(requestCode, permissions, grantResults);
		}
	}

	@Override
	public void superHandleRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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
	public void enterFullscreen() {
		drawingSurface.disableAutoScroll();
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
	}

	@Override
	public void exitFullscreen() {
		drawingSurface.enableAutoScroll();
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
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
