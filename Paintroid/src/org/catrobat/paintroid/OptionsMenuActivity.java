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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;

import org.catrobat.paintroid.dialog.InfoDialog;
import org.catrobat.paintroid.dialog.InfoDialog.DialogType;
import org.catrobat.paintroid.dialog.ProgressIntermediateDialog;
import org.catrobat.paintroid.tools.Tool.StateChange;

import java.io.File;

public abstract class OptionsMenuActivity extends SherlockFragmentActivity {

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
			SaveTask saveTask = new SaveTask(this);
			saveTask.execute();
			break;
		case R.id.menu_item_save_copy:
			PaintroidApplication.saveCopy = true;
			SaveTask saveCopyTask = new SaveTask(this);
			saveCopyTask.execute();
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
		} else if (PaintroidApplication.isSaved) {
			startLoadImageIntent();
		} else {

			final SaveTask saveTask = new SaveTask(this);

			AlertDialog.Builder alertLoadDialogBuilder = new AlertDialog.Builder(
					this);
			alertLoadDialogBuilder
					.setTitle(R.string.menu_load_image)
					.setMessage(R.string.dialog_warning_new_image)
					.setCancelable(true)
					.setPositiveButton(R.string.save_button_text,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int id) {
									saveTask.execute();
									startLoadImageIntent();
								}
							})
					.setNegativeButton(R.string.discard_button_text,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int id) {
									startLoadImageIntent();
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
		} else if (PaintroidApplication.isSaved) {
			initialiseNewBitmap();
		} else {

			final SaveTask saveTask = new SaveTask(this);

			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					this);
			alertDialogBuilder
					.setTitle(R.string.menu_new_image)
					.setMessage(R.string.dialog_warning_new_image)
					.setCancelable(true)
					.setPositiveButton(R.string.save_button_text,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int id) {
									saveTask.execute();
									initialiseNewBitmap();

								}
							})
					.setNegativeButton(R.string.discard_button_text,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int id) {
									initialiseNewBitmap();
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
		} else if (PaintroidApplication.isSaved) {
			takePhoto();
		} else {

			final SaveTask saveTask = new SaveTask(this);

			AlertDialog.Builder newCameraImageAlertDialogBuilder = new AlertDialog.Builder(
					this);
			newCameraImageAlertDialogBuilder
					.setTitle(R.string.menu_new_image_from_camera)
					.setMessage(R.string.dialog_warning_new_image)
					.setCancelable(true)
					.setPositiveButton(R.string.save_button_text,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int id) {
									saveTask.execute();
									takePhoto();
								}
							})
					.setNegativeButton(R.string.discard_button_text,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int id) {
									takePhoto();
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
				PaintroidApplication.isSaved = false;
				PaintroidApplication.savedPictureUri = null;
				break;
			case REQUEST_CODE_TAKE_PICTURE:
				loadBitmapFromUri(mCameraImageUri);
				PaintroidApplication.isPlainImage = false;
				PaintroidApplication.isSaved = false;
				PaintroidApplication.savedPictureUri = null;
				break;
			}

		}
	}

	protected void takePhoto() {
		File tempFile = FileIO.createNewEmptyPictureFile(
				OptionsMenuActivity.this, FileIO.getDefaultFileName());
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

	protected void loadBitmapFromUriAndRun(final Uri uri,
			final RunnableWithBitmap runnable) {
		String loadMessge = getResources().getString(R.string.dialog_load);
		final ProgressDialog dialog = ProgressDialog.show(
				OptionsMenuActivity.this, "", loadMessge, true);

		Thread thread = new Thread("loadBitmapFromUriAndRun") {
			@Override
			public void run() {
				Bitmap bitmap = null;
				try {
					bitmap = FileIO.getBitmapFromUri(uri);
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

	// if needed use Async Task
	public void saveFile() {

		if (!FileIO.saveBitmap(this,
				PaintroidApplication.drawingSurface.getBitmapCopy())) {
			new InfoDialog(DialogType.WARNING,
					R.string.dialog_error_sdcard_text,
					R.string.dialog_error_save_title).show(
					getSupportFragmentManager(), "savedialogerror");
		}

		PaintroidApplication.isSaved = true;
	}

	protected void loadBitmapFromUri(Uri uri) {
		if (uri == null || uri.toString().length() < 1) {
			Log.e(PaintroidApplication.TAG, "BAD URI: cannot load image");
			return;
		}

		loadBitmapFromUriAndRun(uri, new RunnableWithBitmap() {
			@Override
			public void run(Bitmap bitmap) {
				PaintroidApplication.drawingSurface.resetBitmap(bitmap);
				PaintroidApplication.perspective.resetScaleAndTranslation();
			}
		});
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
		PaintroidApplication.isSaved = false;
		PaintroidApplication.savedPictureUri = null;
	}

	protected class SaveTask extends AsyncTask<String, Void, Void> {

		private OptionsMenuActivity context;

		public SaveTask(OptionsMenuActivity context) {
			this.context = context;
		}

		@Override
		protected void onPreExecute() {
			ProgressIntermediateDialog.getInstance().show();
			Log.d(PaintroidApplication.TAG, "async tast prgDialog isShowing"
					+ ProgressIntermediateDialog.getInstance().isShowing());
		}

		@Override
		protected Void doInBackground(String... arg0) {
			saveFile();
			return null;
		}

		@Override
		protected void onPostExecute(Void Result) {
			ProgressIntermediateDialog.getInstance().dismiss();
			if (!PaintroidApplication.saveCopy) {
				Toast.makeText(context, R.string.saved, Toast.LENGTH_LONG)
						.show();
			} else {
				Toast.makeText(context, R.string.copy, Toast.LENGTH_LONG)
						.show();
				PaintroidApplication.saveCopy = false;
			}
		}
	}
}
