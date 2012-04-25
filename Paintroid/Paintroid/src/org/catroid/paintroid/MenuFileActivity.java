/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *  
 *  Paintroid: An image manipulation application for Android, part of the
 *  Catroid project and Catroid suite of software.
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catroid.paintroid;

import org.catroid.paintroid.dialog.DialogAbout;
import org.catroid.paintroid.dialog.DialogError;
import org.catroid.paintroid.dialog.DialogNewDrawing;
import org.catroid.paintroid.dialog.DialogSaveFile;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MenuFileActivity extends Activity implements OnClickListener {
	private static final int REQ_LOAD_PICTURE = 0;
	private static final int REQ_TAKE_PICTURE = 1;

	public static final String RET_ACTION = "RET_ACTION";
	public static final String RET_URI = "RET_URI";
	public static final String RET_FILENAME = "RET_FILENAME";

	public static enum ACTION {
		NEW, LOAD, SAVE, CANCEL, QUIT
	};

	private Button mBtnNewFile;
	private Button mBtnLoadFile;
	private Button mBtnSaveFile;
	private Button mBtnCancel;
	private Uri mCameraImageUri;
	private Intent mResultIntent;

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

		mResultIntent = new Intent();
		mResultIntent.putExtra(RET_ACTION, ACTION.CANCEL);
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
				mResultIntent.putExtra(RET_ACTION, ACTION.NEW);
				finish();
				return true;
			case R.id.item_file_About:
				new DialogAbout(this).show();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void finish() {
		getParent().setResult(Activity.RESULT_OK, mResultIntent);
		super.finish();
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
									mResultIntent.putExtra(RET_ACTION, ACTION.NEW);
									mResultIntent.putExtra(RET_URI, Uri.EMPTY);
									finish();
									break;
								case NEW_CAMERA:
									// Create temporary file for taking photo from camera. This needs to be done to
									// avoid a bug with landscape orientation when returning from the camera activity.
									mCameraImageUri = Uri.fromFile(FileIO.createNewEmptyPictureFile(
											MenuFileActivity.this, "tmp_paintroid_picture.png"));
									if (mCameraImageUri == null) {
										DialogError error = new DialogError(MenuFileActivity.this,
												R.string.dialog_error_sdcard_title, R.string.dialog_error_sdcard_text);
										error.show();
									}
									Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
									intent.putExtra(MediaStore.EXTRA_OUTPUT, mCameraImageUri);
									intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
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
			case R.id.btn_file_Load:
				Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
				intent.setType("image/*");
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
				startActivityForResult(intent, REQ_LOAD_PICTURE);
				break;
			case R.id.btn_file_Save:
				final Bundle bundle = new Bundle();
				DialogSaveFile saveDialog = new DialogSaveFile(this, bundle);
				saveDialog.setOnDismissListener(new OnDismissListener() {
					@Override
					public void onDismiss(DialogInterface dialog) {
						String saveFileName = bundle.getString(DialogSaveFile.BUNDLE_SAVEFILENAME);
						mResultIntent.putExtra(RET_ACTION, ACTION.SAVE);
						mResultIntent.putExtra(RET_FILENAME, saveFileName);
						finish();
					}
				});
				saveDialog.show();
				break;
			case R.id.btn_file_Cancel:
				mResultIntent.putExtra(RET_ACTION, ACTION.CANCEL);
				setResult(Activity.RESULT_OK, mResultIntent);
				finish();
				break;
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
				case REQ_LOAD_PICTURE:
					mResultIntent.putExtra(RET_ACTION, ACTION.LOAD);
					mResultIntent.putExtra(RET_URI, data.getData());
					break;
				case REQ_TAKE_PICTURE:
					mResultIntent.putExtra(RET_ACTION, ACTION.LOAD);
					mResultIntent.putExtra(RET_URI, mCameraImageUri);
					break;
			}
			finish();
		}
	}
}
