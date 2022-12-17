package org.lionsoul.jcseg.segmenter;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

import org.lionsoul.jcseg.util.Util;

/**
 * <p>Jcseg segmenter configuration class</p>
 * 
 * @author  chenxin<chenxin619315@gmail.com>
 */
public class SegmenterConfig implements Cloneable, Serializable
{
    private static final long serialVersionUID = 1L;
    /**default lexicon property file name*/
    public static final String LEX_PROPERTY_FILE = "jcseg.properties";
    
    /**maximum length for maximum match(5-7) */
    public int MAX_LENGTH = 5;
    
    /**maximum length for Latin words */
    public int MAX_LATIN_LENGTH = 64;
    
    /**
     * maximum length for unit words
     * for the NLP algorithm added at 2016/11/18
    */
    public int MAX_UNIT_LENGTH = 5;
    
    /**identify the Chinese name? */
    public boolean I_CN_NAME = false;
    
    /**the max length for the adron of the Chinese last name.like 老陈 “老”*/
    public int MAX_CN_LNADRON = 1;
    
    /**whether to load the Pinyin of the CJK_WORDS*/
    public boolean LOAD_CJK_PINYIN = true;
    
    /**append the Pinyin to the result */
    public boolean APPEND_CJK_PINYIN = false;
    
    /**whether to load the word's part of speech*/
    public boolean LOAD_CJK_POS = true;
    
    /**append the part of speech.*/
    public boolean APPEND_PART_OF_SPEECH = false;
    
    /**whether to load the synonym word of the CJK_WORDS.*/
    public boolean LOAD_CJK_SYN = true;
    
    /**append the syn word to the result.*/
    public boolean APPEND_CJK_SYN = false;

    /**whether to load the entity define*/
    public boolean LOAD_CJK_ENTITY = true;
    
    /**do the entity recognition ? */
    public boolean APPEND_CJK_ENTITY = true;
    
    /**whether to load the self-define parameter*/
    public boolean LOAD_PARAMETER = true;
    
    /**
     * the threshold of the single word that is a single word
     * when it and the last char of the name make up a word.
     */
    public int NAME_SINGLE_THRESHOLD = 1000000;
    
    /**the maximum length for the text between the pair punctuation.*/
    public int PPT_MAX_LENGTH = 15;
    
    /**clear away the stop word.*/
    public boolean CLEAR_STOPWORD = false;
    
    /**Chinese numeric to Arabic .*/
    public boolean CNNUM_TO_ARABIC = true;
    
    /**Chinese fraction to Arabic fraction .*/
    public boolean CNFRA_TO_ARABIC = true;
    
    /**whether to do the secondary split for complex Latin compose by the type of the chars*/
    public boolean EN_SECOND_SEG = true;
    /**minimum length for the secondary segmentation word*/
    public int EN_SEC_MIN_LEN = 1;
    /** maximum/minimum match length for English word extract */
    public int EN_MAX_LEN = 16;
    /**do the English word extract*/
    public boolean EN_WORD_SEG = true;
    
    /**keep punctuation*/
    private String KEEP_PUNCTUATIONS = "@%&.'#+";
    
    /** char for delimiter segmentation default to English whitespace */
    private char DELIMITER = ' ';
    
    /** N for the n-gram */
    private byte GRAM = 1;
    
    public boolean KEEP_UNREG_WORDS = false;
    
    private String[] lexPath = null;        /*lexicon directory path array.*/
    private boolean lexAutoload = false;
    private int pollTime = 10;
    
    //the currently used lexicon properties file
    private String pFile = null;
    
    /** configuration items for cross segment implementation control */
    private boolean keepEnSecOriginalWord;
    private boolean keepEnSegOriginalWord;
    
    /**
     * create the config and do nothing about initialize
     * Note: this may cuz Incompatibility problems for the old version
     * that has use this construct method
     * 
     * @since 1.9.8
    */
    public SegmenterConfig() 
    {
        //do nothing here
    }
    
    /**
     * create and initialize the config by autoload
     * 
     * @param   autoLoad
    */
    public SegmenterConfig(boolean autoLoad)
    {
        if ( autoLoad ) {
            try {autoLoad();} catch (IOException e) {e.printStackTrace();}
        }
    }
    
    /**
     * create and initialize the task config from a properties file
     * 
     * @param   proFile
    */
    public SegmenterConfig( String proFile ) 
    {
        try {load(proFile);} catch (IOException e) {e.printStackTrace();}
    }
    
    /**
     * create and initialize the task config from a InputStream 
     * 
     * @param   is
    */
    public SegmenterConfig( InputStream is ) 
    {
        try {load(is);} catch (IOException e) {e.printStackTrace();}
    }
    
