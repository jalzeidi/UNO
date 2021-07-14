/* Card.java
 * Represents a single card in an UNO game
 */

package uno;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Card extends JButton {

    //Vertical and horizontal sizes of the card
    static Dimension VERTICAL_SIZE = new Dimension(93, 130);
    static Dimension HORIZONTAL_SIZE = new Dimension(VERTICAL_SIZE.height, VERTICAL_SIZE.width);

    private CardColor color;                //color of the card
    private final int number;               //number of the card, -1 if the card has no number
    private final CardAction cardAction;    //cardAction of the card (draw 4, skip, etc...)
    private BufferedImage face;             //image of the front side of the card
    private BufferedImage back;             //image of the back side of the card

    Card(CardColor color, int number, CardAction cardAction) {
        this.color = color;
        this.number = number;
        this.cardAction = cardAction;
        face = ImageProcessor.loadImage("/images/cards/" + color + " " + number + " " + cardAction + ".png");
        back = ImageProcessor.loadImage("/images/cards/Back.png");
        setFocusable(false);                    //Cards cannot have the system's focus, as it is reserved for key events
        setCardIcon(false,0);
    }

    /*
     * Sets the card's icon to face or back
     * Rotates the icon's image
     * Sets the size based on the rotation angle
     */
     void setCardIcon(boolean front, int displayAngle) {
        if(front)
            setIcon(new ImageIcon(ImageProcessor.rotateImage(face, displayAngle)));
        else
            setIcon(new ImageIcon(ImageProcessor.rotateImage(back, displayAngle)));
        if(displayAngle == 90 || displayAngle == 270)
            setPreferredSize(HORIZONTAL_SIZE);
        else
            setPreferredSize(VERTICAL_SIZE);
    }

    void setCardColor(CardColor color) {
        this.color = color;
    }

    CardColor getCardColor() {
        return color;
    }

    int getNumber() {
        return number;
    }

    CardAction getCardAction() {
        return cardAction;
    }

    /*
     * This method is reserved for wild cards
     * It changes the image of the wild card to show what color has been picked by the player
     */
    void changeWildFace() {
        if(cardAction == CardAction.DRAW4)
            face = ImageProcessor.loadImage("/images/cards/WILD DRAW FOUR " + color + ".png");
        else
            face = ImageProcessor.loadImage("/images/cards/WILD " + color + ".png");
    }

    /*
     * Checks if this card is playable on another card
     */
    boolean isPlayableOn(Card discard) {
        if(color.equals(discard.color) || color.equals(CardColor.WILD))
            return true;
        else if(number != -1 && number == discard.number)
            return true;
        else
            return !cardAction.equals(CardAction.NONE) && cardAction.equals(discard.cardAction);
    }

    /*
     * Checks if this card matches the color of another card
     * This method is used to check if a wild card is playable
     */
    boolean matchesColor(Card discard) {
        return color.equals(discard.color);
    }
}