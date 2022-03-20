package player;

import java.util.Set;
import java.util.HashSet;
import java.util.Random;
import java.util.Arrays;

import org.slf4j.*;

import recorder.PlayerTracker;
import slither.*;

public class GreedyPlayer implements Player {
    private static final Logger logger = LoggerFactory.getLogger(GreedyPlayer.class);

    PlayerTracker tracker = new PlayerTracker(this);

    private Random rand;
    private long seed;

    public GreedyPlayer() {
        seed = new Random().nextInt();
        rand = new Random(seed);
    }

    public GreedyPlayer(long seed) {
        this.seed = seed;
        rand = new Random(seed);
    }

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
        if (!nextSteps.isEmpty()) {
            var randomChoice = rand.nextInt(nextSteps.size());
            for (int i = 0; i <= randomChoice; i++) {
                bestDirection = possibleDirectionsIter.next();
            }
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

    @Override
    public int play(GameController gameController, SnakeConfig config) {
        gameController.initSnake(config);

        Snake board = gameController.getSnake();
        gameController.begin();
        while(!(gameController.isWinner() || gameController.gameOver())) {
            analyzeBoard(board);
            makeMove(gameController);
        }
        tracker.recordGame(board.score(), board.getTotalTicks(), config.randSeed());

        return board.score();
    }

    @Override
    public long randSeed() {
        return seed;
    }

    @Override
    public void initializePlayer(String infile) {
        // TODO Auto-generated method stub
    }

    @Override
    public void storePlayerInfo(String outfile) {
        // TODO Auto-generated method stub
    }
}
