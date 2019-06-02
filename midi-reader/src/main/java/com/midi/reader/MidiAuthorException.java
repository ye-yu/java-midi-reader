package com.midi.reader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

class MidiAuthorException extends Exception {
    private static Logger logger = LogManager.getLogger(MidiAuthorException.class);
    MidiAuthorException(String str) {
        super(str);
        logger.error(str);
    }
}
