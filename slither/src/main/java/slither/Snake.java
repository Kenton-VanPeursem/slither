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
    private List<Point> body = new ArrayList<>();
    private Point applePos;
    private Direction faceDirection = Direction.RIGHT;

    private int delta = 1;

    private boolean won = false;

    private int tickCount = 0;

    private int maxX;
    private int maxY;

    private Random rand;

    public Snake(int x, int y, int maxX, int maxY) {
        rand = new Random();

        this.maxX = maxX;
        this.maxY = maxY;
        head = new Point(x, y);
        logger.debug("Snake with start position {}", head);

        addLocation = head.copy();
        addLocation.decrementX(delta);

        applePos = randomApple();

        positionsLog();
    }

    public Snake(int x, int y, int maxX, int maxY, int seed) {
        setRandomSeed(seed);

        this.maxX = maxX;
        this.maxY = maxY;
        head = new Point(x, y);
        logger.debug("Snake with start position {}", head);

        addLocation = head.copy();
        addLocation.decrementX(delta);

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

    public int maxX() {
        return maxX;
    }

    public int maxY() {
        return maxY;
    }

    public void setRandomSeed(long seed) {
        rand = new Random(seed);
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

    public void setDirection(Direction direction) {
        if (direction != Direction.oppositeDirection(faceDirection)) {
            faceDirection = direction;
        }
    }

    public Point nextPoint(Point x, Direction direction) {
        var next = x.copy();
        switch (direction) {
            case UP:
                next.decrementY(delta);
                break;
            case DOWN:
                next.incrementY(delta);
                break;
            case LEFT:
                next.decrementX(delta);
                break;
            case RIGHT:
                next.incrementX(delta);
                break;
            default:
                logger.error("Unknown Direction: {}", faceDirection);
        }

        return next;
    }

    public synchronized void move() {
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

        tickCount++;
    }

    public int getTotalTicks() {
        return tickCount;
    }

    public Point applePosition() {
        return applePos;
    }

    private Point randomApple() {
        var availablePositions = getAvailablePositions();
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

    private Set<Point> allPositions() {
        var valid = new HashSet<Point>();
        for (int i = 0; i < maxX; i++) {
            for (int j = 0; j < maxY; j++) {
                valid.add(new Point(i, j));
            }
        }
        return valid;
    }

    public Set<Point> getAvailablePositions() {
        var valid = allPositions();
        List<Point> snake = getPositions();
        for (Point p: snake)
            valid.remove(p);
        return valid;
    }

    public synchronized boolean didCollide() {
        return body.stream().anyMatch(b -> b.equals(head));
    }
}
