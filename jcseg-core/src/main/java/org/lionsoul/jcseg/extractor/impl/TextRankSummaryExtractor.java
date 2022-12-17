package org.lionsoul.jcseg.extractor.impl;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lionsoul.jcseg.ISegment;
import org.lionsoul.jcseg.IWord;
import org.lionsoul.jcseg.extractor.SummaryExtractor;
import org.lionsoul.jcseg.sentence.Sentence;
import org.lionsoul.jcseg.sentence.SentenceSeg;
import org.lionsoul.jcseg.util.IStringBuffer;
import org.lionsoul.jcseg.util.Sort;

/**
 * TextRank summary extractor base on textRank algorithm
 * 
 * @author chenxin<chenxin619315@gmail.com>
*/
public class TextRankSummaryExtractor extends SummaryExtractor
{
    //const factor
    public static final float D  = 0.85F;
    public static final float K1 = 2.0F;
    public static final float B = 0.75F;
    
    //default keywords number
    protected int sentenceNum = 6;
    
    //max iterate times
    protected int maxIterateNum = 120;
    

    public TextRankSummaryExtractor(ISegment wordSeg, SentenceSeg sentenceSeg)
    {
        super(wordSeg, sentenceSeg);
    }
    
    /**
     * text doc to sentence
     * 
     * @param   reader
     * @return  List<Sentence>
    */
    List<Sentence> textToSentence(Reader reader) throws IOException
    {
        final List<Sentence> sentence = new ArrayList<>();
        
        Sentence sen = null;
        sentenceSeg.reset(reader);
        while ( (sen = sentenceSeg.next()) != null ) {
            sentence.add(sen);
        }
        
        return sentence;
    }
    
    /**
     * sentence to words
     * 
     * @param   sentence
     * @return  List<List<IWord>>
    */
    List<List<IWord>> sentenceTokenize(List<Sentence> sentence) throws IOException
    {
        final List<List<IWord>> senWords = new ArrayList<>();
        for ( Sentence sen : sentence ) {
            List<IWord> words = new ArrayList<>();
            wordSeg.reset(new StringReader(sen.getValue()));
            IWord word = null;
            while ( (word = wordSeg.next()) != null ) {
                words.add(word);
            }
            
            senWords.add(words);
        }
        
        return senWords;
    }
    
    /**
     * degree of correlations matrix build
     * well, base on BM25 alogrithm: 
     * Score(Q,d) = sigema(IDF(qi)*fi(k1+1)/(fi+k1*(1-b+dl/avgdl)))
     * IDF(qi) = log((N-n(qi)+0.5)/(n(qi)+0.5))
     * 
     * @param   sentence
     * @param   senWords
     * @return  double[]
    */
    double[][] BM25RelevanceMatixBuild(
            List<Sentence> sentence, List<List<IWord>> senWords)
    {
        int docNum = sentence.size();
        
        //1. count the average document length
        double avgdl = 0D;
        //for ( Sentence sen : sentence ) avgdl += sen.getLength();
        for ( List<IWord> words : senWords ) avgdl += words.size();
        avgdl /= docNum;
        
        //2. count all the tf and the df/N
        //word and the frequency mapping for each document 
        @SuppressWarnings("unchecked")
        final Map<IWord, Integer>[] tf = new Map[docNum];
        //word and the number of document that contains this word mapping
        final Map<IWord, Integer> df = new HashMap<>();
        int index = 0;
        for ( List<IWord> words : senWords ) {
            final Map<IWord, Integer> f = new HashMap<>();
            for ( IWord word : words ) {
                f.put(word, f.containsKey(word) ? f.get(word) + 1 : 1);
            }
            
            tf[index++] = f;
            
            /*
             * the logic inner the following loop could not merge in the above one
             * is because that same word may appear more than one time in a document,
             * well, map will clear up the duplicate. 
            */
            for ( Map.Entry<IWord, Integer> entry : f.entrySet() ) {
                IWord key = entry.getKey();
                df.put(key, df.containsKey(key) ? df.get(key) + 1 : 1);
            }
        }
        
        //3. count the words idf
        final Map<IWord, Double> idf = new HashMap<>();
        for ( Map.Entry<IWord, Integer> entry : df.entrySet() ) {
            final IWord key = entry.getKey();
            final int nq = df.get(key);
            idf.put(key, Math.log((docNum - nq + 0.5) / (nq + 0.5)) );
        }
        
        //4. build the relevance score matrix
        final double[][] scores = new double[docNum][docNum];
        for ( int i = 0; i < docNum; i++ ) {
            int j = 0;
            int dl = senWords.get(i).size();
            double dlRelative = K1 * (1 - B + B * dl / avgdl);
            
            /*
             * count the relevance for document sentence[i]
             * with all the queries sentence[i]  
            */
            for ( List<IWord> query : senWords ) {
                double score = 0F;
                for ( IWord q : query ) {
                    /*
                     * count the relevance of q with document sentence[i] 
                    */
                    int fi = tf[i].getOrDefault(q, 0);
                    double rel = fi * (K1 + 1) / (fi + dlRelative);
                    
                    /*
                     * count and add the sub relevance value: idf * rel 
                    */
                    score += idf.get(q) * rel;
                }
                
                scores[i][j++] = score;
            }
        }
        
        //let gc do its work
        for ( Map<IWord, Integer> m : tf ) m.clear();
        df.clear();
        idf.clear();
        
        return scores;
    }
    
