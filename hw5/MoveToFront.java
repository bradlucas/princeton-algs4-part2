import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;



public class MoveToFront {

    private static final int R = 256; // Extended ASCII
    
    // apply move-to-front encoding, reading from standard input and writing to standard output
    public static void encode() {
        char[] ary = createArray();

        while (!BinaryStdIn.isEmpty()) {
            char ch = BinaryStdIn.readChar();

            char tmpin, count, tmpout;
            for (count = 0, tmpout = ary[0]; ch != ary[count]; count++) {
                tmpin = ary[count];
                ary[count] = tmpout;
                tmpout = tmpin;
            }
            ary[count] = tmpout;
            BinaryStdOut.write(count);
            ary[0] = ch;
        }

        BinaryStdOut.close();
    }

    // apply move-to-front decoding, reading from standard input and writing to standard output
    public static void decode() {
        char[] ary = createArray();

        while (!BinaryStdIn.isEmpty()) {
            char count = BinaryStdIn.readChar();
            BinaryStdOut.write(ary[count], 8);
            char idx = ary[count];
            while (count > 0) {
                ary[count] = ary[--count];
            }
            ary[0] = idx;
        }
        BinaryStdOut.close();

    }

    private static char[] createArray() {
        char[] ary = new char[R];
        for (char i = 0; i < R; i++) {
            ary[i] = i;
        }
        return ary;
    }
        

    // if args[0] is '-', apply move-to-front encoding
    // if args[0] is '+', apply move-to-front decoding
    public static void main(String[] args) {
        // Require single argument of +/-
        // Then read from STDIN and perform operation

        if (args.length != 1) {
            throw new IllegalArgumentException("Required + or - as an argument");
        }

        String arg = args[0];
        if (arg.equals("+")) {
            decode();
        } else if (arg.equals("-")) {
            encode();
        } else {
            throw new IllegalArgumentException("Required + or - as an argument");
        }
    }
}
