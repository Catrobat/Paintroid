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
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import at.tugraz.ist.paintroid.dialog.DialogAbout;
import at.tugraz.ist.paintroid.dialog.DialogColorPicker;
import at.tugraz.ist.paintroid.dialog.DialogError;
import at.tugraz.ist.paintroid.dialog.DialogHelp;
import at.tugraz.ist.paintroid.dialog.DialogStrokePicker;
import at.tugraz.ist.paintroid.graphic.DrawingSurface;
import at.tugraz.ist.paintroid.graphic.DrawingSurface.ColorPickupListener;
import at.tugraz.ist.paintroid.graphic.listeners.BaseSurfaceListener;
import at.tugraz.ist.paintroid.graphic.utilities.Tool.ToolState;
import at.tugraz.ist.paintroid.helper.FileIO;
import at.tugraz.ist.paintroid.helper.Toolbar;
import at.tugraz.ist.zoomscroll.ZoomStatus;

/**
 * This is the main activity of the program. I contains the drawing surface
 * where the user can modify images. The activity also provides GUI elements
 * like the toolbar and the color picker.
 * 
 * Status: refactored 20.02.2011
 * @author PaintroidTeam
 * @version 0.6.4b
 */
public class MainActivity extends Activity {
	
	public enum ToolType {
		ZOOM, SCROLL, PIPETTE, BRUSH, UNDO, REDO, NONE, MAGIC, RESET, MIDDLEPOINT, FLOATINGBOX, CURSOR, IMPORTPNG
	}

	public DrawingSurface drawingSurface;
	protected ZoomStatus zoomStatus;
	protected Uri savedFileUri;

	// The toolbar buttons
	protected Toolbar toolbar;

	protected final int STDWIDTH = 320;
	protected final int STDHEIGHT = 480;

	protected int selectedColor = Color.BLACK;

	protected int brushStrokeWidth;

	protected Cap selectedBrushType;
	
	protected boolean useAntiAliasing = true;
	protected boolean showMenu = true;

	//request codes
	public final int TOOL_MENU = 0;
	public final int ADD_PNG = 1;

