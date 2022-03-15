package slither;

public class SnakeConfig {
    private long frameSpeedMillis;
    private int windowSize;
    private int blockSize;
    private boolean humanPlayer;

    public SnakeConfig(long frameSpeedMillis, int windowSize, int blockSize, boolean humanPlayer) {
        this.frameSpeedMillis = frameSpeedMillis;
        this.windowSize = windowSize;
        this.blockSize = blockSize;
        this.humanPlayer = humanPlayer;
    }

    public SnakeConfig(long frameSpeedMillis, int windowSize, int blockSize) {
        this.frameSpeedMillis = frameSpeedMillis;
        this.windowSize = windowSize;
        this.blockSize = blockSize;
        this.humanPlayer = true;
    }

    public long getFrameSpeedMillis() {
        return this.frameSpeedMillis;
    }

    public int getWindowSize() {
        return this.windowSize;
    }

    public int getBlockSize() {
        return this.blockSize;
    }

    public boolean isHumanPlayer() {
        return this.humanPlayer;
    }

    @Override
    public String toString() {
        return "SnakeConfig("
                + this.frameSpeedMillis + ", "
                + this.windowSize + ", "
                + this.blockSize + ")";
    }
}
