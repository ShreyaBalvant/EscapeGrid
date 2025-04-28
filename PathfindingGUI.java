import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.PriorityQueue;
import java.util.List;
import javax.swing.Timer;
import java.util.Arrays;



public class PathfindingGUI extends JFrame {
    private static final int CELL_SIZE = 40;
    private static final String[] CELL_TYPES = {"Empty (.)", "Wall (W)", "Start (S)", "Exit (E)", "Fire (F)"};
    private static final Color[] CELL_COLORS = {
            new Color(240, 240, 240), new Color(50, 50, 50), 
            new Color(76, 175, 80), new Color(33, 150, 243), 
            new Color(244, 67, 54)
    };
    
    private int rows = 10, cols = 10;
    private char[][] grid;
    private JPanel gridPanel, gridContainer;
    private JComboBox<String> cellTypeSelector;
    private JButton solveButton, resetButton;
    private JLabel statusLabel;
    private boolean pathFound = false;
    private Color pathColor = new Color(255, 235, 59);

    public PathfindingGUI() {
        super("Pathfinding Application");
        setUILookAndFeel();
        initializeUI();
        createNewGrid(rows, cols);
    }

    private void setUILookAndFeel() {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } 
        catch (Exception e) { e.printStackTrace(); }
    }

    private void initializeUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top Controls
        JPanel controlPanel = new JPanel(new BorderLayout());
        JPanel gridSizePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        gridSizePanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Grid Size"));
        
        JLabel rowsLabel = new JLabel("Rows:");
        JSpinner rowsSpinner = new JSpinner(new SpinnerNumberModel(rows, 3, 30, 1));
        rowsSpinner.addChangeListener(e -> rows = (Integer) rowsSpinner.getValue());
        
        JLabel colsLabel = new JLabel("Columns:");
        JSpinner colsSpinner = new JSpinner(new SpinnerNumberModel(cols, 3, 30, 1));
        colsSpinner.addChangeListener(e -> cols = (Integer) colsSpinner.getValue());
        
        JButton newGridButton = new JButton("Create New Grid");
        newGridButton.setBackground(new Color(220, 220, 220));
        newGridButton.addActionListener(e -> createNewGrid(rows, cols));

        gridSizePanel.add(rowsLabel);
        gridSizePanel.add(rowsSpinner);
        gridSizePanel.add(Box.createHorizontalStrut(10));
        gridSizePanel.add(colsLabel);
        gridSizePanel.add(colsSpinner);
        gridSizePanel.add(Box.createHorizontalStrut(10));
        gridSizePanel.add(newGridButton);

        // Cell type panel
        JPanel cellTypePanel = new JPanel();
        cellTypePanel.setLayout(new BoxLayout(cellTypePanel, BoxLayout.Y_AXIS));
        cellTypePanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Cell Type"));
        
        cellTypeSelector = new JComboBox<>(CELL_TYPES);
        cellTypePanel.add(cellTypeSelector);
        cellTypePanel.add(Box.createVerticalStrut(10));
        
        for (int i = 0; i < CELL_TYPES.length; i++) {
            JPanel sample = new JPanel();
            sample.setPreferredSize(new Dimension(15, 15));
            sample.setBackground(CELL_COLORS[i]);
            sample.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            
            JPanel legend = new JPanel();
            legend.setLayout(new BoxLayout(legend, BoxLayout.X_AXIS));
            legend.add(sample);
            legend.add(Box.createHorizontalStrut(5));
            legend.add(new JLabel(CELL_TYPES[i].split(" ")[0]));
            
            cellTypePanel.add(legend);
            cellTypePanel.add(Box.createVerticalStrut(5));
        }

        // Path sample
        JPanel pathSample = new JPanel();
        pathSample.setPreferredSize(new Dimension(15, 15));
        pathSample.setBackground(pathColor);
        pathSample.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        JPanel pathLegend = new JPanel();
        pathLegend.setLayout(new BoxLayout(pathLegend, BoxLayout.X_AXIS));
        pathLegend.add(pathSample);
        pathLegend.add(Box.createHorizontalStrut(5));
        pathLegend.add(new JLabel("Path"));
        cellTypePanel.add(pathLegend);

        // Action panel
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        actionPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Actions"));

        solveButton = new JButton("Find Path");
        solveButton.setBackground(new Color(76, 175, 80));
        solveButton.addActionListener(e -> findPath());
        solveButton.setToolTipText("Click to find shortest path!");

        resetButton = new JButton("Reset Path");
        resetButton.setBackground(new Color(244, 67, 54));
        resetButton.addActionListener(e -> resetPath());
        resetButton.setEnabled(false);
        resetButton.setToolTipText("Click to clear the drawn path.");

        actionPanel.add(solveButton);
        actionPanel.add(resetButton);

        JPanel topControls = new JPanel(new GridLayout(1, 3, 10, 10));
        topControls.add(gridSizePanel);
        topControls.add(cellTypePanel);
        topControls.add(actionPanel);
        controlPanel.add(topControls, BorderLayout.CENTER);

        // Status
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBorder(BorderFactory.createEtchedBorder());
        statusLabel = new JLabel("Select grid size and create a grid to start.");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        statusPanel.add(statusLabel, BorderLayout.CENTER);

        // Grid container
        gridContainer = new JPanel(new BorderLayout());
        gridContainer.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Grid Layout"));

        gridPanel = new JPanel();
        gridContainer.add(new JScrollPane(gridPanel), BorderLayout.CENTER);

        // Instructions
        JPanel instructionsPanel = new JPanel(new BorderLayout());
        instructionsPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Instructions"));

        JTextArea instructionsText = new JTextArea(
            "1. Create a grid\n2. Set Start (S), Exit (E)\n3. Place Walls (W) and Fire (F)\n4. Click 'Find Path'\n5. Watch the animation!\n6. Reset if needed."
        );
        instructionsText.setEditable(false);
        instructionsText.setWrapStyleWord(true);
        instructionsText.setLineWrap(true);
        instructionsPanel.add(instructionsText, BorderLayout.CENTER);
        instructionsPanel.setPreferredSize(new Dimension(220, 0));

        mainPanel.add(controlPanel, BorderLayout.NORTH);
        mainPanel.add(gridContainer, BorderLayout.CENTER);
        mainPanel.add(statusPanel, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);
        add(instructionsPanel, BorderLayout.EAST);

        setMinimumSize(new Dimension(850, 550));
        pack();
        setLocationRelativeTo(null);
    }

    private void createNewGrid(int rows, int cols) {
        grid = new char[rows][cols];
        for (int i = 0; i < rows; i++) {
            Arrays.fill(grid[i], '.');
        }
        pathFound = false;
        resetButton.setEnabled(false);
        solveButton.setEnabled(true);
        statusLabel.setText("Grid created. Place Start (S), Exit (E), and obstacles.");
        updateGridPanel();
    }

    private void updateGridPanel() {
        gridPanel.removeAll();
        gridPanel.setLayout(new GridLayout(rows, cols, 1, 1));
        gridPanel.setBackground(Color.DARK_GRAY);

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                JPanel cell = createCell(i, j);
                gridPanel.add(cell);
            }
        }
        gridPanel.revalidate();
        gridPanel.repaint();
        pack();
    }

    private JPanel createCell(final int row, final int col) {
        JPanel cell = new JPanel(new BorderLayout());
        cell.setPreferredSize(new Dimension(CELL_SIZE, CELL_SIZE));
        cell.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));

        updateCellAppearance(cell, grid[row][col]);

        cell.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                if (!pathFound) cell.setBorder(BorderFactory.createLineBorder(Color.BLUE, 2));
            }
            @Override public void mouseExited(MouseEvent e) {
                cell.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));
            }
            @Override public void mouseClicked(MouseEvent e) {
                if (pathFound) return;
                String selected = (String) cellTypeSelector.getSelectedItem();
                char cellType = selected.charAt(selected.indexOf('(') + 1);
                if (cellType == 'S') clearExistingCell('S');
                grid[row][col] = cellType;
                updateCellAppearance(cell, cellType);
                cell.setToolTipText("(" + row + "," + col + ") " + getCellTypeName(cellType));
                statusLabel.setText("Set (" + row + "," + col + ") to " + selected);
            }
        });
        return cell;
    }

    private void updateCellAppearance(JPanel cell, char type) {
        cell.removeAll();
        Color bgColor = CELL_COLORS[0];
        switch (type) {
            case '.': bgColor = CELL_COLORS[0]; break;
            case '-': bgColor = new Color(255, 250, 205); break;
            case 'W': bgColor = CELL_COLORS[1]; break;
            case 'S': bgColor = CELL_COLORS[2]; break;
            case 'E': bgColor = CELL_COLORS[3]; break;
            case 'F': bgColor = CELL_COLORS[4]; break;
            case '*': bgColor = pathColor; break;
        }
        cell.setBackground(bgColor);

        JLabel label = new JLabel(String.valueOf(type), SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        label.setForeground((type == 'W') ? Color.WHITE : Color.BLACK);
        if (type == '*') label.setForeground(new Color(150, 90, 0));
        cell.add(label, BorderLayout.CENTER);

        cell.revalidate();
        cell.repaint();
    }

    private void findPath() {
        if (!validateGrid()) return;
        Grid.rows = rows;
        Grid.cols = cols;
        Grid.grid = grid;
        Grid.weights = new int[rows][cols];
        Grid.visited = new boolean[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                char c = grid[i][j];
                Grid.weights[i][j] = (c == 'W' || c == 'F') ? Integer.MAX_VALUE : 1;
            }
        }
        findShortestPath();
    }

    private void resetPath() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (grid[i][j] == '*' || grid[i][j] == '-') grid[i][j] = '.';
            }
        }
        updateGridPanel();
        pathFound = false;
        solveButton.setEnabled(true);
        resetButton.setEnabled(false);
        statusLabel.setText("Path cleared. Modify grid or find a new path.");
    }

    private boolean validateGrid() {
        boolean hasStart = false, hasExit = false;
        for (char[] row : grid) {
            for (char c : row) {
                if (c == 'S') hasStart = true;
                if (c == 'E') hasExit = true;
            }
        }
        if (!hasStart || !hasExit) {
            JOptionPane.showMessageDialog(this, "Place at least one Start and one Exit!", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    private void findShortestPath() {
        PriorityQueue<Cell> pq = new PriorityQueue<>();
        Cell start = findStart();
        if (start == null) {
            statusLabel.setText("Start not found!");
            return;
        }
        pq.add(start);
        List<Cell> explorationOrder = new ArrayList<>();

        while (!pq.isEmpty()) {
            Cell current = pq.poll();
            if (Grid.visited[current.x][current.y]) continue;
            Grid.visited[current.x][current.y] = true;
            explorationOrder.add(current);

            if (Grid.grid[current.x][current.y] == 'E') {
                animatePathDrawing(current, explorationOrder);
                return;
            }

            for (int dir = 0; dir < 4; dir++) {
                int nx = current.x + Grid.dx[dir];
                int ny = current.y + Grid.dy[dir];
                if (isValid(nx, ny)) {
                    pq.add(new Cell(nx, ny, current.cost + Grid.weights[nx][ny], current));
                }
            }
        }
        statusLabel.setText("No path to Exit found.");
    }

    private void animatePathDrawing(Cell endCell, List<Cell> exploredCells) {
        Timer explorationTimer = new Timer(30, null);
        final int[] idx = {0};
        explorationTimer.addActionListener(e -> {
            if (idx[0] < exploredCells.size()) {
                Cell c = exploredCells.get(idx[0]++);
                if (Grid.grid[c.x][c.y] == '.') Grid.grid[c.x][c.y] = '-';
                updateGridPanel();
            } else {
                ((Timer) e.getSource()).stop();
                animateFinalPath(endCell);
            }
        });
        explorationTimer.start();
    }

    private void animateFinalPath(Cell endCell) {
        List<Cell> path = new ArrayList<>();
        for (Cell c = endCell; c != null; c = c.parent) path.add(c);
        Collections.reverse(path);

        Timer pathTimer = new Timer(100, null);
        final int[] idx = {0};
        pathTimer.addActionListener(e -> {
            if (idx[0] < path.size()) {
                Cell c = path.get(idx[0]++);
                if (Grid.grid[c.x][c.y] == '.' || Grid.grid[c.x][c.y] == '-') Grid.grid[c.x][c.y] = '*';
                updateGridPanel();
            } else {
                ((Timer) e.getSource()).stop();
                grid = Grid.grid;
                pathFound = true;
                solveButton.setEnabled(false);
                resetButton.setEnabled(true);
                statusLabel.setText("Path animation complete!");
            }
        });
        pathTimer.start();
    }

    private boolean isValid(int x, int y) {
        return (x >= 0 && x < rows && y >= 0 && y < cols && 
                !Grid.visited[x][y] && Grid.weights[x][y] != Integer.MAX_VALUE);
    }

    private Cell findStart() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (grid[i][j] == 'S') return new Cell(i, j, 0, null);
            }
        }
        return null;
    }

    private void clearExistingCell(char type) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (grid[i][j] == type) grid[i][j] = '.';
            }
        }
        updateGridPanel();
    }

    private String getCellTypeName(char type) {
        switch (type) {
            case 'S': return "Start";
            case 'E': return "Exit";
            case 'W': return "Wall";
            case 'F': return "Fire";
            case '.': return "Empty";
            case '*': return "Path";
            case '-': return "Explored";
            default: return "Unknown";
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new PathfindingGUI().setVisible(true);
        });
    }
}
