/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vectorworld;

import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 *
 * @author zachary
 */
public class Puzzle {

    private ArrayList<Edge> edges;
    private ArrayList<Vertex> vertices;
    private String conffile;
    private String puzzleName = "";
    private String puzzleSet = "";
    public Dimension preferredDimension;

    public Puzzle(String conffile) throws
            PuzzleException {
        this.conffile = conffile;
        edges = new ArrayList<Edge>();
        vertices = new ArrayList<Vertex>();
        if (VectorWorld.DEBUG_LOAD_PUZZ) {
            System.out.println("About to load the puzzle in the file "
                    + conffile);
        }
        load();
        if (VectorWorld.DEBUG_LOAD_PUZZ) {
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

    private void load() throws PuzzleException {
        InputStream in = this.getClass().getResourceAsStream(conffile);
        if (VectorWorld.DEBUG_LOAD_PUZZ) {
            System.out.println("Got a stream for the resource "
                    + conffile);
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        if (VectorWorld.DEBUG_LOAD_PUZZ) {
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
            if (VectorWorld.DEBUG_LOAD_PUZZ) {
                System.out.println("Parsing line " + lineno + ":\n"
                        + line);
            }
            try {
                line = line.trim();
                if (line.startsWith("planarPuzzle ")) {
                    if (VectorWorld.DEBUG_LOAD_PUZZ) {
                        System.out.println("Parsing the head line.");
                    }
                    parseHead(line);
                } else if (line.startsWith("edge ")) {
                    if (VectorWorld.DEBUG_LOAD_PUZZ) {
                        System.out.println("Parsing an edge.");
                    }
                    parseEdge(line);
                } else if(line.startsWith("vertex ")) {
                    if(VectorWorld.DEBUG_LOAD_PUZZ) {
                        System.out.println("Parsing a vertex.");
                    }
                    this.parseVert(line);
                }
                else if (!(line.length() == 0 || line.startsWith("#"))) {
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
        return puzzleName;
    }

    public Vertex getVertexByName(String name) {
        //It'd be nice to replace this with a real search algorithm.
        Vertex result;
        for (int i = 0; i < vertices.size(); i++) {
            result = vertices.get(i);
            if (result.isNamed(name)) {
                return result;
            }
        }
        return null;
    }
    
    public int getWidth() {
        return this.preferredDimension.width;
    }
    
    public int getHeight() {
        return this.preferredDimension.height;
    }

    private void parseVert(String line) throws PuzzleFormatException {
        String[] tokens = cull(line.split("[ \t]+"));
        if (VectorWorld.DEBUG_LOAD_PUZZ) {
            System.out.println("Parsing vertex...");
            describeLoadLine(tokens);
        }

        if (tokens.length != 4) { // the word "vertex", its name, and coords
            throw new PuzzleFormatException("vertex lines take exactly three"
                    + "arguments. This one has " + tokens.length + ".\n"
                    + line);
        }
        try {
        vertices.add(new Vertex(Integer.parseInt(tokens[2]), 
                Integer.parseInt(tokens[3]), tokens[1]));
        } catch(NumberFormatException ex) {
            throw new PuzzleFormatException("vertex lines must give the name,"
                    + "the x, and the y, in that order.\n" +
                    line);
        }
    }

    private void parseEdge(String line) throws PuzzleFormatException,
            PuzzleException {
        // Should contain exactly two vertices;
        // either or both might be the same as earlier vertices
        String[] tokens = cull(line.split("[ \t]+"));
        if (VectorWorld.DEBUG_LOAD_PUZZ) {
            System.out.println("Parsing edge...");
            describeLoadLine(tokens);
        }
        Vertex start = null;
        Vertex end = null;

        //If there are five tokens, four of them are coordinates.
        if (tokens.length == 5) {
            start = new Vertex(Integer.parseInt(tokens[1]),
                    Integer.parseInt(tokens[2]));
            end = new Vertex(Integer.parseInt(tokens[3]),
                    Integer.parseInt(tokens[4]));
        }
        //If there are four tokens, two are coordinates, a third is a name.
        if (tokens.length == 4) {
            int x, y;
            String name;
            try {
                x = Integer.parseInt(tokens[1]);
                y = Integer.parseInt(tokens[2]);
                name = tokens[3];
                start = new Vertex(x, y);
                end = this.getVertexByName(name);
            } catch (NumberFormatException ex) {
                name = tokens[1];
                x = Integer.parseInt(tokens[2]);
                y = Integer.parseInt(tokens[3]);
                start = this.getVertexByName(name);
                end = new Vertex(x, y);
            }
            //tokens[2] can't be the name. The coordinates have to appear
            // consecutively.
        }

        //if there are three tokens, two are names.
        if (tokens.length == 3) {
            String startName = tokens[1];
            String endName = tokens[2];
            start = this.getVertexByName(startName);
            end = this.getVertexByName(endName);
        }

        // avoid adding any redundant vertices or edges
        boolean startAdded = false;
        boolean endAdded = false;
        boolean edgeAdded = false;
        if (vertices == null && VectorWorld.DEBUG_LOAD_PUZZ) {
            System.out.println("There's no place to put these vertices!");
            System.exit(1);
        }
        if (start == null || end == null) {
            throw new PuzzleFormatException("This line doesn't have enough "
                    + "vertices:\n" + line);
        }
        if (vertices.contains(start)) {
            if (VectorWorld.DEBUG_LOAD_PUZZ) {
                System.out.println("Not adding the start vertex to the puzzle, "
                        + "because it's already there.");
            }
            int idx = vertices.indexOf(start);
            start = vertices.get(idx);
            startAdded = true;
        } else {
            startAdded = vertices.add(start);
            if (VectorWorld.DEBUG_LOAD_PUZZ) {
                System.out.println("Added the start vertex to the puzzle!");
            }
        }
        if (vertices.contains(end)) {
            if (VectorWorld.DEBUG_LOAD_PUZZ) {
                System.out.println("Not adding the end vertex to the puzzle, "
                        + "because it's already there.");
            }
            int idx = vertices.indexOf(end);
            end = vertices.get(idx);
            endAdded = true;
        } else {
            endAdded = vertices.add(end);
            if (VectorWorld.DEBUG_LOAD_PUZZ) {
                System.out.println("Added the end vertex to the puzzle!");
            }
        }

        Edge edge = new Edge(start, end);
        if (VectorWorld.DEBUG_LOAD_PUZZ) {
            System.out.println("Instantiated the edge!");
        }
        if (edges.contains(edge)) {
            if (VectorWorld.DEBUG_LOAD_PUZZ) {
                System.out.println("But didn't add it--it was already there.");
            }
            edgeAdded = true;
        } else {
            edgeAdded = edges.add(edge);
            if (VectorWorld.DEBUG_LOAD_PUZZ) {
                System.out.println("Added the edge to the puzzle!");
            }
        }

        if (!(startAdded && endAdded && edgeAdded)) {
            String err = "In the puzzle file: " + conffile
                    + "On the line: " + line
                    + "Failed to parse: ";
            if (!startAdded) {
                err += "start ";
            }
            if (!endAdded) {
                err += "end ";
            }
            if (!edgeAdded) {
                err += "edge ";
            }

            throw new PuzzleFormatException(err);
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
        String xDimension = "";
        String yDimension = "";
        String pN = "";
        String pS = "";
        String[] tokens;
        Dimension panelSize;
        String[] dequoted = cull(line.split("\""));
        //If there are two strings in dequoted, the puzzle is nameless.
        //That's fine.
        if (dequoted.length == 2) {
            tokens = cull(line.split("[ \t]+"));
            if (tokens.length == 4) {
                //The puzzle is part of a set. Remember this.
                pS = tokens[1].trim();
                xDimension = tokens[2].trim();
                yDimension = tokens[3].trim();
            } else if (tokens.length == 3) {
                xDimension = tokens[1].trim();
                yDimension = tokens[2].trim();
            } else {
                throw new PuzzleException("Puzzle's header is invalid.");
            }
        } else if (dequoted.length == 3) {
            //If there are exactly three strings in dequoted,
            //the puzzle has a name.
            pN = dequoted[1];
            tokens = cull(dequoted[2].split("[ \t]+"));

            if (tokens.length == 4) {
                //The puzzle is part of a set. Remember this.
                pS = tokens[1].trim();
                xDimension = tokens[2].trim();
                yDimension = tokens[3].trim();
            } else if (tokens.length == 3) {
                xDimension = tokens[1].trim();
                yDimension = tokens[2].trim();
            } else {
                throw new PuzzleException("Puzzle's header is invalid.");
            }
        } else {
            throw new PuzzleException("Puzzle's header is too long.");
        }

        try {
            panelSize = new Dimension(Integer.parseInt(xDimension),
                    Integer.parseInt(yDimension));
        } catch (Exception ex) {
            throw new PuzzleException("Puzzle dimensions couldn't be "
                    + "converted to integers: " + xDimension + ", "
                    + yDimension);
        }
        this.preferredDimension = panelSize;
        this.puzzleName = pN;
        this.puzzleSet = pS;
    }

    public Edge getEdge(int idx) {
        return edges.get(idx);
    }

    public Vertex getStartVert(int idx) {
        return edges.get(idx).getStart();
    }

    public Vertex getEndVert(int idx) {
        return edges.get(idx).getEnd();
    }

    public Vertex getVert(int idx) {
        return vertices.get(idx);
    }

    public int numEdges() {
        return edges.size();
    }

    public int numVerts() {
        return vertices.size();
    }


    public int countOverlaps() {
        if(VectorWorld.DEBUG_OLAP) {
            System.out.println("I'm counting the overlaps in the puzzle.");
        }
        int overlaps = 0;
        Edge checking;
        ArrayList<Edge> unchecked = (ArrayList<Edge>) edges.clone();
        for(int i=0;i<unchecked.size();i++) {
            checking = unchecked.get(i);
            unchecked.remove(i);
            if(checking.overlapsAny(unchecked)) {
                overlaps++;
            }
        }
        return overlaps;
    }
}
