package org.lionsoul.jcseg.tokenizer.core;

/**
 * Word interface
 * 
 * @author    chenxin<chenxin619315@gmail.com>
 */
public interface IWord extends Cloneable
{    
    public static final String[] NAME_POSPEECH = {"nr"};
    public static final String[] NUMERIC_POSPEECH = {"m"};
    public static final String[] EN_POSPEECH  = {"en"};
    public static final String[] MIX_POSPEECH = {"mix"};
    public static final String[] PPT_POSPEECH = {"nz"};
    public static final String[] PUNCTUATION  = {"w"};
    public static final String[] UNRECOGNIZE  = {"urg"};
            
    /**
     * China,JPanese,Korean words 
     */
    public static final int T_CJK_WORD = 1;
    
    /**
     * Chinese and English mix word like B超,SIM卡. 
     */
    public static final int T_MIXED_WORD = 2;
    
    /**
     * Chinese last name. 
     */
    public static final int T_CN_NAME = 3;
    
    /**
     * Chinese nickname like: 老陈 
     */
    public static final int T_CN_NICKNAME = 4;
    
    /**
     * Latin series.
     * including the Arabic numbers.
     */
    public static final int T_BASIC_LATIN = 5;
    
    /**
     * letter number like 'ⅠⅡ' 
     */
    public static final int T_LETTER_NUMBER = 6;
    
    /**
     * other number like '①⑩⑽㈩' 
     */
    public static final int T_OTHER_NUMBER = 7;
    
    /**
     * Pinyin 
     */
    public static final int T_CJK_PINYIN = 8;
    
    /**
     * Chinese numeric
    */
    public static final int T_CN_NUMERIC = 9;
    
    public static final int T_PUNCTUATION = 10;
    
    /**
     * useless chars like the CJK punctuation
     */
    public static final int T_UNRECOGNIZE_WORD = 11;
    
    ///------------------entity define---------------------
    public static final String E_NAME = "name";
    public static final String E_NAME_CN = "name.cn";
    public static final String E_NAME_FOREIGN = "name.foreign";
    
    public static final String E_PLACE = "place";
    public static final String E_PLACE_CONTINENT = "place.continent";
    public static final String E_PLACE_NATION = "place.nation";
    public static final String E_PLACE_PROVINCE = "place.province";
    public static final String E_PLACE_DISTRICT = "place.district";
    public static final String E_PLACE_TOWNSHIP = "place.township";
    public static final String E_PLACE_VILLAGE = "place.village";
    
    public static final String E_DATE = "date";
    public static final String E_DATE_YEAR = "date.year";
    public static final String E_DATE_MONTH = "date.month";
    public static final String E_DATE_DAY = "date.day";
    
    public static final String E_TIME = "time";
    public static final String E_TIME_HOUR = "time.hour";
    public static final String E_TIME_MINUTE = "time.minute";
    public static final String E_TIME_SECOND = "time.second";
    
    public static final String E_LENGTH = "length";
    public static final String E_LENGTH_METER = "length.meter";
    public static final String E_LENGTH_KILOMETER = "length.kilometer";
    public static final String E_LENGTH_CENTIMETER = "length.centimeter";
    public static final String E_LENGTH_DECIMETER = "length.decimeter";
    public static final String E_LENGTH_MILLIMETER = "length.millimeter";
    public static final String E_LENGTH_MICRON = "length.micron";
    public static final String E_LENGTH_NANO = "length.nano";
    public static final String E_LENGTH_INCH = "length.inch";
    public static final String E_LENGTH_FOOT = "length.foot";
    public static final String E_LENGTH_TENFEET = "length.tenfeet";
    
    public static final String E_DISTANCE = "distance";
    public static final String E_DISTANCE_LI = "distance.li";
    public static final String E_DISTANCE_KILOLI = "distance.kiloli";
    public static final String E_DISTANCE_KILOMETER = "distance.kilometer";
    public static final String E_DISTANCE_MILE = "distance.mile";
    public static final String E_DISTANCE_NAUTICALMILE = "distance.nauticalmile";
    
    public static final String E_MONEY = "money";
    //check the following link for more information
    //http://baike.baidu.com/link?url=CwmERmQUN2_pIaGDUv9eeHgQnh3Do5g4VN7jp9HHuJg2qx5XedVKKUVhHstNymJJWd4eNsjVTptJJdLpxvGNwa
    public static final String E_MONEY_USD = "money.usd";   //美元
    public static final String E_MONEY_HKD = "money.hkd";   //港元
    public static final String E_MONEY_MOP = "money.mop";   //澳门元
    public static final String E_MONEY_TWD = "money.twd";   //台币
    public static final String E_MONEY_KWP = "money.kwp";   //朝鲜元
    public static final String E_MONEY_EUR = "money.eur";   //欧元
    public static final String E_MONEY_KRW = "money.krw";   //韩元
    public static final String E_MONEY_VND = "money.vnd";   //越南盾
    public static final String E_MONEY_JPY = "money.jpy";   //日元
    public static final String E_MONEY_SGD = "money.sgd";   //新加坡元
    public static final String E_MONEY_THB = "money.thb";   //泰铢
    public static final String E_MONEY_BUK = "money.buk";   //缅元
    public static final String E_MONEY_INR = "money.inr";   //卢比
    public static final String E_MONEY_GBP = "money.gbp";   //英镑
    public static final String E_MONEY_FRF = "money.frf";   //法郎
    public static final String E_MONEY_CAD = "money.cad";   //加元
    public static final String E_MONEY_DEM = "money.dem";   //马克
    public static final String E_MONEY_ITL = "money.itl";   //里拉
    public static final String E_MONEY_EGP = "money.egp";   //埃及镑
    public static final String E_MONEY_MZD = "money.nzd";   //新西兰元
    
    /**
     * return the value of the word
     * 
     * @return String
     */
    public String getValue();
    
    /**
     * return the length of the word
     * 
     * @return int
     */
    public int getLength();
    
    /**
     * self define the length
     * 
     * @param    length
     */
    public void setLength( int length );
    
    /**
     * return the frequency of the word,
     *     use only when the word's length is one.
     * 
     * @return int
     */
    public int getFrequency();
    
    /**
     * return the type of the word
     * 
     * @return int
     */
    public int getType();
    
    /**
     * set the position of the word
     * 
     * @param pos
     */
    public void setPosition( int pos );
    
    /**
     * return the start position of the word.
     * 
     * @return int
     */
    public int getPosition();
    
    /**
     * get the entity name of the word
     * 
     * @return  String
    */
    public String getEntity();
    
    /**
     * set the entity name of the word
     * 
     * @param   entity
    */
    public void setEntity(String entity);
    
    /**
     * return the pinying of the word 
     */
    public String getPinyin();
    
    /**
     * return the syn words of the word.
     * 
     * @return String[]
     */
    public String[] getSyn();
    
    public void setSyn( String[] syn );
    
    /**
     * return the part of speech of the word.
     * 
     * @return String[]
     */
    public String[] getPartSpeech();
    
    public void setPartSpeech( String[] ps );
    
    /**
     * set the pinying of the word
     * 
     * @param py
     */
    public void setPinyin( String py );
    
    /**
     * add a new part to speech to the word.
     * 
     * @param ps
     */
    public void addPartSpeech( String ps );
    
    /**
     * add a new syn word to the word.
     * 
     * @param s
     */
    public void addSyn( String s );
    
    /**
     * I mean: you have to rewrite the equals method
     *  cause the Jcseg require it 
     */
    @Override
    public boolean equals( Object o );
    
    /**
     * make clone available
     */
    public IWord clone();
}
