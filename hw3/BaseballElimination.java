import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.In;

import java.util.HashMap;    


public class BaseballElimination {

    private final int numTeams;
    private final Bag<String> teamNames;

    private final HashMap<String, Integer> map;
    private final HashMap<Integer, String> teamNumName;
    
    private final int[] wins;
    private final int[] losses;
    private final int[] remaining;
    private final int [][] games;


    private final int inf;
    private int sum;
    private boolean possible;
    private int[][] gameVertices;
    private int[] teamVertices;
    
    public BaseballElimination(String filename) {  // create a baseball division from given filename in format specified below
        inf = 1000000000;
        
        // 4
        // Atlanta       83 71  8  0 1 6 1
        // Philadelphia  80 79  3  1 0 0 2
        // New_York      78 78  6  6 0 0 0
        // Montreal      77 82  3  1 2 0 0

        // Read first line and get number of teams
        teamNames = new Bag<String>();
        teamNumName = new HashMap<Integer, String>();
        map = new HashMap<String, Integer>();

        
        In in = new In(filename);

        String line = in.readLine();
        numTeams = Integer.parseInt(line);
        
        wins = new int[numTeams];
        losses = new int[numTeams];
        remaining = new int[numTeams];
        games = new int[numTeams][numTeams];

        gameVertices = new int[numTeams][numTeams];
        teamVertices = new int[numTeams];

        // read the rest of the lines
        int i = 0;
        while ((line = in.readLine()) != null) {
            String trimmed = line.trim();
            String[] split = trimmed.split("\\s+");
            teamNumName.put(i, split[0]);
            teamNames.add(split[0]);
            
            // name | wins | loses | remaining | the number of remaining games against each team in the division (num teams wide)
            map.put(split[0], i);
            // StdOut.println(line);
            
            wins[i] = Integer.parseInt(split[1]);
            losses[i] = Integer.parseInt(split[2]);
            remaining[i] = Integer.parseInt(split[3]);
            
            for (int j = 0; j < numTeams; j++) {
                games[i][j] = Integer.parseInt(split[j+4]);
            }
            i++;
            
        }
    }
    
    public int numberOfTeams() {                   // number of teams
        return numTeams;
    }


    public Iterable<String> teams() {              // all teams
        return teamNames;
    }

    public int wins(String team) {                 // number of wins for given team
        if (!map.containsKey(team)) {
            throw new java.lang.IllegalArgumentException("Invalid team name");
        }
        return wins[map.get(team)];
    }
    
    public int losses(String team) {               // number of losses for given team
        if (!map.containsKey(team)) {
            throw new java.lang.IllegalArgumentException("Invalid team name");
        }
        return losses[map.get(team)];
    }
    
    public int remaining(String team) {              // number of remaining games for given team
        if (!map.containsKey(team)) {
            throw new java.lang.IllegalArgumentException("Invalid team name");
        }
        return remaining[map.get(team)];
    }

    public int against(String team1, String team2) { // number of remaining games between team1 and team2
        if (!map.containsKey(team1) || !map.containsKey(team2)) {
            throw new java.lang.IllegalArgumentException("Invalid team name");
        }
        return games[map.get(team1)][map.get(team2)];
    }
    
    public  boolean isEliminated(String team) {      // is given team eliminated?
        if (!map.containsKey(team)) {
            throw new java.lang.IllegalArgumentException("Invalid team name");
        }
        int id = map.get(team);
        if (triviallyEliminated(id)) {
            return true;
        }
        
        FordFulkerson maxflow = returnMaxFlow(team);
        if ((int) maxflow.value() == sum) {
            return false;
        } else {
            return true;
        }
    }

    private boolean triviallyEliminated(int id) {
        for (int i = 0; i < wins.length; i++) {
            if (i != id) {
                if (wins[id] + remaining[id] < wins[i]) {
                    return true;
                }
            }
        }
        return false;
    }

    public Iterable<String> certificateOfElimination(String team) {  // subset R of teams that eliminates given team; null if not eliminated
        if (!map.containsKey(team)) {
            throw new java.lang.IllegalArgumentException("Invalid team name");
        } 

        if (!isEliminated(team)) {
            StdOut.println("certificateOfElimination returnning null because isEliminated returned false");
            return null;
        } else {
            FordFulkerson maxflow = returnMaxFlow(team);
            Bag<String> subset = new Bag<String>();
            int teamId = map.get(team);
            for (int i = 0; i < numTeams; i++) {
                if (i != teamId) {
                    if (maxflow.inCut(teamVertices[i])) {
                        subset.add(teamNumName.get(i));
                    }
                }
            }
            return subset;
        }
    }

    private FordFulkerson returnMaxFlow(String team) {
        int teamID = map.get(team);
        int s = 0;
        int pos = 1;
        int n = numTeams, i, j;

        // Mapping of game vertices has to be correct
        for (i = 0; i < n; i++) {
            for (j = i + 1; j < n; j++) {
                gameVertices[i][j] = pos;
                gameVertices[j][i] = pos;
                pos++;
            }
        }

        // Mapping of team vertices has to be correct
        for (i = 0; i < n; i++) {
            teamVertices[i] = pos;
            pos++;
        }
        
        // Destination
        int t = pos;
        int numV = ((n * (n + 1)) / 2) + 2;
        
        FlowNetwork G = new FlowNetwork(numV);
        sum = 0;
        for (i = 0; i < n; i++) {
            for (j = i + 1; j < n; j++) {
               sum += games[i][j]; 
               pos = gameVertices[i][j];
               FlowEdge e = new FlowEdge(s, pos, games[i][j]);
               FlowEdge e1 = new FlowEdge(pos, teamVertices[i], inf);
               FlowEdge e2 = new FlowEdge(pos, teamVertices[j], inf);
               G.addEdge(e);
               G.addEdge(e1);
               G.addEdge(e2);
            }
        }
        
        int tot = wins[teamID]+remaining[teamID];
        for (i = 0; i < n; i++) {
            if (tot > wins[i]) {
                FlowEdge e = new FlowEdge(teamVertices[i], t, tot - wins[i]);
                G.addEdge(e);
            }    
            else {
                possible = false;
            }    
            
        }
        FordFulkerson maxflow = new FordFulkerson(G, s, t);
        return maxflow;
    }


    public static void main(String[] args) {
        BaseballElimination division = new BaseballElimination(args[0]);
        for (String team : division.teams()) {
            if (division.isEliminated(team)) {
                StdOut.print(team + " is eliminated by the subset R = { ");
                for (String t : division.certificateOfElimination(team)) {
                    StdOut.print(t + " ");
                }
                StdOut.println("}");
            }
            else {
                StdOut.println(team + " is not eliminated");
            }
        }
    }

}
    
