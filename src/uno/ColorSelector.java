/* ColorSelector.java
 * This dialog is used by the human player to select a color after playing a wild card
 */

package uno;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ColorSelector extends JDialog implements ActionListener {

    private JButton [] colorButtons;    //array of buttons, each button representing a color
    private Game game;
    private Timer timer;                //used to fire action events

    ColorSelector(Game game) {
        setSize(155,190);
        setResizable(false);
        int x = Toolkit.getDefaultToolkit().getScreenSize().width/4;
        int y = Toolkit.getDefaultToolkit().getScreenSize().height/2 - getHeight()/2;
        setLocation(x, y);
        setLayout(new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS));
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);                //Dialog is not closeable
        colorButtons = new JButton[4];
        this.game = game;
        timer = new Timer(1, this);                             //fires action events every 1 millisecond
        addLabel();
        addButtons();
    }

    //Instructions for user
    private void addLabel() {
        JPanel labelPanel = new JPanel();
        JLabel label = new JLabel("Select a color.");
        labelPanel.add(label);
        add(labelPanel);
    }

    //Buttons used to select a color for the wild card
    private void addButtons() {
        JPanel upperPanel = new JPanel();
        JPanel lowerPanel = new JPanel();
        Color [] colors = {Color.RED, Color.YELLOW, Color.GREEN, Color.BLUE};
        for(int i = 0; i < colorButtons.length; i++) {
            colorButtons[i] = new JButton();
            colorButtons[i].setBackground(colors[i]);
            colorButtons[i].setPreferredSize(new Dimension(50,50));
            colorButtons[i].addActionListener(this);
        }
        upperPanel.add(colorButtons[0]);
        upperPanel.add(colorButtons[1]);
        lowerPanel.add(colorButtons[2]);
        lowerPanel.add(colorButtons[3]);
        add(upperPanel);
        add(lowerPanel);
    }

    //timer starts firing action events
    void startTimer() {
        timer.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == timer) {
            setVisible(true);   //display this dialog to allow the player to select a color
            timer.stop();       //stop timer as soon as it starts, because we only want it to fire one action event
        }
        else {
            Card discard = game.getDiscardPile().getLast();
            if (e.getSource() == colorButtons[0])
                discard.setCardColor(CardColor.RED);
            else if (e.getSource() == colorButtons[1])
                discard.setCardColor(CardColor.YELLOW);
            else if (e.getSource() == colorButtons[2])
                discard.setCardColor(CardColor.GREEN);
            else
                discard.setCardColor(CardColor.BLUE);
            discard.changeWildFace();
            game.countDown();
            setVisible(false);
        }
    }
}