	/**
	 * Called when the activity is first created
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		// Initializations for the DrawingSurface
		
		zoomStatus = new ZoomStatus();
		// load the DrawingSurface from the resources
		drawingSurface = (DrawingSurface) findViewById(R.id.surfaceview);
		drawingSurface.setZoomStatus(zoomStatus);
		// drawingSurface.setBackgroundColor(Color.MAGENTA);
		drawingSurface.setBackgroundResource(R.drawable.background);
		drawingSurface.setColor(selectedColor);
		drawingSurface.setAntiAliasing(useAntiAliasing);
		Point screenSize = new Point(metrics.widthPixels, metrics.heightPixels);
		drawingSurface.setScreenSize(screenSize);
		drawingSurface.setMiddlepoint(screenSize.x/2, screenSize.y/2);
		zoomStatus.resetZoomState();
		
		toolbar = new Toolbar(this);
		
		setStroke(15); // set standard value
		setShape(Cap.ROUND); // set standard value
		
		drawingSurface.setToolbar(toolbar);
		
		// create a white background for drawing with default dimensions
		Bitmap currentImage = Bitmap.createBitmap(STDWIDTH, STDHEIGHT,
				Bitmap.Config.ARGB_8888);
		Canvas bitmapCanvas = new Canvas();
		bitmapCanvas.setBitmap(currentImage);
		bitmapCanvas.drawColor(Color.WHITE);

		drawingSurface.setBitmap(currentImage);
		
		drawingSurface.setToolType(ToolType.BRUSH);
	}

	/**
	 * Set buttons of the options menu. The menu layout is loaded from the
	 * resources.
	 */
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
			if(showMenu)
			{
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
	public boolean onPrepareOptionsMenu(Menu menu)
	{
		MenuItem hideButton = menu.findItem(R.id.item_HideMenu);
		if(showMenu)
	    {
			hideButton.setTitle(R.string.hide_menu);
	    }
		else {
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
		startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), ADD_PNG);
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
		  if(selectedToolButtonId != -1)
		  {
			  if(ToolType.values().length > selectedToolButtonId && selectedToolButtonId > -1)
			  {
			    ToolType selectedTool = ToolType.values()[selectedToolButtonId];
			    toolbar.setTool(selectedTool);
			  }
		  } else {
			String uriString = data.getStringExtra("UriString");
			String ReturnValue = data.getStringExtra("IntentReturnValue");

			if (ReturnValue.contentEquals("LOAD") && uriString != null) {

				Log.d("PAINTROID", "Main: Uri " + uriString);
				drawingSurface.clearUndoRedo();
				drawingSurface.setToolType(ToolType.BRUSH);
				zoomStatus.resetZoomState();
				loadNewImage(uriString);
			}
			
			if (ReturnValue.contentEquals("NEW")) {

				// create a white background for drawing with default dimensions
				Bitmap currentImage = Bitmap.createBitmap(STDWIDTH, STDHEIGHT,
						Bitmap.Config.ARGB_8888);
				Canvas bitmapCanvas = new Canvas();
				bitmapCanvas.setBitmap(currentImage);
				bitmapCanvas.drawColor(Color.WHITE);
				drawingSurface.clearUndoRedo();
				drawingSurface.setBitmap(currentImage);
				DisplayMetrics metrics = new DisplayMetrics();
				getWindowManager().getDefaultDisplay().getMetrics(metrics);
				Point screenSize = new Point(metrics.widthPixels, metrics.heightPixels);
				drawingSurface.setMiddlepoint(screenSize.x/2, screenSize.y/2);
				drawingSurface.setToolType(ToolType.BRUSH);
				zoomStatus.resetZoomState();
			}
			if (ReturnValue.contentEquals("SAVE")) {
				Log.d("PAINTROID", "Main: Get FileActivity return value: "
						+ ReturnValue);
				savedFileUri = new FileIO(this).saveBitmapToSDCard(
						getContentResolver(), uriString, getCurrentImage(), drawingSurface.getMiddlepoint());
				if (savedFileUri == null) {
					DialogError error = new DialogError(this, R.string.dialog_error_sdcard_title, R.string.dialog_error_sdcard_text);
					error.show();
				}
			}
		  }
		} else if(requestCode == ADD_PNG && resultCode == Activity.RESULT_OK) {
		   Uri selectedGalleryImage = data.getData();
      //Convert the Android URI to a real path
      String imageFilePath =  FileIO.getRealPathFromURI(getContentResolver(), selectedGalleryImage);
		  importPngToFloatingBox(imageFilePath);
		}
	}

	/**
	 * This method is called by onActivityResult to load
	 * a new image which is defined by its Uri.
	 * 
	 * @param uriString Identifier of the image.
	 */
	void loadNewImage(String uriString) {

	  Bitmap currentImage = createBitmapFromUri(uriString);
		
		// alpha transparency does not work with photos if this code is used
		// instead
		// currentImage = BitmapFactory.decodeFile(uriString,
		// options).copy(Bitmap.Config.ARGB_8888, true);

		// Robotium hack, because only mainActivity threads are allowed to call this function
		if(!Thread.currentThread().getName().equalsIgnoreCase("Instr: android.test.InstrumentationTestRunner"))
		{
			drawingSurface.setBitmap(currentImage);
		}
		
		// read xml file
		try {
			if(!uriString.endsWith(".png"))
			{
				return;
			}
			String xmlUriString = uriString.substring(0, uriString.length()-3)+"xml";
			File xmlMetafile = new File(xmlUriString);
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder;
			documentBuilder = documentBuilderFactory.newDocumentBuilder();
			Document xmlDocument = documentBuilder.parse(xmlMetafile);
			xmlDocument.getDocumentElement().normalize();
			NodeList middlepointNode = xmlDocument.getElementsByTagName("middlepoint");
			if(middlepointNode.getLength() != 1)
			{
				return;
			}
			NamedNodeMap attributes = middlepointNode.item(0).getAttributes();
			int x = Integer.parseInt(attributes.getNamedItem("position-x").getNodeValue());
			int y = Integer.parseInt(attributes.getNamedItem("position-y").getNodeValue());
			drawingSurface.setMiddlepoint(x, y);
		} catch (Exception e) {
			
		}
		
	}
	
	/**
	 * Loads a image put it into the floating box
	 * 
	 * @param uriString uri from the image to import
	 */
	protected void importPngToFloatingBox(String uriString)
	{
	  Bitmap newPng = createBitmapFromUri(uriString);
	  if(newPng == null)
	  {
	    return;
	  }
	  
	  drawingSurface.addPng(newPng);
	}
	
	protected Bitmap createBitmapFromUri(String uriString)
	{
	  // First we query the bitmap for dimensions without
    // allocating memory for its pixels.
    BitmapFactory.Options options = new BitmapFactory.Options();
    options.inJustDecodeBounds = true;
    File bitmapFile = new File(uriString);
    if(!bitmapFile.exists())
    {
      return null;
    }
    BitmapFactory.decodeFile(uriString, options);

    int width = options.outWidth;
    int height = options.outHeight;
    
    if(width < 0 || height < 0)
    {
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

    Bitmap bitmap = Bitmap.createBitmap(width, height,
        Bitmap.Config.ARGB_8888);
    
    // we have to load each pixel for alpha transparency to work with photos
    int[] pixels = new int[width * height];
    BitmapFactory.decodeFile(uriString, options).getPixels(pixels, 0,
        width, 0, 0, width, height);

    bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
    return bitmap;
	}
	
	@Override
	public void onBackPressed() {
	    showSecurityQuestionBeforeExit();
	}
	
	protected void showSecurityQuestionBeforeExit()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.closing_security_question)
		       .setCancelable(false)
		       .setPositiveButton(R.string.closing_security_question_yes, new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		               MainActivity.this.finish();
		           }
		       })
		       .setNegativeButton(R.string.closing_security_question_not, new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                dialog.cancel();
		           }
		       });
		AlertDialog alert = builder.create();
		alert.show();
		return;
	}

	@Override
	protected void onDestroy() {
		
		drawingSurface.setOnTouchListener(null);
		zoomStatus.deleteObservers();
		
		// Deletes the undo and redo cached pictures
		deleteCacheFiles();
		
		super.onDestroy();
	}

	/**
	 * Sets current color in DrawSurface
	 * 
	 * @param color to set
	 */
	public void setColor(int color) {
		selectedColor = color; // Save color in value
		drawingSurface.setColor(selectedColor);
	}

	/**
	 * Sets Current stroke width in DrawSurface
	 * 
	 * @param stroke to set
	 */
	public void setStroke(int stroke) {
		brushStrokeWidth = stroke; // Save stroke width in value
		drawingSurface.setStroke(brushStrokeWidth);
		toolbar.setStrokeAndShape(brushStrokeWidth, selectedBrushType);
	}

	/**
	 * Sets Current Shape width in DrawSurface and changes iBtn image
	 * 
	 * @param type to set
	 */
	public void setShape(Cap type) {
	  
	  if(drawingSurface.getToolType() != ToolType.BRUSH)
	  {
	    return;
	  }
		selectedBrushType = type;
		drawingSurface.setShape(selectedBrushType);
		toolbar.setStrokeAndShape(brushStrokeWidth, selectedBrushType);
	}
	
	/**
	 * Deletes the cache files created by the undo redo object
	 * (public because of Robotium)
	 */
	public void deleteCacheFiles()
	{
		// Deletes the undo and redo cached pictures
		int undoBitmapCount = 0;
		File undoBitmap = null;
		do
		{
			if(undoBitmap != null && undoBitmap.exists())
			{
				undoBitmap.delete();
			}
			undoBitmap = new File(this.getCacheDir(), String.valueOf(undoBitmapCount) + ".png");
			undoBitmapCount++;
		} while(undoBitmap.exists() || undoBitmapCount < 5);
	}
	
	/**
	 * Getter for the selected color
	 * 
	 * @return selected color
	 */
	public int getSelectedColor() {
		return selectedColor;
	}
	
	/**
	 * Getter for the drawing surface
	 * 
	 * @return drawing surface
	 */
	public DrawingSurface getDrawingSurface() {
		return drawingSurface;
	}

	/**
	 * Getter for the zoom status
	 * 
	 * @return zoom status
	 */
	public ZoomStatus getZoomStatus() {
		return zoomStatus;
	}
	
	/**
	 * Getter for the brush width
	 * 
	 * @return brush width
	 */
	public int getCurrentBrushWidth() {
		return brushStrokeWidth;
	}
	
	/**
	 * Getter for the brush type
	 * 
	 * @return brush type
	 */
	public Cap getCurrentBrush() {
		return selectedBrushType;
	}

