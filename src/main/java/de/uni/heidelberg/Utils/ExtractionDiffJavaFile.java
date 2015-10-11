package de.uni.heidelberg.Utils;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExtractionDiffJavaFile {
	
	public static void extractDiffJavaFile(String strInputDiff, File fileOutput, String regexStartCommandDiff) throws IOException
	{
			String output = "";
			//String regex = "(?<=" + regexStartCommandDiff + ")" + "(.*?)" + "(?=" + regexStartCommandDiff + ")"; 
			String regex = regexStartCommandDiff + "(.*?)" + "(?=" + regexStartCommandDiff + ")";
			//System.out.println("Regex: " + regex);
			Pattern p = Pattern.compile(regex, Pattern.DOTALL);
			Matcher m = p.matcher(strInputDiff);
			while(m.find())
			{
				String strFound = m.group();
				
				String regexDiffJava = "diff -ENwbur " + "(.*).java (.*).java";
			    Pattern pattern = Pattern.compile(regexDiffJava);
			    Matcher matcher = pattern.matcher(strFound);
			    if(matcher.find())
			    {
			    	output = output + strFound;
			    	//System.out.println("Found: " + strFound);
			    	
			    }//end if
			    
			}//end while
			
			DocumentWriter.writeDocument(output, fileOutput);
	}
}
