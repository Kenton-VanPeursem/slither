package slither;

import org.slf4j.*;

import javax.swing.*;

import java.util.concurrent.TimeUnit;

import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class GameController extends JFrame {
    private static final Logger logger = LoggerFactory.getLogger(GameController.class);
    private SnakePanel game;

    private int WINDOW_BUFFER = 25;

    GameController() throws InterruptedException {
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        setTitle("Snake");

        setSize(250, 300);
        var titlePanel = new DifficultyPanel();
        getContentPane().add(titlePanel, BorderLayout.CENTER);
        setVisible(true);

        while (!titlePanel.ready()) {
            // just wait until the user clicks the button
        }

        SnakeConfig config = titlePanel.getConfig();
        getContentPane().remove(titlePanel);

        setSize(config.getWindowSize(), config.getWindowSize() + WINDOW_BUFFER);
        setResizable(false);

        game = new SnakePanel(getWidth(), getHeight(), config.getBlockSize());
        getContentPane().add(game, BorderLayout.CENTER);

        this.addKeyListener(new SnakeController());

        while (!game.isGameOver()) {
            if (!game.isPaused() && game.isStarted()) {
                game.step();

                try {
                    TimeUnit.MILLISECONDS.sleep(config.getFrameSpeedMillis());
                } catch (InterruptedException e) {
                    logger.debug("Interrupted");
                    throw e;
                }
            }
            repaint();
        }
    }

    private class SnakeController implements KeyListener {
        @Override
        public void keyTyped(KeyEvent e) {
            game.begin();
            var dir = game.getSnake().currentDirection();
            switch (e.getKeyChar()) {
                case 'w':
                    dir = Direction.UP;
                    game.unpause();
                    break;
                case 'a':
                    dir = Direction.LEFT;
                    game.unpause();
                    break;
                case 's':
                    dir = Direction.DOWN;
                    game.unpause();
                    break;
                case 'd':
                    dir = Direction.RIGHT;
                    game.unpause();
                    break;
                case ' ':
                    game.pause();
                    break;
                default:
                    // don't do anything
            }
            if (!game.isPaused()) {
                game.setUserInput(dir);
                logger.debug("Snake is going {}, {}, {}",
                        dir,
                        e.getKeyCode(),
                        e.getKeyChar());
            }
        }

        @Override
        public void keyPressed(KeyEvent e) {
            // Don't do anything on a key press
        }

        @Override
        public void keyReleased(KeyEvent e) {
            // Don't do anything on a key release
        }
    }

    public static void main(String[] args) throws InterruptedException {
        new GameController();
    }
}
