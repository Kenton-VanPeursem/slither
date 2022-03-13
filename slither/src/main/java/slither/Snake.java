package slither;

import org.slf4j.*;

import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashSet;

public class Snake {
    private static final Logger logger = LoggerFactory.getLogger(Snake.class);

    private Point head;
    private Point addLocation;
    private List<Point> body;
    private Point applePos;
    private Direction faceDirection;

    private Set<Point> availablePositions;

    private int delta = 1;

    private boolean won = false;

    private int maxX;
    private int maxY;

    private Random rand;

    Snake(int x, int y, int maxX, int maxY) {
        rand = new Random();
        this.maxX = maxX;
        this.maxY = maxY;
        logger.debug("Snake with start position ({}, {})", x, y);
        head = new Point(x, y);
        body = new ArrayList<>();

        addLocation = head.copy();
        addLocation.decrementX(delta);
        faceDirection = Direction.RIGHT;
        applePos = randomApple();

        positionsLog();
    }

    public void positionsLog() {
        logger.debug("Head: {}", head);
        logger.debug("Body: {}", body);
        logger.debug("Apple: {}", applePos);
    }

    public boolean isWinner() {
        return won;
    }

    public void eat() {
        body.add(addLocation);
        applePos = randomApple();
    }

    private void followHead() {
        if (!body.isEmpty()) {
            addLocation = body.get(body.size() - 1).copy();
            for (int i = body.size() -1; i > 0; i--) {
                body.set(i, body.get(i-1).copy());
            }
            body.set(0, head.copy());
        } else {
            addLocation = head.copy();
        }
    }

    private void right() {
        followHead();
        head.incrementX(delta);
    }

    private void left() {
        followHead();
        head.decrementX(delta);
    }

    private void up() {
        followHead();
        head.decrementY(delta);
    }

    private void down() {
        followHead();
        head.incrementY(delta);
    }

    public Point getHeadPosition() {
        return head;
    }

    public List<Point> getPositions() {
        // return the positions that are taken for
        ArrayList<Point> positions = new ArrayList<>();
        positions.add(head);
        if (!body.isEmpty()) {
            positions.addAll(body);
        }

        return positions;
    }

    public List<Point> getBody() {
        return body;
    }

    public Direction currentDirection() {
        return faceDirection;
    }

    private Direction oppositeDirection(Direction direction) throws IllegalArgumentException {
        switch (direction) {
            case UP:
                return Direction.DOWN;
            case DOWN:
                return Direction.UP;
            case LEFT:
                return Direction.RIGHT;
            case RIGHT:
                return Direction.LEFT;
            default:
                // Nothing
                throw new IllegalArgumentException(direction.toString());
        }
    }

    public void setDirection(Direction direction) {
        if (direction != oppositeDirection(faceDirection)) {
            faceDirection = direction;
        }
    }

    public void move() {
        switch (faceDirection) {
            case UP:
                up();
                break;
            case DOWN:
                down();
                break;
            case LEFT:
                left();
                break;
            case RIGHT:
                right();
                break;
            default:
                logger.error("Unknown Direction: {}", faceDirection);
        }

        positionsLog();

        if (applePos.equals(head)) {
            eat();
        }
    }

    public Point applePosition() {
        return applePos;
    }

    private Point randomApple() {
        setAvailablePositions();
        if (availablePositions.isEmpty()) {
            won = true;
            return null;
        }

        int index = rand.nextInt(availablePositions.size());
        var iter = availablePositions.iterator();
        Point nextApple = null;
        for (int i = 0; i <= index; i++) {
            nextApple = iter.next();
        }

        return nextApple;
    }

    private void setAvailablePositions() {
        availablePositions = new HashSet<>();
        List<Point> snake = getPositions();
        for (int i = 0; i < maxX; i++) {
            for (int j = 0; j < maxY; j++) {
                Point p = new Point(i, j);
                if (snake.stream().noneMatch(b -> b.equals(p)))
                    availablePositions.add(p);
            }
        }
    }

    public boolean didCollide() {
        return body.stream().anyMatch(b -> b.equals(head));
    }
}
