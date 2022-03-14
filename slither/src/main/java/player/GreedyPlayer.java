package player;

import org.slf4j.*;

import slither.*;

public class GreedyPlayer implements Player {
    private static final Logger logger = LoggerFactory.getLogger(GreedyPlayer.class);

    private Direction bestDirection;

    @Override
    public void analyzeBoard(Snake board) {
        var head = board.getHeadPosition().copy();
        var apple = board.applePosition();

        var optimalDirection = nextGreedyDirection(head, apple);

        if (optimalDirection
        if (board.getAvailablePositions();
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

        SnakeConfig config = new SnakeConfig(50, 1100, 50);
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
    }
}
