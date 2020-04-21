/*
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2015 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid.ui;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.Gravity;
import android.widget.Toast;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.WelcomeActivity;
import org.catrobat.paintroid.colorpicker.ColorPickerDialog;
import org.catrobat.paintroid.common.Constants;
import org.catrobat.paintroid.common.MainActivityConstants.ActivityRequestCode;
import org.catrobat.paintroid.contract.MainActivityContracts;
import org.catrobat.paintroid.dialog.AboutDialog;
import org.catrobat.paintroid.dialog.FeedbackDialog;
import org.catrobat.paintroid.dialog.IndeterminateProgressDialog;
import org.catrobat.paintroid.dialog.InfoDialog;
import org.catrobat.paintroid.dialog.LikeUsDialog;
import org.catrobat.paintroid.dialog.PermanentDenialPermissionInfoDialog;
import org.catrobat.paintroid.dialog.PermissionInfoDialog;
import org.catrobat.paintroid.dialog.RateUsDialog;
import org.catrobat.paintroid.dialog.SaveBeforeFinishDialog;
import org.catrobat.paintroid.dialog.SaveBeforeFinishDialog.SaveBeforeFinishDialogType;
import org.catrobat.paintroid.dialog.SaveBeforeLoadImageDialog;
import org.catrobat.paintroid.dialog.SaveBeforeNewImageDialog;
import org.catrobat.paintroid.tools.ToolReference;

import static android.app.Activity.RESULT_OK;
import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;

public class MainActivityNavigator implements MainActivityContracts.Navigator {
	private MainActivity mainActivity;
	private final ToolReference toolReference;
	private AppCompatDialog progressDialog;

	public MainActivityNavigator(MainActivity mainActivity, ToolReference toolReference) {
		this.mainActivity = mainActivity;
		this.toolReference = toolReference;
	}

	@Override
	public void showColorPickerDialog() {
		if (findFragmentByTag(Constants.COLOR_PICKER_DIALOG_TAG) == null) {
			ColorPickerDialog dialog = ColorPickerDialog.newInstance(toolReference.get().getDrawPaint().getColor(), true);
			setupColorPickerDialogListeners(dialog);
			showFragment(dialog, Constants.COLOR_PICKER_DIALOG_TAG);
		}
	}

	private void showDialogFragmentSafely(DialogFragment dialog, String tag) {
		FragmentManager fragmentManager = mainActivity.getSupportFragmentManager();
		if (!fragmentManager.isStateSaved()) {
			dialog.show(fragmentManager, tag);
		}
	}

	private void showFragment(Fragment fragment, String tag) {
		FragmentManager fragmentManager = mainActivity.getSupportFragmentManager();
		fragmentManager.beginTransaction()
				.setCustomAnimations(R.anim.slide_to_top, R.anim.slide_to_bottom, R.anim.slide_to_top, R.anim.slide_to_bottom)
				.addToBackStack(null)
				.add(R.id.fragment_container, fragment, tag)
				.commit();
	}

	private Fragment findFragmentByTag(String tag) {
		return mainActivity.getSupportFragmentManager().findFragmentByTag(tag);
	}

	private void setupColorPickerDialogListeners(ColorPickerDialog dialog) {
		dialog.addOnColorPickedListener(new ColorPickerDialog.OnColorPickedListener() {
			@Override
			public void colorChanged(int color) {
				toolReference.get().changePaintColor(color);
				mainActivity.getPresenter().setBottomNavigationColor(color);
			}
		});
	}

	private void openPlayStore(String applicationId) {
		Uri uriPlayStore = Uri.parse("market://details?id=" + applicationId);
		Intent openPlayStore = new Intent(Intent.ACTION_VIEW, uriPlayStore);

		try {
			mainActivity.startActivity(openPlayStore);
		} catch (ActivityNotFoundException e) {
			Uri uriNoPlayStore = Uri.parse("http://play.google.com/store/apps/details?id=" + applicationId);
			Intent noPlayStoreInstalled = new Intent(Intent.ACTION_VIEW, uriNoPlayStore);
			mainActivity.startActivity(noPlayStoreInstalled);
		}
	}

	@Override
	public void startLoadImageActivity(@ActivityRequestCode int requestCode) {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("image/*");
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
		mainActivity.startActivityForResult(intent, requestCode);
	}

	@Override
	public void startImportImageActivity(@ActivityRequestCode int requestCode) {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("image/*");
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
		mainActivity.startActivityForResult(intent, requestCode);
	}

	@Override
	public void startWelcomeActivity(@ActivityRequestCode int requestCode) {
		Intent intent = new Intent(mainActivity.getApplicationContext(), WelcomeActivity.class);
		intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
		mainActivity.startActivityForResult(intent, requestCode);
	}

	@Override
	public void showAboutDialog() {
		AboutDialog about = AboutDialog.newInstance();
		about.show(mainActivity.getSupportFragmentManager(), Constants.ABOUT_DIALOG_FRAGMENT_TAG);
	}

	@Override
	public void showLikeUsDialog() {
		LikeUsDialog likeUsDialog = LikeUsDialog.newInstance();
		likeUsDialog.show(mainActivity.getSupportFragmentManager(), Constants.LIKE_US_DIALOG_FRAGMENT_TAG);
	}

	@Override
	public void showRateUsDialog() {
		RateUsDialog rateUsDialog = RateUsDialog.newInstance();
		rateUsDialog.show(mainActivity.getSupportFragmentManager(), Constants.RATE_US_DIALOG_FRAGMENT_TAG);
	}

	@Override
	public void showFeedbackDialog() {
		FeedbackDialog feedbackDialog = FeedbackDialog.newInstance();
		feedbackDialog.show(mainActivity.getSupportFragmentManager(), Constants.FEEDBACK_DIALOG_FRAGMENT_TAG);
	}

	@Override
	public void showIndeterminateProgressDialog() {
		if (progressDialog == null) {
			progressDialog = IndeterminateProgressDialog.newInstance(mainActivity);
		}
		progressDialog.show();
	}

	@Override
	public void dismissIndeterminateProgressDialog() {
		if (progressDialog != null) {
			progressDialog.dismiss();
		}
		progressDialog = null;
	}

	@Override
	public void returnToPocketCode(String path) {
		Intent resultIntent = new Intent();
		resultIntent.putExtra(Constants.PAINTROID_PICTURE_PATH, path);
		mainActivity.setResult(RESULT_OK, resultIntent);
		mainActivity.finish();
	}

	@Override
	public void showToast(int resId, int duration) {
		ToastFactory.makeText(mainActivity, resId, duration).show();
	}

	@Override
	public void showSaveErrorDialog() {
		AppCompatDialogFragment dialog = InfoDialog.newInstance(InfoDialog.DialogType.WARNING,
				R.string.dialog_error_sdcard_text, R.string.dialog_error_save_title);
		showDialogFragmentSafely(dialog, Constants.SAVE_DIALOG_FRAGMENT_TAG);
	}

	@Override
	public void showLoadErrorDialog() {
		AppCompatDialogFragment dialog = InfoDialog.newInstance(InfoDialog.DialogType.WARNING,
				R.string.dialog_loading_image_failed_title, R.string.dialog_loading_image_failed_text);
		showDialogFragmentSafely(dialog, Constants.LOAD_DIALOG_FRAGMENT_TAG);
	}

	@Override
	public void showRequestPermissionRationaleDialog(PermissionInfoDialog.PermissionType permissionType, String[] permissions, int requestCode) {
		AppCompatDialogFragment dialog = PermissionInfoDialog.newInstance(permissionType, permissions, requestCode);
		showDialogFragmentSafely(dialog, Constants.PERMISSION_DIALOG_FRAGMENT_TAG);
	}

	@Override
	public void showRequestPermanentlyDeniedPermissionRationaleDialog() {
		AppCompatDialogFragment dialog = PermanentDenialPermissionInfoDialog.newInstance(mainActivity.getName());
		showDialogFragmentSafely(dialog, Constants.PERMISSION_DIALOG_FRAGMENT_TAG);
	}

	@Override
	public void askForPermission(String[] permissions, int requestCode) {
		ActivityCompat.requestPermissions(mainActivity, permissions, requestCode);
	}

	@Override
	public boolean isSdkAboveOrEqualM() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
	}

	@Override
	public boolean doIHavePermission(String permission) {
		return ContextCompat.checkSelfPermission(mainActivity, permission) == PackageManager.PERMISSION_GRANTED;
	}

	@Override
	public boolean isPermissionPermanentlyDenied(String[] permissions) {
		return !ActivityCompat.shouldShowRequestPermissionRationale(mainActivity, permissions[0]);
	}

	@Override
	public void finishActivity() {
		mainActivity.finish();
	}

	@Override
	public void showSaveBeforeReturnToCatroidDialog() {
		AppCompatDialogFragment dialog = SaveBeforeFinishDialog.newInstance(
				SaveBeforeFinishDialogType.BACK_TO_POCKET_CODE);
		showDialogFragmentSafely(dialog, Constants.SAVE_QUESTION_FRAGMENT_TAG);
	}

	@Override
	public void showSaveBeforeFinishDialog() {
		AppCompatDialogFragment dialog = SaveBeforeFinishDialog.newInstance(
				SaveBeforeFinishDialogType.FINISH);
		showDialogFragmentSafely(dialog, Constants.SAVE_QUESTION_FRAGMENT_TAG);
	}

	@Override
	public void showSaveBeforeNewImageDialog() {
		AppCompatDialogFragment dialog = SaveBeforeNewImageDialog.newInstance();
		showDialogFragmentSafely(dialog, Constants.SAVE_QUESTION_FRAGMENT_TAG);
	}

	@Override
	public void showSaveBeforeLoadImageDialog() {
		AppCompatDialogFragment dialog = SaveBeforeLoadImageDialog.newInstance();
		showDialogFragmentSafely(dialog, Constants.SAVE_QUESTION_FRAGMENT_TAG);
	}

	@Override
	public void showToolChangeToast(int offset, int idRes) {
		Toast toolNameToast = ToastFactory.makeText(mainActivity, idRes, Toast.LENGTH_SHORT);
		int gravity = Gravity.TOP | Gravity.CENTER;
		if (mainActivity.getResources().getConfiguration().orientation == ORIENTATION_LANDSCAPE) {
			offset = 0;
		}
		toolNameToast.setGravity(gravity, 0, offset);
		toolNameToast.show();
	}

	@Override
	public void broadcastAddPictureToGallery(Uri uri) {
		Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		mediaScanIntent.setData(uri);
		mainActivity.sendBroadcast(mediaScanIntent);
	}

	@Override
	public void restoreFragmentListeners() {
		Fragment fragment = findFragmentByTag(Constants.COLOR_PICKER_DIALOG_TAG);
		if (fragment != null) {
			setupColorPickerDialogListeners((ColorPickerDialog) fragment);
		}
	}

	@Override
	public void rateUsClicked() {
		openPlayStore(mainActivity.getPackageName());
	}

	@Override
	public void visitPocketCodeClicked() {
		openPlayStore("org.catrobat.catroid");
	}
}
