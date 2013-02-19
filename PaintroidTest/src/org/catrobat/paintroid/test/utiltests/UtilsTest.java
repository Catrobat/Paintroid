package org.catrobat.paintroid.test.utiltests;

import org.junit.Test;

import android.test.AndroidTestCase;

public class UtilsTest extends AndroidTestCase {

	// TODO: AUTOSAVE
	private final String MD5_EMPTY = "D41D8CD98F00B204E9800998ECF8427E";
	private final String MD5_CATROID = "4F982D927F4784F69AD6D6AF38FD96AD";
	private final String MD5_HELLO_WORLD = "ED076287532E86365E841E92BFC50D8C";

	@Test
	public void testMD5CheckSumOfString() {
		assertEquals("MD5 sums do not match!", MD5_CATROID, org.catrobat.paintroid.Utils.md5Checksum("catroid"));
		assertEquals("MD5 sums do not match!", MD5_EMPTY, org.catrobat.paintroid.Utils.md5Checksum(""));
		assertEquals("MD5 sums do not match!", MD5_HELLO_WORLD,
				org.catrobat.paintroid.Utils.md5Checksum("Hello World!"));
	}

}
