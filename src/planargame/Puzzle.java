/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package planargame;

import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

/**
 *
 * @author zachary
 */
public class Puzzle {
    private HashMap<Vertex,Edge> edges;
    private HashMap<String,Vertex> vertices;
    private Vertex context;
    private String conffile = "level";
    private String name;
    private String bgimage;
    private int goal;
    public Dimension preferredDimension;

    public Puzzle(Vertex context, int sequence) throws
            PuzzleException {
        this.context = context;
        this.conffile += sequence;
        edges = new HashMap<Vertex,Edge>();
        vertices = new HashMap<String,Vertex>();
        if (PlanarFrame.DEBUG_LOAD_PUZZ) {
            System.out.println("About to load the puzzle in the file "
                    + conffile);
        }
        load();
        if (PlanarFrame.DEBUG_LOAD_PUZZ) {
            System.out.println("Loaded the puzzle.");
        }
    }

    public static void describeLoadLine(String[] dequoted) {
        String describe = "";
        for (int i = 0; i < dequoted.length; i++) {
            describe += dequoted[i] + "[" + dequoted[i].length()
                    + "]|";
        }
        describe = describe.substring(0, describe.length() - 1);
        System.out.println(describe);
    }

    public int getGoal() {
        return goal;
    }

    public String getBackground() {
        return bgimage;
    }

