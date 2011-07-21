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

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  The ASF licenses this file to You
 * under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.  For additional information regarding
 * copyright in this work, please see the NOTICE file in the top level
 * directory of this distribution.
 */
package com.yourmediashelf.atompub;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.activation.MimeType;
import javax.mail.Header;
import javax.mail.MessagingException;
import javax.mail.internet.InternetHeaders;

import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.parser.ParseException;
import org.apache.abdera.parser.Parser;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.ResponseContext;
import org.apache.abdera.protocol.server.context.EmptyResponseContext;
import org.apache.abdera.protocol.server.context.ResponseContextException;
import org.apache.abdera.protocol.server.impl.AbstractEntityCollectionAdapter;
import org.apache.abdera.protocol.server.multipart.MultipartInputStream;
import org.apache.abdera.protocol.server.multipart.MultipartRelatedCollectionInfo;
import org.apache.abdera.util.Constants;
import org.apache.abdera.util.MimeTypeHelper;
import org.apache.commons.codec.binary.Base64;

/**
 * A composite of org.apache.abdera.protocol.server.impl.AbstractEntityCollectionAdapter 
 * and org.apache.abdera.protocol.server.multipart.AbstractMultipartCollectionAdapter.
 * 
 * @author Edwin Shin
 * 
 */
