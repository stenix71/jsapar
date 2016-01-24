package org.jsapar;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsapar.compose.BeanFactory;
import org.jsapar.compose.BeanFactoryDefault;
import org.jsapar.model.Cell;
import org.jsapar.model.Document;
import org.jsapar.model.Line;
import org.jsapar.input.CellParseError;

/**
 * Uses Java reflection to convert the Document structure into POJO objects.
 * 
 * @author stejon0
 * 
 */
public class BeanComposer {

    private static final String SET_PREFIX = "set";
    
    private BeanFactory         beanFactory        = new BeanFactoryDefault();
    private Map<String, String> setMethodNameCache = new HashMap<>();

    /**
     * Creates a list of java bean objects. For this method to work, the lineType attribute of each line
     * have to contain the full class name of the class to create for each line. Also the set method
     * for each attribute have to match exactly to the name of each cell.
     *
     * You can customize the mapping between line names and the corresponding bean by assigning a custom BeanFactory
     * implementation.
     * 
     * @param document       The document to convert to beans.
     * @param parseErrors    A list which will be populated with errors during this method call.
     * @return A list of Java bean objects.
     */
    @SuppressWarnings("rawtypes")
    public java.util.List createBeans(Document document, List<CellParseError> parseErrors) {
        java.util.List<Object> objects = new ArrayList<>(document.getNumberOfLines());
        java.util.Iterator<Line> lineIter = document.getLineIterator();
        while (lineIter.hasNext()) {
            Line line = lineIter.next();
            try {
                Object o = this.createBean(line, parseErrors);
                objects.add(o);
            } catch (InstantiationException e) {
                parseErrors.add(new CellParseError("", "", null,
                        "Failed to instantiate object. Skipped creating object - " + e));
            } catch (IllegalAccessException e) {
                parseErrors.add(new CellParseError("", "", null,
                        "Failed to call set method. Skipped creating object - " + e));
            } catch (ClassNotFoundException e) {
                parseErrors.add(new CellParseError("", "", null, "Class not found. Skipped creating object - " + e));
            } catch (Throwable e) {
                parseErrors.add(new CellParseError(0, "", "", null, "Skipped creating object - " + e.getMessage(), e));
            }
        }
        return objects;
    }

    /**
     * @param line
     * @return An object of the class, denoted by the lineType of the line, with attributes set by
     *         the supplied line.
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws ClassNotFoundException
     */
    public Object createBean(Line line, List<CellParseError> parseErrors) throws InstantiationException,
            IllegalAccessException, ClassNotFoundException {
        Object o = beanFactory.createBean(line);
        return assign(line, o, parseErrors);
    }

    /**
     * Assigns the cells of a line as attributes to an object.
     * 
     * @param <T>
     *            The type of the object to assign
     * @param line
     *            The line to get parameters from.
     * @param objectToAssign
     *            The object to assign cell attributes to. The object will be modified.
     * @return The object that was assigned. The same object that was supplied as parameter.
     */
    public <T> T assign(Line line, T objectToAssign, List<CellParseError> parseErrors) {

        java.util.Iterator<Cell> cellIter = line.getCellIterator();
        while (cellIter.hasNext()) {
            Cell cell = cellIter.next();
            String sName = cell.getName();
            if (sName == null || sName.isEmpty())
                continue;

            assignCellToField(cell, sName, objectToAssign, parseErrors);
        }
        return objectToAssign;
    }

    /**
     * Assign supplied cell value to supplied object.
     * 
     * @param cell
     * @param sName
     * @param objectToAssign
     * @param parseErrors
     */
    private void assignCellToField(Cell cell, String sName, Object objectToAssign, List<CellParseError> parseErrors) {
        try {
            String[] nameLevels = sName.split("\\.");
            Object currentObject = objectToAssign;
            for (int i = 0; i + 1 < nameLevels.length; i++) {
                try {
                    // Continue looping to next object.
                    currentObject = beanFactory.findOrCreateChildBean(currentObject, nameLevels[i]);
                } catch (InstantiationException e) {
                    parseErrors.add(new CellParseError(cell.getName(), cell.getStringValue(), null,
                            "Skipped assigning cell - Failed to execute default constructor for class accessed by "
                                    + nameLevels[i] + " - " + e));
                    return;
                }
            }
            sName = nameLevels[nameLevels.length - 1];
            assignAttribute(cell, sName, currentObject, parseErrors);
        } catch (InvocationTargetException | IllegalArgumentException e) {
            parseErrors.add(new CellParseError(cell.getName(), cell.getStringValue(), null,
                    "Skipped assigning cell - Failed to execute getter or setter method in class "
                            + objectToAssign.getClass().getName() + " - " + e));
        } catch (IllegalAccessException e) {
            parseErrors.add(new CellParseError(cell.getName(), cell.getStringValue(), null,
                    "Skipped assigning cell - Failed to access getter or setter method in class "
                            + objectToAssign.getClass().getName() + " - " + e));
        } catch (NoSuchMethodException e) {
            parseErrors.add(new CellParseError(cell.getName(), cell.getStringValue(), null,
                    "Skipped assigning cell - Missing getter or setter method in class "
                            + objectToAssign.getClass().getName() + " or sub class - " + e));
        }
    }

