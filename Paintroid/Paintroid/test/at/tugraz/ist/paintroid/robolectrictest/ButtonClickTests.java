package at.tugraz.ist.paintroid.robolectrictest;

import static com.xtremelabs.robolectric.Robolectric.shadowOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import at.tugraz.ist.paintroid.MainActivity;
import at.tugraz.ist.paintroid.R;
import at.tugraz.ist.paintroid.ToolbarButton;

import com.xtremelabs.robolectric.RobolectricTestRunner;
import com.xtremelabs.robolectric.shadows.ShadowImageView;

@RunWith(RobolectricTestRunner.class)
public class ButtonClickTests {
	private MainActivity mainActivity;
	private ToolbarButton handButton;
	private ToolbarButton zoomButton;
	private ToolbarButton brushButton;
	private ToolbarButton eyedropperButton;
	private ToolbarButton magicwandButton;
	private ToolbarButton undoButton;
	private ToolbarButton redoButton;
	private ToolbarButton colorButton;
	private ToolbarButton strokeButton;

	@Before
	public void setUp() throws Exception {
		mainActivity = new MainActivity();
		mainActivity.onCreate(null);
		handButton = (ToolbarButton) mainActivity.findViewById(R.id.ibtn_handTool);
		zoomButton = (ToolbarButton) mainActivity.findViewById(R.id.ibtn_zoomTool);
		brushButton = (ToolbarButton) mainActivity.findViewById(R.id.ibtn_brushTool);
		eyedropperButton = (ToolbarButton) mainActivity.findViewById(R.id.ibtn_eyeDropperTool);
		magicwandButton = (ToolbarButton) mainActivity.findViewById(R.id.ibtn_magicWandTool);
		undoButton = (ToolbarButton) mainActivity.findViewById(R.id.ibtn_undoTool);
		redoButton = (ToolbarButton) mainActivity.findViewById(R.id.ibtn_redoTool);
		colorButton = (ToolbarButton) mainActivity.findViewById(R.id.ibtn_Color);
		strokeButton = (ToolbarButton) mainActivity.findViewById(R.id.ibtn_brushStroke);
	}

	@Test
	public void colorPickerButtonHasStandardColor() throws Exception {
		final ShadowImageView shadowButton = shadowOf(colorButton);
		assertThat(shadowButton.getBackgroundResourceId(), equalTo(R.color.std_color));
	}

	@Test
	public void initialSelectedButtonIsBrush() throws Exception {
		final ShadowImageView shadowButton = shadowOf(brushButton);
		assertThat(shadowButton.getBackgroundResourceId(), equalTo(R.drawable.ic_brush_active));
		assertTrue(onlyOneToolbarButtonActive());
	}

	@Test
	public void clickOnHandTool() throws Exception {
		handButton.performClick();
		final ShadowImageView shadowButton = shadowOf(handButton);
		assertThat(shadowButton.getBackgroundResourceId(), equalTo(R.drawable.ic_hand_active));
		assertTrue(onlyOneToolbarButtonActive());
	}

	@Test
	public void clickOnZoomTool() throws Exception {
		zoomButton.performClick();
		final ShadowImageView shadowButton = shadowOf(zoomButton);
		assertThat(shadowButton.getBackgroundResourceId(), equalTo(R.drawable.ic_hand_active));
		assertTrue(onlyOneToolbarButtonActive());
	}

	@Test
	public void clickOnEyedropperTool() throws Exception {
		eyedropperButton.performClick();
		final ShadowImageView shadowButton = shadowOf(eyedropperButton);
		assertThat(shadowButton.getBackgroundResourceId(), equalTo(R.drawable.ic_hand_active));
		assertTrue(onlyOneToolbarButtonActive());
	}

	private boolean onlyOneToolbarButtonActive() {
		int i = 0;
		if (shadowOf(handButton).getBackgroundResourceId() == R.drawable.ic_hand_active)
			i++;
		if (shadowOf(zoomButton).getBackgroundResourceId() == R.drawable.ic_zoom_active)
			i++;
		if (shadowOf(brushButton).getBackgroundResourceId() == R.drawable.ic_brush_active)
			i++;
		if (shadowOf(eyedropperButton).getBackgroundResourceId() == R.drawable.ic_eyedropper_active)
			i++;
		if (shadowOf(magicwandButton).getBackgroundResourceId() == R.drawable.ic_magicwand_active)
			i++;
		if (i == 1)
			return true;
		else
			return false;
	}
}
