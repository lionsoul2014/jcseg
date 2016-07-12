package org.lionsoul.jcseg.tokenizer.core;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.lionsoul.jcseg.util.Util;

/**
 * <p>Jcseg segmentation task configuration class</p>
 * 
 * @author  chenxin<chenxin619315@gmail.com>
 */
public class JcsegTaskConfig implements Cloneable
{
    /**default lexicon property file name*/
    public static final String LEX_PROPERTY_FILE = "jcseg.properties";
    
    /**simple algorithm or complex algorithm */
    public static final int SIMPLE_MODE  = 1;
    public static final int COMPLEX_MODE = 2;
    public static final int DETECT_MODE  = 3;
    public static final int SEARCH_MODE  = 4;
    
    /**maximum length for maximum match(5-7)*/
    public int MAX_LENGTH = 5;
    
    /**
     * maximum length for the chinese words after the LATIN word.
     *         use to match chinese and english mix word, like 'B超,AA制...'
     */
    public int MIX_CN_LENGTH = 2;
    
    /**identify the chinese name? */
    public boolean I_CN_NAME = false;
    
    /**the max length for the adron of the chinese last name.like 老陈 “老”*/
    public int MAX_CN_LNADRON = 1;
    
    /**wether to load the pinying of the CJK_WORDS*/
    public boolean LOAD_CJK_PINYIN = false;
    
    /**append the pinying to the splited IWord*/
    public boolean APPEND_CJK_PINYIN = false;
    
    /**append the part of speech.*/
    public boolean APPEND_PART_OF_SPEECH = false;
    
    /**wether to load the syn word of the CJK_WORDS.*/
    public boolean LOAD_CJK_SYN = false;
    
    /**append the syn word to the splited IWord.*/
    public boolean APPEND_CJK_SYN = true;
    
    /**wether to load the word's part of speech*/
    public boolean LOAD_CJK_POS = false;
    
    /**
     * the threshold of the single word that is a single word
     * when it and the last char of the name make up a word.
     */
    public int NAME_SINGLE_THRESHOLD = 1000000;
    
    /**the maxinum length for the text bettween the pair punctution.*/
    public int PPT_MAX_LENGTH = 15;
    
    /**clear away the stopword.*/
    public boolean CLEAR_STOPWORD = false;
    
    /**chinese numeric to Arabic .*/
    public boolean CNNUM_TO_ARABIC = true;
    
    /**chinese fraction to arabic fraction .*/
    public boolean CNFRA_TO_ARABIC = true;
    
    /**Wether to do the secondary split for complex latin compose*/
    public boolean EN_SECOND_SEG = true;
    /**Less length for the second split to make up a word*/
    public int STOKEN_MIN_LEN = 1;
    
    /**keep puncutations*/
    private String KEEP_PUNCTUATIONS = "@%&.'#+";
    
    public boolean KEEP_UNREG_WORDS = false;
    
    private String prefix = "lex";
    private String suffix = "lex";
    private String[] lexPath = null;        /*lexicon direcotry path array.*/
    private boolean lexAutoload = false;
    private int polltime = 10;
    
    //the currently used lexicon properties file
    private String pFile = null;
    
    /**
     * create the config and do nothing about initialize
     * Note: this may cuz Incompatibility problems for the old version
     * that has use this construct method
     * 
     * @since 1.9.8
    */
    public JcsegTaskConfig() 
    {
        //do nothing here
    }
    
    /**
     * create and initialize the config by auto load
     * 
     * @param   autoLoad
    */
    public JcsegTaskConfig(boolean autoLoad)
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
    public JcsegTaskConfig( String proFile ) 
    {
        try {load(proFile);} catch (IOException e) {e.printStackTrace();}
    }
    
    /**
     * create and initialize the task config from a InputStream 
     * 
     * @param   is
    */
    public JcsegTaskConfig( InputStream is ) 
    {
        try {load(is);} catch (IOException e) {e.printStackTrace();}
    }
    
    /**
     * initialize the value of its options from a speicfied 
     * jcseg.properties propertie file
     * 
     * @param   proFile 
     * @throws  IOException
     */
    public void load( String proFile ) throws IOException 
    {
        this.load(new FileInputStream(proFile));
    }
    
