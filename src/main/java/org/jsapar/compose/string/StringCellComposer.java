package org.jsapar.compose.string;

import org.jsapar.compose.cell.CellFormat;
import org.jsapar.model.Cell;
import org.jsapar.schema.SchemaCell;

class StringCellComposer {
    private final SchemaCell schemaCell;
    private final CellFormat cellFormat;

    StringCellComposer(SchemaCell schemaCell) {
        this.schemaCell = schemaCell;
        this.cellFormat = CellFormat.ofSchemaCell(schemaCell);
    }

    String getName() {
        return schemaCell.getName();
    }

    Cell makeEmptyCell() {
        return schemaCell.makeEmptyCell();
    }

    String compose(Cell cell)  {
         return cellFormat.format(cell);
    }

}
