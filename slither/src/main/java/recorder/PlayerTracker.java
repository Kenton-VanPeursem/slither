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

    public void recordGame(int score, int gameTicks, int boardSeed) {
        String fname = player.getClass().getSimpleName() + ".csv";

        FileWriter fw = null;
        try {
            File f = new File(fname);
            fw = new FileWriter(fname, true);
            if (f.createNewFile()) {
                fw.write(String.format("Score,Ticks,PlayerSeed,BoardSeed%n"));
            } else {
                logger.debug("Created file already exists");
            }
            fw.write(String.format("%d,%d,%d,%d%n",
                    score, gameTicks, player.randSeed(), boardSeed));
        } catch (IOException e) {
            logger.warn("Error recording score for {}", player.getClass());
        } finally {
            if (fw != null) {
                try {
                    fw.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }
}
