package org.jsapar;

import org.jsapar.compose.ComposeException;
import org.jsapar.compose.bean.BeanComposer;
import org.jsapar.compose.bean.BeanComposerConfig;
import org.jsapar.compose.bean.BeanFactory;
import org.jsapar.compose.bean.RecordingBeanEventListener;
import org.jsapar.error.RecordingErrorEventListener;
import org.jsapar.model.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import static org.junit.Assert.*;

public class BeanComposerTest {

    private java.util.Date birthTime;

    @Before
    public void setUp() throws java.text.ParseException {
	java.text.DateFormat dateFormat=new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	this.birthTime = dateFormat.parse("1971-03-25 23:04:24");

	
    }

    @After
    public void tearDown() throws Exception {
    }

    @SuppressWarnings("unchecked")
    @Test
    public final void testCreateJavaObjects() throws IOException {
        Document document = new Document();
        Line line1 = new Line("org.jsapar.TstPerson");
        line1.addCell(new StringCell("firstName", "Jonas"));
        line1.addCell(new StringCell("lastName", "Stenberg"));
        line1.addCell(new IntegerCell("shoeSize", 42));
        line1.addCell(new DateCell("birthTime", this.birthTime ));
        line1.addCell(new IntegerCell("luckyNumber", 123456787901234567L));
        line1.addCell(new CharacterCell("door", 'A'));
        line1.addCell(new FloatCell("length", 123.45D));
        line1.addCell(new StringCell("gender", "M"));
//      line1.addCell(new StringCell("NeverUsed", "Should not be assigned"));
        

        Line line2 = new Line("org.jsapar.TstPerson");
        line2.addCell(new StringCell("FirstName", "Frida"));
        line2.addCell(new StringCell("LastName", "Bergsten"));
        line2.addCell(new EmptyCell("length", CellType.FLOAT));

        document.addLine(line1);
        document.addLine(line2);

        BeanComposer<TstPerson> composer = new BeanComposer<>();
        RecordingBeanEventListener<TstPerson> beanEventListener = new RecordingBeanEventListener<>();
        composer.addComposedEventListener(beanEventListener);
        composer.compose(document);
        java.util.List<TstPerson> objects = beanEventListener.getBeans();
        assertEquals(2, objects.size());
        TstPerson firstPerson = objects.get(0);
        assertEquals("Jonas", firstPerson.getFirstName());
        assertEquals(42, firstPerson.getShoeSize());
        assertEquals(this.birthTime,  firstPerson.getBirthTime());
        assertEquals(TstGender.M, firstPerson.getGender());
        assertEquals(123456787901234567L, firstPerson.getLuckyNumber());
        assertEquals('A', firstPerson.getDoor());
        assertEquals(123.45D, firstPerson.getLength(), 0.01);
        TstPerson secondPerson = objects.get(1);
        assertEquals("Bergsten", secondPerson.getLastName());
        assertEquals(0.0D, secondPerson.getLength(), 0.01);
    }


    @SuppressWarnings("unchecked")
    @Test
    public final void testCreateJavaObjects_Long_to_int() throws IOException {
        Document document = new Document();
        Line line1 = new Line("org.jsapar.TstPerson");
        line1.addCell(new IntegerCell("shoeSize", 42L));
        

        document.addLine(line1);

        BeanComposer<Object> composer = new BeanComposer<>();
        RecordingBeanEventListener<Object> beanEventListener = new RecordingBeanEventListener<>();
        composer.addComposedEventListener(beanEventListener);
        composer.compose(document);
        java.util.List<Object> objects = beanEventListener.getBeans();
        assertEquals(1, objects.size());
        assertEquals(42, ((TstPerson)objects.get(0)).getShoeSize());
    }

