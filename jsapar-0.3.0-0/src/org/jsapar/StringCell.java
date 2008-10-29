package org.jsapar;

import java.text.Format;
import java.text.ParseException;
import java.util.Locale;

/**
 * Class containging the cell stringValue as a string representation. Each line
 * contains a list of cells.
 * 
 * @author Jonas
 * 
 */
public class StringCell extends Cell implements Comparable<StringCell> {

    /**
     * 
     */
    private static final long serialVersionUID = -2776042954053921679L;

    /**
     * The string representation of the stringValue of this cell.
     */
    private String stringValue;

    public StringCell() {
	super(CellType.STRING);

    }

    public StringCell(String sValue) {
	super(CellType.STRING);
	this.stringValue = sValue;
    }

    public StringCell(String sName, String sValue) {
	super(sName, CellType.STRING);
	this.stringValue = sValue;
    }

    public StringCell(String name, String value, Format format) throws ParseException {
    	super(name, CellType.STRING);
    	setValue(value, format);
	}

	/**
     * @return the stringValue
     */
    @Override
    public Object getValue() {
	return this.stringValue;
    }

    /**
     * @param stringValue
     *                the stringValue to set
     */
    public void setStringValue(String value) {
	this.stringValue = value;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jsapar.Cell#getStringValue()
     */
    @Override
    public String getStringValue() {
	return this.stringValue;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jsapar.Cell#getStringValue(java.text.Format)
     */
    @Override
    public String getStringValue(Format format) throws IllegalArgumentException {
	if (format != null)
	    return format.format(this.stringValue);
	else
	    return this.stringValue;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jsapar.Cell#setValue(java.lang.String, java.text.Format)
     */
    @Override
    public void setValue(String value, Format format) throws ParseException {
	if (format != null)
	    this.stringValue = (String) format.parseObject(value);
	else
	    this.stringValue = value;

    }

    @Override
    public int compareTo(StringCell right){
    	return this.getStringValue().compareTo(right.getStringValue());
    }

	@Override
	public void setValue(String value, Locale locale) throws ParseException {
	    this.stringValue = value;
	}
}