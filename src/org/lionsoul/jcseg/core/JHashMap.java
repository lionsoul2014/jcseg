package org.lionsoul.jcseg.core;

/**
 * Separate Chaining table - implementation the hash table
 * the elements match is based on the equals method of the Object
 * (so better to rewrite the equals method for the complex Object)
 * 
 * @author	chenxin<chenxin619315@gmail.com>
 */
public class JHashMap {
	
	/**
	 * default size for hash table 
	 */
	public static final int DEFAULT_TABLE_SIZE = 31;
	
	/**
	 * the default filling factor 
	 */
	public static final float DEFAULT_FILL_FACTOR = 0.75f;
	
	/**
	 * size of the current hash table 
	 */
	private int size;
	
	/**
	 * current filling factor 
	 */
	private float fillFactor = DEFAULT_FILL_FACTOR;
	
	/**
	 * The next size value at which to resize (capacity * load factor). 
	 */
	transient int threshold;
	
	/**
	 * hash table block 
	 */
	public Entry [] table = null;
	
	
	public JHashMap() {
		this( DEFAULT_TABLE_SIZE );
	}
	
	public JHashMap( int _size ) {
		this( _size, DEFAULT_FILL_FACTOR );
	}
	
	public JHashMap( int _size, float factor ) {
		fillFactor = factor;
		int opacity = nextPrime( _size );
		table = new Entry[opacity];
		size = 0;
		threshold = (int)( opacity * fillFactor );
	}
	
	/**
	 * check the hash table is empty or not
	 * @return true for empty and false for not 
	 */
	public boolean isEmpty() {
		return size == 0;
	}
	
	/**
	 * make the whole table empty 
	 */
	public void makeEmpty() {
		for ( int j = 0; j < table.length; j++ ) 
			table[ j ] = null;
		size = 0;
	}
	
	/**
	 * find an item in a hash table
	 * @param key
	 * @return true for found and false for not 
	 */
	public boolean containsKey( String key ) {
		return getEntry( key ) != null;
	}
	
	/**
	 * find a value in a hash table
	 * @param val
	 * @return true for found and false for not
	 */
	public boolean containsValue( IWord val ) {
		Entry e = null;
		for ( int i = 0; i < table.length; i++ ) {
			for ( e = table[i];
				e != null;
				e = e._next) {
				if ( val.equals( e.val ) || val == e.val )
					return true;
			}
		}
		return false;
	}
	
	/**
	 * get the value associate withe the specified key
	 * @param key
	 * @return if there is a mapping for the key return
	 * 			the value of the Entry
	 * 			or return null 
	 */
	public IWord get( String key ) {
		Entry e = getEntry( key );
		if ( e != null )
			return e.val;
		return null;
	}
	
	/**
     * Returns the entry associated with the specified key in the
     * hash table.  
     * @returns null if the HashMap contains no mapping for the key.
     * 			else the associated Entry
     */
    final Entry getEntry( String key ) {
        int hash = (key == null) ? 0 : hash( key );
        String k;
        for ( Entry e = table[hash];
             e != null;
             e = e._next ) {
            if ( e.hash == hash &&
                ((k = e.key) == key || (key != null && key.equals(k))))
                return e;
        }
        return null;
    }
	
	/**
	 * Associates the specified value with the specified key in the table
	 * if the map contains the mapping for the key, the old value is replaced
	 * @param key
	 * @param val
	 * @return the oldValue if the same key is exists or the item just add 
	 */
	public IWord put( String key,  IWord val ) {
		int hash = (key == null) ? 0 : hash( key );
		String k;
		for ( Entry e = table[hash];
			e != null;
			e = e._next ) {
			if ( e.hash == hash &&
					((k = e.key) == key || ( key != null && key.equals(k)))) {
				IWord oVal = e.getValue();
				e.val = val;
				return oVal;
			}
		}
		
		addEntry( key, val, hash );
		return null;
	}
	
