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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.AdapterView.OnItemClickListener;
import at.tugraz.ist.paintroid.helper.ToolButtonAdapter;
import at.tugraz.ist.paintroid.helper.ToolButton;

public class ToolMenuActivity extends Activity implements OnItemClickListener {
  
  protected ToolButtonAdapter buttonAdapter;
  
  public enum ButtonEnum {
    BRUSH, CURSOR, SCROLL, ZOOM, PIPETTE, MAGIC, UNDO, REDO,
    MIDDLEPOINT, FLOATINGBOX, IMPORTPNG
  }
  
  /**
   * Called when the activity is first created
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.tools_menu);
    
    GridView gridview = (GridView) findViewById(R.id.gridview_tools_menu);
    buttonAdapter = new ToolButtonAdapter(this);
    gridview.setAdapter(buttonAdapter);
    gridview.setOnItemClickListener(this);
    gridview.setColumnWidth(95);
    gridview.setGravity(Gravity.CENTER);
  }

  @Override
  public void onItemClick(AdapterView<?> adapterView, View button, int position, long id) {
    ToolButton toolButton = buttonAdapter.getToolButton(position);
    Intent resultIntent = new Intent(); 
    resultIntent.putExtra("SelectedTool", toolButton.buttonId.ordinal());
    getParent().setResult(Activity.RESULT_OK, resultIntent);
    this.finish();
  }
}
