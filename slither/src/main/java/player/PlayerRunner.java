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
    private int visualizationDelay = 0;

    private Class<? extends Player> playerClazz;
    private String initialPlayerFile;
    private String exportPlayerFile;
    private boolean playerInTraining;

    public PlayerRunner(
            Class<? extends Player> playerClazz,
            String initialPlayerFile,
            String exportPlayerFile,
            boolean playerInTraining,
            int visualizationDelay,
            int block,
            int boardSize,
            int games) {
        this.initialPlayerFile = initialPlayerFile;
        this.exportPlayerFile = exportPlayerFile;
        this.playerInTraining = playerInTraining;
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
        if (initialPlayerFile != null) {
            player.initializePlayer(initialPlayerFile);
        }
        ((QLearningPlayer) player).setTraining(playerInTraining);

        for (int i = 1; i <= games; i++) {
            int score = player.play(gameController, generateSnakeConfig());
            totalScore += score;
            highestScore = Math.max(highestScore, score);

            logger.info("Game: {} Score: {} Highest: {} Ticks: {} Ave: {}",
                    i, score, highestScore, gameController.getSnake().getTotalTicks(), (totalScore / i));
        }

        long endTime = System.currentTimeMillis();
        logger.info("Took {} hours", ((double) (endTime-startTime)) / 1000 / 60 / 60);

        if (exportPlayerFile != null)
            player.storePlayerInfo(exportPlayerFile);

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

        int prev = 0;
        PlayerRunner runner;
        for (int i = 10000; i <= 1000000; i *= 10) {
            String learnedFile = "QLearningPlayer_" + i + ".csv";
            if (prev < 1) {
                runner = new PlayerRunner(QLearningPlayer.class, null, learnedFile, true, 0, 100, 6, i);
                runner.run();
            } else {
                String prevLearnedFile = "QLearningPlayer_" + prev + ".csv";
                runner = new PlayerRunner(QLearningPlayer.class, prevLearnedFile, learnedFile, true, 0, 100, 6, i);
                runner.run();
            }
            runner = new PlayerRunner(QLearningPlayer.class, learnedFile, null, true, 100, 100, 6, 5);
            runner.run();
            prev = i;
        }
    }
}
