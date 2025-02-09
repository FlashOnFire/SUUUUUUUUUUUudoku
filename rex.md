---
title: "Projet APO Sudoku (et variantes) : résolution et génération"
author: Thibaut Laracine, Guillaume Calderon, Eymeric Déchelette, 3AFISA
titlepage: true
toc-own-page: true
toccolor: black
toc-title: Table des matières
---

### Lien du Git : [GitHub](https://github.com/FlashOnFire/SUUUUUUUUUUUudoku)

# Tutoriel utilisation

## Prérequis

Si vous n'utilisez pas nix, les prérequis sont :

- `openjdk-23`
- `gradle`
- `libGL`

## Jar précompilés

Vous pouvez lancer les fichiers en .jar dans le dossier build

```bash
java -jar ./build/libs/imGUI-1.0.jar # Interface graphique avec imGUI
java -jar ./build/libs/tui-1.0.jar # Interface en ligne de commande
java -jar ./build/libs/swing-1.0.jar # Interface graphique avec swing
```

## Compilation

### Utilisateur nix

La méthode privilégiée pour lancer le programme est d'utiliser nix, car celui-ci vous assure d'avoir un environnement
identique aux autres utilisateurs de nix.
Si vous disposez de nix, vous pouvez lancer la compilation avec la commande suivante :

```bash
nix build
```

Vous pouvez ensuite lancer les différents exécutables avec les commandes suivantes :

```bash
./result/bin/tui # Interface en ligne de commande
./result/bin/imGUI # Interface graphique avec imGUI
./result/bin/swing # Interface graphique avec swing
```

### Utilisateur Linux classique

Vous pouvez lancer la compilation avec la commande suivante :

```bash
./gradlew buildAllJars

# pour compiler un seul jar
./gradlew guiImGUIJar
./gradlew guiSwingJar
./gradlew tuiJar
```

Vous pouvez ensuite lancer les différents exécutables avec les commandes suivantes :

```bash
java -jar ./build/libs/imGUI-1.0.jar # Interface graphique avec imGUI
java -jar ./build/libs/tui-1.0.jar # Interface en ligne de commande
java -jar ./build/libs/swing-1.0.jar # Interface graphique avec swing
```

#### Lancer sans compilation par un jar

Vous pouvez également lancer le programme directement via gradle avec ces commandes :

```bash
./gradlew runTui # Interface en ligne de commande
./gradlew runImGUI # Interface graphique avec imGUI
./gradlew runSwing # Interface graphique avec swing
```

---

# Méthodologie

## Articulation entre conception et codage

Pour aborder ce projet, nous avons commencé, après avoir lu attentivement le cahier des charges, par réaliser un
diagramme de cas d'utilisation afin de s'assurer que chaque membre du groupe a parfaitement compris les objectifs requis
de l'application.
Cela nous a permis d'aborder sereinement notre diagramme de classe pour anticiper au mieux l'architecture de notre code.

_Voici le diagramme de cas d'utilisation réalisé au début du projet :_

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

- **Afficher** : Le Client peut afficher une grille de sudoku ou une étape de résolution.
- **Résoudre Grille** : Le Client peut résoudre une grille de sudoku de type Solvable. Il peut choisir de résoudre
  manuellement ou automatiquement.
- **Résoudre Automatiquement** : Le Client peut automatiquement résoudre une grille de sudoku, de type Solvable
  directement en utilisant le solveur.
- **Résoudre Manuellement** : Le Client peut manuellement résoudre une grille de sudoku de type Solvable, via
  l'interface graphique.
- **Générer Grille** : Le Client peut générer une grille de sudoku, de type Solvable, via le générateur, en fonction de
  la difficulté et du type de grille désiré.
- **Ajouter Grille** : Le Client peut ajouter une grille de sudoku, de type Solvable, à la liste des grilles.
- **Créer Grille** : Le Client peut créer une grille de sudoku, de type Solvable, en fonction de la taille et des
  symboles désirés.
- **Étape De Resolution** : Le Client peut afficher une étape de résolution de la grille de sudoku, de type Solvable.

#### Diagramme de classe

