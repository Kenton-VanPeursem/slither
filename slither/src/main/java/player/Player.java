package player;

import slither.GameController;
import slither.Snake;
import slither.SnakeConfig;

public interface Player {
    public void initializePlayer(String infile);
    public void analyzeBoard(Snake board);
    public void makeMove(GameController gameController);
    public int play(GameController gameController, SnakeConfig config);
    public long randSeed();
    public void storePlayerInfo(String outfile);
}
