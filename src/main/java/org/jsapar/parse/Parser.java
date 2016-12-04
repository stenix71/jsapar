package org.jsapar.parse;

import org.jsapar.error.ErrorEventListener;
import org.jsapar.error.MulticastErrorEventListener;

import java.io.IOException;

/**
 * Common interface for all parsers. The interface does not state anything about the origin of the parsed items.
 *
 * An instance of a parser is only useful once. You create an instance, initializes it
 * with the event listeners needed, then call {@link #parse()}.
 *
 * @see org.jsapar.TextParser
 * @see org.jsapar.BeanParser
 */
public interface Parser {

    /**
     * Sets a line event listener to this parser. If you want more than one line event listener registered, use a {@link MulticastLineEventListener}.
     * @param eventListener The line event listener.
     */
    void setLineEventListener(LineEventListener eventListener);

    /**
     * Sets an error event listener to this parser. Default behavior otherwise is to throw an exception upon the first
     * error. If you want more than one listener to get each error event, use a {@link MulticastErrorEventListener}.
     * @param errorEventListener The error event listener.
     */
    void setErrorEventListener(ErrorEventListener errorEventListener);

    /**
     * Start the parsing. Should only be called once for each parser. Consecutive calls may have unexpected behavior.
     * @throws IOException In case there is an error reading the input.
     */
    void parse() throws IOException;

}
