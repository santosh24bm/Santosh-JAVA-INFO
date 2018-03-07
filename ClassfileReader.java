package com.jeantessier.dependencyfinder.gui;

import java.awt.event.*;
import java.io.*;
import java.util.*;

import javax.swing.*;

import org.apache.tools.ant.taskdefs.Sleep;

import com.jeantessier.classreader.*;
import com.jeantessier.classreader.impl.*;
import com.jeantessier.commandline.CommandLine;
import com.jeantessier.commandline.NullParameterStrategy;
import com.jeantessier.dependency.*;

import jdk.internal.dynalink.beans.StaticClass;

public class ClassfileReader extends AbstractAction implements Runnable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	  private DependencyFinder model;
	

    public ClassfileReader(DependencyFinder model) {
    	  this.model = model;

          putValue(Action.LONG_DESCRIPTION, "Extract dependencies from compiled classes");
          putValue(Action.NAME, "Extract");
          putValue(Action.SMALL_ICON, new ImageIcon(getClass().getResource("icons/extract.gif")));
    }
	
    int i =0;
	public void getfilenames(String fname){
		
		 File directory = new File(fname);
			//  File directory = new File(directoryName);
		        //get all the files from a directory
		        File[] fList = directory.listFiles();
		      
		        for (File file : fList){
		            if (file.isFile()){
		            	if(i==0){
		            		File dummy = new File(".");
		            		model.addInputFile(dummy);
		            
		                } else {
		               	 model.addInputFile(file);
			              //  System.out.println(file.getAbsolutePath());
			                System.out.println(file.getName());
			                
			                // main code starts here -------------------------------------------------------
			               
			                new Thread(this).start();
		                }
		                
		            } else if (file.isDirectory()){
		            	getfilenames(file.getAbsolutePath());
		            }
		            i++;
		        }
		
	}
	
	@Override
	  public void run() {
	        Date start = new Date();

	        model.getStatusLine().showInfo("Scanning ...");
	        ClassfileScanner scanner = new ClassfileScanner();
	        scanner.load(model.getInputFiles());

	        model.getProgressBar().setMaximum(scanner.getNbFiles());

	        model.getMonitor().setClosedSession(false);

	        ClassfileLoader loader = new TransientClassfileLoader(model.getClassfileLoaderDispatcher());
	        loader.addLoadListener(new VerboseListener(model.getStatusLine(), model.getProgressBar()));
	        loader.addLoadListener(model.getMonitor());
	        loader.load(model.getInputFiles());

	        if (model.getMaximize()) {
	            model.getStatusLine().showInfo("Maximizing ...");
	            new LinkMaximizer().traverseNodes(model.getPackages());
	        } else if (model.getMinimize()) {
	            model.getStatusLine().showInfo("Minimizing ...");
	            new LinkMinimizer().traverseNodes(model.getPackages());
	        }

	        Date stop = new Date();

	        model.getStatusLine().showInfo("Done (" + ((stop.getTime() - start.getTime()) / (double) 1000) + " secs).");
	        model.setTitle("Dependency Finder - Extractor");
	       
	    }
	
	
	public void gettheresults() throws FileNotFoundException{
		
		   System.out.println("Scanning done ");
	        System.out.println("Start reading the data ");
	        Date newstart = new Date();
	       model.clearDependencyResult();
           System.out.println("Cleared");
           model.doDependencyQuery();
           System.out.println("Done");
           Date newstop = new Date();

           model.getStatusLine().showInfo("Dependency query done (" + ((newstop.getTime() - newstart.getTime()) / (double) 1000) + " secs).");

           System.out.println("Query executed in "+ ((newstop.getTime() - newstart.getTime()) / (double) 1000)+ " secs).");
	        
		
	} 
	
	public static void main(String[] args) throws FileNotFoundException {
		
		//String fname = "C:/Users/E003610/Desktop/version 1.0/cesa";
		//	Action  action = new DependencyExtractAction(this);
		 String fname = "C:/Users/E003610/Desktop/version 1.0/cesa";
		 CommandLine commandLine = new CommandLine(new NullParameterStrategy());
	     DependencyFinder model = new DependencyFinder(commandLine);
	     ClassfileReader aa = new ClassfileReader(model);
	     aa.getfilenames(fname);
	
		aa.gettheresults();
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
	
		
	}


	
}
