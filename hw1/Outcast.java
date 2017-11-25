import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;


public class Outcast {
    private final WordNet wordnet;
    
    public Outcast(WordNet wordnet)  { // constructor takes a WordNet object
        this.wordnet = wordnet;
    }
    
    
    public String outcast(String[] nouns) {  // given an array of WordNet nouns, return an outcast
        // loop through nouns furthest distance
        String outcast = null;
        int maxDistance = 0;
        
        for (String n : nouns) {
            int dist = 0;
            for (String n2: nouns) {
                if (!n.equals(n2)) {
                    dist += wordnet.distance(n, n2);
                }
            }
            if (dist > maxDistance) {
                maxDistance = dist;
                outcast = n;
            }
        }
        return outcast;
    }  


    public static void main(String[] args) {  // see test client below
        
        WordNet wordnet = new WordNet(args[0], args[1]);
        Outcast outcast = new Outcast(wordnet);

        for (int t = 2; t < args.length; t++) {
            In in = new In(args[t]);
            String[] nouns = in.readAllStrings();
            StdOut.println(args[t] + ": " + outcast.outcast(nouns));
        }

        
    }
}

