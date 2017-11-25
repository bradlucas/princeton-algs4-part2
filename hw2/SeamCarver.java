import java.awt.Color;

import edu.princeton.cs.algs4.Picture;
import edu.princeton.cs.algs4.StdOut;


public class SeamCarver {
    private static final double MAX_ENERGY = 1000; /// 195075.0;   // = 255^2 + 255^2 + 255^2
    private static final double EPSILON = 0.00000001; // https://stackoverflow.com/a/1088271

    private Picture picture;
    private double[][] energy;
    private int[][] parent;

    
    public SeamCarver(final Picture picture)  {             // create a seam carver object based on the given picture
        // The data type may not mutate the Picture argument to the constructor.
        // java.lang.IllegalArgumentException if null argument
        if (null == picture) {
            throw new java.lang.IllegalArgumentException();
        }

        // intiialize member variables
        this.picture = new Picture(picture);  // copy picture to prevent client from changing picture later
        this.energy = new double[picture.width()][picture.height()];
        this.parent = new int[picture.width()][picture.height()];

        for (int y = 0; y < height(); y++) {
            for (int x = 0; x < width(); x++) {
                energy[x][y] = energy(x, y);
            }
        }
    }

    public Picture picture() {                        // current picture
        // Don't return a mutatable picture so pass it a copy
        return new Picture(this.picture);
    }
    
    public     int width() {                          // width of current picture
        return picture.width();
    }
    
    public     int height() {                         // height of current picture
        return picture.height();
    }
    
    public  double energy(int x, int y) {             // energy of pixel at column x and row y
        if (x < 0 || x > width() - 1 || y < 0 || y > height() -1) {
            throw new java.lang.IllegalArgumentException();
        }

        if (x == 0 || x == width() - 1 || y == 0 || y == height() - 1) {
            return MAX_ENERGY;
        }
        double xDiff = gradient(picture.get(x - 1, y), picture.get(x + 1, y));
        double yDiff = gradient(picture.get(x, y - 1), picture.get(x, y + 1));
        return java.lang.Math.sqrt(xDiff + yDiff);
    }

    public   int[] findHorizontalSeam() {             // sequence of indices for horizontal seam
        flipPicture();
        int[] seam = findVerticalSeam();
        flipPicture();
        return seam;
    }

    public   int[] findVerticalSeam() {               // sequence of indices for vertical seam
        int[] seam = new int[height()];
        double[] distTo = new double[width()];
        double[] oldDistTo = new double[width()];

        for (int y = 0; y < height(); y++) {
            for (int x = 0; x < width(); x++) {
                verticalRelax(x, y, distTo, oldDistTo);
            }
            System.arraycopy(distTo, 0, oldDistTo, 0, width());
        }

        double min = oldDistTo[0];
        int best = 0;
        for (int index = 0; index < oldDistTo.length; index++) {
            if (oldDistTo[index] < min) {
                min = oldDistTo[index];
                best = index;
            }
        }

        seam[height() - 1] = best;
        for (int i = height() - 2; i >= 0; i--) {
            seam[i] = parent[best][i + 1];
            best = parent[best][i + 1];
        }
        return seam;
    }

    public    void removeHorizontalSeam(int[] seam) {  // remove horizontal seam from current picture
        // java.lang.IllegalArgumentException if null argument
        if (null == seam) {
            throw new java.lang.IllegalArgumentException();
        }

        // java.lang.IllegalArgumentException removeHorizontalSeam() is called when the height of the picture is less than or equal to 1.
        if (height() <= 1) {
            throw new java.lang.IllegalArgumentException();
        }

        // Throw exception if seam is not the same length as the width
        if (seam.length != picture.width()) {
            throw new java.lang.IllegalArgumentException();
        }


        // valid seam (i.e., either an entry is outside its prescribed range or two adjacent entries differ by more than 1).
        for (int i = 0; i < seam.length - 1; i++) {
            if (Math.abs(seam[i] - seam[i+1]) > 1) {
                throw new java.lang.IllegalArgumentException();
            }
        }

        Picture p = new Picture(width(), height() - 1);
        for (int y = 0; y < width(); y++) {
            int k = 0;
            for (int x = 0; x < height(); x++) {
                if (x != seam[y]) {
                    p.set(y, k, picture.get(y, x));
                    k++;
                }
            }
        }

        this.picture = p;
        double[][] oldEnergy = energy;
        energy = new double[width()][height()];

        for (int y = 0; y < height(); y++) {
            for (int x = 0; x < width(); x++) {
                energy[x][y] = energy(x, y);
            }
        }
    }
    
