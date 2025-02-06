---
title: "Projet APO Sudoku (et variantes) : résolution et génération"
author: Thibaut Laracine, Guillaume Calderon, Eymeric Dechelette
titlepage: true
---

## Tutoriel utilisation

### Prérequis

Si vous n'utilisez pas nix, les prérequis sont :

- `openjdk-23`
- `gradle`
- `libGL`

### Jar précompilé

Vous pouvez lancer les fichier en .jar dans le dossier build 

```bash
java -jar ./build/libs/imGUI-1.0.jar # Interface en ligne de commande
java -jar ./build/libs/tui-1.0.jar # Interface graphique avec imGUI
java -jar ./build/libs/swing-1.0.jar # Interface graphique avec swing
```

### Compilation

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

Vous pouvez lancer la compilation avec la commande suivante :

```bash
./gradlew buildAllJars
```

Vous pouvez ensuite lancer les différents executables avec les commandes suivantes :

```bash
java -jar ./build/libs/imGUI-1.0.jar # Interface en ligne de commande
java -jar ./build/libs/tui-1.0.jar # Interface graphique avec imGUI
java -jar ./build/libs/swing-1.0.jar # Interface graphique avec swing
```

##  Méthodologie

### Articulation conception codage

Pour aborder ce projet, nous avons commencé, aprés avoir lu attentivement le cahier des charge, par réaliser un diagramme de cas d'utilisation afin de s'assurer que chaque membre du groupe a parfaitement compris les objectif requis de l'application.
Cela nous a permis d'aborder sereinement notre diagramme de classe pour anticiper au mieux l'architecture de notre code.

#### Diagramme de classe

Nous avons préféré garder un diagramme de classe le plus minimaliste possible au debut car nous savons par experience, que nous devons toujours reorganiser le code plusieurs fois lors de son dévelopement.
Eviter de trop architecturer le projet permet de rester agile et de l'adapter au fur et a mesure.
Nous avons ensuite mis a jour ce diagramme au fur et à mesure du projet afin d'avoir une vue d'ensemble des notre organisation et de voir simplement les points améliorable et les répétition dans notre code.



### Répartition

Pour ce projets, nous nous sommes repartis les tache en tirant partis des compétence de chacun.

- Guillaume c'est occupé de concevoir et d'implementer la résolutions de sudoku. Il a également réalisé l'interface graphique avec ImGUI et a beaucoup participé au différente réorganisation du code.
- Thibaut c'est quand a lui occupé de l'interface graphique via swing qui à ensuite été abandonné car non adapté pour l'affichage de multidoku simple.
	Il c'est également occupé en collaboration avec Eymeric de la génération de sudoku, et plus particulièrement de la génération des contrainte de block déstructuré ainsi que la génération accéléré via des grilles préremplis
- Eymeric de son coté c'est occupé de l'interface en console ainsi que de la generation des sudoku. Il à également réalisé plusieurs réorganisation afin de simplifier le code.

