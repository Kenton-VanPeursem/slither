package recorder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.slf4j.*;

import player.*;

public class PlayerTracker {
    private static final Logger logger = LoggerFactory.getLogger(PlayerTracker.class);
    private Player player;

    public PlayerTracker(Player player) {
        this.player = player;
    }

    public void recordGame(int score, int gameTicks, long boardSeed) {
        String fname = player.getClass().getSimpleName() + ".csv";

        File f = new File(fname);
        boolean needsHeader = !f.exists();

        try (FileWriter fw = new FileWriter(fname, true)) {
            if (needsHeader) {
                fw.write(String.format("Score,Ticks,PlayerSeed,BoardSeed%n"));
            }
            fw.write(String.format("%d,%d,%d,%d%n",
                    score, gameTicks, player.randSeed(), boardSeed));
        } catch (IOException e) {
            logger.warn("Error recording score for {}", player.getClass());
        }
    }
}
