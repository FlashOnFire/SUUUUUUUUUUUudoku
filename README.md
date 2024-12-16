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

class Grid { 
+ Grid Grid(Character[][],List~AbstractConstraint~)
+ void display()
+ boolean areConstraintsSatisfied()
}

class SymbolSets { 
+ Set~Character~ DIGITS$
}

class ColumnConstraint { 
+ ColumnConstraint ColumnConstraint(Set~Character~)
+ boolean isSatisfied(Character[][])
}

class ConstraintsTests { 
+ void testBlockConstraint()
+ void testBlockConstraintFail()
+ void testColumnConstraint()
+ void testColumnConstraintFail()
+ void testLineConstraint()
+ void testLineConstraintFail()
}

class Main { 
+ void main(String[])$
}

AbstractConstraint <|.. NotEmptyConstraint
AbstractConstraint <|.. BlockConstraint
AbstractConstraint <|.. LineConstraint
AbstractConstraint <|.. ColumnConstraint
```
<!-- END_CLASS -->

### Diagramme de séquence :

```
Todo
```

