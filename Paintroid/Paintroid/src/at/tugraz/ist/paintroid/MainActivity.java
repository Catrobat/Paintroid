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
import android.graphics.Color;
import android.graphics.Paint.Cap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import at.tugraz.ist.paintroid.dialog.DialogAbout;
import at.tugraz.ist.paintroid.dialog.DialogBrushPicker;
import at.tugraz.ist.paintroid.dialog.DialogError;
import at.tugraz.ist.paintroid.dialog.DialogHelp;
import at.tugraz.ist.paintroid.dialog.DialogWarning;
import at.tugraz.ist.paintroid.dialog.colorpicker.ColorPickerDialog;
import at.tugraz.ist.paintroid.graphic.DrawingSurface;
import at.tugraz.ist.paintroid.graphic.DrawingSurface.ColorPickupListener;
import at.tugraz.ist.paintroid.graphic.DrawingSurface.Mode;
import at.tugraz.ist.paintroid.graphic.utilities.Brush;
import at.tugraz.ist.paintroid.graphic.utilities.DrawFunctions;

public class MainActivity extends Activity {
	static final String TAG = "PAINTROID";

	static final int REQ_FILEACTIVITY = 0;
	static final int REQ_IMPORTPNG = 1;

	DrawingSurface drawingSurface;
	DialogBrushPicker dialogBrushPicker;
	ColorPickerDialog dialogColorPicker;
	Uri savedFileUri;

	// top left buttons
	ToolbarButton colorPickerButton;
	ToolbarButton brushStrokeButton;

	public enum ToolbarItem {
		HAND, ZOOM, BRUSH, EYEDROPPER, MAGICWAND, UNDO, REDO, NONE, RESET
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		drawingSurface = (DrawingSurface) findViewById(R.id.surfaceview);

		colorPickerButton = (ToolbarButton) this.findViewById(R.id.ibtn_Color);
		brushStrokeButton = (ToolbarButton) this.findViewById(R.id.ibtn_brushStroke);

		updateStrokeButtonBackground();
		final ToolbarButton brushToolButton = (ToolbarButton) this.findViewById(R.id.ibtn_brushTool);
		brushToolButton.activate();
		drawingSurface.setActionType(ToolbarItem.BRUSH);
	}

	@Override
	protected void onDestroy() {
		drawingSurface.setOnTouchListener(null);
		drawingSurface.getZoomStatus().deleteObservers();
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
				drawingSurface.clearBitmap();
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

	public void onToolbarClick(View view) {
		final int id = view.getId();
		switch (id) {
			case R.id.ibtn_handTool:
				activateToolbarButton(id);
				drawingSurface.setActionType(ToolbarItem.HAND);
				break;
			case R.id.ibtn_zoomTool:
				activateToolbarButton(id);
				drawingSurface.setActionType(ToolbarItem.ZOOM);
				break;
			case R.id.ibtn_brushTool:
				activateToolbarButton(id);
				drawingSurface.setActionType(ToolbarItem.BRUSH);
				break;
			case R.id.ibtn_eyeDropperTool:
				activateToolbarButton(id);
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
				activateToolbarButton(id);
				if (drawingSurface.getBitmap() == null) {
					DialogWarning warning = new DialogWarning(this);
					warning.show();
				} else {
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
			case R.id.ibtn_Color:
				if (dialogColorPicker == null) {
					ColorPickerDialog.OnColorPickedListener listener = new ColorPickerDialog.OnColorPickedListener() {
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
							updateStrokeButtonBackground();
						}

						@Override
						public void setStroke(int stroke) {
							drawingSurface.setActiveBrush(stroke);
							updateStrokeButtonBackground();
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

	public void onToolbarLongClick(View view) {
		DialogHelp help = new DialogHelp(this, view.getId());
		help.show();
	}

	private void activateToolbarButton(int buttonId) {
		((ToolbarButton) this.findViewById(R.id.ibtn_handTool)).deactivate();
		((ToolbarButton) this.findViewById(R.id.ibtn_zoomTool)).deactivate();
		((ToolbarButton) this.findViewById(R.id.ibtn_brushTool)).deactivate();
		((ToolbarButton) this.findViewById(R.id.ibtn_eyeDropperTool)).deactivate();
		((ToolbarButton) this.findViewById(R.id.ibtn_magicWandTool)).deactivate();
		((ToolbarButton) this.findViewById(buttonId)).activate();
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
				savedFileUri = new FileIO(this).saveBitmapToSDCard(getContentResolver(), uriString,
						drawingSurface.getBitmap(), drawingSurface.getCenter());
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
			drawingSurface.addPng(imageFilePath);
		}
	}

	private void loadNewImage(String uriString) {
		Bitmap currentImage = DrawFunctions.createBitmapFromUri(uriString);
		// Robotium hack, because only mainActivity threads are allowed to call this function
		if (!Thread.currentThread().getName().equalsIgnoreCase("Instr: android.test.InstrumentationTestRunner")) {
			drawingSurface.setBitmap(currentImage);
		}

		// read xml file TODO:
		//        try {
		//                if(!uriString.endsWith(".png"))
		//                {
		//                        return;
		//                }
		//                String xmlUriString = uriString.substring(0, uriString.length()-3)+"xml";
		//                File xmlMetafile = new File(xmlUriString);
		//                DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		//                DocumentBuilder documentBuilder;
		//                documentBuilder = documentBuilderFactory.newDocumentBuilder();
		//                Document xmlDocument = documentBuilder.parse(xmlMetafile);
		//                xmlDocument.getDocumentElement().normalize();
		//                NodeList middlepointNode = xmlDocument.getElementsByTagName("middlepoint");
		//                if(middlepointNode.getLength() != 1)
		//                {
		//                        return;
		//                }
		//                NamedNodeMap attributes = middlepointNode.item(0).getAttributes();
		//                int x = Integer.parseInt(attributes.getNamedItem("position-x").getNodeValue());
		//                int y = Integer.parseInt(attributes.getNamedItem("position-y").getNodeValue());
		//                drawingSurface.setMiddlepoint(x, y);
		//        } catch (Exception e) {
		//                
		//        }
	}

	private void updateStrokeButtonBackground() {
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

	public String getSavedFileUriString() {
		Log.d("PAINTROID-TEST", "SaveString" + savedFileUri.toString());
		return savedFileUri.toString().replace("file://", "");
	}
}
