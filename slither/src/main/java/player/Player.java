package player;

import slither.GameController;
import slither.Snake;
import slither.SnakeConfig;

public interface Player {
    public void analyzeBoard(Snake board);
    public void makeMove(GameController gameController);
    public int play(GameController gameController, SnakeConfig config, int seed);
    public int randSeed();
}
