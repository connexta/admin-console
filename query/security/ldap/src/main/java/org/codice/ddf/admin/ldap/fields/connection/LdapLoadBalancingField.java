/**
 * Copyright (c) Codice Foundation
 *
 * <p>This is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or any later version.
 *
 * <p>This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details. A copy of the GNU Lesser General Public
 * License is distributed along with this program and can be found at
 * <http://www.gnu.org/licenses/lgpl.html>.
 */
package org.codice.ddf.admin.ldap.fields.connection;

import com.google.common.collect.ImmutableList;
import org.codice.ddf.admin.api.fields.EnumValue;
import org.codice.ddf.admin.common.fields.base.BaseEnumField;

public class LdapLoadBalancingField extends BaseEnumField<String> {
  public static final String DEFAULT_FIELD_NAME = "ldapLoadBalancing";

  public static final String FIELD_TYPE_NAME = "LdapLoadBalancing";

  public static final String DESCRIPTION =
      "The load balancing algorithm to use for LDAP connections";

  public LdapLoadBalancingField() {
    this(new RoundRobinEnumValue());
  }

  private LdapLoadBalancingField(EnumValue<String> loadBalancing) {
    super(
        DEFAULT_FIELD_NAME,
        FIELD_TYPE_NAME,
        DESCRIPTION,
        ImmutableList.of(new RoundRobinEnumValue(), new FailoverEnumValue()),
        loadBalancing);
  }

  public static final class RoundRobinEnumValue implements EnumValue<String> {
    public static final String ROUND_ROBIN = "roundRobin";

    public static final String DESCRIPTION =
        "The configured LDAP server cluster will be treated as an all active cluster and connections to the cluster will be made in a round-robin order.";

    @Override
    public String getEnumTitle() {
      return ROUND_ROBIN;
    }

    @Override
    public String getDescription() {
      return DESCRIPTION;
    }

    @Override
    public String getValue() {
      return ROUND_ROBIN;
    }
  }

  public static final class FailoverEnumValue implements EnumValue<String> {
    public static final String FAILOVER = "failover";

    public static final String DESCRIPTION =
        "The configured LDAP server cluster will be treated as a failover cluster and only the primary (first) server will be connected to unless the connections to it fail at which point the next server in the list will be used.";

    @Override
    public String getEnumTitle() {
      return FAILOVER;
    }

    @Override
    public String getDescription() {
      return DESCRIPTION;
    }

    @Override
    public String getValue() {
      return FAILOVER;
    }
  }
}
