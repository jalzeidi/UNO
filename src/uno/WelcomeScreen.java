package uno;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

public class WelcomeScreen extends JFrame implements ActionListener {

    private Game game;
    private JButton [] modeOptions;
    private boolean modeSelected;

    WelcomeScreen(Game game) {
        this.game = game;
        setTitle("UNO");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(620, 445);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setResizable(false);
        setVisible(true);
        modeOptions = new JButton[2];
        modeOptions[0] = new JButton("2-Player");
        modeOptions[0].addActionListener(this);
        modeOptions[1] = new JButton("4-Player");
        modeOptions[1].addActionListener(this);
        modeSelection();
        unoBackground();
    }

    public void modeSelection() {
        JPanel southPanel = new JPanel();
        southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.PAGE_AXIS));

        JPanel labelPanel = new JPanel();
        JLabel welcomeMsg = new JLabel("Welcome to UNO! Please select a game mode.");
        labelPanel.add(welcomeMsg);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(modeOptions[0]);
        buttonPanel.add(modeOptions[1]);

        southPanel.add(labelPanel);
        southPanel.add(buttonPanel);

        add(southPanel, BorderLayout.SOUTH);
    }

    public void unoBackground() {
        BufferedImage introImg = ImageProcessor.loadImage("/images/other/Uno background.jpg");
        JLabel introLabel = new JLabel();
        introLabel.setIcon(new ImageIcon(introImg));
        introLabel.setHorizontalAlignment(JLabel.CENTER);
        add(introLabel, BorderLayout.CENTER);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == modeOptions[0] || e.getSource() == modeOptions[1]) {
            if (e.getSource() == modeOptions[0])
                game.initPlayerSize(2);
            else
                game.initPlayerSize(4);
            modeSelected = true;
            game.countDown();
            setVisible(false);
        }
    }

    public void setModeSelected(boolean modeSelected) {
        this.modeSelected = modeSelected;
    }

    public boolean isModeSelected() {
        return modeSelected;
    }
}