    public    void removeVerticalSeam(int[] seam) {    // remove vertical seam from current picture
        // java.lang.IllegalArgumentException if null argument
        if (null == seam) {
            throw new java.lang.IllegalArgumentException();
        }
        // java.lang.IllegalArgumentException if removeVerticalSeam() is called when the width of the picture is less than or equal to 1
        if (width() <= 1) {
            throw new java.lang.IllegalArgumentException();
        }

        // java.lang.IllegalArgumentException if array is of wrong length or it the array is not a valid seam
        if (seam.length != picture.height()) {
            throw new java.lang.IllegalArgumentException();
        }
        
        // valid seam (i.e., either an entry is outside its prescribed range or two adjacent entries differ by more than 1).
        for (int i = 0; i < seam.length - 1; i++) {
            if (Math.abs(seam[i] - seam[i+1]) > 1) {
                throw new java.lang.IllegalArgumentException();
            }
        }

        Picture p = new Picture(width() -1, height());
        for (int y = 0; y < height(); y++) {
            int k = 0;
            for (int x = 0; x < width(); x++) {
                if (x != seam[y]) {
                    p.set(k, y, picture.get(x, y));
                    k++;
                }
            }
        }
        this.picture = p;
        double[][] oldEnergy = energy;
        energy = new double[width()][height()];

        for (int y = 0; y < height(); y++) {
            for (int x = 0; x < width(); x++) {
                energy[x][y] = energy(x, y);
            }
        }
    }

    private void verticalRelax(int col, int row, double[] distTo, double[] oldDistTo) {
        if (row == 0) {
            distTo[col] = MAX_ENERGY;
            parent[col][row] = -1;
            return;
        }

        if (col == 0 && oldDistTo.length == 1) {
            distTo[col] = energy[col][row];
            parent[col][row] = col;
            return;
        }
        
        if (col == 0) {
            // we have only 2 edges
            double a = oldDistTo[col];
            double b = oldDistTo[col + 1];        // TODO Fails on 1x8.png, 8x1.png
            double min = Math.min(a, b);
            distTo[col] = min + energy[col][row];
            if (a < min) {
                parent[col][row] = col;
            } else {
                parent[col][row] = col + 1;
            }
            return;
        }

        if (col == width() - 1) {
            // we have only 2 edges
            double a = oldDistTo[col];
            double b = oldDistTo[col - 1];
            double min = Math.min(a, b);
            distTo[col] = min + energy[col][row];
            if (a < min) {
                parent[col][row] = col;
            } else {
                parent[col][row] = col - 1;
            }
            return;
        }

        // for 3 edges
        double left = oldDistTo[col - 1];
        double mid = oldDistTo[col];
        double right = oldDistTo[col + 1];

        double min = Math.min(Math.min(left, mid), right);

        distTo[col] = min + energy[col][row];
        if (Math.abs(min - left) < EPSILON) {
            parent[col][row] = col - 1;
        } else if (Math.abs(min - mid) < EPSILON) {
            parent[col][row] = col;
        } else {
            parent[col][row] = col + 1;
        }
    }

    private void flipPicture() {
        Picture flippedPicture = new Picture(picture.height(), picture.width());
        double[][] energyNew = new double[picture.height()][picture.width()];
        for (int i = 0; i < picture.width(); i++) {
            for (int j = 0; j < picture.height(); j++) {
                flippedPicture.set(j, i, picture.get(i, j));
                energyNew[j][i] = energy[i][j];
            }
        }
        energy = energyNew;
        picture = flippedPicture;
        parent = new int[picture.width()][picture.height()];
    }

    /**
     * (redA - redB)^2 + (greenA - greenB)^2 + (blueA - blueB)^2
     */
    private double gradient(Color a, Color b) {
        int redDiff = a.getRed() - b.getRed();
        int greenDiff = a.getGreen() - b.getGreen();
        int blueDiff = a.getBlue() - b.getBlue();

        return (redDiff * redDiff) + (greenDiff * greenDiff) + (blueDiff * blueDiff);
    }
    
    public static void main(String[] args) {
        // First parameter should be an image filename
        SeamCarver sc = new SeamCarver(new Picture(args[0]));
        StdOut.printf("energy(1, 1) = %f\n", sc.energy(1, 1));

        int[] seam = sc.findVerticalSeam();
        sc.removeVerticalSeam(seam);
    }
        
}
