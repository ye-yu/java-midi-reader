package com.midi.reader;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.omg.PortableInterceptor.AdapterStateHelper;

import java.io.NotSerializableException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Notes {
    private static class Scales {
        private final static int[] C = {0, 2, 4, 5, 7, 9, 11};
    }

    public final static String C = "C";
    public final static String D = "D";
    public final static String E = "E";
    public final static String F = "F";
    public final static String G = "G";
    public final static String A = "A";
    public final static String B = "B";
    public final static String C_SHARP = "C#";
    public final static String D_SHARP = "D#";
    public final static String F_SHARP = "F#";
    public final static String G_SHARP = "G#";
    public final static String A_SHARP = "A#";
    public final static String D_FLAT = "Db";
    public final static String E_FLAT = "Eb";
    public final static String G_FLAT = "Gb";
    public final static String A_FLAT = "Ab";
    public final static String B_FLAT = "Bb";

    public final static String C_MINOR = "Cm";
    public final static String D_MINOR = "Dm";
    public final static String E_MINOR = "Em";
    public final static String F_MINOR = "Fm";
    public final static String G_MINOR = "Gm";
    public final static String A_MINOR = "Am";
    public final static String B_MINOR = "Bm";
    public final static String C_SHARP_MINOR = "C#m";
    public final static String D_SHARP_MINOR = "D#m";
    public final static String F_SHARP_MINOR = "F#m";
    public final static String G_SHARP_MINOR = "G#m";
    public final static String A_SHARP_MINOR = "A#m";
    public final static String D_FLAT_MINOR = "Dbm";
    public final static String E_FLAT_MINOR = "Ebm";
    public final static String G_FLAT_MINOR = "Gbm";
    public final static String A_FLAT_MINOR = "Abm";
    public final static String B_FLAT_MINOR = "Bbm";

    public final static String[] NOTES_NAME = {
            "C",
            "C#",
            "D",
            "D#",
            "E",
            "F",
            "F#",
            "G",
            "G#",
            "A",
            "A#",
            "B",
    };
    public final static HashMap<String, Integer> NOTES_DISTANCE = new HashMap<>();
    private final static HashMap<String, String> SCALE_MAP = new HashMap<>();
    private final static HashMap<String, String> FLAT_TO_SHARP = new HashMap<>();
    private final static HashMap<String, Integer> C_FACTOR = new HashMap<>();

    static {
        // minor - major scales
        SCALE_MAP.put(C_MINOR, D_SHARP);
        SCALE_MAP.put(C_SHARP_MINOR, E);
        SCALE_MAP.put(D_MINOR, F);
        SCALE_MAP.put(D_SHARP_MINOR, F_SHARP);
        SCALE_MAP.put(E_MINOR, G);
        SCALE_MAP.put(F_MINOR, G_SHARP);
        SCALE_MAP.put(F_SHARP_MINOR, A);
        SCALE_MAP.put(G_MINOR, A_SHARP);
        SCALE_MAP.put(G_SHARP_MINOR, B);
        SCALE_MAP.put(A_MINOR, C);
        SCALE_MAP.put(A_SHARP_MINOR, C_SHARP);
        SCALE_MAP.put(B_MINOR, D);

        // flat to sharp
        FLAT_TO_SHARP.put(D_FLAT, C_SHARP);
        FLAT_TO_SHARP.put(E_FLAT, D_SHARP);
        FLAT_TO_SHARP.put(G_FLAT, F_SHARP);
        FLAT_TO_SHARP.put(A_FLAT, G_SHARP);
        FLAT_TO_SHARP.put(B_FLAT, A_SHARP);

        // number to add from C
        C_FACTOR.put(C, 0);
        C_FACTOR.put(C_SHARP, 1);
        C_FACTOR.put(D, 2);
        C_FACTOR.put(D_SHARP, 3);
        C_FACTOR.put(E, 4);
        C_FACTOR.put(F, 5);
        C_FACTOR.put(F_SHARP, 6);
        C_FACTOR.put(G, 7);
        C_FACTOR.put(G_SHARP, 8);
        C_FACTOR.put(A, 9);
        C_FACTOR.put(A_SHARP, 10);
        C_FACTOR.put(B, 11);

        NOTES_DISTANCE.put(C, 0);

        NOTES_DISTANCE.put(C_SHARP, 1);
        NOTES_DISTANCE.put(D_FLAT, 1);

        NOTES_DISTANCE.put(D, 2);

        NOTES_DISTANCE.put(D_SHARP, 3);
        NOTES_DISTANCE.put(E_FLAT, 3);

        NOTES_DISTANCE.put(E, 4);

        NOTES_DISTANCE.put(F, 5);

        NOTES_DISTANCE.put(F_SHARP, 6);
        NOTES_DISTANCE.put(G_FLAT, 6);

        NOTES_DISTANCE.put(G, 7);

        NOTES_DISTANCE.put(G_SHARP, 8);
        NOTES_DISTANCE.put(A_FLAT, 8);

        NOTES_DISTANCE.put(A, 9);

        NOTES_DISTANCE.put(A_SHARP, 10);
        NOTES_DISTANCE.put(B_FLAT, 10);

        NOTES_DISTANCE.put(B, 11);
    }

    public static int[] getScaleInts(String scale, boolean ignoreException) throws NotesException {
        try {
            return getScaleInts(scale);
        } catch (NotesException e) {
            if (ignoreException)
                return null;
            throw e;
        }
    }

    public static int[] getScaleInts(String scale) throws NotesException {
        if (scale.contains("b")) {
            String old = scale.substring(0, 2);
            try {
                String toSharp = FLAT_TO_SHARP.get(old);
                scale = scale.replace(old, toSharp);
            } catch (Exception e) {
                throw new NotesException("Cannot convert piano flat scale to sharp.");
            }
        }

        if (scale.contains("m")) {
            try {
                scale = SCALE_MAP.get(scale);
            } catch (Exception e) {
                throw new NotesException("Cannot convert piano minor scale to major.");
            }
        }

        int factor = C_FACTOR.get(scale);
        int[] scaleInts = new int[7];
        for (int i = 0; i < 7; i++) {
            scaleInts[i] = Scales.C[i] + factor;
            if (scaleInts[i] >= 12)
                scaleInts[i] -= 12;
        }
        Arrays.sort(scaleInts);
        return scaleInts;
    }

    public static void printScale(String scale) throws NotesException {
        int[] scaleInts = getScaleInts(scale);
        System.out.print(scale + ": [ ");
        for (int i : scaleInts)
            System.out.print(i + " ");
        System.out.print("] -> [ ");
        for (int i : scaleInts)
            System.out.print(NOTES_NAME[i] + " ");
        System.out.println("]");
    }

    public static Mapper getNoteMapper(String scale) throws NotesException {
        return new Mapper(scale);
    }

    public static class Mapper {
        private int[] scaleInts;
        private String scale;
        private boolean scaleToC = false;

        public Mapper(String scale) throws NotesException {
            this.scale = scale;
            this.scaleInts = Notes.getScaleInts(scale);
        }

        public int mapToIndex(int note) throws NotesException {
            int factor = note / 12;
            int value = note % 12;
            for (int i = 0; i < 7; i++) {
                if (scaleInts[i] == value)
                {
                    if (!scaleToC)
                        return factor * 7 + i;
                    int cDistance = NOTES_DISTANCE.get(NOTES_NAME[value]);
                    int mapValue = factor * 7 + i - cDistance;
                    if (mapValue < 0)
                        mapValue += 7;
                    return mapValue;
                }
            }
            throw new NotesException("Note is not in the scale " + scale + ": " + note + "[" + NOTES_NAME[value] + "]");
        }

        public int mapToNote(int index) {
            int factor = index / 7;
            int value = index % 7;
            int note = scaleInts[value];
            return factor * 12 + note;
        }

        public void setScale(String scale) throws NotesException {
            this.scale = scale;
            this.scaleInts = Notes.getScaleInts(scale);
        }

        public void setScaleToC(boolean scaleToC) {
            this.scaleToC = scaleToC;
        }
    }

    public static void unitTest(@org.jetbrains.annotations.NotNull Notes.Mapper mapper, int note) throws Exception {
        int mappedValue = mapper.mapToIndex(note);
        int restoredValue = mapper.mapToNote(mappedValue);
        System.out.println(note + " -> " + mappedValue + " --mapped back--> " + restoredValue);
    }

    @NotNull
    public static String parseToCSV(@NotNull ArrayList<Note> notes) {
        StringBuilder sb = new StringBuilder();
        for (Note note : notes) {
            sb.append(note.getTickNumber());
            sb.append(",");
            sb.append(note.getScaledNote());
            sb.append(",");
            sb.append(note.getMeasure());
            sb.append("\n");
        }
        return sb.toString();
    }

    public static void main(String[] args) throws Exception {
        System.out.println("Notes.class: Running sampled test");
        Notes.Mapper mapper = Notes.getNoteMapper(Notes.C);
        System.out.println("Chosen scale:");
        Notes.printScale(Notes.C);

        // range in 0-11 [valid scale]
        unitTest(mapper, 0);
        unitTest(mapper, 2);
        unitTest(mapper, 11);
        // range in > 11 [valid scale]
        unitTest(mapper, 12);
        unitTest(mapper, 14);

        // range in 0-11 [invalid scale]
        try {
            unitTest(mapper, 1);
            System.out.println("Returned value is error;");
        } catch (Exception e) {
            System.out.println("Passed");
        }

        // range in > 11 [invalid scale]
        try {
            unitTest(mapper, 13);
            System.out.println("Returned value is error;");
        } catch (Exception e) {
            System.out.println("Passed");
        }
    }
}
