package org.catrobat.paintroid.test.integration;

import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import com.robotium.solo.Solo;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.R;

import java.util.Arrays;

//tested on SAMSUNG Galaxy s5
//make sure that your PhoneLanguage is Arabic
public class ArabicNumberFormatTest extends ActivityInstrumentationTestCase2<MainActivity> {
    public ArabicNumberFormatTest() {
        super(MainActivity.class);
    }

    private Solo solo;
    private TextView text_OfStrokeWidth;
    private String valueOfStrokeWidth;
    final int RGB_TAB_INDEX = 2;

    private String[] HindiNumbers = new String[]{"٠", "١", "٢", "٣", "٤", "٥", "٦", "٧", "٨", "٩", "١٠",
            "١١", "١٢", "١٣", "١٤", "١٥", "١٦", "١٧", "١٨", "١٩", "٢٠",
            "٢١", "٢٢", "٢٣", "٢٤", "٢٥", "٢٦", "٢٧", "٢٨", "٢٩", "٣٠",
            "٣١", "٣٢", "٣٣", "٣٤", "٣٥", "٣٦", "٣٧", "٣٨", "٣٩", "٤٠",
            "٤١", "٤٢", "٤٣", "٤٤", "٤٥", "٤٦", "٤٧", "٤٨", "٤٩", "٥٠",
            "٥١", "٥٢", "٥٣", "٥٤", "٥٥", "٥٦", "٥٧", "٥٨", "٥٩", "٦٠",
            "٦١", "٦٢", "٦٣", "٦٤", "٦٥", "٦٦", "٦٧", "٦٨", "٦٩", "٧٠",
            "٧١", "٧٢", "٧٣", "٧٤", "٧٥", "٧٦", "٧٧", "٧٨", "٧٩", "٨٠",
            "٨١", "٨٢", "٨٣", "٨٤", "٨٥", "٨٦", "٨٧", "٨٨", "٨٩", "٩٠",
            "٩١", "٩٢", "٩٣", "٩٤", "٩٥", "٩٦", "٩٧", "٩٨", "٩٩", "١٠٠"};

    @Override
    public void setUp() throws Exception {
        solo = new Solo(getInstrumentation(), getActivity());
    }

    @Override
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }

    public void testHindiNumberFormat_inArabicLanguage() throws Exception {
        //BrushStrokeWidthSizeText_Test//
        solo.assertCurrentActivity("the current Activity is not the MainActivity", MainActivity.class);
        solo.sleep(500);
        solo.clickOnView(getActivity().findViewById(R.id.tools_brush));
        text_OfStrokeWidth = (TextView) getActivity().findViewById(R.id.stroke_width_width_text);
        solo.clickOnView(text_OfStrokeWidth);
        assertTrue(text_OfStrokeWidth.isShown());
        assertTrue(text_OfStrokeWidth.getVisibility() == View.VISIBLE);
        solo.sleep(500);
        valueOfStrokeWidth = String.valueOf(text_OfStrokeWidth.getText());
        assertTrue(Arrays.asList(HindiNumbers).contains(valueOfStrokeWidth));
        solo.goBack();
        //TransformSizeTextTest//
        solo.clickOnView(getActivity().findViewById(R.id.tools_transform));
        solo.sleep(500);
        TextView transformSizeText = (TextView) getActivity().findViewById(R.id.transform_size_text);
        solo.clickOnView(transformSizeText);
        assertTrue(transformSizeText.isShown());
        assertTrue(transformSizeText.getVisibility() == View.VISIBLE);
        solo.sleep(500);
        String transformSize = String.valueOf(transformSizeText.getText());
        assertTrue(Arrays.asList(HindiNumbers).contains(transformSize));
        solo.goBack();
        //LineStrokeWidthSizeTextTest//
        solo.clickOnView(getActivity().findViewById(R.id.tools_line));
        solo.sleep(900);
        solo.clickOnView(getActivity().findViewById(R.id.tools_line));
        solo.clickOnView(text_OfStrokeWidth);
        assertTrue(text_OfStrokeWidth.isShown());
        assertTrue(text_OfStrokeWidth.getVisibility() == View.VISIBLE);
        solo.sleep(500);
        assertTrue(Arrays.asList(HindiNumbers).contains(valueOfStrokeWidth));
        solo.goBack();
        //FillColorToleranceSizeTextTest//
        solo.clickOnView(getActivity().findViewById(R.id.tools_fill));
        solo.sleep(900);
        solo.clickOnView(getActivity().findViewById(R.id.tools_fill));
        TextView text_ofColorTolerance = (TextView) getActivity().findViewById(R.id.fill_tool_dialog_color_tolerance_input);
        solo.clickOnView(text_ofColorTolerance);
        assertTrue(text_ofColorTolerance.isShown());
        assertTrue(text_ofColorTolerance.getVisibility() == View.VISIBLE);
        solo.sleep(500);
        String valueOfColorTolerance = String.valueOf(text_ofColorTolerance.getText());
        assertTrue(Arrays.asList(HindiNumbers).contains(valueOfColorTolerance));
        solo.goBack();
        //EraserStrokeWidthSizeTextTest//
        solo.clickOnView(getActivity().findViewById(R.id.tools_stamp));
        solo.sleep(500);
        solo.clickOnView(getActivity().findViewById(R.id.tools_eraser));
        solo.sleep(500);
        solo.clickOnView(getActivity().findViewById(R.id.tools_eraser));
        solo.clickOnView(text_OfStrokeWidth);
        assertTrue(text_OfStrokeWidth.isShown());
        assertTrue(text_OfStrokeWidth.getVisibility() == View.VISIBLE);
        solo.sleep(500);
        assertTrue(Arrays.asList(HindiNumbers).contains(valueOfStrokeWidth));
        solo.goBack();
        solo.goBack();
        //open Color Chooser//
        solo.clickOnView(getActivity().findViewById(R.id.btn_top_color));
        assertTrue(getActivity().findViewById(R.id.btn_top_color).getVisibility() == View.VISIBLE);
        assertTrue(getActivity().findViewById(R.id.btn_top_color).isShown());
        TabHost tabHost = (TabHost) solo.getView(R.id.colorview_tabColors);
        TabWidget colorTabWidget = tabHost.getTabWidget();
        solo.clickOnView(colorTabWidget.getChildAt(RGB_TAB_INDEX), true);
        solo.sleep(900);
        TextView red = (TextView) solo.getView(R.id.rgb_red_value);
        TextView green = (TextView) solo.getView(R.id.rgb_green_value);
        TextView blue = (TextView) solo.getView(R.id.rgb_blue_value);
        TextView alpha = (TextView) solo.getView(R.id.rgb_alpha_value);
        String redValue = String.valueOf(red.getText());
        String greenValue = String.valueOf(green.getText());
        String blueValue = String.valueOf(blue.getText());
        String alphaValue = String.valueOf(alpha.getText());
        assertTrue(Arrays.asList(HindiNumbers).contains(redValue));
        assertTrue(Arrays.asList(HindiNumbers).contains(greenValue));
        assertTrue(Arrays.asList(HindiNumbers).contains(blueValue));
        assertTrue(Arrays.asList(HindiNumbers).contains(alphaValue));
        tearDown();
    }
}
