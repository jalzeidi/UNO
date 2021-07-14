/* Game.java
 * Handles the initialization process of the game
 * Contains the game loop
 */

package uno;

import java.awt.*;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

public class Game implements Runnable {

    private DrawPile drawPile;
    private DiscardPile discardPile;
    private WelcomeScreen welcomeScreen;
    private GameGUI gameGUI;
    private ColorSelector colorSelector;
    private Direction direction;
    private boolean running;
    private Player [] players;
    private int currentTurn;                    //index of the player in the players array that has the current turn
    private CountDownLatch countDownLatch;      //used to pause/resume thread execution, must be reset every time used

    private Game() {
        drawPile = new DrawPile();
        discardPile = new DiscardPile(drawPile);
        direction = Direction.CW;
        running = false;
        countDownLatch = new CountDownLatch(1); //countDown() must be called once to resume thread
    }

    public static void main(String [] args) {
        Game game = new Game();
        GameAudio.load(GameAudio.SILENT);   //silent audio is played because the first audio clip is delayed
        GameAudio.play();
        Runnable runnable = () -> {
            game.welcomeScreen = new WelcomeScreen(game);
            game.gameGUI = new GameGUI(game, game.welcomeScreen);
            game.colorSelector = new ColorSelector(game);
        };
        EventQueue.invokeLater(runnable);   //handles GUI components in the Event Dispatch Thread,
                                            //as required for all swing components
        Thread thread = new Thread(game);   //backend side of the game runs on a separate thread
        thread.start();
    }

    //Called implicitly when the thread starts, then explicitly each time the game is restarted played again
    @Override
    public void run() {
        waitOnModeSelection();
        initDrawPile();
        Hand[] hands = new Hand[players.length];
        if(players.length == 2)
            initTwoHands(hands);
        else
            initFourHands(hands);
        initPlayers(hands);
        HumanPlayer humanPlayer = (HumanPlayer)players[0];
        humanPlayer.addListeners();
        dealHands();
        initDiscardPile();
        randomTurn();
        gameGUI.setVisible(true);
        gameGUI.startTimer();
        roundOneCheck();
        running = true;
        cycle();
    }

    /* Game loop
     * The reverse, wild, and action card checks only occur if the player has played a card during the current turn
     * This is to ensure that the checks aren't called on a card that was played by a player other than that who has
     * the current turn
     */
    private void cycle() {
        while(running) {
            boolean played;                         //keeps track of whether the current player played
            Player player = players[currentTurn];
            played = player.play();
            if(played) {
                reverseCheck();
                wildCheck();
                actionCardCheck(getNextTurn());
            }
            player.getHand().setSelected(null);
            player.setDrawn(false);
            passTurn();
            checkDrawPile();
            gameGUI.startTimer();
            checkWinner(player);
        }
    }

    /* Removes all cards from the draw pile and the discard pile
     * Sets the direction to the initial direction (CW)
     * Calls run, which asks the user to select a mode to start a new game
     */
    private void reset() {
        drawPile.removeCards();
        discardPile.removeCards();
        direction = Direction.CW;
        run();
    }

    //Resumes the execution of this thread and creates a new instance of the countDownLatch, because it is not reusable
    void countDown() {
        countDownLatch.countDown();
        countDownLatch = new CountDownLatch(1);
    }

