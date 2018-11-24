package org.jsapar.parse.csv;

import org.jsapar.error.JSaParException;
import org.jsapar.parse.csv.CellSplitter;
import org.jsapar.parse.csv.SimpleCellSplitter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertArrayEquals;

public class SimpleCellSplitterTest {

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testSplit() throws IOException, JSaParException {
        CellSplitter s = new SimpleCellSplitter(";");
        assertArrayEquals(new String[]{"A", "B", "", "C"}, s.split("A;B;;C"));
    }

    @Test
    public void testSplit_cellSeparatorThatIsReservedRegexpChars() throws IOException, JSaParException {
        CellSplitter s = new SimpleCellSplitter("[]");
        assertArrayEquals(new String[]{"A", "B", "", "["}, s.split("A[]B[][]["));
    }    
    
    @Test
    public void testSplit_lastCellEmpty() throws IOException, JSaParException {
        CellSplitter s = new SimpleCellSplitter(";");
        assertArrayEquals(new String[]{"A", "B", ""}, s.split("A;B;"));
    }

    @Test
    public void testSplit_firstCellEmpty() throws IOException, JSaParException {
        CellSplitter s = new SimpleCellSplitter(";");
        assertArrayEquals(new String[]{"", "A", "B"}, s.split(";A;B"));
    }
    
}
