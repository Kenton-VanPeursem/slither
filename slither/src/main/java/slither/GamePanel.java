package slither;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.HashMap;
import java.util.Map;

import javax.swing.*;
import org.slf4j.*;

public class GamePanel extends JPanel {
    private static final Logger logger = LoggerFactory.getLogger(GamePanel.class);
    private transient Snake snake;

    private Timer timer;

    private int dim;
    private int blocksize;

    private boolean pauseFlag;
    private boolean started;

    private transient Map<Point, Boolean> outOfBoundsCache = new HashMap<>();

    GamePanel(int width, int height, int blocksize) {
        logger.debug("Creating GamePanel width:{} height:{} blocksize:{}",
                width, height, blocksize);
        pauseFlag = false;
        started = false;

        this.blocksize = blocksize;

        dim = width / blocksize;

        int start = dim / 3;

        snake = new Snake(start, start, dim, dim);
    }

    public void start(int millis) {
        ActionListener l = new MyUpdateListener();
        timer = new Timer(millis, l);
        timer.start();
    }

    public boolean isPaused() {
        return pauseFlag;
    }

    public boolean isStarted() {
        return started;
    }

    public void pause() {
        pauseFlag = true;
    }

    public void unpause() {
        pauseFlag = false;
    }

    public void begin() {
        logger.debug("Snake game started.");
        started = true;
    }

    public void setUserInput(Direction direction) {
        snake.setDirection(direction);
    }

    public Snake getSnake() {
        return snake;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (snake != null) {
            drawApple(g);
            drawBody(g);
            drawHead(g);
        }

        if (!isStarted()) {
            // display a not started message if the
            logger.debug("NOT STARTED");
            drawText(g, MessageConstants.START_MESSAGE, MessageConstants.START_X_PADDING, MessageConstants.START_Y_PADDING);
        } else if (isPaused()) {
            // display a paused message on the screen
            logger.debug("PAUSED");
            drawText(g, MessageConstants.PAUSED_MESSAGE, MessageConstants.PAUSED_X_PADDING, MessageConstants.PAUSED_Y_PADDING);
        } else if (isWinner()) {
            // display a game over message to the screen
            logger.debug("Player won");
            drawText(g, MessageConstants.WINNER_MESSAGE, MessageConstants.WINNER_X_PADDING, MessageConstants.WINNER_Y_PADDING);
        } else if (gameOver()) {
            // display a game over message to the screen
            logger.debug("GAME OVER");
            drawText(g, MessageConstants.GAMEOVER_MESSAGE, MessageConstants.GAMEOVER_X_PADDING, MessageConstants.GAMEOVER_Y_PADDING);
        }
        drawScore(g);
    }

    private boolean gameOver() {
        if (didCollide()) {
            logger.debug("Snake collided with itself.");
        }
        if (outOfBounds()) {
            logger.debug("Snake has gone out of bounds.");
        }

        return didCollide() || outOfBounds();
    }

    private boolean isWinner() {
        return snake.isWinner();
    }

    private void drawApple(Graphics g) {
        var apple = snake.applePosition();
        if (apple == null) {
            return;
        }

        g.setColor(Color.RED);
        fillRect(g, apple);
    }

    private void drawHead(Graphics g) {
        var head = snake.getHeadPosition();
        g.setColor(Color.GREEN.darker());
        fillRect(g, head);
    }

    private void drawBody(Graphics g) {
        g.setColor(Color.GREEN);
        var positions = snake.getBody();
        for (int i = 0; i < positions.size(); i++) {
            var pos = positions.get(i);
            fillRect(g, pos);
        }
    }

    private void fillRect(Graphics g, Point point) {
        var startX = point.getX() * blocksize;
        var startY = point.getY() * blocksize;

        g.fillRect(startX, startY, blocksize, blocksize);
    }

    private void drawText(Graphics g, String text, int xPadding, int yPadding) {
        g.setColor(Color.BLACK);
        Graphics2D g2d = (Graphics2D) g;

        Font font = new Font("Courier", Font.PLAIN, 64);
        g2d.setFont(font);
        var width = getWidth() / 2;
        var height = getHeight() / 4;
        g2d.drawString(text, width + xPadding, height + yPadding);
    }

    private void drawScore(Graphics g) {
        g.setColor(Color.GRAY);
        Graphics2D g2d = (Graphics2D) g;

        Font font = new Font("Courier", Font.PLAIN, 18);
        g2d.setFont(font);
        var width = getWidth() / 2;
        String score = String.valueOf(snake.getBody().size());
        int xPadding = score.length() * 6;
        g2d.drawString(score, width - xPadding, 20);
    }

    private boolean didCollide() {
        return snake.didCollide();
    }

    private boolean outOfBounds(Point position) {
        logger.debug("Position {} << {} {}",
                position,
                dim, dim);
        return (position.getX() < 0
                || position.getX() >= dim
                || position.getY() < 0
                || position.getY() >= dim);
    }

    private boolean outOfBounds() {
        return outOfBoundsCache.computeIfAbsent(
                    snake.getHeadPosition(),
                    pos -> Boolean.valueOf(outOfBounds(pos))
                ).booleanValue();
    }

    class MyUpdateListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (isStarted() && !isPaused() && !isWinner()) {
                step();
            }
            repaint();

            if (gameOver() || isWinner())
                timer.stop();
        }

        private void step() {
            if (!isPaused() && !gameOver()) {
                snake.move();
            }
            repaint();
        }
    }
}
