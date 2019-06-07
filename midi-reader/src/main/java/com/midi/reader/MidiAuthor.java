package com.midi.reader;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class MidiAuthor {
    public static Logger logger = LogManager.getLogger(MidiAuthor.class);

    public static void reader(String scale, String input, String output, boolean ignore, boolean scaleToC) throws Exception {
        MidiReader mr = new MidiReader(input);
        mr.setScale(scale);
        mr.parseNotes(output, ignore, scaleToC);
    }

    public static void writer(String scale, String input, String output, int resolution) throws Exception {
        MidiWriter mw = new MidiWriter(input);
        mw.setScale(scale);
        mw.setResolution(resolution);
        mw.parseToMIDI(output);
    }

    public static void main(String[] args) throws Exception {
        CommandLineParser parser = new DefaultParser();
        Options options = new Options();

        Option operation = new Option("op", "operation", true, "operation to perform [tocsv, tomidi]");
        Option input = new Option("i", "input", true, "the input file name");
        Option output = new Option("o", "output", true, "the output file name");
        Option scale = new Option("s", "scale", true, "the scale of the song");
        Option ignoreScale = new Option("S", "ignore-scale", false, "exclude notes that are not in the scale");
        Option scaleToC = new Option("C", "scale-to-c", false, "change the scale to C");
        Option resolution = new Option("r", "resolution", true, "the resolution of the Midi file ticks");
        Option help = new Option("h", "help", false, "print this message");

        options.addOption(operation);
        options.addOption(input);
        options.addOption(output);
        options.addOption(scale);
        options.addOption(ignoreScale);
        options.addOption(resolution);
        options.addOption(help);

        CommandLine cl = parser.parse(options, args);
        if (cl.hasOption("help")) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("java -jar MidiAuthor.jar", options);
            System.exit(0);
        }

        String error;
        if (!cl.hasOption("op")) {
            error = "Missing required option: op=[tocsv,tomidi]";
            throw new MidiAuthorException(error);
        }

        if (!cl.hasOption("s")) {
            error = "Missing required option: s=<musical scale>";
            throw new MidiAuthorException(error);
        }

        if (!cl.hasOption("i")) {
            error = "Missing required option: i=<input filename>";
            throw new MidiAuthorException(error);
        }

        if (!cl.hasOption("o")) {
            error = "Missing required option: o=<output filename>";
            throw new MidiAuthorException(error);
        }

        String op = cl.getOptionValue("op");
        if (!op.equals("tocsv") && !op.equals("tomidi")) {
            error = "Invalid option: op=[tocsv,tomidi]";
            throw new MidiAuthorException(error);
        }

        String sc = cl.getOptionValue("scale");
        String in = cl.getOptionValue("input");
        String ou = cl.getOptionValue("output");

        if (op.equals("tocsv")) {
            logger.debug("Converting Midi to CSV");
            boolean ignore = cl.hasOption("S");
            boolean stc = cl.hasOption("C");
            reader(sc, in, ou, ignore, stc);
        } else {
            if (!cl.hasOption("r")) {
                error = "Missing required option: r=<resolution number>";
                throw new MidiAuthorException(error);
            }
            String re = cl.getOptionValue("resolution");
            writer(sc, in, ou, Integer.parseInt(re));
        }
    }
}
