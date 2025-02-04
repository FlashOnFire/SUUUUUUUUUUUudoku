# SUUUUUUUUUUUudoku

Projet mené par Eymeric Dechelette, Thibaut Laracine et Guillaume Calderon

## Fonctionnalités

Ce SUUUUUUUUUUUudoku dispose des fonctionalité suivantes :

- Génération de grille

  Vous pouvez générer une grille de sudoku de taille avec des performance résonnable jusqu'a 16\*16

- Résolution de grille

  Vous pouvez résoudre une grille de sudoku de taille avec des performance résonnable jusqu'a 100\*100
  Vous pouvez aussi résoudre des multi-doku (prés codé via des fichier dans le dossier
  `src/test/resources`)

- Consulter l'historique des modifications

  Vous pouvez consulter l'historique des modifications de la grille (particulièrement utile pour les résolutions
  automatique)

- Résolution manuelle

  Vous pouvez résoudre une grille de sudoku de manière manuelle

- Interface graphique

  Vous pouvez utiliser une interface graphique pour jouer au sudokus

- Interface en ligne de commande

  Vous pouvez utiliser une interface en ligne de commande pour jouer au sudokus

## Utilisation

### Lancer le programme

#### Utilisateur nix

La methode privilégiée pour lancer le programme est d'utiliser nix, car celui-ci vous assure d'avoir un environement
identique au autre utilisateur de nix.

Si vous disposez de nix, vous pouvez lancer la compilation avec la commande suivante :

```bash
nix build
```

Vous pouvez ensuite lancer les differents executables avec les commande suivante :

```bash
./result/bin/tui # Interface en ligne de commande
./result/bin/imGUI # Interface graphique avec imGUI
./result/bin/swing # Interface graphique avec swing
```

#### Utilisateur linux classique

Il vous faudra installer les dépendances suivantes :

- `openjdk-23`
- `gradle`
- `libGL`

Vous pouvez ensuite lancer la compilation avec la commande suivante :

```bash
./gradlew buildAllJars
```

Vous pouvez ensuite lancer les differents executables avec les commande suivante :

