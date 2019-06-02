package com.midi.reader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

class MidiReaderException extends Exception {
    private static Logger logger = LogManager.getLogger(MidiReaderException.class);

    MidiReaderException(String str) {
        super(str);
        logger.error(str);
    }
}