    /**
     * @param cell
     * @param sName
     * @param objectToAssign
     * @param parseErrors
     */
    private void assignAttribute(Cell cell, String sName, Object objectToAssign, List<CellParseError> parseErrors) {
        if(cell.isEmpty() )
            return;

        String sSetMethodName = createSetMethodName(sName);
        try {
            boolean success = assignParameterBySignature(objectToAssign, sSetMethodName, cell);
            if (!success) // Try again but use the name and try to cast.
                assignParameterByName(objectToAssign, sSetMethodName, cell, parseErrors);
        } catch (IllegalArgumentException e) {
            parseErrors.add(new CellParseError(cell.getName(), cell.getStringValue(), null,
                    "Skipped assigning cell - The method " + sSetMethodName + "() in class "
                            + objectToAssign.getClass().getName() + " does not accept correct type - " + e));
        } catch (IllegalAccessException e) {
            parseErrors.add(new CellParseError(cell.getName(), cell.getStringValue(), null,
                    "Skipped assigning cell - The method " + sSetMethodName + "() in class "
                            + objectToAssign.getClass().getName() + " does not have correct access - " + e));
        } catch (InvocationTargetException e) {
            parseErrors.add(new CellParseError(cell.getName(), cell.getStringValue(), null,
                    "Skipped assigning cell - The method " + sSetMethodName + "() in class "
                            + objectToAssign.getClass().getName() + " failed to execute - " + e));
        }
    }

    /**
     * @param sAttributeName
     * @return The set method that corresponds to this attribute.
     */
    private String createSetMethodName(String sAttributeName) {
        String methodName = this.setMethodNameCache.get(sAttributeName);
        if(methodName == null) {
            methodName = createBeanMethodName(SET_PREFIX, sAttributeName);
            this.setMethodNameCache.put(sAttributeName, methodName);
        }
        return methodName;

    }



    /**
     * @param prefix
     * @param sAttributeName
     * @return The setter or setter method that corresponds to this attribute.
     */
    private String createBeanMethodName(String prefix, String sAttributeName) {
        return prefix + sAttributeName.substring(0, 1).toUpperCase() + sAttributeName.substring(1);
    }

    /**
     * Assigns the cells of a line as attributes to an object.
     * 
     * @param <T>
     *            The type of the object to assign
     * @param cell
     *            The cell to get the parameter from.
     * @param objectToAssign
     *            The object to assign cell attributes to. The object will be modified.
     * @return True if the parameter was assigned to the object, false otherwise.
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     */
    private <T> boolean assignParameterBySignature(T objectToAssign, String sSetMethodName, Cell cell)
            throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {

        if(cell.getValue() == null)
            return false;
        try {
            Class<?> type = cell.getValue().getClass();
            Method f = objectToAssign.getClass().getMethod(sSetMethodName, type);
            f.invoke(objectToAssign, cell.getValue());
            return true;
        } catch (NoSuchMethodException e) {
            // We don't care here since we will try again if this method fails.
        }
        return false;
    }

    /**
     * Assigns the cells of a line as attributes to an object.
     * 
     * @param <T>
     *            The type of the object to assign
     * @param cell
     *            The cell to get the parameter from.
     * @param objectToAssign
     *            The object to assign cell attributes to. The object will be modified.
     * @return True if the parameter was assigned to the object, false otherwise.
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     */
    private <T> boolean assignParameterByName(T objectToAssign,
                                              String sSetMethodName,
                                              Cell cell,
                                              List<CellParseError> parseErrors) throws IllegalArgumentException,
            IllegalAccessException, InvocationTargetException {

        try {
            Method[] methods = objectToAssign.getClass().getMethods();
            for (Method f : methods) {
                Class<?>[] paramTypes = f.getParameterTypes();
                if (paramTypes.length != 1 || !f.getName().equals(sSetMethodName))
                    continue;

                Object value = cell.getValue();
                // Casts between simple types does not work automatically
                if (paramTypes[0] == Integer.TYPE && value instanceof Number)
                    f.invoke(objectToAssign, ((Number) value).intValue());
                else if (paramTypes[0] == Short.TYPE && value instanceof Number)
                    f.invoke(objectToAssign, ((Number) value).shortValue());
                else if (paramTypes[0] == Byte.TYPE && value instanceof Number)
                    f.invoke(objectToAssign, ((Number) value).byteValue());
                else if (paramTypes[0] == Float.TYPE && value instanceof Number)
                    f.invoke(objectToAssign, ((Number) value).floatValue());
                // Will squeeze in first character of any datatype's string representation.
                else if (paramTypes[0] == Character.TYPE) {
                    if (value instanceof Character) {
                        f.invoke(objectToAssign, (Character) value);
                    } else {
                        String sValue = value.toString();
                        if (!sValue.isEmpty())
                            f.invoke(objectToAssign, sValue.charAt(0));
                    }
                }
                else if(Enum.class.isAssignableFrom( paramTypes[0]) && value instanceof String){
                    f.invoke(objectToAssign, Enum.valueOf((Class<Enum>)paramTypes[0], String.valueOf(value)));
                }
                else {
                    try {
                        f.invoke(objectToAssign, value);
                    } catch (IllegalArgumentException e) {
                        // There may be more methods that fits the name.
                        continue;
                    }
                }
                return true;
            }
            parseErrors.add(new CellParseError(cell.getName(), cell.getStringValue(), null,
                    "Skipped assigning cell - No method called " + sSetMethodName + "() found in class "
                            + objectToAssign.getClass().getName() + " that fits the cell " + cell));
        } catch (SecurityException e) {
            parseErrors.add(new CellParseError(cell.getName(), cell.getStringValue(), null,
                    "Skipped assigning cell - The method " + sSetMethodName + "() in class "
                            + objectToAssign.getClass().getName() + " does not have public access - " + e));
        }
        return false;
    }

    public BeanFactory getBeanFactory() {
        return beanFactory;
    }

    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

}