public abstract class AbstractMultipartEntityCollectionAdapter<T> extends
		AbstractEntityCollectionAdapter<T> implements
		MultipartRelatedCollectionInfo {

	private static final String CONTENT_TYPE_HEADER = "content-type";
	private static final String CONTENT_ID_HEADER = "content-id";
	private static final String START_PARAM = "start";
	private static final String TYPE_PARAM = "type";
	private static final String BOUNDARY_PARAM = "boundary";

	protected Map<String, String> accepts;
	
	@Override
	public abstract T postMedia(MimeType mimeType, String slug, InputStream inputStream,
			RequestContext request) throws ResponseContextException;
	
	@Override
	public abstract String getMediaName(T entry) throws ResponseContextException;

	@Override
	public abstract String getContentType(T entry);
	
	@Override
	public abstract InputStream getMediaStream(T entry) throws ResponseContextException;
	
	@Override
    public abstract boolean isMediaEntry(T entry) throws ResponseContextException;
    
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.abdera.protocol.server.CollectionInfo#getAccepts(org.apache
	 * .abdera.protocol.server.RequestContext)
	 */
	@Override
	public String[] getAccepts(RequestContext request) {
		Collection<String> acceptKeys = getAlternateAccepts(request).keySet();
		return acceptKeys.toArray(new String[acceptKeys.size()]);
	}
	
	@Override
	public ResponseContext postMedia(RequestContext request) {
		try {
			if (MimeTypeHelper.isMultipart(request.getContentType().toString())) {
				System.out.println("*** postMedia(request): is MULTIPART ***");
				
				MultipartRelatedPost post = getMultipartRelatedData(request);
/*
# dataHeaders
content-type: application/pdf
content-id: <99334422@example.com>
*/
				
				return createMultipartEntry(request, post);
			} else {
				System.out.println("*** NOT MULTIPART ***");
				return super.postMedia(request);
			}
		} catch (Exception pe) {
			pe.printStackTrace();
			return new EmptyResponseContext(415, pe.getLocalizedMessage());
		}
	}

	protected MultipartRelatedPost getMultipartRelatedData(
			RequestContext request) throws IOException, ParseException,
			MessagingException {

		MultipartInputStream multipart = getMultipartStream(request);
		multipart.skipBoundary();

		String start = request.getContentType().getParameter(START_PARAM);

		Document<Entry> entry = null;
		Map<String, String> entryHeaders = new HashMap<String, String>();
		InputStream data = null;
		Map<String, String> dataHeaders = new HashMap<String, String>();

		Map<String, String> headers = getHeaders(multipart);

		// check if the first boundary is the media link entry
		if (start == null
				|| start.length() == 0
				|| (headers.containsKey(CONTENT_ID_HEADER) && start
						.equals(headers.get(CONTENT_ID_HEADER)))
				|| (headers.containsKey(CONTENT_TYPE_HEADER) && MimeTypeHelper
						.isAtom(headers.get(CONTENT_TYPE_HEADER)))) {
			entry = getEntry(multipart, request);
			entryHeaders.putAll(headers);
		} else {
			data = getDataInputStream(multipart);
			dataHeaders.putAll(headers);
		}

		multipart.skipBoundary();

		headers = getHeaders(multipart);

		if (start != null
				&& (headers.containsKey(CONTENT_ID_HEADER) && start
						.equals(headers.get(CONTENT_ID_HEADER)))
				&& (headers.containsKey(CONTENT_TYPE_HEADER) && MimeTypeHelper
						.isAtom(headers.get(CONTENT_TYPE_HEADER)))) {
			entry = getEntry(multipart, request);
			entryHeaders.putAll(headers);
		} else {
			data = getDataInputStream(multipart);
			dataHeaders.putAll(headers);
		}

		checkMultipartContent(entry, dataHeaders, request);

		return new MultipartRelatedPost(entry, data, entryHeaders, dataHeaders);
	}
	
	protected ResponseContext createMultipartEntry(RequestContext request, MultipartRelatedPost post) {
        try {
        	Entry entry = post.getEntry().getRoot();
			MimeType mimetype = entry.getContentElement().getMimeType();
			String originalTitle = entry.getTitle();
			String slug = request.getSlug();
			if (slug == null || slug.isEmpty()) {
				slug = originalTitle;
			}
        	
            T entryObj = postMedia(mimetype, slug, post.getData(), request);
            
            IRI feedUri = getFeedIRI(entryObj, request);

            addEntryDetails(request, entry, feedUri, entryObj);
            // Use entry's original title value, rather than slug
            entry.setTitle(originalTitle);
            addMediaContent(feedUri, entry, entryObj, request);

            String location = getLink(entryObj, feedUri, request, true);
            return buildPostMediaEntryResponse(location, entry);
        } catch (ResponseContextException e) {
            return createErrorResponse(e);
        }
    }
	
	private IRI getFeedIRI(T entryObj, RequestContext request) {
        String feedIri = getFeedIriForEntry(entryObj, request);
        return new IRI(feedIri).trailingSlash();
    }

	private MultipartInputStream getMultipartStream(RequestContext request)
			throws IOException, ParseException, IllegalArgumentException {
		String boundary = request.getContentType().getParameter(BOUNDARY_PARAM);

		if (boundary == null) {
			throw new IllegalArgumentException(
					"multipart/related stream invalid, boundary parameter is missing.");
		}

		boundary = "--" + boundary;

		String type = request.getContentType().getParameter(TYPE_PARAM);
		if (!(type != null && MimeTypeHelper.isAtom(type))) {
			throw new ParseException(
					"multipart/related stream invalid, type parameter should be "
							+ Constants.ATOM_MEDIA_TYPE);
		}

		PushbackInputStream pushBackInput = new PushbackInputStream(
				request.getInputStream(), 2);
		pushBackInput.unread("\r\n".getBytes());

		return new MultipartInputStream(pushBackInput, boundary.getBytes());
	}

	private void checkMultipartContent(Document<Entry> entry,
			Map<String, String> dataHeaders, RequestContext request)
			throws ParseException {
		if (entry == null) {
			throw new ParseException(
					"multipart/related stream invalid, media link entry is missing");
		}
		if (!dataHeaders.containsKey(CONTENT_TYPE_HEADER)) {
			throw new ParseException(
					"multipart/related stream invalid, data content-type is missing");
		}
		if (!isContentTypeAccepted(dataHeaders.get(CONTENT_TYPE_HEADER),
				request)) {
			throw new ParseException(
					"multipart/related stream invalid, content-type "
							+ dataHeaders.get(CONTENT_TYPE_HEADER)
							+ " not accepted into this multipart file");
		}
	}

	private Map<String, String> getHeaders(MultipartInputStream multipart)
			throws IOException, MessagingException {
		Map<String, String> mapHeaders = new HashMap<String, String>();
		moveToHeaders(multipart);
		InternetHeaders headers = new InternetHeaders(multipart);

		@SuppressWarnings("unchecked")
		Enumeration<Header> allHeaders = headers.getAllHeaders();
		if (allHeaders != null) {
			while (allHeaders.hasMoreElements()) {
				Header header = allHeaders.nextElement();
				mapHeaders.put(header.getName().toLowerCase(),
						header.getValue());
			}
		}

		return mapHeaders;
	}

	private boolean moveToHeaders(InputStream stream) throws IOException {
		boolean dash = false;
		boolean cr = false;
		int byteReaded;

		while ((byteReaded = stream.read()) != -1) {
			switch (byteReaded) {
			case '\r':
				cr = true;
				dash = false;
				break;
			case '\n':
				if (cr == true)
					return true;
				dash = false;
				break;
			case '-':
				if (dash == true) { // two dashes
					stream.close();
					return false;
				}
				dash = true;
				cr = false;
				break;
			default:
				dash = false;
				cr = false;
			}
		}
		return false;
	}

	// FIXME this will not stand
	private InputStream getDataInputStream(InputStream stream)
			throws IOException {
		Base64 base64 = new Base64();
		ByteArrayOutputStream bo = new ByteArrayOutputStream();

		byte[] buffer = new byte[1024];
		while (stream.read(buffer) != -1) {
			bo.write(buffer);
		}
		return new ByteArrayInputStream(base64.decode(bo.toByteArray()));
	}

	@SuppressWarnings("unchecked")
	private <U extends Element> Document<U> getEntry(InputStream stream,
			RequestContext request) throws ParseException, IOException {
		Parser parser = request.getAbdera().getParser();
		if (parser == null)
			throw new IllegalArgumentException(
					"No Parser implementation was provided");
		Document<?> document = parser.parse(stream, request.getResolvedUri()
				.toString(), parser.getDefaultParserOptions());
		return (Document<U>) document;
	}

	private boolean isContentTypeAccepted(String contentType,
			RequestContext request) {		
		if (getAlternateAccepts(request) == null) {
			return false;
		}
		for (Map.Entry<String, String> accept : getAlternateAccepts(request)
				.entrySet()) {
			if (accept.getKey().equalsIgnoreCase(contentType)
					&& accept.getValue() != null
					&& accept.getValue().equalsIgnoreCase(
							Constants.LN_ALTERNATE_MULTIPART_RELATED)) {
				return true;
			}
		}
		return false;
	}

	protected class MultipartRelatedPost {
		private final Document<Entry> entry;
		private final InputStream data;
		private final Map<String, String> entryHeaders;
		private final Map<String, String> dataHeaders;

		public MultipartRelatedPost(Document<Entry> entry, InputStream data,
				Map<String, String> entryHeaders,
				Map<String, String> dataHeaders) {
			this.entry = entry;
			this.data = data;
			this.entryHeaders = entryHeaders;
			this.dataHeaders = dataHeaders;
		}

		public Document<Entry> getEntry() {
			return entry;
		}

		public InputStream getData() {
			return data;
		}

		public Map<String, String> getEntryHeaders() {
			return entryHeaders;
		}

		public Map<String, String> getDataHeaders() {
			return dataHeaders;
		}

	}

}