    /**
     * initialize the value of its options from a specified
     * jcseg.properties propertie file
     * 
     * @param   proFile 
     */
    public void load( String proFile ) throws IOException 
    {
        this.load(Files.newInputStream(Paths.get(proFile)));
    }
    
	/**
	 * initialize the value of its options by auto searching the jcesg.properties file:
	 * 
	 * <p>
	 * 1. Inside the dir that jcseg-core-{version}.jar is located, means beside the jar file.
	 * <p>
	 * 2. Search root classpath.
	 * <li>First, could manually put this file into root classpath (out of any jar file).
	 * <li>Second, there is a copy of this file inside jcseg-core-{version}.jar. It will be used if didn't manually copy this file into classpath.
	 * <p>
	 * 3. Load from system property "user.home".
	 */
    public void autoLoad() throws IOException 
    {
    	// Try load the file from beside jcseg-core-{version}.jar.
        File proFile = new File(Util.getJarHome(this)+"/"+LEX_PROPERTY_FILE);
        if ( proFile.exists() ) {
            pFile = proFile.getAbsolutePath();
            load(proFile.getAbsolutePath());
            return;
        }
        
        // Search root classpath, if didn't copy to classpath manually, then will find & use the one inside jcseg-core-{version}.jar.
        InputStream is = this.getClass().getResourceAsStream("/"+LEX_PROPERTY_FILE);
        if ( is != null ) {
            pFile = "classpath/jcseg.properties";
            load(is);
            return;
        }
            
        // Load from system property "user.home".
        proFile = new File(System.getProperty("user.home")+"/"+LEX_PROPERTY_FILE);
        if ( proFile.exists() ) {
            pFile = proFile.getAbsolutePath();
            load(proFile.getAbsolutePath());
        }
            
        /*
         * Jsceg properties file loading status report,
         * show the current properties file location information
         * 
         * @date 2013-07-06
         */
        String errString = "jcseg properties \"jcseg.properties]\" file auto loaded failed: \n";
        errString += "try the follwing ways to solve the problem: \n";
        errString += "1. put jcseg.properties into the classpath.\n";
        errString += "2. put jcseg.properties together with the jcseg-core-{version}.jar file.\n";
        errString += "3. put jcseg.properties in directory "+System.getProperty("user.home")+"\n\n";
        throw new IOException(errString);
    }
    
    /**
     * initialize the value of its options from a InputStream
     * of a jcseg.properties file
     * 
     * @param   is
    */
    public void load( InputStream is ) throws IOException
    {
        Properties lexPro = new Properties();
        lexPro.load(new BufferedInputStream(is));
        
        for ( java.util.Map.Entry<Object, Object> entry : lexPro.entrySet() ) {
        	set(entry.getKey().toString(), entry.getValue().toString());
        }
    }
    
    /**
     * internal method to check if the specified configuration item is open or not
     * 
     * @param	value
     * @return	boolean
    */
    private static boolean configBoolStatus(String value)
    {
    	return value.equals("1") || value.equals("true") || value.equals("on");
    }
    
