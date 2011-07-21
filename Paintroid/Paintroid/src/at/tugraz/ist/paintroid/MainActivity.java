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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

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
import at.tugraz.ist.paintroid.dialog.DialogError;
import at.tugraz.ist.paintroid.dialog.DialogHelp;
import at.tugraz.ist.paintroid.dialog.DialogStrokePicker;
import at.tugraz.ist.paintroid.dialog.DialogWarning;
import at.tugraz.ist.paintroid.dialog.colorpicker.ColorPickerDialog;
import at.tugraz.ist.paintroid.graphic.DrawingSurface;
import at.tugraz.ist.paintroid.graphic.DrawingSurface.ActionType;
import at.tugraz.ist.paintroid.graphic.DrawingSurface.ColorPickupListener;
import at.tugraz.ist.paintroid.graphic.DrawingSurface.Mode;
import at.tugraz.ist.paintroid.graphic.listeners.BaseSurfaceListener;
import at.tugraz.ist.paintroid.graphic.utilities.Tool.ToolState;

public class MainActivity extends Activity implements OnClickListener, OnLongClickListener {

	public static final int FILE_IO = 0;
	public static final int ADD_PNG = 1;

	DrawingSurface drawingSurface;
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

	private enum ActiveToolbarItem {
		HAND, ZOOM, BRUSH, EYEDROPPER, MAGICWAND, UNDO, REDO
	}

	//	int selectedColor = Color.BLACK;
	int brushStrokeWidth;
	Cap selectedBrushType;

	boolean useAntiAliasing = true;

	/**
	 * Called when the activity is first created
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		DisplayMetrics displayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

		drawingSurface = (DrawingSurface) findViewById(R.id.surfaceview);
		//		drawingSurface.setColor(selectedColor);
		drawingSurface.setAntiAliasing(useAntiAliasing);
		Point screenSize = new Point(displayMetrics.widthPixels, displayMetrics.heightPixels);
		drawingSurface.setScreenSize(screenSize);
		drawingSurface.setCenter(screenSize.x / 2, screenSize.y / 2);

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
		this.setStroke(15);
		this.setShape(Cap.ROUND);

		onToolbarItemSelected(ActiveToolbarItem.BRUSH);
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
				//				zoomStatus.resetZoomState();
				drawingSurface.getZoomStatus().resetZoomState();
				return true;
			case R.id.item_Middlepoint:
				drawingSurface.changeCenterpointMode();
				return true;
			case R.id.item_FloatingBox:
				drawingSurface.toggleFloatingBoxMode();
				return true;
			case R.id.item_ImportPng:
				startActivityForResult(new Intent(Intent.ACTION_PICK,
						android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), ADD_PNG);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * Handle all MainActivity button events
	 */
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.ibtn_handTool:
				onToolbarItemSelected(ActiveToolbarItem.HAND);
				break;
			case R.id.ibtn_zoomTool:
				onToolbarItemSelected(ActiveToolbarItem.ZOOM);
				break;
			case R.id.ibtn_brushTool:
				onToolbarItemSelected(ActiveToolbarItem.BRUSH);
				break;
			case R.id.ibtn_eyeDropperTool:
				onToolbarItemSelected(ActiveToolbarItem.EYEDROPPER);
				// create new ColorChanged Listener to get this event
				ColorPickupListener list = new ColorPickupListener() {

					@Override
					public void colorChanged(int color) {
						// set selected color when new color picked up
						if (color == Color.TRANSPARENT) {
							colorPickerButton.setBackgroundResource(R.drawable.transparentrepeat);
						} else {
							colorPickerButton.setBackgroundColor(color);
						}
						setColor(color);
					}
				};
				// set the created listener
				drawingSurface.setColorPickupListener(list);
				break;

			case R.id.ibtn_magicWandTool:
				onToolbarItemSelected(ActiveToolbarItem.MAGICWAND);
				break;

			case R.id.ibtn_undoTool:
				drawingSurface.undoOneStep();
				break;

			case R.id.ibtn_redoTool:
				drawingSurface.redoOneStep();
				break;

			case R.id.ibtn_fileActivity:
				Bitmap currentImage = getCurrentImage();
				Log.d("PAINTROID", "Current Bitmap: " + currentImage);

