package slither;

import org.slf4j.*;

import javax.swing.*;


import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import java.awt.GridLayout;

public class StartMenuPanel extends JPanel {
    private static final Logger logger = LoggerFactory.getLogger(StartMenuPanel.class);

    StartMenuPanel() {
        JLabel label = new JLabel("Snake Game", SwingConstants.CENTER);
        setLayout(new GridLayout(4,1));
        add(label);

        JButton easyButton = new JButton("Easy");
        JButton medButton = new JButton("Medium");
        JButton hardButton = new JButton("Hard");

        easyButton.addActionListener(new DifficultyButtonHandler(Difficulty.EASY));
        medButton.addActionListener(new DifficultyButtonHandler(Difficulty.MEDIUM));
        hardButton.addActionListener(new DifficultyButtonHandler(Difficulty.HARD));

        add(easyButton);
        add(medButton);
        add(hardButton);
    }

    private class DifficultyButtonHandler implements ActionListener {
        Difficulty difficulty = Difficulty.UNSET;

        DifficultyButtonHandler(Difficulty difficulty) {
            this.difficulty = difficulty;
        }

        public void actionPerformed(ActionEvent e) {
            StartMenuPanel.this.setVisible(false);
            logger.debug("Pressed {}", difficulty);
            playSnake(getConfig(difficulty));
        }

        private SnakeConfig getConfig(Difficulty val) {
            if (val == Difficulty.SUPER_EASY)
                return new SnakeConfig(500, 100, 50);

            if (val == Difficulty.MEDIUM)
                return new SnakeConfig(125, 800, 50);

            if (val == Difficulty.HARD)
                return new SnakeConfig(75, 1050, 50);

            return new SnakeConfig(250, 550, 50);
        }

        private void playSnake(SnakeConfig config) {
            GameController frame = (GameController) SwingUtilities.getWindowAncestor(StartMenuPanel.this);
            frame.initSnake(config);
        }
    }
}