    /**
     * set the option value from a specified key and value define in jcseg.properties
     * 
     * @param	key
     * @param	value
    */
    public void set(String key, String value) throws IOException
    {
    	if ( "lexicon.path".equals(key) ) {
    		if ( value == null ) {
    			throw new IOException("Missing lexicon.path property in jcseg.properties file!!!");
    		}
    		
            if ( ! "null".equalsIgnoreCase(value) ) {
                if (value.contains("{jar.dir}")) {
                	value = value.replace("{jar.dir}", Util.getJarHome(this));
                }
                
                // Multiple path for lexicon.path.
                lexPath = value.split(";");
                for ( int i = 0; i < lexPath.length; i++ ) {
                    lexPath[i] = java.net.URLDecoder.decode(lexPath[i], "UTF-8");
                    final File f = new File(lexPath[i]);
                    if ( ! f.exists() ) {
                        throw new IOException("Invalid sub lexicon path " + lexPath[i] 
                                + " for lexicon.path in jcseg.properties");
                    }
                }
            }
    	} else if ( "jcseg.maxlen".equals(key) ) {
            MAX_LENGTH = Integer.parseInt(value);
        } else if ( "jcseg.icnname".equals(key) ) {
        	I_CN_NAME = configBoolStatus(value);
        } else if ( "jcseg.cnmaxlnadron".equals(key) ) {
            MAX_CN_LNADRON = Integer.parseInt(value);
        } else if ( "jcseg.nsthreshold".equals(key) ) {
            NAME_SINGLE_THRESHOLD = Integer.parseInt(value);
        } else if ( "jcseg.pptmaxlen".equals(key) ) {
            PPT_MAX_LENGTH = Integer.parseInt(value);
        } else if ( "jcseg.loadpinyin".equals(key) ) {
        	LOAD_CJK_PINYIN = configBoolStatus(value);
        } else if ( "jcseg.appendpinyin".equals(key) ) {
        	APPEND_CJK_PINYIN = configBoolStatus(value);
        } else if ( "jcseg.loadsyn".equals(key) ) {
        	LOAD_CJK_SYN = configBoolStatus(value);
        } else if ( "jcseg.appendsyn".equals(key) ) {
        	APPEND_CJK_SYN = configBoolStatus(value);
        } else if ( "jcseg.loadpos".equals(key) ) {
        	LOAD_CJK_POS = configBoolStatus(value);
        } else if ( "jcseg.loadentity".equals(key) ) {
        	LOAD_CJK_ENTITY = configBoolStatus(value);
        } else if ( "jcseg.loadparameter".equals(key) ) {
        	LOAD_PARAMETER = configBoolStatus(value);
        } else if ( "jcseg.clearstopword".equals(key) ) {
        	CLEAR_STOPWORD = configBoolStatus(value);
        } else if ( "jcseg.cnnumtoarabic".equals(key) ) {
        	CNNUM_TO_ARABIC = configBoolStatus(value);
        } else if ( "jcseg.cnfratoarabic".equals(key) ) {
        	CNFRA_TO_ARABIC = configBoolStatus(value);
        } else if ( "jcseg.keepunregword".equals(key) ) {
        	KEEP_UNREG_WORDS = configBoolStatus(value);
        } else if ( "lexicon.autoload".equals(key) ) {
        	lexAutoload = configBoolStatus(value);
        } else if ( "lexicon.polltime".equals(key) ) {
            pollTime = Integer.parseInt(value);
        } else if ( "jcseg.ensecondseg".equals(key) ) {
        	EN_SECOND_SEG = configBoolStatus(value);
        } else if ( "jcseg.ensecminlen".equals(key) ) {
            EN_SEC_MIN_LEN = Integer.parseInt(value);
        } else if ( "jcseg.enmaxlen".equals(key) ) {
        	EN_MAX_LEN = Integer.parseInt(value);
        } else if ( "jcseg.enwordseg".equals(key) ) {
        	EN_WORD_SEG = configBoolStatus(value);
        } else if ( "jcseg.keeppunctuations".equals(key) ) {
            KEEP_PUNCTUATIONS = value;
        } else if ( "jcseg.delimiter".equals(key) ) {
        	DELIMITER = (value.equals("whitespace") || value.equals("default")) ? ' ' : value.charAt(0);
        } else if ( "jcseg.gram".equals(key) ) {
        	GRAM = value.equals("default") ? 1 : Byte.valueOf(value);
        }
    }
    
    /**return the lexicon directory path*/
    public String[] getLexiconPath()
    {
        return lexPath;
    }
    
    public void setLexiconPath( String[] lexPath )
    {
        this.lexPath = lexPath;
    }
    
    /**about lexicon autoload*/
    public boolean isAutoload()
    {
        return lexAutoload;
    }
    
    public void setAutoload( boolean autoload )
    {
        lexAutoload = autoload;
    }
    
    public int getPollTime()
    {
        return pollTime;
    }
    
    public void setPollTime( int polltime )
    {
        this.pollTime = polltime;
    }

    public int getMaxLength()
    {
        return MAX_LENGTH;
    }

    public void setMaxLength( int maxLength )
    {
        MAX_LENGTH = maxLength;
    }
    
    public boolean identifyCnName()
    {
        return I_CN_NAME;
    }

    public void setICnName( boolean iCnName )
    {
        I_CN_NAME = iCnName;
    }

    public int getMaxCnLnadron()
    {
        return MAX_CN_LNADRON;
    }

    public void setMaxCnLnadron( int maxCnLnadron )
    {
        MAX_CN_LNADRON = maxCnLnadron;
    }

    public boolean loadCJKPinyin()
    {
        return LOAD_CJK_PINYIN;
    }

    public void setLoadCJKPinyin( boolean loadCJKPinyin )
    {
        LOAD_CJK_PINYIN = loadCJKPinyin;
    }
    
    public void setAppendPartOfSpeech( boolean partOfSpeech )
    {
        APPEND_PART_OF_SPEECH = partOfSpeech;
    }

    public boolean appendCJKPinyin()
    {
        return APPEND_CJK_PINYIN;
    }

    public void setAppendCJKPinyin( boolean appendCJKPinyin )
    {
        APPEND_CJK_PINYIN = appendCJKPinyin;
    }

