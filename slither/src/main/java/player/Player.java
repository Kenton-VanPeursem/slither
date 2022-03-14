package player;

import slither.GameController;
import slither.Snake;

public interface Player {
    public void analyzeBoard(Snake board);
    public void makeMove(GameController gameController);
}