    /**
     * sum the specified double array
     * 
     * @param   score
     * @return  double
    */
    static double sum(double[] score)
    {
        double r = 0D;
        for ( double d : score ) r += d;
        return r;
    }
    
    /**
     * get the documents order by relevance score.
     * 
     * @param   sentence
     * @param   senWords
    */
    protected Document[] textRankSortedDocuments(
            List<Sentence> sentence, List<List<IWord>> senWords)
    {
        int docNum = sentence.size();
    
        //documents relevance matrix build
        final double[][] relevance = BM25RelevanceMatixBuild(sentence, senWords);
        //org.lionsoul.jcseg.util.Util.printMatrix(relevance);
        
        final double[] score = new double[docNum];
        final double[] weight_sum = new double[docNum];
        for ( int i = 0; i < docNum; i++ ) {
            weight_sum[i] = sum(relevance[i]) - relevance[i][i];
            score[i] = 0;
        }
        
        //do the text-rank score iteration
        for ( int c = 0; c < maxIterateNum; c++ ) {
            for ( int i = 0; i < docNum; i++ ) {
                double sigema = 0D;
                for ( int j = 0; j < docNum; j++ ) {
                    if ( i == j || weight_sum[j] == 0 ) continue;
                    /*
                     * ws(vj) * wji / sigma(wjk) with k in Out(Vj) 
                     * ws(vj): score[j] the score of document[j]
                     * wji: relevance score bettween document[j] and document[i]
                     * sigema(wjk): weight sum for document[j]
                    */
                    sigema += relevance[j][i] / weight_sum[j] * score[j];
                }
                
                score[i] = 1 - D + D * sigema;
            }
        }
        
        //build the document set
        //and sort the documents by scores
        final Document[] docs = new Document[docNum];
        for ( int i = 0; i < docNum; i++ ) {
            docs[i] = new Document(i, sentence.get(i), senWords.get(i), score[i]);
        }
        
        Sort.shellSort(docs);
        return docs;
    }

    @Override
    public List<String> getKeySentence(Reader reader) throws IOException 
    {
        //build the documents
        final List<Sentence> sentence = textToSentence(reader);
        if ( sentence.size() == 1 ) {
            List<String> list = new ArrayList<>(1);
            list.add(sentence.get(0).getValue());
            return list;
        }
        
        final List<List<IWord>> senWords = sentenceTokenize(sentence);
        int docNum = sentence.size();
                
        //get the text rank score sorted documents
        final Document[] docs = textRankSortedDocuments(sentence, senWords);
        
        //return the sublist as the final result
        int len = Math.min(sentenceNum, docNum);
        final List<String> topSentence = new ArrayList<>(len);
        for ( int i = 0; i < len; i++ ) {
            topSentence.add(docs[i].getSentence().getValue());
            //System.out.println(i+", "+docs[i].getScore()+", "+docs[i].getSentence());
        }
        
        //let gc do its work
        sentence.clear();
        senWords.clear();

        return topSentence;
    }

