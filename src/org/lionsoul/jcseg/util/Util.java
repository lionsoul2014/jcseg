package org.lionsoul.jcseg.util;

import java.io.File;

/**
 * static method for jcseg.
 * 
 * @author	chenxin<chenxin619315@gmail.com>
 */
public class Util {
	
	/**
	 * get the absolute parent path for the jar file. 
	 * 
	 * @param o
	 * @return String
	 */
	public static String getJarHome(Object o) {
		String path = o.getClass().getProtectionDomain()
					.getCodeSource().getLocation().getFile();
		File jarFile = new File(path);
		return jarFile.getParentFile().getAbsolutePath();
	}

}
