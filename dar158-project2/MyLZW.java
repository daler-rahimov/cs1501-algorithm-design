
/**
 * ***********************************************************************
 * Edited by Daler Rahimov 10/05/2016 Compilation: javac MyLZW.java Execution:
 * java LZW - < input.txt (compress) Execution: java MyLZW + [mode] < input.txt
 * [mode]: n -Do nothing mode , r - Reset mode , m - monitor mode.
 * (expand) Dependencies: BinaryIn.java BinaryOut.java
 *
 * Compress or expand binary input from standard input using LZW.
 *
 * WARNING: STARTING WITH ORACLE JAVA 6, UPDATE 7 the SUBSTRING METHOD TAKES
 * TIME AND SPACE LINEAR IN THE SIZE OF THE EXTRACTED SUBSTRING (INSTEAD OF
 * CONSTANT SPACE AND TIME AS IN EARLIER IMPLEMENTATIONS).
 *
 * See
 * <a href = "http://java-performance.info/changes-to-string-java-1-7-0_06/">this
 * article</a>
 * for more details.
 *
 ************************************************************************
 */
public class MyLZW {

    private static final int NUMBER_OF_INPUT_CHARS = 256;        // number of input chars
    private static final int MAX_NUMBER_OF_INPUT_CHARS = 65536; // 2^16 max number of inputs in to the codebook allowed
    private static int numberOfCodewords = 512;       // number of codewords = 2^W
    private static int codewordWidth = 9;         // codeword width
    private static double uncompressedNumberOfBits = 0;
    private static double compressedNumberOfBits = 0;
    private static double oldRatio = 0;
    private static double newRatio = 0;
    private static final double THRESHOLD = 1.1;
    private static boolean isOldRatioSet = false;   // if code word width is max set oldRatio ones. This is why this flag is needed 
    private static char mode = 'n';

    public static void compress() {
        // Writing 2 bit representin the mode 0->n 1->r 2->m
        switch (mode) {
            case 'n':
                BinaryStdOut.write(0, 2);
                break;
            case 'r':
                BinaryStdOut.write(1, 2);
                break;
            case 'm':
                BinaryStdOut.write(2, 2);
                break;
        }
        String input = BinaryStdIn.readString();
        TST<Integer> codeBook = new TST<Integer>();
        for (int i = 0; i < NUMBER_OF_INPUT_CHARS; i++) {
            codeBook.put("" + (char) i, i);
        }
        int code = NUMBER_OF_INPUT_CHARS + 1;  // R is codeword for EOF

        while (input.length() > 0) {
            // increase the code word with
            if (code == numberOfCodewords && codewordWidth < 16) {
//                System.err.println("uncomp ="+uncompressedNumberOfBits+"comp ="+compressedNumberOfBits 
//                        + "oldRatio= "+oldRatio + " newRatio" + newRatio);

                codewordWidth++;
                numberOfCodewords *= 2;
            } ///Reset the codebook and start over 
            else if (mode == 'r' && codewordWidth == 16 && code == numberOfCodewords) {
                codewordWidth = 9;
                numberOfCodewords = 512;
                codeBook = new TST<>();
                for (int i = 0; i < NUMBER_OF_INPUT_CHARS; i++) {
                    codeBook.put("" + (char) i, i);
                }
                code = NUMBER_OF_INPUT_CHARS + 1;  // R is codeword for EOF
            } else if (mode == 'm' && codewordWidth == 16 && code == numberOfCodewords) {
                if (!isOldRatioSet) {
                    oldRatio = newRatio;
                    isOldRatioSet = true;
                } else if ((oldRatio / newRatio) > THRESHOLD) {// reset if ratio of ratios are bigger the 1.1
                    codewordWidth = 9;
                    numberOfCodewords = 512;
                    codeBook = new TST<>();
                    for (int i = 0; i < NUMBER_OF_INPUT_CHARS; i++) {
                        codeBook.put("" + (char) i, i);
                    }
                    code = NUMBER_OF_INPUT_CHARS + 1;  // R is codeword for EOF
                    isOldRatioSet = false;
                    oldRatio = 0;
                    newRatio = 0;
                }
            }

            String s = codeBook.longestPrefixOf(input);  // Find max prefix match s.
            //count number of bits uncompressed 
            uncompressedNumberOfBits = s.length() * 8 + uncompressedNumberOfBits;// BinaryStdIn.readString read one bite at a time so *8 

            BinaryStdOut.write(codeBook.get(s), codewordWidth);      // Print s's encoding.
            // count number of bits compressed  
            compressedNumberOfBits += codewordWidth; //
            newRatio = uncompressedNumberOfBits / compressedNumberOfBits;// calculate the new ration 

            int t = s.length();
            if (t < input.length() && code < numberOfCodewords) {    // Add s to symbol table.
                codeBook.put(input.substring(0, t + 1), code++);
            }
            input = input.substring(t);            // Scan past s in input.
        }
        BinaryStdOut.write(NUMBER_OF_INPUT_CHARS, codewordWidth);
        BinaryStdOut.close();
    }

