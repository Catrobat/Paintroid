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

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnDismissListener;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import at.tugraz.ist.paintroid.dialog.DialogAbout;
import at.tugraz.ist.paintroid.dialog.DialogError;
import at.tugraz.ist.paintroid.dialog.DialogNewDrawing;
import at.tugraz.ist.paintroid.dialog.DialogOverwriteFile;
import at.tugraz.ist.paintroid.dialog.DialogSaveFileName;
import at.tugraz.ist.paintroid.helper.FileIO;

public class FileActivity extends Activity implements OnClickListener {

	// Buttons from FileMenu-panel
	Button file_button_new;
	Button file_button_load;
	Button file_button_save;
	Button file_button_cancel;

	//Uri of the image taken directly from cam
	private Uri camImageUri = null;

	// Request codes for activity results
	private static int IMAGE_TO_LOAD = 0;
	private static int TAKE_PICTURE = 1;

	// Returns values from activity when finished
	Intent resultIntent = new Intent();

	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.file);

		// Listeners for the buttons
		file_button_new = (Button) this.findViewById(R.id.btn_file_New);
		file_button_new.setOnClickListener(this);

		file_button_load = (Button) this.findViewById(R.id.btn_file_Load);
		file_button_load.setOnClickListener(this);

		file_button_save = (Button) this.findViewById(R.id.btn_file_Save);
		file_button_save.setOnClickListener(this);

		file_button_cancel = (Button) this.findViewById(R.id.btn_file_Cancel);
		file_button_cancel.setOnClickListener(this);

		//create temporary picture for taking photo from cam
		//this needs to be done here to avoid landspace bug when returning from cam activity
		camImageUri = new FileIO(this).createBitmapToSDCardURI(getContentResolver(), "tmpCamPicture");
		if (camImageUri == null) {
			DialogError error = new DialogError(this, R.string.dialog_error_sdcard_title,
					R.string.dialog_error_sdcard_text);
			error.show();
		}
	}

	/**
	 * Handle {@link FileActivity}-buttons onClick-events
	 */
	@Override
	public void onClick(View v) {

		switch (v.getId()) {

			// Show new drawing Dialog an handle return Value
			case R.id.btn_file_New:

				DialogNewDrawing newdrawingDialog = new DialogNewDrawing(this);
				OnDismissListener newdrawingListener = new DialogInterface.OnDismissListener() {

					@Override
					public void onDismiss(DialogInterface newdrawingInterface) {

						if (newdrawingInterface instanceof DialogNewDrawing) {
							DialogNewDrawing newdrawingDialog_ = (DialogNewDrawing) newdrawingInterface;
							//call method to handle return value
							newDrawingEvent(newdrawingDialog_.newDrawingChoose);
						}
					}
				};

				newdrawingDialog.setOnDismissListener(newdrawingListener);
				newdrawingDialog.show();
				break;

			// Cancel and return to MainActivity (returns EMPTY Uri to Main)
			case R.id.btn_file_Cancel:

				resultIntent.putExtra("IntentReturnValue", "CANCEL");
				resultIntent.putExtra("UriString", Uri.EMPTY);
				setResult(Activity.RESULT_OK, resultIntent);
				this.finish();
				break;

			// Save the current image (returns Save Name to Main)
			case R.id.btn_file_Save:

				DialogSaveFileName saveDialog = new DialogSaveFileName(this);
				saveDialog.show();
				break;

			// Load an image from the sdcard through the gallery
			case R.id.btn_file_Load:

				startActivityForResult(new Intent(Intent.ACTION_PICK,
						android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), IMAGE_TO_LOAD);
				break;
		}
	}

	/**
	 * This method will be called when the gallery activity finishes and
	 * an image was selected by the user.
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		// The intent contains the results of this activity when it finishes
		Intent resultIntent = new Intent();

		// Check if the gallery returned an image and pass its URI to the MainActivity
		if (requestCode == IMAGE_TO_LOAD && resultCode == Activity.RESULT_OK) {

			Uri selectedGalleryImage = data.getData();
			//Convert the Android URI to a real path
			String imageFilePath = FileIO.getRealPathFromURI(getContentResolver(), selectedGalleryImage);

			resultIntent.putExtra("IntentReturnValue", "LOAD");
			resultIntent.putExtra("UriString", imageFilePath);
			resultIntent.putExtra("GaleryUri", selectedGalleryImage.toString());
			getParent().setResult(Activity.RESULT_OK, resultIntent);
			this.finish();

		} else if (requestCode == TAKE_PICTURE && resultCode == Activity.RESULT_OK) {
			resultIntent.putExtra("IntentReturnValue", "LOAD");
			resultIntent.putExtra("UriString", camImageUri.getPath());
			getParent().setResult(Activity.RESULT_OK, resultIntent);
			this.finish();
		}

	}

	/**
	 * Set the elements of the options menu. The {@link MainActivity}'s
	 * menu is reused, thus the two items 'clear' and 'reset'
	 * must be removed.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.file_menu, menu);
		return true;
	}

	/**
	 * Handle options menu button events
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {

			case R.id.item_file_Quit:
				this.finish();
				return true;

			case R.id.item_file_About:
				DialogAbout about = new DialogAbout(this);
				about.show();
				return true;

			default:
				return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * This method is called by {@link DialogSaveFileName}
	 * 
	 * @param saveFileName
	 *            new name of the file to save
	 */
	public void setSaveName(String saveFileName) {

		Log.d("PAINTROID", "Get Filename to save: " + saveFileName);

		resultIntent.putExtra("IntentReturnValue", "SAVE");
		resultIntent.putExtra("UriString", saveFileName);
		getParent().setResult(Activity.RESULT_OK, resultIntent);
		this.finish();
	}

	public void startWarningOverwriteDialog(String filename) {
		DialogOverwriteFile overwriteDialog = new DialogOverwriteFile(this, filename);
		overwriteDialog.show();
	}

	/**
	 * This method handles the return value of
	 * the new drawing Dialog
	 * 
	 * @param newDrawingChoose
	 *            return Value of the new drawing Dialog
	 */
	private void newDrawingEvent(String newDrawingChoose) {
		if (newDrawingChoose.equals("CANCEL")) {
			//do nothing, only show the FileActivity
		} else if (newDrawingChoose.equals("FROMCAM")) {
			//Start cam app to take photo
			Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
			intent.putExtra(MediaStore.EXTRA_OUTPUT, camImageUri);
			startActivityForResult(intent, TAKE_PICTURE);
		} else if (newDrawingChoose.equals("NEWDRAWING")) {
			resultIntent.putExtra("IntentReturnValue", "NEW");
			resultIntent.putExtra("UriString", Uri.EMPTY);
			getParent().setResult(Activity.RESULT_OK, resultIntent);
			this.finish();
		}
	}
}