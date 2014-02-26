package org.lionsoul.jcseg.test;

import org.lionsoul.jcseg.util.IIntQueue;

/**
 * IIntQueue class test program
 * 
 * @author chenxin <chenxin619315@gmail.com>
 */
public class IIntQueueTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) 
	{
		IIntQueue q = new IIntQueue();
		
		//enqueue
		/*for ( int i = 0; i < 100; i++ )
		{
			q.enQueue(i);
		}
		System.out.println("size: "+q.size());*/
		
		//dequeue test
/*		for ( int i = 0; i < 1000; i++ )
		{
			if ( i != 0 ) System.out.print(", ");
			System.out.print(q.deQueue());
		}
		System.out.println("size: "+q.size());*/
		
		q.enQueue('A');
		q.enQueue('B');
		System.out.println("size: "+q.size());
		
		q.enQueue('C');
		System.out.println((char)q.deQueue());
		q.enQueue('D');
		q.enQueue('E');
		
		while ( q.size() > 0 ) 
		{
			System.out.println((char)q.deQueue()+", size: " + q.size());
		}
	}

}
