package org.jsapar.parse.fixed;

import org.jsapar.parse.ParseConfig;
import org.jsapar.schema.CellValueCondition;
import org.jsapar.schema.FixedWidthSchemaCell;
import org.jsapar.schema.FixedWidthSchemaLine;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by stejon0 on 2016-03-12.
 */
public class FWLineParserMatcher {
    private final FixedWidthSchemaLine schemaLine;
    private List<FWControlCell> controlCells =new ArrayList<>();
    private FixedWidthLineParser lineParser;
    private int occursLeft;
    private int maxControlEndPos;
    private FixedWidthCellParser cellParser = new FixedWidthCellParser();

    public FWLineParserMatcher(FixedWidthSchemaLine schemaLine, ParseConfig config) {
        this.schemaLine = schemaLine;
        this.lineParser = new FixedWidthLineParser(schemaLine, config);
        occursLeft = schemaLine.getOccurs();
        int beginPos=0;
        for (FixedWidthSchemaCell schemaCell : schemaLine.getSchemaCells()) {
            CellValueCondition lineCondition = schemaCell.getLineCondition();
            if(lineCondition != null){
                controlCells.add(new FWControlCell(beginPos, schemaCell));
            }
            beginPos += schemaCell.getLength();
            maxControlEndPos = beginPos;
        }
    }
    
    public LineParserMatcherResult testLineParserIfMatching(BufferedReader reader) throws IOException {
        if(occursLeft <= 0)
            return LineParserMatcherResult.NO_OCCURS;
        if(!controlCells.isEmpty()) {
            // We only peek into the line to follow.
            reader.mark(maxControlEndPos);
            try {
                int read = 0;
                for (FWControlCell controlCell : controlCells) {
                    int offset = controlCell.beginPos - read;
                    String value = cellParser.parseToString(controlCell.schemaCell, reader, offset, schemaLine.isTrimFillCharacters(),
                            schemaLine.getFillCharacter());
                    if (value == null)
                        return LineParserMatcherResult.EOF; // EOF reached
                    if (!controlCell.schemaCell.getLineCondition().satisfies(value))
                        return LineParserMatcherResult.NOT_MATCHING; // Not matching criteria.
                    read = controlCell.beginPos + controlCell.schemaCell.getLength();
                }
            } finally {
                reader.reset();
            }
        }
        if (!schemaLine.isOccursInfinitely())
            occursLeft--;
        return LineParserMatcherResult.SUCCESS;
    }

    public FixedWidthLineParser getLineParser() {
        return lineParser;
    }

    private class FWControlCell{
        final int beginPos;
        final FixedWidthSchemaCell schemaCell;

        public FWControlCell(int beginPos, FixedWidthSchemaCell schemaCell) {
            this.beginPos = beginPos;
            this.schemaCell = schemaCell;
        }
    }

    public boolean isOccursLeft() {
        return schemaLine.isOccursInfinitely() ? true : occursLeft > 0;
    }

}
