package cigniti.resume.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.hwpf.usermodel.Paragraph;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.hwpf.usermodel.Table;
import org.apache.poi.hwpf.usermodel.TableRow;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import cigniti.resume.impl.ResumeStorageImpl;

public class ResumeController {
	

	public static StringBuffer readDocFile(File fileName) {
		StringBuffer sbtemp = new StringBuffer();
		try {
			//File file = new File(fileName);
			FileInputStream fis = new FileInputStream(fileName.getAbsolutePath());

			HWPFDocument doc = new HWPFDocument(fis);

			WordExtractor we = new WordExtractor(doc);

			String[] paragraphs = we.getParagraphText();
			
			System.out.println("Total no of paragraph "+paragraphs.length);
			for (String para : paragraphs) {
				//System.out.println(para.toString());
				
			//	System.out.println(para.toString());
				sbtemp.append(para.toString()).append("\t");
			}
			fis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sbtemp;

	}

	//Reference: http://kemppro.blogspot.in/2014/11/reading-table-data-from-word-document.html
	
	public static StringBuffer readTables(File filename) throws IOException{
		StringBuffer sbtemp = new StringBuffer();
		FileInputStream	fis = new FileInputStream(filename);
	
		@SuppressWarnings("resource")
		HWPFDocument doc = new HWPFDocument(fis);
		
		// new 
		Range range = doc.getRange();
		  Paragraph tablePar = range.getParagraph(0);
	        if (tablePar.isInTable()) {
	            Table table = range.getTable(tablePar);
	            for (int rowIdx=0; rowIdx<table.numRows(); rowIdx++) {
	                TableRow row = table.getRow(rowIdx);
	                System.out.println("============================================================="+row);
	           /*     System.out.println("row "+(rowIdx+1)+", is table header: "+row.isTableHeader());
	                for (int colIdx=0; colIdx<row.numCells(); colIdx++) {
	                    TableCell cell = row.getCell(colIdx);
	                    System.out.println("column "+(colIdx+1)+", text="+cell.getParagraph(0).text());
	                }
	            }*/
	        }
		
		//new ends
		
				
		
		
	/*	
		@SuppressWarnings("resource")
		XWPFDocument  doc = new XWPFDocument(fis);
		
		List<XWPFTable>  tables;
		
        tables = doc.getTables();
        for ( XWPFTable table : tables )
        {
            for ( XWPFTableRow row : table.getRows() )
            {
                for ( XWPFTableCell cell : row.getTableCells() )
                {
                  //  System.out.print(cell.getText());
                  //  System.out.print("\t");
                    sbtemp.append(cell.getText()).append("\t").append("\n");
                }
                System.out.println("");
            }
        }*/
  
    	fis.close();
		
	}
			return sbtemp;
	}
	
	public static HashMap<String, Integer> getWordFrequency(String str){
		 String [] list = str.replaceAll("\\.|\\,|\\/|\"\"", " " ).split("\\s+");
		
		  HashMap<String, Integer> wordMap = new HashMap<String , Integer >();
			System.out.println(list.length);
			for (String word : list) {
			//	System.out.print(word);
			//	System.out.print("--");
				word = word.toLowerCase();
				  if(wordMap.containsKey(word)){
					  wordMap.put(word, wordMap.get(word)+1);
	                }else {
	                	wordMap.put(word, 1);
	                }
			}
			return wordMap;

	}
	
	
	/**
	 * @param text
	 *            String
	 * @return Boolean
	 */
	public static boolean checkSkillPaterns(final String text) {
		boolean flag = false;
		Pattern findMyPattern = Pattern.compile("flight|ticket|airlin|air lin", Pattern.CASE_INSENSITIVE);
		Matcher foundAMatch;
		Pattern findMyPattern_sub = Pattern.compile("cancel|delay|book", Pattern.CASE_INSENSITIVE);
		Matcher findMyPattern_sub_b;

		foundAMatch = findMyPattern.matcher(text);
		if (foundAMatch.find()) {
			findMyPattern_sub_b = findMyPattern_sub.matcher(text);
			if (findMyPattern_sub_b.find()) {
				flag = true;
			}
			findMyPattern_sub_b.reset();
		}
		foundAMatch.reset();
		return flag;
	}
	
	
	public static HashMap<String, Integer> getWordFrequency_semantic(String str){
		// String [] list = str.replaceAll("\\.|\\,|\\/|\"\"", " " ).split("\\s+");
		 HashMap<String, Integer> wordMap = new HashMap<String , Integer >();
		System.out.println("+++++++++++++++++++++++++++++++++");
	//	System.out.println(str);
		String [] textArray = str.split("\n");
		for(String row : textArray){
		//	System.out.println(row);
	//	String pattrenStr = "Regression\\sTesting\\b|Database\\sTesting\\b|Oracle\\sEBS\\b|C\\+\\+\\s";
		
		String skillPatternAll = "Mobile|Guideware|Regression Testing|Black box/White box|ETL|Database Testing "
				+ "|Informatica|Onesheild|Bugzilla|TFS|JIRA|Canoo|Webcorder|Embeded Testing|PEGA|SQL Server/My-Sql"
				+ "|Crossbrowser Testing|Oracle EBS|QTP/UFT|Selenium-Java|Selenium-C#|Test Complete|Ranorex|Coded UI|Cucumber|SOAP UI|Ruby "
				+ "|Appium|SeeTest|Webservices|Eggplant|Java Script|VB Script|Jenkins|Test Partner|Robotium|Tosca|Telerik|TestWhiz|Watir"
				+ "|Watin|SQL Server/My-Sql|Jmeter|Load Runner|VSTS|App-Dynamics|Neo Load|Web Load "
				+ "|Dynatrace|Newrelic|Hp- diagnostics|VMStat|Sitescope|N-Mon|OSAP|AutoSpy|OSSTMM|NIST|EnCase|Oxygen Forensic suite|MOBILedit|Burp Suite "
				+ "|Network Security|Penetration Testing|Linux|Ethereal|Paros|Qualys "
				+ "|Nessus|Net Sparker|Iron WASP|RestClient|WebScarab|Java|Dot Net|C#|C\\+\\+\\s|javascript|angular|mysql|my sql";
		
//	String pp =	"Mobile |Guideware |Regression|Testing |Black box/White box |ETL |Database Testing |Informatica |Onesheild |Bugzilla |TFS |JIRA |Canoo |Webcorder |Embeded|Testing |PEGA |SQL|Server/My-Sql |Crossbrowser|Testing |Oracle|EBS |QTP/UFT |Selenium-Java |Selenium-C# |Test Complete |Ranorex |Coded|UI |Cucumber |SOAP|UI |Ruby |Appium |SeeTest |Webservices |Eggplant |Java|Script |VB|Script |Jenkins |Test|Partner |Robotium |Tosca |Telerik |TestWhiz |Watir |Watin |SQL|Server/My-Sql |Jmeter |Load|Runner |VSTS |App-Dynamics |Neo|Load |Web|Load |Dynatrace |Newrelic |Hp-|diagnostics |VMStat |Sitescope |N-Mon |OSAP |AutoSpy |OSSTMM |NIST |EnCase |Oxygen|Forensic|suite |MOBILedit |Burp|Suite |Network|Security |Penetration|Testing |Linux |Ethereal |Paros |Qualys |Nessus "
	//		+ "|Net|Sparker |Iron|WASP |RestClient |WebScarab |Java |Dot Net |C# |C\\+\\+\\s";	
		
	//	System.out.println("Pattern : "+skillPatternAll);
		Pattern findMyPattern = Pattern.compile(skillPatternAll, Pattern.CASE_INSENSITIVE);
		Matcher foundAMatch;
		Pattern findMyPattern_sub = Pattern.compile("cancel|delay|book", Pattern.CASE_INSENSITIVE);
		Matcher findMyPattern_sub_b;
		
		foundAMatch = findMyPattern.matcher(row);
		//System.out.println(foundAMatch.group(0));
		
		 while(foundAMatch.find()) {
		      System.out.println(foundAMatch.group());
		String   word =foundAMatch.group();
			
					word = word.toLowerCase();
					  if(wordMap.containsKey(word)){
						  wordMap.put(word, wordMap.get(word)+1);
		                }else {
		                	wordMap.put(word, 1);
		                }
				
		      
		    }
		
		/*if (foundAMatch.find()) {
		//	System.out.println(foundAMatch.group());
		//	System.out.println(foundAMatch.);
			findMyPattern_sub_b = findMyPattern_sub.matcher(row);
			if (findMyPattern_sub_b.find()) {
			//	flag = true;
			}
			findMyPattern_sub_b.reset();
		}*/
		foundAMatch.reset();
		
		}
		
/*		String [] list = str.split(",");
		System.out.println("Printing  words");
		for (String word : list) {
				System.out.println(word);
				System.out.print("--");
				if(word.contains("and")){
					String [] tempwords = word.replace("and|&"," ").split("\\s+");
					for(String s : tempwords){
						System.out.println(s);
					}
				}
			
			}*/
		
		
	/*	 String [] list1 = str.replaceAll("\\.|\\,", " " ).replaceAll("", replacement)
		  HashMap<String, Integer> wordMap = new HashMap<String , Integer >();
			System.out.println(list.length);
			for (String word : list) {
			//	System.out.print(word);
			//	System.out.print("--");
				word = word.toLowerCase();
				  if(wordMap.containsKey(word)){
					  wordMap.put(word, wordMap.get(word)+1);
	                }else {
	                	wordMap.put(word, 1);
	                }
			}*/
			return wordMap;

	}
	
	public static StringBuffer readDocxFile(File fileName) {
		StringBuffer sbtemp = new StringBuffer();
		try {
		
			
			FileInputStream fis = new FileInputStream(fileName.getAbsolutePath());
			
		//	org.apache.poi.xwpf.extractor.XWPFWordExtractor.XWPFWordExtractor 
			
			XWPFDocument document = new XWPFDocument(fis);

			List<XWPFParagraph> paragraphs = document.getParagraphs();
			
			
			System.out.println("Total no of paragraph "+paragraphs.size());
			for (XWPFParagraph para : paragraphs) {
			//	System.out.println(para.getText());
				sbtemp.append(para.getText()).append("\t").append("\n");
			
				
			}
			System.out.println("print hidden ");
			

			fis.close();
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	
		return sbtemp;
	
	}

    private static Map<String, Integer> sortByValue(Map<String, Integer> unsortMap) {

        // 1. Convert Map to List of Map
        List<Map.Entry<String, Integer>> list =
                new LinkedList<Map.Entry<String, Integer>>(unsortMap.entrySet());

        // 2. Sort list with Collections.sort(), provide a custom Comparator
        //    Try switch the o1 o2 position for a different order
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> o1,
                               Map.Entry<String, Integer> o2) {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });

        // 3. Loop the sorted list and put it into a new insertion order Map LinkedHashMap
        Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
        for (Map.Entry<String, Integer> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        /*
        //classic iterator example
        for (Iterator<Map.Entry<String, Integer>> it = list.iterator(); it.hasNext(); ) {
            Map.Entry<String, Integer> entry = it.next();
            sortedMap.put(entry.getKey(), entry.getValue());
        }*/


        return sortedMap;
    }
	
	
	public static void main(String[] args) throws IOException {
	
	  // readDocFile("C:\\Test.doc");
     
	File file = new File("C:\\New Folder\\Emp ID_Employee Name_DD-MM-YY.doc");
	//	String fileWithDocExtenstion = file.getName().substring(file.getName().lastIndexOf(".") + 1);
	System.out.println("s================"+file.getName());
	String	fileWithDocExtenstion = "C:\\New Folder\\"+file.getName().substring(0,file.getName().lastIndexOf("."))+".doc";
	System.out.println(fileWithDocExtenstion);
	fileWithDocExtenstion= fileWithDocExtenstion.replaceAll("\\\\", "\\\\");
	System.out.println(fileWithDocExtenstion);
	// File file = new File("C:\\New Folder\\New.doc");
		StringBuffer sb = new StringBuffer();
	  // sb = readDocxFile(file);
	  sb = readDocFile(new File(fileWithDocExtenstion));
	//	sb.append(readTables(file));
	//	HashMap<String, Integer> wordFreqMap= getWordFrequency(sb.toString() );
	
	 // these special appeared in table parsing in doc
        String fullText  = sb.toString().replaceAll("", "");
		HashMap<String, Integer> wordFreqMap= getWordFrequency_semantic(fullText); 
		
	
		// sorted map 
        HashMap<String, Integer> sortedMap = (HashMap<String, Integer>) sortByValue(wordFreqMap);
        System.out.println();
        String skills = null ;
        for (Map.Entry<String, Integer> entry : sortedMap.entrySet()) {
        	System.out.println(" " + entry.getKey().toString().toUpperCase() + " : " + entry.getValue());
        	   	skills = skills+entry.getKey().toString().toUpperCase()+":"+entry.getValue()+",";
        }
        skills = "{"+skills+"}";
        
        
        // open/read the application context file
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");

       
       //Store data in DB 
       ResumeStorageImpl resImpl = new ResumeStorageImpl(); 
       boolean result = resImpl.saveResumeDetails(skills,fullText,fileWithDocExtenstion);
       System.out.println(result);
        
        
	}


}


