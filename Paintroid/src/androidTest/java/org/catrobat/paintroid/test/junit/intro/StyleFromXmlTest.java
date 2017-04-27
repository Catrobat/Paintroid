/**
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2015 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 * <p/>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid.test.junit.intro;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.content.ContextCompat;

import com.getkeepsafe.taptargetview.TapTarget;

import org.catrobat.paintroid.R;
import org.catrobat.paintroid.Session;
import org.catrobat.paintroid.intro.TapTargetStyle;
import org.catrobat.paintroid.test.junit.ui.IntroTestBase;
import org.catrobat.paintroid.test.utils.PrivateAccess;
import org.junit.Before;
import org.junit.Test;
import org.junit.internal.builders.NullBuilder;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.catrobat.paintroid.Session.IS_FIRST_TIME_LAUNCH;
import static org.catrobat.paintroid.Session.PREF_NAME;
import static org.catrobat.paintroid.Session.PRIVATE_MODE;



@RunWith(AndroidJUnit4.class)
public class StyleFromXmlTest extends IntroTestBase{


    @Before
    public void setUp() throws NoSuchFieldException, IllegalAccessException {
        super.setUp();
    }

    @Test
    public void testIntroHeaderStyle() {
        TapTargetStyle headerStyle = TapTargetStyle.HEADER_STYLE;
        assertEquals("Text Size not matching", headerStyle.getTextSize(), 24);

        int color = ContextCompat.getColor(context, R.color.color_chooser_white);
        assertEquals("Header Text Color not matching",color, headerStyle.getTextColor());

        Typeface expectedTypeface = Typeface.create("sans-serif", Typeface.NORMAL);
        assertEquals("Typeface not matching",expectedTypeface, headerStyle.getTypeface());

    }

    @Test
    public void testIntroTextStyle() {
        TapTargetStyle textStyle = TapTargetStyle.TEXT_STYLE;
        assertEquals("Text Size not matching", textStyle.getTextSize(), 16);

        int color = ContextCompat.getColor(context, R.color.color_chooser_white);
        assertEquals("Header Text Color not matching",color, textStyle.getTextColor());

        Typeface expectedTypeface = Typeface.create("sans-serif", Typeface.NORMAL);
        assertEquals("Typeface not matching",expectedTypeface, textStyle.getTypeface());
    }
}
