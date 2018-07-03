/*
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2015 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid.test.listener;

import android.view.View;

import org.catrobat.paintroid.listener.BottomBarScrollListener;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@RunWith(MockitoJUnitRunner.class)
public class BottomBarScrollListenerTest {

	@Mock
	private View previous;

	@Mock
	private View next;

	private BottomBarScrollListener bottomBarScrollListener;

	@Before
	public void setUp() {
		bottomBarScrollListener = new BottomBarScrollListener(previous, next);
	}

	@Test
	public void testOnScrollMostRight() {
		bottomBarScrollListener.onScrollMostRight();

		verify(next).setVisibility(View.GONE);
		verifyNoMoreInteractions(previous);
		verifyNoMoreInteractions(next);
	}

	@Test
	public void testOnScrollMostLeft() {
		bottomBarScrollListener.onScrollMostLeft();

		verify(previous).setVisibility(View.GONE);
		verifyNoMoreInteractions(previous);
		verifyNoMoreInteractions(next);
	}

	@Test
	public void testOnScrollFromMostLeft() {
		bottomBarScrollListener.onScrollFromMostLeft();

		verify(previous).setVisibility(View.VISIBLE);
		verifyNoMoreInteractions(previous);
		verifyNoMoreInteractions(next);
	}

	@Test
	public void testOnScrollFromMostRight() {
		bottomBarScrollListener.onScrollFromMostRight();

		verify(next).setVisibility(View.VISIBLE);
		verifyNoMoreInteractions(previous);
		verifyNoMoreInteractions(next);
	}
}
