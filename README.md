# SUUUUUUUUUUUudoku

Projet mené par Eymeric Dechelette, Thibaut Laracine et Guillaume Calderon

## Fonctionnalités

Ce SUUUUUUUUUUUudoku dispose des fonctionnalités suivantes :

- Génération de grille

  Vous pouvez générer une grille de sudoku de taille avec des performances raisonnables jusqu'à 16\*16.

- Résolution de grille

  Vous pouvez résoudre une grille de sudoku de taille avec des performances raisonnables jusqu'à 100\*100.
  Vous pouvez aussi résoudre des multi-doku (pré-codé via des fichiers dans le dossier
  `src/test/resources`)

- Consulter l'historique des modifications

  Vous pouvez consulter l'historique des modifications de la grille (particulièrement utile pour les résolutions
  automatiques).

- Résolution manuelle

  Vous pouvez résoudre une grille de sudoku de manière manuelle

- Interface graphique

  Vous pouvez utiliser une interface graphique pour jouer au sudoku

- Interface en ligne de commande

  Vous pouvez utiliser une interface en ligne de commande pour jouer au sudoku

## Utilisation

### Lancer le programme

#### Utilisateur nix

La methode privilégiée pour lancer le programme est d'utiliser nix, car celui-ci vous assure d'avoir un environnement
identique au autre utilisateur de nix.

Si vous disposez de nix, vous pouvez lancer la compilation avec la commande suivante :

```bash
nix build
```

Vous pouvez ensuite lancer les différents executables avec les commandes suivantes :

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

Vous pouvez ensuite lancer les différents executables avec les commandes suivantes :

```bash
java -jar ./build/libs/imGUI-1.0-SNAPSHOT.jar # Interface en ligne de commande
java -jar ./build/libs/tui-1.0-SNAPSHOT.jar # Interface graphique avec imGUI
java -jar ./build/libs/swing-1.0-SNAPSHOT.jar # Interface graphique avec swing
```

### Diagramme de cas d'utilisation :

```mermaid
flowchart LR
    subgraph system["system"]
        B(["Créer Grille"])
        C(["Ajouter Grille"])
        D(["Générer Grille"])
        G(["Résoudre Automatiquement"])
        E(["Résoudre Grille"])
        F(["Résoudre Manuellement"])
        I(["Étape De Resolution"])
        H(["Afficher"])
        J(["Grille"])
    end
    B & E & H --- A(["Client"])
    G --> E
    C & D --> B
    F --> E
    I & J ---> H
    F -. << include >> .-> G
    I -. << include >> .-> G
    G -. << include >> .-> D
```

### Diagramme de classes :

<!-- BEGIN_CLASS -->
<!-- END_CLASS -->

