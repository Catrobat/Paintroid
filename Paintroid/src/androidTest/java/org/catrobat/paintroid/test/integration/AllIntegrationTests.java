package org.catrobat.paintroid.test.integration;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.catrobat.paintroid.test.integration.dialog.*;
import org.catrobat.paintroid.test.integration.tools.*;

public class AllIntegrationTests extends TestSuite {

    public static Test suite() throws Exception {

        Class[] dialogTestClasses = {
                BrushPickerIntegrationTest.class,
                ColorDialogIntegrationTest.class,
                IndeterminateProgressDialogIntegrationTest.class
        };

        //TODO: Add following test classes when refactored
        //ActivityOpenedFromPocketCodeNewImageTest.class,
        //ActivityOpenedFromPocketCodeTest.class,
        //LayerIntegrationTest.class,
        //UndoRedoIntegrationTest.class,
        Class [] integrationTestClasses = {
                BitmapIntegrationTest.class,
                LandscapeTest.class,
                MainActivityIntegrationTest.class,
                MenuFileActivityIntegrationTest.class,
                ScrollingViewIntegrationTest.class,
                StatusbarIntegrationTest.class,
                ToolOnBackPressedTests.class,
                ToolSelectionIntegrationTest.class
        };

        //TODO: Add following test classes when refactored
        //TransformToolIntegrationTest.class,
        //RotationToolIntegrationTest.class,
        //FlipToolIntegrationTest.class,
        //FillToolIntegrationTest.class
        Class [] toolTestClasses = {
                EraserToolIntegrationTest.class,
                ImportPngToolTest.class,
                LineToolIntegrationTest.class,
                MoveZoomIntegrationTest.class,
                RectangleFillToolIntegrationTest.class,
        };

        Class [] toolTestClasses2 = {
                TextToolIntegrationTest.class,
                StampToolIntegrationTest.class
        };



        TestSuite suite = new TestSuite( );

        suite.addTest(new TestSuite(toolTestClasses2));
        suite.addTest(new TestSuite(dialogTestClasses));
        suite.addTest(new TestSuite(integrationTestClasses));
        suite.addTest(new TestSuite(toolTestClasses));





        // How to call a single test
        //suite.addTest(createTest(org.catrobat.paintroid.test.integration.dialog.BrushPickerIntegrationTest.class, "testBrushPickerDialogKeepStrokeOnToolChange"));

        return suite;

    }

}
