---
title: "Projet APO Sudoku (et variantes) : résolution et génération"
author: Thibaut Laracine, Guillaume Calderon, Eymeric Déchelette
titlepage: true
toc-own-page: true
---

## Tutoriel utilisation

### Prérequis

Si vous n'utilisez pas nix, les prérequis sont :

- `openjdk-23`
- `gradle`
- `libGL`

### Jar précompilés

Vous pouvez lancer les fichiers en .jar dans le dossier build

```bash
java -jar ./build/libs/imGUI-1.0.jar # Interface graphique avec imGUI
java -jar ./build/libs/tui-1.0.jar # Interface en ligne de commande
java -jar ./build/libs/swing-1.0.jar # Interface graphique avec swing
```

### Compilation

#### Utilisateur nix

La méthode privilégiée pour lancer le programme est d'utiliser nix, car celui-ci vous assure d'avoir un environnement identique aux autres utilisateurs de nix.
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

#### Utilisateur Linux classique

Vous pouvez lancer la compilation avec la commande suivante :

```bash
./gradlew buildAllJars
```

Vous pouvez ensuite lancer les différents exécutables avec les commandes suivantes :

```bash
java -jar ./build/libs/imGUI-1.0.jar # Interface graphique avec imGUI
java -jar ./build/libs/tui-1.0.jar # Interface en ligne de commande
java -jar ./build/libs/swing-1.0.jar # Interface graphique avec swing
```

## Méthodologie

### Articulation conception codage

Pour aborder ce projet, nous avons commencé, après avoir lu attentivement le cahier des charges, par réaliser un diagramme de cas d'utilisation afin de s'assurer que chaque membre du groupe a parfaitement compris les objectifs requis de l'application.
Cela nous a permis d'aborder sereinement notre diagramme de classe pour anticiper au mieux l'architecture de notre code.

Voici le diagramme de cas d'utilisation réalisé au début du projet :

![](diagram/utilisation.png)


#### Diagramme de classe

Nous avons préféré garder un diagramme de classe le plus minimaliste possible au début, car nous savons par expérience que nous devons toujours réorganiser le code plusieurs fois lors de son développement.
Éviter de trop architecturer le projet permet de rester agile et de l'adapter au fur et à mesure.
Nous avons ensuite actualisé ce diagramme au fur et à mesure du projet afin d'avoir une vue d'ensemble des notre organisation et de voir simplement les points améliorables et les répétitions dans notre code.

Voici le diagramme de classe final mis à jour le 07/02/2025 :

![](diagram/class.png)

![](diagram/algorithm.png)
![](diagram/constraints.png)
![](diagram/graphics.png)
![](diagram/grid.png)
![](diagram/utils.png)

### Répartition

Pour ce projet, nous nous sommes repartis les tâches en tirant parti des compétences de chacun.

- Guillaume s'est occupé de concevoir et d'implémenter la résolution de sudoku. Il a également réalisé l'interface graphique avec ImGUI et a grandement participé aux différentes réorganisations du code.
- Thibaut s'est quant à lui occupé de l'interface graphique via swing qui a ensuite été abandonné, car non adapté pour l'affichage de multi-doku.
Il s'est de plus occupé en collaboration avec Eymeric de la génération de sudoku, et plus particulièrement de la génération des contraintes de blocs déstructurés ainsi que la génération accélérée via des grilles préremplies.
- Eymeric de son côté, s'est occupé de l'interface en console ainsi que de la génération des sudokus. Il a par ailleurs réalisé plusieurs réorganisations afin de simplifier le code. Et la pipeline GitHub actions pour vérifier les tests unitaires à chaque push.


## Extensions

- Environnement de travail : [github](https://github.com/FlashOnFire/SUUUUUUUUUUUudoku)
	+ Nous avons pris soin de respecter le nommage conventionnel des commits
- Tests unitaires
	+ Toutes les fonctions importantes du projet sont testées à l'aide de plusieurs tests unitaires afin d'éviter toute régression
- Pipeline de test automatique avec GitHub, actions et nix
	+ Nous avons réalisé des configurations nix (flake + package) afin d'avoir un environnement de développement identique entre tous les développeurs.
	+ Cela nous a permis de simplement créer un pipeline GitHub action qui lance la compilation du projet ainsi que les tests (dans le même environnement que les développeurs) et envoie un e-mail en cas de problème.
- Interface graphique
	+ Nous avons réalisé deux interfaces graphiques.
		* Une première avec Swing, abandonnée à mi-projet par la découverte d'un autre outil plus puissant
		* Une seconde avec imGUI une librairie C++ à l'origine avec des bindings Java. 
		C'est cette interface qui est privilégiée à ce jour.
		Elle a permis d'intégrer les multi-doku plus simplement

- Fichier de config et sauvegarde
	+ Nous avons créé des méthodes permettant d'importer et d'exporter des sudokus ainsi que des multi-doku dans des fichier `.csv`. Cela nous sert pour les tests ainsi que pour accélérer la génération.
	Cependant, ces fichiers sont intégrés dans le fichier .jar ce qui ne permet pas facilement de les modifier. Et, par manque de temps, nous n'avons pas intégré la possibilité de les charger depuis un autre endroit que les ressources du jar.

- Grilles avec multiples solutions :
	+ La fonction permettant de trouver toutes les solutions existe et est testé et fonctionnelle, cependant, elle n'est pas utilisée, car cela aurait demandé de lourdes modifications dans les interfaces d'affichage.

- Rajout de contraintes :
	+ L'architecture de l'application est pensée pour pouvoir ajouter des contraintes simplement.
	+ Cela nous a permis d'ajouter une contrainte sur une liste de position qui est, en quelque sorte une contrainte de bloc déstructuré (les éléments du bloc sont éclatés à travers la grille).

- Résolution par l'humain FAUT VOIR !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

- Génération de grille optimal
	+ Vous pouvez générer une grille de sudoku de taille avec des performances raisonnables (6 s) jusqu'à 25x25.
	+ Voir les diagrammes de générateur pour les détails sur la méthode utilisée

- Résolution de grille
	+ Vous pouvez résoudre une grille de sudoku de taille avec des performances raisonnables jusqu'à 100x100.
	+ Vous pouvez également résoudre des multigrille efficacement

- Consulter l'historique des modifications
	+ Vous pouvez consulter l'historique des modifications de la grille.
	Particulièrement utile pour les résolutions automatiques.
- Résolution manuelle
	+ Vous pouvez résoudre une grille de sudoku de manière manuelle
- Interface en ligne de commande ergonomique
	+ Vous pouvez utiliser une interface en ligne de commande pour jouer au sudoku avec une ergonomie proche de l'interface graphique