```mermaid
---
config:
layout: elk
---
classDiagram
    direction TB
    namespace Ui {
        class ImGUIFrame {
            Integer? currentSymbol
            + ImGUIFrame()
            - drawGrid(Grid, Vec2i, ImVec2) void
            # configure(Configuration) void
            - keyPress(int) void
            - setSelection(int, int) void
            + main(String[]) void
            # preRun() void
            - drawMultiGrid(MultiGrid, Vec2i, ImVec2) void
            - applyLastChanges() void
            - handleInput() void
            + process() void
        }
        class Tui {
            + Tui()
            - welcomeMessage() void
            - displayGrid(Grid, Vec2i) void
            - play(Solvable~?~) void
            - showTimeLine(int, int) void
            + main(String[]) void
            - stopLoader() void
            - selectMode(String[]) int
            ~ start() void
            - startLoader() void
            - gameOver() void
            - cleanTerminal() void
            - solve(Solvable~?~) Solvable~?~
            - displayMultiGrid(MultiGrid) void
            - showHelper() void
            - displayOptions(String[], int) void
        }
        class SudokuOptions {
            + SudokuOptions(Color, Runnable, Runnable, Runnable, Runnable, Runnable, Runnable)
            - applyMaterialDesign(JButton) void
        }
        class SudokuFrame {
            + SudokuFrame(Grid)
            + main(String[]) void
            - updateJpanel() void
        }
        class SudokuBoard {
            ~ SudokuBoard(Grid)
            + recoverPreviousSudoku(Grid) void
            + update(Integer[][], boolean) void
        }
        class ObservableGrid {
            - Grid grid
            Map~Vec2i, Set~ Integer~~ emptyCellsPossibilities
            Grid grid
            Vec2i size
            List~Move2i~ moves
            + ObservableGrid(Grid, GridListener)
            + computeAllEmptyCellsPossibilities() void
            + placeUnchecked(Vec2i, Integer, boolean, boolean) void
            + undoLastMove(boolean) void
            + cleanMoves() void
            + areConstraintsSatisfied(boolean) boolean
            + shallowCopy() ObservableGrid
            + getSymbolAt(Vec2i) Integer
        }
        class GridListener {
            + onGridChange(InnerGrid) void
        }
    }
    namespace Constraints {
        class GeneralSymbolConstraint {
            - Vec2i[] positionList
            Vec2i[] positionList
            + GeneralSymbolConstraint(Set~Integer~, Vec2i[])
            + isSatisfied(InnerGrid) boolean
            - extractValues(InnerGrid) Set~Integer~
            + isPosAffected(Vec2i) boolean
            - isInPositionList(Vec2i) boolean
            + isAffectedBy(Vec2i, Vec2i) boolean
            + getPossibilities(InnerGrid, Vec2i) Optional~Set~Integer~~
        }
        class DiagonalConstraint {
            + DiagonalConstraint(Set~Integer~)
            + isAffectedBy(Vec2i, Vec2i) boolean
            + isSatisfied(InnerGrid) boolean
            + getPossibilities(InnerGrid, Vec2i) Optional~Set~Integer~~
            + isPosAffected(Vec2i) boolean
        }
        class BlockConstraint {
            Box2D block
            + BlockConstraint(Set~Integer~, Box2D)
            + isAffectedBy(Vec2i, Vec2i) boolean
            + isSatisfied(InnerGrid) boolean
            - extractBlock(InnerGrid) Set~Integer~
            + isInBlock(Vec2i) boolean
            + getPossibilities(InnerGrid, Vec2i) Optional~Set~Integer~~
            + equals(Object) boolean
            + hashCode() int
            + isPosAffected(Vec2i) boolean
        }
        class AbstractConstraint {
            + isSatisfied(InnerGrid) boolean
            + isAffectedBy(Vec2i, Vec2i) boolean
            + getPossibilities(InnerGrid, Vec2i) Optional~Set~Integer~~
            + isPosAffected(Vec2i) boolean
            + getClassicConstraints(int, Set~Integer~) List~AbstractConstraint~
            + getRectConstraints(int, int, Set~Integer~) List~AbstractConstraint~
        }
        class NotEmptyConstraint {
            + NotEmptyConstraint()
            + isAffectedBy(Vec2i, Vec2i) boolean
            + isPosAffected(Vec2i) boolean
            + getPossibilities(InnerGrid, Vec2i) Optional~Set~Integer~~
            + isSatisfied(InnerGrid) boolean
        }
        class LineConstraint {
            + LineConstraint(Set~Integer~)
            + isPosAffected(Vec2i) boolean
            + isSatisfied(InnerGrid) boolean
            + isAffectedBy(Vec2i, Vec2i) boolean
            + getPossibilities(InnerGrid, Vec2i) Optional~Set~Integer~~
        }
        class ColumnConstraint {
            + ColumnConstraint(Set~Integer~)
            + isSatisfied(InnerGrid) boolean
            + isAffectedBy(Vec2i, Vec2i) boolean
            + getPossibilities(InnerGrid, Vec2i) Optional~Set~Integer~~
            + isPosAffected(Vec2i) boolean
        }
    }
    namespace UtilsNamespace {
class Box2D {
+ Box2D(Vec2i, Vec2i)
+ Box2D(int, int, int, int)
+ absolute() Box2D
+ x() int
+ dx() int
+ overlap(Box2D) Box2D
+ height() int
+ contains(Vec2i) boolean
+ absolute(int, int, int, int) Box2D
+ dy() int
+ width() int
+ offset(int, int) Box2D
+ equals(Object) boolean
+ hashCode() int
+ contains(int, int) boolean
+ substract(Box2D) Box2D
+ y() int
}
class Vec2i {
- int x
- int y
int x
int y
+ Vec2i(Vec2i)
+ Vec2i(int, int)
+ zero() Vec2i
+ equals(int, int) boolean
+ random(int, int) Vec2i
+ equals(Object) boolean
+ substract(Vec2i) Vec2i
+ absolute() Vec2i
+ toString() String
+ hashCode() int
+ add(Vec2i) Vec2i
}
class Move2i {
+ Move2i(Vec2i, Integer, Integer)
+ position() Vec2i
+ toString() String
+ previous_value() Integer
+ value() Integer
}
class Pair~K, V~ {
- K first
- V second
K first
V second
+ Pair(K, V)
}
class Difficulty {
String[] values
int value
+ Difficulty()
+ fromInt(int) Difficulty
+ values() Difficulty[]
+ valueOf(String) Difficulty
}
class CsvUtils {
+ CsvUtils()
+ exportGrid(Path, Grid) void
+ importMultiGrid(Path) MultiGrid
+ importGrid(Path) Integer[][]
+ exportMultiGrid(Path, MultiGrid) void
}
class Main {
+ Main()
+ main(String[]) void
}
class Utils {
+ Utils()
+ hslToRgb(float, float, float) int[]
+ applyMapping(O[][], M[][], HashMap~O, M~) void
}
}
namespace GridNamespace {
class Grid {
- ArrayList~Move2i~ moves
- InnerGrid innerGrid
~ List~AbstractConstraint~ constraints
- HashMap~Vec2i, Set~ Integer~~ emptyCellsPossibilities
InnerGrid innerGrid
Vec2i size
List~Move2i~ moves
Set~Integer~ symbols
List~AbstractConstraint~ constraints
HashMap~Vec2i, Set~ Integer~~ emptyCellsPossibilities
+ Grid(Integer[][], Set~Integer~)
+ Grid(Grid)
+ Grid(Integer[][], Set~Integer~, int, int)
+ Grid(Integer[][], List~AbstractConstraint~, Set~Integer~)
+ computeAllEmptyCellsPossibilities() void
+ placeUnchecked(Vec2i, Integer, boolean, boolean) void
+ areConstraintsSatisfied(boolean) boolean
+ getSymbolAt(Vec2i) Integer
+ tryPlace(Vec2i, Integer, boolean, boolean) boolean
+ display() void
+ getSymbolAt(int, int) Integer
+ length() int
+ undoLastMove(boolean) void
+ shallowCopy() Grid
+ computeChangedEmptyCellsPossibilities(Vec2i, boolean) void
+ cleanMoves() void
+ applyNakedPairs() boolean
}
class InnerGrid {
+ InnerGrid(Integer[][])
+ InnerGrid(InnerGrid)
+ set(Vec2i, Integer) void
+ get() Integer[][]
+ display() void
+ length() int
+ computeEmptyCells() HashSet~Vec2i~
+ at(Vec2i) Integer
+ hashCode() int
+ equals(Object) boolean
}
class MultiGrid {
- Grid[] grids
- Vec2i[] paddings
- Vec2i size
- List~Move2i~ moves
- HashMap~Vec2i, Set~ Integer~~ emptyCellsPossibilities
Vec2i[] randomPadding
Map~Vec2i, Set~ Integer~~ emptyCellsPossibilities
Vec2i size
List~Move2i~ moves
Vec2i[] paddings
Grid[] grids
+ MultiGrid(List~Pair~ Vec2i, Grid~~)
+ MultiGrid(MultiGrid)
+ getGridFor(int, int) Pair~Integer, Grid~
+ fillOverlappingCells() void
+ computeAllEmptyCellsPossibilities() void
+ areConstraintsSatisfied(boolean) boolean
+ placeUnchecked(Vec2i, Integer, boolean, boolean) void
+ cleanMoves() void
+ isNotInGrid(Vec2i) boolean
+ undoLastMove(boolean) void
+ shallowCopy() MultiGrid
+ gatherEmptyCellsPossibilities() void
+ isInGrid(Vec2i) boolean
+ getSymbolAt(Vec2i) Integer
+ display() void
}
class Solvable~T~ {
~ Set~Integer~ symbols
boolean solved
Map~Vec2i, Set~ Integer~~ emptyCellsPossibilities
Vec2i size
List~Move2i~ moves
Set~Integer~ symbols
# Solvable(Set~Integer~)
+ cleanMoves() void
+ areConstraintsSatisfied(boolean) boolean
+ getSymbolAt(Vec2i) Integer
+ shallowCopy() T
+ undoLastMove(boolean) void
+ computeAllEmptyCellsPossibilities() void
+ placeUnchecked(Vec2i, Integer, boolean, boolean) void
}
class SymbolSets {
+ SymbolSets()
+ generateSymbols(int) Set~Integer~
}
 }
namespace Solver {
class SolvingState {
+ SolvingState()
+ values() SolvingState[]
+ valueOf(String) SolvingState
 }
class Generator {
+ Generator()
- removeRandomCells(T, int, Difficulty) void
+ fastSolvedGridCreation(int, int) Grid
~ createRandomConstraints(Grid) List~AbstractConstraint~
+ createExempleSolvedSudoku(int, int) Grid
+ generateSudokuWithBlockConstraints(int, int, Difficulty) Grid
+ generateClassicSudoku(int, Difficulty) Grid
+ printGrid(Integer[][]) void
+ generateSudokuWithRandomBlockConstraint(int, Difficulty) Grid
+ generateMultigridSudoku(int, int, Difficulty) MultiGrid
- findDividers(int) Vec2i
- createSolvedSudoku(int, int) Grid
}
class SudokuSolver_copy_1["SudokuSolver"] {
+ SudokuSolver()
+ findAllSolutions(T, boolean, boolean, boolean) List~T~
+ hasMoreThanOneSolution(T, boolean, boolean) boolean
- solveDeduction(T, boolean) SolvingState
+ solve(T, boolean, boolean, boolean) Pair~SolvingState, T~
- doBacktracking(T, boolean) List~T~
}
 }
<<Interface>> AbstractConstraint
<<enumeration>> Difficulty
<<Interface>> GridListener
<<enumeration>> SolvingState
SudokuFrame ..> SudokuOptions: «create»
BlockConstraint ..> AbstractConstraint
ColumnConstraint ..> AbstractConstraint
CsvUtils ..> Grid: «create»
CsvUtils ..> MultiGrid: «create»
DiagonalConstraint ..> AbstractConstraint
GeneralSymbolConstraint ..> AbstractConstraint
Generator ..> ColumnConstraint: «create»
Generator ..> GeneralSymbolConstraint: «create»
Generator ..> Grid : «create»
Generator ..> LineConstraint: «create»
Generator ..> MultiGrid: «create»
Generator ..> NotEmptyConstraint: «create»
Grid "1" *--> "constraints *" AbstractConstraint
Grid ..> InnerGrid : «create»
Grid "1" *--> "innerGrid 1" InnerGrid
Grid --> Solvable
ImGUIFrame ..> Grid: «create»
ImGUIFrame ..> ObservableGrid: «create»
ImGUIFrame "1" *--> "originalSolvable 1" Solvable
LineConstraint ..> AbstractConstraint
MultiGrid "1" *--> "grids *" Grid
MultiGrid --> Solvable
NotEmptyConstraint ..> AbstractConstraint
ObservableGrid "1" *--> "grid 1" Grid
ObservableGrid "1" *--> "listener 1" GridListener
ObservableGrid --> Solvable
SudokuBoard "1" *--> "previousGrid 1" Grid
SudokuBoard ..> Grid: «create»
SudokuFrame "1" *--> "grid 1" Grid
SudokuFrame ..> Grid: «create»
SudokuFrame ..> SudokuBoard: «create»
SudokuFrame "1" *--> "board 1" SudokuBoard
Tui ..> Grid: «create»
Tui ..> MultiGrid: «create»
```

