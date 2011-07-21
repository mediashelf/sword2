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

import javax.xml.namespace.QName;
import org.apache.abdera.util.Constants;

public interface Sword2Constants {
	// Namespaces
    public static String SWORD_TERMS_NS = "http://purl.org/net/sword/terms/";
    public static String APP_NS = Constants.APP_NS;
    public static String DC_NS = "http://purl.org/dc/terms/";
    public static String ORE_NS = "http://www.openarchives.org/ore/terms/";
    public static String ATOM_NS = Constants.ATOM_NS;

    // QNames for Extension Elements
    public static QName SWORD_VERSION = new QName(SWORD_TERMS_NS, "version");
    public static QName SWORD_MAX_UPLOAD_SIZE = new QName(SWORD_TERMS_NS, "maxUploadSize");
    public static QName SWORD_COLLECTION_POLICY = new QName(SWORD_TERMS_NS, "collectionPolicy");
    public static QName SWORD_MEDIATION = new QName(SWORD_TERMS_NS, "mediation");
    public static QName SWORD_TREATMENT = new QName(SWORD_TERMS_NS, "treatment");
    public static QName SWORD_ACCEPT_PACKAGING = new QName(SWORD_TERMS_NS, "acceptPackaging");
    public static QName SWORD_SERVICE = new QName(SWORD_TERMS_NS, "service");
    public static QName SWORD_PACKAGING = new QName(SWORD_TERMS_NS, "packaging");
    public static QName SWORD_VERBOSE_DESCRIPTION = new QName(SWORD_TERMS_NS, "verboseDescription");
    public static QName APP_ACCEPT = new QName(APP_NS, "accept");

    // URIs for the statement
    public static String SWORD_DEPOSITED_BY = SWORD_TERMS_NS + "depositedBy";
    public static String SWORD_DEPOSITED_ON_BEHALF_OF = SWORD_TERMS_NS + "depositedOnBehalfOf";
    public static String SWORD_DEPOSITED_ON = SWORD_TERMS_NS + "depositedOn";
    public static String SWORD_ORIGINAL_DEPOSIT = SWORD_TERMS_NS + "originalDeposit";
    public static String SWORD_STATE_DESCRIPTION = SWORD_TERMS_NS + "stateDescription";
    public static String SWORD_STATE = SWORD_TERMS_NS + "state";

    // rel values
    public static String REL_STATEMENT = "http://purl.org/net/sword/terms/statement";
    public static String REL_SWORD_EDIT = "http://purl.org/net/sword/terms/add";
    public static String REL_ORIGINAL_DEPOSIT = "http://purl.org/net/sword/terms/originalDeposit";
    public static String REL_DERIVED_RESOURCE = "http://purl.org/net/sword/terms/derivedResource";

    // Package Formats
    public static String PACKAGE_SIMPLE_ZIP = "http://purl.org/net/sword/package/SimpleZip";
    public static String PACKAGE_BINARY = "http://purl.org/net/sword/package/Binary";

    // Error Codes
    public static String ERROR_BAD_REQUEST = "http://purl.org/net/sword/error/ErrorBadRequest";
    public static String ERROR_CONTENT = "http://purl.org/net/sword/error/ErrorContent";
    public static String ERROR_CHECKSUM_MISMATCH = "http://purl.org/net/sword/error/ErrorChecksumMismatch";
    public static String ERROR_TARGET_OWNER_UNKNOWN = "http://purl.org/net/sword/error/TargetOwnerUnknown";
    public static String ERROR_MEDIATION_NOT_ALLOWED = "http://purl.org/net/sword/error/MediationNotAllowed";
    public static String ERROR_METHOD_NOT_ALLOWED = "http://purl.org/net/sword/error/MethodNotAllowed";
}
