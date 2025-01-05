package org.jsapar.model;

import java.time.temporal.TemporalAmount;

/**
 */
abstract class TemporalAmountCell<T extends TemporalAmount> extends AbstractCell<T> {

    /**
     * Creates a cell with a name.
     *
     * @param name     The name of the cell
     * @param value    The value of the cell.
     * @param cellType The type of the cell.
     */
    TemporalAmountCell(String name, T value, CellType cellType) {
        super(name, value, cellType);
    }
}
