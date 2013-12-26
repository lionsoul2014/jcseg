package org.lionsoul.jcseg.util;

/**
 * All kind of Sort alogrithm implemented method.
 * 	use the default compare method.
 * 
 * @author chenxin <chenxin619315@gmail.com>
 */
public class Sort {
	
	private static final int CUTOFF = 11;
/*	private static final int[] GAPS = new int[]{
		1, 5,
		13, 43,
		113, 297, 815,
		1989, 4711,
		11969, 27901, 84801,
        213331, 543749,							//1000th
        1355339, 3501671, 8810089,
        21521774, 58548857,
        157840433, 410151271,
        1131376761, 2147483647};*/
	
	/**
	 * shell sort gaps array. <br />
	 * generate with 9*pow(4, j) - 9 * pow(2, j) + 1,
	 * and pow(4, j) - 3 * pow(2, j) + 1 .<br />
	 */
	private static final int[] GAPS = new int[] {
		1, 5,
		19, 41,
		109, 209, 505, 929,
		2161, 8929,
		16001, 36289, 64769,
		146305, 260609, 587521,					//1000th
		1045505, 2354689, 4188161, 9427969,
		16764929, 37730305, 67084289,
		150958081, 268386305, 603906049,
		1073643521, 2147483647};
	
	
	
	/**
	 * insert sort method. <br />
	 * 
	 * @param arr an array of a comparable items.
	 */
	public static <T extends Comparable<? super T>> void insertionSort( T[] arr ) {
		int j;
		for ( int i = 1; i < arr.length; i++ ) {
			T tmp = arr[i];
			for ( j = i; j > 0 && tmp.compareTo(arr[j-1]) < 0; j--) {
				arr[j] = arr[j-1];
			}
			if ( j < i ) arr[j] = tmp; 
		}
	}
	
	
	
	
	/**
	 * shell sort algorithm. <br />
	 * 
	 * @param arr an array of Comparable items.
	 */
	public static <T extends Comparable<? super T>> void shellSort( T[] arr ) {
		int j, k = 0, gap;
		for ( ; GAPS[k] < arr.length; k++ ) ;
		
		while ( k-- > 0 ) {
			gap = GAPS[k];
			for ( int i = gap; i < arr.length; i++ ) {
				T tmp = arr[ i ];
				for ( j = i; 
					j >= gap && tmp.compareTo( arr[ j - gap ] ) < 0; j -= gap ) {
					arr[ j ] = arr[ j - gap ];
				}
				if ( j < i ) arr[ j ] = tmp;
			}
		}
	}
	
	
	
	
	/**
	 * merge sort algorithm.
	 * 
	 * @param arr an array of Comparable item.
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Comparable<? super T>> void mergeSort( T[] arr ) {
		/*if ( arr.length < 15 ) {
			insertionSort( arr );
			return;
		}*/
		
		T[] tmpArr = (T[]) new Comparable[arr.length];
		
