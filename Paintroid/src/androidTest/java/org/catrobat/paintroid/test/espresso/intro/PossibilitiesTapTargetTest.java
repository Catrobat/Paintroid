/**
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2015 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid.test.espresso.intro;

import org.catrobat.paintroid.R;
import org.catrobat.paintroid.test.espresso.intro.util.WelcomeActivityIntentsTestRule;
import org.catrobat.paintroid.test.espresso.util.IntroUtils;
import org.catrobat.paintroid.test.utils.SystemAnimationsRule;
import org.catrobat.paintroid.tools.ToolType;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;

import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.changeIntroPage;
import static org.catrobat.paintroid.test.espresso.util.IntroUtils.getPageIndexFromLayout;
import static org.catrobat.paintroid.test.espresso.util.IntroUtils.introClickToolAndCheckView;
import static org.junit.runners.Parameterized.Parameter;
import static org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class PossibilitiesTapTargetTest {

	@Rule
	public WelcomeActivityIntentsTestRule activityRule = new WelcomeActivityIntentsTestRule(false);

	@Rule
	public SystemAnimationsRule systemAnimationsRule = new SystemAnimationsRule();
	@Parameter
	public ToolType toolType;

	@Parameters(name = "{0}")
	public static Iterable<ToolType> data() {
		return Arrays.asList(
				ToolType.UNDO,
				ToolType.REDO,
				ToolType.LAYER,
				ToolType.COLORCHOOSER);
	}

	@Before
	public void setUp() {
		changeIntroPage(getPageIndexFromLayout(activityRule.getLayouts(), R.layout.islide_possibilities));
	}

	@Test
	public void testWithoutSequenceTool() {
		introClickToolAndCheckView(toolType, IntroUtils.IntroSlide.Possibilities);
	}
}
