package org.lionsoul.jcseg.tokenizer.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.CodeSource;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.lionsoul.jcseg.util.StringUtil;


/**
 * Dictionary abstract super class
 * 
 * @author  chenxin<chenxin619315@gmail.com>
 */
public abstract class ADictionary 
{
    /**
     * the default autoload task file name 
    */
    public static final String AL_TODO_FILE = "lex-autoload.todo";
    
    protected JcsegTaskConfig config;
    protected boolean sync;
    
    /**autoload thread */
    private Thread autoloadThread = null;
    
    /**
     * initialize the ADictionary
     * 
     * @param   config
     * @param   sync
    */
    public ADictionary( JcsegTaskConfig config, Boolean sync ) 
    {
        this.sync   = sync.booleanValue();
        this.config = config;
    }
    
    /**
     * load all the words from a specified lexicon file
     * 
     * @param   file
     * @throws  IOException 
     * @throws  FileNotFoundException 
     * @throws  NumberFormatException 
     */
    public void load( File file ) 
            throws NumberFormatException, FileNotFoundException, IOException
    {
        loadWords(config, this, file);
    }
    
    /**
     * load all the words from a specified lexicon path
     * 
     * @param   file
     * @throws  IOException 
     * @throws  FileNotFoundException 
     * @throws  NumberFormatException 
    */
    public void load( String file ) 
            throws NumberFormatException, FileNotFoundException, IOException
    {
        loadWords(config, this, file);
    }
    
    /**
     * load all the words from a specified lexicon input stream
     * 
     * @param   is
     * @throws  IOException 
     * @throws  NumberFormatException 
    */
    public void load( InputStream is ) throws NumberFormatException, IOException
    {
        loadWords(config, this, is);
    }
    
    /**
     * load the all the words form all the files under a specified lexicon directionry
     * 
     * @param   lexDir
     * @throws  IOException
     */
    public void loadDirectory( String lexDir ) throws IOException
    {
        File path = new File(lexDir);
        if ( path.exists() == false ) {
            throw new IOException("Lexicon directory ["+lexDir+"] does'n exists.");
        }
        
        /*
         * load all the lexicon file under the lexicon path 
         *     that start with __prefix and end with __suffix.
         */
        final String __suffix = config.getLexiconFileSuffix();
        final String __prefix = config.getLexiconFilePrefix();
        File[] files = path.listFiles(new FilenameFilter(){
            @Override
            public boolean accept(File dir, String name) {
                return ( name.startsWith(__prefix)
                            && name.endsWith(__suffix));
            }
        });
        
        for ( File file : files ) {
            load(file);
        }
    }
    
    /**
     * load all the words from all the files under the specified class path.
     * 
     * added at 2016/07/12:
     * only in the jar file could the ZipInputStream available
     * add IDE classpath supported here
     * 
     * @param   lexDir
     * @since   1.9.9
     * @throws  IOException
    */
    public void loadClassPath() throws IOException
    {
        String __suffix = config.getLexiconFileSuffix();
        String __prefix = config.getLexiconFilePrefix();
        Class<?> dClass = this.getClass();
        
        CodeSource codeSrc = this.getClass().getProtectionDomain().getCodeSource();
        if ( codeSrc == null ) {
            return;
        }
        
        String codePath = codeSrc.getLocation().getPath();
        if ( codePath.toLowerCase().endsWith(".jar") ) {
            ZipInputStream zip = new ZipInputStream(codeSrc.getLocation().openStream());
            while ( true ) {
                ZipEntry e = zip.getNextEntry();
                if ( e == null ) {
                    break;
                }
                
                String fileName = e.getName();
                if ( fileName.startsWith("lexicon/"+__prefix) 
                        && fileName.endsWith(__suffix) ) {
                    load(dClass.getResourceAsStream("/"+fileName));
                }
            }
        } else {
            //now, the classpath is an IDE directory 
            //  like eclipse ./bin or maven ./target/classes/
            loadDirectory(codePath+"/lexicon");
        }
    }
    