    private void load() throws PuzzleException {
        InputStream in = this.getClass().getResourceAsStream(conffile);
        if (PlanarFrame.DEBUG_LOAD_PUZZ) {
            System.out.println("Got a stream for the resource "
                    + conffile);
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        if (PlanarFrame.DEBUG_LOAD_PUZZ) {
            if (br == null) {
                System.out.println("Uh-oh! The stream couldn't be read.\n"
                        + "Perhaps the file is empty?");
            } else {
                System.out.println("And then, got a BufferedReader for it.");
            }
        }
        String line;
        try {
            line = br.readLine();
        } catch (IOException ex) {
            throw new PuzzleException("Tried to load an empty puzzle.");
        }
        int lineno = 0;
        while (line != null) {
            if (PlanarFrame.DEBUG_LOAD_PUZZ) {
                System.out.println("Parsing line " + lineno + ":\n"
                        + line);
            }
            try {
                line = line.trim();
                if (line.startsWith("planarPuzzle ")) {
                    if (PlanarFrame.DEBUG_LOAD_PUZZ) {
                        System.out.println("Parsing the head line.");
                    }
                    parseHead(line);
                } else if (line.startsWith("edge ")) {
                    if (PlanarFrame.DEBUG_LOAD_PUZZ) {
                        System.out.println("Parsing an edge.");
                    }
                    parseEdge(line);
                } else if (line.startsWith("vertex ")) {
                    if (PlanarFrame.DEBUG_LOAD_PUZZ) {
                        System.out.println("Parsing a vertex.");
                    }
                    this.parseVert(line);
                } else if (!(line.length() == 0 || line.startsWith("#"))) {
                    throw new PuzzleFormatException();
                }
                line = br.readLine();
                lineno++;
            } catch (PuzzleFormatException ex) {
                System.out.println("Line " + lineno + " could not be parsed.\n"
                        + ex);
                break;
            } catch (IOException ex) {
                System.out.println("Reached the end of the puzzle.");
                break;
            }
        }
    }

    public String getName() {
        return name;
    }

    public Vertex getVertexByName(String name) {
        return vertices.get(name);
    }
    
    public Edge getEdgeByVertex(Vertex v) {
        return edges.get(v);
    }

    public int getWidth() {
        return this.preferredDimension.width;
    }

    public int getHeight() {
        return this.preferredDimension.height;
    }

    private void parseVert(String line) throws PuzzleFormatException {
        String[] tokens = cull(line.split("[ \t]+"));
        if (PlanarFrame.DEBUG_LOAD_PUZZ) {
            System.out.println("Parsing vertex...");
            describeLoadLine(tokens);
        }
        if (context == null && PlanarFrame.DEBUG_LOAD_PUZZ) {
            System.out.println("...without a context vertex?!");
        }
        if (tokens.length != 4) { // the word "vertex", its name, and coords
            throw new PuzzleFormatException("vertex lines take exactly three"
                    + "arguments. This one has " + tokens.length + ".\n"
                    + line);
        }
        Vertex tryme = context.make(Integer.parseInt(tokens[2]),
                    Integer.parseInt(tokens[3]), tokens[1]);
        try {
            edges.put(tryme, null);
            vertices.put(tryme.getName(), tryme);
            
        } catch (NumberFormatException ex) {
            throw new PuzzleFormatException("vertex lines must give the name,"
                    + "the x, and the y, in that order.\n"
                    + line);
        }
    }

    private void parseEdge(String line) throws PuzzleFormatException,
            PuzzleException {
        // Should contain exactly two vertices;
        // either or both might be the same as earlier vertices
        String[] tokens = cull(line.split("[ \t]+"));
        if (PlanarFrame.DEBUG_LOAD_PUZZ) {
            System.out.println("Parsing edge...");
            describeLoadLine(tokens);
        }
        if (context == null && PlanarFrame.DEBUG_LOAD_PUZZ) {
            System.out.println("No, wait, I don't have a context vertex. Sorry!");
        }
        Vertex start = null;
        Vertex end = null;

        //if there are three tokens, two are names.
        if (tokens.length != 3)  {
            throw new PuzzleFormatException("Edge lines must have three tokens."
                    + " This one has " + tokens.length + ".\n" + line);
        }
        String startName = tokens[1];
        String endName = tokens[2];
        start = this.getVertexByName(startName);
        if(start == null) {
            throw new PuzzleFormatException("No vertex by the name " + 
                    startName + ".");
                    }
        end = this.getVertexByName(endName);
        if(end == null) {
            throw new PuzzleFormatException("No vertex by the name " +
                    endName + ".");
        }

        

        Edge edge = new Edge(start, end);
        edges.put(start, edge);
        edges.put(end, edge);
        
        if (PlanarFrame.DEBUG_LOAD_PUZZ) {
            System.out.println("Instantiated the edge!");
        }

    }

    public static String[] trimAll(String[] strs) {
        String[] result = new String[strs.length];
        for (int i = 0; i < strs.length; i++) {
            result[i] = strs[i].trim();
        }
        return result;
    }

    public static int numBlanks(String[] strs) {
        int blanks = 0;
        for (int i = 0; i < strs.length; i++) {
            if (strs[i].trim().isEmpty()) {
                blanks++;
            }
        }
        return blanks;
    }

    public static int numNulls(String[] strs) {
        int nulls = 0;
        for (int i = 0; i < strs.length; i++) {
            if (strs[i] == null) {
                nulls++;
            }
        }
        return nulls;
    }

    public static String[] nonnulls(String[] strs) {
        String[] result = new String[strs.length - numNulls(strs)];
        int o = 0;
        for (int i = 0; i < strs.length; i++) {
            if (strs[i] != null) {
                result[o] = strs[i];
                o++;
            }
        }
        return result;
    }

    public static String[] nonblanks(String[] strs) {
        String[] result = new String[strs.length - numBlanks(strs)];
        int o = 0;
        for (int i = 0; i < strs.length; i++) {
            if (!strs[i].trim().isEmpty()) {
                result[o] = strs[i];
                o++;
            }
        }
        return result;
    }

    public static String[] cull(String[] args) {
        return trimAll(nonblanks(nonnulls(args)));
    }

    private void parseHead(String line) throws PuzzleFormatException,
            PuzzleException {
        //"puzzle name" puzzleSetName xDimension yDimension
        if (preferredDimension != null) {
            throw new PuzzleFormatException("The puzzle seems to have two headers.");
        }
        Dimension panelSize;
        String[] dequoted = cull(line.split("\""));
        String[] parsed = new String[5];
        parsed[0] = dequoted[1];
        String[] parsing;
        parsing = dequoted[2].split(" ");
        if (parsing.length != 4) {
            throw new PuzzleException("This puzzle header has the wrong "
                    + "number of arguments (" + parsing.length + 1 + ").\n"
                    + line);
        }
        parsed[1] = parsing[0];
        parsed[2] = parsing[1];
        parsed[3] = parsing[2];
        parsed[4] = parsing[3];


        //parsed must contain:
        //the puzzle's name
        // the preferred dimensions, x and y
        // the goal, an integer
        // For simplicity I will assume it's in that order.


        try {
            panelSize = new Dimension(Integer.parseInt(parsed[1]),
                    Integer.parseInt(parsed[2]));
        } catch (Exception ex) {
            throw new PuzzleException("Puzzle dimensions couldn't be "
                    + "converted to integers: " + parsed[1] + ", "
                    + parsed[2]);
        }

        int pG;
        try {
            pG = Integer.parseInt(parsed[4]);
            if(pG <= 0) throw new Exception();
        } catch (Exception ex) {
            throw new PuzzleException("Puzzle doesn't have a sensible goal. "
                    + parsed[4] + " should be an integer greater than zero.");
        }

        this.preferredDimension = panelSize;
        this.name = parsed[0];
        this.goal = pG;
        this.bgimage = parsed[3];
    }


    public int countOverlaps() {
        int overlaps = 0;
        Edge checking;
        Collection<Edge> es = edges.values();
        LinkedList<Edge> unchecked = new LinkedList<Edge>(es);
        LinkedList<Edge> checked = new LinkedList<Edge>();
        while(!unchecked.isEmpty()) {
            checking = unchecked.remove();
            if (checked.contains(checking)) {
                continue;
            }
            if (checking.overlapsAny(unchecked)) {
                overlaps++;
            }
            checked.add(checking);
        }
        return overlaps;
    }
    
    public int numVerts() {
        return vertices.size();
    }
    
    public int numEdges() {
        //Every edge has exactly two vertices.
        return edges.size() / 2;
    }
    
    public Collection<Vertex> getVertCol() {
        return vertices.values();
    }
}
