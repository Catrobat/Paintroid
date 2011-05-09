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

package at.tugraz.ist.paintroid.test;

import java.util.Locale;

import android.content.res.Configuration;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import at.tugraz.ist.paintroid.MainActivity;
import com.jayway.android.robotium.solo.Solo;


public class HelpTests extends ActivityInstrumentationTestCase2<MainActivity> {

	private Solo solo;
	private MainActivity mainActivity;
	
	final int COLORPICKER = 0;
	final int STROKE = 0;
	final int HAND = 1;
	final int MAGNIFIY = 2;
	final int BRUSH = 3;
	final int EYEDROPPER = 4;
	final int WAND = 5;
	final int UNDO = 6;
	final int REDO = 7;
	final int FILE = 8;
	
	public HelpTests() {
		super("at.tugraz.ist.paintroid", MainActivity.class);
	}

	public void setUp() throws Exception {
		solo = new Solo(getInstrumentation(), getActivity());
		String languageToLoad_before  = "en";
		Locale locale_before = new Locale(languageToLoad_before);
		Locale.setDefault(locale_before);
		
		Configuration config_before = new Configuration();
		config_before.locale = locale_before;
		
		mainActivity = (MainActivity) solo.getCurrentActivity();
		mainActivity.getBaseContext().getResources().updateConfiguration(config_before, mainActivity.getBaseContext().getResources().getDisplayMetrics());
	}
	
	public void testHelpScroll(){
		ImageButton ibutton = solo.getImageButton(HAND);
		solo.clickOnImageButton(HAND);
		solo.clickLongOnView(ibutton, 10000);
		String help_text = solo.getText(1).getText().toString();
		Log.d("PaintroidTest", "Text" + help_text);
		assertEquals(help_text, "I\'m the scroll hand. With me you can scroll in your picture.");
		solo.clickOnButton("Done");
		
	}
	
	public void testHelpZoom(){
		ImageButton ibutton = solo.getImageButton(MAGNIFIY);
		solo.clickLongOnView(ibutton, 10000);
	    String help_text = solo.getText(1).getText().toString();
		assertEquals(help_text, "I\'m the magnifying glass. With me you can zoom in and out just move your finger over the screen");
		solo.clickOnButton("Done");
		
	}
	
	public void testHelpBrush(){
		ImageButton ibutton = solo.getImageButton(BRUSH);
	 	solo.clickLongOnView(ibutton, 10000);
	    String help_text = solo.getText(1).getText().toString();
		assertEquals(help_text, "I\'m the brush. With me you can draw on your picture");
		solo.clickOnButton("Done");
		
	}
	
	public void testHelpEyedropper(){
		ImageButton ibutton = solo.getImageButton(EYEDROPPER);
		solo.clickLongOnView(ibutton, 10000);
	    String help_text = solo.getText(1).getText().toString();
		assertEquals(help_text, "I\'m the eyedropper. I can help you to select a color from your picture");
		solo.clickOnButton("Done");
		
	}
	
	public void testHelpUndo(){
		ImageButton ibutton = solo.getImageButton(WAND);
		solo.clickLongOnView(ibutton, 10000);
	    String help_text = solo.getText(1).getText().toString();
		assertEquals(help_text, "I\'m the magic wand. I can change one color in the whole picture to your selected color");
		solo.clickOnButton("Done");
		
	}
	
	public void testHelpRedo(){
		ImageButton ibutton = solo.getImageButton(UNDO);
		solo.clickLongOnView(ibutton, 10000);
	    String help_text = solo.getText(1).getText().toString();
		assertEquals(help_text, "I\'m the undo arrow. I can make your latest changes undone.");
		solo.clickOnButton("Done");
		
	}
	
	public void testHelpMagicWand(){
		ImageButton ibutton = solo.getImageButton(REDO);
		solo.clickLongOnView(ibutton, 10000);
	    String help_text = solo.getText(1).getText().toString();
		assertEquals(help_text, "I\'m the redo arrow. I can redraw your latest undos.");
		solo.clickOnButton("Done");
		
	}
	
	public void testHelpFile(){
		ImageButton ibutton = solo.getImageButton(FILE);
		solo.clickLongOnView(ibutton, 10000);
	    String help_text = solo.getText(1).getText().toString();
		assertEquals(help_text, "I\'m the file manager. I can load, save and create a new picture.");
		solo.clickOnButton("Done");
		
	}
	
	public void testHelpColorpicker(){
		Button ibutton = solo.getButton(COLORPICKER);
		solo.clickLongOnView(ibutton, 10000);
	    String help_text = solo.getText(1).getText().toString();
		assertEquals(help_text, "I\'m the color picker. I can help you to choose the color you want.");
		solo.clickOnButton("Done");
		
	}
	
	public void testShapePicker(){
		ImageButton ibutton = solo.getImageButton(STROKE);
		solo.clickLongOnView(ibutton, 10000);
	    String help_text = solo.getText(1).getText().toString();
		assertEquals(help_text, "I\'m the stroke\'n\'shape picker. I can help you to choose the shape and size of your brush.");
		solo.clickOnButton("Done");
		
	}
	
	@Override
  public void tearDown() throws Exception {
	  solo.clickOnMenuItem("More");
    solo.clickInList(0);
//    solo.clickOnMenuItem("Quit");
    try {
      solo.finalize();
    } catch (Throwable e) {
      e.printStackTrace();
    }
    getActivity().finish();
    super.tearDown();
  }
}