    /**
     * start the lexicon autoload thread
    */
    public void startAutoload() 
    {
        if ( autoloadThread != null 
                || config.getLexiconPath() == null ) {
            return;
        }
        
        //create and start the lexicon auto load thread
        autoloadThread = new Thread(new Runnable() {
            @Override
            public void run() {
                String[] paths = config.getLexiconPath();
                AutoLoadFile[] files = new AutoLoadFile[paths.length];
                for ( int i = 0; i < files.length; i++ ) {
                    files[i] = new AutoLoadFile(paths[i] + "/" + AL_TODO_FILE);
                    files[i].setLastUpdateTime(files[i].getFile().lastModified());
                }
                
                while ( true ) {
                    //sleep for some time (seconds)
                    try {
                        Thread.sleep(config.getPollTime() * 1000);
                    } catch (InterruptedException e) {break;}
                    
                    
                    //check the update of all the reload todo files
                    File f            = null;
                    AutoLoadFile af   = null;
                    
                    for ( int i = 0; i < files.length; i++ ) {
                        af  = files[i];
                        f   = files[i].getFile();
                        
                        if ( ! f.exists() ) continue;
                        if ( f.lastModified() <= af.getLastUpdateTime() ) {
                            continue;
                        }
                        
                        //load words form the lexicon files
                        try {
                            BufferedReader reader = new BufferedReader(new FileReader(f));
                            String line = null;
                            while ( ( line = reader.readLine() ) != null ) {
                                line = line.trim();
                                if ( line.indexOf('#') != -1 ) continue;
                                if ( "".equals(line) ) continue; 
                                load(paths[i] + "/" + line);
                            }
                            
                            reader.close();
                            
                            FileWriter fw = new FileWriter(f);
                            fw.write("");
                            fw.close();
                            
                            //update the last update time
                            //@Note: some file system may close the in-time last update time update
                            //    in that case, this won't work normally.
                            //but, it will still work!!!
                            af.setLastUpdateTime(f.lastModified());
                            //System.out.println("newly added words loaded for path " + f.getParent());
                        } catch (IOException e) {
                            break;
                        }
                    }
                    
                }
            }
            
        });
        
        autoloadThread.setDaemon(true);
        autoloadThread.start();
        //System.out.println("lexicon autoload thread started!!!");
    }
    
    public void stopAutoload() 
    {
        if ( autoloadThread != null ) {
            autoloadThread.interrupt();
            autoloadThread = null;
        }
    }
    
    public boolean isSync() {
        return sync;
    }
    
    /**
     * loop up the dictionary, check the given key is in the dictionary or not
     * 
     * @param t
     * @param key
     * @return true for matched false for not match.
     */
    public abstract boolean match( int t, String key );
    
    /**
     * add a new word to the dictionary
     * 
     * @param t
     * @param key
     * @param type
     */
    public abstract void add( int t, String key, int type );
    
    /**
     * add a new word to the dictionary with its statistics frequency
     * 
     * @param t
     * @param key
     * @param fre
     * @param type
     */
    public abstract void add( int t, String key, int fre, int type );
    
    /**
     * return the IWord asscociate with the given key.
     * if there is not mapping for the key null will be return
     * 
     * @param t
     * @param key
     */
    public abstract IWord get( int t, String key );
    
    /**
     * remove the mapping associate with the given key
     * 
     * @param t
     * @param key
     */
    public abstract void remove( int t, String key );
    
    /**
     * return the size of the dictionary
     * 
     * @param    t
     * @return int
     */
    public abstract int size(int t);
    
    
    /**
     * get the key's type index located in ILexicon interface
     * 
     * @param key
     * @return int
     */
    public static int getIndex( String key ) {
        if ( key == null )
            return -1;
        key = key.toUpperCase();
        
        if ( key.equals("CJK_WORDS") ) 
            return ILexicon.CJK_WORD;
        else if ( key.equals("CJK_UNITS") ) 
            return ILexicon.CJK_UNITS;
        else if ( key.equals("EC_MIXED_WORD") )
            return ILexicon.EC_MIXED_WORD;
        else if ( key.equals("CE_MIXED_WORD") )
            return ILexicon.CE_MIXED_WORD;
        else if ( key.equals("CN_LNAME") ) 
            return ILexicon.CN_LNAME;
        else if ( key.equals("CN_SNAME") )
            return ILexicon.CN_SNAME;
        else if ( key.equals("CN_DNAME_1") )
            return ILexicon.CN_DNAME_1;
        else if ( key.equals("CN_DNAME_2") )
            return ILexicon.CN_DNAME_2;
        else if ( key.equals("CN_LNAME_ADORN") )
            return ILexicon.CN_LNAME_ADORN;
        else if ( key.equals("EN_PUN_WORDS") )
            return ILexicon.EN_PUN_WORD;
        else if ( key.equals("STOP_WORDS") )
            return ILexicon.STOP_WORD;
        else if ( key.equals("EN_WORD") )
            return ILexicon.EN_WORD;
            
        return ILexicon.CJK_WORD;
    }
    
    public JcsegTaskConfig getConfig()
    {
        return config;
    }
    
    public void setConfig( JcsegTaskConfig config )
    {
        this.config = config;
    }
    
    /**
     * load all the words in the specified lexicon file into the dictionary
     * 
     * @param   config
     * @param   dic
     * @param   file
     * @throws  IOException 
     * @throws  FileNotFoundException 
     * @throws  NumberFormatException 
     */
    public static void loadWords( JcsegTaskConfig config, ADictionary dic, File file ) 
            throws NumberFormatException, FileNotFoundException, IOException 
    {
        loadWords(config, dic, new FileInputStream(file));
    }
    
    /**
     * load all the words from a specified lexicon file path
     * 
     * @param   config
     * @param   dic
     * @param   file
     * @throws  IOException 
     * @throws  FileNotFoundException 
     * @throws  NumberFormatException 
    */
    public static void loadWords( JcsegTaskConfig config, ADictionary dic, String file ) 
            throws NumberFormatException, FileNotFoundException, IOException
    {
        loadWords(config, dic, new FileInputStream(file));
    }
    
