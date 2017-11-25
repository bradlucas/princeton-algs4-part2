import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class BurrowsWheeler {

    private static final int R = 256; // Radix of a byte.

    
    // apply Burrows-Wheeler encoding, reading from standard input and writing to standard output
    public static void encode() {
        String in = BinaryStdIn.readString();
        CircularSuffixArray csa = new CircularSuffixArray(in);
        int first = 0;
        while (first < csa.length() && csa.index(first) != 0) {
            first++;
        }
        BinaryStdOut.write(first);
        for (int i = 0; i < csa.length(); i++) {
            BinaryStdOut.write(in.charAt((csa.index(i) + in.length() - 1) % in.length()));
        }
        BinaryStdOut.close();
        
    }

    // apply Burrows-Wheeler decoding, reading from standard input and writing to standard output
    public static void decode() {
        int first = BinaryStdIn.readInt();
        String s = BinaryStdIn.readString();
        int n = s.length();

        int[] count = new int[R + 1];
        int[] next = new int[n];

        for (int i = 0; i < n; i++) {
            count[s.charAt(i) + 1]++;
        }

        for (int i = 1; i < R + 1; i++) {
            count[i] += count[i - 1];
        }

        for (int i = 0; i < n; i++) {
            next[count[s.charAt(i)]++] = i;
        }

        for (int i = next[first], c = 0; c < n; i = next[i], c++) {
            BinaryStdOut.write(s.charAt(i));
        }

        BinaryStdOut.close();
        
    }

    // if args[0] is '-', apply Burrows-Wheeler encoding
    // if args[0] is '+', apply Burrows-Wheeler decoding
    public static void main(String[] args) {
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
