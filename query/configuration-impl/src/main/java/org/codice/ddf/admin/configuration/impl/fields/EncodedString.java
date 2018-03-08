package org.codice.ddf.admin.configuration.impl.fields;

import com.google.common.collect.ImmutableList;
import java.util.List;
import org.codice.ddf.admin.api.Field;
import org.codice.ddf.admin.common.fields.base.BaseObjectField;
import org.codice.ddf.admin.common.fields.base.scalar.StringField;

public class EncodedString extends BaseObjectField {

  public static final String FIELD_TYPE_NAME = "EncodedString";

  public static final String DESCRIPTION = "Represents a string with a specific encoding";

  private StringField string;

  private Encoding encoding;

  public String string() {
    return string.getValue();
  }

  public String encoding() {
    return encoding.getValue();
  }

  public EncodedString(String fieldName) {
    super(fieldName, FIELD_TYPE_NAME, DESCRIPTION);
    string = new StringField();
    encoding = new Encoding();
  }

  @Override
  public List<Field> getFields() {
    return ImmutableList.of(string, encoding);
  }
}
