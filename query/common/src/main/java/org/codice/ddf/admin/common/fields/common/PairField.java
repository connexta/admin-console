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
package org.codice.ddf.admin.common.fields.common;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.concurrent.Callable;
import org.codice.ddf.admin.api.Field;
import org.codice.ddf.admin.common.fields.base.BaseListField;
import org.codice.ddf.admin.common.fields.base.BaseObjectField;
import org.codice.ddf.admin.common.fields.base.scalar.StringField;

public class PairField extends BaseObjectField {

  public static final String DEFAULT_FIELD_NAME = "pair";

  public static final String FIELD_TYPE_NAME = "Pair";

  public static final String DESCRIPTION = "Represents a generic key value pair.";

  public static final String KEY_FIELD_NAME = "key";

  public static final String VALUE_FIELD_NAME = "value";

  private StringField key;

  private StringField value;

  public PairField() {
    super(DEFAULT_FIELD_NAME, FIELD_TYPE_NAME, DESCRIPTION);
    key = new StringField(KEY_FIELD_NAME);
    value = new StringField(VALUE_FIELD_NAME);
    updateInnerFieldPaths();
  }

  @Override
  public List<Field> getFields() {
    return ImmutableList.of(key, value);
  }

  public PairField key(String key) {
    this.key.setValue(key);
    return this;
  }

  public PairField value(String value) {
    this.value.setValue(value);
    return this;
  }

  public String key() {
    return key.getValue();
  }

  public String value() {
    return value.getValue();
  }

  public static class ListImpl extends BaseListField<PairField> {

    public static final String DEFAULT_FIELD_NAME = "entries";

    public ListImpl() {
      super(DEFAULT_FIELD_NAME);
    }

    @Override
    public Callable<PairField> getCreateListEntryCallable() {
      return PairField::new;
    }
  }
}