//------------------------------Methods For JUnit TESTING---------------------------------------
	public void setAntiAliasing(boolean antiAliasingFlag)
	{
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

	public String getZoomLevel() {
		return String.valueOf(zoomStatus.getZoomLevel());
	}
	
	public float getScrollX() {
	  return zoomStatus.getScrollX();
	}
	
	public float getScrollY() {
	  return zoomStatus.getScrollY();
	}

	public String getCurrentSelectedColor() {
		return String.valueOf(getSelectedColor());
	}
	
	public int getPixelFromScreenCoordinates(float x, float y)
	{
		return drawingSurface.getPixelFromScreenCoordinates(x, y);
	}
	
	public Point getPixelCoordinates(float x, float y)
	{
		return drawingSurface.getPixelCoordinates(x, y);
	}
	
	public BaseSurfaceListener getDrawingSurfaceListener() {
		return drawingSurface.getDrawingSurfaceListener();
	}
	
	public boolean cacheFilesExist()
	{
		for (int cachFileCount = 0; cachFileCount < 150; cachFileCount++) {
			File undoBitmap = new File(this.getCacheDir(), String.valueOf(cachFileCount) + ".png");
			if(undoBitmap.exists())
			{
				return true;
			}
		}
		return false;
	}
	
//	public Mode getMode()
//	{
//		return drawingSurface.getMode();
//	}

	public ToolState getToolState()
	{
		return drawingSurface.getToolState();
	}
	
	public Point getMiddlepoint()
	{
		return new Point(drawingSurface.getMiddlepoint());
	}
	
	public void loadImage(String path)
	{
		drawingSurface.clearUndoRedo();
		loadNewImage(path);
	}
	
	public Point getFloatingBoxCoordinates()
	{
		return drawingSurface.getFloatingBoxCoordinates();
	}
	
	public void setFloatingBoxPng(String imageFilePath)
	{
	  importPngToFloatingBox(imageFilePath);
	}
	
	public Point getFloatingBoxSize()
	{
	  return drawingSurface.getFloatingBoxSize();
	}
	
	public float getFloatingBoxRotation()
	{
	  return drawingSurface.getFloatingBoxRotation();
	}

}
