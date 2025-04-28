public class Cell implements Comparable<Cell> {
    public int x, y, cost;
    public Cell parent;

    public Cell(int x, int y, int cost, Cell parent) {
        this.x = x;
        this.y = y;
        this.cost = cost;
        this.parent = parent;
    }

    @Override
    public int compareTo(Cell other) {
        return Integer.compare(this.cost, other.cost);
    }
}