Nous avons préféré garder un diagramme de classe le plus minimaliste possible au début, car nous savons par expérience
que nous devons toujours réorganiser le code plusieurs fois lors de son développement.
Éviter de trop architecturer le projet permet de rester agile et de l'adapter au fur et à mesure.
Nous avons ensuite actualisé ce diagramme au fur et à mesure du projet afin d'avoir une vue d'ensemble de notre
organisation et de voir simplement les points améliorables et les répétitions dans notre code.

_Voici le diagramme de classe final mis à jour le 09/02/2025 :_

![](diagram/class.png)

_Voici les diagrammes de classe finaux par package mis à jour le 09/02/2025 :_

---

**Le package des algorithmes** contient les fonctions utiles pour effectuer nos modifications sur nos grilles. C'est ici
que l'on retrouvera notamment le Solveur, ainsi que le Générateur

![](diagram/algorithm.png)

---

**Le package des contraintes** contient toutes les contraintes qui peuvent être utilisées par nos grilles. C'est ici que
l'on pourra ajouter de nouvelles contraintes si besoin, tant que celle-ci hérite de la classe AbstractConstraint.

![](diagram/constraints.png)

---

**Le package graphique** contient toutes les différentes UI proposées par le projet.

![](diagram/graphics.png)

---

**Le package des grilles** contient l'ensemble des types de grilles, ainsi que les classes qui vont servir à
l'utilisation des grilles par d'autres parties du projet.

![](diagram/grid.png)

---

**Le package utils**, enfin, contient toutes les petites classes basiques qui vont servir pour implémenter nos objets
plus volumineux.

![](diagram/utils.png)

---

# Conception

## Les Grilles et MultiGrille

Nos sudokus sont représentés par une classe abstraite Solvable, qui peut être prise en charge par notre SudokuSolver :

- La classe Grid représente une grille de sudoku classique
- La classe MultiGrid représente une grille de sudoku avec plusieurs grilles (de type Grid). Leurs positions sont
  déterminées par un décalage de positions de référence.

Chaque Solvable possède une liste de contraintes qui lui est propre, ainsi qu'une liste de mouvements qui ont été
effectués par le solveur ou l'utilisateur pour permettre le suivi de la résolution. Les symboles sont les valeurs
pouvant être stockées dans le Solvable.

_Voici un exemple de ce que peut être une grille de type Solvable, à travers un diagramme d'objet :_

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

Nous pouvons ainsi obtenir des grilles de sudoku comme celle-ci :
![img.png](diagram/img.png)

---

# Solveur :

