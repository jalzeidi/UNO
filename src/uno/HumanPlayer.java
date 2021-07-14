/* HumanPlayer.java
 * Represents a human player in a game of UNO
 * The purpose of this class is to listen to action events that enable the player to take their turn within UNO rules
 */

package uno;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

public class HumanPlayer extends Player implements ActionListener {

    private DrawPile drawPile;
    private Game game;
    private GameGUI gameGUI;
    private ColorSelector colorSelector;
    private boolean turn;                   //this value is used to know when to detect button clicks
    private boolean played;

    HumanPlayer(Hand hand, DiscardPile discardPile, DrawPile drawPile, Game game,
                GameGUI gameGUI, ColorSelector colorSelector) {
        super(hand, discardPile);
        this.drawPile = drawPile;
        this.game = game;
        this.gameGUI = gameGUI;
        this.colorSelector = colorSelector;
        turn = false;
        played = false;
        AI = false;
    }

    public boolean play() {
        turn = true;            //sets turn to true to allow listening for events
        game.sleepThread();     //sleeps the thread until player makes an action
        turn = false;           //ends the turn to disable listening for events
        return played;          //returns true if the player played a card, false if player passed
    }


    public void pickColor() {
        gameGUI.startTimer();           //refreshes the GUI to show that a wild card was played
        colorSelector.startTimer();     //displays the color selector dialog
        game.sleepThread();             //sleeps the thread until the player chooses a color
    }

    /* Adds a listener to the pass button
     * Adds a listener to each card in the draw pile
     * This method must be called before cards are distributed from the draw pile
     * to ensure that there is a listener for every card in the game.
     */
    void addListeners() {
        LinkedList<Card> cards = drawPile.cards();
        for(Card card : cards) {
            card.addActionListener(this);
        }
        gameGUI.getPass().addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(turn && game.isRunning()) {
            Hand hand = getHand();
            if(e.getSource() == discardPile.getLast())
                discardClicked(hand);
            else if (e.getSource() == drawPile.getLast())
                drawClicked(hand);
            else if (e.getSource() == gameGUI.getPass())
                passClicked();
            else
                cardClicked(e, hand);
        }
    }

    /* When the discard pile is clicked and there is a selected card, the playability of that card is checked
     * If the card is playable, it is placed in the discard pile and the thread is resumed
     * If the card is NOT playable, a message is displayed prompting the user to select a valid card
     */
    private void discardClicked(Hand hand) {
        if(hand.getSelected() != null) {
            Card card = hand.getSelected();
            Card discard = discardPile.getLast();
            boolean playable;
            if(card.getCardAction().equals(CardAction.DRAW4))
                playable = hand.isWildDrawFourPlayable(discard);
            else
                playable = card.isPlayableOn(discard);
            if(playable) {
                hand.playCard(card);
                hand.decreaseStartIndex();
                played = true;
                game.countDown();
            }
            else {
                hand.setSelected(null);
                showMessage("Select a valid card");
            }
        }
    }

    //If the top card of the draw pile is clicked and the player has not drawn a card this round a card is drawn.
    private void drawClicked(Hand hand) {
        if(!isDrawn()) {
            hand.drawCard(1);
            setDrawn(true);
        }
        else
            showMessage("Unable to draw more than once per turn");
    }

    /* When a card in the hand is clicked, it is highlighted if it meets one of the two qualifying conditions
     * 1. The player has not drawn a card this round
     * 2. The player has drawn a card this round, but the card that is clicked is the card that was drawn
     * If the card that was clicked does not meet either condition, a message is displayed to guide the player
     */
    private void cardClicked(ActionEvent e, Hand hand) {
        for(Card card : hand.cards()) {
            if(e.getSource() == card) {
                if(!isDrawn() || card.equals(hand.getLast()))
                    hand.setSelected(card);
                else
                    showMessage("Must select the card that was drawn this round");
            }
        }
    }

    //The pass button only passes the turn if the user has drawn a card this round
    private void passClicked() {
        if(isDrawn()) {
            played = false;
            game.countDown();
        }
        else
            showMessage("Draw a card to pass");
    }

    //Displays a message to the user
    private void showMessage(String text) {
        JLabel message = new JLabel(text);
        JOptionPane.showMessageDialog(null, message);
    }
}