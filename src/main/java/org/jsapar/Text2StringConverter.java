package org.jsapar;

import org.jsapar.compose.Composer;
import org.jsapar.compose.string.StringComposedConsumer;
import org.jsapar.compose.string.StringComposedEvent;
import org.jsapar.compose.string.StringComposedEventListener;
import org.jsapar.compose.string.StringComposer;
import org.jsapar.convert.AbstractConverter;
import org.jsapar.convert.ConvertTask;
import org.jsapar.text.TextParseConfig;
import org.jsapar.parse.text.TextParseTask;
import org.jsapar.schema.Schema;

import java.io.IOException;
import java.io.Reader;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Converts a text input to  {@link StringComposedEvent} for each line that is parsed.
 * <p>
 * The {@link StringComposedEvent} provides a
 * {@link java.util.stream.Stream} of {@link java.lang.String} for the current {@link org.jsapar.model.Line} where each
 * string is matches the cell in a schema. Each cell is formatted according to provided
 * {@link org.jsapar.schema.Schema}.
 * <p>
 * The schema can be of either CSV or FixedWith, the only thing that is significant is the order of the cells and the
 * cell formatting.
 * <p>
 * See {@link AbstractConverter} for details about error handling and manipulating data.
 */
public class Text2StringConverter extends AbstractConverter {
    private final Schema parseSchema;
    private final Schema composeSchema;
    private TextParseConfig parseConfig = new TextParseConfig();

    public Text2StringConverter(Schema parseSchema, Schema composeSchema) {
        this.parseSchema = parseSchema;
        this.composeSchema = composeSchema;
    }

    public Text2StringConverter(Schema parseSchema, Schema composeSchema, TextParseConfig parseConfig) {
        this.parseSchema = parseSchema;
        this.composeSchema = composeSchema;
        this.parseConfig = parseConfig;
    }

    /**
     * Deprecated since 2.2. Use {@link #convertForEach(Reader, StringComposedConsumer)} instead.
     * @param reader                The reader to read input from
     * @param composedEventListener The string composed event listener that get notification of each line.
     * @return Number of converted lines.
     * @throws IOException In case of IO error
     */
    @Deprecated
    public long convert(Reader reader, StringComposedEventListener composedEventListener) throws IOException {
        TextParseTask parseTask = new TextParseTask(this.parseSchema, reader, parseConfig);
        ConvertTask convertTask = new ConvertTask(parseTask, new StringComposer(composeSchema, composedEventListener));
        return execute(convertTask);
    }

    /**
     * This method provides apart from the string values, also the line type and line number.
     * @param reader                The reader to read input from
     * @param stringComposedConsumer The string composed event listener that get notification of each line.
     * @return Number of converted lines.
     * @throws IOException In case of IO error
     * @see #convertForEach(Reader, Consumer)
     */
    public long convertForEach(Reader reader, StringComposedConsumer stringComposedConsumer) throws IOException {
        TextParseTask parseTask = new TextParseTask(this.parseSchema, reader, parseConfig);
        ConvertTask convertTask = new ConvertTask(parseTask, makeComposer(composeSchema, stringComposedConsumer));
        return execute(convertTask);
    }

    /**
     * Use this method if you are only interested in the values.
     * @param reader                The reader to read input from
     * @param stringComposedConsumer The string composed event listener that get notification of each line.
     * @return Number of converted lines.
     * @throws IOException In case of IO error
     * @see #convertForEach(Reader, StringComposedConsumer)
     *
     */
    public long convertForEach(Reader reader, Consumer<Stream<String>> stringComposedConsumer) throws IOException {
        return this.convertForEach(reader, (cells, lineType, lineNumber)->stringComposedConsumer.accept(cells));
    }

    /**
     * Creates the composer. Makes it possible to override with custom made implementation of {@link StringComposer}
     * @param schema                The output schema to use while composing.
     * @param stringComposedConsumer The consumer that will be called for each line.
     * @return The composer to use in this converter
     */
    @SuppressWarnings("WeakerAccess")
    protected Composer makeComposer(Schema schema, StringComposedConsumer stringComposedConsumer) {
        return new StringComposer(schema, stringComposedConsumer);
    }


    public TextParseConfig getParseConfig() {
        return parseConfig;
    }

    public void setParseConfig(TextParseConfig parseConfig) {
        this.parseConfig = parseConfig;
    }
}
