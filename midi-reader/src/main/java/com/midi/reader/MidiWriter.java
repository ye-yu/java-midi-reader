package com.midi.reader;

import lombok.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

@Data
public class MidiWriter {
    private static Logger logger = LogManager.getLogger(MidiWriter.class);
    private ArrayList<Note> notes = new ArrayList<>();
    private Notes.Mapper mapper = null;
    private Sequence seq = null;
    private int resolution = 0;

    public MidiWriter(String path) throws IOException, MidiWriterException {
        File file = new File(path);
        if (!file.exists() || file.isDirectory()) {
            throw new IOException("Input file is not a readable csv file.");
        }
        String header;
        try (Scanner sc = new Scanner(file)) {
            header = sc.nextLine();
            if (!header.equals("tick,note,measure"))
                throw new MidiWriterException("Input file is not a readable format.");
            while (sc.hasNext()) {
                String[] line = sc.nextLine().split(",");
                long tickNumber = Long.parseLong(line[0]);
                int noteNumber = Integer.parseInt(line[1]);
                long measure = Long.parseLong(line[2]);
                Note note = new Note(tickNumber, noteNumber, 1L);
                note.setMeasure(measure);
                notes.add(note);
            }
        }
    }

    public void setScale(String scale) throws NotesException {
        mapper = Notes.getNoteMapper(scale);
    }

    public void parseToMIDI(String filename) throws MidiWriterException, InvalidMidiDataException, IOException {
        if (Objects.isNull(mapper))
            throw new MidiWriterException("Scale is not set.");

        if (resolution == 0)
            throw new MidiWriterException("Resolution is not set.");

        if (Objects.isNull(seq)) {
            seq = new Sequence(Sequence.PPQ, resolution);
            Track track = seq.createTrack();
            for (Note note : notes) {
                int key = mapper.mapToNote(note.getScaledNote());
                ShortMessage msg_on = new ShortMessage(ShortMessage.NOTE_ON, key, 127);
                MidiEvent event_on = new MidiEvent(msg_on, note.getTickNumber());
                track.add(event_on);
                ShortMessage msg_off = new ShortMessage(ShortMessage.NOTE_OFF, key, 127);
                MidiEvent event_off = new MidiEvent(msg_off, note.getTickNumber() + 5L);
                track.add(event_off);
            }
        }
        MidiSystem.write(seq, 0, new File(filename + ".mid"));
    }

    public static void main(String[] args) throws IOException, MidiWriterException, InvalidMidiDataException, NotesException {
        logger.debug("MidiWriter.class: Running sampled test");
        MidiWriter mw = new MidiWriter(MidiWriter.class.getResource("/Falling.csv").getPath());
        mw.setScale("Em");
        mw.setResolution(240);
        mw.parseToMIDI("falling-processed");
    }
}
