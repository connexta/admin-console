package org.codice.ddf.admin.beta.fields.expression.attribute;

import java.util.ArrayList;
import java.util.List;

import org.codice.ddf.admin.api.Field;
import org.codice.ddf.admin.beta.FieldOperand;
import org.codice.ddf.admin.common.fields.base.BaseObjectField;
import org.codice.ddf.admin.common.fields.base.scalar.StringField;
import org.opengis.filter.Filter;

import com.google.common.collect.ImmutableList;

import ddf.catalog.filter.FilterBuilder;

public class ContextualExpression {

    public static class Like extends BaseObjectField implements FieldOperand {

        private StringField string;
        private StringField fuzzyString;
        private StringField caseSensitiveString;

        public Like() {
            super("like", "Like", "todo");
            string = new StringField();
            fuzzyString = new StringField("fuzzyString");
            caseSensitiveString = new StringField("caseInsensitiveString");
            updateInnerFieldPaths();
        }

        @Override
        public List<Field> getFields() {
            return ImmutableList.of(string, fuzzyString, caseSensitiveString);
        }

        @Override
        public Filter buildFilter(String attribute, Class attributeType, FilterBuilder builder) {
            List<Filter> filters = new ArrayList<>();
            if(string.getValue() != null) {
                filters.add(builder.attribute(attribute)
                        .is()
                        .like()
                        .text(string.getValue()));
            }

            if(fuzzyString.getValue() != null) {
                filters.add(builder.attribute(attribute)
                        .is()
                        .like()
                        .fuzzyText(fuzzyString.getValue()));
            }

            if(caseSensitiveString.getValue() != null) {
                filters.add(builder.attribute(attribute)
                        .is()
                        .like()
                        .caseSensitiveText(caseSensitiveString.getValue()));
            }

            return FieldOperand.allOf(builder, filters);
        }
    }
}