	/**
	 * Add a new entry with the specified key, value and hash code to
     * the specified bucket.  
     * It is the responsibility of this
     * method to resize the table if appropriate.
	 */
	void addEntry( String key, IWord val, int hash ) {
		Entry e = table[hash];
		table[hash] = new Entry( key, val, hash, e );
		if ( size++ >= threshold )
			reHash();
		/*
		 * Attention:
		 * do not use the following condition check
		 * it will cause memory use up error.
		 * here use threshold instead
		 */
		//if ( table.length * fillFactor >= size++ ) {
			//reHash();
		//}
	}
	
	
	/**
	 * remove a item from a hash table
	 * @param key the item to remove
	 * @return if there is a mapping for the key
	 * 			return the old value
	 * 			else return null 
	 */
	public IWord remove( String key ) {
		int hash = ( key == null ) ? 0 : hash( key );
		String k;
		Entry eb = null;
		for ( Entry e = table[hash];
			e != null;
			e = e._next ) {
			Entry next = e._next;
			/*
			 * the first Entry of the LinkedList
			 */
			if ( eb == null ) {
				table[hash] = next;
				if ( next == null ) {
					IWord eVal = e.val;
					e = null;
					return eVal;
				}
				eb = e;
				continue;
			}
			else if ( e.hash == hash &&
				((k=e.key) == key || (key != null && key.equals(k)))) {
				eb._next = e._next;
				IWord eVal = e.val;
				e = null;
				size--;
				return eVal;
			}
			eb = eb._next;
		}
		return null;
	}
	
	/**
	 * if table.length times fillFactor is larger than
	 * the size, we need to reload the hash table  
	 */
	private void reHash() {
		Entry[] _src = table;
		//create a new double-sized table
		//then copy the table
		//nextPrime( 2 * table.length )
		int opacity = nextPrime(2 * table.length);
		table = new Entry[opacity];
		Entry e;
		for ( int j = 0; j < _src.length; j++ ) {
			e = _src[j];
			if ( e != null ) {
				_src[j] = null;
				do {
					Entry next = e._next;
					int hash = hash( e.key );
					e.hash = hash;
					e._next = table[hash];
					table[hash] = e;
					e = next;
				} while ( e != null );
			}
		}
		threshold = (int)(opacity * fillFactor);
	}
	
	/**
	 * Entry class 
	 */
	public static class Entry {
		
		String key;
		IWord val;
		int hash;
		Entry _next;
		
		public Entry( String k, IWord v, int h, Entry next ) {
			key = k;
			val = v;
			hash = h;
			_next = next;
		}
		
		public String getKey() {
			return key;
		}
		
		public IWord getValue() {
			return val;
		}
	}
	
	/**
	 * a hash routine for String Object
	 * @param key
	 * @return the hashcode 
	 */
	public int hash( String key ) {
		
		int factor = 131;
		int hashVal = 0;
		
		for ( int j = 0; j < key.length(); j++ ) 
			hashVal = hashVal * factor + key.charAt(j);
		
		hashVal = hashVal % size;
		if ( hashVal < 0 )
			hashVal = hashVal + size;
		
		return (hashVal & 0x7FFFFFFF);
	}
	
	/**
	 * internal method to general prime number after the given number
	 * @param n the base number
	 * @return the next prime number 
	 */
	private static int nextPrime( int n ) {
		
		//make sure n is an odd number
		if ( n % 2 == 0 )
			n++;
		
		for ( ; ! isPrime( n ); n = n + 2 ) ;
		
		return n;
	}
	
	/**
	 * internal method to test a given number is a prime or not
	 * @param n the number to test
	 * @return the result of the test 
	 */
	private static boolean isPrime( int n ) {
		
		if ( n == 2 || n == 3 )
			return true;
		
		if ( n == 1 || n % 2 == 0 )
			return false;
		
		for ( int j = 3; j * j < n; j++) 
			if ( n % j == 0 )
				return false;
		
		return true;
	}
	
}
