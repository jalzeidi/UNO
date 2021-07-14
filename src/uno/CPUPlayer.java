/* CPUPlayer.java
 * Represents an AI player in a game of UNO
 */

package uno;

import java.util.LinkedList;
import java.util.Random;

public class CPUPlayer extends Player {

    CPUPlayer(Hand hand, DiscardPile discardPile){
        super(hand, discardPile);
        AI = true;
    }

    /* AI plays their turn, there are 3 outcomes: playing a card, drawing a card and passing,
     * drawing a card and playing
     */
    @Override
    public boolean play() {
        pause(2);
        LinkedList <Card> playableCards = playableCards();
        Hand hand = getHand();
        if(playableCards.size() == 0) {
            if(!isDrawn()) {
                hand.drawCard(1);
                setDrawn(true);
                return play();
            }
            else
                return false;
        }
        else
            choosePlayableCard(playableCards);
        return true;
    }

    //Compares each card in hand to the last(top) card of the discard pile, generating a list of playable cards
    private LinkedList<Card> playableCards() {
        LinkedList<Card> cards = getHand().cards();
        Card discard = discardPile.getLast();                   //top card of the discard pile
        LinkedList <Card> playableCards = new LinkedList<>();
        for(Card card : cards) {
            if(card.isPlayableOn(discard)) {
                if(!card.getCardAction().equals(CardAction.DRAW4) || getHand().isWildDrawFourPlayable(discard))
                    playableCards.addLast(card);
            }
        }
        return playableCards;
    }

    //Plays a random card from the list of playable cards
    private void choosePlayableCard(LinkedList <Card> playableCards) {
        int size = playableCards.size();
        int randomIndex;
        Random rand = new Random();
        randomIndex = rand.nextInt(size);
        Card cardToPlay = playableCards.get(randomIndex);
        getHand().playCard(cardToPlay);
    }

    //AI chooses a color for a wild card, color picking is based on availability
    public void pickColor() {
        int blue = 0;
        int red = 0;
        int green = 0;
        int yellow = 0;
        LinkedList <Card> cards = getHand().cards();
        for(Card card : cards) {
            if(card.getCardColor().equals(CardColor.BLUE))
                blue++;
            else if(card.getCardColor().equals(CardColor.RED))
                red++;
            else if(card.getCardColor().equals(CardColor.GREEN))
                green++;
            else if(card.getCardColor().equals(CardColor.YELLOW))
                yellow++;
        }
        CardColor color = abundantColor(blue, red, green, yellow);
        Card discard = discardPile.getLast();
        discard.setCardColor(color);
        discard.changeWildFace();
    }

    //Returns the color that exists most in this player's hand
    private CardColor abundantColor(int blue, int red, int green, int yellow) {
        if(blue > red && blue > green && blue > yellow)
            return CardColor.BLUE;
        else if(red > green && red > yellow)
            return CardColor.RED;
        else if(green > yellow)
            return CardColor.GREEN;
        else
            return CardColor.YELLOW;
    }

    //sleeps the thread for a given time to slow down AI playing, to mimic real life speed
    private void pause(int secs) {
        try {
            Thread.sleep(secs * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}