package org.jsapar.schema;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.Format;
import java.text.NumberFormat;
import java.util.Locale;

import org.jsapar.Cell.CellType;

public class SchemaCellFormat implements Cloneable {
    private CellType cellType = CellType.STRING;
    private java.text.Format format = null;
    private String pattern = "";

    public SchemaCellFormat() {

    }

    public SchemaCellFormat(CellType cellType) {
        this.cellType = cellType;
    }

    public SchemaCellFormat(CellType cellType, Format format) {
        this.cellType = cellType;
        this.format = format;
    }

    /**
     * @param cellType
     * @param sPattern
     * @throws SchemaException
     */
    public SchemaCellFormat(CellType cellType, String sPattern) throws SchemaException {
        this(cellType, sPattern, null);
    }

    /**
     * @param cellType
     * @param sPattern
     * @param locale
     * @throws SchemaException
     */
    public SchemaCellFormat(CellType cellType, String sPattern, Locale locale) throws SchemaException {
        setFormat(cellType, sPattern, locale);
    }

    /**
     * Sets the format according to cell type, pattern and locale.
     * 
     * @param cellType
     * @param sPattern
     * @param locale
     * @throws SchemaException
     */
    public void setFormat(CellType cellType, String sPattern, Locale locale) throws SchemaException {
        this.cellType = cellType;
        this.pattern = sPattern;
        if (sPattern == null) {
            this.format = null;
            return;
        }
        switch (cellType) {
        case STRING:
            if (sPattern != null)
                this.format = new RegExpFormat(sPattern);
            else
                this.format = null;
            break;
        case DATE:
            this.format = new java.text.SimpleDateFormat(sPattern);
            break;
        case INTEGER:
            if (locale == null)
                locale = Locale.getDefault();
            if (sPattern != null && sPattern.length() > 0)
                this.format = new java.text.DecimalFormat(sPattern, new DecimalFormatSymbols(locale));
            else
                this.format = NumberFormat.getIntegerInstance(locale);
            break;
        case FLOAT:
            if (locale == null)
                locale = Locale.getDefault();
            if (sPattern != null && sPattern.length() > 0)
                this.format = new java.text.DecimalFormat(sPattern, new DecimalFormatSymbols(locale));
            else
                this.format = NumberFormat.getInstance(locale);
            break;
        case DECIMAL:
            if (locale == null)
                locale = Locale.getDefault();
            DecimalFormat decFormat = new java.text.DecimalFormat(sPattern, new DecimalFormatSymbols(locale));
            decFormat.setParseBigDecimal(true);
            this.format = decFormat;
            break;
        case CUSTOM:
            throw new SchemaException("CUSTOM cell type formatter can not be created without specifying a formatter.");
        case BOOLEAN:
            // TODO
            throw new SchemaException("Not yet implemented celltype: " + cellType);
        default:
            throw new SchemaException("Unknown cellType supplied: " + cellType);

        }
    }

    /**
     * @return the cellType
     */
    public CellType getCellType() {
        return cellType;
    }

    /**
     * @param cellType
     *            the cellType to set
     */
    public void setCellType(CellType cellType) {
        this.cellType = cellType;
    }

    /**
     * @return the format
     */
    public java.text.Format getFormat() {
        return format;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("CellType=");
        sb.append(this.cellType);
        if (this.format != null) {
            sb.append(", Format={");
            sb.append(this.format);
            sb.append("}");
        }
        return sb.toString();
    }

    /**
     * @return the pattern
     */
    public String getPattern() {
        return pattern;
    }

}