    public boolean loadCJKSyn()
    {
        return LOAD_CJK_SYN;
    }

    public void setLoadCJKSyn( boolean loadCJKSyn )
    {
        LOAD_CJK_SYN = loadCJKSyn;
    }

    public boolean appendCJKSyn()
    {
        return APPEND_CJK_SYN;
    }

    public void setAppendCJKSyn( boolean appendCJKPinyin )
    {
        APPEND_CJK_SYN = appendCJKPinyin;
    }

    public boolean ladCJKPos()
    {
        return LOAD_CJK_POS;
    }

    public void setLoadCJKPos( boolean loadCJKPos )
    {
        LOAD_CJK_POS = loadCJKPos;
    }
    
    public boolean loadCJKEntity()
    {
        return LOAD_CJK_ENTITY;
    }
    
    public void setLoadEntity( boolean loadEntity ) 
    {
        LOAD_CJK_ENTITY = loadEntity;
    }

    public int getNameSingleThreshold()
    {
        return NAME_SINGLE_THRESHOLD;
    }

    public void setNameSingleThreshold( int thresold )
    {
        NAME_SINGLE_THRESHOLD = thresold;
    }

    public int getPPTMaxLength()
    {
        return PPT_MAX_LENGTH;
    }

    public void setPPT_MAX_LENGTH( int pptMaxLength )
    {
        PPT_MAX_LENGTH = pptMaxLength;
    }

    public boolean clearStopwords()
    {
        return CLEAR_STOPWORD;
    }

    public void setClearStopwords( boolean clearstopwords )
    {
        CLEAR_STOPWORD = clearstopwords;
    }

    public boolean cnNumToArabic()
    {
        return CNNUM_TO_ARABIC;
    }

    public void setCnNumToArabic( boolean cnNumToArabic )
    {
        CNNUM_TO_ARABIC = cnNumToArabic;
    }

    public boolean cnFractionToArabic()
    {
        return CNFRA_TO_ARABIC;
    }

    public void setCnFactionToArabic( boolean cnFractionToArabic )
    {
        CNFRA_TO_ARABIC = cnFractionToArabic;
    }
    
    public boolean getEnSecondSeg()
    {
        return EN_SECOND_SEG;
    }
    
    public void setEnSecondSeg( boolean enSecondSeg )
    {
        this.EN_SECOND_SEG = enSecondSeg;
    }
    
    public int getEnSecondMinLen()
    {
        return EN_SEC_MIN_LEN;
    }
    
    public void setEnSecondMinLen( int minLen )
    {
    	EN_SEC_MIN_LEN = minLen;
    }
    
    public int getEnMaxLen() {
		return EN_MAX_LEN;
	}

	public void setEnMaxLen(int enMaxLen) {
		EN_MAX_LEN = enMaxLen;
	}

	public boolean isEnWordSeg() {
		return EN_WORD_SEG;
	}

	public void setEnWordSeg(boolean enWordSeg) {
		EN_WORD_SEG = enWordSeg;
	}

	public void setKeepPunctuations( String keepPunctuations ) {
        KEEP_PUNCTUATIONS = keepPunctuations;
    }
    
    public boolean isKeepPunctuation( char c ) {
        return (KEEP_PUNCTUATIONS.indexOf(c) > -1);
    }
    
    public char getDELIMITER() {
		return DELIMITER;
	}

	public void setDELIMITER(char dELIMITER) {
		DELIMITER = dELIMITER;
	}

	public byte getGRAM() {
		return GRAM;
	}

	public void setGRAM(byte gRAM) {
		GRAM = gRAM;
	}

	public boolean keepUnregWords() {
        return KEEP_UNREG_WORDS;
    }
    
    public void setKeepUnregWords( boolean keepUnregWords ) {
        KEEP_UNREG_WORDS = keepUnregWords;
    }
    
    //return the currently used properties file
    public String getPropertieFile() {
        return pFile;
    }
    
    public boolean isKeepEnSecOriginalWord() {
		return keepEnSecOriginalWord;
	}

	public void setKeepEnSecOriginalWord(boolean keepEnSecOriginalWord) {
		this.keepEnSecOriginalWord = keepEnSecOriginalWord;
	}

	public boolean isKeepEnSegOriginalWord() {
		return keepEnSegOriginalWord;
	}

	public void setKeepEnSegOriginalWord(boolean keepEnSegOriginalWord) {
		this.keepEnSegOriginalWord = keepEnSegOriginalWord;
	}

	/**
     * rewrite the clone method
     * 
     * @return	SegmenterConfig
    */
    @Override
    public SegmenterConfig clone() throws CloneNotSupportedException {
        return (SegmenterConfig) super.clone();
    }
    
}
