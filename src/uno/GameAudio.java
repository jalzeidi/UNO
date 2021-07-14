/* GameAudio.java
 * Plays sound effects for the game
 * Static variables CARD_PLAY and CARD_DRAW are addresses to files located in this project's resources folder
 */

package uno;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.BufferedInputStream;
import java.io.InputStream;

public class GameAudio {

    //Constants for each audio in the game, any new audio file can be added here if needed
    public static final String CARD_PLAY = "/audio/Cardplace1.wav";
    public static final String CARD_DRAW = "/audio/Cardslide1.wav";
    public static final String SILENT = "/audio/Silent.wav";

    private static Clip clip;

    /* Loads the audio input stream with an audio file in the resources folder
     * NOTE: The string parameter takes in a path to a file located in the resources folder
     */
    public static void play() {
        clip.start();
    }

    public static void load(String resource) {
        InputStream inputStream = new BufferedInputStream(GameAudio.class.getResourceAsStream(resource));
        AudioInputStream audioInputStream;
        try {
            audioInputStream = AudioSystem.getAudioInputStream(inputStream);
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}