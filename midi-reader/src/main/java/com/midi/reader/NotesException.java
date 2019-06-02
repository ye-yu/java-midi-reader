package com.midi.reader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

class NotesException extends Exception {
    private static Logger logger = LogManager.getLogger(NotesException.class);

    NotesException(String str) {
        super(str);
        logger.error(str);
    }
}
