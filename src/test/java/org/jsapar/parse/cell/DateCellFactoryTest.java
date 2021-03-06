package org.jsapar.parse.cell;

import org.jsapar.model.Cell;
import org.jsapar.model.DateCell;
import org.junit.Test;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import static org.junit.Assert.assertEquals;

public class DateCellFactoryTest {

    DateCellFactory cellFactory = new DateCellFactory();


    @Test
    public final void testDateCellStringStringFormat() throws ParseException {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        DateCell cell = (DateCell) cellFactory.makeCell("Name", "2007-10-01 14:13", format);
        assertEquals("Name", cell.getName());
        assertEquals(format.parse("2007-10-01 14:13"), cell.getValue());
    }


    /**
     * Test method for {@link Cell#getStringValue()}.
     */
    @Test
    public final void testGetStringValue() throws ParseException {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        DateCell cell = (DateCell) cellFactory.makeCell("Name", "2007-10-01 14:13", format);

        // Compare without the zone part
        assertEquals("2007-10-01 14:13:00.000", cell.getStringValue().substring(0, 23));
    }

}