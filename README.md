 EscapeGrid 🚪🏃‍♂️

EscapeGrid is a simulation project to find the safest and fastest evacuation path inside a building during emergency situations like fire outbreaks.

## 🔥 Problem Statement
Simulate a building or floor plan as a 2D grid.  
Given a start position (person) and multiple exits, the system finds the shortest or fastest path to safety, avoiding obstacles like walls, fire zones, or other hazards.

## 🧠 Key Concepts & Data Structures
- **Graph Representation**: Treat the 2D grid as a graph (nodes = grid cells).
- **Shortest Path Algorithms**:
  - **BFS**: Used when all moves have equal cost.
  - **Dijkstra's Algorithm**: Used when some areas have different weights (e.g., smoke, stairs).
- **Priority Queue / Queue**: For efficient path finding.
- **Path Reconstruction**: To display the exact evacuation route.

## 🚀 Features
- Dynamic grid creation
- Multiple exit handling
- Obstacle and hazard mapping
- Fast and safe route calculation
- Visual path output (optional)

## 🛠️ Technologies Used
- Java (Core language)
- Data Structures: Graphs, Queues, Priority Queues

## 📈 Future Enhancements
- Add a GUI visualization
- Real-time fire spread simulation
- Multiple people evacuation
- Exit prioritization based on proximity

> EscapeGrid - Because every second matters during emergencies! 🚨
