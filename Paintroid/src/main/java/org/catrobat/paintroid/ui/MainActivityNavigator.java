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

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.view.Gravity;
import android.widget.Toast;

import org.catrobat.paintroid.FileIO;
import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.WelcomeActivity;
import org.catrobat.paintroid.colorpicker.ColorPickerDialog;
import org.catrobat.paintroid.colorpicker.OnColorPickedListener;
import org.catrobat.paintroid.common.Constants;
import org.catrobat.paintroid.common.MainActivityConstants;
import org.catrobat.paintroid.common.MainActivityConstants.ActivityRequestCode;
import org.catrobat.paintroid.contract.MainActivityContracts;
import org.catrobat.paintroid.dialog.AboutDialog;
import org.catrobat.paintroid.dialog.FeedbackDialog;
import org.catrobat.paintroid.dialog.ImportImageDialog;
import org.catrobat.paintroid.dialog.IndeterminateProgressDialog;
import org.catrobat.paintroid.dialog.InfoDialog;
import org.catrobat.paintroid.dialog.JpgInfoDialog;
import org.catrobat.paintroid.dialog.LikeUsDialog;
import org.catrobat.paintroid.dialog.OverwriteDialog;
import org.catrobat.paintroid.dialog.PermanentDenialPermissionInfoDialog;
import org.catrobat.paintroid.dialog.PermissionInfoDialog;
import org.catrobat.paintroid.dialog.PngInfoDialog;
import org.catrobat.paintroid.dialog.RateUsDialog;
import org.catrobat.paintroid.dialog.SaveBeforeFinishDialog;
import org.catrobat.paintroid.dialog.SaveBeforeFinishDialog.SaveBeforeFinishDialogType;
import org.catrobat.paintroid.dialog.SaveBeforeLoadImageDialog;
import org.catrobat.paintroid.dialog.SaveBeforeNewImageDialog;
import org.catrobat.paintroid.dialog.SaveInformationDialog;
import org.catrobat.paintroid.tools.ToolReference;
import org.catrobat.paintroid.ui.fragments.CatroidMediaGalleryFragment;

import androidx.appcompat.app.AppCompatDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import static android.app.Activity.RESULT_OK;
import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;

