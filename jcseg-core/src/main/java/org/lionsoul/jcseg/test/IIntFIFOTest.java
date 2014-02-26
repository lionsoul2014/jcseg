package org.lionsoul.jcseg.test;

import org.lionsoul.jcseg.util.IIntFIFO;

/**
 * IIntFIFO test program
 * 
 * @author chenxin <chenxin619315@gmail.com>
 */
public class IIntFIFOTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) 
	{
		IIntFIFO q = new IIntFIFO();
		
		q.enQueue('A');
		q.enQueue('B');
		System.out.println("size: " + q.size());
		
		q.enQueue('C');
		q.enQueue('D');
		System.out.println("size: " + q.size());
		
		while ( q.size() > 0 )
		{
			System.out.println("size: " + q.size() + ", " + (char)q.deQueue());
		}
	}

}
