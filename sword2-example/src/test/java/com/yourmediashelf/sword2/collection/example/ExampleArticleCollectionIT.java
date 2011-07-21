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

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import org.apache.abdera.Abdera;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Base;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Service;
import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.abdera.protocol.client.ClientResponse;
import org.apache.abdera.protocol.client.RequestOptions;
import org.apache.abdera.writer.Writer;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.junit.Test;

import com.yourmediashelf.com.sword2.Sword2Constants;

public class ExampleArticleCollectionIT implements Sword2Constants {
	private String jettyPort = System.getProperty("jetty.port");
	private String baseUrl = "http://localhost:" + jettyPort + "/sword2-example/";
	private AbderaClient client = new AbderaClient();

	@Test
	public void testServiceDocument() {
        ClientResponse response = client.get(baseUrl);
        assertEquals(200, response.getStatus());
        
        Document<Service> serviceDoc = response.getDocument();
		Service service = serviceDoc.getRoot();

		assertEquals("2.0", service.getSimpleExtension(SWORD_VERSION));
		//TODO: test for "accept" element
	}
	
	@Test
	public void testArticleFeed() {
		ClientResponse response = client.get(baseUrl + "articles");
		assertEquals(200, response.getStatus());
		Document<Feed> feedDoc = response.getDocument();
		Feed feed = feedDoc.getRoot();
		assertEquals("SWORD2 Example Articles Collection", feed.getTitle());
	}
	
	@Test
	public void testPostArticle() throws Exception {
		Abdera abdera = new Abdera();
		Factory factory = abdera.getFactory();
		
		IRI colUri = new IRI(baseUrl).resolve("articles");
		
		Entry entry = factory.newEntry();
        entry.setTitle("The Migrational Behaviors of Frogs");
        entry.setUpdated(new Date());
        entry.addAuthor("Jim Smith");
        entry.setId(factory.newUuidUri());
        entry.setContent("The quick brown fox jumped over the lazy dog.");
        
        RequestOptions opts = new RequestOptions();
        opts.setContentType("application/atom+xml;type=entry");
        ClientResponse res = client.post(colUri.toString(), entry, opts);
        assertEquals(201, res.getStatus());
        prettyPrint(abdera, res.getDocument());
        
        IRI location = res.getLocation();
        System.out.println("LOCATION: " + location.toString());
        
        // GET the entry
        res = client.get(location.toString());
        assertEquals(200, res.getStatus());

        // prettyPrint(abdera, res.getDocument());
        
        Document<Entry> entry_doc = res.getDocument();
        Entry entry2 = entry_doc.getRoot();
        assertEquals(entry.getTitle(), entry2.getTitle());
        assertEquals(entry.getContent(), entry2.getContent());
        assertEquals(entry.getAuthor().getName(), entry2.getAuthor().getName());
	}
	
	@Test
	public void testPostPDF() throws Exception {
		IRI colUri = new IRI(baseUrl).resolve("articles");
        
        InputStream mediaResource = this.getClass().getResourceAsStream("testArticle.pdf");
        
        RequestOptions opts = new RequestOptions();
        opts.setSlug("A Foo Story");
        //FIXME set content-disposition header per 6.3.1
        
        InputStreamRequestEntity entity = new InputStreamRequestEntity(mediaResource, "application/pdf");
        ClientResponse res = client.post(colUri.toString(), entity, opts);
        assertEquals(201, res.getStatus());
        
        Document<Entry> entryDoc = res.getDocument();
        
        prettyPrint(new Abdera(), entryDoc);
        IRI location = res.getLocation();
        int idx = location.toString().lastIndexOf("-") + 1;
        assertEquals("A_Foo_Story", location.toString().substring(idx));
        
        Entry entry = entryDoc.getRoot();
        res = client.get(entry.getContentSrc().toString());
        assertEquals("application/pdf", res.getContentType().toString());
        assertEquals(7843, res.getContentLength());
	}
	
	@Test
	public void testPost2PDFs() throws Exception {
		IRI colUri = new IRI(baseUrl).resolve("articles");
        
        InputStream mediaResource = this.getClass().getResourceAsStream("testArticle.pdf");
        
        RequestOptions opts = new RequestOptions();
        opts.setSlug("A Foo Story");
        
        InputStreamRequestEntity entity = new InputStreamRequestEntity(mediaResource, "application/pdf");
        ClientResponse res = client.post(colUri.toString(), entity, opts);
        assertEquals(201, res.getStatus());
        
        Document<Entry> entryDoc = res.getDocument();
        
        IRI location = res.getLocation();
        int idx = location.toString().lastIndexOf("-") + 1;
        assertEquals("A_Foo_Story", location.toString().substring(idx));
        
        Entry entry = entryDoc.getRoot();
        res = client.get(entry.getContentSrc().toString());
        assertEquals("application/pdf", res.getContentType().toString());
        assertEquals(7843, res.getContentLength());
        
        mediaResource = this.getClass().getResourceAsStream("testArticle2.pdf");
        opts = new RequestOptions();
        opts.setSlug("A Bar Story");
        
        entity = new InputStreamRequestEntity(mediaResource, "application/pdf");
        res = client.post(colUri.toString(), entity, opts);
        assertEquals(201, res.getStatus());
        prettyPrint(new Abdera(), res.getDocument());
	}
	
	@Test
	public void testPostMultipartArticle() throws Exception {
		Abdera abdera = new Abdera();
		Factory factory = abdera.getFactory();
		
		IRI colUri = new IRI(baseUrl).resolve("articles");
		
		Entry entry = factory.newEntry();
        entry.setTitle("The Reproductive Behavior of Frogs");
        entry.addAuthor("Jim Smith");
        entry.setUpdated(new Date());
        entry.setSummary("some summary");
        entry.setId("urn:1004");
        entry.setContent(new IRI("cid:99334422@example.com"), "application/pdf");
        
        InputStream mediaResource = this.getClass().getResourceAsStream("testArticle.pdf");
     
        ClientResponse res = client.post(colUri.toString(), entry, mediaResource);
        assertEquals(201, res.getStatus());
        //prettyPrint(abdera, res.getDocument());

        Document<Entry> entryDoc = res.getDocument();
        entry = entryDoc.getRoot();
        res = client.get(entry.getContentSrc().toString());
        
        assertEquals("application/pdf", res.getContentType().toString());
        assertEquals(8192, res.getContentLength());
	}
	
	protected void prettyPrint(Abdera abdera, Base doc) throws IOException {
        Writer writer = abdera.getWriterFactory().getWriter("prettyxml");
        writer.writeTo(doc, System.out);
        System.out.println();
    }

}
