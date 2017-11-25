import edu.princeton.cs.algs4.BreadthFirstDirectedPaths;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.List;


/*
 * Shortest Ancestral PAth
 */

/**
 * All methods should throw a java.lang.IllegalArgumentException if any argument is null or 
 * if any argument vertex is invalid—not between 0 and G.V() - 1.
 **/
public class SAP {
    private final Digraph graph;

    
    public SAP(Digraph G) { // constructor takes a digraph (not necessarily a DAG)
        if (null == G) {
            throw new IllegalArgumentException("Null input to constructor");
        }
        graph = new Digraph(G);
        
    }


    private boolean validIndex(int idx) {
        if (idx < 0 || idx >= graph.V()) {
            return false;
        }
        return true;
    }

    private boolean validIndex(Iterable<Integer> vertices) {
        for (Integer v : vertices) {
            if (!validIndex(v)) {
                return false;
            }
        }
        return true;
    }
    
    
    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        if (!validIndex(v) || !validIndex(w)) {
            throw new IllegalArgumentException("Invalid indexe passed to length");
        }
         
        Processor p = new Processor(v, w);
        return p.distance;
    }
    
    // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
    public int ancestor(int v, int w) {
        if (!validIndex(v) || !validIndex(w)) {
            throw new IllegalArgumentException("Invalid indexe passed to ancestor");
        }

        Processor p = new Processor(v, w);
        return p.ancestor;
    }
    
    // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        if (!validIndex(v) || !validIndex(w)) {
            throw new IllegalArgumentException("Invalid indexe passed to length");
        }

        Processor p = new Processor(v, w);
        return p.distance;
    }

    // a common ancestor that participates in shortest ancestral path; -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        if (!validIndex(v) || !validIndex(w)) {
            throw new IllegalArgumentException("Invalid indexe passed to ancestor");
        }

        Processor p = new Processor(v, w);
        return p.ancestor;
    }

    // do unit testing of this class
    public static void main(String[] args) {
        In in = new In(args[0]);
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);
        while (!StdIn.isEmpty()) {
            int v = StdIn.readInt();
            int w = StdIn.readInt();
            int length   = sap.length(v, w);
            int ancestor = sap.ancestor(v, w);
            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        }
    }

    private class Processor {
        int ancestor;
        int distance;
        
        public Processor(int v, int w) {
            BreadthFirstDirectedPaths pathsA = new BreadthFirstDirectedPaths(graph, v);
            BreadthFirstDirectedPaths pathsB = new BreadthFirstDirectedPaths(graph, w);
            process(pathsA, pathsB);
        }

        public Processor(Iterable<Integer> v, Iterable<Integer> w) {
            BreadthFirstDirectedPaths pathsA = new BreadthFirstDirectedPaths(graph, v);
            BreadthFirstDirectedPaths pathsB = new BreadthFirstDirectedPaths(graph, w);
            process(pathsA, pathsB);
        }

        private void process(BreadthFirstDirectedPaths pathsA, BreadthFirstDirectedPaths pathsB) {
            List<Integer> ancestors = new ArrayList<>();

            for (int i = 0; i < graph.V(); i++) {
                if (pathsA.hasPathTo(i) && pathsB.hasPathTo(i)) {
                    ancestors.add(i);
                }
            }

            int shortestPathAncestor = -1;
            int minDistance = Integer.MAX_VALUE;
            for (int ancestor : ancestors) {
                int dist = pathsA.distTo(ancestor) + pathsB.distTo(ancestor);
                if (dist < minDistance) {
                    minDistance = dist;
                    shortestPathAncestor = ancestor;
                }
            }
            if (Integer.MAX_VALUE == minDistance) {
                distance = -1;
            } else {
                distance = minDistance;
            }
            ancestor = shortestPathAncestor;
        }
    }
        
}
