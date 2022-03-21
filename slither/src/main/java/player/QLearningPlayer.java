package player;

import org.slf4j.*;

import recorder.PlayerTracker;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.BitSet;
import java.util.Random;

import slither.Direction;
import slither.GameController;
import slither.Snake;
import slither.SnakeConfig;

public class QLearningPlayer implements Player {
    private static final Logger logger = LoggerFactory.getLogger(QLearningPlayer.class);
    private long seed;
    private Random rand;

    private double[][] q;
    private int numStates = QState.NUM_STATES;
    private int numActions = 4;

    private double learningRate = 0.1;
    private double discountFactor = 0.99;

    private long episode = 0;
    private boolean training = true;
    private long trainingPeriod = 1000;

    private Direction bestDirection;

    PlayerTracker tracker = new PlayerTracker(this);

    public QLearningPlayer() {
        seed = new Random().nextInt();
        rand = new Random(seed);

        initializeQMatrix();
    }

    public QLearningPlayer(long seed) {
        this.seed = seed;
        rand = new Random(seed);

        initializeQMatrix();
    }

    @Override
    public void analyzeBoard(Snake board) {
        bestDirection = requestDirection(board);
    }

    @Override
    public void makeMove(GameController gameController) {
        gameController.setUserInput(bestDirection);
    }

