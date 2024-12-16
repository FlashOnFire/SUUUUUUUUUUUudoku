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
    F -. &lt;&lt;include&gt;&gt; .-> G
    I -. &lt;&lt;include&gt;&gt; .-> G
    G -. &lt;&lt;include&gt;&gt; .-> D
```

### Diagramme de classes :

<!-- BEGIN_CLASS -->
```mermaid
classDiagram
class NotEmptyConstraint { 
+ boolean isSatisfied(Character[][])
}

class GraphicsSwing { 
}

class BlockConstraint { 
+ BlockConstraint BlockConstraint(Set~Character~,int,int,int,int)
+ boolean isSatisfied(Character[][])
}

class LineConstraint { 
+ LineConstraint LineConstraint(Set~Character~)
+ boolean isSatisfied(Character[][])
}

class AbstractConstraint { 
<<interface>>
}

class SudokuSolver { 
+ SolvingState solve(Grid,Set~Character~,boolean)$
}

class Grid { 
+ Grid Grid(Character[][],List~AbstractConstraint~)
+ void display()
+ boolean areConstraintsSatisfied()
+ boolean tryPlace(Vec2i,char)
+ boolean isSolved()
+ Grid clone()
}

class SymbolSets { 
+ Set~Character~ DIGITS$
}

class Vec2i { 
+ Vec2i Vec2i(int,int)
+ boolean equals(Object)
+ int hashCode()
+ String toString()
}

class ColumnConstraint { 
+ ColumnConstraint ColumnConstraint(Set~Character~)
+ boolean isSatisfied(Character[][])
}

class SolvingState { 
<<enum>>
+ SOLVED$
+ PARTIALLY_SOLVED$
+ UNSOLVABLE$
+ CONTINUE$
}

class Main { 
+ void main(String[])$
}

AbstractConstraint <|.. NotEmptyConstraint
Vec2i .. NotEmptyConstraint
AbstractConstraint <|.. BlockConstraint
Vec2i .. BlockConstraint
AbstractConstraint <|.. LineConstraint
Vec2i .. LineConstraint
SolvingState .. SudokuSolver
Grid .. SudokuSolver
Vec2i .. Grid
AbstractConstraint .. Grid
Grid .. Grid
AbstractConstraint <|.. ColumnConstraint
Vec2i .. ColumnConstraint
```
<!-- END_CLASS -->

### Diagramme de séquence :

```
Todo
```

