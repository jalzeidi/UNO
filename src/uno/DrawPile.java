/* DrawPile.java
 * Creates all the cards in the game
 * Serves as the pile of cards that players draw from during the game
 */

package uno;

import javax.swing.*;
import java.util.Random;

public class DrawPile extends Deck {
    @Override
    public void createDeck() {
        //colored cards
        CardColor[] colors = CardColor.values();
        for(int i = 0; i < 4; i++) {
            cards.addLast(new Card(colors[i], 0, CardAction.NONE));
            for(int j = 1; j < 10; j++) {
                cards.addLast(new Card(colors[i], j, CardAction.NONE));
                cards.addLast(new Card(colors[i], j, CardAction.NONE));
            }
            for(int j = 0; j < 2; j++) {
                cards.addLast(new Card(colors[i], -1, CardAction.DRAW2));
            }
            for(int j = 0; j < 2; j++) {
                cards.addLast(new Card(colors[i], -1, CardAction.SKIP));
            }
            for(int j = 0; j < 2; j++) {
                cards.addLast(new Card(colors[i], -1, CardAction.REVERSE));
            }
        }
        //wild cards
        for(int j = 0; j < 4; j++) {
            cards.addLast(new Card(CardColor.WILD, -1, CardAction.NONE));
        }
        //wild draw 4 cards
        for(int j = 0; j < 4; j++) {
            cards.addLast(new Card(CardColor.WILD, -1, CardAction.DRAW4));
        }
    }

    public void shuffleCards() {
        Random rand = new Random();
        int r;                                   //random number
        for(int i = 0; i < cards.size(); i++) {
            r = rand.nextInt(cards.size());      //generate random number between 0 and the size of the list
            swap(i, r);                          //swap cards at current index with cards at random index (r)
        }
    }

    /* Displays the last(top) of the draw pile
     * The removeAll() call is made because components are replaced at least once every turn
     */
    public void renderCards(JPanel panel) {
        removeAll();
        if(cards.size() != 0) {
            Card last = cards.getLast();
            last.setCardIcon(false, 0);
            add(last);
            panel.add(this);
        }
    }

    //swap cards in two different indices
    private void swap(int i, int j) {
        Card temp;
        temp = cards.get(i);
        cards.set(i, cards.get(j));
        cards.set(j, temp);
    }
}