    /**
     * load words from a InputStream
     * 
     * @param   config
     * @param   dic
     * @param   is
     * @throws  IOException 
     * @throws  NumberFormatException 
    */
    public static void loadWords( JcsegTaskConfig config, ADictionary dic, InputStream is ) 
            throws NumberFormatException, IOException
    {
        boolean isFirstLine = true;
        int t = -1;
        String line  = null;
        BufferedReader buffReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        
        while ( (line = buffReader.readLine()) != null ) {
            line = line.trim();
            if ( "".equals(line) ) continue;
            //swep the notes
            if ( line.charAt(0) == '#' && line.length() > 1 ) {
                continue;
            }
            
            //the first line fo the lexicon file.
            if ( isFirstLine == true ) {
                t = ADictionary.getIndex(line);
                //System.out.println(line+", "+t);
                isFirstLine = false;
                if ( t >= 0 ) continue;
            }
            
            //handle the stopwords
            if ( t == ILexicon.STOP_WORD ) {
                if ( line.charAt(0) <= 127 || ( line.charAt(0) > 127 
                        && line.length() <= config.MAX_LENGTH) ) {
                    dic.add(ILexicon.STOP_WORD, line, IWord.T_CJK_WORD);
                }
                
                continue;
            }
            
            //special lexicon
            if ( line.indexOf('/') == -1 ) {
                /*
                 * Here:
                 *  1. english and chinese mixed words,
                 *  2. chinese and english mixed words,
                 *  3. english punctuation words,
                 *         don't have to limit its length. 
                 */
                boolean olen = (t == ILexicon.EC_MIXED_WORD);
                olen = olen || (t == ILexicon.CE_MIXED_WORD);
                olen = olen || (t == ILexicon.EN_PUN_WORD);
                if ( olen || line.length() <= config.MAX_LENGTH ) {
                    dic.add(t, line, IWord.T_CJK_WORD);
                }
            } else {  //normal words lexicon file
                String[] wd = line.split("/");
                
                if ( wd.length < 4 ) {    //format check
                    System.out.println("Word: \"" + wd[0] + "\" format error. -ignored");
                    continue;
                }
                
                if ( wd.length == 5 ) {    //single word degree check.
                    if ( ! StringUtil.isDigit(wd[4]) ) {
                        System.out.println("Word: \"" + wd[0] + 
                                "\" format error(single word degree should be an integer). -ignored");
                        continue;
                    }
                }
                
                //length limit(CJK_WORD only)
                if ( ( t == ILexicon.CJK_WORD ) 
                        && wd[0].length() > config.MAX_LENGTH ) {
                    continue;
                }
                
                if ( dic.get(t, wd[0]) == null ) {
                    if ( wd.length > 4 ) {
                        dic.add(t, wd[0], Integer.parseInt(wd[4]), IWord.T_CJK_WORD);
                    } else {
                        dic.add(t, wd[0], IWord.T_CJK_WORD);
                    }
                }
                
                IWord w = dic.get(t, wd[0]);
                //System.out.println(wd.length);
                //set the pinying of the word.
                if ( config.LOAD_CJK_PINYIN && ! "null".equals(wd[2]) ) {
                    w.setPinyin(wd[2]);
                }
                
                boolean li = ( t == ILexicon.CJK_WORD );
                
                //set the syn words.
                String[] arr = w.getSyn();
                if ( config.LOAD_CJK_SYN && ! "null".equals(wd[3]) ) {
                    String[] syns = wd[3].split(",");
                    for ( int j = 0; j < syns.length; j++ ) {
                        syns[j] = syns[j].trim();
                        /* Here:
                         * filter the syn words that its length 
                         *         is greater than Config.MAX_LENGTH
                         */
                        if ( li && syns[j].length() > config.MAX_LENGTH ) continue;
                        /* Here:
                         * check the syn word is not exists, make sure
                         *     the same syn word won't appended. (dictionary reload)
                         * 
                         * @date 2013-09-02
                         */
                        if ( arr != null ) {
                            int length = arr.length;
                            boolean add = true;
                            for ( int i = 0; i < length; i++ )  {
                                if ( syns[j].equals(arr[i]) ) {
                                    add = false;
                                    break;
                                }
                            }
                            if ( ! add ) continue;
                        }
                        w.addSyn(syns[j]);
                    }
                }
                
                //set the word's part of speech
                arr = w.getPartSpeech();
                if ( config.LOAD_CJK_POS && ! "null".equals(wd[1]) ) {
                    String[] pos = wd[1].split(",");
                    for ( int j = 0; j < pos.length; j++ ) {
                        pos[j] = pos[j].trim();
                        /* Here:
                         * check the part of speech is not exists, make sure
                         *     the same part of speech won't appended.(dictionary reload)
                         * 
                         * @date 2013-09-02
                         */
                        if ( arr != null ) {
                            int length = arr.length;
                            boolean add = true;
                            for ( int i = 0; i < length; i++ )  {
                                if ( pos[j].equals(arr[i]) ) {
                                    add = false;
                                    break;
                                }
                            }
                            if ( ! add ) continue;
                        }
                        w.addPartSpeech(pos[j].trim());
                    }
                }
            }
        }
        
        buffReader.close();
        buffReader = null;
    }
    
}
