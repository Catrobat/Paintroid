package at.tugraz.ist.paintroid;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MenuActivity extends Activity implements OnClickListener {
  
  /**
   * Called when the activity is first created
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.tools_menu);
    
    Button hideButton = (Button) this.findViewById(R.id.ibtn_menu_draw);
    hideButton.setOnClickListener(this);
  }

  @Override
  public void onClick(View view) {
    switch (view.getId()) {
    case R.id.ibtn_menu_draw:
      finish();
      break;
    }
    
  }
}
