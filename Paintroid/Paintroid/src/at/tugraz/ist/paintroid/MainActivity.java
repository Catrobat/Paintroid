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
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import at.tugraz.ist.paintroid.MenuFileActivity.ACTION;
import at.tugraz.ist.paintroid.dialog.DialogAbout;
import at.tugraz.ist.paintroid.dialog.DialogError;
import at.tugraz.ist.paintroid.dialog.DialogSaveFile;
import at.tugraz.ist.paintroid.listener.DrawingSurfaceListener;
import at.tugraz.ist.paintroid.tools.Tool;
import at.tugraz.ist.paintroid.tools.Tool.ToolType;
import at.tugraz.ist.paintroid.tools.implementation.StampTool;
import at.tugraz.ist.paintroid.ui.Toolbar;
import at.tugraz.ist.paintroid.ui.implementation.DrawingSurfaceImplementation;
import at.tugraz.ist.paintroid.ui.implementation.PerspectiveImplementation;
import at.tugraz.ist.paintroid.ui.implementation.ToolbarImplementation;

public class MainActivity extends Activity {

	private abstract class RunnableWithBitmap {
		public abstract void run(Bitmap bitmap);
	}

	public static final int REQ_FILE_MENU = 0;
	public static final int REQ_IMPORTPNG = 1;
	public static final int REQ_FINISH = 3;
	public static final int REQ_TAKE_PICTURE = 4;
	public static final int REQ_TOOLS_DIALOG = 5;
	public static final String EXTRA_INSTANCE_FROM_CATROBAT = "EXTRA_INSTANCE_FROM_CATROBAT";

	protected DrawingSurfaceListener mDrawingSurfaceListener;
	protected Toolbar mToolbar;

	protected boolean mToolbarIsVisible = true;
	protected boolean mOpenedWithCatroid;

	private Uri mCameraImageUri;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		PaintroidApplication.DRAWING_SURFACE = (DrawingSurfaceImplementation) findViewById(R.id.drawingSurfaceView);
		PaintroidApplication.CURRENT_PERSPECTIVE = new PerspectiveImplementation(
				((SurfaceView) PaintroidApplication.DRAWING_SURFACE).getHolder());
		mDrawingSurfaceListener = new DrawingSurfaceListener();
		mToolbar = new ToolbarImplementation(this);

		((View) PaintroidApplication.DRAWING_SURFACE).setOnTouchListener(mDrawingSurfaceListener);