### Grille de sudoku et multi grille (Solvable) :

Voici un diagramme d'objet représentant un multi-doku résolu en partie :

```mermaid
---
config:
  layout: elk
---
classDiagram
    direction TD
    class Object_5 {
        : MultiGrid$
        - offsets =((x=0, y=0), (x=12, y=0), (x=6, y=6), (x=0, y=12), (x=12, y=12))
        - size = Vec2i(x=21, y=21)
        - moves =(((x=4, y=6), null, 4), ((x=8, y=11), null, 7), ....)
        - emptyCellsPossibilities =((x=2, y=1), 4), ...)
        - symbols = HashSet(1, 2, 3, 4, 5, 6, 7, 8, 9)
    }
    class Object_0 {
        : Grid$
        innerGrid =()
        emptyCellsPossibilities =((x=2, y=1), 4), ...)
        constraints(BlockConstraint, LineConstraint, ...)
        moves =(((x=4, y=6), null, 4), ...)
        symbols = HashSet(1, 2, 3, 4, 5, 6, 7, 8, 9)
    }
    class Object_1 {
        : Grid$
    }
    class Object_2 {
        : Grid$
    }
    class Object_3 {
        : Grid$
    }
    class Object_4 {
        : Grid$
    }
    note for Object_0 "offset : x=0, y=0 <br/> Les grilles contiennent <br/> des valeurs qui leurs <br/> sont propres"
    note for Object_1 "offset : x=12, y=0"
    note for Object_2 "offset : x=6, y=6"
    note for Object_3 "offset : x=0, y=12"
    note for Object_4 "offset : x=12, y=12"
    Object_5 -- Object_0
    Object_5 -- Object_1
    Object_5 -- Object_2
    Object_5 -- Object_3
    Object_5 -- Object_4
```