		mergeSort(arr, tmpArr, 0, arr.length - 1);
	}
	
	/**
	 * internal method to make a recursive call. <br />
	 * 
	 * @param arr an array of Comparable items. <br />
	 * @param tmpArr temp array to placed the merged result. <br />
	 * @param left left-most index of the subarray. <br />
	 * @param right right-most index of the subarray. <br />
	 */
	private static <T extends Comparable<? super T>> 
	void mergeSort( T[] arr, T[] tmpArr,
			int left, int right ) {
		//recursive way
		if ( left < right ) {
			int center = ( left + right ) / 2;
			mergeSort(arr, tmpArr, left, center);
			mergeSort(arr, tmpArr, center + 1, right);
			merge(arr, tmpArr, left, center + 1, right);
		}
		
		//loop instead
/*		int len = 2, pos;
		int rpos, offset, cut;
		while ( len <= right ) {
			pos = 0;
			offset = len / 2;
			while ( pos + len <= right  ) {
				rpos = pos + offset;
				merge( arr, tmpArr, pos, rpos, rpos + offset - 1 );
				pos += len;
			}
			
			//merge the rest
			cut = pos + offset;
			if ( cut <= right ) 
				merge( arr, tmpArr, pos, cut, right );
			
			len *= 2;
		}
		merge( arr, tmpArr, 0, len / 2, right );*/
	} 
	
	/**
	 * internal method to merge the sorted halves of a subarray. <br />
	 * 
	 * @param arr an array of Comparable items. <br />
	 * @param tmpArr temp array to placed the merged result. <br />
	 * @param leftPos left-most index of the subarray. <br />
	 * @param rightPos right start index of the subarray. <br />
	 * @param endPos right-most index of the subarray. <br />
	 */
	private static <T extends Comparable<? super T>>
	void merge( T[] arr, T[] tmpArr,
			int lPos, int rPos, int rEnd ) {
		int lEnd = rPos - 1;
		int tPos = lPos;
		int leftTmp = lPos;
		
		while ( lPos <= lEnd && rPos <= rEnd  ) {
			if ( arr[lPos].compareTo( arr[rPos] ) <= 0 )
				tmpArr[ tPos++ ] = arr[ lPos++ ];
			else 
				tmpArr[ tPos++ ] = arr[ rPos++ ];
		}
		
		//copy the rest element of the left half subarray.
		while ( lPos <= lEnd ) 
			tmpArr[ tPos++ ] = arr[ lPos++ ];
		//copy the rest elements of the right half subarray. (only one loop will be execute)
		while ( rPos <= rEnd ) 
			tmpArr[ tPos++ ] = arr[ rPos++ ];
		
		//copy the tmpArr back cause we need to change the arr array items.
		for ( ; rEnd >= leftTmp; rEnd-- )
			arr[rEnd] = tmpArr[rEnd];
	}
	
	
	
	
	/**
	 * method to swap elements in an array.<br />
	 * 
	 * @param arr an array of Objects. <br />
	 * @param idx1 the index of the first element. <br />
	 * @param idx2 the index of the second element. <br />
	 */
	private static <T> void swapReferences( T[] arr, int idx1, int idx2 ) {
		T tmp = arr[idx1];
		arr[idx1] = arr[idx2];
		arr[idx2] = tmp;
	}
	
	
	
	
	/**
	 * quick sort algorithm. <br />
	 * 
	 * @param arr an array of Comparable items. <br />
	 */
	public static <T extends Comparable<? super T>> void quicksort( T[] arr ) {
		quicksort( arr, 0, arr.length - 1 );
	}
	
	/**
	 * get the median of the left, center and right. <br />
	 * order these and hide the pivot by put it the end of
	 * of the array. <br />
	 * 
	 * @param arr an array of Comparable. <br />
	 * @param left the most-left index of the subarray. <br />
	 * @param right the most-right index of the subarray.<br />
	 * @return T
	 */
	private static <T extends Comparable<? super T>>
	T median( T[] arr, int left, int right ) {
		
		int center = ( left + right ) / 2;
		
		if ( arr[left].compareTo( arr[center] ) > 0 )
			swapReferences( arr, left, center );
		if ( arr[left].compareTo( arr[right] ) > 0 )
			swapReferences( arr, left, right );
		if ( arr[center].compareTo( arr[right] ) > 0 )
			swapReferences( arr, center, right );
		
		swapReferences( arr, center, right - 1 );
		return arr[ right - 1 ];
	}
	
	/**
	 * method to sort an subarray from start to end
	 * 		with insertion sort algorithm. <br />
	 * 
	 * @param arr an array of Comparable items. <br />
	 * @param start the begining position. <br />
	 * @param end the end position. <br />
	 */
	public static <T extends Comparable<? super T>> 
	void insertionSort( T[] arr, int start, int end ) {
		int i;
		for ( int j = start + 1; j <= end; j++ ) {
			T tmp = arr[j];
			for ( i = j; i > start && tmp.compareTo( arr[i - 1] ) < 0; i-- ) {
				arr[ i ] = arr[ i - 1 ];
			}
			if ( i < j ) arr[ i ] = tmp;
		}
	}
	
	/**
	 * internal method to sort the array with quick sort algorithm. <br />
	 * 
	 * @param arr an array of Comparable Items. <br />
	 * @param left the left-most index of the subarray. <br />
	 * @param right the right-most index of the subarray. <br />
	 */
	private static <T extends Comparable<? super T>> 
	void quicksort( T[] arr, int left, int right ) {
		if ( left + CUTOFF <= right  ) {
			//find the pivot
			T pivot = median( arr, left, right );
			
			//start partitioning
			int i = left, j = right - 1;
			for ( ; ; ) {
				while ( arr[++i].compareTo( pivot ) < 0 ) ;
				while ( arr[--j].compareTo( pivot ) > 0 ) ;
				if ( i < j )
					swapReferences( arr, i, j );
				else
					break;
			}
			
			//swap the pivot reference back to the small collection.
			swapReferences( arr, i, right - 1 );
			
			quicksort( arr, left, i - 1 );		//sort the small collection.
			quicksort( arr, i + 1, right );		//sort the large collection.
			
		} else {
			//if the total number is less than CUTOFF we use insertion sort instead.
			insertionSort( arr, left, right );
		}
	}
	
	
	
	
	/**
	 * quick select algorithm. <br />
	 * 
	 * @param arr an array of Comparable items. <br />
	 * @param k the k-th small index.
	 */
	public static <T extends Comparable<? super T>>
	void quickSelect( T[] arr, int k ) {
		quickSelect( arr, 0, arr.length - 1, k );
	}
	
	/**
	 * internal method to find the Kth small element for the given array. <br /> 
	 * 
	 * @param arr an array of Comparable items. <br />
	 * @param left the left-most index of the subarray. <br />
	 * @param right the right-most index of the subarray. <br />
	 * @param k the k-th small element.
	 */
	private static <T extends Comparable<? super T>> 
	void quickSelect( T[] arr, int left, int right, int k ) {
		if ( left + CUTOFF <= right ) {
			//find the pivot
			T pivot = median( arr, left, right );
			
			int i = left, j = right - 1;
			for ( ; ; ) {
				while ( arr[ ++i ].compareTo( pivot ) < 0 ) ;
				while ( arr[ --j ].compareTo( pivot ) > 0 ) ;
				if ( i < j )
					swapReferences( arr, i, j );
				else 
					break;
			}
			
			//swap the pivot
			swapReferences( arr, i, right - 1 );
			
			if ( k <= i )
				quickSelect( arr, left, i - 1, k );
			else if ( k > i + 1 )
				quickSelect( arr, i + 1, right, k );
			
		} else {
			insertionSort( arr, left, right );
		}
	}
	
	
	
	/**
	 * bucket sort algorithm. <br />
	 * 
	 * @param arr an int array. <br />
	 * @param m the large-most one for all the Integers in arr
	 */
	public static void bucketSort( int[] arr, int m ) {
		int[] count = new int[m];
		int j, i = 0;
		//System.out.println(count[0]==0?"true":"false");
		for ( j = 0; j < arr.length; j++ ) 
			count[ arr[j] ]++;
		
		//loop and filter the elements
		for ( j = 0; j < m; j++ ) {
			if ( count[j] > 0 ) {
				while ( count[j]-- > 0 ) 
					arr[i++] = j;
			}
		}
	}
	
	/**
	 * bucket sort algorithm. <br />
	 * 
	 * @param arr an array of Integer items. <br />
	 * @param m the large-most one for all the Integers in arr
	 */
	public static void bucketSort( Integer[] arr, int m ) {
		int[] count = new int[m];
		int j, i = 0;
		for ( j = 0; j < arr.length; j++ ) 
			count[ arr[j] ]++;
		
		//loop and filter the elements
		for ( j = 0; j < m; j++ ) 
			if ( count[j] > 0 ) {
				while ( count[j]-- > 0 )
					arr[i++] = new Integer(j);
			}
	}

}
