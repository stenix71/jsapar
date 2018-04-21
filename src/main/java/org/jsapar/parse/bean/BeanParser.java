package org.jsapar.parse.bean;

import org.jsapar.BeanCollection2TextConverter;
import org.jsapar.error.ErrorEvent;
import org.jsapar.error.ErrorEventListener;
import org.jsapar.model.Cell;
import org.jsapar.model.CellType;
import org.jsapar.model.Line;
import org.jsapar.parse.CellParseException;
import org.jsapar.parse.LineParsedEvent;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

/**
 * Builds {@link LineParsedEvent} from single bean instances. The {@link Line#lineType} of each line will be
 * the name of the class denoted by {@link Class#getName()}. Each bean property that have a getter method will result in
 * a cell with the bean property name The {@link Cell#name} of each cell will be the name of the bean property, e.g. if
 * the bean has a method declared as {@code public int getNumber()}, it will result in a cell with the name "number" of
 * type {@link CellType}.INTEGER.
 * <p>
 * If you use these rules you can write a {@link org.jsapar.schema.Schema} that converts a bean to a different type of output.
 *
 * @see BeanCollection2TextConverter
 */
public class BeanParser<T>  {

    private final BeanMap beanMap;


    public BeanParser(BeanMap beanMap) {
        this.beanMap = beanMap;
    }


    /**
     * Builds a line bean according to the getter fields of the bean. Each cell in the line will
     * be named according to the property name defined in the current {@link BeanMap}. If not stated otherwise this will
     * be the java bean attribute name. This means that if there is a member
     * method called <tt>getStreetAddress()</tt>, the name of the cell will be
     * <tt>streetAddress</tt>. Only properties defined in the current {@link BeanMap} will be added to the created line.
     *
     * @param bean       The bean.
     * @param lineNumber The number of the line being parsed. Numbering starts from 1.
     * @return A Line bean containing cells according to the getter method of the supplied bean.
     */
    public Optional<Line> parseBean(T bean, ErrorEventListener errorListener, long lineNumber) {
        return beanMap.getBeanPropertyMap(bean.getClass()).map(beanPropertyMap -> {
            Line line = new Line(beanPropertyMap.getLineType());
            line.setLineNumber(lineNumber);
            this.parseBean(line, bean, beanPropertyMap, errorListener);
            return line;
        });
    }


    @SuppressWarnings("unchecked")
    private void parseBean(Line line, Object object, BeanPropertyMap beanPropertyMap, ErrorEventListener errorListener) {

        for (Bean2Cell bean2Cell : beanPropertyMap.getBean2Cells()) {
            PropertyDescriptor pd = bean2Cell.getPropertyDescriptor();

            try {
                BeanPropertyMap children = bean2Cell.getChildren();
                if (children != null) {
                    Object subObject = pd.getReadMethod().invoke(object);
                    if (subObject == null)
                        continue;
                    // Recursively add sub classes.
                    this.parseBean(line, subObject, children, errorListener);
                } else
                    line.addCell(bean2Cell.makeCell(object));
            } catch (IllegalArgumentException e) {
                handleCellError(errorListener, bean2Cell.getCellName(), object, line, "Illegal argument in getter method.");
            } catch (IllegalAccessException e) {
                handleCellError(errorListener, bean2Cell.getCellName(), object, line, "Attribute getter does not have public access.");
            } catch (InvocationTargetException e) {
                handleCellError(errorListener, bean2Cell.getCellName(), object, line, "Getter method fails to execute.");
            }
        }
    }

    private void handleCellError(ErrorEventListener errorListener,
                                 String sAttributeName,
                                 Object object,
                                 Line line,
                                 String message) {
        CellParseException error = new CellParseException(sAttributeName, "", null,
                "Unable to build cell for attribute " + sAttributeName + " of class " + object.getClass().getName()
                        + " - " + message);
        line.addCellError(error);
        errorListener.errorEvent(new ErrorEvent(this, error));
    }

 }