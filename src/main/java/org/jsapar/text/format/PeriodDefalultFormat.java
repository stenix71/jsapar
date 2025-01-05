package org.jsapar.text.format;

import org.jsapar.model.CellType;
import org.jsapar.model.PeriodCell;
import org.jsapar.text.Format;

import java.text.ParseException;
import java.time.Duration;
import java.time.Period;

public class PeriodDefalultFormat implements Format<Period> {

    public PeriodDefalultFormat() {

    }

    @Override
    public CellType cellType() {
        return CellType.PERIOD;
    }

    @Override
    public Period parse(String csValue) throws ParseException {
        return Period.parse(csValue);
    }

    @Override
    public String format(Object value) throws IllegalArgumentException {
        return value.toString();
    }
}
