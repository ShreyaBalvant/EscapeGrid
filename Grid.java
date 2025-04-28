import java.util.Scanner;

public class Grid {
    public static char[][] grid;
    public static int[][] weights;
    public static boolean[][] visited;
    public static int rows, cols;

    static int[] dx = {-1, 0, 1, 0};
    static int[] dy = {0, 1, 0, -1};

    public static void initialize() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter number of rows: ");
        rows = scanner.nextInt();

        System.out.print("Enter number of columns: ");
        cols = scanner.nextInt();
        scanner.nextLine(); // consume leftover newline

        grid = new char[rows][cols];
        weights = new int[rows][cols];

        System.out.println("Enter the grid row by row (use . for empty, W for wall, S for start, E for exit, F for fire):");

        for (int i = 0; i < rows; i++) {
            String line = scanner.nextLine();
            for (int j = 0; j < cols; j++) {
                grid[i][j] = line.charAt(j);
            }
        }

        visited = new boolean[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                char cell = grid[i][j];
                if (cell == 'W' || cell == 'F') {
                    weights[i][j] = Integer.MAX_VALUE;
                } else {
                    weights[i][j] = 1;
                }
            }
        }
    }

    public static boolean isValid(int x, int y) {
        return (x >= 0 && x < rows && y >= 0 && y < cols &&
                !visited[x][y] && weights[x][y] != Integer.MAX_VALUE);
    }

    public static void printGrid() {
        System.out.println("\nFinal Building Layout:");
        for (char[] row : grid) {
            for (char c : row) {
                System.out.print(c + " ");
            }
            System.out.println();
        }
    }
}
