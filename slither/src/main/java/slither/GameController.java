package slither;

import javax.swing.*;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import java.awt.BorderLayout;

public class GameController extends JFrame {
    private GamePanel game;

    private transient SnakeConfig prevConfig;

    private static final int WINDOW_BUFFER = 25;

    public GameController() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        setTitle("Snake");
        startMenu();
    }

    private void startMenu() {
        getContentPane().removeAll();

        setSize(250, 300);
        getContentPane().add(new StartMenuPanel(), BorderLayout.CENTER);
        revalidate();
        setVisible(true);
    }

    public void initSnake(SnakeConfig config) {
        prevConfig = config;
        getContentPane().removeAll();

        setSize(config.getWindowSize(), config.getWindowSize() + WINDOW_BUFFER);
        setResizable(false);

        game = new GamePanel(getWidth(), getHeight(), config.getBlockSize());

        getContentPane().add(game, BorderLayout.CENTER);
        revalidate();

        // maybe just don't add a new key listener if already exists?
        if (!config.isHumanPlayer()) {
            for (var kl : getKeyListeners() ) {
                removeKeyListener(kl);
            }
            addKeyListener(new SnakeController());
            requestFocus();
        }

        game.start((int) config.getFrameSpeedMillis());
    }

    public Snake getSnake() {
        return game.getSnake();
    }

    public boolean gameOver() {
        return game.gameOver();
    }

    public boolean isWinner() {
        return game.isWinner();
    }

    public void begin() {
        game.begin();
    }

    public void setUserInput(Direction direction) {
        game.setUserInput(direction);
    }

    private class SnakeController implements KeyListener {
        @Override
        public void keyTyped(KeyEvent e) {
            // Don't do anything on a key typed
        }

        @Override
        public void keyPressed(KeyEvent e) {
            var dir = game.getSnake().currentDirection();
            switch (e.getKeyCode()) {
                case 27:    // esc
                    if (game.isStarted()) {
                        initSnake(prevConfig);
                    }
                    else
                        startMenu();
                    return;
                case 65:    // a
                case 37:    // left
                case 77:    // m
                    dir = Direction.LEFT;
                    game.begin();
                    game.unpause();
                    break;
                case 83:    // s
                case 40:    // down
                case 44:    // ,
                    dir = Direction.DOWN;
                    game.begin();
                    game.unpause();
                    break;
                case 68:    // d
                case 39:    // right
                case 46:    // .
                    dir = Direction.RIGHT;
                    game.begin();
                    game.unpause();
                    break;
                case 87:     // w
                case 38:    // up
                case 75:    // k
                    dir = Direction.UP;
                    game.begin();
                    game.unpause();
                    break;
                case 32:    // space
                    game.pause();
                    break;
                default:
                    // don't do anything
            }

            if (!game.isPaused()) {
                setUserInput(dir);
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            // Don't do anything on a key release
        }
    }

    public static void main(String[] args) {
        new GameController();
    }
}
