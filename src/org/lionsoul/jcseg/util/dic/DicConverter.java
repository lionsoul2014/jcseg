package org.lionsoul.jcseg.util.dic;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;

import org.lionsoul.jcseg.util.IStringBuffer;


/**
 * Jcseg dictionary simplified/traditional convert class.
 * 
 * @author chenxin <chenxin619315@gmail.com>
 */
public class DicConverter {
	
	public static final int SIMPLIFIED_TO_TRADITIONAL = 0;
	public static final int TRADITIONAL_TO_SIMPLIFIED = 1;
	
	/**
	 * convert the srcfile to dstfile to with 
	 * 	the specified convert rule. (SIMPLIFIED_TO_TRADITIONAL
	 * 	or TRADITIONAL_TO_SIMPLIFIED).
	 * 
	 * @param	srcfile
	 * @param	dstfile
	 * @param	_cvt
	 * @return 	boolean
	 * @throws IOException
	 */
	public static boolean convert( 
			File srcFile, File dstFile, int _cvt ) throws IOException 
	{
		if ( srcFile.equals(dstFile) ) return false;
		BufferedReader reader = new BufferedReader(new FileReader(srcFile));
		BufferedWriter writer = new BufferedWriter(new FileWriter(dstFile));
		
		String line;
		IStringBuffer isb = new IStringBuffer();
		switch ( _cvt ) {
		case SIMPLIFIED_TO_TRADITIONAL:
			while ( (line = reader.readLine()) != null ) {
				isb.clear();
				STConverter.SimToTraditional(line, isb);
				writer.write(isb.buffer(), 0, isb.length());
				writer.write('\n');
			}
			break;
		case TRADITIONAL_TO_SIMPLIFIED:
			while ( (line = reader.readLine()) != null ) {
				isb.clear();
				STConverter.TraToSimplified(line, isb);
				writer.write(isb.buffer(), 0, isb.length());
				writer.write('\n');
			}
			break;
		default:
			reader.close();
			writer.close();
			return false;
		}
		
		reader.close();
		writer.close();
		
		return true;
	}
	
	public static void main( String[] args ) {
		if ( args.length != 3 ) {
			System.out.println("Usage: java -jar jcseg-dicst.jar {tpy} {src} {dst}");
			System.out.println("{typ}: convert type.");
			System.out.println("       0 for convert simplified to traditional.");
			System.out.println("       1 for convert traditional to simplified.");
			System.out.println("{src}: directory of source dictionary.");
			System.out.println("{dst}: directory of destination dictinary.");
			System.exit(0);
		}
		
		//check the type
		int cvt = Integer.parseInt(args[0]);
		if ( cvt != SIMPLIFIED_TO_TRADITIONAL 
				&& cvt != TRADITIONAL_TO_SIMPLIFIED ) {
			System.out.println("error: type should 0 or 1.");
			System.exit(0);
		}
		
		//check the srcDir
		File srcDir = new File(args[1]);
		if ( ! srcDir.exists() ) {
			System.out.println("error: srcDir {" + args[1] + "} is not a valid directory.");
			System.exit(0);
		}
		
		//check the dstDir
		File dstDir = new File(args[2]);
		if ( ! dstDir.exists() ) {
			System.out.println("error: dstDir {" + args[2] + "} is not a valid directory.");
		}
		
		//get the all the files.
		File[] files = srcDir.listFiles(new FilenameFilter(){
			@Override
			public boolean accept(File dir, String name) {
				return ( name.startsWith("lex")
							&& name.endsWith("lex"));
			}
		});
		
		//show the confirm message.
		System.out.println("type: Jcseg词库" + 
				((cvt==SIMPLIFIED_TO_TRADITIONAL)?"简体转繁体":"繁体转简体"));
		System.out.println("srcDir: " + srcDir.getAbsolutePath());
		System.out.println("dstDir: " + dstDir.getAbsolutePath());
		System.out.print("Execute?(Y/N): ");
		try {
			char opt = (char) System.in.read();
			if ( ! (opt == 'Y' || opt == 'y') ) {
				System.out.println("Operation was terminated.");
				System.exit(0);
			}
			
			int success = 0;
			//convert all the dictionary files.
			for ( int j = 0; j < files.length; j++ ) {
				System.out.format("+-Converting file %20s ... ", files[j].getName());
				if ( convert(files[j], 
						new File(dstDir.getAbsolutePath()+"/"+files[j].getName()), cvt)) {
					System.out.println("--[Ok]");
					success++;
				} else 
					System.out.println("--[Fail]");
			}
			System.out.println("+Done, Total [" + files.length+"], successfull convert [" + success + "]");
		
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
