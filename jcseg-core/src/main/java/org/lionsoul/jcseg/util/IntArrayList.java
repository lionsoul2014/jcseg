package org.lionsoul.jcseg.util;

/**
 * array list for basic int data type.
 * 	to intead of ArrayList<T>, Well, this will save a lot
 * 	work to Reopened and Unpacking. <br />
 * 
 * @author chenxin <chenxin619315@gmail.com>
 */
public class IntArrayList 
{
	private int size = 0;
	//int items array.
	private int[] items;
	
	public IntArrayList() 
	{
		this(6);
	}
	
	public IntArrayList( int opacity ) 
	{
		if ( opacity <= 0 )
			throw new IndexOutOfBoundsException("opacity <= 0");
		items = new int[opacity];
	}
	
	private void resize( int size ) 
	{
		int[] tmp = items;
		items = new int[size];
		int length = (size > tmp.length) ? tmp.length : size;
		
		//copy the items to the tmp
		/*for ( int j = 0; j < length; j++ ) {
			items[j] = tmp[j];
		}*/
		System.arraycopy(tmp, 0, items, 0, length);
	}
	
	/**
	 * Append a new Integer to the end.
	 * 
	 * @param	val.
	 */
	public void add( int val ) 
	{
		if ( size == items.length ) 
			resize( items.length * 2 + 1 );
		items[size++] = val;
	}
	
	public int get( int idx ) 
	{
		if ( idx < 0 || idx > size )
			throw new IndexOutOfBoundsException();
		return items[idx];
	}
	
	public void set( int idx, int val )
	{
		if ( idx < 0 || idx > size )
			throw new IndexOutOfBoundsException();
		items[idx] = val;
	}
	
	/**
	 * remove the element at the specified position.
	 * 	use System.arraycopy intead of a loop may be
	 * 		more effcient. <br />
	 * 
	 * @param idx
	 */
	public void remove( int idx ) 
	{
		if ( idx < 0 || idx > size )
			throw new IndexOutOfBoundsException();
		int numMove = size - idx - 1;
		if ( numMove > 0 )
			System.arraycopy(items, idx + 1, items, idx, numMove);
		size--;
	}
	
	public int size()
	{
		return size;
	}
	
	public void clear() 
	{
		size = 0;
	}
}
