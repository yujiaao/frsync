package com.ssll.rsync;

import java.io.Console;
import java.io.IOException;
import java.util.Scanner;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class App {
	
	private static boolean verbose=false;
	
	public static void main(String[] args) throws InterruptedException {

		Options opt = new Options();
		opt.addOption("f", "format",  false, "date format, default is yyyyMMdd.");
		opt.addOption("v", "verbose", false, "explain what is being done.");
		opt.addOption("l", "localpath", true, "localpath to sync with.");
		opt.addOption("r","remote",true,"remote server address  like user@server:/filedir");
		opt.addOption("h", "help", false, "print help for the command.");

//		opt.addOption(Option
//				.builder("r")
//				.argName("remote")
//				.hasArg()
//				.desc("remote server address  like user@server:/filedir")
//				.build());


		String formatstr = "java -jar frsync.jar  [-f/--format=yyyyMMdd][-v/--verbose][-h/--help] -r/--remote=user@server:/dir   -l/--localpath=/home/backup   filepattern1 filepattern2";

		HelpFormatter formatter = new HelpFormatter();
		CommandLineParser parser = new DefaultParser();
		CommandLine cl = null;
		try {
			// 处理Options和参数
			cl = parser.parse(opt, args);
		} catch (ParseException e) {
			formatter.printHelp(formatstr, opt); // 如果发生异常，则打印出帮助信息
			e.printStackTrace();
			System.exit(-1);
		}
		// 如果包含有-h或--help，则打印出帮助信息
		if (cl.hasOption("h") || cl.getArgList().size()<=0) {
			HelpFormatter hf = new HelpFormatter();
			hf.printHelp(formatstr, "", opt, "");
			return;
		}
		
		String dateFormat = "yyyyMMdd";
		// 判断是否有-f参数
		if (cl.hasOption("f")) {
			dateFormat = cl.getOptionValue("f");
		}
		// 判断是否有-v或--verbose参数
		if (cl.hasOption("v")) {
			verbose = true;
			log("has v");
		}
		
		
		String remote = "";
		// 判断是否含有block-size参数
		if (cl.hasOption("r")) {
					// print the value of block-size
			remote = cl.getOptionValue("r");
					log("r=" + remote);
		}
		
		String localPath=".";
		if (cl.hasOption("l")) {
			// print the value of block-size
			localPath = cl.getOptionValue("l");
			log("l=" + localPath);
}
		
		
		//String password = passwordScanner();
		
		// 获取参数值，这里主要是localFiles
		for(String file: cl.getArgList()){
			try {				
					String pattern = file + "(\\d{8})"+".gz";
					new FastBackup(verbose, System.out).run(remote,  localPath, pattern, dateFormat);
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}	
		
			
	}
	
	private static void log(String src){
		if(verbose)
			System.out.println(src);
	}
	
	private static String passwordScanner(){
//		System.out.println("password: ");
//		Scanner scanIn = new Scanner(System.in);
//		
//		String inputString = scanIn.nextLine();
//		scanIn.close();
//		return inputString;
		Console console = System.console();
		char passwordArray[] = console.readPassword("Enter your secret password: ");
		return  new String(passwordArray);

	}

}
