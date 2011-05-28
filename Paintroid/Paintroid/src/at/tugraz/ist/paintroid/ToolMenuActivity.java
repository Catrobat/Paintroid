package at.tugraz.ist.paintroid;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class ToolMenuActivity extends Activity implements OnClickListener {
  
  /**
   * Called when the activity is first created
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.tools_menu);
    
    ImageButton drawButton = (ImageButton) this.findViewById(R.id.ibtn_menu_draw);
    drawButton.setOnClickListener(this);
    
    ImageButton chooseButton = (ImageButton) this.findViewById(R.id.ibtn_menu_choose);
    chooseButton.setOnClickListener(this);
    
    ImageButton magicButton = (ImageButton) this.findViewById(R.id.ibtn_menu_magic);
    magicButton.setOnClickListener(this);
    
    ImageButton middlepointButton = (ImageButton) this.findViewById(R.id.ibtn_menu_middlepoint);
    middlepointButton.setOnClickListener(this);
  }

  @Override
  public void onClick(View view) {
    switch (view.getId()) {
    case R.id.ibtn_menu_draw:
      
      break;
    }
    finish();
  }
}
