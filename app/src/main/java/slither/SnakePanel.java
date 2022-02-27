package slither;

import java.awt.Color;
import java.awt.Graphics;
import java.util.HashMap;

import javax.swing.*;

import org.slf4j.*;

public class SnakePanel extends JPanel {
    private int WINDOW_BUFFER = 25;
    private static final Logger logger = LoggerFactory.getLogger(SnakePanel.class);
    private transient Snake snake;

    private int blocksize;
    private int halfblock;
    private int widthBounds;
    private int heightBounds;

    private boolean pause;
    private boolean started;

    private HashMap<Point, Boolean> outOfBoundsCache = new HashMap<>();

    SnakePanel(int width, int height, int blocksize) {
        this.pause = false;
        this.started = false;

        this.blocksize = blocksize;
        this.halfblock = blocksize / 2;

        this.widthBounds = width;
        this.heightBounds = height;

        snake = new Snake(width / 2, (height - WINDOW_BUFFER) / 2, blocksize);
    }

    public boolean isPaused() {
        return pause;
    }

    public boolean isStarted() {
        return started;
    }

    public void pause() {
        this.pause = true;
    }

    public void unpause() {
        this.pause = false;
    }

    public boolean isGameOver() {
        if (didCollide()) {
            logger.debug("Snake collided with itself.");
        }
        if (outOfBounds()) {
            logger.debug("Snake has gone out of bounds.");
        }

        return didCollide() || outOfBounds();
    }

    public Snake getSnake() {
        return snake;
    }

    public void step() {
        if (!isPaused()) {
            snake.move();
        }
    }

    public void begin() {
        started = true;
    }

    public void setUserInput(Direction direction) {
        snake.setDirection(direction);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (snake != null) {
            var positions = snake.getPositions();
            for (int i = 0; i < positions.size(); i++) {
                var pos = positions.get(i);
                if (pos.equals(snake.getHeadPosition())) {
                    g.setColor(Color.GREEN.darker());
                } else {
                    g.setColor(Color.GREEN);
                }
                g.fillRect(
                        pos.getX() - halfblock,
                        pos.getY() - halfblock,
                        blocksize,
                        blocksize);
            }

            if (!isStarted()) {
                // display a not started message if the
               logger.debug("NOT STARTED");
            }

            if (isPaused()) {
                // display a paused message on the screen
                logger.debug("IS PAUSED");
            }

            if (isGameOver()) {
                // display a game over message to the screen
                logger.debug("GAME OVER");
            }
        }
    }

    private boolean didCollide() {
        return snake.didCollide();
    }

    private boolean outOfBounds(Point position) {
        logger.debug("Position {} {} {} {} << {} {}",
                position.getX() - halfblock,
                position.getX() + halfblock,
                position.getY() - halfblock,
                position.getY() + halfblock,
                widthBounds, heightBounds);
        return (position.getX() - halfblock < 0
                || position.getX() + halfblock > widthBounds
                || position.getY() - halfblock < 0
                || position.getY() + halfblock > heightBounds);
    }

    private boolean outOfBounds() {
        return outOfBoundsCache.computeIfAbsent(
                    snake.getHeadPosition(),
                    pos -> Boolean.valueOf(outOfBounds(pos))
                ).booleanValue();
    }
}
