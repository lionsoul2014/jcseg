package org.lionsoul.jcseg.util;

import java.util.HashMap;
import java.util.Map;

import org.lionsoul.jcseg.core.IWord;

/**
 * A normal queue base one single link
 * 	but with hash index, so, it is fast for searching
 * 
 * Currently build to replace the LinkList work pool 
 * 	of class org.lionsoul.jcseg.ASegment
 * 
 * @Note: Not thread safe
 * 
 * @author chenxin <chenxin619315@gmail.com>
 */

 //--------------------------------------------------

public class IHashQueue<T extends IWord> 
{
	private int 			size;			//size of the current queue
	private Entry<T> 		head;			//head of the queue
	private Entry<T> 		tail;			//tail of the queue
	
	private Map<String, T>	index;			//hash index layer
	
	public IHashQueue()
	{
		size 	= 0;
		tail 	= new Entry<T>(null, null, null);
		head 	= new Entry<T>(null, null, tail);
		tail.prev = head;
		
		//initialize the hash indexer
		index	= new HashMap<String, T>(16, 0.85F);
	}
	
	/**
	 * append a item from the tail
	 * 
	 * @param	word
	 * @return	boolean
	 */
	public boolean add( T word )
	{
		Entry<T> o = new Entry<T>(word, tail.prev, tail);
		tail.prev.next = o;
		tail.prev = o;
		
		//set the size and set the index
		size++;
		index.put(word.getValue(), word);
		
		return true;
	}
	
	/**
	 * check the specifield T is aleady exists in the queue or not
	 * 
	 * @param	word
	 * @return	boolean
	 */
	public boolean contains( T word )
	{
		return index.containsKey(word.getValue());
	}
	
	/**
	 * remove the node from the head
	 * 	and you should make sure the size is larger than 0 by calling size()
	 * before you invoke the method or you will just get null
	 * 
	 * @param	T
	 */
	public T remove()
	{
		if ( size == 0 ) return null;
		
		//remove the first element
		Entry<T> o 	= head.next;
		head.next 	= o.next;
		o.next.prev = head;
		
		//bakup the data
		T v 		= o.data;
		size--;
		index.remove(v.getValue());
		
		o = null;			//Let gc do its work
		
		return v;
	}
	
	/**
	 * get the size of the queue
	 * 
	 * @return int
	 */
	public int size()
	{
		return size;
	}
	
	/**
	 * innner Entry node class
	 * 
	 * @author chenxin<chenxin619315@gmail.com>
	 */
	public static class Entry<T>
	{
		public T 			data;			//data of the current node
		public Entry<T> 	prev;			//prev entry quote
		public Entry<T> 	next;			//next entry quote
		
		public Entry( T data, 
			Entry<T> prev, Entry<T> next )
		{
			this.data = data;
			this.prev = prev;
			this.next = next;
		}
	}
}
