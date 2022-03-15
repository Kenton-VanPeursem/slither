package player;

import slither.*;

import java.util.Random;

import org.slf4j.*;

public class PlayerRunner {
    private static final Logger logger = LoggerFactory.getLogger(PlayerRunner.class);

    public static void main(String[] args) {
        Random rand = new Random();
        GameController gameController = new GameController();

        int highestScore = -1;

        var block = 25;
        var boardSize = 21;

        int games = 50;
        int seed = 42;

        SnakeConfig config = new SnakeConfig(50, boardSize * block, block, false);

        for (int i = 1; i <= games; i++) {
            Player player = new GreedyPlayer(Math.abs(rand.nextInt()));
            int score = player.play(gameController, config, seed);

            highestScore = Math.max(highestScore, score);

            logger.info("Game: {} Score: {} Highest {}",
                    i, score, highestScore);
        }
    }
}
