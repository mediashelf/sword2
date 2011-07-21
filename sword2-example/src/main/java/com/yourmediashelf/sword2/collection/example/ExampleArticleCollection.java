/**
 * Copyright (C) 2011 MediaShelf <http://www.yourmediashelf.com/>
 *
 * This file is part of sword2.
 *
 * sword2 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * sword2 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with sword2.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.yourmediashelf.sword2.collection.example;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.activation.MimeType;

import org.apache.abdera.Abdera;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.i18n.text.UrlEncoding;
import org.apache.abdera.model.Content;
import org.apache.abdera.model.Person;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.context.ResponseContextException;
import org.apache.abdera.util.Constants;

import com.yourmediashelf.atompub.AbstractMultipartEntityCollectionAdapter;
import com.yourmediashelf.com.sword2.util.Sword2Util;

/**
 * Example CollectionAdapter implementation to store Atompub collection entries.
 */
public class ExampleArticleCollection extends
		AbstractMultipartEntityCollectionAdapter<ExampleArticle> {

	private Factory factory = new Abdera().getFactory();
	private AtomicInteger nextId = new AtomicInteger(1000);
	private Map<String, ExampleArticle> articles = new HashMap<String, ExampleArticle>();

	// BEGIN: Feed Metadata
	/**
	 * Unique id for this collection.
	 * 
	 */
	@Override
	public String getId(RequestContext request) {
		return "urn:foo:bar:article:feed";
	}

	@Override
	public String getTitle(RequestContext request) {
		return "SWORD2 Example Articles Collection";
	}

	@Override
	public String getAuthor(RequestContext request)
			throws ResponseContextException {
		return "Anonymous";
	}

	// END: Feed Metadata

	// BEGIN: Entry Metadata

	@Override
	public String getId(ExampleArticle article) throws ResponseContextException {
		return "urn:" + article.getId();
	}

	@Override
	public String getTitle(ExampleArticle article)
			throws ResponseContextException {
		return article.getTitle();
	}

	@Override
	public List<Person> getAuthors(ExampleArticle entry, RequestContext request)
			throws ResponseContextException {
		Person author = request.getAbdera().getFactory().newAuthor();
		author.setName(entry.getAuthor());
		return Arrays.asList(author);
	}

	@Override
	public String getName(ExampleArticle article)
			throws ResponseContextException {
		return article.getId() + "-" + article.getTitle().replaceAll(" ", "_");
	}

	@Override
	public Object getContent(ExampleArticle article, RequestContext request)
			throws ResponseContextException {
		Content content = factory.newContent(Content.Type.TEXT);
		content.setText(article.getText());
		return content;
	}

	@Override
	public Date getUpdated(ExampleArticle article)
			throws ResponseContextException {
		return article.getDate();
	}

	// END: Entry Metadata

	@Override
	public Iterable<ExampleArticle> getEntries(RequestContext request)
			throws ResponseContextException {
		return articles.values();
	}

	@Override
	public ExampleArticle getEntry(String resourceName, RequestContext request)
			throws ResponseContextException {
		
		System.out.println("example getEntry: " + resourceName);
		
		System.out.println("request.getTargetPath(): " + request.getTargetPath());
		//
		String path = request.getTargetPath();
        int q = path.indexOf("?");
        if (q != -1) {
            path = path.substring(0, q);
        }
        String[] segments = path.split("/");
        System.out.println("segments: " + segments.length);
        for (String s : segments) {
        	System.out.println("** " + s);
        }
        String last = segments[segments.length - 1];
        if (last.equals("media")) {
        	String mediaLinkId = segments[segments.length -2];
        	String mediaParentId = segments[segments.length -3];
        	System.out.println("mediaLinkId: " + mediaLinkId);
        	System.out.println("mediaParentId: " + mediaParentId);
        	resourceName = UrlEncoding.decode(mediaLinkId);
        }
        
        //
		
		Integer id = getIdFromResourceName(resourceName);
		return articles.get(id.toString());
	}

	@Override
	public void deleteEntry(String resourceName, RequestContext request)
			throws ResponseContextException {
		Integer id = getIdFromResourceName(resourceName);
		articles.remove(id);
	}

	@Override
	public ExampleArticle postEntry(String title, IRI id, String summary,
			Date updated, List<Person> authors, Content content,
			RequestContext request) throws ResponseContextException {
		
		ExampleArticle article = new ExampleArticle();
		article.setId(Integer.toString(nextId.getAndIncrement()));
		article.setTitle(title);
		if (authors != null) {
			article.setAuthor(authors.get(0).getName());
		}
		article.setDate(updated);
		if (content != null) {
			article.setText(content.getText());
		}
		articles.put(article.getId(), article);		
		return article;
	}

	@Override
	public void putEntry(ExampleArticle article, String title, Date updated,
			List<Person> authors, String summary, Content content,
			RequestContext request) throws ResponseContextException {
		article.setTitle(title);
		article.setAuthor(authors.get(0).getName());
		article.setDate(updated);
		article.setText(content.getText());
	}

	// BEGIN: Multipart methods

	@Override
	public ExampleArticle postMedia(MimeType mimeType, String slug, InputStream inputStream,
			RequestContext request) throws ResponseContextException {
		System.out.println("?? postMedia: ");
		System.out.println("? slug: " + slug);
		if (slug == null) {
			slug = "untitled";
		}
		
		ExampleArticle article = postEntry(slug, null, null, new Date(), null, null, request);
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		Sword2Util.copy(inputStream, bout);
		article.setNewMedia(slug, bout.toByteArray());
		
		return article;
	}
	
	@Override
	public String getMediaName(ExampleArticle entry) throws ResponseContextException {
		//return entry.getId() + "-" + entry.getTitle() + "/media/" + entry.getId() + "-media";
		String name = entry.getId() + "-" + entry.getTitle() + "/media/" + entry.getId() + "-media";
		System.out.println("? getMediaName(): " + name);
		return name;
    }
	
	@Override
	public String getContentType(ExampleArticle entry) {
        return "application/pdf";
    }

	@Override
	public Map<String, String> getAlternateAccepts(RequestContext request) {		
		if (accepts == null) {
			accepts = new HashMap<String, String>() {
				private static final long serialVersionUID = 1;
				{
					put("application/pdf", Constants.LN_ALTERNATE_MULTIPART_RELATED);
				}
			};
		}
		return accepts;
	}
	
	@Override
	public InputStream getMediaStream(ExampleArticle entry) throws ResponseContextException {
		//InputStream is = new ByteArrayInputStream(entry.getMedia());
		System.out.println("?? getMediaStream: " + entry.getTitle());
		InputStream is = new ByteArrayInputStream(entry.getNewMedia(entry.getTitle()));
		return is;
    }
	
	@Override
    public boolean isMediaEntry(ExampleArticle entry) throws ResponseContextException {
		boolean isMedia = entry.getMedia() != null;
        return isMedia;
    }

	// END: Multipart methods

	private Integer getIdFromResourceName(String resourceName)
			throws ResponseContextException {
		System.out.println("getIdFromResourceName resource: " + resourceName);
		int idx = resourceName.indexOf("-");
		if (idx == -1) {
			System.out.println("*-*-*-*-*-*-*-*");
			throw new ResponseContextException(404);
		}
		return new Integer(resourceName.substring(0, idx));
	}
}
