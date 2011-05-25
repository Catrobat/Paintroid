package at.tugraz.ist.paintroid;

import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;

public class MenuTabActivity extends TabActivity {
  
  /**
   * Called when the activity is first created
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.menu_tab);

    Resources res = getResources(); // Resource object to get Drawables
    TabHost tabHost = getTabHost();  // The activity TabHost
    TabHost.TabSpec spec;  // Resusable TabSpec for each tab
    Intent intent;  // Reusable Intent for each tab

    // Create an Intent to launch an Activity for the tab (to be reused)
    intent = new Intent().setClass(this, FileActivity.class);

    // Initialize a TabSpec for each tab and add it to the TabHost
    spec = tabHost.newTabSpec("file").setIndicator("File",
                      res.getDrawable(R.drawable.ic_tab_file))
                  .setContent(intent);
    tabHost.addTab(spec);

    // Do the same for the other tabs
    intent = new Intent().setClass(this, MenuActivity.class);
    spec = tabHost.newTabSpec("menu").setIndicator("Draw",
                      res.getDrawable(R.drawable.ic_tab_menu))
                  .setContent(intent);
    tabHost.addTab(spec);

    tabHost.setCurrentTab(1);
  }
  
  public void finishFromChild(Activity child) {
    finish();
    overridePendingTransition(R.anim.push_down_in, R.anim.push_down_out);
  }
}
