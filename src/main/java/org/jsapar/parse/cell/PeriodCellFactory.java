package org.jsapar.parse.cell;

import org.jsapar.model.DurationCell;
import org.jsapar.model.PeriodCell;
import org.jsapar.text.Format;

import java.text.ParseException;
import java.time.Duration;
import java.time.Period;
import java.util.Locale;

public class PeriodCellFactory implements CellFactory<Period> {

    @Override
    public PeriodCell makeCell(String name, String value, Format<Period> format) throws ParseException {
        return new PeriodCell(name, format.parse(value));
    }

    @Override
    public Format<? extends Period> makeFormat(Locale locale) {
        return makeFormat(locale, null);
    }

    @Override
    public Format<? extends Period> makeFormat(Locale locale, String pattern) {
        return Format.ofPeriodInstance(locale, pattern);
    }
}
