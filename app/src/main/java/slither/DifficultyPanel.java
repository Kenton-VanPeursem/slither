package slither;

import org.slf4j.*;

import javax.swing.*;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.GridLayout;

public class DifficultyPanel extends JPanel {
    private static final Logger logger = LoggerFactory.getLogger(DifficultyPanel.class);
    private Difficulty val = Difficulty.UNSET;

    DifficultyPanel() {
        JLabel label = new JLabel("Snake Game");
        setLayout(new GridLayout(4,1));
        add(label);

        JButton easyButton = new JButton("Easy");
        JButton medButton = new JButton("Medium");
        JButton hardButton = new JButton("Hard");

        easyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                val = Difficulty.EASY;
                logger.debug("Pressed easy");
            }
        });
        medButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                val = Difficulty.MEDIUM;
                logger.debug("Pressed medium");
            }
        });
        hardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                val = Difficulty.HARD;
                logger.debug("Pressed hard");
            }
        });

        add(easyButton);
        add(medButton);
        add(hardButton);
    }

    public boolean ready() {
        logger.debug("ready? {} {}", val, Difficulty.UNSET);
        return val != Difficulty.UNSET;
    }

    public SnakeConfig getConfig() {
        if (val == Difficulty.MEDIUM)
           return new SnakeConfig(250, 800, 50);

        if (val == Difficulty.HARD)
            return new SnakeConfig(125, 1050, 50);

        return new SnakeConfig(500, 550, 50);
    }
}