Il peut représenter une grille de multi sudoku tel que celle-ci dessous :
![img.png](img.png)

### Générateur :

```mermaid
---
config:
  theme: neo-dark
  look: neo
---
stateDiagram
    Generated: Grille générée !
    fastgeneration: Génération rapide <br/> d'une grille résolu avec <br/> contraintes de bloc <br/> de taille NxM
    removeCells: Suppression des cellules <br/> dans la grille
    cleanHistory: Suppression de <br/> l'historique des mouvements <br/> après génération
    generateConstraint: Générer des contraintes de <br/> bloc avec position aléatoire
    getRandomPadding: Choisir les décalages <br/> basiques du multi-doku <br/> par rapport à l'origine <br/> N = M = 3
    placeGrids: Placer les autres grilles <br/> autour de notre grille
    state choice_generation <<choice>>
    state choice_grid_gen <<choice>>

state Generation_Generale {
[*] --> choice_generation: Grid ou Multigrid ?
choice_generation --> fastgeneration: Grid (paramètre N et M demandé)
choice_generation --> getRandomPadding: Multi-Grid
fastgeneration --> choice_grid_gen: type de génération ?
choice_grid_gen --> generateConstraint: generateSudokuWithRandomBlockConstraint
generateConstraint --> removeCells
choice_grid_gen --> removeCells: generateSudokuWithBlockConstraints
removeCells --> cleanHistory
choice_grid_gen --> placeGrids: generateMultigridSudoku
placeGrids --> SudokuSolver.solve
SudokuSolver.solve --> removeCells
cleanHistory --> Generated
getRandomPadding --> fastgeneration
Generated --> [*]
}
```

