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
import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import at.tugraz.ist.paintroid.dialog.DialogAbout;
import at.tugraz.ist.paintroid.dialog.DialogBrushPicker;
import at.tugraz.ist.paintroid.dialog.DialogError;
import at.tugraz.ist.paintroid.dialog.colorpicker.ColorPickerDialog;
import at.tugraz.ist.paintroid.graphic.DrawingSurface;
import at.tugraz.ist.paintroid.graphic.utilities.DrawFunctions;
import at.tugraz.ist.paintroid.helper.FileIO;
import at.tugraz.ist.paintroid.helper.Toolbar;

public class MainActivity extends Activity {
	static final String TAG = "PAINTROID";

	public enum ToolType {
		ZOOM, SCROLL, PIPETTE, BRUSH, UNDO, REDO, NONE, MAGIC, RESET, FLOATINGBOX, CURSOR, IMPORTPNG
	}

	public DrawingSurface drawingSurface;

	DialogBrushPicker dialogBrushPicker;
	ColorPickerDialog dialogColorPicker;

	protected Uri savedFileUri;

	// The toolbar buttons
	protected Toolbar toolbar;
	protected boolean showMenu = true;

	private boolean openedWithCatroid;

	//request codes
	public static final int TOOL_MENU = 0;
	public static final int REQ_IMPORTPNG = 1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		drawingSurface = (DrawingSurface) findViewById(R.id.surfaceview);

		toolbar = new Toolbar(this);

		drawingSurface.setToolbar(toolbar);

		drawingSurface.setToolType(ToolType.BRUSH);

		openedWithCatroid = false;

		//check if awesome catroid app opened it:
		Bundle bundle = this.getIntent().getExtras();
		if (bundle == null) {
			return;
		}
		String pathToImage = bundle.getString(this.getString(R.string.extra_picture_path_catroid));
		if (pathToImage != null) {
			openedWithCatroid = true;
		}
		if (pathToImage != "") {
			loadNewImage(pathToImage);
		}
	}

	@Override
	protected void onDestroy() {
		drawingSurface.setOnTouchListener(null);
		deleteUndoRedoCacheFiles();
		drawingSurface = null;
		dialogBrushPicker = null;
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
			//			hideButton.setTitle(R.string.show_menu);  // only change text in menu if toolbar is hidden

			//Show toolbar directly after the menu button was hit
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
					toolbar.setTool(selectedTool);
				}
			} else {
				String uriString = data.getStringExtra("UriString");
				String returnValue = data.getStringExtra("IntentReturnValue");

				if (returnValue.contentEquals("LOAD") && uriString != null) {
					Log.d("PAINTROID", "Main: Uri " + uriString);

					drawingSurface.clearUndoRedo();
					loadNewImage(uriString);
				}
				if (returnValue.contentEquals("NEW")) {
					drawingSurface.newEmptyBitmap();
				}
				if (returnValue.contentEquals("SAVE")) {
					Log.d("PAINTROID", "Main: Get FileActivity return value: " + returnValue);
					savedFileUri = new FileIO(this).saveBitmapToSDCard(getContentResolver(), uriString, drawingSurface
							.getBitmap(), drawingSurface.getCenter());
					if (savedFileUri == null) {
						DialogError error = new DialogError(this, R.string.dialog_error_sdcard_title,
								R.string.dialog_error_sdcard_text);
						error.show();
					}
				}
				drawingSurface.resetPerspective();
			}
		} else if (requestCode == REQ_IMPORTPNG && resultCode == Activity.RESULT_OK) {
			Uri selectedGalleryImage = data.getData();
			//Convert the Android URI to a real path
			String imageFilePath = FileIO.getRealPathFromURI(getContentResolver(), selectedGalleryImage);
			importPngToFloatingBox(imageFilePath);
		}
	}

	private void loadNewImage(String uriString) {
		Bitmap currentImage = DrawFunctions.createBitmapFromUri(uriString);
		// Robotium hack, because only mainActivity threads are allowed to call this function
		if (!Thread.currentThread().getName().equalsIgnoreCase("Instr: android.test.InstrumentationTestRunner")) {
			drawingSurface.setBitmap(currentImage);
		}

		// read xml file
		//		try {
		//			if(!uriString.endsWith(".png"))
		//			{
		//				return;
		//			}
		//			String xmlUriString = uriString.substring(0, uriString.length()-3)+"xml";
		//			File xmlMetafile = new File(xmlUriString);
		//			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		//			DocumentBuilder documentBuilder;
		//			documentBuilder = documentBuilderFactory.newDocumentBuilder();
		//			Document xmlDocument = documentBuilder.parse(xmlMetafile);
		//			xmlDocument.getDocumentElement().normalize();
		//			NodeList middlepointNode = xmlDocument.getElementsByTagName("middlepoint");
		//			if(middlepointNode.getLength() != 1)
		//			{
		//				return;
		//			}
		//			NamedNodeMap attributes = middlepointNode.item(0).getAttributes();
		//			int x = Integer.parseInt(attributes.getNamedItem("position-x").getNodeValue());
		//			int y = Integer.parseInt(attributes.getNamedItem("position-y").getNodeValue());
		//			drawingSurface.setMiddlepoint(x, y);
		//		} catch (Exception e) {
		//			
		//		}
	}

	protected void importPngToFloatingBox(String uriString) {
		Bitmap newPng = createBitmapFromUri(uriString);
		if (newPng == null) {
			return;
		}

		drawingSurface.addPng(newPng);
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
			builder.setMessage(getString(R.string.use_picture)).setCancelable(false).setPositiveButton(
					R.string.closing_security_question_yes, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int id) {
							File file = new File(Environment.getExternalStorageDirectory().toString() + "/Paintroid/"
									+ getString(R.string.temp_picture_name) + ".png"); //TODO: it should be possible to alter .jpg and keep them jpg
							try {
								file.createNewFile();
								new FileIO(MainActivity.this).saveBitmapToSDCard(
										MainActivity.this.getContentResolver(), getString(R.string.temp_picture_name),
										drawingSurface.getBitmap(), drawingSurface.getCenter());

								Bundle bundle = new Bundle();
								bundle
										.putString(getString(R.string.extra_picture_path_catroid), file
												.getAbsolutePath());
								Intent intent = new Intent();
								intent.putExtras(bundle);
								setResult(RESULT_OK, intent);
								MainActivity.this.finish();
							} catch (IOException e) {
								Log.e(TAG, "ERROR", e);
							}
						}
					}).setNegativeButton(R.string.closing_security_question_not, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int id) {
					MainActivity.this.finish();
				}
			});
			AlertDialog alert = builder.create();
			alert.show();
		} else {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(R.string.closing_security_question).setCancelable(false).setPositiveButton(
					R.string.closing_security_question_yes, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int id) {
							MainActivity.this.finish();
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