Le Solveur est une classe très importante pour notre projet, étant une fonction majeure très sollicitée et sensible du
projet (utilisé par le générateur, les aides de l'utilisateur, etc.).
Cette fonction se doit d'être la plus rapide possible pour ne pas avoir de ralentissement trop important qui pourrait
nuire à l'expérience utilisateur.

C'est dans ce contexte qu'une attention particulière a été portée sur la question dès le début du projet, et n'a cessé
d'être remis en cause et optimisé pour en arriver là.
La méthode solve() permet de résoudre n'importe quel Solvable, en se basant les contraintes de la grille et/ou en
utilisant le backtracking pour les grilles plus complexes.

_Voici un diagramme d'activité de cette méthode essentielle au bon fonctionnement de notre application :_

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
    suppress: On récupère un element de la queue
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
state is_solved_bis_test <<choice>>
is_solved_bis_test --> RESOLU: Oui
is_solved_test --> RESOLU: Oui
RESOLU --> [*]
if_deduced: Doit on essayer de déduire avec les contraintes ?
state is_deduced_test <<choice>>
state is_backtracking_test <<choice>>
if_deduced --> is_deduced_test
is_backtracking_test --> SudokuSolver.doBacktracking: Oui
is_deduced_test --> SudokuSolver.doBacktracking: Non, alors on fait forcement du backtracking et on récupère les possibilités engendrée
is_deduced_test --> SudokuSolver.solveDeduction: Oui
if_solved_bis: La grille est résolu ?
SudokuSolver.solveDeduction --> if_solved_bis
if_solved_bis --> is_solved_bis_test
is_solved_bis_test --> if_unsolvable: Non
if_unsolvable: La grille peut être résolu en l'état ?
state is_unsolvable_test <<choice>>
if_unsolvable --> is_unsolvable_test
is_unsolvable_test --> INSOLVABLE: Non
is_unsolvable_test --> if_backtracking: Oui
if_backtracking: Doit on essayer le backtracking ?
if_backtracking --> is_backtracking_test
is_backtracking_test --> partial: Non, on ne peut pas aller plus loin juste avec le déduction
partial --> [*]
}
```

_On peut également observer cette même méthode de résolution à travers un diagramme de séquence :_

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

---

# Générateur :

Le Générateur est une classe complexe qui permet de générer des grilles de sudoku de différents types.
En fonction de la fonction de génération appelée, nous pouvons avoir une variété de sudokus :

- Des sudokus avec des contraintes de blocs de taille NxM, avec la fonction `generateSudokuWithBlockConstraints`
- Des sudokus avec des contraintes de valeurs sur des positions (contrainte de blocs déstructurée), avec la fonction
  `generateSudokuWithRandomBlockConstraint`
- Des multi-doku qui génèrent un multi-doku contenant des grilles de sudoku de taille 9x9 avec des formes prédéfinies,
  avec la fonction `generateMultigridSudoku`
  La forme de ces multi-doku peut facilement être enrichie en ajoutant des formes de grilles dans la fonction
  `getRandomOffset`.

Ces trois fonctions utilisant des fonctions communes pour générer leur grille de sudoku, nous avons un fonctionnement du
Generator qui ressemble à ceci :

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

state Generation {
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

## Détails des fonctions utiles à la génération

Nous présenterons dans cette section trois fonctions importantes pour la génération de sudoku.

### Génération rapide de sudokus avec des contraintes de blocs de taille NxM

Notre approche se base sur des sudokus déjà résolus et stockés dans un fichier conforme.
Le cas échéant, nous effectuons une génération traditionnelle.
Si un tel fichier existe et est conforme au type de sudoku demandé, nous allons mélanger astucieusement pour obtenir une
grille de sudoku résolu, mais totalement aléatoire.
Pour une grille de sudoku avec des contraintes de blocs de taille NxM déjà résolue :

- On mélange les lignes de blocs de contraintes, ce qui nous ajoute `M!` possibilités
- On mélange les colonnes de blocs de contraintes, ce qui nous ajoute `N!` possibilités
- On mélange les lignes au sein de chaque bloc de contraintes, ce qui nous ajoute `N!` possibilités
- On mélange les colonnes au sein de chaque bloc de contraintes, ce qui nous ajoute `M!` possibilités
- On mélange tous les symboles, ce qui nous ajoute `(NxM)!` possibilités

---

**Ainsi, nous obtenons un nombre de possibilités de `M!² * N!² * (NxM)!` pour une grille de sudoku de taille NxM, ce qui
représenterait plus de [470 millions de possibilités]{.underline} de grilles résolues avec [une unique solution]
{.underline} à partir d'une unique grille de sudoku pré résolues de taille 9x9.**

---

Voici le diagramme d'activité illustrant le processus de génération rapide de sudoku :

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

### Génération des contraintes pour un sudoku avec des contraintes de blocs de NxM :

Une fois une grille résolue générée, nous pouvons éventuellement faire en sorte que les contraintes de bloc comme ceci :

![img_1.png](diagram/img_1.png)

Puisse avoir des contraintes de blocs "mélangées" comme ceci :

![img_2.png](diagram/img_2.png)

Pour ce faire, il suffit de respecter un principe simple :

---

**Si une case d'un bloc des contraintes contient un symbole équivalent à un symbole d'un autre bloc de contraintes,
alors les cases de ces deux blocs de contraintes peuvent s'interchanger.**

---

_Exemple :_

![img_3.png](diagram/img_3.png)

_Voici le diagramme d'activité représentant le fonctionnement de la fonction :_

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

### Suppression des cellules dans un Solvable résolu :

Dans l'objectif de générer des grilles à faire résoudre par l'utilisateur, nous devons être en mesure de supprimer des
cellules de la grille précédemment résolue, tout en gardant l'unicité de la solution.

C'est dans cet objectif que nous avons créé la fonction `removeRandomCells` qui permet de supprimer des cellules
aléatoirement dans une grille résolue, tout en gardant l'unicité de la solution.

Cette fonction est très importante pour la génération, car elle est au cœur du processus de génération et est la
fonction qui va couter le plus de temps à s'exécuter.

Nous avons optimisé le processus en plaçant plusieurs cellules simultanément avant de tenter de résoudre le Sudoku.
Initialement, nous plaçons un lot de cellules de taille égale à $sqrt(n)$ (où $n$ est la taille du Sudoku). Si la
solution n'est pas unique, nous annulons le dernier lot et réduisons de moitié la taille du lot suivant, jusqu'à poser
les cellules une par une si nécessaire. (Cette optimisation n'est pas représentée dans le diagramme de séquence.)

_Voici le diagramme de séquence de cette fonction pour mieux comprendre ce qu'il se passe :_

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
        Generator ->> List(Vec2i): a = toTestRemove.removeFirst()
        activate List(Vec2i)
        List(Vec2i) -->> Generator: a = toTestRemove.removeFirst()
        deactivate List(Vec2i)
        Generator ->> Solvable: solvedGrid.placeUnchecked(a, null, null, true)
        activate Solvable
        Solvable -->> Generator: .
        Generator ->> Solvable: solvedGrid.computeAllEmptyCellsPossibilities()
        Solvable -->> Generator: .
        deactivate Solvable
        Generator ->> SudokuSolver: bool = SudokuSolver.solve(solvedGrid).getFirst()
        activate SudokuSolver
        SudokuSolver -->> Generator: bool = SudokuSolver.solve(solvedGrid).getFirst()
        deactivate SudokuSolver
        alt if b != SOLVED
            Generator ->> Solvable: undoLastMove()
            activate Solvable
            deactivate Solvable
            Solvable -->> Generator: .
        end
    end
    SudokuSolver ->> Generator: c = hasMoreThanOneSolution()
    activate SudokuSolver
    Generator -->> SudokuSolver: c = hasMoreThanOneSolution()
    deactivate SudokuSolver
    loop Tant que c = true
        Generator ->> Generator: undoLastMove()
        SudokuSolver ->> Generator: c = hasMoreThanOneSolution()
        activate SudokuSolver
        Generator -->> SudokuSolver: c = hasMoreThanOneSolution()
        deactivate SudokuSolver
    end
    Generator ->> Solvable: solvedGrid.cleanMoves()
    activate Solvable
    deactivate Solvable
    Solvable -->> Generator: .
    Generator ->> .: .
    deactivate Generator
```

