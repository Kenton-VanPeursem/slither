package player;

import slither.*;

import java.util.Random;

import org.slf4j.*;

public class PlayerRunner {
    private static final Logger logger = LoggerFactory.getLogger(PlayerRunner.class);

    private Random rand = new Random();
    private int block;
    private int boardSize;
    private int games;
    private Player player;

    public PlayerRunner(int block, int boardSize, int games, Class<? extends Player> playerClazz) {
        this.block = block;
        this.boardSize = boardSize;
        this.games = games;

        player = playerFactory(playerClazz);
    }

    public SnakeConfig generateSnakeConfig() {
        return new SnakeConfig(
                50, boardSize * block, block, false, rand.nextInt(Integer.MAX_VALUE));
    }

    public int numGames() {
        return games;
    }

    private Player playerFactory(Class<? extends Player> playerClazz) {
        if (playerClazz == GreedyPlayer.class)
                return new GreedyPlayer(rand.nextInt(Integer.MAX_VALUE));
        // default brute force
        return new BruteForcePlayer();
    }

    public int run() {
        GameController gameController = new GameController();
        int highestScore = -1;
        for (int i = 1; i <= games; i++) {
            int score = player.play(gameController, generateSnakeConfig());

            highestScore = Math.max(highestScore, score);

            logger.info("Game: {} Score: {} Highest {}",
                    i, score, highestScore);
        }

        return highestScore;
    }

    public static void main(String[] args) {
        // bug in bruteForce when boardSize is even number
        // PlayerRunner runner = new PlayerRunner(25, 20, 1, BruteForcePlayer.class);

        PlayerRunner runner = new PlayerRunner(25, 20, 1000, GreedyPlayer.class);
        runner.run();
    }
}
