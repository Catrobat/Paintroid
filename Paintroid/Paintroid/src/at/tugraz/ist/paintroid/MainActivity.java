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
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.widget.RelativeLayout;
import at.tugraz.ist.paintroid.MenuFileActivity.ACTION;
import at.tugraz.ist.paintroid.dialog.DialogAbout;
import at.tugraz.ist.paintroid.dialog.DialogError;
import at.tugraz.ist.paintroid.listener.DrawingSurfaceListener;
import at.tugraz.ist.paintroid.tools.Tool;
import at.tugraz.ist.paintroid.tools.Tool.ToolType;
import at.tugraz.ist.paintroid.tools.implementation.StampTool;
import at.tugraz.ist.paintroid.ui.Perspective;
import at.tugraz.ist.paintroid.ui.Toolbar;
import at.tugraz.ist.paintroid.ui.implementation.DrawingSurfaceImplementation;
import at.tugraz.ist.paintroid.ui.implementation.PerspectiveImplementation;
import at.tugraz.ist.paintroid.ui.implementation.ToolbarImplementation;

public class MainActivity extends Activity {

	private abstract class RunnableWithBitmap {
		public abstract void run(Bitmap bitmap);
	}

	public static final int REQ_TAB_MENU = 0;
	public static final int REQ_IMPORTPNG = 1;

	protected Perspective mPerspective;
	protected DrawingSurfaceListener mDrawingSurfaceListener;
	protected Toolbar mToolbar;

	protected boolean mToolbarIsVisible = true;
	protected boolean mOpenedWithCatroid;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		PaintroidApplication.DRAWING_SURFACE = (DrawingSurfaceImplementation) findViewById(R.id.drawingSurfaceView);
		mPerspective = new PerspectiveImplementation(((SurfaceView) PaintroidApplication.DRAWING_SURFACE).getHolder());
		mDrawingSurfaceListener = new DrawingSurfaceListener(mPerspective);
		mToolbar = new ToolbarImplementation(this);

		((View) PaintroidApplication.DRAWING_SURFACE).setOnTouchListener(mDrawingSurfaceListener);
		PaintroidApplication.DRAWING_SURFACE.setPerspective(mPerspective);

