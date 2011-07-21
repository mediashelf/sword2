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
package com.yourmediashelf.com.sword2;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import org.apache.abdera.protocol.server.CategoriesInfo;
import org.apache.abdera.protocol.server.CategoryInfo;
import org.apache.abdera.protocol.server.CollectionAdapter;
import org.apache.abdera.protocol.server.CollectionInfo;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.RequestProcessor;
import org.apache.abdera.protocol.server.ResponseContext;
import org.apache.abdera.protocol.server.WorkspaceInfo;
import org.apache.abdera.protocol.server.WorkspaceManager;
import org.apache.abdera.protocol.server.context.StreamWriterResponseContext;
import org.apache.abdera.protocol.server.multipart.MultipartRelatedCollectionInfo;
import org.apache.abdera.util.Constants;
import org.apache.abdera.writer.StreamWriter;


public class Sword2ServiceRequestProcessor implements RequestProcessor,
		Sword2Constants {

	public ResponseContext process(RequestContext context,
			WorkspaceManager workspaceManager,
			CollectionAdapter collectionAdapter) {
		return this.processService(context, workspaceManager);
	}

	private ResponseContext processService(RequestContext context,
			WorkspaceManager workspaceManager) {
		String method = context.getMethod();
		if (method.equalsIgnoreCase("GET")) {
			return this.getServiceDocument(context, workspaceManager);
		} else {
			return null;
		}
	}

	// TODO needs to handle
	// - on-behalf-of header
	// - max upload size
	protected ResponseContext getServiceDocument(final RequestContext request,
			final WorkspaceManager workspaceManager) {
		return new StreamWriterResponseContext(request.getAbdera()) {

			protected void writeTo(StreamWriter sw) throws IOException {
				sw.startDocument().startService();
				//
				sw.writeNamespace("sword", SWORD_TERMS_NS);
				sw.writeNamespace("atom", ATOM_NS);

				sw.startElement(SWORD_VERSION);
				sw.writeElementText("2.0");
				sw.endElement();

				//
				for (WorkspaceInfo wi : workspaceManager.getWorkspaces(request)) {
					sw.startWorkspace().writeTitle(wi.getTitle(request));
					Collection<CollectionInfo> collections = wi
							.getCollections(request);

					if (collections != null) {
						for (CollectionInfo ci : collections) {
							sw.startCollection(ci.getHref(request)).writeTitle(
									ci.getTitle(request));
							if (ci instanceof MultipartRelatedCollectionInfo) {								
								MultipartRelatedCollectionInfo multipartCi = (MultipartRelatedCollectionInfo) ci;
								for (Map.Entry<String, String> accept : multipartCi
										.getAlternateAccepts(request)
										.entrySet()) {
									sw.startElement(Constants.ACCEPT);
									if (accept.getValue() != null
											&& accept.getValue().length() > 0) {
										sw.writeAttribute(
												Constants.LN_ALTERNATE,
												accept.getValue());
									}
									sw.writeElementText(accept.getKey())
											.endElement();
								}
							} else {
								sw.writeAccepts(ci.getAccepts(request));
							}
							CategoriesInfo[] catinfos = ci
									.getCategoriesInfo(request);
							if (catinfos != null) {
								for (CategoriesInfo catinfo : catinfos) {
									String cathref = catinfo.getHref(request);
									if (cathref != null) {
										sw.startCategories()
												.writeAttribute(
														"href",
														request.getTargetBasePath()
																+ cathref)
												.endCategories();
									} else {
										sw.startCategories(
												catinfo.isFixed(request),
												catinfo.getScheme(request));
										for (CategoryInfo cat : catinfo) {
											sw.writeCategory(
													cat.getTerm(request),
													cat.getScheme(request),
													cat.getLabel(request));
										}
										sw.endCategories();
									}
								}
							}
							sw.endCollection();
						}
					}
					sw.endWorkspace();
				}
				sw.endService().endDocument();
			}
		}.setStatus(200).setContentType(Constants.APP_MEDIA_TYPE);
	}
}
