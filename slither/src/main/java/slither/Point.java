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

    public Point copy() {
        return new Point(x, y);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Point)) {
            return false;
        }

        return ((Point) o).getX() == x && ((Point) o).getY() == y;
    }

    @Override
    public int hashCode() {
        // map (x,y) to [0,1,000,000]
        return y*1000 + x;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
