package player;

import org.slf4j.*;

import slither.*;

public class BruteForcePlayer implements Player {
    private static final Logger logger = LoggerFactory.getLogger(BruteForcePlayer.class);
    Direction bestDirection;

    @Override
    public void analyzeBoard(Snake board) {
        var head = board.getHeadPosition();

        if (head.getX() == board.maxX() - 1) {
            if (head.getY() == board.maxY() - 1) {
                bestDirection = Direction.LEFT;
            } else {
                bestDirection = Direction.DOWN;
            }
        } else if (head.getY() == board.maxY() - 1) {
            if (head.getX() == 0) {
                bestDirection = Direction.UP;
            } else {
                bestDirection = Direction.LEFT;
            }
        } else if (head.getX() % 2 == 0) {
            if (head.getY() == 0) {
                bestDirection = Direction.RIGHT;
            } else {
                bestDirection = Direction.UP;
            }
        } else {
            if (head.getY() < board.maxY() - 2) {
                bestDirection = Direction.DOWN;
            } else {
                bestDirection = Direction.RIGHT;
            }
        }
    }

    @Override
    public void makeMove(GameController gameController) {
        gameController.setUserInput(bestDirection);
    }

    public static void main(String[] args) {
        BruteForcePlayer player = new BruteForcePlayer();

        GameController gameController = new GameController();

        // won in 59163
        SnakeConfig config = new SnakeConfig(10, 1100, 50);
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
