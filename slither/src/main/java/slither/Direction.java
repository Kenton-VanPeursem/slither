package slither;

public enum Direction {
    RIGHT, LEFT, DOWN, UP;

    public static Direction oppositeDirection(Direction direction) throws IllegalArgumentException {
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

}
