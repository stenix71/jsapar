package org.jsapar.model;

import java.time.Period;

public class PeriodCell extends TemporalAmountCell<Period> {
    /**
     * Creates a cell with a name.
     *
     * @param name     The name of the cell
     * @param value    The value to set for this cell.
     */
    public PeriodCell(String name, Period value) {
        super(name, value, CellType.PERIOD);
    }

    /**
     * @param name The name of the cell
     * @return An empty cell of this type.
     */
    public static Cell<Period> emptyOf(String name) {
        return new EmptyCell<>(name, CellType.PERIOD);
    }

    @Override
    public int compareValueTo(Cell<Period> right) {
        return Integer.compare(getValue().getDays(), right.getValue().getDays());
    }

    @Override
    public Cell<Period> cloneWithName(String newName) {
        return new PeriodCell(newName, getValue());
    }
}
