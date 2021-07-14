/* Hand.java
 * Represents a player's hand in game
 */

package uno;

import javax.swing.*;
import java.awt.*;

public class Hand extends Deck {

    private DrawPile drawPile;
    private DiscardPile discardPile;
    private GameGUI gameGUI;
    private int startIndex;             //indicates the index to start displaying cards
    private Card selected;              //card that was clicked on by mouse, making it ready to play (for human player)
    private final int maxCards;         //indicates the maximum amount of cards to be displayed
    private final int displayAngle;     //angle in which cards in this hand should be displayed (0, 90, 180, 270)
    private final int gridx;            //x axis grid location of this panel in GridBagLayout
    private final int gridy;            //y axis grid location of this panel in GridBagLayout

    Hand(DrawPile drawPile, DiscardPile discardPile, GameGUI gameGUI, int maxCards, int displayAngle,
         int gridx, int gridy) {

        this.drawPile = drawPile;
        this.discardPile = discardPile;
        this.gameGUI = gameGUI;
        startIndex = 0;
        selected = null;
        this.maxCards = maxCards;
        this.displayAngle = displayAngle;
        this.gridx = gridx;
        this.gridy = gridy;
        if(displayAngle == 0 || displayAngle == 180)
            setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
    }

    //Starts this hand with 7 cards
    @Override
    public void createDeck() {
        for(int i = 0; i < 7; i++) {
            Card card = drawPile.draw();
            cards.addLast(card);
        }
    }

    /* Cards in indices ranging from startIndex up until the sum of startIndex and maxCards are displayed
     * as long as the index is not out of bounds
     * The layout is different for the left and right hands (90 and 270)
     * because their cards are displayed in a top to down manner
     */
    public void displayCards(boolean front) {
        JPanel cardsPanel = new JPanel();
        if(displayAngle == 90 || displayAngle == 270)
            cardsPanel.setLayout(new BoxLayout(cardsPanel, BoxLayout.PAGE_AXIS));
        for(int i = startIndex; i < startIndex + maxCards; i++) {
            if(i < cards.size()) {
                Card card = cards.get(i);
                card.setCardIcon(front, displayAngle);
                cardsPanel.add(card);
            }
        }
        add(cardsPanel);
    }

    /* Adds this panel to the frame
     * removeAll() is called because the GUI is redisplayed with updated components at least once per turn
     */
    public void addToFrame(JFrame frame) {
        removeAll();
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = gridx;
        c.gridy = gridy;
        frame.add(this, c);
    }

    /* Displays the player label
     * Displays the number of cards in this hand
     */
    public void displayInfo(int playerLabel) {
        JLabel name = new JLabel("Player " + (playerLabel));
        JLabel cardInfo = new JLabel("Number of cards: " + cards.size());
        JPanel namePanel = new JPanel();
        JPanel infoPanel = new JPanel();
        JPanel nameAndInfoPanel = new JPanel();
        nameAndInfoPanel.setLayout(new BoxLayout(nameAndInfoPanel, BoxLayout.PAGE_AXIS));
        if(displayAngle == 90 || displayAngle == 180)
            add(nameAndInfoPanel);
        else
            add(nameAndInfoPanel, 0);   //in the bottom and right hands, the info must be placed before cards
        namePanel.add(name);
        infoPanel.add(cardInfo);
        nameAndInfoPanel.add(namePanel);
        nameAndInfoPanel.add(infoPanel);
    }

    //Increments the start index as long as the incrementation doesn't prevent the display of maximum cards on screen
    public void increaseStartIndex() {
        if(startIndex < cards.size() - maxCards) {
            startIndex += 1;
        }
    }

    //Decreases the start index if it is greater than 0
    public void decreaseStartIndex() {
        if(startIndex > 0) {
            startIndex -= 1;
        }
    }

    /* Checks if a wild draw four card is playable
     * This method was not implemented in the Card class because the check for the playability of a
     * Wild Draw Four card is solely based on the playability of other cards in the hand
     * If any card in the hand matches colors with the top of the discard pile, then a Wild Draw Four is not playable
     * otherwise, it is playable
     */
    boolean isWildDrawFourPlayable(Card discard) {
        for(Card card : cards) {
            if(card.matchesColor(discard))
                return false;
        }
        return true;
    }

    /* Removes the card from this hand and places it in the discard pile
     * Plays the corresponding audio
     */
    void playCard(Card card) {
        discardPile.addCard(card);
        cards.remove(card);
        GameAudio.load(GameAudio.CARD_PLAY);
        GameAudio.play();
    }

    /* Draw card(s) from draw pile
     * Plays the corresponding audio
     */
    void drawCard(int num) {
        boolean didDraw = false;
        for(int i = 0; i < num; i++) {
            if(drawPile.getLength() != 0) {
                Card draw = drawPile.draw();
                cards.addLast(draw);
                didDraw = true;
            }
        }
        if(didDraw) {
            GameAudio.load(GameAudio.CARD_DRAW);    //loads the audio input stream with the Card draw sound
            GameAudio.play();
            gameGUI.startTimer();                   //re-renders screen contents to show that this player drew card(s)
        }
    }

    void setSelected(Card selected) {
        this.selected = selected;
    }

    Card getSelected() {
        return selected;
    }
}