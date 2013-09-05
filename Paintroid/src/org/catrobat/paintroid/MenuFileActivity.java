/**
 *  Paintroid: An image manipulation application for Android.
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
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
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.catrobat.paintroid.dialog.DialogSaveFile;
import org.catrobat.paintroid.dialog.InfoDialog;
import org.catrobat.paintroid.dialog.InfoDialog.DialogType;
import org.catrobat.paintroid.tools.Tool.StateChange;

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

	protected static final int REQUEST_CODE_IMPORTPNG = 1;
	protected static final int REQUEST_CODE_LOAD_PICTURE = 2;
	protected static final int REQUEST_CODE_FINISH = 3;
	protected static final int REQUEST_CODE_TAKE_PICTURE = 4;

	protected static final String PREFIX_CONTENT_GALLERY3D = "content://com.google.android.gallery3d";
	protected static final String PREFIX_CONTENT_ALTERNATIVE_DEVICES = "content://com.android.gallery3d.provider";
	protected static final String URI_NORMAL = "com.google.android.gallery3d";
	protected static final String URI_ALTERNATIVE_DEVICES = "com.android.gallery3d";
	protected static final String TEMPORARY_BITMAP_NAME = "temporary.bmp";

	public static final float ACTION_BAR_HEIGHT = 50.0f;

	protected boolean loadBitmapFailed = false;

	public static enum ACTION {
		SAVE, CANCEL
	};

	private static Uri mCameraImageUri;

	protected abstract class RunnableWithBitmap {
		public abstract void run(Bitmap bitmap);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.menu_item_save_image:
			final Bundle bundle = new Bundle();
			DialogSaveFile saveDialog = new DialogSaveFile(this, bundle);

			// Log.d(PaintroidApplication.TAG, "file loaded from: "
			// + PaintroidApplication.savedBitmapFile.getAbsolutePath());

			if (PaintroidApplication.savedBitmapFile != null) {
				saveDialog.replaceLoadedFile();
			} else {
				saveDialog.show(getSupportFragmentManager(),
						"SaveDialogFragment");
			}
			break;
		case R.id.menu_item_new_image:
			chooseNewImage();
			break;

		case R.id.menu_item_load_image:
			onLoadImage();
			break;
		default:
			return super.onOptionsItemSelected(item);
		}
		return true;
	}

	private void onLoadImage() {

		if (!PaintroidApplication.commandManager.hasCommands()
				&& PaintroidApplication.isPlainImage) {
			startLoadImageIntent();
		} else {

			AlertDialog.Builder alertLoadDialogBuilder = new AlertDialog.Builder(
					this);
			alertLoadDialogBuilder
					.setTitle(R.string.menu_load_image)
					.setMessage(R.string.dialog_warning_new_image)
					.setCancelable(true)
					.setPositiveButton(R.string.yes,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int id) {
									startLoadImageIntent();
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
		}
	}

	private void startLoadImageIntent() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("image/*");
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
		startActivityForResult(intent, REQUEST_CODE_LOAD_PICTURE);
	}

	private void chooseNewImage() {
		AlertDialog.Builder alertChooseNewBuilder = new AlertDialog.Builder(
				this);
		alertChooseNewBuilder.setTitle(R.string.menu_new_image).setItems(
				R.array.new_image, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case 0:
							onNewImage();
							break;
						case 1:
							onNewImageFromCamera();
							break;
						}
					}
				});
		AlertDialog alertNew = alertChooseNewBuilder.create();
		alertNew.show();
		return;

	}

	private void onNewImage() {
		if (!PaintroidApplication.commandManager.hasCommands()
				&& PaintroidApplication.isPlainImage
				&& !PaintroidApplication.openedFromCatroid) {
			initialiseNewBitmap();
		} else {

			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					this);
			alertDialogBuilder
					.setTitle(R.string.menu_new_image)
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
		}
	}

	private void onNewImageFromCamera() {
		if (!PaintroidApplication.commandManager.hasCommands()
				&& PaintroidApplication.isPlainImage
				&& !PaintroidApplication.openedFromCatroid) {
			takePhoto();
		} else {

			AlertDialog.Builder newCameraImageAlertDialogBuilder = new AlertDialog.Builder(
					this);
			newCameraImageAlertDialogBuilder
					.setTitle(R.string.menu_new_image_from_camera)
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
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case REQUEST_CODE_LOAD_PICTURE:
				loadBitmapFromUri(data.getData());
				PaintroidApplication.isPlainImage = false;
				break;
			case REQUEST_CODE_TAKE_PICTURE:
				loadBitmapFromUri(mCameraImageUri);
				PaintroidApplication.isPlainImage = false;
				break;
			}

		}
	}

	protected void takePhoto() {
		File tempFile = FileIO.createNewEmptyPictureFile(MenuFileActivity.this,
				getString(R.string.temp_picture_name) + ".png");
		if (tempFile != null) {
			mCameraImageUri = Uri.fromFile(tempFile);
		}
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
		startActivityForResult(intent, REQUEST_CODE_TAKE_PICTURE);
	}

	protected void loadBitmapFromFileAndRun(final File file,
			final RunnableWithBitmap runnable) {
		String loadMessge = getResources().getString(R.string.dialog_load);
		final ProgressDialog dialog = ProgressDialog.show(
				MenuFileActivity.this, "", loadMessge, true);

		Thread thread = new Thread("loadBitmapFromFileAndRun") {
			@Override
			public void run() {
				Bitmap bitmap = null;
				try {
					bitmap = FileIO.getBitmapFromFile(file);
				} catch (Exception e) {
					loadBitmapFailed = true;

				}
				if (bitmap != null) {
					runnable.run(bitmap);
				} else {
					loadBitmapFailed = true;
				}
				dialog.dismiss();
				PaintroidApplication.currentTool
						.resetInternalState(StateChange.NEW_IMAGE_LOADED);
				if (loadBitmapFailed) {
					loadBitmapFailed = false;
					new InfoDialog(DialogType.WARNING,
							R.string.dialog_loading_image_failed_title,
							R.string.dialog_loading_image_failed_text).show(
							getSupportFragmentManager(),
							"loadbitmapdialogerror");
				}
			}
		};
		thread.start();
	}

	public void saveFile(String fileName) {
		if (FileIO.saveBitmap(this,
				PaintroidApplication.drawingSurface.getBitmapCopy(), fileName) == null) {
			new InfoDialog(DialogType.WARNING,
					R.string.dialog_error_sdcard_text,
					R.string.dialog_error_save_title).show(
					getSupportFragmentManager(), "savedialogerror");
		}
		PaintroidApplication.isSaved = true;
	}

	public boolean isPicasaUri(Uri uri) {
		if (uri.toString().startsWith(PREFIX_CONTENT_ALTERNATIVE_DEVICES)) {
			uri = Uri.parse(uri.toString().replace(URI_ALTERNATIVE_DEVICES,
					URI_NORMAL));
		}

		if (uri.toString().startsWith(PREFIX_CONTENT_GALLERY3D)) {
			return (true);
		} else {
			return (false);
		}
	}

	protected void loadBitmapFromUri(Uri uri) {
		// FIXME Loading a mutable (!) bitmap from the gallery should be easier
		// *sigh* ...
		// Utils.createFilePathFromUri does not work with all kinds of Uris.
		// Utils.decodeFile is necessary to load even large images as mutable
		// bitmaps without
		// running out of memory.

		String filepath = null;

		if (uri == null || uri.toString().length() < 1) {
			Log.e(PaintroidApplication.TAG, "BAD URI: cannot load image");
			return;
		}

		if (isPicasaUri(uri)) {
			loadBitmapFromPicasaAndRun(uri, new RunnableWithBitmap() {
				@Override
				public void run(Bitmap bitmap) {
					PaintroidApplication.drawingSurface.resetBitmap(bitmap);
					PaintroidApplication.perspective.resetScaleAndTranslation();
				}
			});
			return;
		}

		filepath = FileIO.createFilePathFromUri(this, uri);

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

	protected void loadBitmapFromPicasaAndRun(final Uri uri,
			final RunnableWithBitmap runnable) {
		String loadMessge = getResources().getString(R.string.dialog_load);
		final ProgressDialog dialog = ProgressDialog.show(this, "", loadMessge,
				true);

		Thread thread = new Thread() {
			@Override
			public void run() {

				File cacheDirectory;

				cacheDirectory = MenuFileActivity.this.getCacheDir();

				if (!cacheDirectory.exists()) {
					cacheDirectory.mkdirs();
				}

				File cacheFile = new File(cacheDirectory, TEMPORARY_BITMAP_NAME);

				try {
					Bitmap bitmap = null;
					InputStream inputStream = null;

					inputStream = getContentResolver().openInputStream(uri);

					OutputStream outputStream = new FileOutputStream(cacheFile);
					FileIO.copyStream(inputStream, outputStream);
					outputStream.close();

					bitmap = FileIO.getBitmapFromFile(cacheFile);

					if (bitmap != null) {
						runnable.run(bitmap);
					} else {
						Log.e("PAINTROID", "BAD FILE " + cacheFile);
					}
					dialog.dismiss();
					PaintroidApplication.currentTool
							.resetInternalState(StateChange.NEW_IMAGE_LOADED);

					return;
				} catch (Exception ex) {
					Log.e("PAINTROID", "Failed to load Picasa image");
					return;
				}
			}
		};
		thread.start();
	}

	protected void initialiseNewBitmap() {
		Display display = getWindowManager().getDefaultDisplay();
		float width = display.getWidth();
		float height = display.getHeight();
		Log.d("PAINTROID - MFA", "init new bitmap with: w: " + width + " h:"
				+ height);
		Bitmap bitmap = Bitmap.createBitmap((int) width, (int) height,
				Config.ARGB_8888);
		bitmap.eraseColor(Color.TRANSPARENT);
		PaintroidApplication.drawingSurface.resetBitmap(bitmap);
		PaintroidApplication.perspective.resetScaleAndTranslation();
		PaintroidApplication.currentTool
				.resetInternalState(StateChange.NEW_IMAGE_LOADED);
		PaintroidApplication.isPlainImage = true;
	}
}