    @Override
    public int play(GameController gameController, SnakeConfig config) {
        episode++;
        gameController.initSnake(config);

        Snake board = gameController.getSnake();
        gameController.begin();
        int initState;
        int action;
        while(!(gameController.isWinner() || gameController.gameOver())) {
            initState = computeState(board);
            analyzeBoard(board);
            action = directionToAction(bestDirection);
            updateQMatrix(board, initState, action);
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
    public void storePlayerInfo(String outfile) {
        // export QMatrix to a file
        try (FileWriter csvWriter = new FileWriter(outfile)) {
            // numstates,numactions
            csvWriter.append(Integer.toString(numStates));
            csvWriter.append(',');
            csvWriter.append(Integer.toString(numActions));
            csvWriter.append('\n');

            for (int i = 0; i < numStates; i++) {
                for (int j = 0; j < numActions; j++) {
                    if (j != 0) {
                        csvWriter.append(',');
                    }
                    csvWriter.append(Double.toString(q[i][j]));
                }
                csvWriter.append('\n');
            }
        } catch (IOException e) {
            logger.error("Exception writing QMatrix to {}", outfile, e);
        }
    }

    @Override
    public void initializePlayer(String infile) {
        // assign QMatrix from a file
        try (BufferedReader csvReader = new BufferedReader(new FileReader(infile))) {
            String row;
            boolean first = true;
            int i = 0;
            while ((row = csvReader.readLine()) != null) {
                String[] data = row.split(",");
                if (first) {
                    numStates = Integer.parseInt(data[0]);
                    numActions = Integer.parseInt(data[1]);
                    first = false;
                    continue;
                }
                for (int j = 0; j < numActions; j++) {
                    q[i][j] = Double.parseDouble(data[j]);
                }
                i++;
            }
        } catch (Exception e) {
            logger.error("Exception reading QMatrix from {}", infile, e);
        }
    }

    public void setTraining(boolean training) {
        this.training = training;
    }

    private void initializeQMatrix() {
        logger.debug("Initialize q matrix to all zeros ({}, {})", numStates, numActions);
        q = new double[numStates][numActions];
        for (int i = 0; i < numStates; i++) {
            for (int j = 0; j < numActions; j++) {
                q[i][j] = 0.0;
            }
        }
    }

    private boolean shouldExplore() {
        if (!training) {
            return false;
        }
        // exploration 20% of the time in first 100,000 episodes otherwise only 5% of time
        return (episode < trainingPeriod && rand.nextInt(5) == 1) || rand.nextInt(20) == 1;
    }

    private Direction requestDirection(Snake board) {
        Direction exploreDirection = actionToDirection(rand.nextInt(4));

        // find probabilistic best move
        int qt = computeState(board);
        double maxVal = q[qt][0];
        int maxI = 0;
        for (int i = 1; i < numActions; i++) {
            if (maxVal < q[qt][i]) {
                maxVal = q[qt][i];
                maxI = i;
            }
        }

        Direction probDirection = actionToDirection(maxI);

        logger.debug("Random Explore Direction: {} Probabilistic Dir: {}", exploreDirection, probDirection);
        if (shouldExplore()) {
            return exploreDirection;
        }
        return probDirection;
    }

    private int updateQMatrix(Snake board, int state, int action) {
        logger.debug("Update Q matrix with move");
        Snake fauxBoard = new Snake(board);
        fauxBoard.setDirection(actionToDirection(action));
        fauxBoard.move();
        double reward = computeReward(fauxBoard);

        int nextState = computeState(fauxBoard);
        double maxNext = maxOfState(nextState);
        double oldVal = q[state][action];

        q[state][action] = oldVal + learningRate * (reward + discountFactor * maxNext - oldVal);

        return state;
    }

    private double maxOfState(int state) {
        double maxVal = Double.MIN_VALUE;

        for (int i = 0; i < numActions; i++) {
            if (maxVal < q[state][i]) {
                maxVal = q[state][i];
            }
        }
        return maxVal;
    }

    private int computeState(Snake board) {
        var head = board.getHeadPosition();
        var apple = board.applePosition();

        QState state = new QState();
        if (board.isWinner()) {
            return state.get();
        }
        // apple horizontal direction
        if (head.getX() < apple.getX())
            state.setAppleRight();
        else if (head.getX() > apple.getX())
            state.setAppleLeft();

        // apple vertical direction
        if (head.getY() < apple.getY())
            state.setAppleDown();
        else if (head.getY() > apple.getY())
            state.setAppleUp();

        // previous direction
        state.setPrevDirection(board.currentDirection());

        // set left and right walls
        if (head.getX() == 0)
            state.setWallLeft();
        else if (head.getX() == board.maxX() - 1)
            state.setWallRight();

        // set top and bottom walls
        if (head.getY() == 0)
            state.setWallUp();
        else if (head.getY() == board.maxY() - 1)
            state.setWallDown();

        // set available positions
        for (Direction d : Direction.values()) {
            if (directionAvailable(board, d)) {
                state.setAvailable(d);
            }
        }

        var stateVal = state.get();
        logger.debug("with state: {}", stateVal);
        return stateVal;
    }

    private boolean directionAvailable(Snake start, Direction direction) {
        Snake snakeLeft = new Snake(start);
        snakeLeft.setDirection(direction);
        snakeLeft.move();
        return !(snakeLeft.didCollide() && snakeLeft.outOfBounds());
    }

    private double computeReward(Snake board) {
        if (board.didCollide() || board.outOfBounds())
            return -100.0;
        if (board.ateOnLastStep())
            return 1000.0;

        return -1.0;
    }

    private int directionToAction(Direction direction) {
        switch (direction) {
            case LEFT:
                return 0;
            case UP:
                return 1;
            case DOWN:
                return 2;
            case RIGHT:
                return 3;
        }

        return 0;
    }

    private Direction actionToDirection(int actionIndex) {
        switch (actionIndex) {
            case 0:
                return Direction.LEFT;
            case 1:
                return Direction.UP;
            case 2:
                return Direction.DOWN;
            case 3:
            default:
                return Direction.RIGHT;
        }
    }

    private class QState {
        private BitSet stateBits = new BitSet(14);
        private static final int NUM_STATES = 16383;

        public void setAppleLeft() {
            stateBits.set(0);
        }

        public void setAppleUp() {
            stateBits.set(1);
        }

        public void setAppleDown() {
            stateBits.set(2);
        }

        public void setAppleRight() {
            stateBits.set(3);
        }

        public void setPrevDirection(Direction direction) {
            switch (direction) {
                case LEFT:
                    stateBits.clear(4);
                    stateBits.clear(5);
                    break;
                case UP:
                    stateBits.clear(4);
                    stateBits.set(5);
                    break;
                case DOWN:
                    stateBits.set(4);
                    stateBits.clear(5);
                    break;
                case RIGHT:
                    stateBits.set(4);
                    stateBits.set(5);
                    break;
            }
        }

        public void setWallLeft() {
            stateBits.set(6);
        }

        public void setWallUp() {
            stateBits.set(7);
        }

        public void setWallDown() {
            stateBits.set(8);
        }

        public void setWallRight() {
            stateBits.set(9);
        }

        public void setAvailable(Direction direction) {
            switch (direction) {
                case LEFT:
                    setLeftAvailable();
                    break;
                case UP:
                    setUpAvailable();
                    break;
                case DOWN:
                    setDownAvailable();
                    break;
                case RIGHT:
                    setRightAvailable();
            }
        }

        private void setLeftAvailable() {
            stateBits.set(10);
        }

        private void setUpAvailable() {
            stateBits.set(11);
        }

        private void setDownAvailable() {
            stateBits.set(12);
        }

        private void setRightAvailable() {
            stateBits.set(13);
        }

        public int get() {
            int stateVal = 0;
            for (int i = 0; i < stateBits.size(); i++) {
                if (stateBits.get(i))
                    stateVal += Math.pow(2, i);
            }
            return stateVal;
        }
    }
}
