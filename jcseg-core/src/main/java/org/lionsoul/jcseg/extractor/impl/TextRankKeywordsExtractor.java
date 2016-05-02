package org.lionsoul.jcseg.extractor.impl;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.lionsoul.jcseg.extractor.KeywordsExtractor;
import org.lionsoul.jcseg.tokenizer.core.ISegment;
import org.lionsoul.jcseg.tokenizer.core.IWord;

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
    protected int maxIterateNum = 120;
    
    //window size
    protected int windowSize = 5;
    
    /**
     * auto filter the words with low score
    */
    protected boolean autoFilter = false;


    public TextRankKeywordsExtractor(ISegment seg)
    {
        super(seg);
    }

    @Override
    public List<String> getKeywords(Reader reader) throws IOException 
    {
        Map<String, List<String>> winMap = new HashMap<String, List<String>>();
        List<String> words = new ArrayList<String>();
        
        //document segment
        IWord w = null;
        seg.reset(reader);
        while ( (w = seg.next()) != null ) {
            if ( filter(w) == false ) continue;
            
            String word = w.getValue();
            if ( ! winMap.containsKey(word) ) {
                winMap.put(word, new LinkedList<String>());
            }
            
            words.add(word);
        }
        
        //count the neighbour
        for ( int i = 0; i < words.size(); i++ ) {
            String word = words.get(i);
            List<String> support = winMap.get(word);
            
            int sIdx = Math.max(0, i - windowSize);
            int eIdx = Math.min(i + windowSize, words.size() - 1);
            
            for ( int j = sIdx; j <= eIdx; j++ ) {
                if ( j == i ) continue;
                support.add(words.get(j));
            }
        }
        
        //do the page rank scores caculate
        HashMap<String, Float> score = new HashMap<String, Float>();
        for ( int c = 0; c < maxIterateNum; c++ ) {
            for ( Map.Entry<String, List<String>> entry : winMap.entrySet() ) {
                String key = entry.getKey();
                List<String> value = entry.getValue();
                
                float sigema = 0F;
                for ( String ele : value ) {
                    int size = winMap.get(ele).size();
                    if ( ele.equals(key) || size == 0 ) {
                        continue;
                    }
                    
                    float Sy = 0;
                    if ( score != null 
                            && score.containsKey(ele) ) {
                        Sy = score.get(ele);
                    }
                    
                    sigema += Sy / size;
                }
                
                score.put(key, 1 - D + D * sigema);
            }
        }

        //sort the items by score
        List<Map.Entry<String, Float>> entryList = new ArrayList<Map.Entry<String, Float>>(score.entrySet());
        Collections.sort(entryList, new Comparator<Map.Entry<String, Float>>(){
            @Override
            public int compare(Map.Entry<String, Float> o1, Map.Entry<String, Float> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });
        
        float tScores = 0F, avgScores = 0F, stdScores = 0F;
        for ( Map.Entry<String, Float> entry : entryList ) {
            tScores += entry.getValue();
            //System.out.println(entry.getKey()+"="+entry.getValue());
        }
        
        avgScores = tScores / words.size();
        stdScores = avgScores * (1 + D);
        
        //return the sublist as the final result
        int len = Math.min(keywordsNum, entryList.size());
        List<String> keywords = new ArrayList<String>(len);
        for ( int i = 0; i < entryList.size(); i++ ) {
            Map.Entry<String, Float> e = entryList.get(i);
            if ( i >= len ) break;
            if ( autoFilter && e.getValue() < stdScores ) break;
            keywords.add(e.getKey());
        }
        
        //let gc do its work
        winMap.clear();
        words.clear();
        winMap = null; words = null;
        score = null; entryList = null;
        
        return keywords;
    }

    public int getKeywordsNum()
    {
        return keywordsNum;
    }

    public void setKeywordsNum(int keywordsNum)
    {
        this.keywordsNum = keywordsNum;
    }

    public int getMaxIterateNum()
    {
        return maxIterateNum;
    }

    public void setMaxIterateNum(int maxIterateNum)
    {
        this.maxIterateNum = maxIterateNum;
    }

    public int getWindowSize()
    {
        return windowSize;
    }

    public void setWindowSize(int windowSize)
    {
        this.windowSize = windowSize;
    }

    public boolean isAutoFilter()
    {
        return autoFilter;
    }

    public void setAutoFilter(boolean autoFilter)
    {
        this.autoFilter = autoFilter;
    }
    
}
