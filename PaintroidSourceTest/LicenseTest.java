import java.io.*;
import junit.framework.TestCase;

public class LicenseTest extends TestCase {

	protected String path_to_project = "../Paintroid/src";
	protected String[] license = {
							"Catroid: An on-device graphical programming language for Android devices",
						    "Copyright (C) 2010  Catroid development team",
							"(<http://code.google.com/p/catroid/wiki/Credits>)",
							"This program is free software: you can redistribute it and/or modify",
						    "it under the terms of the GNU General Public License as published by",
						    "the Free Software Foundation, either version 3 of the License, or",
						    "(at your option) any later version.",
						    "This program is distributed in the hope that it will be useful,",
						    "but WITHOUT ANY WARRANTY; without even the implied warranty of",
						    "MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the",
						    "GNU General Public License for more details.",
						    "You should have received a copy of the GNU General Public License",
						    "along with this program.  If not, see <http://www.gnu.org/licenses/>."
						};
	
	public LicenseTest() {

	}

	public void setUp() throws Exception {
		
	}
	
	public void testIfGplLicenseIsInAllFiles() throws Exception{
		File directory = new File(path_to_project);
		walkThroughDirectories(directory);
		
	}

	protected void walkThroughDirectories(File directory) {
	    if (directory.isDirectory()) {
	        String[] subdirectory = directory.list();
	        for (int index=0; index < subdirectory.length; index++) {
	        	walkThroughDirectories(new File(directory, subdirectory[index]));
	        }
	    } else {
	        fileCheckForLicense(directory);
	    }
	}
	
	protected void fileCheckForLicense(File file)
	{
		System.out.println(file.getAbsolutePath());
		try
		{
			FileInputStream fileInputStream = new FileInputStream(file);
			DataInputStream dataInputStream = new DataInputStream(fileInputStream);
	        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(dataInputStream));
		    
	        String lineFromSourceFile;
		    int indexFromLicenseString = 0;
		    while ((lineFromSourceFile = bufferedReader.readLine()) != null && indexFromLicenseString < license.length)   {
		    	if(lineFromSourceFile.length() <2 || lineFromSourceFile.substring(2).trim().isEmpty())
		    	{
		    		continue;
		    	}
		    	assertEquals(license[indexFromLicenseString], lineFromSourceFile.substring(2).trim());
		    	indexFromLicenseString++;
		    }
		    dataInputStream.close();
		}
		catch (Exception e) {
			assertTrue(false);
		}
	}
}