				// set up a new Intent an send the Bitmap to FileActivity
				Intent intentFile = new Intent(this, FileActivity.class);
				startActivityForResult(intentFile, FILE_IO);
				break;

			case R.id.btn_Color: // color chooser dialog
				ColorPickerDialog.OnColorChangedListener mColor = new ColorPickerDialog.OnColorChangedListener() {
					@Override
					public void colorChanged(int color) {
						if (color == Color.TRANSPARENT) {
							colorPickerButton.setBackgroundResource(R.drawable.transparentrepeat);
							setColor(color);
						} else {
							colorPickerButton.setBackgroundColor(color);
							setColor(color);
						}
					}
				};

				ColorPickerDialog colorpicker = new ColorPickerDialog(this, mColor, drawingSurface.getActiveColor());
				colorpicker.show();
				break;

			case R.id.ibtn_brushStroke: // starting stroke chooser dialog

				DialogStrokePicker.OnStrokeChangedListener mStroke = new DialogStrokePicker.OnStrokeChangedListener() {

					@Override
					public void strokeChanged(int stroke) {
						setStroke(stroke);
						setShape(selectedBrushType);
					}

					@Override
					public void strokeShape(Cap type) {
						setShape(type);
					}
				};

				DialogStrokePicker strokepicker = new DialogStrokePicker(this, mStroke);
				strokepicker.show();
				break;

