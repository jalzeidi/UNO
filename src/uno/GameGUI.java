/* GameGUI.java
 * Handles the game's GUI
 * This class MUST run on the Event Dispatch Thread to work properly
 * All GUI components for this game are removed from their containers and then replaced with new components that
 * correspond to the current game's state every time the timer is started
 */

package uno;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class GameGUI extends JFrame implements ActionListener, KeyListener {

    private Game game;
    private WelcomeScreen welcomeScreen;
    private ImageIcon clockWise;            //icon used to show direction of play is clockwise
    private ImageIcon counterClockWise;     //icon used to show direction of play is counter-clockwise
    private JButton pass;                   //used by the human player to pass turn
    private Timer timer;                    //used to fire action events

    GameGUI(Game game, WelcomeScreen welcomeScreen) {
        setTitle("UNO");
        setLayout(new GridBagLayout());
        setExtendedState(JFrame.MAXIMIZED_BOTH);            //sets the frame's extended state to maximum size
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setFocusable(true);                                 //gives this frame the keyboard's focus
        setFocusTraversalKeysEnabled(false);                //disables keys that shift focus between components
        addKeyListener(this);
        this.game = game;
        this.welcomeScreen = welcomeScreen;
        clockWise = new ImageIcon(ImageProcessor.loadImage("/images/other/Clockwise symbol small.png"));
        counterClockWise = new ImageIcon(ImageProcessor.loadImage("/images/other/" +
                "Counter-Clockwise symbol small.png"));
        pass = new JButton("Pass");
        pass.setFocusable(false);                  //pass button cannot gain focus
        timer = new Timer(1, this);  //timer fires action events every 1 millisecond when started
    }

    /* When timer is called, JFrame is re-rendered
     * After the call, the timer is stopped because there is no need to redisplay components more than once per call
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == timer) {
            render();
            timer.stop();
        }
    }

    //Timer starts firing action events
    void startTimer() {
        timer.start();
    }

    /* Displays all visual game components on the screen(cards, player labels, current turn, direction of play, etc...)
     * This method is called at least once every time the turn is passed
     */
    private void render() {
        getContentPane().removeAll();       //removes all components from frame
        JPanel middlePanel = new JPanel();  //contains the draw pile, discard pile, turn & direction label, pass button
        middlePanel.setLayout(new BoxLayout(middlePanel, BoxLayout.PAGE_AXIS));
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 1;
        c.insets = new Insets(25, 0, 25, 0);  //spacing between middlePanel and other components
        add(middlePanel, c);

        turnLabel(middlePanel);

        JPanel discardDrawPanel = new JPanel();
        middlePanel.add(discardDrawPanel);
        displayDiscardPile(discardDrawPanel);
        displayDrawPile(discardDrawPanel);
        passButton(middlePanel);
        directionLabel(middlePanel);
        displayHands();
        displayPlayersInfo();
        revalidate();
        repaint();
        checkWinner();
    }

    //Display's the game's current direction of play
    private void directionLabel(JPanel panel) {
        JPanel labelPanel = new JPanel();
        JLabel directionLabel = new JLabel();
        if(game.getDirection().equals(Direction.CW))
            directionLabel.setIcon(clockWise);
        else
            directionLabel.setIcon(counterClockWise);
        directionLabel.setHorizontalAlignment(JLabel.CENTER);
        labelPanel.add(directionLabel);
        panel.add(labelPanel);
    }

    //Displays the game's current turn
    private void turnLabel(JPanel panel) {
        JPanel turnPanel = new JPanel();
        int currentTurn = game.getCurrentTurn();
        JLabel turnLabel = new JLabel("Player " + (currentTurn + 1) + "'s " + "turn");
        turnPanel.add(turnLabel);
        panel.add(turnPanel);
    }

    //Displays the top card of the draw pile
    private void displayDrawPile(JPanel panel) {
        DrawPile drawPile = game.getDrawPile();
        drawPile.renderCards(panel);
    }

    //Displays the top card of the discard pile
    private void displayDiscardPile(JPanel panel) {
        DiscardPile discardPile = game.getDiscardPile();
        discardPile.renderCards(panel);
    }

    //Displays the pass button
    private void passButton(JPanel panel) {
        JPanel passPanel = new JPanel();
        passPanel.add(pass);
        panel.add(passPanel);
    }

    //Displays all hands in the game
    private void displayHands() {
        Player[] players = game.getPlayers();
        for(Player player : players) {
            Hand hand = player.getHand();
            hand.addToFrame(this);
            hand.displayCards(!player.isAI());
        }
    }

    //Displays every player's info (number of cards and player label)
    private void displayPlayersInfo() {
        Player[] players = game.getPlayers();
        for(int i = 0; i < players.length; i++) {
            Hand hand = players[i].getHand();
            hand.displayInfo(i+1);
        }
    }

    //Checks if there is a winner
    private void checkWinner() {
        Player[] players = game.getPlayers();
        for(int i = 0; i < players.length; i++) {
            int cards = players[i].getHand().getLength();
            if(cards == 0) {
                winnerDialog(i);
                break;
            }
        }
    }

    //Prints the label of the player that won, allows the user to play again or quit
    private void winnerDialog(int winnerIndex) {
        String message = "Player " + (winnerIndex + 1) + " has won!";
        String [] options = {"Play Again", "Quit"};
        int optionSelected = JOptionPane.showOptionDialog(null, message, "Winner Message",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
        if(optionSelected == 0) {
            welcomeScreen.setVisible(true);
            setVisible(false);
        }
        else
            System.exit(0);         //ends the program's execution
    }

    JButton getPass() {
        return pass;
    }

    //Unused methods
    @Override
    public void keyTyped(KeyEvent e) {}
    @Override
    public void keyPressed(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {
        Player[] players = game.getPlayers();
        Player player = players[0];
        Hand hand = player.getHand();
        if(e.getKeyCode() == KeyEvent.VK_LEFT) {
            hand.decreaseStartIndex();
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            hand.increaseStartIndex();
        }
        render();
    }
}