package com.midi.reader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

class MidiWriterException extends Exception {
    private static Logger logger = LogManager.getLogger(MidiWriterException.class);

    MidiWriterException(String str) {
        super(str);
        logger.error(str);
    }
}