```bash
java -jar ./build/libs/imGUI-1.0-SNAPSHOT.jar # Interface en ligne de commande
java -jar ./build/libs/tui-1.0-SNAPSHOT.jar # Interface graphique avec imGUI
java -jar ./build/libs/swing-1.0-SNAPSHOT.jar # Interface graphique avec swing
```

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
        + getClassicConstraints(int, Set~Integer~) List~AbstractConstraint~
        + getPossibilities(InnerGrid, Vec2i) Optional~Set~Integer~~
        + getRectConstraints(int, int, Set~Integer~) List~AbstractConstraint~
        + isAffectedBy(Vec2i, Vec2i) boolean
        + isPosAffected(Vec2i) boolean
        + isSatisfied(InnerGrid) boolean
    }
    class BlockConstraint {
        + BlockConstraint(Set~Integer~, Box2D)
        - extractBlock(InnerGrid) List~Integer~
        + getPossibilities(InnerGrid, Vec2i) Optional~Set~Integer~~
        + isAffectedBy(Vec2i, Vec2i) boolean
        + isInBlock(Vec2i) boolean
        + isPosAffected(Vec2i) boolean
        + isSatisfied(InnerGrid) boolean
        Box2D block
    }
    class Box2D {
        + Box2D(int, int, int, int)
        + absolute(int, int, int, int) Box2D
        + contains(int, int) boolean
        + contains(Vec2i) boolean
        + dx() int
        + dy() int
        + equals(Object) boolean
        + hashCode() int
        + height() int
        + overlap(Box2D) Box2D
        + substract(Box2D) Box2D
        + width() int
        + x() int
        + y() int
    }
    class ColumnConstraint {
        + ColumnConstraint(Set~Integer~)
        + getPossibilities(InnerGrid, Vec2i) Optional~Set~Integer~~
        + isAffectedBy(Vec2i, Vec2i) boolean
        + isPosAffected(Vec2i) boolean
        + isSatisfied(InnerGrid) boolean
    }
    class CsvUtils {
        + CsvUtils()
        + exportGrid(Path, Grid) void
        + exportMultiGrid(Path, MultiGrid) void
        + importGrid(Path) Grid
        + importMultiGrid(Path) MultiGrid
    }
    class GeneralSymbolConstraint {
        + GeneralSymbolConstraint(Set~Integer~, Vec2i[])
        - Vec2i[] positionList
        - extractValues(InnerGrid) Set~Integer~
        + getPossibilities(InnerGrid, Vec2i) Optional~Set~Integer~~
        + isAffectedBy(Vec2i, Vec2i) boolean
        - isInPositionList(Vec2i) boolean
        + isPosAffected(Vec2i) boolean
        + isSatisfied(InnerGrid) boolean
        Vec2i[] positionList
    }
    class Generator {
        + Generator()
        ~ createRandomConstraints(Grid) List~AbstractConstraint~
        - createSolvedSudoku(int, int) Grid
        - findDividers(int) Vec2i
        + generateClassicSudoku(int) Grid
        + generateSudokuWithBlockConstraints(int, int) Grid
        + generateSudokuWithRandomBlockConstraint(int) Grid
        - removeRandomCells(Grid, int) Grid
    }
    class Grid {
        + Grid(Integer[][], Set~Integer~)
        + Grid(Grid)
        + Grid(Integer[][], Set~Integer~, int, int)
        + Grid(Integer[][], List~AbstractConstraint~, Set~Integer~)
        ~ List~AbstractConstraint~ constraints
        - HashMap~Vec2i, Set~ Integer~~ emptyCellsPossibilities
        - InnerGrid innerGrid
        - ArrayList~Move2i~ moves
        + applyNakedPairs() boolean
        + areConstraintsSatisfied(boolean) boolean
        + computeAllEmptyCellsPossibilities() void
        + computeChangedEmptyCellsPossibilities(Vec2i, boolean) void
        + display() void
        + getSymbolAt(Vec2i) Integer
        + getSymbolAt(int, int) Integer
        + length() int
        + placeUnchecked(Vec2i, Integer, boolean, boolean) void
        + shallowCopy() Grid
        + tryPlace(Vec2i, Integer, boolean, boolean) boolean
        List~AbstractConstraint~ constraints
        HashMap~Vec2i, Set~ Integer~~ emptyCellsPossibilities
        InnerGrid innerGrid
        List~Move2i~ moves
        Vec2i size
        Set~Integer~ symbols
    }
    class GridListener {
        <<Interface>>
        + onGridChange(InnerGrid) void
    }
    class ImGUIFrame {
        + ImGUIFrame()
        - applyLastChanges() void
        # configure(Configuration) void
        - drawGrid(Vec2i, ImVec2) void
        - handleInput() void
        - keyPress(int) void
        + main(String[]) void
        # preRun() void
        + process() void
        - setSelection(int, int) void
        Integer? currentSymbol
    }
    class InnerGrid {
        + InnerGrid(Integer[][])
        + InnerGrid(InnerGrid)
        + at(Vec2i) Integer
        + computeEmptyCells() HashSet~Vec2i~
        + display() void
        + equals(Object) boolean
        + get() Integer[][]
        + hashCode() int
        + length() int
        + set(Vec2i, Integer) void
    }
    class LineConstraint {
        + LineConstraint(Set~Integer~)
        + getPossibilities(InnerGrid, Vec2i) Optional~Set~Integer~~
        + isAffectedBy(Vec2i, Vec2i) boolean
        + isPosAffected(Vec2i) boolean
        + isSatisfied(InnerGrid) boolean
    }
    class Main {
        + Main()
        + main(String[]) void
    }
    class Move2i {
        + Move2i(Vec2i, Integer, Integer)
        + position() Vec2i
        + previous_value() Integer
        + value() Integer
    }
    class MultiGrid {
        + MultiGrid(List~Pair~ Vec2i, Grid~~)
        + MultiGrid(MultiGrid)
        - HashMap~Vec2i, Set~ Integer~~ emptyCellsPossibilities
        - Grid[] grids
        - List~Move2i~ moves
        - Vec2i[] paddings
        - Vec2i size
        + areConstraintsSatisfied(boolean) boolean
        + computeAllEmptyCellsPossibilities() void
        + gatherEmptyCellsPossibilities() void
        + getSymbolAt(Vec2i) Integer
        + isInGrid(Vec2i) boolean
        + placeUnchecked(Vec2i, Integer, boolean, boolean) void
        + shallowCopy() MultiGrid
        Map~Vec2i, Set~ Integer~~ emptyCellsPossibilities
        Grid[] grids
        List~Move2i~ moves
        Vec2i[] paddings
        Vec2i size
    }
    class NotEmptyConstraint {
        + NotEmptyConstraint()
        + getPossibilities(InnerGrid, Vec2i) Optional~Set~Integer~~
        + isAffectedBy(Vec2i, Vec2i) boolean
        + isPosAffected(Vec2i) boolean
        + isSatisfied(InnerGrid) boolean
    }
    class ObservableGrid {
        + ObservableGrid(Grid, GridListener)
        - Grid grid
        + areConstraintsSatisfied(boolean) boolean
        + computeAllEmptyCellsPossibilities() void
        + placeUnchecked(Vec2i, Integer, boolean, boolean) void
        + shallowCopy() ObservableGrid
        Map~Vec2i, Set~ Integer~~ emptyCellsPossibilities
        Grid grid
    }
    class Pair~K, V~ {
+ Pair(K, V)
- K first
- V second
K first
V second
}
class ShallowCopyable~T~ {
<<Interface>>
+ shallowCopy() T
 }
