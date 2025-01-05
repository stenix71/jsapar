package org.jsapar.text.format;

import org.jsapar.model.CellType;
import org.jsapar.text.Format;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

public class PeriodFormat implements Format<Period> {
    private final DateTimeFormatter formatter;

    public PeriodFormat(DateTimeFormatter formatter) {
        this.formatter = formatter;
    }

    @Override
    public CellType cellType() {
        return CellType.PERIOD;
    }

    @Override
    public Period parse(String csValue) throws ParseException {
        LocalDate date = LocalDate.parse(csValue, formatter);
        return Period.of(date.getYear(), date.getMonthValue(), date.getDayOfMonth());
    }

    @Override
    public String format(Object value) throws IllegalArgumentException {
        Period period = (Period) value;
        LocalDate date = LocalDate.of(period.getYears(), period.getMonths(), period.getDays());
        return date.format(formatter);
    }
}
