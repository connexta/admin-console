/**
 * Copyright (c) Codice Foundation
 * <p>
 * This is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser
 * General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or any later version.
 * <p>
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details. A copy of the GNU Lesser General Public License
 * is distributed along with this program and can be found at
 * <http://www.gnu.org/licenses/lgpl.html>.
 **/
package org.codice.ddf.admin.ldap.discover;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.codice.ddf.admin.api.DataType;
import org.codice.ddf.admin.api.fields.FunctionField;
import org.codice.ddf.admin.common.fields.base.BaseFunctionField;
import org.codice.ddf.admin.common.fields.base.scalar.IntegerField;
import org.codice.ddf.admin.common.fields.common.MapField;
import org.codice.ddf.admin.ldap.commons.LdapConnectionAttempt;
import org.codice.ddf.admin.ldap.commons.LdapTestingUtils;
import org.codice.ddf.admin.ldap.fields.LdapDistinguishedName;
import org.codice.ddf.admin.ldap.fields.connection.LdapBindUserInfo;
import org.codice.ddf.admin.ldap.fields.connection.LdapConnectionField;
import org.codice.ddf.admin.ldap.fields.query.LdapQueryField;
import org.forgerock.opendj.ldap.Attribute;
import org.forgerock.opendj.ldap.ByteString;
import org.forgerock.opendj.ldap.SearchScope;
import org.forgerock.opendj.ldap.responses.SearchResultEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

public class LdapQuery extends BaseFunctionField<MapField.ListImpl> {

    private static final Logger LOGGER = LoggerFactory.getLogger(LdapQuery.class);

    public static final String FIELD_NAME = "query";

    public static final String DESCRIPTION = "Executes a query against LDAP.";

    public static final String MAX_QUERY_FIELD_NAME = "maxQueryResults";

    public static final String QUERY_BASE_FIELD_NAME = "queryBase";

    private static final int DEFAULT_MAX_QUERY_RESULTS = 25;

    public static final MapField.ListImpl RETURN_TYPE = new MapField.ListImpl();

    private LdapConnectionField conn;

    private LdapBindUserInfo creds;

    private IntegerField maxQueryResults;

    private LdapDistinguishedName queryBase;

    private LdapQueryField query;

    private LdapTestingUtils utils;

    public LdapQuery() {
        super(FIELD_NAME, DESCRIPTION);
        conn = new LdapConnectionField().useDefaultRequired();
        creds = new LdapBindUserInfo().useDefaultRequired();
        maxQueryResults = new IntegerField(MAX_QUERY_FIELD_NAME);
        queryBase = new LdapDistinguishedName(QUERY_BASE_FIELD_NAME);
        queryBase.isRequired(true);
        query = new LdapQueryField();
        query.isRequired(true);
        updateArgumentPaths();

        utils = new LdapTestingUtils();
    }

    @Override
    public List<DataType> getArguments() {
        return ImmutableList.of(conn, creds, maxQueryResults, queryBase, query);
    }

    @Override
    public MapField.ListImpl performFunction() {
        List<SearchResultEntry> searchResults;
        List<MapField> convertedSearchResults = new ArrayList<>();
        try (LdapConnectionAttempt connectionAttempt = utils.bindUserToLdapConnection(conn,
                creds)) {
            addMessages(connectionAttempt);

            if (containsErrorMsgs()) {
                return null;
            }

            searchResults = utils.getLdapQueryResults(connectionAttempt.result(),
                    queryBase.getValue(),
                    query.getValue(),
                    SearchScope.WHOLE_SUBTREE,
                    maxQueryResults.getValue() == null ?
                            DEFAULT_MAX_QUERY_RESULTS :
                            maxQueryResults.getValue());

            for (SearchResultEntry entry : searchResults) {
                MapField entryMap = new MapField();
                for (Attribute attri : entry.getAllAttributes()) {
                    entryMap.put("name",
                            entry.getName()
                                    .toString());
                    if (!attri.getAttributeDescriptionAsString()
                            .toLowerCase()
                            .contains("password")) {
                        List<String> attributeValueList = attri.parallelStream()
                                .map(ByteString::toString)
                                .collect(Collectors.toList());
                        String attributeValue = attributeValueList.size() == 1 ?
                                attributeValueList.get(0) :
                                attributeValueList.toString();
                        entryMap.put(attri.getAttributeDescriptionAsString(), attributeValue);
                    }
                }
                convertedSearchResults.add(entryMap);
            }
        } catch (IOException e) {
            LOGGER.warn("Error closing LDAP connection", e);
        }

        return new MapField.ListImpl().addAll(convertedSearchResults);
    }

    @Override
    public FunctionField<MapField.ListImpl> newInstance() {
        return new LdapQuery();
    }


    @Override
    public MapField.ListImpl getReturnType() {
        return RETURN_TYPE;
    }

    /**
     * Intentionally scoped as private.
     * This is a test support method to be invoked by Spock tests which will elevate scope as needed
     * in order to execute. If Java-based unit tests are ever needed, this scope will need to be
     * updated to package-private.
     *
     * @param utils Ldap support utilities
     */
    private void setTestingUtils(LdapTestingUtils utils) {
        this.utils = utils;
    }
}
