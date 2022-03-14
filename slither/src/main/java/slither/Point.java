package slither;

public class Point {
    private int x;
    private int y;

    public Point(int x, int y) {
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

    public static Point nextPoint(Point p1, Point p2) {
        int xDir = p2.getX() - p1.getX();
        int xDiff = Math.abs(xDir);

        int yDir = p2.getY()-p1.getY();
        int yDiff = Math.abs(yDir);

        if (xDiff > yDiff) {
            if (xDir < 0)
                return new Point(p1.getX() - 1, p1.getY());
            else
                return new Point(p1.getX() + 1, p1.getY());
        }
        if (xDiff < yDiff) {
            if (yDir < 0)
                return new Point(p1.getX(), p1.getY() - 1);
            else
                return new Point(p1.getX(), p1.getY() + 1);
        }

        return p2.copy();
    }

    public static double distance(Point p1, Point p2) {
        double x2 = Math.pow((p2.getX() - p1.getX()), 2);
        double y2 = Math.pow((p2.getY() - p1.getY()), 2);

        return Math.sqrt(x2 + y2);
    }
}
