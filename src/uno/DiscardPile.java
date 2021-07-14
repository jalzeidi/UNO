/* DiscardPile.java
 * Players place their cards on this pile
 */

package uno;

import javax.swing.*;

public class DiscardPile extends Deck {

    private DrawPile drawPile;

    DiscardPile(DrawPile drawPile) {
        this.drawPile = drawPile;
    }

    //Starts the discard pile from the last card in the draw pile
    @Override
    public void createDeck() {
        Card card = drawPile.draw();
        cards.addLast(card);
    }

    /* Displays the last(top) card
     * The removeAll() call is made because components are replaced at least once every turn
     */
    public void renderCards(JPanel panel) {
        removeAll();
        if(cards.size() != 0) {
            Card last = cards.getLast();
            last.setCardIcon(true,0);
            add(last);
            panel.add(this);
        }
    }

    /* Returns all the cards to the draw pile
     * This method is used when the draw pile is depleted mid-game
     */
    public void returnCards() {
        for(Card card : cards) {
            drawPile.addCard(card);
            cards.remove(card);
        }
    }
}