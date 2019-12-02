package org.jsapar.compose.string;

import org.jsapar.compose.Composer;
import org.jsapar.error.ErrorEventListener;
import org.jsapar.model.Line;
import org.jsapar.schema.Schema;
import org.jsapar.schema.SchemaLine;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Composer that creates {@link StringComposedEvent} for each line that is composed.
 * <p>
 * The {@link StringComposedEvent} provides a
 * {@link java.util.stream.Stream} of {@link java.lang.String} for the current {@link org.jsapar.model.Line} where each
 * string is matches the cell in a schema. Each cell is formatted according to provided
 * {@link org.jsapar.schema.Schema}.
 */
public class StringComposer implements Composer {

    private final Schema             schema;
    private final StringComposedEventListener stringComposedEventListener;
    private final Map<String, StringLineComposer> lineComposers;

    public StringComposer(Schema schema, StringComposedEventListener composedEventListener) {
        this(schema, composedEventListener, schema.stream().collect(Collectors.toMap(SchemaLine::getLineType, StringLineComposer::new)));
    }

    StringComposer(Schema schema, StringComposedEventListener composedEventListener, Map<String, StringLineComposer> lineComposers) {
        this.schema = schema;
        this.stringComposedEventListener = composedEventListener;
        this.lineComposers = lineComposers;
    }

    @Override
    public boolean composeLine(Line line) {
        StringLineComposer lineComposer = lineComposers.get(line.getLineType());
        if(lineComposer == null || lineComposer.isIgnoreWrite())
            return false;
        stringComposedEvent(new StringComposedEvent(
                        line.getLineType(),
                        line.getLineNumber(),
                        lineComposer.composeStringLine(line)));
        return true;
    }


    @Override
    public void setErrorEventListener(ErrorEventListener errorListener) {
//        this.errorEventListener = errorListener; // Not used.
    }

    private boolean stringComposedEvent(StringComposedEvent event) {
        if (this.stringComposedEventListener != null) {
            stringComposedEventListener.stringComposedEvent(event);
            return true;
        }
        return false;
    }

}
