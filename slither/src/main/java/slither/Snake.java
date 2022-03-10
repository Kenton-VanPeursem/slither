package slither;

import org.slf4j.*;

import java.util.List;
import java.util.ArrayList;

public class Snake {
    private static final Logger logger = LoggerFactory.getLogger(Snake.class);

    private Point head;
    private Point last;
    private List<Point> body;
    private Direction faceDirection;
    private int delta;

    Snake(int x, int y) {
        this.head = new Point(x, y);
        this.body = new ArrayList<>();
        this.last = new Point(x, y);
        this.delta = 1;
        this.faceDirection = Direction.RIGHT;
    }

    Snake(int x, int y, int delta) {
        this.head = new Point(x, y);
        this.body = new ArrayList<>();
        this.last = new Point(x, y);
        this.delta = delta;
        this.faceDirection = Direction.RIGHT;
    }

    public void eat() {
        body.add(last);
    }

    private void followHead() {
        if (!body.isEmpty()) {
            last = body.get(body.size() - 1);
            for (int i = body.size() -1; i > 0; i--) {
                body.set(i, body.get(i-1));
            }
            body.set(0, head);
        } else {
            last = head;
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
                throw new IllegalArgumentException("" + direction);
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
    }

    public boolean didCollide() {
        return body.stream().anyMatch(b -> b.equals(head));
    }
}
