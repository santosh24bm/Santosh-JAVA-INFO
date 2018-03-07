package com.san.controller;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.sun.rowset.internal.Row;



@Controller
public class JiraPatchread {


	@Autowired
	@Qualifier(value = "sessionFactory")
	private SessionFactory sessionFactory;
	
	
	@RequestMapping("/getIssuesAndPatches")
	public void readddd() throws ParserConfigurationException, MalformedURLException, SAXException, IOException{

		System.out.println("inside the JiraReadXml.readJiraXmlIssues ");  
		
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		
		String [] issue_keys = null;
		 
		  try {

		   Criteria criteria = session.createCriteria(JiraIssueIds.class).add(Restrictions.eq("version","1.2.3"));
		   ProjectionList projList = Projections.projectionList();
		   //projList.add(Projections.distinct(Projections.property("sentiment")));
		   projList.add(Projections.property("issue_key"), "issue_key");
		   projList.add(Projections.property("version"), "version");
		   criteria.setProjection(projList);
		   criteria.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);
		   List list = criteria.list();
		   issue_keys = new String[list.size()];
		   String version=null;
		  
	
		   
  		   JiraPatchDetails patchdetails = null;
		   
		  for(int i = 0 ; i<list.size();i++ ){
			 //  for(int i = 69 ; i<71;i++ ){
			   System.out.println("count "+i);
			   
			   Map m1 = (Map) list.get(i);
			  issue_keys[i] = (String) m1.get("issue_key");
			  version =  (String) m1.get("version");
			   // get the xml and attachment ids 
				String xmlFileUrl  = "https://issues.apache.org/jira/si/jira.issueviews:issue-xml/"+issue_keys[i]+"/"+issue_keys[i]+".xml";
			//	String xmlFileUrl  = "https://issues.apache.org/jira/si/jira.issueviews:issue-xml/HBASE-13811/HBASE-13811.xml";
					
				
					Map<String, Integer> sonarMap = null;
					
					 DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
					  DocumentBuilder db = dbf.newDocumentBuilder();
					  Document doc = (Document) db.parse(new URL(xmlFileUrl).openStream());
					
					  ((org.w3c.dom.Document) doc).getDocumentElement().normalize();
					  System.out.println("Root element :" + ((org.w3c.dom.Document) doc).getDocumentElement().getNodeName());
					  NodeList nList1 = ((org.w3c.dom.Document) doc).getElementsByTagName("attachment");
					  
					  System.out.println("nList1.getLength() "+nList1.getLength());
					  String id="";
					  NodeList transaction = doc.getElementsByTagName("rss");
					
					  Map<String,String> patches = new  HashMap<String, String>();
			
					  for (int temp = 0; temp < nList1.getLength(); temp++)
					  { 
						  
					   Node node = nList1.item(temp);
					   System.out.println("");    //Just a separator
					   if (node.getNodeType() == Node.ELEMENT_NODE)
					   {
						   
						   // set the issue count 
						   
						   
						   
							
						   
						      //Print each employee's detail
						      Element eElement = (Element) node;
						      System.out.println("attachment id : "    + eElement.getAttribute("id"));
						      System.out.println("attachment name : "    + eElement.getAttribute("name"));
						      patches.put(eElement.getAttribute("id"), eElement.getAttribute("name"));
						
						      
						      if(!eElement.getAttribute("name").contains(".png") && !eElement.getAttribute("name").contains(".gif")){ // dont open image  files 
						    	  
							      // read the patches 
									//https://issues.apache.org/jira/secure/attachment/12871224/HBASE-18159-branch-1.patch
									//	String xmlFileUrl  = "https://issues.apache.org/jira/si/jira.issueviews:issue-xml/"+issue_keys[i]+"/"+issue_keys[i]+".xml";
							      String patchUrl  = "https://issues.apache.org/jira/secure/attachment/"+eElement.getAttribute("id")+"/"+eElement.getAttribute("name")+"";
										
							      if(patchUrl.contains(" ")){
							    	  continue;
							      }
											
									URL oracle = new URL(patchUrl);
									StringBuffer response = null;
									BufferedReader in = new BufferedReader(new InputStreamReader(oracle.openStream()));
									System.out.println("after  buffer read ");
									String inputLine = null;
									response = new StringBuffer();
									while ((inputLine = in.readLine()) != null) {
											response.append(inputLine);
											response.append("\n$");
											// inputline is each line 
											// logic to get the java file name and file path
											
											if(inputLine.contains("diff --")){
												patchdetails = new JiraPatchDetails();
												File file = new File(inputLine);
												String name = file.getName(); //
												String filepath = inputLine.substring(inputLine.lastIndexOf(" b/") + 1);
										
												
												System.out.println("issue_key "+issue_keys[i]+" version "+version+" patchName "+eElement.getAttribute("name")
												+" filename "+name+" filepaths "+filepath);
												
												patchdetails.setIssue_key(issue_keys[i]);
												patchdetails.setVersion(version);
												patchdetails.setFilepath(filepath);
												patchdetails.setFilename(name);
												patchdetails.setPatchname(eElement.getAttribute("name"));
											
										
												
												
												session.save(patchdetails);
											}
											
											
									}
									in.close();
								
								//	System.out.println(" response "+response.toString());
						      }
					
								
										      
						   }
					  }
				
					
			   
		}
			tx.commit();
			session.close();
		   
		   System.out.println("issue keys "+issue_keys);

		  } catch (Exception e) {
		   tx.rollback();
		   e.printStackTrace();
		  }
		  
		  
	
	}
	
}
