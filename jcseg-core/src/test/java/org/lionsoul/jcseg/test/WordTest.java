package org.lionsoul.jcseg.test;

import org.lionsoul.jcseg.IWord;
import org.lionsoul.jcseg.segmenter.Word;

public class WordTest
{

    public static void main(String[] args)
    {
        IWord word1 = new Word("科技", IWord.T_CJK_WORD);
        IWord word2 = word1.clone();
        word2.setPosition(120);
        
        System.out.println("value equals ? " + (word1.getValue() == word2.getValue()));
        System.out.println("position equals ? " + (word1.getPosition() == word2.getPosition()));
    }

}