class Solvable {
# Solvable(Set~Integer~)
+ areConstraintsSatisfied(boolean) boolean
+ computeAllEmptyCellsPossibilities() void
+ placeUnchecked(Vec2i, Integer, boolean, boolean) void
Map~Vec2i, Set~ Integer~~ emptyCellsPossibilities
boolean solved
}
class SolvingState {
<<enumeration>>
+ SolvingState()
+ valueOf(String) SolvingState
+ values() SolvingState[]
}
class SudokuBoard {
~ SudokuBoard(Grid)
+ recoverPreviousSudoku(Grid) void
+ update(Integer[][], boolean) void
}
class SudokuFrame {
+ SudokuFrame(Grid)
+ main(String[]) void
- updateJpanel() void
}
class SudokuOptions {
+ SudokuOptions(Color, Runnable, Runnable, Runnable, Runnable, Runnable, Runnable)
- applyMaterialDesign(JButton) void
}
class SudokuSolver {
+ SudokuSolver()
- doBacktracking(T, boolean) List~T~
+ findAllSolutions(T, boolean, boolean, boolean) List~T~
+ hasMoreThanOneSolution(T, boolean, boolean) boolean
+ solve(T, boolean, boolean, boolean) Pair~SolvingState, T~
- solveDeduction(T, boolean) SolvingState
}
class SymbolSets {
+ SymbolSets()
+ generateSymbols(int) Set~Integer~
}
class Tui {
+ Tui()
- displayGrid(Grid, Vec2i) void
- displayMultiGrid(MultiGrid) void
- displayOptions(String[], int) void
- displaySizes(int, int[]) void
- gameOver() void
- hslToRgb(float, float, float) int[]
+ main(String[]) void
- play(Solvable) void
- selectMode(String[]) int
- selectSize() int
- showHelper() void
- showTimeLine(int, int) void
- solve(Solvable) Solvable
~ start() void
- startLoader() void
- stopLoader() void
- welcomeMessage() void
}
class Utils {
+ Utils()
+ applyMapping(O[][], M[][], HashMap~O, M~) M[][]
}
class Vec2i {
+ Vec2i(Vec2i)
+ Vec2i(int, int)
- int x
- int y
+ absolute() Vec2i
+ add(Vec2i) Vec2i
+ equals(int, int) boolean
+ equals(Object) boolean
+ hashCode() int
+ random(int, int) Vec2i
+ substract(Vec2i) Vec2i
+ toString() String
+ zero() Vec2i
int x
int y
}

