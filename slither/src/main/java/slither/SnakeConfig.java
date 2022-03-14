package slither;

public class SnakeConfig {
    private long frameSpeedMillis;
    private int windowSize;
    private int blockSize;

    public SnakeConfig(long frameSpeedMillis, int windowSize, int blockSize) {
        this.frameSpeedMillis = frameSpeedMillis;
        this.windowSize = windowSize;
        this.blockSize = blockSize;
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

    @Override
    public String toString() {
        return "SnakeConfig("
                + this.frameSpeedMillis + ", "
                + this.windowSize + ", "
                + this.blockSize + ")";
    }
}
