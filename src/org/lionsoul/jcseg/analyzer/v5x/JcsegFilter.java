package org.lionsoul.jcseg.analyzer.v5x;

import java.io.IOException;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;

/**
 * <p>
 * token filter class according the to lucene API, this will be remove in in Lucene 5.0
 * </p>
 * 
 * @author chenxin<chenxin619315@gmail.com>
 */
public class JcsegFilter extends TokenFilter 
{
    
    //private CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);

    protected JcsegFilter(TokenStream input) 
    {
        super(input);
    }

    @Override
    final public boolean incrementToken() throws IOException 
    {
        while (input.incrementToken()) {
            //char text[] = termAtt.buffer();
            //int termLength = termAtt.length();
            
            return true;
        }
        
        return false;
    }
}
