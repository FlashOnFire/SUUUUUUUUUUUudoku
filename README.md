# SUUUUUUUUUUUudoku

Projet mené par Eymeric Dechelette, Thibaut Laracine et Guillaume Calderon

### Diagramme de cas d'utilisation :

```mermaid
flowchart LR
    subgraph system["system"]
        B(["CreerGrille"])
        C(["AjouterGrille"])
        D(["GenererGrille"])
        G(["ResoudreAutomatiquement"])
        E(["ResoudreGrille"])
        F(["ResoudreManuellement"])
        I(["EtapeDeResolution"])
        H(["Afficher"])
        J(["Grille"])
    end
    B & E & H --- A(["Client"])
    G --> E
    C & D --> B
    F --> E
    I & J ---> H
    F -. &lt ;&lt ; include&gt ; &gt ; .-> G
I -. &lt ; &lt ; include&gt ; &gt ; .-> G
G -. &lt ; &lt ; include&gt ; &gt ; .-> D
```

### Diagramme de classes :

<!-- BEGIN_CLASS -->

```mermaid
classDiagram
    direction BT
    class AbstractConstraint {
        <<Interface>>
        + getPossibilities(Integer[][], Vec2i) Optional~Set~Integer~~
        + getClassicConstraints(int, Set~Integer~) List~AbstractConstraint~
        + isSatisfied(Integer[][]) boolean
        + isAffectedBy(Vec2i, Vec2i) boolean
        + isPosAffected(Vec2i) boolean
    }
    class BlockConstraint {
        + BlockConstraint(Set~Integer~, int, int, int, int)
        - int y
        - int dy
        - int dx
        - int x
        + isSatisfied(Integer[][]) boolean
        + isInBlock(Vec2i) boolean
        - extractBlock(Integer[][]) Set~Integer~
        + isPosAffected(Vec2i) boolean
        + isAffectedBy(Vec2i, Vec2i) boolean
        + getPossibilities(Integer[][], Vec2i) Optional~Set~Integer~~
        int dx
        int dy
        int x
        int y
    }
    class ColumnConstraint {
        + ColumnConstraint(Set~Integer~)
        + isSatisfied(Integer[][]) boolean
        + isAffectedBy(Vec2i, Vec2i) boolean
        + getPossibilities(Integer[][], Vec2i) Optional~Set~Integer~~
        + isPosAffected(Vec2i) boolean
    }
    class CsvUtils {
        + CsvUtils()
        + importGrid(Path) Grid
        + exportGrid(Path, Grid) void
    }
    class Generator {
        + Generator()
        + generate9x9() Grid
    }
    class Grid {
        + Grid(Integer[][], List~AbstractConstraint~, Set~Integer~)
        + Grid(Integer[][], Set~Integer~)
        + Grid(Grid)
        - InnerGrid grid
        - Set~Integer~ symbols
        - List~AbstractConstraint~ constraints
        - HashMap~Vec2i, Set~ Integer~~ emptyCellsPossibilities
        + computeAllEmptyCellsPossibilities() void
        + tryPlace(Vec2i, Integer, boolean) boolean
        + display() void
        + computeChangedEmptyCellsPossibilities(Vec2i, boolean) void
        + areConstraintsSatisfied(boolean) boolean
        + applyNakedPairs() boolean
        + placeUnchecked(Vec2i, Integer, boolean) void
        + length() int
        InnerGrid grid
        boolean solved
        Set~Integer~ symbols
        List~AbstractConstraint~ constraints
        HashMap~Vec2i, Set~ Integer~~ emptyCellsPossibilities
    }
    class InnerGrid {
        + InnerGrid(Integer[][])
        + InnerGrid(InnerGrid)
        - Integer[][] grid
        + hashCode() int
        + equals(Object) boolean
        + display() void
        + computeEmptyCells() HashSet~Vec2i~
        + length() int
        Integer[][] inner
        Integer[][] grid
    }
    class LineConstraint {
        + LineConstraint(Set~Integer~)
        + isPosAffected(Vec2i) boolean
        + isSatisfied(Integer[][]) boolean
        + getPossibilities(Integer[][], Vec2i) Optional~Set~Integer~~
        + isAffectedBy(Vec2i, Vec2i) boolean
    }
    class Main {
        + Main()
        + main(String[]) void
    }
    class NotEmptyConstraint {
        + NotEmptyConstraint()
        + isPosAffected(Vec2i) boolean
        + isSatisfied(Integer[][]) boolean
        + getPossibilities(Integer[][], Vec2i) Optional~Set~Integer~~
        + isAffectedBy(Vec2i, Vec2i) boolean
    }
    class SolvingState {
        <<enumeration>>
        + SolvingState()
        + valueOf(String) SolvingState
        + values() SolvingState[]
    }
    class SudokuBoard {
        ~ SudokuBoard(Grid)
        + update(Integer[][]) void
        + recoverPreviousSudoku(Grid) void
    }
    class SudokuFrame {
        + SudokuFrame(Grid)
    }
    class SudokuOptions {
        + SudokuOptions(Color, Runnable, Runnable)
        - applyMaterialDesign(JButton) void
    }
    class SudokuSolver {
        + SudokuSolver()
        + solve(Grid, boolean, boolean) SolvingState
        - doBacktracking(Grid) List~Grid~
        - solveDeduction(Grid) SolvingState
        - findAllSolutions(Grid, boolean, boolean) List~Grid~
    }
    class SymbolSets {
        + SymbolSets()
        + generateSymbols(int) Set~Integer~
    }
    class Vec2i {
        + Vec2i(int, int)
        - int y
        - int x
        + toString() String
        + hashCode() int
        + equals(Object) boolean
        int x
        int y
    }

    AbstractConstraint ..> BlockConstraint: «create»
    AbstractConstraint ..> ColumnConstraint: «create»
    AbstractConstraint ..> LineConstraint: «create»
    AbstractConstraint ..> NotEmptyConstraint: «create»
    BlockConstraint ..> AbstractConstraint
    ColumnConstraint ..> AbstractConstraint
    CsvUtils ..> Grid: «create»
    Generator ..> Grid: «create»
    Generator ..> Vec2i: «create»
    Grid "1" *--> "constraints *" AbstractConstraint
    Grid "1" *--> "grid 1" InnerGrid
    Grid ..> InnerGrid: «create»
    Grid "1" *--> "emptyCellsPossibilities *" Vec2i
    InnerGrid ..> Vec2i: «create»
    LineConstraint ..> AbstractConstraint
    Main ..> SudokuFrame: «create»
    NotEmptyConstraint ..> AbstractConstraint
    SudokuBoard "1" *--> "grid 1" Grid
    SudokuBoard ..> InnerGrid: «create»
    SudokuFrame ..> SudokuBoard: «create»
    SudokuFrame ..> SudokuOptions: «create»
    SudokuSolver ..> Grid: «create»

```

<!-- END_CLASS -->

### Diagramme de séquence :

```
Todo
```

