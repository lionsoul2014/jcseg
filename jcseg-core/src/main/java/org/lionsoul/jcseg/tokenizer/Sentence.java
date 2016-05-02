package org.lionsoul.jcseg.tokenizer;

/**
 * sentence desc class
 * 
 * @author chenxin<chenxin619315@gmail.com>
*/
public class Sentence 
{
    /**
     * sentence string value 
    */
    private String value;
    
    /**
     * length of the sentence 
    */
    private int length = -1;
    
    /**
     * original position in the document 
    */
    private int position = -1;
    
    /**
     * construct method
     * 
     * @param    value
     * @param    position
    */
    public Sentence(String value, int position)
    {
        this.value = value;
        this.position = position;
        this.length = value.length();
    }
    
    public Sentence(String value)
    {
        this(value, -1);
    }
    
    public int getPosition()
    {
        return position;
    }
    
    public Sentence setPosition(int position)
    {
        this.position = position;
        return this;
    }

    public String getValue()
    {
        return value;
    }

    public Sentence setValue(String value)
    {
        this.value = value;
        return this;
    }

    public int getLength()
    {
        return length;
    }

    public Sentence setLength(int length)
    {
        this.length = length;
        return this;
    }
    
    
    /**
     * rewrite the toString method
     * 
     * @return    String
    */
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append('{')
        .append("position=").append(position).append(',')
        .append("length=").append(length).append(',')
        .append("value=").append(value).append('}');
        
        return sb.toString();
    }
}