    @SuppressWarnings("unchecked")
    @Test
    public final void testCreateJavaObjects_Int_to_long() throws  IOException {
        Document document = new Document();
        Line line1 = new Line("org.jsapar.TstPerson");
        line1.addCell(new IntegerCell("luckyNumber", 1234));
        

        document.addLine(line1);

        BeanComposer composer = new BeanComposer();
        RecordingBeanEventListener<TstPerson> beanEventListener = new RecordingBeanEventListener<>();
        composer.addComposedEventListener(beanEventListener);
        composer.compose(document);
        java.util.List<TstPerson> objects = beanEventListener.getBeans();
        assertEquals(1, objects.size());
        assertEquals(1234, (objects.get(0)).getLuckyNumber());
    }

    @Test(expected = ComposeException.class)
    public final void testCreateJavaObjects_wrongType() throws IOException {
        Document document = new Document();
        Line line1 = new Line("org.jsapar.TstPerson");
        line1.addCell(new IntegerCell("firstName", 1234));
        

        document.addLine(line1);

        BeanComposer<TstPerson> composer = new BeanComposer<>();
        RecordingBeanEventListener<TstPerson> beanEventListener = new RecordingBeanEventListener<>();
        composer.addComposedEventListener(beanEventListener);
        composer.compose(document);
        beanEventListener.getBeans();
    }

    /**
     *
     */
    @SuppressWarnings("unchecked")
    @Test
    public final void testCreateJavaObjects_subclass() throws IOException {
        Document document = new Document();
        Line line1 = new Line("org.jsapar.TstPerson");
        line1.addCell(new StringCell("address.street", "Stigen"));
        line1.addCell(new StringCell("address.town", "Staden"));
        line1.addCell(new StringCell("address.subAddress.town", "By"));


        document.addLine(line1);

        BeanComposer<TstPerson> composer = new BeanComposer<>();
        RecordingBeanEventListener<TstPerson> beanEventListener = new RecordingBeanEventListener<>();
        composer.addComposedEventListener(beanEventListener);
        composer.compose(document);
        java.util.List<TstPerson> objects = beanEventListener.getBeans();
        TstPerson parsedPerson = objects.get(0);
        assertNotNull(parsedPerson.getAddress());
        assertEquals("Stigen", parsedPerson.getAddress().getStreet());
        assertEquals("Staden", parsedPerson.getAddress().getTown());
        assertEquals("By", parsedPerson.getAddress().getSubAddress().getTown());
    }


