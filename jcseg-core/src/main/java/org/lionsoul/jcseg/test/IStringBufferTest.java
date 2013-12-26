package org.lionsoul.jcseg.test;

import org.lionsoul.jcseg.util.IStringBuffer;

public class IStringBufferTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		IStringBuffer isb = new IStringBuffer();
		
		long s = System.currentTimeMillis();
		for ( int j = 0; j < 10; j++ ) {
			isb.append(j+"");
			isb.append(',');
		}
		long e = System.currentTimeMillis();
		System.out.println("Done, cost:"+(e-s)+"msec, "+isb.toString());
		
		isb.clear();
		s = System.currentTimeMillis();
		for ( int j = 0; j < 10; j++ ) {
			isb.append(""+j);
			isb.append(',');
		}
		e = System.currentTimeMillis();
		System.out.println("charAt(4)="+isb.charAt(4));
		isb.deleteCharAt(4);
		System.out.println("Done, cost:"+(e-s)+"msec, "+isb.toString());
	}

}