			default:
				// set default option to zoom
				onToolbarItemSelected(ActiveToolbarItem.HAND);
		}
	}

	/**
	 * LongClick Listener for Help function
	 * 
	 */
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
				break;
		}

		return true;
	}

	private void onToolbarItemSelected(ActiveToolbarItem active) {

		// unselect all buttons
		eyeDropperToolButton.setBackgroundResource(R.drawable.pipette32);
		brushToolButton.setBackgroundResource(R.drawable.draw32);
		handToolButton.setBackgroundResource(R.drawable.choose32);
		magicWandToolButton.setBackgroundResource(R.drawable.action32);
		fileActivityButton.setBackgroundResource(R.drawable.file32);
		zoomToolButton.setBackgroundResource(R.drawable.magnifying_glass32);

		switch (active) {
			case ZOOM:
				zoomToolButton.setBackgroundResource(R.drawable.zoom32_active);
				drawingSurface.setActionType(ActionType.ZOOM);
				break;
			case HAND:
				handToolButton.setBackgroundResource(R.drawable.choose32_active);
				drawingSurface.setActionType(ActionType.SCROLL);
				break;
			case BRUSH:
				if (getCurrentImage() == null) {
					DialogWarning warning = new DialogWarning(this);
					warning.show();
				} else {
					brushToolButton.setBackgroundResource(R.drawable.draw32_active);
					drawingSurface.setActionType(ActionType.DRAW);
				}
				break;
			case EYEDROPPER:
				eyeDropperToolButton.setBackgroundResource(R.drawable.pipette32_active);
				drawingSurface.setActionType(ActionType.CHOOSE);
				break;
			case MAGICWAND:
				if (getCurrentImage() == null) {
					DialogWarning warning = new DialogWarning(this);
					warning.show();
				} else {
					magicWandToolButton.setBackgroundResource(R.drawable.action32_active);
					drawingSurface.setActionType(ActionType.MAGIC);
				}
				break;
			case UNDO:
				break;
			case REDO:
				break;
			default:
				handToolButton.setBackgroundResource(R.drawable.choose32_active);
				drawingSurface.setActionType(ActionType.SCROLL);
				break;
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		// get the URI from FileIO Intent and set in DrawSurface
		if (requestCode == FILE_IO && resultCode == Activity.RESULT_OK) {

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
			onToolbarItemSelected(ActiveToolbarItem.HAND);
			drawingSurface.getZoomStatus().resetZoomState();
		} else if (requestCode == ADD_PNG && resultCode == Activity.RESULT_OK) {
			Uri selectedGalleryImage = data.getData();
			//Convert the Android URI to a real path
			String imageFilePath = FileIO.getRealPathFromURI(getContentResolver(), selectedGalleryImage);
			importPngToFloatingBox(imageFilePath);
		}
	}

	/**
	 * This method is called by onActivityResult to load
	 * a new image which is defined by its Uri.
	 * 
	 * @param uriString
	 *            Identifier of the image.
	 */
	void loadNewImage(String uriString) {

		Bitmap currentImage = createBitmapFromUri(uriString);

		// alpha transparency does not work with photos if this code is used
		// instead
		// currentImage = BitmapFactory.decodeFile(uriString,
		// options).copy(Bitmap.Config.ARGB_8888, true);

		// Robotium hack, because only mainActivity threads are allowed to call this function
		if (!Thread.currentThread().getName().equalsIgnoreCase("Instr: android.test.InstrumentationTestRunner")) {
			drawingSurface.setBitmap(currentImage);
		}

		// read xml file
		try {
			if (!uriString.endsWith(".png")) {
				return;
			}
			String xmlUriString = uriString.substring(0, uriString.length() - 3) + "xml";
			File xmlMetafile = new File(xmlUriString);
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder;
			documentBuilder = documentBuilderFactory.newDocumentBuilder();
			Document xmlDocument = documentBuilder.parse(xmlMetafile);
			xmlDocument.getDocumentElement().normalize();
			NodeList centerNode = xmlDocument.getElementsByTagName("center");
			if (centerNode.getLength() != 1) {
				return;
			}
			NamedNodeMap attributes = centerNode.item(0).getAttributes();
			int x = Integer.parseInt(attributes.getNamedItem("position-x").getNodeValue());
			int y = Integer.parseInt(attributes.getNamedItem("position-y").getNodeValue());
			drawingSurface.setCenter(x, y);
		} catch (Exception e) {

		}

	}

	/**
	 * Loads a image put it into the floating box
	 * 
	 * @param uriString
	 *            uri from the image to import
	 */
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

	@Override
	protected void onDestroy() {
		drawingSurface.setOnTouchListener(null);
		drawingSurface.getZoomStatus().deleteObservers();

		// Deletes the undo and redo cached pictures
		deleteCacheFiles();

		super.onDestroy();
	}

	/**
	 * Sets current color in DrawSurface
	 * 
	 * @param color
	 *            to set
	 */
	public void setColor(int color) {
		drawingSurface.setColor(color);
	}

	/**
	 * Sets Current stroke width in DrawSurface
	 * 
	 * @param stroke
	 *            to set
	 */
	public void setStroke(int stroke) {
		brushStrokeWidth = stroke; // Save stroke width in value
		drawingSurface.setStroke(brushStrokeWidth);
	}

	/**
	 * Sets Current Shape width in DrawSurface and changes iBtn image
	 * 
	 * @param type
	 *            to set
	 */
	public void setShape(Cap type) {

		selectedBrushType = type;
		drawingSurface.setShape(selectedBrushType);

		switch (selectedBrushType) {
			case SQUARE:
				switch (brushStrokeWidth) {

					case 1:
						brushStrokeButton.setBackgroundResource(R.drawable.rect_1_32);
						break;
					case 5:
						brushStrokeButton.setBackgroundResource(R.drawable.rect_2_32);
						break;
					case 15:
						brushStrokeButton.setBackgroundResource(R.drawable.rect_3_32);
						break;
					case 25:
						brushStrokeButton.setBackgroundResource(R.drawable.rect_4_32);
						break;
				}
				break;
			case ROUND:
				switch (brushStrokeWidth) {

					case 1:
						brushStrokeButton.setBackgroundResource(R.drawable.circle_1_32);
						break;
					case 5:
						brushStrokeButton.setBackgroundResource(R.drawable.circle_2_32);
						break;
					case 15:
						brushStrokeButton.setBackgroundResource(R.drawable.circle_3_32);
						break;
					case 25:
						brushStrokeButton.setBackgroundResource(R.drawable.circle_4_32);
						break;
				}
				break;
			default:
				break;
		}
	}

	/**
	 * Deletes the cache files created by the undo redo object
	 * (public because of Robotium)
	 */
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
	public void setAntiAliasing(boolean antiAliasingFlag) {
		useAntiAliasing = antiAliasingFlag;
		drawingSurface.setAntiAliasing(antiAliasingFlag);
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

	public int getCurrentBrushWidth() {
		return brushStrokeWidth;
	}

	public Cap getCurrentBrush() {
		return selectedBrushType;
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