    //Sleeps the thread until the countDown() method is called
    void sleepThread() {
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //Generates a random turn
    private void randomTurn() {
        Random r = new Random();
        currentTurn = r.nextInt(players.length);
    }


    //This method is only used during the start of the game or after the draw pile is depleted during game-play
    private void roundOneCheck() {
        wildDrawFourCheck();
        wildCheck();
        gameGUI.startTimer();
        if(reverseCheck())
            if(players.length != 2)
                passTurn();
        actionCardCheck(currentTurn);
    }

    /* Checks if the top card in the discard pile is a wild draw four card
     * This is not allowed in UNO, so the card is returned to the draw pile, the draw pile is then shuffled
     * and the discard pile is recreated. The method recursively calls itself again to ensure that the top of the
     * newly created discard pile is not a wild draw four card as well.
     */
    private void wildDrawFourCheck() {
        Card discard = discardPile.getLast();
        if(discard.getCardAction().equals(CardAction.DRAW4)) {
            discard = discardPile.draw();
            drawPile.addCard(discard);
            drawPile.shuffleCards();
            discardPile.createDeck();
            wildDrawFourCheck();
        }
    }

    /* Checks the discard pile for a reverse card
     * if there are two players, the reverse card acts as a skip card (based on UNO rules)
     * if there are more than 2 players, reverse switches the direction of play
     */
    private boolean reverseCheck() {
        Card discard = discardPile.getLast();
        CardAction action = discard.getCardAction();
        if(action.equals(CardAction.REVERSE)) {
            if(players.length == 2)
                passTurn();
            else
                switchDirection();
            return true;
        }
        return false;
    }

    /* Checks for actions card
     * In a game of UNO, action cards include the reverse card and exclude the Wild draw four card
     * In this method, the the grouping and labeling of action cards was based on the way they are dealt with
     * Draw 2, Draw 4, and Skip all have a thing in common, which is the skipping of the next player's turn
     */
    private void actionCardCheck(int playerIndex) {
        Card discard = discardPile.getLast();
        Hand hand = players[playerIndex].getHand();
        CardAction cardAction = discard.getCardAction();
        if(cardAction.equals(CardAction.DRAW2))
            hand.drawCard(2);
        else if(cardAction.equals(CardAction.DRAW4))
            hand.drawCard(4);
        else if(!cardAction.equals(CardAction.SKIP))
            return;
        passTurn();
    }

    /* Checks if a card is wild
     * The current player chooses a color to continue play
     */
    private void wildCheck() {
        Card discard = discardPile.getLast();
        if(players[currentTurn].getHand().getLength() != 0)
            if(discard.getCardColor().equals(CardColor.WILD))
                players[currentTurn].pickColor();
    }

    /* Checks if the draw pile is depleted
     * If so, the cards in the discard pile are returned to the draw pile
     * The cards are shuffled and the last card of the draw pile is used to create a new discard pile
     */
    private void checkDrawPile() {
        if(drawPile.getLength() == 0) {
            discardPile.returnCards();
            drawPile.shuffleCards();
            discardPile.createDeck();
            roundOneCheck();
        }
    }

    /* Checks if the player won
     * Setting running to false stops the game loop
     */
    private void checkWinner(Player player) {
        if(player.getHand().getLength() == 0) {
            running = false;
            welcomeScreen.setModeSelected(false);
            reset();
        }
    }

    /* Returns the player who's turn is going to be next
     * This method is used when a draw two or draw four card is played to make the player who's turn is next
     * draw the corresponding number cards
     */
    private int getNextTurn() {
        int nextTurn = currentTurn;
        if(direction == Direction.CW) {
            if(nextTurn != players.length-1)
                nextTurn++;
            else
                nextTurn = 0;
        }
        else {
            if(nextTurn != 0)
                nextTurn--;
            else
                nextTurn = players.length-1;
        }
        return nextTurn;
    }

    // Passes the turn based on the current direction
    private void passTurn() {
        if(direction == Direction.CW)
            passTurnCW();
        else
            passTurnCCW();
    }

    /* Passes the turn in a clockwise manner
     * When the max player index is reached, turn circles back to the first index
     */
    private void passTurnCW() {
        if(currentTurn != players.length-1)
            currentTurn++;
        else
            currentTurn = 0;
    }

    /* Passes the turn in a counter-clockwise manner
     * When the min player index is reached, turn circles back to the last index
     */
    private void passTurnCCW() {
        if(currentTurn != 0)
            currentTurn--;
        else
            currentTurn = players.length-1;
    }

    //switches the direction of play, from clockwise to counter-clockwise and vice versa
    private void switchDirection() {
        if(direction == Direction.CW)
            direction = Direction.CCW;
        else
            direction = Direction.CW;
    }

    //Initializes the player array
    void initPlayerSize(int size) {
        players = new Player[size];
    }

    //Creates and shuffles all cards in the game
    private void initDrawPile() {
        drawPile.createDeck();
        drawPile.shuffleCards();
    }

    //Creates the discard pile
    private void initDiscardPile() {
        discardPile.createDeck();
    }

    //Initializes the game with two hands corresponding to two players
    private void initTwoHands(Hand[] hands) {
        hands[0] = new Hand(drawPile, discardPile, gameGUI,7, 0, 1, 2);
        hands[1] = new Hand(drawPile, discardPile, gameGUI,7, 180, 1, 0);
    }

    //Initializes the game with four hands corresponding to four players
    private void initFourHands(Hand[] hands) {
        hands[0] = new Hand(drawPile, discardPile, gameGUI,7, 0, 1, 2);
        hands[1] = new Hand(drawPile, discardPile, gameGUI,3, 90, 0, 1);
        hands[2] = new Hand(drawPile, discardPile, gameGUI,7, 180, 1, 0);
        hands[3] = new Hand(drawPile, discardPile, gameGUI,3, 270, 2, 1);
    }

    //Adds cards for each hand
    private void dealHands() {
        for(Player player : players) {
            Hand hand = player.getHand();
            hand.createDeck();
        }
    }

    /* Initializes all players in the game
     * player[0] is reserved for the user, which is the only human player in this game
     */
    private void initPlayers(Hand[] hands) {
        players[0] = new HumanPlayer(hands[0], discardPile, drawPile,this, gameGUI, colorSelector);
        for(int i = 1; i < players.length; i++) {
            players[i] = new CPUPlayer(hands[i], discardPile);
        }
    }

    DrawPile getDrawPile() {
        return drawPile;
    }

    DiscardPile getDiscardPile() {
        return discardPile;
    }

    Direction getDirection() {
        return direction;
    }

    boolean isRunning() {
        return running;
    }

    Player[] getPlayers() {
        return players;
    }

    int getCurrentTurn() {
        return currentTurn;
    }

    //Pauses the thread until a mode is selected
    private void waitOnModeSelection() {
        while(welcomeScreen == null || !welcomeScreen.isModeSelected()) {
            try {
                countDownLatch.await();
            }
            catch(InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}