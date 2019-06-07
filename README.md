# java-midi-reader

This java program converts midi to simple csv and re-converts supported csv to midi of one track.

### Pre-requisites and Features
The preferred midi format is a synchronous or solo track midi. 
The encoding of the notes is simplified to scale-oriented information. 
Currently, only major scales are supported, and minor scales are mirrorred to their similar major scales.
In the future, minor scales and custom scales will be added as a new feature.

### How to run
There are two options:
  - from midi to csv (using the flag `-op=tocsv`)
  - from csv to midi (using the flag `-op=tomidi`)


From MIDI to CSV
================
The required arguments are as such:
  - `-op=tocsv`: to enable convertion to csv
  - `-i=<name of the input file>`: to specify the source of the midi file
  - `-o=<name of the output file>`: to specify the output of the csv file
  - `-s=<scale of the file`: to specify the scale of the source midi file

When running in the minimum requirement of arguments, the program will exit immediately when the note does not
match the specified scale. These notes will never be included in the csv file and can be ignored with the optional
`-S` flag.

#### The output of the csv
The csv will have a header of three columns:
  - tick: the tick number of the note
  - note: the note of the song
  - measure: the number of measure of the note

The output filename will following such format:
`<name>-<resolution>-<scale>.csv`

The value of `resolution` varies with the source midi file. It is recommended to use the same resolution value 
when converting back to midi if necessary, e.g. after certain changes has been made. 

From CSV to MIDI
================
The required arguments are as such:
  - `-op=tomidi`: to enable convertion to midi
  - `-i=<name of the input file>`: to specify the source of the csv file
  - `-o=<name of the output file>`: to specify the output of the midi file
  - `-s=<scale of the file>`: to specify the scale of the output midi file
  - `-r=<resolution of the midi>`: to specify the resolution of the output midi file

When running in the minimum requirement of arguments, the program will exit immediately when the note does not
match the specified scale. These notes will never be included in the csv file and can be ignored with the optional
`-S` flag.

#### The output of the midi
Each note will have length of 5 ticks.
