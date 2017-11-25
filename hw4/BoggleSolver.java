import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import edu.princeton.cs.algs4.SET;
// import edu.princeton.cs.algs4.TrieSET;


public class BoggleSolver
{
    private final TrieSET dictionary;
    
    // Initializes the data structure using the given array of strings as the dictionary.
    // (You can assume each word in the dictionary contains only the uppercase letters A through Z.)
    public BoggleSolver(String[] dictionary) {
        this.dictionary = new TrieSET();
        for (String s : dictionary) {
            this.dictionary.add(s);
        }
    }

    // Returns the set of all valid words in the given Boggle board, as an Iterable.
    public Iterable<String> getAllValidWords(BoggleBoard board) {
        boolean[][] visited = new boolean[board.rows()][board.cols()];
        SET<String> validWords = new SET<String>();
        for (int i = 0; i < board.rows(); i++) {
            for (int j = 0; j < board.cols(); j++) {
                search(i, j, board, visited, validWords, "");
            }

        }
        return validWords;
    }

    private void search(int row, int col, BoggleBoard board, boolean[][] visited, SET<String> validWords, String prefix) {
        if (visited[row][col]) {
            return;
        }

        char letter = board.getLetter(row, col);
        String word = prefix;

        if (letter == 'Q') {
            word += "QU";
        } else {
            word += letter;
        }

        if (!dictionary.hasPrefix(word)) {
            // StdOut.println(word);
            return;
        }

        if (word.length() > 2 && dictionary.contains(word)) {
            // StdOut.println(word);
            validWords.add(word);
        }

        visited[row][col] = true;

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0) {
                    continue;
                }

                if ((row + i >= 0) && (row + i < board.rows()) && (col + j >= 0) && (col + j < board.cols())) {
                    search(row + i, col + j, board, visited, validWords, word);
                }
            }
        }

        visited[row][col] = false;
    }
                           

    // Returns the score of the given word if it is in the dictionary, zero otherwise.
    // (You can assume the word contains only the uppercase letters A through Z.)
    
    // The Qu special case. In the English language, the letter Q is
    // almost always followed by the letter U. Consequently, the side of
    // one die is printed with the two-letter sequence Qu instead of Q
    // (and this two-letter sequence must be used together when forming
    // words). When scoring, Qu counts as two letters; for example, the
    // word QuEUE scores as a 5-letter word even though it is formed by
    // following a sequence of 4 dice.
    
    // |--------------+--------|
    // | World Length | Points |
    // |--------------+--------|
    // |          0–2 |      0 |
    // |          3–4 |      1 |
    // |            5 |      2 |
    // |            6 |      3 |
    // |            7 |      5 |
    // |           8+ |     11 |
    // |--------------+--------|
    public int scoreOf(String word) {
        int rtn = 0;

        if (dictionary.contains(word)) {
            int len = word.length();
            switch (len) {
            case 0:
            case 1:
            case 2:
                rtn = 0;
                break;
            case 3:
            case 4:
                rtn = 1;
                break;
            case 5:
                rtn = 2;
                break;
            case 6:
                rtn = 3;
                break;
            case 7:
                rtn = 5;
                break;
            default:
                rtn = 11;
                break;
            }
        }
        return rtn;
    }



    public static void main(String[] args) {
        In in = new In(args[0]);
        String[] dictionary = in.readAllStrings();
        BoggleSolver solver = new BoggleSolver(dictionary);
        BoggleBoard board = new BoggleBoard(args[1]);
        int score = 0;
        for (String word : solver.getAllValidWords(board)) {
            StdOut.println(word);
            score += solver.scoreOf(word);
        }
        StdOut.println("Score = " + score);
    }

}