    /**
     *
     */
    @SuppressWarnings("unchecked")
    @Test
    public final void testCreateJavaObjects_subclass_BeanFactory() throws IOException {
        Document document = new Document();
        Line line1 = new Line("Test person");
        line1.addCell(new StringCell("a.street", "Stigen"));
        line1.addCell(new StringCell("a.town", "Staden"));
        line1.addCell(new StringCell("a.aa.town", "By"));
        document.addLine(line1);

        BeanComposer<TstPerson> composer = new BeanComposer<>(new BeanFactory<TstPerson>() {
            @Override
            public TstPerson createBean(Line line)
                    throws ClassNotFoundException, InstantiationException, IllegalAccessException {
                if(line.getLineType().equals("Test person"))
                    return new TstPerson();
                else
                    return null;
            }

            @Override
            public Object findOrCreateChildBean(Object parentBean, String childBeanName)
                    throws InstantiationException, IllegalAccessException, NoSuchMethodException, SecurityException,
                    IllegalArgumentException, InvocationTargetException {
                if(childBeanName.equals("a") && parentBean instanceof TstPerson){
                    TstPerson p = (TstPerson)parentBean;
                    if(p.getAddress() != null)
                        return p.getAddress();
                    TstPostAddress child = new TstPostAddress();
                    p.setAddress(child);
                    return child;
                }
                else if(childBeanName.equals("aa") && parentBean instanceof TstPostAddress){
                    TstPostAddress a = (TstPostAddress)parentBean;
                    if(a.getSubAddress() != null)
                        return a.getSubAddress();
                    TstPostAddress child = new TstPostAddress();
                    a.setSubAddress(child);
                    return child;
                }

                return null;
            }
        });
        RecordingBeanEventListener<TstPerson> beanEventListener = new RecordingBeanEventListener<>();
        composer.addComposedEventListener(beanEventListener);
        composer.compose(document);
        java.util.List<TstPerson> objects = beanEventListener.getBeans();
        TstPerson parsedPerson = objects.get(0);
        assertNotNull(parsedPerson.getAddress());
        assertEquals("Stigen", parsedPerson.getAddress().getStreet());
        assertEquals("Staden", parsedPerson.getAddress().getTown());
        assertEquals("By", parsedPerson.getAddress().getSubAddress().getTown());
    }
    /**
     *
     */
    @SuppressWarnings("unchecked")
    @Test
    public final void testCreateJavaObjects_subclass_error() throws IOException {
        Document document = new Document();
        Line line1 = new Line("org.jsapar.TstPerson");
        line1.addCell(new StringCell("address.doNotExist", "Stigen"));
        line1.addCell(new StringCell("address.town", "Staden"));
        line1.addCell(new StringCell("address.subAddress.town", "By"));
        

        document.addLine(line1);

        BeanComposer composer = new BeanComposer();
        RecordingBeanEventListener<TstPerson> beanEventListener = new RecordingBeanEventListener<>();
        RecordingErrorEventListener errorEventListener = new RecordingErrorEventListener();
        composer.addComposedEventListener(beanEventListener);
        composer.setErrorEventListener(errorEventListener);
        composer.compose(document);
        java.util.List<TstPerson> objects = beanEventListener.getBeans();
        assertEquals(1, errorEventListener.getErrors().size());
        assertEquals(1, objects.size());
        assertNotNull((objects.get(0)).getAddress());
        assertEquals("Staden", (objects.get(0)).getAddress().getTown());
        assertEquals("By", (objects.get(0)).getAddress().getSubAddress().getTown());
        System.out.println("The (expected) error: " + errorEventListener.getErrors());
    }
    
    /**
     *
     */
    @SuppressWarnings("unchecked")
    @Test
    public final void testCreateJavaObjects_null_value() throws IOException {
        Document document = new Document();
        Line line1 = new Line("org.jsapar.TstPerson");
        line1.addCell(new StringCell("firstName", "Jonas"));
        line1.addCell(new StringCell("lastName", null));
        line1.addCell(new IntegerCell("shoeSize", 42));
       
        document.addLine(line1);

        BeanComposer composer = new BeanComposer();
        RecordingBeanEventListener<TstPerson> beanEventListener = new RecordingBeanEventListener<>();
        composer.addComposedEventListener(beanEventListener);
        composer.compose(document);
        java.util.List<TstPerson> objects = beanEventListener.getBeans();
        assertEquals(1, objects.size());
        assertEquals("Jonas", objects.get(0).getFirstName());
        assertNull(objects.get(0).getLastName());
        assertEquals(42, objects.get(0).getShoeSize());
    }    

    @Test
    public void testGetSetBeanFactory(){
        BeanFactory<Object> testBeanFactory = new BeanFactoryMock();
        BeanComposer<Object> c = new BeanComposer<>();
        assertNotSame(testBeanFactory, c.getBeanFactory());
        c.setBeanFactory(testBeanFactory);
        assertSame(testBeanFactory, c.getBeanFactory());
    }

    private class BeanFactoryMock implements BeanFactory<Object> {
        @Override
        public Object createBean(Line line)
                throws ClassNotFoundException, InstantiationException, IllegalAccessException, ClassCastException {
            return null;
        }

        @Override
        public Object findOrCreateChildBean(Object parentBean, String childBeanName)
                throws InstantiationException, IllegalAccessException, NoSuchMethodException, SecurityException,
                IllegalArgumentException, InvocationTargetException {
            return null;
        }
    }

    @Test
    public void testGetSetConfig(){
        BeanComposerConfig testConfig = new BeanComposerConfig();
        BeanComposer c = new BeanComposer();
        assertNotSame(testConfig, c.getConfig());
        c.setConfig(testConfig);
        assertSame(testConfig, c.getConfig());
    }

}
