package org.lionsoul.jcseg.test;

import org.lionsoul.jcseg.util.IntArrayList;

/**
 * IntArrayList class Simple test program. <br />
 * 
 * @author chenxin <chenxin619315@gmail.com>
 */
public class IntArrayListTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		IntArrayList list = new IntArrayList();
		
		System.out.println("+---Test add: ");
		//add some elements.
		for ( int j = 0; j < 10; j++ ) {
			list.add(j);
		}
		System.out.println("size="+list.size()+"\n");
		
		list.set(0, 11);
		list.set(3, 10);
		
		System.out.println("+---Test get: ");
		for ( int j = 0; j < list.size(); j++ )
			System.out.println("get("+j+")="+list.get(j));
		System.out.println("\n");
		
		System.out.println("+---Test remove: ");
		for ( int j = 0; j < 3; j++ ) {
			int i = ((int)( Math.random() * 1000))%list.size();
			list.remove(i);
			System.out.print("remove("+i+")");
			System.out.println(", size="+list.size());
		}
		System.out.println("\n");
		
		System.out.println("+---Left: ");
		for ( int j = 0; j < list.size(); j++ ) {
			System.out.println("get("+j+")="+list.get(j));
		}
	}

}