#### Explication de la méthode de génération de grille simple optimisé :

```mermaid
---
config:
  theme: neo-dark
  look: neo
---
stateDiagram
    getPath: Récuperer le chemin <br/> du présumé fichier <br/> contenant la grille
    export: Exporter la grille <br/> dans un fichier
    return: Grille Résolu Générée !
    init: initialiser la grille
    shuffleBlockRow: Mélanger les <br/> lignes de blocs <br/> entre elles
    shuffleBlockColumn: Mélanger les <br/> colonnes de blocs <br/> entre elles
    shuffleRowInBlockRow: Dans les lignes de blocs <br/> mélanger les lignes
    shuffleColumnInBlockColumn: Dans les colonnes de blocs <br/> mélanger les colonnes
    shuffleSymbols: Mélanger tout les symboles
    initGrid: Initialisation <br/> d'une grille vide <br/> (taille NxM*NxM)
    initSymbolSet: Initialisation et mélange d'une liste de <br/> symboles de taille NxM
    placeSymbolOnDiagonal: Placer les symboles <br/> sur la diagonale dans <br/> l'ordre défini <br> (symbole)
    computePossibility: Calculer toutes les possibilités par cases
    solve: Résoudre

    state createSolvedSudoku {
        [*] --> initGrid
        initGrid --> initSymbolSet
        initSymbolSet --> placeSymbolOnDiagonal
        placeSymbolOnDiagonal --> computePossibility
        computePossibility --> solve
        solve --> [*]
    }

    state fastSolvedGridCreation {
        [*] --> getPath: longueur et largeur des contraintes de block
        state exist <<choice>>
        getPath --> exist: Essayer de récuperer la <br/> grille à partir du fichier
        exist --> init
        export --> return
        state isSolve <<choice>>
        init --> isSolve: la grille est elle résolu ?
        isSolve --> shuffleBlockRow: Oui
        shuffleBlockRow --> shuffleBlockColumn
        shuffleBlockColumn --> shuffleRowInBlockRow
        shuffleRowInBlockRow --> shuffleColumnInBlockColumn
        shuffleColumnInBlockColumn --> shuffleSymbols
        shuffleSymbols --> return
        return --> [*]
    }

    createSolvedSudoku --> export
    exist --> createSolvedSudoku: Le fichier n'existe pas
    isSolve --> createSolvedSudoku: Non
```

