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
+ Optional~List~Character~~ tryDeduce(Character[][],Vec2i)
}

class BlockConstraint { 
+ BlockConstraint BlockConstraint(Set~Character~,int,int,int,int)
+ boolean isSatisfied(Character[][])
+ Optional~List~Character~~ tryDeduce(Character[][],Vec2i)
}

class LineConstraint { 
+ LineConstraint LineConstraint(Set~Character~)
+ boolean isSatisfied(Character[][])
+ Optional~List~Character~~ tryDeduce(Character[][],Vec2i)
}

class AbstractConstraint { 
<<interface>>
}

class SudokuSolver { 
+ Optional~Grid~ solve(Grid,Set~Character~,boolean)$
}

class Grid { 
+ Grid Grid(Character[][],List~AbstractConstraint~)
+ void display()
+ boolean areConstraintsSatisfied()
+ Character[][] getGrid()
+ boolean tryPlace(Vec2i,char)
+ List~AbstractConstraint~ getConstraints()
}

class SymbolSets { 
+ Set~Character~ DIGITS$
}

class Vec2i { 
+ Vec2i Vec2i(int,int)
+ int getX()
+ int getY()
+ boolean equals(Object)
+ int hashCode()
+ String toString()
}

class ColumnConstraint { 
+ ColumnConstraint ColumnConstraint(Set~Character~)
+ boolean isSatisfied(Character[][])
+ Optional~List~Character~~ tryDeduce(Character[][],Vec2i)
}

class ConstraintsTests { 
+ void testBlockConstraint()
+ void testBlockConstraintEmpty()
+ void testBlockConstraintFail()
+ void testColumnConstraint()
+ void testColumnConstraintEmpty()
+ void testColumnConstraintFail()
+ void testLineConstraint()
+ void testLineConstraintEmpty()
+ void testLineConstraintFail()
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
Grid .. SudokuSolver
Vec2i .. Grid
AbstractConstraint .. Grid
AbstractConstraint <|.. ColumnConstraint
Vec2i .. ColumnConstraint
```
<!-- END_CLASS -->

### Diagramme de séquence :

```
Todo
```