import static org.catrobat.paintroid.common.MainActivityConstants.PERMISSION_EXTERNAL_STORAGE_SAVE_COPY;

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
			ColorPickerDialog dialog = ColorPickerDialog.newInstance(toolReference.get().getDrawPaint().getColor());
			setupColorPickerDialogListeners(dialog);
			showDialogFragmentSafely(dialog, Constants.COLOR_PICKER_DIALOG_TAG);
		}
	}

	@Override
	public void showCatroidMediaGallery() {
		if (findFragmentByTag(Constants.CATROID_MEDIA_GALLERY_FRAGMENT_TAG) == null) {
			CatroidMediaGalleryFragment fragment = new CatroidMediaGalleryFragment();
			fragment.setMediaGalleryListener(new CatroidMediaGalleryFragment.MediaGalleryListener() {
				@Override
				public void bitmapLoadedFromSource(Bitmap loadedBitmap) {
					mainActivity.getPresenter().bitmapLoadedFromSource(loadedBitmap);
				}

				@Override
				public void showProgressDialog() {
					showIndeterminateProgressDialog();
				}

				@Override
				public void dissmissProgressDialog() {
					dismissIndeterminateProgressDialog();
				}
			});
			showFragment(fragment, Constants.CATROID_MEDIA_GALLERY_FRAGMENT_TAG);
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

	private void showDialogFragmentSafely(DialogFragment dialog, String tag) {
		FragmentManager fragmentManager = mainActivity.getSupportFragmentManager();
		if (!fragmentManager.isStateSaved()) {
			dialog.show(fragmentManager, tag);
		}
	}

	private Fragment findFragmentByTag(String tag) {
		return mainActivity.getSupportFragmentManager().findFragmentByTag(tag);
	}

	private void setupColorPickerDialogListeners(ColorPickerDialog dialog) {
		dialog.addOnColorPickedListener(new OnColorPickedListener() {
			@Override
			public void colorChanged(int color) {
				toolReference.get().changePaintColor(color);
				mainActivity.getPresenter().setBottomNavigationColor(color);
			}
		});

		dialog.setBitmap(mainActivity.getPresenter().getBitmap());
	}

	private void setupCatroidMediaGalleryListeners(CatroidMediaGalleryFragment dialog) {
		dialog.setMediaGalleryListener(new CatroidMediaGalleryFragment.MediaGalleryListener() {
			@Override
			public void bitmapLoadedFromSource(Bitmap loadedBitmap) {
				mainActivity.getPresenter().bitmapLoadedFromSource(loadedBitmap);
			}

			@Override
			public void showProgressDialog() {
				showIndeterminateProgressDialog();
			}

			@Override
			public void dissmissProgressDialog() {
				dismissIndeterminateProgressDialog();
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
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
		mainActivity.startActivityForResult(intent, requestCode);
	}

	@Override
	public void startImportImageActivity(@ActivityRequestCode int requestCode) {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("image/*");
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
		mainActivity.startActivityForResult(intent, requestCode);
	}

	@Override
	public void startWelcomeActivity(@ActivityRequestCode int requestCode) {
		Intent intent = new Intent(mainActivity.getApplicationContext(), WelcomeActivity.class);
		intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
		mainActivity.startActivityForResult(intent, requestCode);
	}

	@Override
	public void startShareImageActivity(Bitmap bitmap) {
		Uri uri = FileIO.saveBitmapToCache(bitmap, mainActivity);
		if (uri != null) {
			Intent shareIntent = new Intent();
			shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
			shareIntent.setDataAndType(uri, mainActivity.getContentResolver().getType(uri));
			shareIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
			shareIntent.setAction(Intent.ACTION_SEND);
			String chooserTitle = mainActivity.getResources().getString(R.string.share_image_via_text);
			mainActivity.startActivity(Intent.createChooser(shareIntent, chooserTitle));
		}
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
	public void showOverwriteDialog(int permissionCode) {
		OverwriteDialog overwriteDialog = OverwriteDialog.newInstance(permissionCode);
		overwriteDialog.show(mainActivity.getSupportFragmentManager(), Constants.OVERWRITE_INFORMATION_DIALOG_TAG);
	}

	@Override
	public void showPngInformationDialog() {
		PngInfoDialog pngInfoDialog = PngInfoDialog.newInstance();
		pngInfoDialog.show(mainActivity.getSupportFragmentManager(), Constants.PNG_INFORMATION_DIALOG_TAG);
	}

	@Override
	public void showJpgInformationDialog() {
		JpgInfoDialog jpgInfoDialog = JpgInfoDialog.newInstance();
		jpgInfoDialog.show(mainActivity.getSupportFragmentManager(), Constants.JPG_INFORMATION_DIALOG_TAG);
	}

	@Override
	public void sendFeedback() {
		Intent intent = new Intent(Intent.ACTION_SENDTO);
		Uri data = Uri.parse("mailto:support-paintroid@catrobat.org");
		intent.setData(data);
		mainActivity.startActivity(intent);
	}

	@Override
	public void showImageImportDialog() {
		ImportImageDialog importImage = ImportImageDialog.newInstance();
		importImage.show(mainActivity.getSupportFragmentManager(), Constants.ABOUT_DIALOG_FRAGMENT_TAG);
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
	public boolean isSdkAboveOrEqualQ() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q;
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

	@SuppressLint("VisibleForTests")
	@Override
	public void showSaveImageInformationDialogWhenStandalone(int permissionCode, int imageNumber, boolean isExport) {
		Uri uri = mainActivity.model.getSavedPictureUri();
		if (uri != null && permissionCode != PERMISSION_EXTERNAL_STORAGE_SAVE_COPY) {
			FileIO.parseFileName(uri, mainActivity.getContentResolver());
		}

		if (!isExport && mainActivity.model.isOpenedFromCatroid()) {
			FileIO.filename = "image" + imageNumber;
			FileIO.compressFormat = Bitmap.CompressFormat.PNG;
			FileIO.ending = ".png";
			FileIO.catroidFlag = true;
			mainActivity.getPresenter().switchBetweenVersions(permissionCode);
			return;
		}

		boolean isStandard = false;
		if (permissionCode == MainActivityConstants.PERMISSION_EXTERNAL_STORAGE_SAVE_COPY) {
			isStandard = true;
		}

		SaveInformationDialog saveInfodialog = SaveInformationDialog.newInstance(permissionCode, imageNumber, isStandard);
		saveInfodialog.show(mainActivity.getSupportFragmentManager(), Constants.SAVE_INFORMATION_DIALOG_TAG);
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

		fragment = findFragmentByTag(Constants.CATROID_MEDIA_GALLERY_FRAGMENT_TAG);
		if (fragment != null) {
			setupCatroidMediaGalleryListeners((CatroidMediaGalleryFragment) fragment);
		}
	}

	@Override
	public void rateUsClicked() {
		openPlayStore(mainActivity.getPackageName());
	}
}
