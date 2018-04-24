/**
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2015 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 * <p/>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.annotation.IntDef;
import android.support.annotation.VisibleForTesting;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.widget.Toast;

import org.catrobat.paintroid.command.Command;
import org.catrobat.paintroid.command.implementation.LayerCommand;
import org.catrobat.paintroid.command.implementation.LoadCommand;
import org.catrobat.paintroid.dialog.CustomAlertDialogBuilder;
import org.catrobat.paintroid.dialog.IndeterminateProgressDialog;
import org.catrobat.paintroid.dialog.InfoDialog;
import org.catrobat.paintroid.dialog.InfoDialog.DialogType;
import org.catrobat.paintroid.listener.LayerListener;
import org.catrobat.paintroid.tools.Tool.StateChange;
import org.catrobat.paintroid.tools.implementation.ImportTool;
import org.catrobat.paintroid.ui.ToastFactory;

import java.io.File;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public abstract class NavigationDrawerMenuActivity extends AppCompatActivity {
	private static final String TAG = NavigationDrawerMenuActivity.class.getSimpleName();

	public static final float ACTION_BAR_HEIGHT = 50.0f;

	static final int REQUEST_CODE_IMPORTPNG = 1;
	static final int REQUEST_CODE_LOAD_PICTURE = 2;
	static final int REQUEST_CODE_FINISH = 3;
	static final int REQUEST_CODE_TAKE_PICTURE = 4;
	static final int REQUEST_CODE_LANGUAGE = 5;
	public static boolean isSaved = true;
	public static Uri savedPictureUri = null;

	@IntDef({REQUEST_CODE_IMPORTPNG,
			REQUEST_CODE_LOAD_PICTURE,
			REQUEST_CODE_FINISH,
			REQUEST_CODE_TAKE_PICTURE,
			REQUEST_CODE_LANGUAGE})
	@Retention(RetentionPolicy.SOURCE)
	@interface RequestCode {
	}

	Uri cameraImageUri;
	boolean loadBitmapFailed = false;
	boolean isPlainImage = true;
	boolean scaleImage = true;
	@VisibleForTesting
	public boolean saveCopy = false;
	@VisibleForTesting
	public boolean openedFromCatroid;

	boolean imageHasBeenModified() {
		return (!(LayerListener.getInstance().getAdapter().getLayers().size() == 1)
				|| !isPlainImage || PaintroidApplication.commandManager.checkIfDrawn());
	}

	boolean imageHasBeenSaved() {
		return isSaved;
	}

	protected void onLoadImage() {

		if (!imageHasBeenModified() || imageHasBeenSaved()) {
			startLoadImageIntent();
		} else {

			final SaveTask saveTask = new SaveTask(this);

			AlertDialog.Builder alertLoadDialogBuilder = new CustomAlertDialogBuilder(this);
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
			alertLoadDialogBuilder.show();
		}
	}

	private void startLoadImageIntent() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("image/*");
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
		startActivityForResult(intent, REQUEST_CODE_LOAD_PICTURE);
	}

	protected void newImage() {
		if (!imageHasBeenModified() && !openedFromCatroid || imageHasBeenSaved()) {
			chooseNewImage();
		} else {

			final SaveTask saveTask = new SaveTask(this);

			AlertDialog.Builder newCameraImageAlertDialogBuilder = new CustomAlertDialogBuilder(this);
			newCameraImageAlertDialogBuilder
					.setTitle(R.string.menu_new_image)
					.setMessage(R.string.dialog_warning_new_image)
					.setCancelable(true)
					.setPositiveButton(R.string.save_button_text,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int id) {
									saveTask.execute();
									chooseNewImage();
								}
							})
					.setNegativeButton(R.string.discard_button_text,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int id) {
									chooseNewImage();
								}
							});
			newCameraImageAlertDialogBuilder.show();
		}
	}

	protected void chooseNewImage() {

		AlertDialog.Builder alertChooseNewBuilder = new CustomAlertDialogBuilder(this);
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
		alertChooseNewBuilder.show();
	}

	private void onNewImage() {
		PaintroidApplication.commandManager.resetAndClear(false);
		initialiseNewBitmap();
		LayerListener.getInstance().resetLayer();
	}

	private void onNewImageFromCamera() {
		PaintroidApplication.commandManager.resetAndClear(false);
		LayerListener.getInstance().resetLayer();
		takePhoto();
	}

	@Override
	public void onActivityResult(@RequestCode int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == Activity.RESULT_OK) {
			PaintroidApplication.commandManager.resetAndClear(false);
			LayerListener.getInstance().resetLayer();

			switch (requestCode) {
				case REQUEST_CODE_LOAD_PICTURE:
					loadBitmapFromUri(data.getData());
					saveCopy = true;
					break;
				case REQUEST_CODE_TAKE_PICTURE:
					loadBitmapFromUri(cameraImageUri);
					break;
				case REQUEST_CODE_FINISH:
				case REQUEST_CODE_IMPORTPNG:
				case REQUEST_CODE_LANGUAGE:
				default:
					return;
			}

			isPlainImage = false;
			isSaved = false;
			savedPictureUri = null;
			LayerListener.getInstance().getCurrentLayer().setImage(PaintroidApplication.drawingSurface.getBitmapCopy());
			LayerListener.getInstance().refreshView();
		}
	}

	protected void takePhoto() {
		File tempFile = FileIO.createNewEmptyPictureFile(FileIO.getDefaultFileName());
		if (tempFile != null) {
			cameraImageUri = Uri.fromFile(tempFile);
		}
		if (cameraImageUri == null) {
			InfoDialog.newInstance(DialogType.WARNING,
					R.string.dialog_error_sdcard_text,
					R.string.dialog_error_save_title).show(
					getSupportFragmentManager(), "savedialogerror");
			return;
		}
		Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
		intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
		startActivityForResult(intent, REQUEST_CODE_TAKE_PICTURE);
	}

	protected void loadBitmapFromUriAndRun(final Uri uri, final RunnableWithBitmap runnable) {
		String loadMessge = getResources().getString(R.string.dialog_load);
		final ProgressDialog dialog = ProgressDialog.show(
				this, "", loadMessge, true);

		Thread thread = new Thread("loadBitmapFromUriAndRun") {
			@Override
			public void run() {
				Bitmap bitmap = null;
				try {
					bitmap = FileIO.getBitmapFromUri(NavigationDrawerMenuActivity.this, uri, scaleImage);
					scaleImage = true;
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
					InfoDialog.newInstance(DialogType.WARNING,
							R.string.dialog_loading_image_failed_title,
							R.string.dialog_loading_image_failed_text).show(
							getSupportFragmentManager(),
							"loadbitmapdialogerror");
				} else {
					if (!(PaintroidApplication.currentTool instanceof ImportTool)) {
						savedPictureUri = uri;
					}
				}
			}
		};
		thread.start();
	}

	// if needed use Async Task
	public void saveFile() {

		if (!FileIO.saveBitmap(this, LayerListener.getInstance().getBitmapOfAllLayersToSave(), null, saveCopy)) {
			InfoDialog.newInstance(DialogType.WARNING,
					R.string.dialog_error_sdcard_text,
					R.string.dialog_error_save_title).show(
					getSupportFragmentManager(), "savedialogerror");
		}

		isSaved = !openedFromCatroid;
	}

	protected void loadBitmapFromUri(Uri uri) {
		if (uri == null || uri.toString().length() < 1) {
			Log.e(TAG, "BAD URI: cannot load image");
			return;
		}

		loadBitmapFromUriAndRun(uri, new RunnableWithBitmap() {
			@Override
			public void run(Bitmap bitmap) {
				Command command = new LoadCommand(bitmap);
				PaintroidApplication.commandManager.commitCommandToLayer(
						new LayerCommand(LayerListener.getInstance().getCurrentLayer()), command);
			}
		});
	}

	protected void initialiseNewBitmap() {
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		Log.d("PAINTROID - MFA", "init new bitmap with: w: " + size.x + " h:" + size.y);
		Bitmap bitmap = Bitmap.createBitmap(size.x, size.y, Config.ARGB_8888);
		bitmap.eraseColor(Color.TRANSPARENT);
		PaintroidApplication.drawingSurface.resetBitmap(bitmap);
		PaintroidApplication.perspective.resetScaleAndTranslation();
		PaintroidApplication.currentTool
				.resetInternalState(StateChange.NEW_IMAGE_LOADED);
		isPlainImage = true;
		isSaved = false;
		savedPictureUri = null;
		PaintroidApplication.drawingSurface.refreshDrawingSurface();
	}

	abstract class RunnableWithBitmap {
		public abstract void run(Bitmap bitmap);
	}

	class SaveTask extends AsyncTask<String, Void, Void> {

		private NavigationDrawerMenuActivity context;

		SaveTask(NavigationDrawerMenuActivity context) {
			this.context = context;
		}

		@Override
		protected void onPreExecute() {
			IndeterminateProgressDialog.getInstance().show();
			Log.d(TAG, "async tast prgDialog isShowing"
					+ IndeterminateProgressDialog.getInstance().isShowing());
		}

		@Override
		protected Void doInBackground(String... arg0) {
			saveFile();
			return null;
		}

		@Override
		protected void onPostExecute(Void Result) {
			IndeterminateProgressDialog.getInstance().dismiss();
			if (!saveCopy) {
				ToastFactory.makeText(context, R.string.saved, Toast.LENGTH_LONG)
						.show();
			} else {
				ToastFactory.makeText(context, R.string.copy, Toast.LENGTH_LONG)
						.show();
				saveCopy = false;
			}
		}
	}
}
