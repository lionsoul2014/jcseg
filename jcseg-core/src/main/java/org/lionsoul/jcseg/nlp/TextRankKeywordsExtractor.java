package org.lionsoul.jcseg.nlp;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.lionsoul.jcseg.core.ISegment;
import org.lionsoul.jcseg.core.IWord;

/**
 * document keywords extractor base on textRank algorithm
 * 
 * @author chenxin<chenxin619315@gmail.com>
*/
public class TextRankKeywordsExtractor extends KeywordsExtractor
{
	//page rank damping factor
	public static final float D = 0.85F;
	
	//default keywords number
	protected int keywordsNum = 10;
	
	//max iterate times
	protected int maxIterateNum = 210;
	
	//window size
	protected int windowSize = 5;


	public TextRankKeywordsExtractor(ISegment seg) {
		super(seg);
	}

	@Override
	public List<String> getKeywords(Reader reader) throws IOException {
		Map<String, List<String>> vote = new HashMap<String, List<String>>();
		List<String> words = new ArrayList<String>();
		
		//document segment
		IWord w = null;
		seg.reset(reader);
		while ( (w = seg.next()) != null )
		{
			if ( filter(w) == false ) continue;
			
			String word = w.getValue();
			if ( ! vote.containsKey(word) ) 
			{
				vote.put(word, new LinkedList<String>());
			}
			
			words.add(word);
		}
		
		//count the neighbour
		for ( int i = 0; i < words.size(); i++ )
		{
			String word = words.get(i);
			List<String> support = vote.get(word);
			
			int sIdx = Math.max(0, i - windowSize);
			int eIdx = Math.min(i + windowSize, words.size() - 1);
			
			for ( int j = sIdx; j <= eIdx; j++ )
			{
				if ( j == i ) continue;
				support.add(words.get(j));
			}
		}
		
		//do the page rank scores caculate
		HashMap<String, Float> score = new HashMap<String, Float>(1, 0.7F);
		for ( int c = 0; c < maxIterateNum; c++ )
		{
			HashMap<String, Float> T = new HashMap<String, Float>();
			for ( Map.Entry<String, List<String>> entry : vote.entrySet() )
			{
				String key = entry.getKey();
				List<String> value = entry.getValue();
				
				float sega = 0F;
				for ( String item : value )
				{
					int size = vote.get(item).size();
					if ( item.equals(key) || size == 0 ) continue;
					
					float Sy = 0;
					if ( score != null 
							&& score.containsKey(item) ) {
						Sy = score.get(item);
					}
					
					sega += Sy / size;
				}
				
				T.put(key, 1 - D + D * sega);
			}
			
			/*
			 * prepare the global score for the next iteration
			 * */
			score = T;
		}

		//sort the items by score
		List<Map.Entry<String, Float>> entryList = new ArrayList<Map.Entry<String, Float>>(score.entrySet());
		Collections.sort(entryList, new Comparator<Map.Entry<String, Float>>(){
			@Override
			public int compare(Map.Entry<String, Float> o1, 
					Map.Entry<String, Float> o2) {
				return o2.getValue().compareTo(o1.getValue());
			}
		});
		
		for ( Map.Entry<String, Float> entry : entryList )
		{
			System.out.println(entry.getKey()+"="+entry.getValue());
		}
		
		//return the sublist as the final result
		int len = Math.min(keywordsNum, entryList.size());
		List<String> keywords = new ArrayList<String>(len);
		for ( int i = 0; i < len; i++ )
		{
			keywords.add(entryList.get(i).getKey());
		}
		
		return keywords;
	}

	public int getKeywordsNum() {
		return keywordsNum;
	}

	public void setKeywordsNum(int keywordsNum) {
		this.keywordsNum = keywordsNum;
	}

	public int getMaxIterateNum() {
		return maxIterateNum;
	}

	public void setMaxIterateNum(int maxIterateNum) {
		this.maxIterateNum = maxIterateNum;
	}

	public int getWindowSize() {
		return windowSize;
	}

	public void setWindowSize(int windowSize) {
		this.windowSize = windowSize;
	}
	
}