    public static void expand() {
        int intMode = BinaryStdIn.readInt(2);// mode represatation in 2 bits
        switch (intMode) {
            case 0:
                mode = 'n';
                break;
            case 1:
                mode = 'r';
                break;
            case 2:
                mode = 'm';
                break;
        }

        String[] codeBook = new String[MAX_NUMBER_OF_INPUT_CHARS]; // 2^16 is maximum number of codewords posible 
        int i; // next available codeword value
        // initialize symbol table with all 1-character strings
        for (i = 0; i < NUMBER_OF_INPUT_CHARS; i++) {
            codeBook[i] = "" + (char) i;
        }
        codeBook[i++] = "";                        // (unused) lookahead for EOF
        int codeword = BinaryStdIn.readInt(codewordWidth);
        // count number of compressed bits
        compressedNumberOfBits += codewordWidth;

        if (codeword == NUMBER_OF_INPUT_CHARS) {
            return;           // expanded message is empty string
        }
        String val = codeBook[codeword];
        BinaryStdOut.write(val);
        // count number of bits uncompressed
        uncompressedNumberOfBits = val.length() * 8 + uncompressedNumberOfBits;

        while (true) {
            // increase the code word with
            if (i == numberOfCodewords - 1 && codewordWidth < 16) {
//                System.err.println("uncomp ="+uncompressedNumberOfBits+"comp ="+compressedNumberOfBits 
//                        + "oldRatio= "+oldRatio + " newRatio" + newRatio);

                codewordWidth++;
                numberOfCodewords *= 2;
            } else if (mode == 'r' && codewordWidth == 16 && i == numberOfCodewords - 1) {
                codewordWidth = 9;
                numberOfCodewords = 512;
                for (i = 0; i < NUMBER_OF_INPUT_CHARS; i++) {
                    codeBook[i] = "" + (char) i;
                }
                codeBook[i++] = "";                        // (unused) lookahead for EOF
                codeword = BinaryStdIn.readInt(codewordWidth);
                if (codeword == NUMBER_OF_INPUT_CHARS) {
                    break;           // expanded message is empty string
                }
                val = codeBook[codeword];
                BinaryStdOut.write(val);
            } else if (mode == 'm' && codewordWidth == 16 && i == numberOfCodewords - 1) {
                if (!isOldRatioSet) {
                    oldRatio = newRatio;
                    isOldRatioSet = true;
                } else if ((oldRatio / newRatio) > THRESHOLD) {// reset if ratio of ratios are bigger the 1.1
                    isOldRatioSet = false;
                    oldRatio = 0;
                    newRatio = 0;
                    codewordWidth = 9;
                    numberOfCodewords = 512;
                    for (i = 0; i < NUMBER_OF_INPUT_CHARS; i++) {
                        codeBook[i] = "" + (char) i;
                    }
                    codeBook[i++] = "";                        // (unused) lookahead for EOF
                    codeword = BinaryStdIn.readInt(codewordWidth);
                    // count number of compressed bits
                    compressedNumberOfBits += codewordWidth;

                    if (codeword == NUMBER_OF_INPUT_CHARS) {
                        break;           // expanded message is empty string
                    }
                    val = codeBook[codeword];
                    BinaryStdOut.write(val);
                    // count number of bits uncompressed
                    uncompressedNumberOfBits = val.length() * 8 + uncompressedNumberOfBits;
                }
            }

            codeword = BinaryStdIn.readInt(codewordWidth);
            // count number of compressed bits
            compressedNumberOfBits += codewordWidth;

            if (codeword == NUMBER_OF_INPUT_CHARS) {
                break;
            }
            String s = codeBook[codeword];
            if (i == codeword) {
                s = val + val.charAt(0);   // special case hack
            }
            if (i < numberOfCodewords) {
                codeBook[i++] = val + s.charAt(0);
            }
            val = s;
            BinaryStdOut.write(val);
            // count number of bits uncompressed
            uncompressedNumberOfBits = val.length() * 8 + uncompressedNumberOfBits;
            newRatio = uncompressedNumberOfBits /compressedNumberOfBits ;// calculate ratio
        }
        BinaryStdOut.close();
    }


    public static void main(String[] args) {
        if (args[0].equals("-")) {
            mode = args[1].charAt(0);
            compress();
        } else if (args[0].equals("+")) {
            expand();
        } else {
            throw new IllegalArgumentException("Illegal command line argument");
        }
    }

}
