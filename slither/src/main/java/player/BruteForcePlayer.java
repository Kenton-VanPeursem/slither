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

    @Override
    public int play(GameController gameController, SnakeConfig config) {
        gameController.initSnake(config);

        Snake board = gameController.getSnake();
        gameController.begin();
        while(!(gameController.isWinner() || gameController.gameOver())) {
            analyzeBoard(board);
            makeMove(gameController);
        }

        return board.score();
    }

    @Override
    public long randSeed() {
        // no random seed for BruteForcePlayer
        return -1;
    }

    @Override
    public void initializePlayer(String infile) {
        // BruteForcePlayer requires no initialization
    }

    @Override
    public void storePlayerInfo(String outfile) {
        // BruteForcePlayer has nothing to store
    }
}