    /**
     * initialize the value of its options by auto searching the jcesg.properties file:
     * 
     * 1. the path that jcseg-core-{version}.jar located
     * 2. the classpath - the property file in the jcseg-core-{version}.jar zip file
     * 3. user home dir
     * 
     * @throws  IOException
    */
    public void autoLoad() throws IOException 
    {
        /*
         * 1.load the the jcseg.properties located with the jar file.
         * 2.load the jcseg.propertiess from the classpath.
         * 3.load the jcseg.properties from the user.home 
         */
        
        File proFile = new File(Util.getJarHome(this)+"/"+LEX_PROPERTY_FILE);
        if ( proFile.exists() ) {
            pFile = proFile.getAbsolutePath();
            load(proFile.getAbsolutePath());
            return;
        }
        
        /*
         * by default we have package a jcseg.properties file
         * with default setting insite the class path
        */
        InputStream is = this.getClass().getResourceAsStream("/"+LEX_PROPERTY_FILE);
        if ( is != null ) {
            pFile = "classpath/jcseg.properties";
            load(is);
            return;
        }
            
        proFile = new File(System.getProperty("user.home")+"/"+LEX_PROPERTY_FILE);
        if ( proFile.exists() ) {
            pFile = proFile.getAbsolutePath();
            load(proFile.getAbsolutePath());
        }
            
        /*
         * jcseg properties file loading status report,
         * show the crorrent properties file location information
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
     * of a jcseg.properties prperties file
     * 
     * @param   is
     * @throws  IOException 
    */
    public void load( InputStream is ) throws IOException
    {
        Properties lexPro = new Properties();
        lexPro.load(new BufferedInputStream(is));
        
        //about the lexicon, the lexicon path
        String lexDirs = lexPro.getProperty("lexicon.path");
        if ( lexDirs == null ) {
            throw new IOException("Missing lexicon.path property in jcseg.properties file!!!");
        }
        
        if ( ! "null".equalsIgnoreCase(lexDirs) ) {
            if ( lexDirs.indexOf("{jar.dir}") > -1 ) {
                lexDirs = lexDirs.replace("{jar.dir}", Util.getJarHome(this));
            }
            
            //Multiple path for lexicon.path.
            lexPath = lexDirs.split(";");
            File f  = null;
            for ( int i = 0; i < lexPath.length; i++ ) {
                lexPath[i] = java.net.URLDecoder.decode(lexPath[i], "UTF-8");
                f = new File(lexPath[i]);
                if ( ! f.exists() ) {
                    throw new IOException("Invalid sub lexicon path " + lexPath[i] 
                            + " for lexicon.path in jcseg.properties");
                }
                f = null;    //Let gc do its work.
            }
        }
        
        //the lexicon file prefix and suffix
        if ( lexPro.getProperty("lexicon.suffix") != null )
            suffix = lexPro.getProperty("lexicon.suffix");
        if ( lexPro.getProperty("lexicon.prefix") != null )
            prefix = lexPro.getProperty("lexicon.prefix");
        
        //reset all the options
        if ( lexPro.getProperty("jcseg.maxlen") != null )
            MAX_LENGTH = Integer.parseInt(lexPro.getProperty("jcseg.maxlen"));
        if ( lexPro.getProperty("jcseg.mixcnlen") != null )
            MIX_CN_LENGTH = Integer.parseInt(lexPro.getProperty("jcseg.mixcnlen"));
        if ( lexPro.getProperty("jcseg.icnname") != null
                && lexPro.getProperty("jcseg.icnname").equals("1"))
            I_CN_NAME = true;
        if ( lexPro.getProperty("jcseg.cnmaxlnadron") != null )
            MAX_CN_LNADRON = Integer.parseInt(lexPro.getProperty("jcseg.cnmaxlnadron"));
        if ( lexPro.getProperty("jcseg.nsthreshold") != null )
            NAME_SINGLE_THRESHOLD = Integer.parseInt(lexPro.getProperty("jcseg.nsthreshold"));
        if ( lexPro.getProperty("jcseg.pptmaxlen") != null ) 
            PPT_MAX_LENGTH = Integer.parseInt(lexPro.getProperty("jcseg.pptmaxlen"));
        if ( lexPro.getProperty("jcseg.loadpinyin") != null
                && lexPro.getProperty("jcseg.loadpinyin").equals("1")) 
            LOAD_CJK_PINYIN = true;
        if ( lexPro.getProperty("jcseg.loadsyn") != null
                && lexPro.getProperty("jcseg.loadsyn").equals("1") )
            LOAD_CJK_SYN = true;
        if ( lexPro.getProperty("jcseg.loadpos") != null
                && lexPro.getProperty("jcseg.loadpos").equals("1")) 
            LOAD_CJK_POS = true;
        if ( lexPro.getProperty("jcseg.clearstopword") != null
                && lexPro.getProperty("jcseg.clearstopword").equals("1"))
            CLEAR_STOPWORD = true;
        if ( lexPro.getProperty("jcseg.cnnumtoarabic") != null
                && lexPro.getProperty("jcseg.cnnumtoarabic").equals("0"))
            CNNUM_TO_ARABIC = false;
        if ( lexPro.getProperty("jcseg.cnfratoarabic") != null
                && lexPro.getProperty("jcseg.cnfratoarabic").equals("0"))
            CNFRA_TO_ARABIC = false;
        if ( lexPro.getProperty("jcseg.keepunregword") != null
                && lexPro.getProperty("jcseg.keepunregword").equals("1"))
            KEEP_UNREG_WORDS = true;
        if ( lexPro.getProperty("lexicon.autoload") != null
                && lexPro.getProperty("lexicon.autoload").equals("1"))
            lexAutoload = true;
        if ( lexPro.getProperty("lexicon.polltime") != null )
            polltime = Integer.parseInt(lexPro.getProperty("lexicon.polltime"));
        
        //secondary split
        if ( lexPro.getProperty("jcseg.ensencondseg") != null
                && lexPro.getProperty("jcseg.ensencondseg").equals("0"))
            EN_SECOND_SEG = false;
        if ( lexPro.getProperty("jcseg.stokenminlen") != null )
            STOKEN_MIN_LEN = Integer.parseInt(lexPro.getProperty("jcseg.stokenminlen"));
        
        //load the keep punctuations.
        if ( lexPro.getProperty("jcseg.keeppunctuations") != null )
            KEEP_PUNCTUATIONS = lexPro.getProperty("jcseg.keeppunctuations");
    }
    
    /**property about lexicon file.*/
    public String getLexiconFilePrefix()
    {
        return prefix;
    }
    
    public String getLexiconFileSuffix()
    {
        return suffix;
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
        return polltime;
    }
    
    public void setPollTime( int polltime )
    {
        this.polltime = polltime;
    }

    public int getMaxLength()
    {
        return MAX_LENGTH;
    }

    public void setMaxLength( int maxLength )
    {
        MAX_LENGTH = maxLength;
    }

    public int getMixCnLength()
    {
        return MIX_CN_LENGTH;
    }

    public void setMixCnLength( int mixCnLength )
    {
        MIX_CN_LENGTH = mixCnLength;
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
    
    public int getSTokenMinLen()
    {
        return STOKEN_MIN_LEN;
    }
    
    public void setSTokenMinLen( int len )
    {
        STOKEN_MIN_LEN = len;
    }
    
    public void setKeepPunctuations( String keepPunctuations )
    {
        KEEP_PUNCTUATIONS = keepPunctuations;
    }
    
    public boolean isKeepPunctuation( char c )
    {
        return (KEEP_PUNCTUATIONS.indexOf(c) > -1);
    }
    
    public boolean keepUnregWords()
    {
        return KEEP_UNREG_WORDS;
    }
    
    public void setKeepUnregWords( boolean keepUnregWords )
    {
        KEEP_UNREG_WORDS = keepUnregWords;
    }
    
    //return the currently use properties file
    public String getPropertieFile()
    {
        return pFile;
    }
    
    /**
     * rewrite the clone method
     * 
     * @return  JcsegTaskConfig
    */
    @Override
    public JcsegTaskConfig clone() throws CloneNotSupportedException
    {
        return (JcsegTaskConfig) super.clone();
    }
    
}
