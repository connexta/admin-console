package org.codice.ddf.admin.beta.discover;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.codice.ddf.admin.api.fields.ListField;
import org.codice.ddf.admin.beta.types.MetacardField;
import org.codice.ddf.admin.common.fields.base.ListFieldImpl;
import org.codice.ddf.admin.common.report.message.ErrorMessage;
import org.opengis.filter.Filter;

import com.google.common.base.CaseFormat;

import ddf.catalog.CatalogFramework;
import ddf.catalog.data.Attribute;
import ddf.catalog.data.Metacard;
import ddf.catalog.data.MetacardType;
import ddf.catalog.data.Result;
import ddf.catalog.operation.impl.QueryImpl;
import ddf.catalog.operation.impl.QueryRequestImpl;

public class TransformCommons {

    public static final String INVALID_SYNTAX = "INVALID_SYNTAX";

    public static ErrorMessage invalidaSyntaxError(List<String> argPath){
        return new ErrorMessage(INVALID_SYNTAX, argPath);
    }

    public static ListField<MetacardField> executeQuery(Filter filter, CatalogFramework framework, List<MetacardType> metacardTypes) {
        ListField<MetacardField> metacardFields = new ListFieldImpl<>(MetacardField.class);
        List<Result> results = Collections.emptyList();

        if(filter == null) {
            return metacardFields;
        }

        try {
            results = framework.query(new QueryRequestImpl(new QueryImpl(filter))).getResults();
        } catch (Exception e) {
            e.printStackTrace();
        }

        results.stream()
                .map(card -> TransformCommons.metacardToMetacardField(card, metacardTypes))
                .filter(Objects::nonNull)
                .forEach(metacardFields::add);
        return metacardFields;
    }

    public static MetacardField metacardToMetacardField(Result result, List<MetacardType> metacardTypes) {
        MetacardField metacardField = new MetacardField(metacardTypes);
        Metacard metacard = result.getMetacard();

        Map<String, Object> values = new HashMap<>();
        metacard.getMetacardType()
                .getAttributeDescriptors()
                .forEach(descriptor -> {
                    String name = descriptorToCamelCase(descriptor.getName());
                    Attribute attribute = metacard.getAttribute(descriptor.getName());
                    if (attribute != null) {
                        if (descriptor.isMultiValued() && attribute.getValues() != null) {
                            values.put(name, attribute.getValues());
                        } else if (attribute.getValue() != null) {
                            values.put(name, attribute.getValue());
                        }
                    }
                });

        metacardField.setValue(values);
        return metacardField;
    }

    public static String descriptorToCamelCase(String str) {
        // graphql doesn't like these characters
        String modStr = str.replaceAll("\\-", "_")
                .replaceAll("\\.", "_")
                .toUpperCase();

        return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, modStr);
    }
}
