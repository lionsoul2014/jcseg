package org.lionsoul.jcseg.util;

import org.lionsoul.jcseg.IWord;
import org.lionsoul.jcseg.segmenter.Entity;
import org.lionsoul.jcseg.segmenter.Word;

/**
 * Time Util class
 * 
 * @author  chenxin<chenxin619315@gmail.com>
*/
public class TimeUtil
{
    /**
     * date-time part index constants
     * we consider a date-time as the following seven parts:
     * +------+-------+-----+---------------+------+--------+--------+
     * | 0    | 1     | 2   | 3             | 4    | 5      | 6      |
     * +------+-------+-----+---------------+------+--------+--------+
     * | year | month | day | timing method | hour | minute | second |
     * +------+-------+-----+---------------+------+--------+--------+
     * and the numeric value before every part.
     * 
     * Note: {@link org.lionsoul.jcseg.segmenter.NLPSeg}'s date-time recognition base on this logic
     * 1, the odd index is the entity part
     * 2, the even index is the numeric value part
    */
    public static final int DATETIME_NONE = -1;
    public static final int DATETIME_Y = 0;
    public static final int DATETIME_M = 1;
    public static final int DATETIME_D = 2;
    public static final int DATETIME_A = 3;
    public static final int DATETIME_H = 4;
    public static final int DATETIME_I = 5;
    public static final int DATETIME_S = 6;
    
    private static final String[] KeyMap = {
        "y", "m", "d", "tm", "h", "i", "s"
    };
    
    /**
     * get and return the time part index of the specified IWord#entity
     * 
     * @param   entity
     * @return  int or -1 for failed
    */
    public static final int getDateTimeIndex(String entity)
    {
        if ( entity == null ) {
            return -1;
        }
        
        int sIdx = entity.indexOf('.');
        if ( sIdx == -1 ) {
            return DATETIME_NONE;
        }
        
        String attr = entity.substring(sIdx+1);
        if ( attr.length() < 1 ) {
            return DATETIME_NONE;
        }
        
        if ( attr.startsWith("y") ) return DATETIME_Y;
        if ( attr.startsWith("m") ) return DATETIME_M;
        if ( attr.startsWith("d") ) return DATETIME_D;
        if ( attr.startsWith("a") ) return DATETIME_A;
        if ( attr.startsWith("h") ) return DATETIME_H;
        if ( attr.startsWith("i") ) return DATETIME_I;
        if ( attr.startsWith("s") ) return DATETIME_S;
          
        return DATETIME_NONE;
    }
    
    /**
     * create and return a date-time pool
     * 
     * @return  IWord[]
    */
    public static final IWord[] createDateTimePool()
    {
        return new IWord[]{
            null,   //year
            null,   //month
            null,   //day
            null,   //timing method
            null,   //hour
            null,   //minute
            null,   //seconds
        };
    }
    
    /**
     * fill the date-time pool specified part through the specified
     * time entity string.
     * 
     * @param   wPool
     * @param   word
     * @return  int
    */
    public static final int fillDateTimePool(IWord[] wPool, IWord word)
    {
        int pIdx = getDateTimeIndex(word.getEntity(0));
        if ( pIdx == DATETIME_NONE ) {
            return DATETIME_NONE;
        }
        
        if ( wPool[pIdx] == null ) {
            wPool[pIdx] = word;
            return pIdx;
        }
        
        return DATETIME_NONE;
    }
    
    /**
     * fill the date-time pool specified part with part index constant
     * 
     * @param   wPool
     * @param   pIdx
     * @param   word
    */
    public static final void fillDateTimePool(
            IWord[] wPool, int pIdx, IWord word)
    {
        if ( wPool[pIdx] == null ) {
            wPool[pIdx] = word;
        }
    }

    /**
     * get and return the time key part of the specified entity string 
     * 
     * @param   entity
     * @return  String
    */
    public static final String getTimeKey(String entity)
    {
        if ( entity == null ) {
            return null;
        }
        
        int sIdx = entity.indexOf('.');
        if ( sIdx == -1 ) {
        	return null;
        }
        
        return entity.substring(sIdx + 1);
    }
    
    public static final String getTimeKey(IWord word)
    {
        return getTimeKey(word.getEntity(0));
    }
    
    /**
     * get and return the time key part with the part index value
     * 
     * @param   pIdx
     * @return  String
    */
    public static final String getTimeKey(int pIdx)
    {
        if ( pIdx < 0 || pIdx > 12 ) {
            return null;
        }
        
        return KeyMap[pIdx];
    }
    
    
    /**
     * fill a date-time time part with a standard time format like '15:45:36' 
     * to the specified time pool
     * 
     * @param   wPool
     * @param   timeVal
    */
    public static final void fillTimeToPool(
            IWord[] wPool, String timeVal)
    {
        String[] p = timeVal.split(":");
        TimeUtil.fillDateTimePool(wPool, TimeUtil.DATETIME_H, 
                new Word(p[0]+"点", IWord.T_CJK_WORD, Entity.E_TIME_H_A));
        TimeUtil.fillDateTimePool(wPool, TimeUtil.DATETIME_I, 
                new Word(p[1]+"分", IWord.T_CJK_WORD, Entity.E_TIME_I_A));
        
        if ( p.length == 3 ) {
            TimeUtil.fillDateTimePool(wPool, TimeUtil.DATETIME_S, 
                    new Word(p[2]+"秒", IWord.T_CJK_WORD, Entity.E_TIME_S_A));
        }
    }
    
}
