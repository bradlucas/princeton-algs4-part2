import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.DirectedCycle;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class WordNet {
    private final SAP sap;

    private final Map<Integer, String> idToSynset;
    private final Map<String, Set<Integer>> nounToIds;

    /**
     * Constructor takes the name of the two input files.
     */
    public WordNet(String synsets, String hypernyms) {  
        // java.lang.IllegalArgumentException
        if (null == synsets || null == hypernyms) {
            throw new IllegalArgumentException("Null input to constructor");
        }

        // read the input files
        idToSynset = new HashMap<>();
        nounToIds = new HashMap<>();
        initIdToSynset(synsets);

        Digraph graph = initHypernyms(hypernyms);

        // ensure argument corresponds to a rooted DAG
        DirectedCycle cycle = new DirectedCycle(graph);
        if (cycle.hasCycle() || !rootedDAG(graph)) {
            throw new IllegalArgumentException("Input does not correspond to a rooted DAG");
        }
        sap = new SAP(graph);
    }

    private boolean rootedDAG(Digraph graph) {
        // look through the graph and return true if only one node has a next
        int numRoots = 0;

        for (int i = 0; i < graph.V(); i++) {

            // if no adj()...
            if (!graph.adj(i).iterator().hasNext()) {
                numRoots++;
                // if we found more than one stop
                if (numRoots > 1) {
                    return false;
                }
            }
        }
        return numRoots == 1;
    }


    /**
     * The file synsets.txt lists all the (noun) synsets in WordNet. 
     * The first field is the synset id (an integer), the second field is 
     * the synonym set (or synset), and the third field is its dictionary definition (or gloss).
     * For example, the line
     * 36,AND_circuit AND_gate,a circuit in a computer that fires only when all of its inputs fire
     */
    private void initIdToSynset(String filename) {
        In file = new In(filename);
        while (file.hasNextLine()) {
            String[] line = file.readLine().split(",");

            Integer id = Integer.valueOf(line[0]);
            String n = line[1];

            idToSynset.put(id, n);

            String[] nouns = n.split(" ");
            for (String noun : nouns) {
                Set<Integer> ids = nounToIds.get(noun);
                if (null == ids) {
                    ids = new HashSet<>();
                }
                ids.add(id);
                nounToIds.put(noun, ids);
            }
        }
    }

    /**
     * The file hypernyms.txt contains the hypernym relationships: 
     * The first field is a synset id; subsequent fields are the id 
     * numbers of the synset's hypernyms. 
     * For example:
     * 164,21012,56099
     * Digraph:
     * see https://algs4.cs.princeton.edu/code/edu/princeton/cs/algs4/Digraph.java.html
    */
    private Digraph initHypernyms(String filename) {
        Digraph graph = new Digraph(idToSynset.size());
        // first value is the synset id
        // subsequent ids are the synset's hypernyms
        In file = new In(filename);
        while (file.hasNextLine()) {
            String[] line = file.readLine().split(",");
            Integer synsetId = Integer.valueOf(line[0]);
            for (int i = 1; i < line.length; i++) {
                Integer id = Integer.valueOf(line[i]);
                graph.addEdge(synsetId, id);
            }
        }
        return graph;
    }
    

    
    // returns all WordNet nouns
    public Iterable<String> nouns() {
        return nounToIds.keySet();
    }
       
    public boolean isNoun(String word) {                 // is the word a WordNet noun?
        if (null == word) {
            throw new IllegalArgumentException("isNoun was passed null");
        }
        if ("".equals(word)) {
            return false;
        }

        return nounToIds.containsKey(word);
    }
    
    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {
        if (null == nounA || null == nounB) {
            throw new IllegalArgumentException("distance was passed null");
        }

        // ensure nouns are WordNet nouns
        if (!isNoun(nounA) || !isNoun(nounB)) {
            throw new IllegalArgumentException("distance arguments are not both WordNet nouns");
        }

        Set<Integer> idsA = nounToIds.get(nounA);
        Set<Integer> idsB = nounToIds.get(nounB);

        return sap.length(idsA, idsB);
    }

    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        if (null == nounA || null == nounB) {
            throw new IllegalArgumentException("sap was passed a null");
        }
        
        // ensure nouns are WordNet nouns
        if (!isNoun(nounA) || !isNoun(nounB)) {
            throw new IllegalArgumentException("sap arguments are not both WordNet nouns");
        }

        Set<Integer> idsA = nounToIds.get(nounA);
        Set<Integer> idsB = nounToIds.get(nounB);
        
        int ancestor = sap.ancestor(idsA, idsB);
        return idToSynset.get(ancestor);
    }

    
    public static void main(String[] args) {   // do unit testing of this class
        // Need at least the two input files
        if (args.length < 2) {
            throw new IllegalArgumentException("Need at least the two files to process");
        }
        
        // take two files form command line
        WordNet wn = new WordNet(args[0], args[1]);

        // read stdin for word pairs
        while (!StdIn.isEmpty()) {
            String v = StdIn.readString();
            String w = StdIn.readString();

            // valid word?
            if (!wn.isNoun(v)) {
                StdOut.println("The word " + v + " is not in the word net");
                continue;
            }
            if (!wn.isNoun(w)) {
                StdOut.println("The word " + w + " is not in the word net");
                continue;
            }
            int dist = wn.distance(v, w);
            String ancestor = wn.sap(v, w);
            StdOut.printf("Distance is %d, ancestor is %s\n", dist, ancestor);
        }
    }
    
}