#### Explication de la méthode de suppression des cellules dans un Solvable résolu :

```mermaid
---
config:
  theme: neo-dark
---
sequenceDiagram
    actor .
    participant Generator
    participant Solvable
    participant List(Vec2i)
    participant SudokuSolver
    . ->> Generator: removeRandomCells (solvedGrid, lengthInnerGrid, difficulty)
    activate Generator
    Generator ->> List(Vec2i): <<create>>
    activate List(Vec2i)
    List(Vec2i) -->> Generator: toTestRemove = new ArrayList<>()
    deactivate List(Vec2i)
    loop i : Allant de 0 à lengthInnerGrid
        loop j : Allant de 0 à lengthInnerGrid
            alt solvedGrid n'est pas une instance de MultiGrid <br> ou solvedGrid contient la position (i, j)
                Generator ->> List(Vec2i): <<create>>
                activate List(Vec2i)
                List(Vec2i) -->> Generator: toTestRemove.add(new Vect2i(i, j))
                deactivate List(Vec2i)
            end
        end
    end
    Generator ->> Generator: Collections.shuffle(toTestRemove);
    Generator ->> Generator: toTestRemove = <br> sous ensemble de toTestRemove <br> basé sur difficulty
loop Tant que TestRemove n'est pas vide
Generator->>List(Vec2i): a = toTestRemove.removeFirst()
activate List(Vec2i)
List(Vec2i) -->> Generator: a = toTestRemove.removeFirst()
deactivate List(Vec2i)
Generator ->> Solvable: solvedGrid.placeUnchecked(a, null, null, true)
activate Solvable
Solvable -->> Generator: 
Generator->>Solvable: solvedGrid.computeAllEmptyCellsPossibilities()
Solvable -->> Generator: 
deactivate Solvable
Generator ->> SudokuSolver: bool = SudokuSolver.solve(solvedGrid).getFirst()
activate SudokuSolver
SudokuSolver -->> Generator: bool = SudokuSolver.solve(solvedGrid).getFirst()
deactivate SudokuSolver
alt if b != SOLVED
Generator->>Solvable: undoLastMove()
activate Solvable
deactivate Solvable
Solvable -->> Generator: 
end
end
SudokuSolver->>Generator: c = hasMoreThanOneSolution()
activate SudokuSolver
Generator -->> SudokuSolver: c = hasMoreThanOneSolution()
deactivate SudokuSolver
loop Tant que c = true
Generator->>Generator: undoLastMove()
SudokuSolver->>Generator: c = hasMoreThanOneSolution()
activate SudokuSolver
Generator -->> SudokuSolver: c = hasMoreThanOneSolution()
deactivate SudokuSolver
end
Generator->>Solvable: solvedGrid.cleanMoves()
activate Solvable
deactivate Solvable
Solvable -->> Generator: 
Generator ->> .: 
deactivate Generator
```

