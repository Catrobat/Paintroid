/*    Catroid: An on-device graphical programming language for Android devices
 *    Copyright (C) 2010  Catroid development team
 *    (<http://code.google.com/p/catroid/wiki/Credits>)
 *
 *    This program is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package at.tugraz.ist.paintroid;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint.Cap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import at.tugraz.ist.paintroid.dialog.DialogAbout;
import at.tugraz.ist.paintroid.dialog.DialogBrushPicker;
import at.tugraz.ist.paintroid.dialog.DialogError;
import at.tugraz.ist.paintroid.dialog.DialogHelp;
import at.tugraz.ist.paintroid.dialog.DialogWarning;
import at.tugraz.ist.paintroid.dialog.colorpicker.ColorPickerDialog;
import at.tugraz.ist.paintroid.graphic.DrawingSurface;
import at.tugraz.ist.paintroid.graphic.DrawingSurface.ColorPickupListener;
import at.tugraz.ist.paintroid.graphic.DrawingSurface.Mode;
import at.tugraz.ist.paintroid.graphic.listeners.BaseSurfaceListener;
import at.tugraz.ist.paintroid.graphic.utilities.Brush;
import at.tugraz.ist.paintroid.graphic.utilities.Tool.ToolState;

public class MainActivity extends Activity implements OnClickListener, OnLongClickListener {
	static final String TAG = "PAINTROID";

	static final int REQ_FILEACTIVITY = 0;
	static final int REQ_IMPORTPNG = 1;

	DrawingSurface drawingSurface;
	DialogBrushPicker dialogBrushPicker;
	ColorPickerDialog dialogColorPicker;
	Uri savedFileUri;

	// toolbar buttons
	ImageButton handToolButton;
	ImageButton zoomToolButton;
	ImageButton brushToolButton;
	ImageButton eyeDropperToolButton;
	ImageButton magicWandToolButton;
	ImageButton undoToolButton;
	ImageButton redoToolButton;
	ImageButton fileActivityButton;

	// top left buttons
	Button colorPickerButton;
	ImageButton brushStrokeButton;

	public enum ToolbarItem {
		HAND, ZOOM, BRUSH, EYEDROPPER, MAGICWAND, UNDO, REDO, NONE, RESET
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		DisplayMetrics displayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

		drawingSurface = (DrawingSurface) findViewById(R.id.surfaceview);

		handToolButton = (ImageButton) this.findViewById(R.id.ibtn_handTool);
		handToolButton.setOnClickListener(this);
		handToolButton.setOnLongClickListener(this);

		zoomToolButton = (ImageButton) this.findViewById(R.id.ibtn_zoomTool);
		zoomToolButton.setOnClickListener(this);
		zoomToolButton.setOnLongClickListener(this);

		brushToolButton = (ImageButton) this.findViewById(R.id.ibtn_brushTool);
		brushToolButton.setOnClickListener(this);
		brushToolButton.setOnLongClickListener(this);

		eyeDropperToolButton = (ImageButton) this.findViewById(R.id.ibtn_eyeDropperTool);
		eyeDropperToolButton.setOnClickListener(this);
		eyeDropperToolButton.setOnLongClickListener(this);

		magicWandToolButton = (ImageButton) this.findViewById(R.id.ibtn_magicWandTool);
		magicWandToolButton.setOnClickListener(this);
		magicWandToolButton.setOnLongClickListener(this);

		undoToolButton = (ImageButton) this.findViewById(R.id.ibtn_undoTool);
		undoToolButton.setOnClickListener(this);
		undoToolButton.setOnLongClickListener(this);

		redoToolButton = (ImageButton) this.findViewById(R.id.ibtn_redoTool);
		redoToolButton.setOnClickListener(this);
		redoToolButton.setOnLongClickListener(this);

		fileActivityButton = (ImageButton) this.findViewById(R.id.ibtn_fileActivity);
		fileActivityButton.setOnClickListener(this);
		fileActivityButton.setOnLongClickListener(this);

		colorPickerButton = (Button) this.findViewById(R.id.btn_Color);
		colorPickerButton.setOnClickListener(this);
		colorPickerButton.setOnLongClickListener(this);
		colorPickerButton.setBackgroundColor(DrawingSurface.STDCOLOR);

		brushStrokeButton = (ImageButton) this.findViewById(R.id.ibtn_brushStroke);
		brushStrokeButton.setOnClickListener(this);
		brushStrokeButton.setOnLongClickListener(this);

		updateBrushTypeButton();
		brushToolButton.setBackgroundResource(R.drawable.ic_brush_active);
		drawingSurface.setActionType(ToolbarItem.BRUSH);
	}

	@Override
	protected void onDestroy() {
		drawingSurface.setOnTouchListener(null);
		drawingSurface.getZoomStatus().deleteObservers();
		deleteCacheFiles(); // delete the undo and redo cached pictures
		drawingSurface = null;
		dialogBrushPicker = null;
		savedFileUri = null;
		super.onDestroy();
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// toggle select center menu item
		MenuItem centerItem = menu.findItem(R.id.item_Middlepoint);
		if (drawingSurface.getMode() == Mode.CENTERPOINT) {
			centerItem.setTitle(R.string.centerpoint_save);
		} else {
			centerItem.setTitle(R.string.centerpoint_define);
		}
		return super.onPrepareOptionsMenu(menu);
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
				this.finish();
				return true;
			case R.id.item_Clear:
				drawingSurface.setBitmap(null);
				return true;
			case R.id.item_About:
				DialogAbout about = new DialogAbout(this);
				about.show();
				return true;
			case R.id.item_Reset:
				drawingSurface.getZoomStatus().resetZoomState();
				return true;
			case R.id.item_Middlepoint:
				drawingSurface.toggleCenterpointMode();
				return true;
			case R.id.item_FloatingBox:
				drawingSurface.toggleFloatingBoxMode();
				return true;
			case R.id.item_ImportPng:
				startActivityForResult(new Intent(Intent.ACTION_PICK,
						android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), REQ_IMPORTPNG);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.ibtn_handTool:
				deselectAllToolbarButtons();
				handToolButton.setBackgroundResource(R.drawable.ic_hand_active);
				drawingSurface.setActionType(ToolbarItem.HAND);
				break;
			case R.id.ibtn_zoomTool:
				deselectAllToolbarButtons();
				zoomToolButton.setBackgroundResource(R.drawable.ic_zoom_active);
				drawingSurface.setActionType(ToolbarItem.ZOOM);
				break;
			case R.id.ibtn_brushTool:
				deselectAllToolbarButtons();
				brushToolButton.setBackgroundResource(R.drawable.ic_brush_active);
				drawingSurface.setActionType(ToolbarItem.BRUSH);
				break;
			case R.id.ibtn_eyeDropperTool:
				deselectAllToolbarButtons();
				eyeDropperToolButton.setBackgroundResource(R.drawable.ic_eyedropper_active);
				drawingSurface.setActionType(ToolbarItem.EYEDROPPER);
				ColorPickupListener colorPickupListener = new ColorPickupListener() {
					@Override
					public void colorChanged(int color) {
						if (color == Color.TRANSPARENT) {
							colorPickerButton.setBackgroundResource(R.drawable.transparentrepeat);
						} else {
							colorPickerButton.setBackgroundColor(color);
						}
						drawingSurface.setActiveColor(color);
					}
				};
				drawingSurface.setColorPickupListener(colorPickupListener);
				break;
			case R.id.ibtn_magicWandTool:
				deselectAllToolbarButtons();
				if (getCurrentImage() == null) {
					DialogWarning warning = new DialogWarning(this);
					warning.show();
				} else {
					magicWandToolButton.setBackgroundResource(R.drawable.ic_magicwand_active);
					drawingSurface.setActionType(ToolbarItem.MAGICWAND);
				}
				break;
			case R.id.ibtn_undoTool:
				drawingSurface.undoOneStep();
				break;
			case R.id.ibtn_redoTool:
				drawingSurface.redoOneStep();
				break;
			case R.id.ibtn_fileActivity:
				Intent fileActivityIntent = new Intent(this, FileActivity.class);
				startActivityForResult(fileActivityIntent, REQ_FILEACTIVITY);
				break;
			case R.id.btn_Color:
				if (dialogColorPicker == null) {
					ColorPickerDialog.OnColorChangedListener listener = new ColorPickerDialog.OnColorChangedListener() {
						@Override
						public void colorChanged(int color) {
							if (color == Color.TRANSPARENT) {
								colorPickerButton.setBackgroundResource(R.drawable.transparentrepeat);
							} else {
								colorPickerButton.setBackgroundColor(color);
							}
							drawingSurface.setActiveColor(color);
						}
					};
					dialogColorPicker = new ColorPickerDialog(this, listener, drawingSurface.getActiveColor());
				}
				dialogColorPicker.show();
				break;
			case R.id.ibtn_brushStroke:
				if (dialogBrushPicker == null) {
					DialogBrushPicker.OnBrushChangedListener listener = new DialogBrushPicker.OnBrushChangedListener() {
						@Override
						public void setCap(Cap cap) {
							drawingSurface.setActiveBrush(cap);
							updateBrushTypeButton();
						}

						@Override
						public void setStroke(int stroke) {
							drawingSurface.setActiveBrush(stroke);
							updateBrushTypeButton();
						}
					};
					dialogBrushPicker = new DialogBrushPicker(this, listener);
				}
				dialogBrushPicker.show();
				break;
			default:
				Log.e(TAG, "Clicked on unknown element!");
		}
	}

	@Override
	public boolean onLongClick(View v) {
		DialogHelp help;
		switch (v.getId()) {
			case R.id.ibtn_handTool:
				help = new DialogHelp(this, R.id.ibtn_handTool);
				help.show();
				break;
			case R.id.ibtn_zoomTool:
				help = new DialogHelp(this, R.id.ibtn_zoomTool);
				help.show();
				break;
			case R.id.ibtn_brushTool:
				help = new DialogHelp(this, R.id.ibtn_brushTool);
				help.show();
				break;
			case R.id.ibtn_eyeDropperTool:
				help = new DialogHelp(this, R.id.ibtn_eyeDropperTool);
				help.show();
				break;
			case R.id.ibtn_magicWandTool:
				help = new DialogHelp(this, R.id.ibtn_magicWandTool);
				help.show();
				break;
			case R.id.ibtn_undoTool:
				help = new DialogHelp(this, R.id.ibtn_undoTool);
				help.show();
				break;
			case R.id.ibtn_redoTool:
				help = new DialogHelp(this, R.id.ibtn_redoTool);
				help.show();
				break;
			case R.id.ibtn_fileActivity:
				help = new DialogHelp(this, R.id.ibtn_fileActivity);
				help.show();
				break;
			case R.id.btn_Color:
				help = new DialogHelp(this, R.id.btn_Color);
				help.show();
				break;
			case R.id.ibtn_brushStroke:
				help = new DialogHelp(this, R.id.ibtn_brushStroke);
				help.show();
				break;
			default:
				Log.e(TAG, "Long-clicked on unknown element!");
		}
		return true;
	}

	private void deselectAllToolbarButtons() {
		eyeDropperToolButton.setBackgroundResource(R.drawable.ic_eyedropper);
		brushToolButton.setBackgroundResource(R.drawable.ic_brush);
		handToolButton.setBackgroundResource(R.drawable.ic_hand);
		magicWandToolButton.setBackgroundResource(R.drawable.ic_magicwand);
		zoomToolButton.setBackgroundResource(R.drawable.ic_zoom);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		// get the URI from FileIO Intent and set in DrawingSurface
		if (requestCode == REQ_FILEACTIVITY && resultCode == Activity.RESULT_OK) {
			String uriString = data.getStringExtra("UriString");
			String ReturnValue = data.getStringExtra("IntentReturnValue");

			if (ReturnValue.contentEquals("LOAD") && uriString != null) {
				Log.d("PAINTROID", "Main: Uri " + uriString);
				drawingSurface.clearUndoRedo();
				loadNewImage(uriString);
			}

			if (ReturnValue.contentEquals("NEW")) {
				drawingSurface.newEmptyBitmap();
			}

			if (ReturnValue.contentEquals("SAVE")) {
				Log.d("PAINTROID", "Main: Get FileActivity return value: " + ReturnValue);
				savedFileUri = new FileIO(this).saveBitmapToSDCard(getContentResolver(), uriString, getCurrentImage(),
						drawingSurface.getCenter());
				if (savedFileUri == null) {
					DialogError error = new DialogError(this, R.string.dialog_error_sdcard_title,
							R.string.dialog_error_sdcard_text);
					error.show();
				}
			}
			drawingSurface.getZoomStatus().resetZoomState();

		} else if (requestCode == REQ_IMPORTPNG && resultCode == Activity.RESULT_OK) {
			Uri selectedGalleryImage = data.getData();
			//Convert the Android URI to a real path
			String imageFilePath = FileIO.getRealPathFromURI(getContentResolver(), selectedGalleryImage);
			importPngToFloatingBox(imageFilePath);
		}
	}

	private void loadNewImage(String uriString) {
		Bitmap currentImage = createBitmapFromUri(uriString);
		// Robotium hack, because only mainActivity threads are allowed to call this function
		if (!Thread.currentThread().getName().equalsIgnoreCase("Instr: android.test.InstrumentationTestRunner")) {
			drawingSurface.setBitmap(currentImage);
		}

		// TODO: what is this?
		//		try {
		//			if (!uriString.endsWith(".png")) {
		//				return;
		//			}
		//			String xmlUriString = uriString.substring(0, uriString.length() - 3) + "xml";
		//			File xmlMetafile = new File(xmlUriString);
		//			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		//			DocumentBuilder documentBuilder;
		//			documentBuilder = documentBuilderFactory.newDocumentBuilder();
		//			Document xmlDocument = documentBuilder.parse(xmlMetafile);
		//			xmlDocument.getDocumentElement().normalize();
		//			NodeList centerNode = xmlDocument.getElementsByTagName("center");
		//			if (centerNode.getLength() != 1) {
		//				return;
		//			}
		//		} catch (Exception e) {
		//			Log.e(TAG, "Error loading new image.", e);
		//		}
	}

	private void importPngToFloatingBox(String uriString) {
		Bitmap newPng = createBitmapFromUri(uriString);
		if (newPng == null) {
			return;
		}
		drawingSurface.addPng(newPng);
	}

	private Bitmap createBitmapFromUri(String uriString) {
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

	private void updateBrushTypeButton() {
		Brush brush = drawingSurface.getActiveBrush();
		switch (brush.cap) {
			case SQUARE:
				switch (brush.stroke) {
					case Brush.stroke1:
						brushStrokeButton.setBackgroundResource(R.drawable.rect_1_32);
						break;
					case Brush.stroke5:
						brushStrokeButton.setBackgroundResource(R.drawable.rect_2_32);
						break;
					case Brush.stroke15:
						brushStrokeButton.setBackgroundResource(R.drawable.rect_3_32);
						break;
					case Brush.stroke25:
						brushStrokeButton.setBackgroundResource(R.drawable.rect_4_32);
						break;
				}
				break;
			case ROUND:
				switch (brush.stroke) {
					case Brush.stroke1:
						brushStrokeButton.setBackgroundResource(R.drawable.circle_1_32);
						break;
					case Brush.stroke5:
						brushStrokeButton.setBackgroundResource(R.drawable.circle_2_32);
						break;
					case Brush.stroke15:
						brushStrokeButton.setBackgroundResource(R.drawable.circle_3_32);
						break;
					case Brush.stroke25:
						brushStrokeButton.setBackgroundResource(R.drawable.circle_4_32);
						break;
				}
				break;
		}
	}

	public void deleteCacheFiles() {
		// Deletes the undo and redo cached pictures
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

	//------------------------------Methods For JUnit TESTING---------------------------------------
	public void setAntiAliasing(boolean b) {
		drawingSurface.setAntiAliasing(b);
	}

	public Bitmap getCurrentImage() {
		return drawingSurface.getBitmap();
	}

	public String getSavedFileUriString() {
		Log.d("PAINTROID-TEST", "SaveString" + savedFileUri.toString());
		return savedFileUri.toString().replace("file://", "");
	}

	public Object getImageButtonBackground(int imageButtonID) {
		switch (imageButtonID) {
			case 1:
				return handToolButton.getContext();
			case 2:
				return zoomToolButton.getContext();
			case 3:
				return brushToolButton.getContext();
			case 4:
				return eyeDropperToolButton.getContext();
			case 5:
				return magicWandToolButton.getContext();
			case 6:
				return undoToolButton.getContext();
			case 7:
				return redoToolButton.getContext();
			case 8:
				return fileActivityButton.getContext();
			default:
				return null;
		}
	}

	public String getZoomLevel() {
		return String.valueOf(drawingSurface.getZoomStatus().getZoomLevel());
	}

	public float getScrollX() {
		return drawingSurface.getZoomStatus().getScrollX();
	}

	public float getScrollY() {
		return drawingSurface.getZoomStatus().getScrollY();
	}

	public int getSelectedColor() {
		return drawingSurface.getActiveColor();
	}

	public Brush getActiveBrush() {
		return drawingSurface.getActiveBrush();
	}

	public int getPixelFromScreenCoordinates(float x, float y) {
		return drawingSurface.getPixelFromScreenCoordinates(x, y);
	}

	public Point getPixelCoordinates(float x, float y) {
		return drawingSurface.getPixelCoordinates(x, y);
	}

	public BaseSurfaceListener getDrawingSurfaceListener() {
		return drawingSurface.getDrawingSurfaceListener();
	}

	public boolean cacheFilesExist() {
		for (int cachFileCount = 0; cachFileCount < 150; cachFileCount++) {
			File undoBitmap = new File(this.getCacheDir(), String.valueOf(cachFileCount) + ".png");
			if (undoBitmap.exists()) {
				return true;
			}
		}
		return false;
	}

	public Mode getMode() {
		return drawingSurface.getMode();
	}

	public ToolState getToolState() {
		return drawingSurface.getToolState();
	}

	public Point getCenterpoint() {
		return new Point(drawingSurface.getCenter());
	}

	public void loadImage(String path) {
		drawingSurface.clearUndoRedo();
		loadNewImage(path);
	}

	public Point getFloatingBoxCoordinates() {
		return drawingSurface.getFloatingBoxCoordinates();
	}

	public void setFloatingBoxPng(String imageFilePath) {
		importPngToFloatingBox(imageFilePath);
	}

	public Point getFloatingBoxSize() {
		return drawingSurface.getFloatingBoxSize();
	}

	public float getFloatingBoxRotation() {
		return drawingSurface.getFloatingBoxRotation();
	}

}