		// check if awesome Catroid app created this activity
		String catroidPicturePath = null;
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			catroidPicturePath = extras.getString(getString(R.string.extra_picture_path_catroid));
		}
		if (catroidPicturePath != null) {
			mOpenedWithCatroid = true;
		}
		// check if catrobat wants to take a photo
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

	@Override
	protected void onDestroy() {
		PaintroidApplication.COMMAND_MANAGER.resetAndClear();
		((DrawingSurfaceImplementation) PaintroidApplication.DRAWING_SURFACE).recycleBitmap();
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.item_Quit:
				showSecurityQuestionBeforeExit();
				return true;
			case R.id.item_About:
				DialogAbout about = new DialogAbout(this);
				about.show();
				return true;
			case R.id.item_HideMenu:
				RelativeLayout toolbarLayout = (RelativeLayout) findViewById(R.id.BottomRelativeLayout);
				if (mToolbarIsVisible) {
					toolbarLayout.setVisibility(View.INVISIBLE);
					mToolbarIsVisible = false;
					// set fullscreen
					getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
					getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
				} else {
					toolbarLayout.setVisibility(View.VISIBLE);
					mToolbarIsVisible = true;
					// set not fullscreen
					getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
					getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
				}
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		MenuItem hideMenuButton = menu.findItem(R.id.item_HideMenu);
		if (mToolbarIsVisible) {
			hideMenuButton.setTitle(R.string.hide_menu);
		} else {
			mToolbarIsVisible = true;
			RelativeLayout toolbarLayout = (RelativeLayout) findViewById(R.id.BottomRelativeLayout);
			toolbarLayout.setVisibility(View.VISIBLE);
			// set not fullscreen
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

			return false;
		}
		return super.onPrepareOptionsMenu(menu);
	}

	public void openToolDialog() {
		Intent intent = new Intent(this, ToolsDialogActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
		intent.putExtra(EXTRA_INSTANCE_FROM_CATROBAT, mOpenedWithCatroid);
		startActivityForResult(intent, REQ_TOOLS_DIALOG);
		overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
	}

	public void importPng() {
		Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
		startActivityForResult(intent, REQ_IMPORTPNG);
	}

	private void showFileMenu() {
		Intent intent = new Intent(this, MenuFileActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
		startActivityForResult(intent, REQ_FILE_MENU);
		overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode != Activity.RESULT_OK) {
			// nothing
		} else if (requestCode == REQ_TOOLS_DIALOG) {

			int selectedToolButtonId = data.getIntExtra(ToolsDialogActivity.EXTRA_SELECTED_TOOL, -1);
			if (selectedToolButtonId != -1) {
				if (ToolType.values().length > selectedToolButtonId && selectedToolButtonId > -1) {
					ToolType tooltype = ToolType.values()[selectedToolButtonId];
					switch (tooltype) {
						case REDO:
							PaintroidApplication.COMMAND_MANAGER.redo(); // FIXME redo should be on toolbar
							break;
						case UNDO:
							PaintroidApplication.COMMAND_MANAGER.undo();
							break;
						case IMPORTPNG:
							importPng();
							break;
						case FILEMENU:
							showFileMenu();
							break;
						case BACK_TO_CATROID:
							showSecurityQuestionBeforeExit();
							break;
						case SAVE:
							final Bundle bundle = new Bundle();
							DialogSaveFile saveDialog = new DialogSaveFile(this, bundle);
							saveDialog.setOnDismissListener(new OnDismissListener() {
								@Override
								public void onDismiss(DialogInterface dialog) {
									String saveFileName = bundle.getString(DialogSaveFile.BUNDLE_SAVEFILENAME);
									saveFile(saveFileName);
								}
							});
							saveDialog.show();
							break;
						default:
							switchTool(tooltype);
							break;
					}
				}
			}
		} else if (requestCode == REQ_FILE_MENU) {
			if (data != null) {
				switch ((ACTION) data.getSerializableExtra(MenuFileActivity.RET_ACTION)) {
					case LOAD:
						loadBitmapFromUri((Uri) data.getParcelableExtra(MenuFileActivity.RET_URI));
						break;
					case NEW:
						initialiseNewBitmap();
						// PaintroidApplication.CURRENT_PERSPECTIVE.resetScaleAndTranslation();
						// PaintroidApplication.COMMAND_MANAGER.commitCommand(new ClearCommand());
						break;
					case SAVE:
						String fileName = data.getStringExtra(MenuFileActivity.RET_FILENAME);
						saveFile(fileName);

						break;
				}
			}
		} else if (requestCode == REQ_IMPORTPNG) {
			Uri selectedGalleryImage = data.getData();
			String imageFilePath = FileIO.getRealPathFromURI(this, selectedGalleryImage);
			importPngToFloatingBox(imageFilePath);

		} else if (requestCode == REQ_FINISH) {
			finish();
		} else if (requestCode == REQ_TAKE_PICTURE) {
			loadBitmapFromUri(mCameraImageUri);
		}
	}

	private void saveFile(String fileName) {
		if (FileIO.saveBitmap(this, PaintroidApplication.DRAWING_SURFACE.getBitmap(), fileName) == null) {
			new DialogError(this, R.string.dialog_error_save_title, R.string.dialog_error_sdcard_text).show();
		}
	}

	private void switchTool(ToolType changeToToolType) {
		Paint tempPaint = new Paint(PaintroidApplication.CURRENT_TOOL.getDrawPaint());
		Tool tool = Utils.createTool(changeToToolType, this, PaintroidApplication.DRAWING_SURFACE);

		mToolbar.setTool(tool);
		Log.d(PaintroidApplication.TAG, "switchTool set CURRENT_TOOL");
		PaintroidApplication.CURRENT_TOOL = tool;
		Log.d(PaintroidApplication.TAG, "switchTool setDrawPaint");
		PaintroidApplication.CURRENT_TOOL.setDrawPaint(tempPaint);
		Log.d(PaintroidApplication.TAG, "switch tool after setDrawPaint");
	}

	protected void importPngToFloatingBox(String filePath) {
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

	private void loadBitmapFromUri(final Uri uri) {
		// FIXME Loading a mutable (!) bitmap from the gallery should be easier *sigh* ...
		// Utils.createFilePathFromUri does not work with all kinds of Uris.
		// Utils.decodeFile is necessary to load even large images as mutable bitmaps without
		// running out of memory.
		Log.d(PaintroidApplication.TAG, "Load Uri " + uri); // TODO remove logging

		String filepath = null;

		if (uri == null || uri.toString().length() < 1) {
			Log.e(PaintroidApplication.TAG, "BAD URI: cannot load image");
		} else {
			filepath = Utils.createFilePathFromUri(this, uri);
		}

		if (filepath == null || filepath.length() < 1) {
			Log.e("PAINTROID", "BAD URI " + uri);
		} else {
			loadBitmapFromFileAndRun(new File(filepath), new RunnableWithBitmap() {
				@Override
				public void run(Bitmap bitmap) {
					PaintroidApplication.DRAWING_SURFACE.resetBitmap(bitmap);
				}
			});
		}
	}

	private void loadBitmapFromFileAndRun(final File file, final RunnableWithBitmap runnable) {
		String loadMessge = getResources().getString(R.string.dialog_load);
		final ProgressDialog dialog = ProgressDialog.show(MainActivity.this, "", loadMessge, true);

		Thread thread = new Thread() {
			@Override
			public void run() {
				Bitmap bitmap = Utils.getBitmapFromFile(file);// Utils.decodeFile(MainActivity.this, file);
				if (bitmap != null) {
					runnable.run(bitmap);
				} else {
					Log.e("PAINTROID", "BAD FILE " + file);
				}
				dialog.dismiss();
			}
		};
		thread.start();
	}

	private void takePhoto() {
		mCameraImageUri = Uri.fromFile(FileIO.createNewEmptyPictureFile(this, getString(R.string.temp_picture_name)
				+ ".png"));
		if (mCameraImageUri == null) {
			DialogError error = new DialogError(this, R.string.dialog_error_sdcard_title,
					R.string.dialog_error_sdcard_text);
			error.show();
			return;
		}
		Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
		intent.putExtra(MediaStore.EXTRA_OUTPUT, mCameraImageUri);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
		startActivityForResult(intent, REQ_TAKE_PICTURE);
	}

	@Override
	public void onBackPressed() {
		if (!mToolbarIsVisible) {
			RelativeLayout toolbarLayout = (RelativeLayout) findViewById(R.id.BottomRelativeLayout);
			toolbarLayout.setVisibility(View.VISIBLE);
			// set not fullscreen
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

		} else if (PaintroidApplication.CURRENT_TOOL.getToolType() == ToolType.BRUSH) {
			showSecurityQuestionBeforeExit();
		} else {
			switchTool(ToolType.BRUSH);
		}

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

	protected void exitToCatroid() {
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

	protected void initialiseNewBitmap() {
		Display display = getWindowManager().getDefaultDisplay();
		int width = display.getWidth();
		int height = display.getHeight();
		Bitmap bitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		bitmap.eraseColor(Color.TRANSPARENT);
		PaintroidApplication.DRAWING_SURFACE.resetBitmap(bitmap);
		PaintroidApplication.CURRENT_PERSPECTIVE.resetScaleAndTranslation();
	}

	public void onToolbarClick(View view) {
		// empty stub
	}
}
