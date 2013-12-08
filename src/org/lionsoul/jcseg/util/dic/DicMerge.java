package org.lionsoul.jcseg.util.dic;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.lionsoul.jcseg.Word;
import org.lionsoul.jcseg.core.IWord;
import org.lionsoul.jcseg.util.IStringBuffer;
import org.lionsoul.jcseg.util.Sort;


/**
 * Jcseg dictionary merge class.
 * 	all the duplicate entries will be removed, sorted them by natural order.
 * 
 * demo: 人事部/nt/ren shi bu/人事管理部門,人事管理部 . <br />
 * 
 * <ul>
 * <li>1. the pinyin will be merged.</li>
 * <li>2. the part of the speech will be merged.</li>
 * <li>3. the synonyms words will be merged.</li>
 * </ul>
 * 
 * @author chenxin <chenxin619315@gmail.com>
 */
public class DicMerge {
	
	private static boolean inArray( String[] arr, String item ) {
		for ( int j = 0; j < arr.length; j++ )
			if ( arr[j].equals(item) ) return true;
		return false;
	}
	
	/**
	 * merge two jcseg dictionary files,
	 * 	remove the duplicate entries and store the entris in a specified file. <br />
	 * 
	 * @param	srcFiles
	 * @param	dstfile
	 * @return	int
	 * @throws 	IOException
	 */
	public static int merge( 
			 File[] srcFiles, File dstFile ) throws IOException 
	{
		//merge the source dictionary.
		IWord word = null;
		BufferedReader reader = null;
		String keywords = null;
		HashMap<String, IWord> entries = new HashMap<String, IWord>();
		
		for ( int j = 0; j < srcFiles.length; j++  ) 
		{
			String line = null;
			reader = new BufferedReader(new FileReader(srcFiles[j]));
			keywords = reader.readLine();
			while ( (line = reader.readLine()) != null ) {
				line = line.trim();
				//clear the comment and the whitespace
				if ( line.equals("") ) continue;
				if ( line.length() > 1 && line.charAt(0) == '#' ) continue;
				if ( line.indexOf('/') == -1 ) {	//simple word
					if ( ! entries.containsKey(line) )
						entries.put(line, new Word(line, 1));
					continue;
				}
				
				// 人事部/nt/ren shi bu/人事管理部門,人事管理部 
				String[] splits = line.split("/");
				if ( splits.length < 4 ) {
					line = null;
					splits = null;		//let the gc do its work.
					continue;
				}
				
				//get the entries
				word = entries.get(splits[0]);
				if ( word == null ) 
				{
					int type = 0, fre = 0;
					if ( splits.length > 4 ) {
						fre = Integer.parseInt(splits[4]);
						type = 2;
					}
					word = new Word(splits[0], fre, type);
					if ( ! splits[1].equals("null") )		//part of the speech
						word.setPartSpeech(splits[1].split(","));
					if ( ! ( splits[2].equals("%")
							|| splits[2].equals("null")) )	//pinyin
						word.setPinyin(splits[2]);
					if ( ! splits[3].equals("null") )		//synonyms
						word.setSyn(splits[3].split(","));
					entries.put(splits[0], word);
				} 
				else {
					//System.out.println("re-word: " + splits[0]);
					//check check the part of the speech
					if ( ! splits[1].equals("null") ) {
						String[] pps = splits[1].split(",");
						if ( word.getPartSpeech() == null)
								word.setPartSpeech(pps);
						else {
							String[] ps = word.getPartSpeech();
							for ( int i = 0; i < pps.length; i++ )
								if ( ! inArray(ps, pps[i]) ) word.addSyn(pps[i]);
						}
						pps = null;
					}
					
					//check the pinyin
					if ( word.getPinyin() == null
							&& ! splits[2].equals("null") )
						word.setPinyin(splits[2]);
					
					//check the synonyms.
					if ( ! splits[3].equals("null") ) {
						String[] syns = splits[3].split(",");
						if ( word.getSyn() == null )
							word.setSyn(syns);
						else {
							String[] syn = word.getSyn();
							for ( int i = 0; i < syns.length; i++ )
								if ( ! inArray(syn, syns[i]) ) word.addSyn(syns[i]);
						}
						syns = null;
					}
				}
				
			} //end of while
			
			reader.close();
		}
		
		//sort the entries by natrual order.
		String[] keys = new String[entries.size()];
		entries.keySet().toArray(keys);
		Sort.quicksort(keys);
		
		//write the merged entries to the destination file.
		BufferedWriter writer  = new BufferedWriter(new FileWriter(dstFile));
		writer.write(keywords);
		writer.write('\n');
		
		IStringBuffer isb = new IStringBuffer();
		for ( int j = 0; j < keys.length; j++ ) {
			word = entries.get(keys[j]);
			
			/* Here:
			 *  if the orgin lexicon is simple lexicon.
			 *  we just need the word item.
			 *  
			 * @added 2013-11-28
			 */
			if ( word.getType() == 1 ) {
				writer.write(word.getValue());
				writer.write('\n');
				continue;
			}
			
			isb.clear();
			isb.append(word.getValue());				//word
			isb.append('/');
			if ( word.getPartSpeech() == null )			//part of speech
				isb.append("null");
			else {
				String[] ps = word.getPartSpeech();
				for ( int i = 0; i < ps.length; i++ ) {
					if ( i == 0 )
						isb.append(ps[0]);
					else {
						isb.append(',');
						isb.append(ps[i]);
					}
				}
			} 
			
			isb.append('/');
			
			if ( word.getPinyin() == null )				//pinyin
				isb.append("null");
			else
				isb.append(word.getPinyin());
			
			isb.append('/');
			
			if ( word.getSyn() == null )				//synonyms
				isb.append("null");
			else {
				String[] syn = word.getSyn();
				for ( int i = 0; i < syn.length; i++ ) {
					if ( i == 0 )
						isb.append(syn[0]);
					else {
						isb.append(',');
						isb.append(syn[i]);
					}
				}
			}
			
			if ( word.getType() == 2 ) {				//single word degree
				isb.append('/');
				isb.append(""+word.getFrequency());
			}
			
			writer.write(isb.buffer(), 0, isb.length());
			writer.write('\n');
		}
		writer.close();
		
		return keys.length;
	}

