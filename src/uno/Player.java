/* Player.java
 * Represents a player in an UNO game
 * Not a visible game object
 */

package uno;

public abstract class Player {

    protected Hand hand;
    protected DiscardPile discardPile;
    private boolean drawn;              //true if the player has drawn a card during this round
    protected boolean AI;               //whether the player is human or AI

    Player(Hand hand, DiscardPile discardPile) {
        this.hand = hand;
        this.discardPile = discardPile;
        drawn = false;
    }

    //player plays their turn (playing means either placing a card or passing if no cards are playable)
    public abstract boolean play();

    //picks a color for a wild card
    public abstract void pickColor();

    public Hand getHand() {
        return hand;
    }

    public void setDrawn(boolean drawn) {
        this.drawn = drawn;
    }

    public boolean isDrawn() {
        return drawn;
    }

    public boolean isAI() {
        return AI;
    }
}