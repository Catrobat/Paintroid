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
import android.widget.ImageButton;
import android.widget.TextView;
import at.tugraz.ist.paintroid.ToolMenuActivity.ButtonEnum;
import at.tugraz.ist.paintroid.dialog.DialogAbout;
import at.tugraz.ist.paintroid.dialog.DialogColorPicker;
import at.tugraz.ist.paintroid.dialog.DialogError;
import at.tugraz.ist.paintroid.dialog.DialogHelp;
import at.tugraz.ist.paintroid.dialog.DialogStrokePicker;
import at.tugraz.ist.paintroid.graphic.DrawingSurface;
import at.tugraz.ist.paintroid.graphic.DrawingSurface.ActionType;
import at.tugraz.ist.paintroid.graphic.DrawingSurface.ColorPickupListener;
import at.tugraz.ist.paintroid.graphic.DrawingSurface.Mode;
import at.tugraz.ist.paintroid.graphic.listeners.BaseSurfaceListener;
import at.tugraz.ist.paintroid.graphic.utilities.FloatingBox;
import at.tugraz.ist.paintroid.graphic.utilities.Tool.ToolState;
import at.tugraz.ist.paintroid.helper.FileIO;
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
public class MainActivity extends Activity implements OnClickListener, OnLongClickListener {

	public DrawingSurface drawingSurface;
	ZoomStatus zoomStatus;
	Uri savedFileUri;

	// The toolbar buttons
	Button toolButton;
	TextView attributeButton1;
	TextView attributeButton2;
	Button undoButton;

	final int STDWIDTH = 320;
	final int STDHEIGHT = 480;

	int selectedColor = Color.BLACK;

	int brushStrokeWidth;

	Cap selectedBrushType;
	
	boolean useAntiAliasing = true;

	//request codes
	public final int FILE_IO = 0;
	public final int TOOL_MENU = 1;
	public final int ADD_PNG = 2;

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

		attributeButton1 = (TextView) this.findViewById(R.id.btn_Parameter1);
	    attributeButton1.setOnClickListener(this);
	    attributeButton1.setOnLongClickListener(this);
	    attributeButton1.setBackgroundColor(selectedColor);
		
		// Listeners for the MainActivity buttons
		attributeButton2 = (TextView) this.findViewById(R.id.btn_Parameter2);
		attributeButton2.setOnClickListener(this);
		attributeButton2.setOnLongClickListener(this);
		setStroke(15); // set standard value
		setShape(Cap.ROUND); // set standard value
		
		toolButton = (Button) this.findViewById(R.id.btn_Tool);
		toolButton.setOnClickListener(this);
		toolButton.setOnLongClickListener(this);
		
		undoButton = (Button) this.findViewById(R.id.btn_Undo);
		undoButton.setOnClickListener(this);
		undoButton.setOnLongClickListener(this);
		
		// create a white background for drawing with default dimensions
		Bitmap currentImage = Bitmap.createBitmap(STDWIDTH, STDHEIGHT,
				Bitmap.Config.ARGB_8888);
		Canvas bitmapCanvas = new Canvas();
		bitmapCanvas.setBitmap(currentImage);
		bitmapCanvas.drawColor(Color.WHITE);

		drawingSurface.setBitmap(currentImage);
		
		drawingSurface.setActionType(ActionType.DRAW);
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
			this.finish();
			return true;
			
		case R.id.item_Clear:
			drawingSurface.setBitmap(null);
			return true;
			
		case R.id.item_About: // show the about dialog
			DialogAbout about = new DialogAbout(this);
			about.show();
			return true;
			
		case R.id.item_Reset: // reset zoom and scroll values
			zoomStatus.resetZoomState();
			return true;
			
		case R.id.item_Middlepoint:
			drawingSurface.changeMiddlepointMode();
			return true;
			
		case R.id.item_FloatingBox:
			drawingSurface.changeFloatingBoxMode();
			return true;
		
