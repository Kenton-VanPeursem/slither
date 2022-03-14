package player;

import java.util.Set;
import java.util.HashSet;

import java.util.Arrays;

import org.slf4j.*;

import slither.*;

public class GreedyPlayer implements Player {
    private static final Logger logger = LoggerFactory.getLogger(GreedyPlayer.class);

    private Direction bestDirection;

    private Set<Direction> allDirections() {
        return new HashSet<>(
                Arrays.asList(Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT));
    }

    private Set<Direction> possibleDirections(Snake board) {
        var directions = allDirections();

        // remove backwards direction
        directions.remove(Direction.oppositeDirection(board.currentDirection()));

        Set<Direction> nextDirections = new HashSet<>();

        var head = board.getHeadPosition();
        var dirIterator = directions.iterator();
        while (dirIterator.hasNext()) {
            var dir = dirIterator.next();

            var next = board.nextPoint(head, dir);
            if (board.getAvailablePositions().contains(next)) {
                nextDirections.add(dir);
            }
        }

        return nextDirections;
    }

    @Override
    public void analyzeBoard(Snake board) {
        var head = board.getHeadPosition().copy();
        var apple = board.applePosition();

        var optimalDirection = nextGreedyDirection(head, apple);

        var nextSteps = possibleDirections(board);
        if (nextSteps.contains(optimalDirection)) {
            bestDirection = optimalDirection;
            return;
        }

        var possibleDirectionsIter = nextSteps.iterator();
        if (possibleDirectionsIter.hasNext()) {
            bestDirection = nextSteps.iterator().next();
        }
    }

    @Override
    public void makeMove(GameController gameController) {
        gameController.setUserInput(bestDirection);
    }

    private Direction nextGreedyDirection(Point src, Point dest) {
        int xDir = dest.getX() - src.getX();
        int xDiff = Math.abs(xDir);

        int yDir = dest.getY()-src.getY();
        int yDiff = Math.abs(yDir);

        if (xDiff > yDiff) {
            if (xDir < 0)
                return Direction.LEFT;
            else
                return Direction.RIGHT;
        } else {
            if (yDir < 0)
                return Direction.UP;
            else
                return Direction.DOWN;
        }
    }

    public static void main(String[] args) {
        Player player = new GreedyPlayer();
        GameController gameController = new GameController();

        var block = 25;
        var boardSize = 21;

        int highestScore = -1;

        SnakeConfig config = new SnakeConfig(50, boardSize * block, block);

        for (int i = 0; i < 10; i++) {
            gameController.initSnake(config);

            Snake board = gameController.getSnake();
            gameController.begin();
            while(!(gameController.isWinner() || gameController.gameOver())) {
                player.analyzeBoard(board);
                player.makeMove(gameController);
            }

            if (board.isWinner()) {
                logger.info("Won game in {} ticks", board.getTotalTicks());
            } else {
                logger.info("Lost game in {} ticks", board.getTotalTicks());
            }

            highestScore = Math.max(highestScore, board.getBody().size());
            logger.info("Current Score: {}", board.getBody().size());
            logger.info("Highest Score: {}", highestScore);
        }
    }
}
