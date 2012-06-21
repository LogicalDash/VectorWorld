/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package planargame;

/**
 *
 * @author sanotehu
 */
public class Actor {
        /*
     * Actors exist in one location at a time.
     * A location may be a vertex or an edge.
     * If it's an edge, the character is traveling along it.
     * They make progress at a rate in ticks-per-percent
     * given by the edge's weight, minimum 1.
     * 
     * For now, I will move the character by clicking on a vertex
     * connected to the one they're in right now.
     * Otherwise the character will stay put.
     */
    SpriteMap sprites;
    String displayName;
    Vertex startLoc;
    Actor(String displayName, Vertex startLoc, SpriteMap sprites) {
        /*
         * Each actor is represented by one or more sprites.
         * Each sprite may have special significance, but in any case I'll
         * store the lot of them in a hash map specific to the character.
         */
        this.sprites = sprites;
        this.displayName = displayName;
        this.startLoc = startLoc;
    }
}