AbstractConstraint  ..>  BlockConstraint: «create»
AbstractConstraint  ..>  Box2D: «create»
AbstractConstraint  ..>  ColumnConstraint : «create»
AbstractConstraint  ..>  LineConstraint: «create»
AbstractConstraint  ..>  NotEmptyConstraint: «create»
BlockConstraint  ..>  AbstractConstraint
BlockConstraint "1" *--> "box 1" Box2D
ColumnConstraint  ..>  AbstractConstraint
CsvUtils  ..>  Grid: «create»
CsvUtils  ..>  MultiGrid : «create»
CsvUtils  ..>  Pair~K, V~: «create»
CsvUtils  ..>  Vec2i: «create»
GeneralSymbolConstraint  ..>  AbstractConstraint
GeneralSymbolConstraint "1" *--> "positionList *" Vec2i
Generator  ..>  ColumnConstraint: «create»
Generator  ..>  GeneralSymbolConstraint : «create»
Generator  ..>  Grid: «create»
Generator  ..>  LineConstraint: «create»
Generator  ..>  NotEmptyConstraint: «create»
Generator  ..>  Vec2i: «create»
Grid "1" *--> "constraints *" AbstractConstraint
Grid "1" *--> "innerGrid 1" InnerGrid
Grid  ..>  InnerGrid: «create»
Grid  ..>  Move2i: «create»
Grid "1" *--> "moves *" Move2i
Grid  ..>  ShallowCopyable~T~
Grid  -->  Solvable
Grid  ..>  Vec2i: «create»
Grid "1" *--> "emptyCellsPossibilities *" Vec2i
ImGUIFrame "1" *--> "originalGrid 1" Grid
ImGUIFrame  ..>  ObservableGrid: «create»
ImGUIFrame  ..>  Vec2i: «create»
ImGUIFrame "1" *--> "selected_pos 1" Vec2i
InnerGrid  ..>  Vec2i: «create»
LineConstraint  ..>  AbstractConstraint
Move2i "1" *--> "position 1" Vec2i
MultiGrid "1" *--> "grids *" Grid
MultiGrid  ..>  Move2i: «create»
MultiGrid "1" *--> "moves *" Move2i
MultiGrid  ..>  ShallowCopyable~T~
MultiGrid  -->  Solvable
MultiGrid  ..>  Vec2i: «create»
MultiGrid "1" *--> "paddings *" Vec2i
NotEmptyConstraint  ..>  AbstractConstraint
ObservableGrid "1" *--> "grid 1" Grid
ObservableGrid "1" *--> "listener 1" GridListener
ObservableGrid  ..>  ShallowCopyable~T~
ObservableGrid  -->  Solvable
SudokuBoard "1" *--> "solvedGrid 1" Grid
SudokuBoard  ..>  Grid: «create»
SudokuBoard  ..>  Vec2i: «create»
SudokuFrame  ..>  Grid : «create»
SudokuFrame "1" *--> "grid 1" Grid
SudokuFrame "1" *--> "board 1" SudokuBoard
SudokuFrame  ..>  SudokuBoard: «create»
SudokuFrame  ..>  SudokuOptions: «create»
SudokuSolver  ..>  Pair~K, V~ : «create»
Tui  ..>  Grid: «create»
Tui  ..>  Vec2i: «create»

```

<!-- END_CLASS -->

### Diagramme d'activité du générateur :

```
TODO
```

### Diagramme d'activité du solveur :

```mermaid
stateDiagram
    queue_init: Ajouter grille à la queue
    boucle: La queue est vide ?
    if_solved: La grille est résolu ?
    partial: PARTIELLEMENT RESOLU
    suppress: On supprime un element de la queue
    SudokuSolver.doBacktracking: BACKTRACKING
SudokuSolver.solveDeduction: DEDUCTION

state SudokuSolver.solve {
SudokuSolver.doBacktracking --> boucle
[*] --> solve
solve --> queue_init
queue_init --> boucle

state boucle_test <<choice>>
boucle --> boucle_test
boucle_test --> suppress: Non
suppress --> if_solved
boucle_test --> INSOLVABLE: Oui, on ne peut pas résoudre
INSOLVABLE --> [*]

state is_solved_test <<choice>>
if_solved --> is_solved_test
is_solved_test --> if_deduced: Non

is_solved_bis_test --> RESOLU: Oui
is_solved_test --> RESOLU: Oui
RESOLU --> [*]

if_deduced: Doit on essayer de déduire avec les contraintes ?
state is_deduced_test <<choice>>
if_deduced --> is_deduced_test
is_backtracking_test --> SudokuSolver.doBacktracking: Oui
is_deduced_test --> SudokuSolver.doBacktracking: Non, alors on fait forcement du backtracking et on récupère les possibilités engendrée
is_deduced_test --> SudokuSolver.solveDeduction: Oui

if_solved_bis: La grille est résolu ?
SudokuSolver.solveDeduction --> if_solved_bis
state is_solved_bis_test <<choice>>
if_solved_bis --> is_solved_bis_test
is_solved_bis_test --> if_unsolvable: Non

if_unsolvable: La grille peut être résolu en l'état ?
state is_unsolvable_test <<choice>>
if_unsolvable --> is_unsolvable_test
is_unsolvable_test --> INSOLVABLE: Non
is_unsolvable_test --> if_backtracking: Oui

if_backtracking: Doit on essayer le backtracking ?
state is_backtracking_test <<choice>>
if_backtracking --> is_backtracking_test
is_backtracking_test --> partial: Non, on ne peut pas aller plus loin juste avec le déduction


partial --> [*]


}
```
