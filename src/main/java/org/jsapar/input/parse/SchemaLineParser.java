package org.jsapar.input.parse;

import java.io.IOException;

import org.jsapar.JSaParException;
import org.jsapar.model.Line;
import org.jsapar.input.LineParsedEvent;
import org.jsapar.input.LineEventListener;
import org.jsapar.schema.SchemaLine;

public abstract class SchemaLineParser {

    /**
     * @param nLineNumber
     * @param listener
     * @return
     * @throws IOException 
     * @throws JSaParException 
     */
    public abstract boolean parse(long nLineNumber, LineEventListener listener) throws JSaParException, IOException;

    /**
     * Handles behavior of empty lines
     * 
     * @param lineNumber
     * @param listener
     * @return Returns true (always).
     * @throws JSaParException
     */
    protected boolean handleEmptyLine(SchemaLine schemaLine, long lineNumber, LineEventListener listener) throws JSaParException {
        if (!schemaLine.isIgnoreReadEmptyLines()) {
            listener.lineParsedEvent(new LineParsedEvent(this, new Line(schemaLine.getLineType()), lineNumber));
        }
        return true;
    }
    
}