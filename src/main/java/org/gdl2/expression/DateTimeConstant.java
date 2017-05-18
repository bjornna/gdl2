package org.gdl2.expression;

import lombok.EqualsAndHashCode;
import lombok.Value;
import org.gdl2.datatypes.DvDateTime;

@EqualsAndHashCode(callSuper = true)
@Value
public class DateTimeConstant extends ConstantExpression {
    private DvDateTime dateTimeValue;

    public DateTimeConstant(String dateTime) {
        super(dateTime);
        this.dateTimeValue = DvDateTime.valueOf(dateTime);
    }

    public String toString() {
        return "(" + dateTimeValue.toString() + ")";
    }
}