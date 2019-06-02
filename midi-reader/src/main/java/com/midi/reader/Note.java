package com.midi.reader;

import lombok.Data;

@Data
public class Note implements Comparable<Note> {

    private long tickNumber;
    private int scaledNote;
    private long measure;
    public Note(long tickNumber, int scaledNote, long resolution) {
        this.tickNumber = tickNumber;
        this.scaledNote = scaledNote;
        this.measure = tickNumber / (resolution * 4);
    }

    @Override
    public int compareTo(Note o) {
        long self = this.getTickNumber();
        long other = o.getTickNumber();
        return Long.compare(self, other);
    }
}
