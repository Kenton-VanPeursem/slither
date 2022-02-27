package slither;

public class Point {
    private int x;
    private int y;

    Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void incrementX(int delta) {
        x += delta;
    }

    public void incrementY(int delta) {
        y += delta;
    }

    public void decrementX(int delta) {
        x -= delta;
    }

    public void decrementY(int delta) {
        y -= delta;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Point)) {
            return false;
        }

        return ((Point) o).getX() == this.getX() || ((Point) o).getY() == this.getY();
    }
}
