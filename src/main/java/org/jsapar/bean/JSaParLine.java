package org.jsapar.bean;

import java.lang.annotation.*;

/**
 * Annotation that maps a java bean to a line type in a schema.
 * @see JSaParCell
 * @see JSaParContainsCells
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface JSaParLine {

    /**
     * @return The type of the line, as described in the schema, that this class should be mapped to.
     */
    String lineType();
}