---

# Comparaison des performances de génération de début de projet → fin de projet

## Résultat de la vitesse de génération de début de projet

Les résultats sont en fonction de la taille de la grille (Échantillon de 50 générations).

| Taille | Moyenne | Minimum | Maximum | Médiane |
|:------:|:-------:|:-------:|:-------:|:-------:|
|  4x4   |   4ms   |   1ms   |  33ms   |   3ms   |
|  9x9   |  474ms  |  13ms   | 14324ms |  117ms  |

*Nous n'avons testé uniquement le 4x4 et le 9x9 ont été testés, les grilles de tailles supérieures mettant trop de temps
pour pouvoir effectuer des tests corrects sur un échantillon de cette taille.*

![img_5.png](diagram/img_5.png)

## Résultat de la vitesse de génération rapide actuelle

Les résultats sont en fonction de la taille de la grille (Échantillon de 50 générations).

| Taille | Moyenne | Minimum | Maximum | Médiane |
|:------:|:-------:|:-------:|:-------:|:-------:|
|  4x4   |   5ms   |   2ms   |  79ms   |   3ms   |
|  9x9   |  31ms   |  20ms   |  77ms   |  28ms   |
| 16x16  |  472ms  |  390ms  |  676ms  |  465ms  |
| 25x25  | 5380ms  | 4490ms  | 6144ms  | 5368ms  |
| 36x36  | 38224ms | 34740ms | 41455ms | 38309ms |

![img_4.png](diagram/img_4.png)

---

**On remarque dès lors que la génération actuelle est plus rapide, mais surtout bien plus constante, ce qui est un
avantage non négligeable pour l'utilisateur.**

En effet, la génération de grilles de taille plus élevée entrainait une grande différence Min/Max dans la manière
précédente de générer nos grilles avec la méthode conventionnelle, ce qui n'est plus le cas avec la génération actuelle.

---

# Répartition

Pour ce projet, nous nous sommes repartis les tâches en tirant parti des compétences de chacun.

