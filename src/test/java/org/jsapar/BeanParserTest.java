/**
 * 
 */
package org.jsapar;

import org.jsapar.error.ExceptionErrorEventListener;
import org.jsapar.error.JSaParException;
import org.jsapar.model.DateCell;
import org.jsapar.model.Document;
import org.jsapar.model.IntegerCell;
import org.jsapar.model.Line;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author stejon0
 * 
 */
public class BeanParserTest {
    static final Date birthTime = new Date();

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testBuild() throws JSaParException, IOException {
        List<TstPerson> people = new ArrayList<>(2);
        TstPerson person = new TstPerson();
        person.setFirstName("Jonas");
        people.add(person);

        person = new TstPerson();
        person.setFirstName("Test2");
        people.add(person);

        BeanParser<TstPerson> parser = new BeanParser<>(people);
        DocumentBuilder builder = new DocumentBuilder(parser);
        Document doc = builder.build();

        assertEquals(2, doc.size());
        Line line = doc.getLine(0);
        assertEquals("Jonas", line.getCell("firstName").getStringValue());

        line = doc.getLine(1);
        assertEquals("Test2", line.getCell("firstName").getStringValue());

    }

    @Test
    public void testBuildLine() throws JSaParException {
        TstPerson person = new TstPerson();
        person.setBirthTime(birthTime);
        person.setFirstName("Jonas");
        person.setLastName("Bergsten");
        person.setLuckyNumber(123456787901234567L);
        person.setShoeSize((short)42);
        person.setStreetNumber(4711);
        person.setDoor('A');


        BeanParser<TstPerson> builder = new BeanParser<>(Arrays.asList(new TstPerson[]{person}));
        Line line = builder.parseBean(person, new ExceptionErrorEventListener());
        assertEquals("org.jsapar.TstPerson", line.getLineType());
        assertEquals(8, line.size());
        assertEquals("Jonas", line.getCell("firstName").getStringValue());
        assertEquals(42, ((IntegerCell) line.getCell("shoeSize")).getNumberValue().shortValue());
        assertEquals(4711, ((IntegerCell) line.getCell("streetNumber")).getNumberValue().intValue());
        assertEquals(birthTime, ((DateCell) line.getCell("birthTime")).getDateValue());
        assertEquals(123456787901234567L, ((IntegerCell) line.getCell("luckyNumber")).getNumberValue().longValue());
        assertEquals("A", line.getStringCellValue("door"));
    }

    @Test
    public void testBuildLine_subClass() throws JSaParException {
        TstPerson person = new TstPerson();
        person.setFirstName("Jonas");
        person.setAddress(new TstPostAddress("Stigen", "Staden"));
        BeanParser<TstPerson> builder = new BeanParser<>(Arrays.asList(new TstPerson[]{person}));
        Line line = builder.parseBean(person, new ExceptionErrorEventListener());
        assertEquals("org.jsapar.TstPerson", line.getLineType());
        assertEquals("Stigen", line.getStringCellValue("address.street"));
        assertEquals("Staden", line.getStringCellValue("address.town"));
        
        // Make sure that loops are avoided.
        Assert.assertNull(line.getStringCellValue("address.owner.firstName"));
    }

    @Test
    public void testBuildLine_subClass_multiplePaths() throws JSaParException {
        TstPerson person = new TstPerson();
        person.setAddress(new TstPostAddress("Stigen", "Staden"));
        person.getAddress().setSubAddress(new TstPostAddress("Road", "Town"));
        person.setWorkAddress(new TstPostAddress("Gatan", "Byn"));
        BeanParser<TstPerson> builder = new BeanParser<>(Arrays.asList(new TstPerson[]{person}));
        Line line = builder.parseBean(person, new ExceptionErrorEventListener());
        assertEquals("org.jsapar.TstPerson", line.getLineType());
        assertEquals("Stigen", line.getStringCellValue("address.street"));
        assertEquals("Staden", line.getStringCellValue("address.town"));
        assertEquals("Road", line.getStringCellValue("address.subAddress.street"));
        assertEquals("Town", line.getStringCellValue("address.subAddress.town"));
        assertEquals("Gatan", line.getStringCellValue("workAddress.street"));
        assertEquals("Byn", line.getStringCellValue("workAddress.town"));
    }

    @Test
    public void testBuildLine_subClass_maxOneLevel() throws JSaParException {
        TstPerson person = new TstPerson();
        person.setAddress(new TstPostAddress("Stigen", "Staden"));
        person.getAddress().setSubAddress(new TstPostAddress("Road", "Town"));
        person.setWorkAddress(new TstPostAddress("Gatan", "Byn"));
        BeanParser<TstPerson> builder = new BeanParser<>(Arrays.asList(new TstPerson[]{person}));
        builder.setMaxSubLevels(1);
        assertEquals(1, builder.getMaxSubLevels());
        Line line = builder.parseBean(person, new ExceptionErrorEventListener());
        assertEquals("org.jsapar.TstPerson", line.getLineType());
        assertEquals("Stigen", line.getStringCellValue("address.street"));
        assertEquals("Staden", line.getStringCellValue("address.town"));
        Assert.assertNull(line.getStringCellValue("address.subAddress.street"));
        Assert.assertNull(line.getStringCellValue("address.subAddress.town"));
        assertEquals("Gatan", line.getStringCellValue("workAddress.street"));
        assertEquals("Byn", line.getStringCellValue("workAddress.town"));
    }
    
}