    @Override
    public String getSummary(Reader reader, int length) throws IOException 
    {
        //build the documents
        final List<Sentence> sentence = textToSentence(reader);
        if ( sentence.size() == 1 ) {
            String summary = sentence.get(0).getValue();
            return length >= summary.length() 
                    ? summary.substring(0) : summary.substring(0, length);
        }
        
        final List<List<IWord>> senWords = sentenceTokenize(sentence);
        int docNum = sentence.size();
                
        //get the text rank score sorted documents
        final Document[] docs = textRankSortedDocuments(sentence, senWords);
        
        /*
         * substring length chars from the position
         * of the document with the greatest text rank score.
         * if still not enough start ahead of it...
        */
        boolean allAfter = false;
        int less = length, sIdx = docs[0].getIndex();
        for ( int i = docs[0].getIndex(); i < docNum; i++ ) {
            less -= sentence.get(i).getLength();
            if ( less <= 0 ) {
                allAfter = true;
                break;
            }
        }
        
        //not enough: check the sentence ahead of it
        if ( less > 0 ) {
            for ( int i = docs[0].getIndex() - 1; i >= 0; i-- ) {
                less -= sentence.get(i).getLength();
                if ( less <= 0 ) {
                    sIdx = i;
                    break;
                }
            }
            
            if ( less > 0 ) sIdx = 0;
        }
        
        final IStringBuffer isb = new IStringBuffer();
        for ( int i = sIdx; i < docNum; i++ ) {
            int senLen = isb.length() + sentence.get(i).getLength();
            if ( senLen < length ) {
                isb.append(sentence.get(i).getValue());
            } else if ( ((float)length - isb.length()) / length >= 0.15F ) {
                isb.append(sentence.get(i).getValue());
            } else {
                break;
            }
        }
        
        /*
         * do the length limit and substring
         * if all the sentence are after the key one
         * 
         * Added at 2017/09/17
        */
        if ( allAfter && isb.length() > length ) {
            isb.setLength(length);
        }
        
        //let gc do its work
        sentence.clear();
        senWords.clear();
                
        return isb.toString();
    }
    
    public int getSentenceNum()
    {
        return sentenceNum;
    }

    public void setSentenceNum(int sentenceNum)
    {
        this.sentenceNum = sentenceNum;
    }

    public int getMaxIterateNum()
    {
        return maxIterateNum;
    }

    public void setMaxIterateNum(int maxIterateNum)
    {
        this.maxIterateNum = maxIterateNum;
    }


    /**
     * summary document inner class
     * 
     * @author  chenxin<chenxin619315@gmail.com>
    */
    public static class Document implements Comparable<Document>
    {
        /**
         * the relevance score for the current document 
        */
        private double score;
        
        /**
         * the original sentence for the document
        */
        private Sentence sentence;
        
        /**
         * the original index
        */
        private int index;
        
        /**
         * the Words list for the current document 
        */
        private List<IWord> words;
        
        /**
         * construct method 
         * 
         * @param   index
         * @param   sentence
         * @param   words
         * @param   score
        */
        public Document(int index, Sentence sentence, List<IWord> words, double score)
        {
            this.index = index;
            this.sentence = sentence;
            this.words = words;
            this.score = score;
        }

        public double getScore()
        {
            return score;
        }

        public void setScore(double score)
        {
            this.score = score;
        }
        
        public int getIndex()
        {
            return index;
        }

        public void setIndex(int index)
        {
            this.index = index;
        }

        public Sentence getSentence()
        {
            return sentence;
        }

        public void setSentence(Sentence sentence)
        {
            this.sentence = sentence;
        }

        public List<IWord> getWords()
        {
            return words;
        }

        public void setWords(List<IWord> words)
        {
            this.words = words;
        }

        /**
         * override the compareTo method
         * compare document with its relevance score
         * 
         * @param   o
        */
        @Override
        public int compareTo(Document o) 
        {
            double v = o.getScore() - score;
            if ( v > 0 ) return 1;
            if ( v < 0 ) return -1;
            return 0;
        }
    }
    
}
