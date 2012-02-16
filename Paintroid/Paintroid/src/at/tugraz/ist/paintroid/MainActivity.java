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
import at.tugraz.ist.paintroid.FileActivity.RETURN_VALUE;
import at.tugraz.ist.paintroid.commandmanagement.implementation.CommandHandlerImplementation;
import at.tugraz.ist.paintroid.dialog.DialogAbout;
import at.tugraz.ist.paintroid.dialog.DialogError;
import at.tugraz.ist.paintroid.listener.DrawingSurfaceListener;
import at.tugraz.ist.paintroid.tools.Tool;
import at.tugraz.ist.paintroid.ui.DrawingSurface;
import at.tugraz.ist.paintroid.ui.Perspective;
import at.tugraz.ist.paintroid.ui.Toolbar;
import at.tugraz.ist.paintroid.ui.implementation.DrawingSurfacePerspective;
import at.tugraz.ist.paintroid.ui.implementation.DrawingSurfaceView;
import at.tugraz.ist.paintroid.ui.implementation.ToolbarImplementation;

public class MainActivity extends Activity {
	public static final int REQ_TOOL_MENU = 0;
	public static final int REQ_IMPORTPNG = 1;

	public enum ToolType {
		ZOOM, SCROLL, PIPETTE, BRUSH, UNDO, REDO, NONE, MAGIC, RESET, STAMP, CURSOR, IMPORTPNG
	}

	private DrawingSurface drawingSurface;
	private Perspective drawingSurfacePerspective;
	private DrawingSurfaceListener drawingSurfaceListener;

	private Uri savedFileUri;
	private boolean showMenu = true;

	private boolean openedWithCatroid;

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
		drawingSurface.setPerspective(drawingSurfacePerspective);

		Display display = getWindowManager().getDefaultDisplay();
		int width = display.getWidth();
		int height = display.getHeight();
		Bitmap bitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		drawingSurface.setBitmap(bitmap);

		PaintroidApplication.COMMAND_HANDLER = new CommandHandlerImplementation();
		PaintroidApplication.CURRENT_TOOL = toolbar.getCurrentTool();

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
			case R.id.item_Quit:
				showSecurityQuestionBeforeExit();
				return true;
			case R.id.item_About:
				DialogAbout about = new DialogAbout(this);
				about.show();
				return true;
			case R.id.item_HideMenu:
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
			RelativeLayout toolbarLayout = (RelativeLayout) findViewById(R.id.BottomRelativeLayout);
			toolbarLayout.setVisibility(View.VISIBLE);
			showMenu = true;
			return false;
		}
		return super.onPrepareOptionsMenu(menu);
	}

	public void callToolMenu() {
		Intent intent = new Intent(this, MenuTabActivity.class);
		startActivityForResult(intent, REQ_TOOL_MENU);

		overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
	}

	public void callImportPng() {
		startActivityForResult(new Intent(Intent.ACTION_PICK,
				android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), REQ_IMPORTPNG);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == REQ_TOOL_MENU && resultCode == Activity.RESULT_OK) {
			int selectedToolButtonId = data.getIntExtra(ToolMenuActivity.EXTRA_SELECTED_TOOL, -1);
			if (selectedToolButtonId != -1) {
				if (ToolType.values().length > selectedToolButtonId && selectedToolButtonId > -1) {
					ToolType tooltype = ToolType.values()[selectedToolButtonId];
					Paint tempPaint = new Paint(PaintroidApplication.CURRENT_TOOL.getDrawPaint());
					Tool tool = Utils.createTool(tooltype, this);
					toolbar.setTool(tool);
					PaintroidApplication.CURRENT_TOOL = tool;
					PaintroidApplication.CURRENT_TOOL.setDrawPaint(tempPaint);
				}
			} else {
				switch ((RETURN_VALUE) data.getSerializableExtra(FileActivity.RET_VALUE)) {
					case LOAD:
						final Uri fileUri = data.getParcelableExtra(FileActivity.RET_URI);

						if (fileUri == null || fileUri.toString().length() < 1) {
							Log.e(PaintroidApplication.TAG, "BAD URI: cannot load image");
						} else {
							// FIXME Loading a mutable (!) bitmap from the gallery should be easier *sigh* ...
							// Utils.createFilePathFromUri does not work with all kinds of Uris.
							// Utils.decodeFile is necessary to load even large images as mutable bitmaps without
							// running out of memory.
							Log.d(PaintroidApplication.TAG, "Load Uri " + fileUri); // TODO remove logging

							String filepath = Utils.createFilePathFromUri(this, fileUri);

							if (filepath == null || filepath.length() < 1) {
								Log.e("PAINTROID", "BAD URI " + fileUri);
							} else {
								final File imageFile = new File(filepath);

								String loadMessge = getResources().getString(R.string.dialog_load);
								final ProgressDialog load = ProgressDialog
										.show(MainActivity.this, "", loadMessge, true);

								Thread thread = new Thread() {
									@Override
									public void run() {
										Bitmap bitmap = Utils.decodeFile(MainActivity.this, imageFile);
										if (bitmap != null) {
											drawingSurface.setBitmap(bitmap);
										} else {
											Log.e("PAINTROID", "BAD URI " + fileUri);
										}
										load.dismiss();
									}
								};

								thread.start();
							}
						}
						break;
					case NEW:
						drawingSurfacePerspective.resetScaleAndTranslation();
						drawingSurface.clearBitmap();
						break;
					case SAVE:
						String filename = data.getStringExtra(FileActivity.RET_FILENAME);
						if (FileIO.saveBitmap(MainActivity.this, drawingSurface.getBitmap(), filename) == null) {
							DialogError d = new DialogError(this, R.string.dialog_error_sdcard_title,
									R.string.dialog_error_sdcard_text);
							d.show();
						}
						break;
				}
			}
		} else if (requestCode == REQ_IMPORTPNG && resultCode == Activity.RESULT_OK) {
			Uri selectedGalleryImage = data.getData();
			String imageFilePath = at.tugraz.ist.paintroid.FileIO.getRealPathFromURI(this, selectedGalleryImage);
			importPngToFloatingBox(imageFilePath);
		}
	}

	protected void importPngToFloatingBox(String uriString) {
		// TODO
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
