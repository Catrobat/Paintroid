/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid.test.espresso.intro.base;

import android.app.Activity;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.intent.rule.IntentsTestRule;

import org.catrobat.paintroid.Session;
import org.catrobat.paintroid.test.utils.SystemAnimations;
public class WelcomeActivityIntentsTestRule<T extends Activity> extends IntentsTestRule<T> {
    private SystemAnimations systemAnimations;

    public WelcomeActivityIntentsTestRule(Class<T> activityClass) {
        super(activityClass);
    }

    public WelcomeActivityIntentsTestRule(Class<T> activityClass, boolean initialTouchMode) {
        super(activityClass, initialTouchMode);
    }

    public WelcomeActivityIntentsTestRule(Class<T> activityClass, boolean initialTouchMode, boolean launchActivity) {
        super(activityClass, initialTouchMode, launchActivity);
    }

    @Override
    protected void beforeActivityLaunched() {
        super.beforeActivityLaunched();
        Session session = new Session(InstrumentationRegistry.getTargetContext());
        session.setFirstTimeLaunch(true);
    }

    @Override
    protected void afterActivityLaunched() {
        systemAnimations = new SystemAnimations(InstrumentationRegistry.getTargetContext());
        systemAnimations.disableAll();
        super.afterActivityLaunched();
    }

    @Override
    protected void afterActivityFinished() {
        systemAnimations.enableAll();
        super.afterActivityFinished();
    }
}
