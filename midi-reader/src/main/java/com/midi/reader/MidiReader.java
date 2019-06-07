package com.midi.reader;

import lombok.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sound.midi.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

@Data
public class MidiReader {
    public static Logger logger = LogManager.getLogger(MidiReader.class);
    private static final int NOTE_ON = 0x90;
    private static final int NOTE_OFF = 0x80;
    private Sequence seq;
    private String scale;
    private long ticks;
    private Notes.Mapper mapper;
    private ArrayList<Note> notes = null;

    public MidiReader(String path) throws IOException, InvalidMidiDataException {
        seq = MidiSystem.getSequence(new File(path));
        ticks = seq.getResolution();
    }

    public void setScale(String scale) throws NotesException {
        this.scale = scale;
        mapper = Notes.getNoteMapper(scale);

    }

    public String parseNotes(String toFile) throws MidiReaderException, NotesException, IOException {
        return parseNotes(toFile, false, false);
    }

    private void addNote(long tick, int note, Notes.Mapper mapper) throws NotesException {
        int parsed;
        if (Objects.isNull(mapper))
            parsed = note;
        else
            parsed = mapper.mapToIndex(note);
        String noteName = Notes.NOTES_NAME[note % 12];
        System.out.println(note + " -> " + parsed + "," + tick + "," + noteName);
        notes.add(new Note(tick, parsed, ticks));
    }

    public String parseNotes(String toFile, boolean ignoreScale, boolean scaleToC) throws MidiReaderException, NotesException, IOException {
        if (!ignoreScale && Objects.isNull(mapper))
            throw new MidiReaderException("Scale of the music is not set.");

        if (ignoreScale)
            System.out.println("Ignore scale flag is on. Notes outside of the scale are removed!");

        mapper.setScaleToC(scaleToC);

        if (notes == null) {
            notes = new ArrayList<>();
            int trackNumber = 0;
            for (Track track : seq.getTracks()) {
                trackNumber++;
                System.out.println("Track " + trackNumber + ": size = " + track.size());
                System.out.println("CSV: key -> parsed, tick, note name");
                for (int i = 0; i < track.size(); i++) {
                    MidiEvent event = track.get(i);
                    long tick = event.getTick();
                    MidiMessage message = event.getMessage();
                    if (message instanceof ShortMessage) {
                        ShortMessage sm = (ShortMessage) message;
                        if (sm.getCommand() == NOTE_ON) {
                            int key = sm.getData1();
                            try {
                                addNote(tick, key, mapper);
                            } catch (NotesException e) {
                                if (!ignoreScale)
                                    throw e;
                                else
                                    System.out.println("Ignoring: " + Notes.NOTES_NAME[key % 12]);
                            }
                        }
                    }
                }
            }
            Collections.sort(notes);
        } else {
            System.out.println("Using cached output.");
        }
        String csv = Notes.parseToCSV(notes);
        if (toFile == null)
            // return a string
            return csv;
        try (FileWriter output = new FileWriter(toFile + "-" + seq.getResolution() + "-" + scale + ".csv")) {
            output.write("tick,note,measure");
            output.write(System.getProperty("line.separator"));
            output.write(csv);
        }
        return null;
    }

    public static void main(String[] args) throws Exception {
        System.out.println("MidiReader.class: Running sampled test");
        String path = MidiReader.class.getResource("/Falling.mid").getPath();
        MidiReader midiReader = new MidiReader(path);
        midiReader.setScale("Em");
        midiReader.parseNotes(null, true, false);
        midiReader.parseNotes("falling-01", true, false);
    }
}
