package com.opentext.teamsite.lsds;

import org.dom4j.Document;
import org.dom4j.Element;

import com.interwoven.livesite.content.ContentService;
import com.interwoven.livesite.dom4j.Dom4jUtils;
import com.interwoven.livesite.runtime.RequestContext;
import com.interwoven.wcm.lscs.Client;

public class LSDSWrapper {
	
	private static final int MAX_RESULTS = 25;	
	
	private String getQueryString(RequestContext context) {
		System.out.println("Init getQueryString");

		String queryString = "";
		try {
			//if(queryString == null || queryString.compareTo("") == 0) {								
				String contentCategory = context.getParameterString("contentCategory");
				System.out.println("Content category: " + contentCategory);	

				String contentName = context.getParameterString("contentName");
				System.out.println("Content Name: " + contentName);	

				//if(contentCategory != null && contentName != null) {
					StringBuilder query = new StringBuilder();
					query.append("q=TeamSite/Templating/DCR/Type:")
					.append(contentCategory)
					.append("/")
					.append(contentName);
					queryString = query.toString();
				//}
			//}
		} catch (Exception e) {
			System.err.println("getQueryString: " +  e.getMessage());
		}

		System.out.println("QUERY STRING: " + queryString);

		return queryString;
	}
	
	
	private int getMaxResultsParam(RequestContext context) {	
		int intMaxResults = MAX_RESULTS;
		
		String strMaxResults = context.getParameterString("maxResults");
		System.out.println("Max Result: " + strMaxResults);	

		if(strMaxResults != null && strMaxResults.compareTo("") != 0) {
			try {
				intMaxResults = Integer.parseInt(strMaxResults);
			}
			catch (NumberFormatException e) {
				System.err.println("Max Result is not a valid number. Using default max. value" + e.getMessage());	
			}
		}
		else {
			System.out.println("Using default max. value");
		}
		
		return intMaxResults;
	}	
	
	/**
	 * Get the `Content Items` of a given Category/Name or that match the given LSCS query.
	 * These are the parameters supported in TeamSite configuration:
	 * 	- documentQuery: LSCA query string to be use. Some examples:
	 * 			q=type:datasheet 
	 * 			q=category:products&format=json
	 * 	NOTE: If this parameter is specified in TeamSite the other parameters, 
	 * 	`contentCategory` and `contentName`, and will be ignored
	 * 	- contentCategory: Content template category
	 * 	- contentName: Content template name
	 *  - maxResults: Maximum number of result to be returned. 25 by default
	 * @param context - Request context
	 * @return XML Document to contains the content items 
	 * that match with the search criteria
	 */
	public Document getDCRAssets(RequestContext context) {
		Document doc = Dom4jUtils.newDocument();
		
		try {
			Element rootElement = doc.addElement("root");
			System.out.println("Root element created");
						
			Client client = ContentService.getInstance().getContentClient(context);
			String projectName = context.getSite().getBranch();
			client.setProject(projectName);
			System.out.println("Client created.  Project name: " + projectName);	
			
			String queryString = getQueryString(context);
			System.out.println("QUERY STRING: " + queryString);
			
			int maxResults = getMaxResultsParam(context);
			
			Element resultsElement = rootElement.addElement("results");
			resultsElement.addCDATA(queryString);
			resultsElement.addCDATA("MAX RESULTS: " + maxResults);
			doc.add(resultsElement);
			System.out.println("Results added");							
		}
		catch (Exception e) {
			System.err.println(e.getMessage());
		}
		
		return doc;
	}
}
