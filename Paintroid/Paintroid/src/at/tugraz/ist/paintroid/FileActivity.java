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
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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

public class FileActivity extends Activity implements OnClickListener {
	private static final int REQ_LOAD_PICTURE = 0;
	private static final int REQ_TAKE_PICTURE = 1;

	public static final String RET_VALUE = "RET_VALUE";
	public static final String RET_URI = "RET_URI";
	public static final String RET_FILENAME = "RET_FILENAME";

	public static enum RETURN_VALUE {
		CANCEL, LOAD, NEW, SAVE
	};

	private Button mBtnNewFile;
	private Button mBtnLoadFile;
	private Button mBtnSaveFile;
	private Button mBtnCancel;
	private Uri camImageUri;
	private Intent mResultIntent = new Intent();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.file);

		mBtnNewFile = (Button) this.findViewById(R.id.btn_file_New);
		mBtnNewFile.setOnClickListener(this);

		mBtnLoadFile = (Button) this.findViewById(R.id.btn_file_Load);
		mBtnLoadFile.setOnClickListener(this);

		mBtnSaveFile = (Button) this.findViewById(R.id.btn_file_Save);
		mBtnSaveFile.setOnClickListener(this);

		mBtnCancel = (Button) this.findViewById(R.id.btn_file_Cancel);
		mBtnCancel.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_file_New:
				DialogNewDrawing dialogNewDrawing = new DialogNewDrawing(this);
				dialogNewDrawing.setOnDismissListener(new DialogInterface.OnDismissListener() {
					@Override
					public void onDismiss(DialogInterface dialog) {
						if (dialog instanceof DialogNewDrawing) {
							switch (((DialogNewDrawing) dialog).resultCode) {
								case NEW_EMPTY:
									mResultIntent.putExtra(RET_VALUE, RETURN_VALUE.NEW);
									mResultIntent.putExtra(RET_URI, Uri.EMPTY);
									getParent().setResult(Activity.RESULT_OK, mResultIntent);
									finish();
									break;
								case NEW_CAMERA:
									// Create temporary file for taking photo from camera. This needs to be done to
									// avoid a bug with landscape orientation when returning from the camera activity.
									camImageUri = Uri.fromFile(FileIO.createNewEmptyPictureFile(FileActivity.this,
											"tmp_paintroid_picture.png"));
									if (camImageUri == null) {
										DialogError error = new DialogError(FileActivity.this,
												R.string.dialog_error_sdcard_title, R.string.dialog_error_sdcard_text);
										error.show();
									}
									Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
									intent.putExtra(MediaStore.EXTRA_OUTPUT, camImageUri);
									startActivityForResult(intent, REQ_TAKE_PICTURE);
									break;
								default:
									break;
							}

						}
					}
				});
				dialogNewDrawing.show();
				break;
			case R.id.btn_file_Cancel:
				mResultIntent.putExtra(RET_VALUE, RETURN_VALUE.CANCEL);
				mResultIntent.putExtra(RET_URI, Uri.EMPTY);
				setResult(Activity.RESULT_OK, mResultIntent);
				this.finish();
				break;
			case R.id.btn_file_Save:
				DialogSaveFileName saveDialog = new DialogSaveFileName(this);
				saveDialog.show();
				break;
			case R.id.btn_file_Load:
				Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
				intent.setType("image/*");
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
				startActivityForResult(intent, REQ_LOAD_PICTURE);
				break;
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == REQ_LOAD_PICTURE && resultCode == Activity.RESULT_OK) {

			mResultIntent.putExtra(RET_VALUE, RETURN_VALUE.LOAD);
			mResultIntent.putExtra(RET_URI, data.getData());
			getParent().setResult(Activity.RESULT_OK, mResultIntent);
			finish();

		} else if (requestCode == REQ_TAKE_PICTURE && resultCode == Activity.RESULT_OK) {

			mResultIntent.putExtra(RET_VALUE, RETURN_VALUE.LOAD);
			mResultIntent.putExtra(RET_URI, camImageUri);
			getParent().setResult(Activity.RESULT_OK, mResultIntent);
			finish();

		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.file_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.item_file_Quit:
				finish();
				return true;
			case R.id.item_file_About:
				DialogAbout about = new DialogAbout(this);
				about.show();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	public void setSaveName(String saveFileName) {
		mResultIntent.putExtra(RET_VALUE, RETURN_VALUE.SAVE);
		mResultIntent.putExtra(RET_FILENAME, saveFileName);
		getParent().setResult(Activity.RESULT_OK, mResultIntent);
		finish();
	}

	public void startWarningOverwriteDialog(String filename) {
		DialogOverwriteFile overwriteDialog = new DialogOverwriteFile(this, filename);
		overwriteDialog.show();
	}
}
