package player;

import slither.*;

import java.util.Random;

import org.slf4j.*;

public class PlayerRunner {
    private static final Logger logger = LoggerFactory.getLogger(PlayerRunner.class);

    public static void main(String[] args) {
        GameController gameController = new GameController();
        Random rand = new Random();

        int highestScore = -1;

        var block = 25;
        var boardSize = 21;

        int games = 10;
        int boardSeed = 42;
        int playerSeed = 42;

        for (int i = 1; i <= games; i++) {
            SnakeConfig config = new SnakeConfig(
                    50, boardSize * block, block, false, rand.nextInt(Integer.MAX_VALUE));
            // Player player = new GreedyPlayer(1535439173L);
            Player player = new GreedyPlayer(rand.nextInt(Integer.MAX_VALUE));
            int score = player.play(gameController, config);

            highestScore = Math.max(highestScore, score);

            logger.info("Game: {} Score: {} Highest {}",
                    i, score, highestScore);
        }
    }
}