	public static void main(String[] args) 
	{
		//usage
		if ( args.length < 2 ) {
			System.out.println("Usage: java -jar jcseg-dicmerge.jar {dstdir} {srcdir1} {srcdir2} ...");
			System.out.println("{dstdir}: directory to store all the merge dictionary.");
			System.out.println("{srcdir}: source dictionary path 1.");
			System.out.println("{srcdir}: source dictionary path 2.");
			System.out.println("     ...: source dictionary path n.");
			System.exit(0);
		}
		
		//dstDir
		File dstDir = new File(args[0]);
		if ( ! dstDir.exists() ) {
			System.out.println("error: srcDir {" + args[0] + "} is not a valid directory.");
			System.exit(0);
		}
		
		
		//check all the src directory.
		for ( int j = 1; j < args.length; j++ ) {
			File f = new File(args[j]);
			if ( ! f.exists() ) {
				System.out.println("error: dstDir {" + args[j] + "} is not a valid directory.");
				System.exit(0);
			}
		}
		
		
		File srcDir = new File(args[1]);
		//get the all the files of srcDir 1.
		File[] files = srcDir.listFiles(new FilenameFilter(){
			@Override
			public boolean accept(File dir, String name) {
				return ( name.startsWith("lex")
							&& name.endsWith("lex"));
			}
		});
		
		System.out.println("Jcseg词库合并: ");
		System.out.println("dstDir: " + dstDir.getAbsolutePath());
		System.out.println("srcDir: ");
		for ( int j = 1; j < args.length; j++ )
			System.out.println("        "+args[j]);
		System.out.print("Execute?(Y/N): ");
		
		//merge all the files.
		try {
			char opt = (char) System.in.read();
			if ( ! (opt == 'y' || opt == 'Y') ) {
				System.out.println("Operation was terminated.");
				System.exit(0);
			}
			
			int t, counter = 0;
			ArrayList<File> dstList = new ArrayList<File>();
			for ( int j = 0; j < files.length; j++ ) {
				//System.out.print("+-Merging file " + files[j].getName()+"... ");
				System.out.format("+-Merging file %20s ... ", files[j].getName());
				dstList.clear();
				dstList.add(files[j]);
				for ( t = 2; t < args.length; t++ ) {
					File f = new File(args[t]+"/"+files[j].getName());
					if ( ! f.exists() ) continue;
					dstList.add(f);
				}
				File[] dstFiles = new File[dstList.size()];
				dstList.toArray(dstFiles);
				int ct = merge(dstFiles, 
						new File(dstDir.getAbsolutePath()+"/"+files[j].getName()));
				if ( ct > 0 ) {
					counter++;
					System.out.format("[%-6s] items --[Ok]\n", ct);
				} else 
					System.out.println("--Fail");
			}
			System.out.println("+Done, Total: ["+files.length+"], Successfully merged ["+counter+"]");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
