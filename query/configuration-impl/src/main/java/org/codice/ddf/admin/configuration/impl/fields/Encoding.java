package org.codice.ddf.admin.configuration.impl.fields;

import com.google.common.collect.ImmutableList;
import java.util.List;
import org.codice.ddf.admin.api.fields.EnumValue;
import org.codice.ddf.admin.common.fields.base.BaseEnumField;
import org.codice.ddf.admin.common.fields.base.BaseEnumValue;

public class Encoding extends BaseEnumField<String> {

  public static final String DEFAULT_FIELD_NAME = "encoding";

  public static final String FIELD_TYPE_NAME = "Encoding";

  public static final String DESCRIPTION = "A set of supported encoding strings";

  public static final List<EnumValue<String>> ENUM_VALUES = ImmutableList.of(Utf8.UTF_8);

  public Encoding() {
    super(DEFAULT_FIELD_NAME, FIELD_TYPE_NAME, DESCRIPTION, ENUM_VALUES);
  }

  public static class Utf8 extends BaseEnumValue<String> {

    public static final String TITLE = "UTF_8";

    // TODO: tbatie - 3/2/18 - UTF-8 description
    public static final String DESCRIPTION = "TODO UTF-8 description";

    public Utf8() {
      super(TITLE, DESCRIPTION, TITLE);
    }

    public static final Utf8 UTF_8 = new Utf8();
  }
}
