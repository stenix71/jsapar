package org.jsapar;

import java.io.Serializable;
import java.util.Iterator;

/**
 * A line is one row of the input buffer. Each line contains a list of cells.
 * Cells can be retreived either by index O(1) or by name O(n). Note that the
 * class is not synchronized internally. If multiple threads access the same
 * instance, external synchronization is required.
 * 
 * @author Jonas Stenberg
 * 
 */
public class Line implements Serializable {

	/**
     * 
     */
	private static final long serialVersionUID = 6026541900371948402L;

	private java.util.ArrayList<Cell> cellsByIndex = null;

	private java.util.HashMap<String, Cell> cellsByName = null;

	/**
	 * Line type.
	 */
	private String lineType;

	/**
	 * Creates an empty line without any cells.
	 */
	public Line() {
		this.cellsByIndex = new java.util.ArrayList<Cell>();
		this.cellsByName = new java.util.HashMap<String, Cell>();
	}

	/**
	 * Creates an empty line without any cells but with an initial capacity.
	 * 
	 * @param nInitialCapacity
	 */
	public Line(int nInitialCapacity) {
		this.cellsByIndex = new java.util.ArrayList<Cell>(nInitialCapacity);
		this.cellsByName = new java.util.HashMap<String, Cell>(nInitialCapacity);

	}

	public Line(String sLineType) {
		this();
		lineType = sLineType;
	}

	/**
	 * For better performance while iterating multiple lines, it is better to
	 * call the {@link #getCellIterator()} method.
	 * 
	 * @return A clone of the internal collection that contains all the cells of
	 *         this documents. Altering the returned collection will not alter
	 *         the original collection of the this Line.
	 * @see #getCellIterator()
	 */
	@SuppressWarnings("unchecked")
	public java.util.List<Cell> getCells() {
		return (java.util.List<Cell>) cellsByIndex.clone();
	}

	/**
	 * Returns an iterator that will iterate all the cells of this line.
	 * 
	 * @return An iterator that will iterate all the cells of this line.
	 */
	public Iterator<Cell> getCellIterator() {
		return cellsByIndex.iterator();
	}

	/**
	 * Adds a cell to the end of the line.
	 * 
	 * @param cell
	 *            The cell to add
	 */
	public void addCell(Cell cell) {
		this.cellsByIndex.add(cell);
		if (cell.getName() != null)
			this.cellsByName.put(cell.getName(), cell);
	}

	/**
	 * Adds a cell at specified index of a line. First cell has index 0.
	 * Existing cells to the rigth of the new cell will have incremented
	 * indexes.
	 * 
	 * @param cell
	 *            The cell to add
	 * @param index
	 *            The index the cell will have in the line.
	 */
	public void addCell(Cell cell, int index) {
		this.cellsByIndex.add(index, cell);
		if (cell.getName() != null)
			this.cellsByName.put(cell.getName(), cell);
	}

	/**
	 * Gets a cell at specified index. First cell has index 0.
	 * 
	 * @param index
	 * @return The cell
	 */
	public Cell getCell(int index) {
		return this.cellsByIndex.get(index);
	}

	/**
	 * Gets a cell with specified name. Name is specified by the schema.
	 * 
	 * @param name
	 * @return The cell
	 */
	public Cell getCell(String name) {
		return this.cellsByName.get(name);
	}

	/**
	 * Gets the number of cells that this line contains.
	 * 
	 * @return the number of cells that this line contains.
	 */
	public int getNumberOfCells() {
		return this.cellsByIndex.size();
	}

	/**
	 * Returns the type of this line. The line type attribute is primarily used
	 * when parsing lines of different types, distinguished by a control cell.
	 * 
	 * @return the lineType
	 */
	public String getLineType() {
		return lineType;
	}

	/**
	 * Sets the type of this line. The line type attribute is primarily used
	 * when parsing lines of different types, distinguished by a control cell.
	 * 
	 * @param lineType
	 *            the lineType to set
	 */
	public void setLineType(String lineType) {
		this.lineType = lineType;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (this.lineType != null && this.lineType.length() > 0) {
			sb.append("Line type=");
			sb.append(this.lineType);
			sb.append(", ");
		}
		sb.append("Cells: ");
		sb.append(this.cellsByIndex);
		return sb.toString();
	}
}