- Guillaume s'est occupé de concevoir et d'implémenter la résolution de sudoku. Il a également réalisé l'interface
  graphique avec ImGUI et a grandement participé aux différentes réorganisations du code.
- Thibaut s'est quant à lui occupé de l'interface graphique via swing, qui a ensuite été abandonnée, car non adaptée
  pour l'affichage de multi-doku. Il a également mis au propre la conception élaborée par l'équipe sous forme de
  diagramme mermaid.
  Il s'est de plus occupé en collaboration avec Eymeric de la génération de sudoku, et plus particulièrement de la
  génération des contraintes de blocs déstructurés ainsi que de la génération accélérée via des grilles préremplies.
- Eymeric de son côté, s'est occupé de l'interface en console ainsi que de la génération des sudokus. Il a par ailleurs
  réalisé plusieurs réorganisations afin de simplifier le code, la grande majorité des tests unitaires et la pipeline
  GitHub actions pour vérifier ces tests à chaque push.

# Extensions

- Environnement de travail : [github](https://github.com/FlashOnFire/SUUUUUUUUUUUudoku)
    - Nous avons pris soin de respecter le [nommage conventionnel des commits](https://www.conventionalcommits.org)


- Tests unitaires
    - Toutes les fonctions importantes du projet sont testées à l'aide de plusieurs tests unitaires afin d'éviter toute
      régression


- Pipeline de test automatique avec GitHub, actions et nix
    - Nous avons réalisé des configurations nix (flake + package) afin d'avoir un environnement de développement
      identique entre tous les développeurs.
    - Cela nous a permis de simplement créer un pipeline GitHub action qui lance la compilation du projet ainsi que les
      tests (dans le même environnement que les développeurs) et envoie un e-mail en cas de problème.


- Interface graphique
    - Nous avons réalisé deux interfaces graphiques.
        - Une première avec Swing, abandonnée à mi-projet par la découverte d'un autre outil plus puissant
        - Une seconde avec imGUI une librairie C++ à l'origine avec des bindings Java.
          C'est cette interface qui est privilégiée à ce jour.
          Elle a permis d'intégrer les multi-doku plus simplement


- Fichier de configuration et de sauvegarde
    - Nous avons créé des méthodes permettant d'importer et d'exporter des sudokus ainsi que des multi-doku dans des
      fichiers `.csv`. Cela nous sert pour les tests ainsi que pour accélérer la génération.
      Cependant, ces fichiers sont intégrés dans le fichier .jar ce qui ne permet pas facilement de les modifier. Et,
      par manque de temps, nous n'avons pas intégré la possibilité de les charger depuis un autre endroit que les
      ressources du jar.


- Grilles avec multiples solutions :
    - La fonction permettant de trouver toutes les solutions existe et est testée et fonctionnelle, cependant, elle
      n'est pas utilisée, car cela aurait demandé de lourdes modifications dans les interfaces d'affichage.


- Rajout de contraintes :
    - L'architecture de l'application est pensée pour pouvoir ajouter des contraintes simplement.
    - Cela nous a permis d'ajouter une contrainte sur une liste de position qui est, en quelque sorte une contrainte de
      bloc déstructuré (les éléments du bloc sont éclatés à travers la grille).


- Résolution par l'humain :
    - L'interface ImGUI prend en charge la résolution d'un sudoku par l'utilisateur, avec une possibilité d'avoir de l'
      aide si l'on est en difficulté.


- Génération de grille optimale
    - Vous pouvez générer une grille de sudoku de taille avec des performances raisonnables (6 s) jusqu'à 25x25.
    - Voir les diagrammes de générateur pour les détails sur la méthode utilisée


- Résolution de grille
    - Vous pouvez résoudre une grille de sudoku de taille avec des performances raisonnables jusqu'à 100x100.
    - Vous pouvez également résoudre des multigrilles efficacement


- Consulter l'historique des modifications
    - Vous pouvez consulter l'historique des modifications de la grille.
      Particulièrement utile pour les résolutions automatiques.


- Résolution manuelle
    - Vous pouvez résoudre une grille de sudoku de manière manuelle


- Interface en ligne de commande ergonomique
    - Vous pouvez utiliser une interface en ligne de commande pour jouer au sudoku avec une ergonomie proche de
      l'interface graphique
