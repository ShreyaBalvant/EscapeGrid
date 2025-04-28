
import java.util.*;

public class Main {

    public static void main(String[] args) {
        Grid.initialize();
        findShortestPath();
    }

    public static void findShortestPath() {
        Grid.visited = new boolean[Grid.rows][Grid.cols];
        PriorityQueue<Cell> pq = new PriorityQueue<>();

        Cell start = findStart();
        if (start == null) {
            System.out.println("Start position not found!");
            return;
        }

        pq.add(start);

        while (!pq.isEmpty()) {
            Cell current = pq.poll();

            if (Grid.visited[current.x][current.y]) continue;
            Grid.visited[current.x][current.y] = true;

            if (Grid.grid[current.x][current.y] == 'E') {
                System.out.println("\nReached an Exit at (" + current.x + "," + current.y + ")!");
                System.out.println("Total Cost (steps taken): " + current.cost);
                reconstructPath(current);
                return; // Stop after reaching the first nearest Exit
            }

            for (int dir = 0; dir < 4; dir++) {
                int newX = current.x + Grid.dx[dir];
                int newY = current.y + Grid.dy[dir];

                if (Grid.isValid(newX, newY)) {
                    pq.add(new Cell(newX, newY, current.cost + Grid.weights[newX][newY], current));
                }
            }
        }

        System.out.println("No path to exit found.");
    }

    public static Cell findStart() {
        for (int i = 0; i < Grid.rows; i++) {
            for (int j = 0; j < Grid.cols; j++) {
                if (Grid.grid[i][j] == 'S') {
                    return new Cell(i, j, 0, null);
                }
            }
        }
        return null;
    }

    public static void reconstructPath(Cell end) {
        List<Cell> path = new ArrayList<>();
        Cell current = end;

        while (current != null) {
            path.add(current);
            current = current.parent;
        }

        Collections.reverse(path);

        System.out.println("\nPath followed:");
        for (Cell cell : path) {
            System.out.print(" -> (" + cell.x + "," + cell.y + ")");
            if (Grid.grid[cell.x][cell.y] == '.')
                Grid.grid[cell.x][cell.y] = '*'; // Mark path
        }

        Grid.printGrid();
    }
}
