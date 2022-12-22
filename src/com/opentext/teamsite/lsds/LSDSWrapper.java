package com.opentext.teamsite.lsds;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.DOMReader;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.interwoven.livesite.common.io.StreamUtil;
import com.interwoven.livesite.content.ContentService;
import com.interwoven.livesite.dom4j.Dom4jUtils;
import com.interwoven.livesite.runtime.RequestContext;
import com.interwoven.wcm.lscs.Client;
import com.interwoven.wcm.lscs.LSCSException;
import com.interwoven.wcm.lscs.LSCSIterator;

public class LSDSWrapper {
	private static final String LOCALE_ES_ES = "es_ES";
	private static final String LOCALE_EN_GB = "en_GB";

	private static final String PARAM_MAX_RESULTS = "maxResults";
	private static final String PARAM_CONTENT_NAME = "contentName";
	private static final String PARAM_CONTENT_CATEGORY = "contentCategory";
	private static final String PARAM_LOCALE = "locale";
	private static final int NUM_MAX_RESULTS = 25;

	private boolean hasValue(String param) {
		return param != null && param.compareTo("") != 0;
	}

	private String getQueryString(RequestContext context) {
		System.out.println("Init getQueryString");

		String queryString = "";
		try {

			String contentCategory = context.getParameterString(PARAM_CONTENT_CATEGORY);
			System.out.println("Content category: " + contentCategory);

			String contentName = context.getParameterString(PARAM_CONTENT_NAME);
			System.out.println("Content Name: " + contentName);

			String locale = context.getParameterString(PARAM_LOCALE);
			System.out.println("locale: " + locale);

			StringBuilder query = new StringBuilder();
			if (hasValue(contentCategory) && hasValue(contentName)) {
				query.append("q=TeamSite/Templating/DCR/Type:").append(contentCategory).append("/").append(contentName);
			}

			if (hasValue(locale)) {
				query.append(" AND G11N/Locale:").append(locale);
			}

			queryString = query.toString();

		} catch (Exception e) {
			System.err.println("getQueryString: " + e.getMessage());
		}

		System.out.println("QUERY STRING: " + queryString);

		return queryString;
	}

	private int getMaxResultsParam(RequestContext context) {
		int intMaxResults = NUM_MAX_RESULTS;

		String strMaxResults = context.getParameterString(PARAM_MAX_RESULTS);
		System.out.println("Max Result: " + strMaxResults);

		if (strMaxResults != null && strMaxResults.compareTo("") != 0) {
			try {
				intMaxResults = Integer.parseInt(strMaxResults);
			} catch (NumberFormatException e) {
				System.err.println("Max Result is not a valid number. Using default max. value" + e.getMessage());
			}
		} else {
			System.out.println("Using default max. value");
		}

		return intMaxResults;
	}

	private boolean isEdit(RequestContext context) {
		return !context.isPreview() && !context.isRuntime();
	}

	/**
	 * Get the `Content Items` of a given Category/Name or that match the given LSCS
	 * query. These are the parameters supported in TeamSite configuration: -
	 * documentQuery: LSCA query string to be use. Some examples: q=type:datasheet
	 * q=category:products&format=json NOTE: If this parameter is specified in
	 * TeamSite the other parameters, `contentCategory` and `contentName`, and will
	 * be ignored - contentCategory: Content template category - contentName:
	 * Content template name - maxResults: Maximum number of result to be returned.
	 * 25 by default
	 * 
	 * @param context - Request context
	 * @return XML Document to contains the content items that match with the search
	 *         criteria
	 */
	public Document getDCRAssets(RequestContext context) {
		Document doc = Dom4jUtils.newDocument();

		try {
			Element rootElement = doc.addElement("root");
			System.out.println("Root element created");

			// Define project name
			Client client = ContentService.getInstance().getContentClient(context);
			String projectName = context.getSite().getBranch();
			client.setProject(projectName);
			System.out.println("Client created.  Project name: " + projectName);

			// Define context name in Preview and Edit views
			if (context.isPreview() || isEdit(context)) {
				System.out.println("Is preview");

				String contextName = context.getSite().getArea();
				client.setContextString(contextName);
			}

			// Recover parameters
			String queryString = getQueryString(context);
			System.out.println("QUERY STRING: " + queryString);
			int maxResults = getMaxResultsParam(context);

			// Add results
			Element resultsElement = rootElement.addElement("results");

			// Use LSDS API to recover content from TeamSite
			LSCSIterator<com.interwoven.wcm.lscs.Document> iter = client.getDocuments(queryString, 0, maxResults);
			System.out.println("# results: " + iter.getTotalSize());

			while (iter.hasNext()) {
				com.interwoven.wcm.lscs.Document iterDoc = iter.next();
				System.out.println("Result document path: " + iterDoc.getPath());
				resultsElement.add(lscsDocumentToXml(iterDoc, true).getRootElement());
			}

			// Add results to root document
			doc.add(resultsElement);
			System.out.println("Results added");
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}

		return doc;
	}

	/**
	 * Constructing document from String Object
	 * 
	 * @param rs
	 * @return
	 * @throws FactoryConfigurationError
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	private org.w3c.dom.Document toDocument(String rs)
			throws FactoryConfigurationError, ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		StringReader reader = new StringReader(rs);
		InputSource source = new InputSource(reader);
		return builder.parse(source);
	}

	private org.dom4j.Document lscsDocumentToXml(com.interwoven.wcm.lscs.Document lscsDocument, boolean includeContent)
			throws LSCSException, IOException {
		Element xmlElement = DocumentHelper.createElement("document");
		org.dom4j.Document xmlDocument = DocumentHelper.createDocument(xmlElement);
		String content;
		xmlElement.addAttribute("id", lscsDocument.getId());
		xmlElement.addAttribute("path", lscsDocument.getPath());
		xmlElement.addAttribute("uri", lscsDocument.getContentURL());

		Element metadata = xmlElement.addElement("metadata");
		String[] metadataNames = lscsDocument.getAttributeNames();
		for (String metadataName : metadataNames) {
			metadata.addElement("field").addAttribute("name", metadataName)
					.addText(lscsDocument.getAttribute(metadataName));
		}

		org.w3c.dom.Document w3DocumentContainer;
		org.dom4j.Document document;

		// This is needed until client.setIncludeContent(true); is fixed
		if (includeContent) {
			content = StreamUtil.read(lscsDocument.getContentStream());
			Element contentXmlElement = xmlElement.addElement("content");

			try {
				w3DocumentContainer = toDocument(content);
				org.dom4j.io.DOMReader reader = new DOMReader();
				document = reader.read(w3DocumentContainer);
				contentXmlElement.add(document.getRootElement());
			} catch (FactoryConfigurationError | ParserConfigurationException | SAXException e) {
				System.err.println("Error parsing LSCS document: " + lscsDocument.getPath() + e.getMessage());
			}
		}

		return xmlDocument;
	}
}