		// check if awesome Catroid app created this activity
		String catroidPicturePath = null;
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			catroidPicturePath = extras.getString(getString(R.string.extra_picture_path_catroid));
		}
		if (catroidPicturePath != null) {
			mOpenedWithCatroid = true;
		}
		if (mOpenedWithCatroid && catroidPicturePath.length() > 0) {
			loadBitmapFromFileAndRun(new File(catroidPicturePath), new RunnableWithBitmap() {
				@Override
				public void run(Bitmap bitmap) {
					PaintroidApplication.DRAWING_SURFACE.resetBitmap(bitmap);
				}
			});
		} else {
			Display display = getWindowManager().getDefaultDisplay();
			int width = display.getWidth();
			int height = display.getHeight();
			Bitmap bitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
			PaintroidApplication.DRAWING_SURFACE.resetBitmap(bitmap);
		}
	}

	@Override
	protected void onDestroy() {
		// deleteUndoRedoCacheFiles();
		PaintroidApplication.COMMAND_HANDLER.resetAndClear();
		super.onDestroy();
	}

	// public void deleteUndoRedoCacheFiles() {
	// int undoBitmapCount = 0;
	// File undoBitmap = null;
	// do {
	// if (undoBitmap != null && undoBitmap.exists()) {
	// undoBitmap.delete();
	// }
	// undoBitmap = new File(this.getCacheDir(), String.valueOf(undoBitmapCount) + ".png");
	// undoBitmapCount++;
	// } while (undoBitmap.exists() || undoBitmapCount < 5);
	// }

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
				} else {
					toolbarLayout.setVisibility(View.VISIBLE);
					mToolbarIsVisible = true;
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
			return false;
		}
		return super.onPrepareOptionsMenu(menu);
	}

	public void openTabMenu() {
		Intent intent = new Intent(this, MenuTabActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
		startActivityForResult(intent, REQ_TAB_MENU);
		overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
	}

	public void importPng() {
		Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
		startActivityForResult(intent, REQ_IMPORTPNG);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode != Activity.RESULT_OK) {
			// nothing
		} else if (requestCode == REQ_TAB_MENU) {

			int selectedToolButtonId = data.getIntExtra(MenuToolsActivity.EXTRA_SELECTED_TOOL, -1);
			if (selectedToolButtonId != -1) {
				if (ToolType.values().length > selectedToolButtonId && selectedToolButtonId > -1) {
					ToolType tooltype = ToolType.values()[selectedToolButtonId];
					switch (tooltype) {
						case REDO:
							PaintroidApplication.COMMAND_HANDLER.redo(); // FIXME redo should be on toolbar
							break;
						case IMPORTPNG:
							importPng();
						default:
							Paint tempPaint = new Paint(PaintroidApplication.CURRENT_TOOL.getDrawPaint());
							Tool tool = Utils.createTool(tooltype, this, PaintroidApplication.DRAWING_SURFACE);
							mToolbar.setTool(tool);
							PaintroidApplication.CURRENT_TOOL = tool;
							PaintroidApplication.CURRENT_TOOL.setDrawPaint(tempPaint);
							break;
					}
				}
			} else if (data != null) {
				switch ((ACTION) data.getSerializableExtra(MenuFileActivity.RET_ACTION)) {
					case LOAD:
						loadBitmapFromUri((Uri) data.getParcelableExtra(MenuFileActivity.RET_URI));
						break;
					case NEW:
						mPerspective.resetScaleAndTranslation();
						PaintroidApplication.DRAWING_SURFACE.clearBitmap();
						break;
					case SAVE:
						String name = data.getStringExtra(MenuFileActivity.RET_FILENAME);
						if (FileIO.saveBitmap(this, PaintroidApplication.DRAWING_SURFACE.getBitmap(), name) == null) {
							new DialogError(this, R.string.dialog_error_sdcard_title, R.string.dialog_error_sdcard_text)
									.show();
						}
						break;
				}
			}

		} else if (requestCode == REQ_IMPORTPNG) {
			Uri selectedGalleryImage = data.getData();
			String imageFilePath = at.tugraz.ist.paintroid.FileIO.getRealPathFromURI(this, selectedGalleryImage);
			importPngToFloatingBox(imageFilePath);
		}
	}

	protected void importPngToFloatingBox(String filePath) {
		loadBitmapFromFileAndRun(new File(filePath), new RunnableWithBitmap() {
			@Override
			public void run(Bitmap bitmap) {
				StampTool tool = (StampTool) PaintroidApplication.CURRENT_TOOL;
				tool.addBitmap(bitmap);
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
				Bitmap bitmap = Utils.decodeFile(MainActivity.this, file);
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

	@Override
	public void onBackPressed() {
		showSecurityQuestionBeforeExit();
	}

	private void showSecurityQuestionBeforeExit() {
		if (mOpenedWithCatroid) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(getString(R.string.use_picture)).setCancelable(false)
					.setPositiveButton(R.string.closing_security_question_yes, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int id) {
							File file = FileIO.saveBitmap(MainActivity.this,
									PaintroidApplication.DRAWING_SURFACE.getBitmap(),
									getString(R.string.temp_picture_name));

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
					}).setNegativeButton(R.string.closing_security_question_not, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int id) {
							finish();
						}
					});
			AlertDialog alert = builder.create();
			alert.show();
		} else {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(R.string.closing_security_question).setCancelable(false)
					.setPositiveButton(R.string.closing_security_question_yes, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int id) {
							finish();
						}
					}).setNegativeButton(R.string.closing_security_question_not, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
						}
					});
			AlertDialog alert = builder.create();
			alert.show();
		}
	}
}
