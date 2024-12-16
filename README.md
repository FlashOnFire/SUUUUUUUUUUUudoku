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

```
<!-- END_CLASS -->

### Diagramme de séquence :

```
Todo
```

