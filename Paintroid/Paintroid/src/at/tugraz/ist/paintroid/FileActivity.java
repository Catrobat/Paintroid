/*    Catroid: An on-device graphical programming language for Android devices
 *    Copyright (C) 2010  Catroid development team
 *    (<http://code.google.com/p/catroid/wiki/Credits>)
 *
 *    This program is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package at.tugraz.ist.paintroid;

import at.tugraz.ist.paintroid.R;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import at.tugraz.ist.paintroid.dialog.DialogAbout;
import at.tugraz.ist.paintroid.dialog.DialogOverwriteFile;
import at.tugraz.ist.paintroid.dialog.DialogSaveFileName;

/**
 * This activity is responsible for creating, loading and saving of images.
 *
 * Returns an intent which contains following elements:  
 *   "IntentReturnValue": Chosen event in {@link FileActivity} (e.g. SAVE,NEW,...)
 *   "UriString": Path to bitmap when selected on the sdcard or NULL
 * 
 * Status: refactored 20.02.2011
 * @author PaintroidTeam
 * @version 0.6.4b
 */
public class FileActivity extends Activity implements OnClickListener{
	
	// Buttons from FileMenu-panel
	Button file_button_new;
	Button file_button_load;
	Button file_button_save;
	Button file_button_cancel;
	
	// Request code for the gallery browser
	//  0 if no images was selected
	// !0 some image was selected
	private static int IMAGE_TO_LOAD = 0;
	
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
    }
      
   
    /**
     * Handle {@link FileActivity}-buttons onClick-events
     */
	@Override
	public void onClick(View v) {
				
		switch(v.getId()) {

		// Create new empty drawing panel in the workspace (returns EMPTY Uri to Main)
		case R.id.btn_file_New:
			
			resultIntent.putExtra("IntentReturnValue", "NEW");
		    resultIntent.putExtra("UriString", Uri.EMPTY);
		    setResult(Activity.RESULT_OK, resultIntent); //Set FileActivity result
		    this.finish();
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
			
			startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), IMAGE_TO_LOAD);
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
	      String imageFilePath =  FileIO.getRealPathFromURI(getContentResolver(), selectedGalleryImage);

	      resultIntent.putExtra("IntentReturnValue", "LOAD");
	      resultIntent.putExtra("UriString", imageFilePath);
	      resultIntent.putExtra("GaleryUri", selectedGalleryImage.toString());
	      setResult(Activity.RESULT_OK, resultIntent);
	      this.finish();
	      
	   } else {
		  Log.d("PAINTROID", "FileActivity: Error! No Picture loaded");
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
		getMenuInflater().inflate(R.menu.main_menu, menu);
		menu.removeItem(R.id.item_Clear); 
		menu.removeItem(R.id.item_Reset);
		menu.removeItem(R.id.item_Middlepoint);
		menu.removeItem(R.id.item_FloatingBox);
		menu.removeItem(R.id.item_ImportPng);
		return true;
	}
       
    
	/**
     * Handle options menu button events
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	
    	switch (item.getItemId()) {
       
        case R.id.item_Quit:
        	this.finish();
            return true; 
            
        case R.id.item_About:
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
     * @param saveFileName new name of the file to save
     */
	public void setSaveName(String saveFileName) {
		
		Log.d("PAINTROID", "Get Filename to save: " + saveFileName);
	
		resultIntent.putExtra("IntentReturnValue", "SAVE");
	    resultIntent.putExtra("UriString", saveFileName);	
	    setResult(Activity.RESULT_OK, resultIntent); 
	    this.finish();
	}


	public void startWarningOverwriteDialog(String filename){
		DialogOverwriteFile overwriteDialog = new DialogOverwriteFile(this, filename);
		overwriteDialog.show();
	}
      
}