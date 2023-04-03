/*
 * Paintroid: An image manipulation application for Android.
 *  Copyright (C) 2010-2022 The Catrobat Team
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

package org.catrobat.paintroid.iotasks;

import android.app.DownloadManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import org.catrobat.paintroid.FileIO;
import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.contract.LayerContracts;
import org.catrobat.paintroid.model.Layer;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import static android.content.Context.DOWNLOAD_SERVICE;

import static org.catrobat.paintroid.common.ConstantsKt.MAX_LAYERS;
import static org.catrobat.paintroid.common.ConstantsKt.SPECIFIC_FILETYPE_SHARED_PREFERENCES_NAME;

//
//Source:	https://www.openraster.org/baseline/file-layout-spec.html
//
//Layout:
//example.ora  [considered as a folder-like object]
//        ├ mimetype
//        ├ stack.xml
//        ├ data/
//        │  ├ [image data files referenced by stack.xml, typically layer*.png]
//        │  └ [other data files, indexed elsewhere]
//        ├ Thumbnails/
//        │  └ thumbnail.png
//        └ mergedimage.png
public final class OpenRasterFileFormatConversion {
	private static final String TAG = OpenRasterFileFormatConversion.class.getSimpleName();
	private static final int COMPRESS_QUALITY = 100;
	private static final int THUMBNAIL_WIDTH = 256;
	private static final int THUMBNAIL_HEIGHT = 256;
	private static final String ORA_VERSION = "0.0.2";
	public static MainActivity mainActivity = null;

	private OpenRasterFileFormatConversion() {
		throw new AssertionError();
	}

	public static void setContext(MainActivity toSet) {
		mainActivity = toSet;
	}

	public static Uri exportToOraFile(List<LayerContracts.Layer> layers, String fileName, Bitmap bitmapAllLayers, ContentResolver resolver) throws IOException {
		Uri imageUri;
		OutputStream outputStream;
		ContentValues contentValues = new ContentValues();
		float wholeSize = 0;

		String mimeType = "image/openraster";
		byte[] mimeByteArray = mimeType.getBytes();
		byte[] xmlByteArray = getXmlStack(layers);

		for (LayerContracts.Layer current: layers) {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			current.getBitmap().compress(Bitmap.CompressFormat.PNG, COMPRESS_QUALITY, bos);
			byte[] byteArray = bos.toByteArray();
			wholeSize += byteArray.length;

			byte[] alphaByteArray = BigInteger.valueOf(current.getOpacityPercentage()).toByteArray();
			wholeSize += alphaByteArray.length;
		}

		ByteArrayOutputStream bosMerged = new ByteArrayOutputStream();
		bitmapAllLayers.compress(Bitmap.CompressFormat.PNG, COMPRESS_QUALITY, bosMerged);
		byte[] bitmapByteArray = bosMerged.toByteArray();

		Bitmap bitmapThumb = Bitmap.createScaledBitmap(bitmapAllLayers, THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT, false);
		ByteArrayOutputStream bosThumb = new ByteArrayOutputStream();
		bitmapThumb.compress(Bitmap.CompressFormat.PNG, COMPRESS_QUALITY, bosThumb);
		byte[] bitmapThumbArray = bosThumb.toByteArray();
		File imageRoot = null;

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
			//applefile has no file ending. which is important for api level 30.
			// we can't save an application file in media directory so we have to save it in downloads.
			contentValues.put(MediaStore.Files.FileColumns.DISPLAY_NAME, fileName);
			contentValues.put(MediaStore.Files.FileColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);
			contentValues.put(MediaStore.Files.FileColumns.MIME_TYPE, "application/applefile");

			imageUri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues);
		} else {

			imageRoot = Environment.getExternalStoragePublicDirectory(
					Environment.DIRECTORY_DOWNLOADS);

			if (!imageRoot.exists() && !imageRoot.mkdirs()) {
				imageRoot.mkdirs();
			}

			Uri uri = MediaStore.Files.getContentUri("external");

			contentValues.put(MediaStore.Files.FileColumns.DATA, imageRoot.getAbsolutePath() + "/" + fileName);
			contentValues.put(MediaStore.Files.FileColumns.DISPLAY_NAME, fileName);
			contentValues.put(MediaStore.Files.FileColumns.MIME_TYPE, "application/zip");
			contentValues.put(MediaStore.Files.FileColumns.MEDIA_TYPE, MediaStore.Files.FileColumns.MEDIA_TYPE_NONE);

			long date = System.currentTimeMillis();
			contentValues.put(MediaStore.MediaColumns.DATE_MODIFIED, date / 1000);

			wholeSize += xmlByteArray.length;
			wholeSize += mimeByteArray.length;
			wholeSize += bitmapByteArray.length;
			wholeSize += bitmapThumbArray.length;
			contentValues.put(MediaStore.Images.Media.SIZE, wholeSize);

			DownloadManager downloadManager = (DownloadManager) mainActivity.getBaseContext().getSystemService(DOWNLOAD_SERVICE);
			long id = downloadManager.addCompletedDownload(fileName, fileName, true, "application/zip", imageRoot.getAbsolutePath() + "/" + fileName, (long) wholeSize, true);

			imageUri = resolver.insert(uri, contentValues);

			SharedPreferences sharedPreferences = mainActivity.getSharedPreferences(SPECIFIC_FILETYPE_SHARED_PREFERENCES_NAME, 0);
			sharedPreferences.edit().putLong(imageRoot.getAbsolutePath() + "/" + fileName, id).apply();
		}

		outputStream = resolver.openOutputStream(Objects.requireNonNull(imageUri));

		ZipOutputStream streamZip = new ZipOutputStream(outputStream);
		ZipEntry mimetypeEntry = new ZipEntry("mimetype");
		mimetypeEntry.setMethod(ZipEntry.DEFLATED);

		streamZip.putNextEntry(mimetypeEntry);
		streamZip.write(mimeByteArray, 0, mimeByteArray.length);
		streamZip.closeEntry();

		streamZip.putNextEntry(new ZipEntry("stack.xml"));
		streamZip.write(xmlByteArray, 0, Objects.requireNonNull(xmlByteArray).length);
		streamZip.closeEntry();

		int counter = 0;
		for (LayerContracts.Layer current: layers) {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			current.getBitmap().compress(Bitmap.CompressFormat.PNG, COMPRESS_QUALITY, bos);
			byte[] byteArray = bos.toByteArray();

			streamZip.putNextEntry(new ZipEntry("data/layer" + counter + ".png"));
			streamZip.write(byteArray, 0, byteArray.length);
			streamZip.closeEntry();

			streamZip.putNextEntry(new ZipEntry("alpha/" + counter));
			byte[] alphaByteArray = BigInteger.valueOf(current.getOpacityPercentage()).toByteArray();
			streamZip.write(alphaByteArray, 0, alphaByteArray.length);
			streamZip.closeEntry();
			counter++;
		}

		streamZip.putNextEntry(new ZipEntry("Thumbnails/thumbnail.png"));
		streamZip.write(bitmapThumbArray, 0, bitmapThumbArray.length);
		streamZip.closeEntry();

		streamZip.putNextEntry(new ZipEntry("mergedimage.png"));
		streamZip.write(bitmapByteArray, 0, bitmapByteArray.length);
		streamZip.closeEntry();
		streamZip.close();
		outputStream.close();

		return imageUri;
	}

	private static byte[] getXmlStack(List<LayerContracts.Layer> bitmapList) {

		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("image");
			Attr attr1 = doc.createAttribute("version");
			Attr attr2 = doc.createAttribute("w");
			Attr attr3 = doc.createAttribute("h");

			attr1.setValue(ORA_VERSION);
			attr2.setValue(String.valueOf(bitmapList.get(0).getBitmap().getWidth()));
			attr3.setValue(String.valueOf(bitmapList.get(0).getBitmap().getHeight()));

			rootElement.setAttributeNode(attr1);
			rootElement.setAttributeNode(attr2);
			rootElement.setAttributeNode(attr3);
			doc.appendChild(rootElement);

			Element stack = doc.createElement("stack");
			rootElement.appendChild(stack);

			for (int i = bitmapList.size() - 1; i >= 0; i--) {
				Element layer = doc.createElement("layer");

				layer.setAttribute("name", "layer" + i);
				layer.setAttribute("src", "data/layer" + i + ".png");
				stack.appendChild(layer);
			}

			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			StreamResult result = new StreamResult(stream);

			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);

			transformer.transform(source, result);

			return stream.toByteArray();
		} catch (ParserConfigurationException e) {
			Log.e(TAG, "Could not create document.");
			return null;
		} catch (TransformerException e) {
			Log.e(TAG, "Could not transform Xml file.");
			return null;
		}
	}

	public static Uri saveOraFileToUri(List<LayerContracts.Layer> layers, Uri uri, String fileName, Bitmap bitmapAllLayers, ContentResolver resolver) throws IOException {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

			String[] projection = {MediaStore.Images.Media._ID};
			Cursor c = resolver.query(uri, projection, null, null, null);
			if (c.moveToFirst()) {
				long id = c.getLong(c.getColumnIndexOrThrow(MediaStore.Images.Media._ID));
				Uri deleteUri = ContentUris.withAppendedId(MediaStore.Downloads.EXTERNAL_CONTENT_URI, id);
				resolver.delete(deleteUri, null, null);
			} else {
				throw new AssertionError("No file to delete was found!");
			}
			c.close();
		} else {
			File file = new File(uri.getPath());
			boolean isDeleted = file.delete();
			SharedPreferences sharedPreferences = mainActivity.getSharedPreferences(SPECIFIC_FILETYPE_SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
			long id = sharedPreferences.getLong(uri.getPath(), -1);
			if (id > -1) {
				DownloadManager downloadManager = (DownloadManager) mainActivity.getBaseContext().getSystemService(DOWNLOAD_SERVICE);
				downloadManager.remove(id);
			}
			if (!isDeleted) {
				throw new AssertionError("No file to delete was found!");
			}
		}

		return exportToOraFile(layers, fileName, bitmapAllLayers, resolver);
	}

	public static BitmapReturnValue importOraFile(ContentResolver resolver, Uri uri) throws IOException {
		BitmapFactory.Options options = new BitmapFactory.Options();
		InputStream inputStream = resolver.openInputStream(uri);
		ZipInputStream zipInput = new ZipInputStream(inputStream);
		List<LayerContracts.Layer> layers = new ArrayList<>();
		ZipEntry current = zipInput.getNextEntry();

		while (current != null) {
			if (current.getName().matches("data/(.*).png")) {
				Bitmap layerBitmap = FileIO.enableAlpha(BitmapFactory.decodeStream(zipInput, null, options));

				if (layerBitmap == null) {
					throw new IOException("Cannot decode stream to bitmap!");
				}

				LayerContracts.Layer layer = new Layer(layerBitmap);

				current = zipInput.getNextEntry();
				if (current != null && current.getName().matches("alpha/(.*)")) {
					int opacityPercentage = zipInput.read();
					layer.setOpacityPercentage(opacityPercentage);
					current = zipInput.getNextEntry();
				}

				layers.add(layer);
			} else {
				current = zipInput.getNextEntry();
			}
		}

		if (layers.isEmpty() || layers.size() > MAX_LAYERS) {
			throw new IOException("Bitmap list is wrong!");
		}
		return new BitmapReturnValue(layers, null, false);
	}
}
