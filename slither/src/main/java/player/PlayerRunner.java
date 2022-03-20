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
    private Class<? extends Player> playerClazz;
    private int visualizationDelay = 0;

    public PlayerRunner(
            Class<? extends Player> playerClazz,
            int visualizationDelay,
            int block,
            int boardSize,
            int games) {
        this.visualizationDelay = visualizationDelay;
        this.block = block;
        this.boardSize = boardSize;
        this.games = games;

        this.playerClazz = playerClazz;
    }

    public SnakeConfig generateSnakeConfig() {
        return generateSnakeConfig(rand.nextInt(Integer.MAX_VALUE));
    }

    public SnakeConfig generateSnakeConfig(long boardSeed) {
        return new SnakeConfig(
                visualizationDelay,
                boardSize * block,
                block,
                false,
                boardSeed);
    }

    public int numGames() {
        return games;
    }

    private Player playerFactory(Class<? extends Player> playerClazz) {
        return PlayerRunner.playerFactory(playerClazz, rand.nextInt(Integer.MAX_VALUE));
    }

    private static Player playerFactory(Class<? extends Player> playerClazz, long playerSeed) {
        if (playerClazz == GreedyPlayer.class)
            return new GreedyPlayer(playerSeed);
        if (playerClazz == QLearningPlayer.class)
            return new QLearningPlayer(playerSeed);
        // default brute force
        return new BruteForcePlayer();
    }

    public int run() {
        long startTime = System.currentTimeMillis();
        GameController gameController = new GameController();
        int highestScore = -1;
        double totalScore = 0.0;

        var player = playerFactory(playerClazz);
        player.initializePlayer("QLearningPlayer_0318b.csv");

        for (int i = 1; i < games; i++) {
            int score = player.play(gameController, generateSnakeConfig());
            totalScore += score;
            highestScore = Math.max(highestScore, score);

            logger.info("Game: {} Score: {} Highest: {} Ticks: {} Ave: {}",
                    i, score, highestScore, gameController.getSnake().getTotalTicks(), (totalScore / i));
        }

        long endTime = System.currentTimeMillis();
        logger.info("Took {} hours", ((double) (endTime-startTime)) / 1000 / 60 / 60);

        return highestScore;
    }

    public void replay(long boardSeed, long playerSeed) {
        GameController gameController = new GameController();
        var player = PlayerRunner.playerFactory(playerClazz, playerSeed);
        player.play(gameController, generateSnakeConfig(boardSeed));
    }

    public static void main(String[] args) {
        // bug in bruteForce when boardSize is odd number
        // PlayerRunner runner = new PlayerRunner(25, 20, 1, BruteForcePlayer.class);

        PlayerRunner runner = new PlayerRunner(QLearningPlayer.class, 0, 100, 6, 100);
        runner.run();
    }
}
