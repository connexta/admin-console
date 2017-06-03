package org.codice.ddf.admin.beta;

import java.util.List;

import ddf.catalog.filter.FilterBuilder;

public interface OperandProvider extends FieldOperand {

    List<FieldOperand> getFieldOperands();
}
