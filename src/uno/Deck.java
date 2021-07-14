/* Deck.java
 * Maintains the lists of cards in the game
 */

package uno;

import javax.swing.*;
import java.util.LinkedList;

public abstract class Deck extends JPanel {

    protected LinkedList<Card> cards;

    Deck() {
        cards = new LinkedList<>();
    }

    public abstract void createDeck();

    public LinkedList<Card> cards() {
        return cards;
    }

    //Adds a card to the list
    public void addCard(Card card) {
        cards.addLast(card);
    }

    //Removes and returns the last card in this list
    public Card draw() {
        return cards.removeLast();
    }

    //Returns the last card without removal
    public Card getLast() {
        if(cards.size() != 0)
            return cards.getLast();
        else
            return null;
    }

    //Removes all the cards from the list of cards
    public void removeCards() {
        for(int i = 0; i < cards.size(); i++) {
            cards.remove(i);
        }
    }

    //Size of the list of cards
    public int getLength() {
        return cards.size();
    }

    //test method: prints all cards in a deck
    public void printCards() {
        for(int i = 0; i < cards.size(); i++) {
            System.out.println(i + 1 + ". " + cards.get(i).getCardColor().toString() + " " + cards.get(i).getNumber() + " " + cards.get(i).getCardAction().toString());
        }
        System.out.println();
    }
}