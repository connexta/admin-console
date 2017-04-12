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
package org.codice.ddf.admin.ldap.actions.discover;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.codice.ddf.admin.api.fields.Field;
import org.codice.ddf.admin.common.actions.BaseAction;
import org.codice.ddf.admin.common.fields.base.scalar.IntegerField;
import org.codice.ddf.admin.common.fields.common.MapField;
import org.codice.ddf.admin.ldap.actions.commons.LdapConnectionAttempt;
import org.codice.ddf.admin.ldap.actions.commons.LdapTestingUtils;
import org.codice.ddf.admin.ldap.fields.LdapDistinguishedName;
import org.codice.ddf.admin.ldap.fields.connection.LdapBindUserInfo;
import org.codice.ddf.admin.ldap.fields.connection.LdapConnectionField;
import org.codice.ddf.admin.ldap.fields.query.LdapEntriesListField;
import org.codice.ddf.admin.ldap.fields.query.LdapQueryField;
import org.forgerock.opendj.ldap.Attribute;
import org.forgerock.opendj.ldap.ByteString;
import org.forgerock.opendj.ldap.SearchScope;
import org.forgerock.opendj.ldap.responses.SearchResultEntry;

import com.google.common.collect.ImmutableList;

public class LdapQuery extends BaseAction<LdapEntriesListField> {

    public static final String NAME = "query";

    public static final String DESCRIPTION = "Executes a query against LDAP.";

    private LdapConnectionField conn;

    private LdapBindUserInfo creds;

    private IntegerField maxQueryResults;

    private LdapDistinguishedName dn;

    private LdapQueryField query;

    private LdapTestingUtils utils;

    public LdapQuery() {
        super(NAME, DESCRIPTION, new LdapEntriesListField());
        conn = new LdapConnectionField();
        creds = new LdapBindUserInfo();
        maxQueryResults = new IntegerField("maxQueryResults");
        dn = new LdapDistinguishedName("queryBase");
        query = new LdapQueryField();
        utils = new LdapTestingUtils();
    }

    @Override
    public List<Field> getArguments() {
        // TODO: tbatie - 4/3/17 - Add other args
        return ImmutableList.of(conn, creds, dn, maxQueryResults, dn, query);
    }

    @Override
    public LdapEntriesListField performAction() {

        LdapConnectionAttempt connectionAttempt = utils.bindUserToLdapConnection(conn, creds);
        addReturnValueMessages(connectionAttempt.messages());

        if(!connectionAttempt.connection().isPresent()) {
            return null;
        }

        List<SearchResultEntry> searchResults = utils.getLdapQueryResults(
                connectionAttempt.connection().get(),
                dn.getValue(),
                query.getValue(),
                SearchScope.WHOLE_SUBTREE,
                maxQueryResults.getValue());

        List<MapField> convertedSearchResults = new ArrayList<>();

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
                    String attributeValue = attributeValueList.size() == 1 ? attributeValueList.get(
                            0) : attributeValueList.toString();
                    entryMap.put(attri.getAttributeDescriptionAsString(), attributeValue);
                }
            }
            convertedSearchResults.add(entryMap);
        }

        return new LdapEntriesListField().addAll(convertedSearchResults);
    }
}