		case R.id.item_ImportPng:
		  startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), ADD_PNG);
      return true;
			
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		MenuItem middlepoint_item = menu.findItem(R.id.item_Middlepoint);
		if(drawingSurface.getMode() == Mode.MIDDLEPOINT)
		{
			middlepoint_item.setTitle(R.string.middlepoint_save);
		}
		else
		{
			middlepoint_item.setTitle(R.string.middlepoint_define);
		}
		return super.onPrepareOptionsMenu(menu);
	}

	/**
	 * Handle all MainActivity button events
	 * 
	 */
	@Override
	public void onClick(View view) {
		//TODO
		switch (view.getId()) { // toolbar buttons

//		case R.id.ibtn_Scroll:
//			onToolbarItemSelected(ActiveToolbarItem.HAND);
//			break;
//
//		case R.id.ibtn_Zoom:
//			onToolbarItemSelected(ActiveToolbarItem.ZOOM);
//			break;

//		case R.id.ibtn_Choose:
//			onToolbarItemSelected(ActiveToolbarItem.EYEDROPPER);
//			// create new ColorChanged Listener to get this event
//			ColorPickupListener list = new ColorPickupListener() {
//
//				@Override
//				public void colorChanged(int color) {
//					// set selected color when new color picked up
//					selectedColorButton.setBackgroundColor(color);
//					setColor(color);
//				}
//			};
//			// set the created listener
//			drawingSurface.setColorPickupListener(list);
//			break;
//
//		case R.id.ibtn_Action:
//			onToolbarItemSelected(ActiveToolbarItem.MAGICWAND);
//			break;

		case R.id.btn_Undo:
			drawingSurface.undoOneStep();
			break;
//			
//		case R.id.ibtn_Redo:
//			drawingSurface.redoOneStep();
//			break;
//
//		case R.id.ibtn_File:
//			Bitmap currentImage = getCurrentImage();
//			Log.d("PAINTROID", "Current Bitmap: " + currentImage);
//			
//			// set up a new Intent an send the Bitmap to FileActivity
//			Intent intentFile = new Intent(this, FileActivity.class);
//			startActivityForResult(intentFile, FILE_IO);
//			break;

		case R.id.btn_Parameter1:
		  if((drawingSurface.getMode() == Mode.DRAW || drawingSurface.getMode() == Mode.CURSOR) &&
		     (drawingSurface.getActionType() == ActionType.DRAW || drawingSurface.getActionType() == ActionType.MAGIC))
		  {
  			DialogColorPicker.OnColorChangedListener mColor = new DialogColorPicker.OnColorChangedListener() {
  				@Override
  				public void colorChanged(int color) {
  					if (color == Color.TRANSPARENT) {
  						Log.d("PAINTROID", "Transparent set");
  						attributeButton1.setBackgroundColor(color); // R.color.main_background);
  						setColor(color);
  					} else {
  					  attributeButton1.setBackgroundColor(color);
  						setColor(color);
  					}
  				}
  			};
  			DialogColorPicker colorpicker = new DialogColorPicker(this, mColor,
            selectedColor);
        colorpicker.show();
		  } else if(drawingSurface.getActionType() == ActionType.ZOOM)
		  {
			  zoomStatus.resetZoomState();
		  } else if(drawingSurface.getMode() == Mode.FLOATINGBOX)
		  {
			  //Rotate left
			  drawingSurface.rotateFloatingBox(-90);
		  }
			break;

		case R.id.btn_Parameter2: // starting stroke chooser dialog
		  if((drawingSurface.getMode() == Mode.DRAW || drawingSurface.getMode() == Mode.CURSOR) && 
			 (drawingSurface.getActionType() == ActionType.DRAW))
		  {
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
  			
  			DialogStrokePicker strokepicker = new DialogStrokePicker(this,
  					mStroke);
  			strokepicker.show();
		  } else if(drawingSurface.getMode() == Mode.FLOATINGBOX)
		  {
			  //Rotate right
			  drawingSurface.rotateFloatingBox(90);
		  }
			break;
			
		case R.id.btn_Tool:
		  Intent intent = new Intent(this, MenuTabActivity.class);
		  startActivityForResult(intent, TOOL_MENU);
		  
		  overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);     
			break;
		default:
			// set default option
		  drawingSurface.setActionType(ActionType.DRAW);
		}
	}


	/**
	 * LongClick Listener for Help function
	 * 
	 */
	@Override
	public boolean onLongClick(View v) {
		//TODO
		DialogHelp help;
			switch (v.getId()) {
//			case R.id.ibtn_Scroll:
//				help = new DialogHelp(this,R.id.ibtn_Scroll);
//				
//				help.show();
//				break;
//
//			case R.id.ibtn_Zoom:
//				
//				help = new DialogHelp(this,R.id.ibtn_Zoom);
//				help.show();
//				break;

//			case R.id.btn_Tool:
//				help = new DialogHelp(this,R.id.btn_Tool);
//				help.show();
//				break;

//			case R.id.ibtn_Choose:
//				help = new DialogHelp(this,R.id.ibtn_Choose);
//				help.show();
//				break;
//
//			case R.id.ibtn_Action:
//				help = new DialogHelp(this,R.id.ibtn_Action);
//				help.show();
//				break;

			case R.id.btn_Undo:
				help = new DialogHelp(this,R.id.btn_Undo);
				help.show();
				break;
				
//			case R.id.ibtn_Redo:
//				help = new DialogHelp(this,R.id.ibtn_Redo);
//				help.show();
//				break;
//
//			case R.id.ibtn_File:
//				help = new DialogHelp(this,R.id.ibtn_File);
//				help.show();
//				break;

//			case R.id.btn_Color: 
//				help = new DialogHelp(this,R.id.btn_Color);
//				help.show();
//				break;
//
//			case R.id.ibtn_Stroke:
//				help = new DialogHelp(this,R.id.ibtn_Stroke);
//				help.show();
//				break;
			default:
				break;
			}
			
			return true;
	}

	/**
	 * Listener for ACTIVITY RESULTS (Intent)
	 */
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
			drawingSurface.setActionType(ActionType.DRAW);
			zoomStatus.resetZoomState();
		} else if (requestCode == TOOL_MENU && resultCode == Activity.RESULT_OK) {
		  int selectedToolButtonId = data.getIntExtra("SelectedTool", -1);
		  if(ButtonEnum.values().length > selectedToolButtonId && selectedToolButtonId > -1)
		  {
		    ButtonEnum selectedTool = ButtonEnum.values()[selectedToolButtonId];
		    switch(selectedTool)
		    {
		    case BRUSH:
		      resetAttributeButtons();
		      toolButton.setBackgroundResource(R.drawable.brush64);
		      attributeButton1.setVisibility(View.VISIBLE);
		      attributeButton1.setBackgroundColor(selectedColor);
		      attributeButton2.setVisibility(View.VISIBLE);
		      setAttributeButton2BackgroundForStrokeShape();
		      drawingSurface.setActionType(ActionType.DRAW);
		      break;
		    case CURSOR:
		      resetAttributeButtons();
		      toolButton.setBackgroundResource(R.drawable.cursor64);
		      attributeButton1.setVisibility(View.VISIBLE);
		      attributeButton1.setBackgroundColor(selectedColor);
		      attributeButton2.setVisibility(View.VISIBLE);
		      setAttributeButton2BackgroundForStrokeShape();
		      drawingSurface.activateCursor();
		      break;
		    case SCROLL:
		      resetAttributeButtons();
		      toolButton.setBackgroundResource(R.drawable.scroll64);
		      drawingSurface.setActionType(ActionType.SCROLL);
		      break;
		    case ZOOM:
		      resetAttributeButtons();
		      toolButton.setBackgroundResource(R.drawable.zoom64);
		      attributeButton1.setVisibility(View.VISIBLE);
		      attributeButton1.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.zoom48, 0, 0);
		      attributeButton1.setText(R.string.button_reset_zoom);
		      drawingSurface.setActionType(ActionType.ZOOM);
		      break;
		    case PIPETTE:
		      resetAttributeButtons();
		      toolButton.setBackgroundResource(R.drawable.pipette64);
		      attributeButton1.setVisibility(View.VISIBLE);
		      attributeButton1.setBackgroundColor(selectedColor);
		      drawingSurface.setActionType(ActionType.PIPETTE);
		      ColorPickupListener list = new ColorPickupListener() {
            
            @Override
            public void colorChanged(int color) {
              // set selected color when new color picked up
              attributeButton1.setBackgroundColor(color);
              setColor(color);
            }
          };
          // set the created listener
          drawingSurface.setColorPickupListener(list);
          break;
		    case MAGIC:
		      resetAttributeButtons();
		      toolButton.setBackgroundResource(R.drawable.magic64);
		      drawingSurface.setActionType(ActionType.MAGIC);		      
		      attributeButton1.setVisibility(View.VISIBLE);
		      attributeButton1.setBackgroundColor(selectedColor);
     	      break;
		    case UNDO:
		      drawingSurface.undoOneStep();
		      break;
		    case REDO:
		      drawingSurface.redoOneStep();
		      break;
		    case MIDDLEPOINT:
		      resetAttributeButtons();
		      toolButton.setBackgroundResource(R.drawable.middlepoint64);
		      drawingSurface.changeMiddlepointMode();
		      break;
		    case FLOATINGBOX:
		      resetAttributeButtons();
		      toolButton.setBackgroundResource(R.drawable.middlepoint64);
		      attributeButton1.setVisibility(View.VISIBLE);
		      attributeButton1.setBackgroundResource(R.drawable.rotate_left_64);
		      attributeButton2.setVisibility(View.VISIBLE);
		      attributeButton2.setBackgroundResource(R.drawable.rotate_right_64);
		      drawingSurface.changeFloatingBoxMode();
		      break;
		    case IMPORTPNG:
		      resetAttributeButtons();
		      toolButton.setBackgroundResource(R.drawable.middlepoint64);
		      startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), ADD_PNG);
		      break;
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
	 * Resets the attribute buttons to empty hidden text views
	 */
	protected void resetAttributeButtons()
	{
		attributeButton1.setVisibility(View.INVISIBLE);
		attributeButton1.setBackgroundColor(Color.TRANSPARENT);
	    attributeButton1.setBackgroundResource(0);
	    attributeButton1.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
	    attributeButton1.setText("");
	    attributeButton2.setVisibility(View.INVISIBLE);
	    attributeButton2.setBackgroundColor(Color.TRANSPARENT);
	    attributeButton2.setBackgroundResource(0);
	    attributeButton2.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
	    attributeButton2.setText("");
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
	}

	/**
	 * Sets Current Shape width in DrawSurface and changes iBtn image
	 * 
	 * @param type to set
	 */
	public void setShape(Cap type) {
	  
	  if(drawingSurface.getActionType() != ActionType.DRAW)
	  {
	    return;
	  }

		selectedBrushType = type;
		drawingSurface.setShape(selectedBrushType);

		setAttributeButton2BackgroundForStrokeShape();
	}
	
	public void setAttributeButton2BackgroundForStrokeShape()
	{
	  switch (selectedBrushType) {
    case SQUARE:
      switch (brushStrokeWidth) {

      case 1:
        attributeButton2.setBackgroundResource(R.drawable.rect_1_32);
        break;
      case 5:
        attributeButton2.setBackgroundResource(R.drawable.rect_2_32);
        break;
      case 15:
        attributeButton2.setBackgroundResource(R.drawable.rect_3_32);
        break;
      case 25:
        attributeButton2.setBackgroundResource(R.drawable.rect_4_32);
        break;
      }
      break;
    case ROUND:
      switch (brushStrokeWidth) {

      case 1:
        attributeButton2.setBackgroundResource(R.drawable.circle_1_32);
        break;
      case 5:
        attributeButton2.setBackgroundResource(R.drawable.circle_2_32);
        break;
      case 15:
        attributeButton2.setBackgroundResource(R.drawable.circle_3_32);
        break;
      case 25:
        attributeButton2.setBackgroundResource(R.drawable.circle_4_32);
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

	public Object getButtonBackground(int ButtonID) {
		switch(ButtonID){
			case 1:
				return toolButton.getContext();
			case 2:
				return attributeButton1.getContext();
			case 3:
				return attributeButton2.getContext();		
			case 4:
				return undoButton.getContext();
			default:
				return null;	
		}
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
		return String.valueOf(selectedColor);
	}
	
	public int getCurrentBrushWidth() {
		return brushStrokeWidth;
	}
	
	public Cap getCurrentBrush() {
		return selectedBrushType;
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
	
	public Mode getMode()
	{
		return drawingSurface.getMode();
	}

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
