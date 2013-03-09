/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2012 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid/licenseadditionalterm
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid;

import java.io.File;

import org.catrobat.paintroid.dialog.DialogSaveFile;
import org.catrobat.paintroid.dialog.InfoDialog;
import org.catrobat.paintroid.dialog.InfoDialog.DialogType;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;

public abstract class MenuFileActivity extends SherlockFragmentActivity {

	protected static final int REQ_FILE_MENU = 0;
	protected static final int REQ_IMPORTPNG = 1;
	protected static final int REQ_LOAD_PICTURE = 2;
	protected static final int REQ_FINISH = 3;
	protected static final int REQ_TAKE_PICTURE = 4;
	protected static final int REQ_TOOLS_DIALOG = 5;

	// 50dip in style.xml but need 62 here. must be a 12dip padding somewhere.
	public static final float ACTION_BAR_HEIGHT = 62.0f;

	public static final String RET_ACTION = "RET_ACTION";
	public static final String RET_URI = "RET_URI";
	public static final String RET_FILENAME = "RET_FILENAME";

	public static enum ACTION {
		SAVE, CANCEL
	};

	private static Uri mCameraImageUri;

	// private Intent mResultIntent;

	protected abstract class RunnableWithBitmap {
		public abstract void run(Bitmap bitmap);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.menu_item_save_image:
			final Bundle bundle = new Bundle();
			DialogSaveFile saveDialog = new DialogSaveFile(this, bundle);
			saveDialog.show(getSupportFragmentManager(), "SaveDialogFragment");
			// saveDialog.setOnDismissListener(new OnDismissListener() {
			// @Override
			// public void onDismiss(DialogInterface dialog) {
			// if (bundle.getString(DialogSaveFile.BUNDLE_RET_ACTION)
			// .equals(ACTION.SAVE.toString())) {
			// String saveFileName = bundle
			// .getString(DialogSaveFile.BUNDLE_SAVEFILENAME);
			// saveFile(saveFileName);
			// }
			// }
			// });
			// saveDialog.show();
			break;
		case R.id.menu_item_new_image_from_camera:
			AlertDialog.Builder newCameraImageAlertDialogBuilder = new AlertDialog.Builder(
					this);
			newCameraImageAlertDialogBuilder
					.setMessage(R.string.dialog_warning_new_image)
					.setCancelable(true)
					.setPositiveButton(R.string.yes,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int id) {
									takePhoto();
								}
							})
					.setNegativeButton(R.string.no,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
								}
							});
			AlertDialog alertNewCameraImage = newCameraImageAlertDialogBuilder
					.create();
			alertNewCameraImage.show();

			break;
		case R.id.menu_item_new_image:
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					this);
			alertDialogBuilder
					.setMessage(R.string.dialog_warning_new_image)
					.setCancelable(true)
					.setPositiveButton(R.string.yes,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int id) {
									initialiseNewBitmap();
								}
							})
					.setNegativeButton(R.string.no,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
								}
							});
			AlertDialog alertNewImage = alertDialogBuilder.create();
			alertNewImage.show();
			break;
		case R.id.menu_item_load_image:

			AlertDialog.Builder alertLoadDialogBuilder = new AlertDialog.Builder(
					this);
			alertLoadDialogBuilder
					.setMessage(R.string.dialog_warning_new_image)
					.setCancelable(true)
					.setPositiveButton(R.string.yes,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int id) {
									Intent intent = new Intent(
											Intent.ACTION_GET_CONTENT);
									intent.setType("image/*");
									intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
									startActivityForResult(intent,
											REQ_LOAD_PICTURE);
								}
							})
					.setNegativeButton(R.string.no,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
								}
							});
			AlertDialog alertLoadImage = alertLoadDialogBuilder.create();
			alertLoadImage.show();

			break;
		default:
			return super.onOptionsItemSelected(item);
		}
		return true;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case REQ_LOAD_PICTURE:
				loadBitmapFromUri(data.getData());
				break;
			case REQ_TAKE_PICTURE:
				loadBitmapFromUri(mCameraImageUri);
				break;
			}

		}
	}

	protected void takePhoto() {
		mCameraImageUri = Uri.fromFile(FileIO.createNewEmptyPictureFile(
				MenuFileActivity.this, getString(R.string.temp_picture_name)
						+ ".png"));
		if (mCameraImageUri == null) {
			new InfoDialog(DialogType.WARNING,
					R.string.dialog_error_sdcard_text,
					R.string.dialog_error_save_title).show(
					getSupportFragmentManager(), "savedialogerror");
			return;
		}
		Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
		intent.putExtra(MediaStore.EXTRA_OUTPUT, mCameraImageUri);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
		startActivityForResult(intent, REQ_TAKE_PICTURE);
	}

	protected void loadBitmapFromFileAndRun(final File file,
			final RunnableWithBitmap runnable) {
		String loadMessge = getResources().getString(R.string.dialog_load);
		final ProgressDialog dialog = ProgressDialog.show(this, "", loadMessge,
				true);

		Thread thread = new Thread() {
			@Override
			public void run() {
				Bitmap bitmap = FileIO.getBitmapFromFile(file);// Utils.decodeFile(MainActivity.this,
																// file);
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

	public void saveFile(String fileName) {
		if (FileIO.saveBitmap(this,
				PaintroidApplication.drawingSurface.getBitmap(), fileName) == null) {
			new InfoDialog(DialogType.WARNING,
					R.string.dialog_error_sdcard_text,
					R.string.dialog_error_save_title).show(
					getSupportFragmentManager(), "savedialogerror");
		}
	}

	protected void loadBitmapFromUri(final Uri uri) {
		// FIXME Loading a mutable (!) bitmap from the gallery should be easier
		// *sigh* ...
		// Utils.createFilePathFromUri does not work with all kinds of Uris.
		// Utils.decodeFile is necessary to load even large images as mutable
		// bitmaps without
		// running out of memory.
		Log.d(PaintroidApplication.TAG, "Load Uri " + uri); // TODO remove
															// logging

		String filepath = null;

		if (uri == null || uri.toString().length() < 1) {
			Log.e(PaintroidApplication.TAG, "BAD URI: cannot load image");
		} else {
			filepath = FileIO.createFilePathFromUri(this, uri);
		}

		if (filepath == null || filepath.length() < 1) {
			Log.e("PAINTROID", "BAD URI " + uri);
		} else {
			loadBitmapFromFileAndRun(new File(filepath),
					new RunnableWithBitmap() {
						@Override
						public void run(Bitmap bitmap) {
							PaintroidApplication.drawingSurface
									.resetBitmap(bitmap);
							PaintroidApplication.perspective
									.resetScaleAndTranslation();
						}
					});
		}
	}

	protected void initialiseNewBitmap() {
		Display display = getWindowManager().getDefaultDisplay();
		float actionbarHeight = ACTION_BAR_HEIGHT
				* getResources().getDisplayMetrics().density;
		float width = display.getWidth();
		float height = display.getHeight() - 2 * actionbarHeight;
		Log.d("PAINTROID - MFA", "init new bitmap with: w: " + width + " h:"
				+ height + " = height - 2*" + actionbarHeight);
		Bitmap bitmap = Bitmap.createBitmap((int) width, (int) height,
				Config.ARGB_8888);
		bitmap.eraseColor(Color.TRANSPARENT);
		PaintroidApplication.drawingSurface.resetBitmap(bitmap);
		PaintroidApplication.perspective.resetScaleAndTranslation();
	}

}
