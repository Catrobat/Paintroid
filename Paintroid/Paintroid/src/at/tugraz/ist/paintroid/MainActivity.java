/*
 *   This file is part of Paintroid, a software part of the Catroid project.
 *   Copyright (C) 2010  Catroid development team
 *   <http://code.google.com/p/catroid/wiki/Credits>
 *
 *   Paintroid is free software: you can redistribute it and/or modify it
 *   under the terms of the GNU Affero General Public License as published
 *   by the Free Software Foundation, either version 3 of the License, or
 *   at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package at.tugraz.ist.paintroid;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.widget.RelativeLayout;
import at.tugraz.ist.paintroid.commandmanagement.implementation.CommandHandlerSingleton;
import at.tugraz.ist.paintroid.dialog.DialogAbout;
import at.tugraz.ist.paintroid.dialog.DialogError;
import at.tugraz.ist.paintroid.listener.DrawingSurfaceListener;
import at.tugraz.ist.paintroid.ui.DrawingSurface;
import at.tugraz.ist.paintroid.ui.Perspective;
import at.tugraz.ist.paintroid.ui.Toolbar;
import at.tugraz.ist.paintroid.ui.implementation.DrawingSurfacePerspective;
import at.tugraz.ist.paintroid.ui.implementation.DrawingSurfaceView;
import at.tugraz.ist.paintroid.ui.implementation.ToolbarImplementation;

public class MainActivity extends Activity {
	static final String TAG = "PAINTROID";
	private static final CommandHandlerSingleton KEEP_COMMAND_HANDLER_INSTANCE_ALIVE = CommandHandlerSingleton.COMMAND_HANDLER_SINGLETON_INSTANCE;

	public enum ToolType {
		ZOOM, SCROLL, PIPETTE, BRUSH, UNDO, REDO, NONE, MAGIC, RESET, FLOATINGBOX, CURSOR, IMPORTPNG
	}

	private DrawingSurface drawingSurface;
	private Perspective drawingSurfacePerspective;
	private DrawingSurfaceListener drawingSurfaceListener;

	private Uri savedFileUri;
	private boolean showMenu = true;

	private boolean openedWithCatroid;

	// request codes
	public static final int TOOL_MENU = 0;
	public static final int REQ_IMPORTPNG = 1;

	protected Toolbar toolbar;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);
		openedWithCatroid = false;

		toolbar = new ToolbarImplementation(this);

		drawingSurface = (DrawingSurfaceView) findViewById(R.id.drawingSurfaceView);
		drawingSurfacePerspective = new DrawingSurfacePerspective(((SurfaceView) drawingSurface).getHolder());
		drawingSurfaceListener = new DrawingSurfaceListener(drawingSurfacePerspective);

		((View) drawingSurface).setOnTouchListener(drawingSurfaceListener);

		Display display = getWindowManager().getDefaultDisplay();
		int width = display.getWidth();
		int height = display.getHeight();
		Bitmap bitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		drawingSurface.setBitmap(bitmap);

		// check if awesome catroid app opened it:
		Bundle bundle = this.getIntent().getExtras();
		if (bundle != null) {

			String pathToImage = bundle.getString(this.getString(R.string.extra_picture_path_catroid));
			if (pathToImage != null) {
				openedWithCatroid = true;
			}
			if (pathToImage != "") {
				// TODO load image
			}
		}
	}

	@Override
	protected void onDestroy() {
		deleteUndoRedoCacheFiles();
		savedFileUri = null;
		super.onDestroy();
	}

	public void deleteUndoRedoCacheFiles() {
		int undoBitmapCount = 0;
		File undoBitmap = null;
		do {
			if (undoBitmap != null && undoBitmap.exists()) {
				undoBitmap.delete();
			}
			undoBitmap = new File(this.getCacheDir(), String.valueOf(undoBitmapCount) + ".png");
			undoBitmapCount++;
		} while (undoBitmap.exists() || undoBitmapCount < 5);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return true;
	}

	/**
	 * Handle options menu events
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.item_Quit: // Exit the application
			showSecurityQuestionBeforeExit();
			return true;

		case R.id.item_About: // show the about dialog
			DialogAbout about = new DialogAbout(this);
			about.show();
			return true;

		case R.id.item_HideMenu: // hides the toolbar
			RelativeLayout toolbarLayout = (RelativeLayout) findViewById(R.id.BottomRelativeLayout);
			if (showMenu) {
				toolbarLayout.setVisibility(View.INVISIBLE);
				showMenu = false;
			} else {
				toolbarLayout.setVisibility(View.VISIBLE);
				showMenu = true;
			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		MenuItem hideButton = menu.findItem(R.id.item_HideMenu);
		if (showMenu) {
			hideButton.setTitle(R.string.hide_menu);
		} else {
			// Show toolbar directly after the menu button was hit
			RelativeLayout toolbarLayout = (RelativeLayout) findViewById(R.id.BottomRelativeLayout);
			toolbarLayout.setVisibility(View.VISIBLE);
			showMenu = true;
			return false;
		}
		return super.onPrepareOptionsMenu(menu);
	}

	/**
	 * Opens the tool menu
	 */
	public void callToolMenu() {
		Intent intent = new Intent(this, MenuTabActivity.class);
		startActivityForResult(intent, TOOL_MENU);

		overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
	}

	/**
	 * Calls the images chooser for the import png function
	 */
	public void callImportPng() {
		startActivityForResult(new Intent(Intent.ACTION_PICK,
				android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), REQ_IMPORTPNG);
	}

	/**
	 * Listener for ACTIVITY RESULTS (Intent)
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		// get the URI from FileIO Intent and set in DrawSurface
		if (requestCode == TOOL_MENU && resultCode == Activity.RESULT_OK) {
			int selectedToolButtonId = data.getIntExtra("SelectedTool", -1);
			if (selectedToolButtonId != -1) {
				if (ToolType.values().length > selectedToolButtonId && selectedToolButtonId > -1) {
					ToolType selectedTool = ToolType.values()[selectedToolButtonId];
					// TODO set tool
				}
			} else {
				String uriString = data.getStringExtra("UriString");
				String returnValue = data.getStringExtra("IntentReturnValue");

				if (returnValue.contentEquals("LOAD") && uriString != null) {
					// TODO load image
				}
				if (returnValue.contentEquals("NEW")) {
					// TODO new empty bitmap
				}
				if (returnValue.contentEquals("SAVE")) {
					Bitmap bitmap = null; // TODO get drawingSurface bitmap
					File file = at.tugraz.ist.paintroid.FileIO.saveBitmap(MainActivity.this, bitmap, uriString);
					if (file == null) {
						DialogError errorDialog = new DialogError(this, R.string.dialog_error_sdcard_title,
								R.string.dialog_error_sdcard_text);
						errorDialog.show();
					}
				}
			}
		} else if (requestCode == REQ_IMPORTPNG && resultCode == Activity.RESULT_OK) {
			Uri selectedGalleryImage = data.getData();
			String imageFilePath = at.tugraz.ist.paintroid.FileIO.getRealPathFromURI(this, selectedGalleryImage);
			importPngToFloatingBox(imageFilePath);
		}
	}

	protected void importPngToFloatingBox(String uriString) {
		Bitmap newPng = createBitmapFromUri(uriString);
		if (newPng == null) {
			return;
		}

		// TODO set bitmap on drawing surface
	}

	protected Bitmap createBitmapFromUri(String uriString) {
		// First we query the bitmap for dimensions without
		// allocating memory for its pixels.
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		File bitmapFile = new File(uriString);
		if (!bitmapFile.exists()) {
			return null;
		}
		BitmapFactory.decodeFile(uriString, options);

		int width = options.outWidth;
		int height = options.outHeight;

		if (width < 0 || height < 0) {
			return null;
		}

		int size = width > height ? width : height;

		// if the image is too large we subsample it
		if (size > 1000) {

			// we use the thousands digit to dynamically define the sample size
			size = Character.getNumericValue(Integer.toString(size).charAt(0));

			options.inSampleSize = size + 1;
			BitmapFactory.decodeFile(uriString, options);
			width = options.outWidth;
			height = options.outHeight;
		}
		options.inJustDecodeBounds = false;

		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

		// we have to load each pixel for alpha transparency to work with photos
		int[] pixels = new int[width * height];
		BitmapFactory.decodeFile(uriString, options).getPixels(pixels, 0, width, 0, 0, width, height);

		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		return bitmap;
	}

	public String getSavedFileUriString() {
		return savedFileUri.toString().replace("file://", "");
	}

	@Override
	public void onBackPressed() {
		showSecurityQuestionBeforeExit();
	}

	private void showSecurityQuestionBeforeExit() {
		if (openedWithCatroid) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(getString(R.string.use_picture)).setCancelable(false)
					.setPositiveButton(R.string.closing_security_question_yes, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int id) {
							Bitmap bitmap = null; // TODO get bitmap from Catroid
							String name = getString(R.string.temp_picture_name);

							File file = at.tugraz.ist.paintroid.FileIO.saveBitmap(MainActivity.this, bitmap, name);

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