#### Méthode de génération des contraintes pour un sudoku avec des contraintes de blocs de NxM :

```mermaid
---
config:
  theme: neo-dark
  look: neo
---
stateDiagram
    positionList: Initialiser positionList <br> la liste des <br>symboles possibles
    constraint: Contraintes créé !
    initConstraint: Initialiser une liste <br> de contraintes
    addLastConstraint: Ajout des contraintes <br> de lignes, de colonnes <br> et de complétude <br> à constraints
    i_1: I = 0
    j_1: J = 0
    i_2: I = 0
    j_2: J = 0
    initlistinconst: Initialisation d'une <br> liste de positions <br> qui sera placé <br> dans la contrainte
    getpos: Selection au hasard <br> dans positionList[J]
    addPos: Ajout de pos <br> à listInConstraint
    delPos: Suppression de pos <br> dans positionList[J]
    addConstraint: Ajout d'une contrainte <br> sur les positions <br> dans listInConstraint
    add: ajout la position <br> Vec2i(I, J) à <br> positionList[symbol - 1]
    state for_i_2 <<choice>>
    state for_j_2 <<choice>>
    state for_i_1 <<choice>>
    state for_j_1 <<choice>>

    state createRandomConstraint {
        [*] --> positionList: Grid grid
        positionList --> i_1: position List est <br> de taille grid.length()
        for_i_1 --> initConstraint: Si I >= length
        initConstraint --> i_2: AbstractConstraint constraints <br> List< List< Vec2i>> positionList
        for_i_2 --> addLastConstraint: Si I >= length <br> constraints
        addLastConstraint --> constraint
        constraint --> [*]
        i_2 --> for_i_2
        j_2 --> for_j_2
        addConstraint --> for_i_2: I++
        for_j_2 --> addConstraint: Si J >= length <br>
        getpos --> addPos: On obtient pos
        for_j_2 --> getpos: Si J < length
        for_i_2 --> initlistinconst: Si I < length
        addPos --> delPos
        delPos --> for_j_2: J++
        initlistinconst --> j_2: listInConstraint
        i_1 --> for_i_1
        j_1 --> for_j_1
        for_j_1 --> add: Si J < length <br> symbol = grid.getSymbolAt(I, J)
        for_j_1 --> for_i_1: Si J >= length <br> I++
        add --> for_j_1: J++
        for_i_1 --> j_1: Si I < length
    }
```

### Solveur :

```mermaid
---
config:
  theme: neo-dark
  look: neo
---
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

On peut également observer la méthode de résolution à travers un diagramme de séquence :

```mermaid
sequenceDiagram
    actor C as Client
    participant S as solver : SudokuSolver
    participant Fin
    participant G as Grid/Multgrid : T extends Solvable<C> & ShallowCopyable<T>
    C ->> S: SudokuSolver.solve(grid, IsDeduce, IsBacktracked, IsMovesStored)
    activate S
    create participant AD as currentList : ArrayDeque<T>
    S -->> AD: <<create>>
    S ->> G: copiedGrid = grid.shallowCopy()
    activate G
    G -->> S: copiedGrid = grid.shallowCopy()
    deactivate G
    S ->> S: currentList.add(copiedGrid)
    loop while currentList isn't empty
        S ->> S: currentGrid = currentList.removeLast()
        alt currentGrid.isSolved()
            S ->> Fin: return new Pair<>SolvingState.SOLVED, currentGrid)
        else
            alt isDeduce is set to true
                S ->> S: state = solveDeduction(currentGrid, IsMovesStored)
                alt state == SolvingState.SOLVED
                    S ->> Fin: return new Pair<>SolvingState.SOLVED, currentGrid)
                else
                    alt state == SolvingState.UNSOLVABLE
                        S ->> Fin: return new Pair<>(SolvingState.UNSOLVABLE, null);
                    else
                        opt IsBacktracked
                            S ->> Fin: return new Pair<>(SolvingState.PARTIALLY_SOLVED, currentGrid);
                        end
                    end
                end
            end
        end
        S ->> S: currentList.addAll(doBacktracking(currentGrid, isMovesStored))
    end
    S ->> Fin: return new Pair<>(SolvingState.UNSOLVABLE, null);
    deactivate S
```