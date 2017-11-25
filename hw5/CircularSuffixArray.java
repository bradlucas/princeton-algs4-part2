

public class CircularSuffixArray {
    
    private static final int CUTOFF =  15;   // cutoff to insertion sort
    
    private int[] index = null;
    private final int len;
    
    public CircularSuffixArray(String s) {  // circular suffix array of s
        if (null == s) {
            throw new IllegalArgumentException("Constructor was pass a null parameter");
        }
        
        len = s.length();
        index = new int[s.length()];
        
        for (int i = 0; i < s.length(); i++) {
            index[i] = i;
        }
        sort(s, 0, s.length() - 1, 0);
    }
    
    public int length() {                  // length of s
        return index.length;
    }
    
    public int index(int i) {               // returns index of ith sorted suffix
        if (i < 0 || i > (len - 1)) {
            throw new IllegalArgumentException("Index i is not with bounds of 0 and len - 1i");
        }
        return index[i];
    }

    // 3-way string quicksort a[lo..hi] starting at dth character
    // From https://algs4.cs.princeton.edu/51radix/Quick3string.java.html
    private void sort(String s, int lo, int hi, int d) { 

        // cutoff to insertion sort for small subarrays
        if (hi <= lo + CUTOFF) {
            insertion(s, lo, hi, d);
            return;
        }

        int lt = lo, gt = hi;
        int v = charAt(s, index[lo], d);
        int i = lo + 1;
        while (i <= gt) {
            int t = charAt(s, index[i], d);
            if (t < v) {
                exch(lt++, i++);
            } else if (t > v) {
                exch(i, gt--);
            } else {
                i++;
            }
        }

        // a[lo..lt-1] < v = a[lt..gt] < a[gt+1..hi]. 
        sort(s, lo, lt-1, d);
        if (v >= 0) sort(s, lt, gt, d+1);
        sort(s, gt + 1, hi, d);
    }


    // sort from a[lo] to a[hi], starting at the dth character
    private void insertion(String s, int lo, int hi, int d) {
        for (int i = lo; i <= hi; i++)
            for (int j = i; j > lo && less(s, j, j - 1, d); j--)
                exch(j, j-1);
    }

    
    // exchange a[i] and a[j]
    private void exch(int i, int j) {
        int temp = index[i];
        index[i] = index[j];
        index[j] = temp;
    }

    // return the offset'th character of the suffix begininng in s at index suffix
    private int charAt(String s, int suffix, int offset) {
        return s.charAt((suffix + offset) % len);
    }


    // Is suffix i less than suffix j, starting at offset
    private boolean less(String s, int i, int j, int offset) {
        int oi = index[i];
        int oj = index[j];
        for (; offset < len; offset++) {
            int ival = charAt(s, oi, offset);
            int jval = charAt(s, oj, offset);
            if (ival < jval) {
                return true;
            } else if (ival > jval) {
                return false;
            }
        }
        return false;
    }

    
    public static void main(String[] args)  { // unit testing of the methods (optional)

    }
    
}
