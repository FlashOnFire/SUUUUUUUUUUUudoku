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

Vous pouvez lancer les fichiers en .jar dans le dossier build 

```bash
java -jar ./build/libs/imGUI-1.0.jar # Interface graphique avec imGUI
java -jar ./build/libs/tui-1.0.jar # Interface en ligne de commande
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
java -jar ./build/libs/imGUI-1.0.jar # Interface graphique avec imGUI
java -jar ./build/libs/tui-1.0.jar # Interface en ligne de commande
java -jar ./build/libs/swing-1.0.jar # Interface graphique avec swing
```

##  Méthodologie

### Articulation conception codage

Pour aborder ce projet, nous avons commencé, aprés avoir lu attentivement le cahier des charges, par réaliser un diagramme de cas d'utilisation afin de s'assurer que chaque membre du groupe a parfaitement compris les objectifs requis de l'application.
Cela nous a permis d'aborder sereinement notre diagramme de classe pour anticiper au mieux l'architecture de notre code.

#### Diagramme de classe

Nous avons préféré garder un diagramme de classe le plus minimaliste possible au debut car nous savons par experience, que nous devons toujours réorganiser le code plusieurs fois lors de son développement.
Éviter de trop architecturer le projet permet de rester agile et de l'adapter au fur et à mesure.
Nous avons ensuite mis à jour ce diagramme au fur et à mesure du projet afin d'avoir une vue d'ensemble des notre organisation et de voir simplement les points améliorables et les répétitions dans notre code.



### Répartition

Pour ce projet, nous nous sommes repartis les tâches en tirant partis des compétences de chacun.

- Guillaume s'est occupé de concevoir et d'implementer la résolution de sudoku. Il a également réalisé l'interface graphique avec ImGUI et a beaucoup participé aux différentes réorganisations du code.
- Thibaut s'est quant à lui occupé de l'interface graphique via swing qui a ensuite été abandonné, car non adapté pour l'affichage de multi-doku simple.
	Il s'est également occupé en collaboration avec Eymeric de la génération de sudoku, et plus particulièrement de la génération des contraintes de block déstructuré ainsi que la génération accélérée via des grilles préremplies.
- Eymeric de son cotée s'est occupé de l'interface en console ainsi que de la generation des sudokus. Il a également réalisé plusieurs réorganisations afin de simplifier le code. Et la pipeline github actions pour verifier les tests unitaire à chaque push.

## Extensions

- Environnement de travail : [github](https://github.com/FlashOnFire/SUUUUUUUUUUUudoku)
	+ Nous avons pris soin de respecter le nommage conventionnel des commits
- Tests unitaires
	+ Toutes les fonctions importantes du projet sont testé à l'aide de plusieurs tests unitaires
- Pipeline de test automatique avec github actions et nix
	+ Nous avons réalisé des configurations nix (flake + package) afin d'avoir un environnement de développement identique entre tous les développeurs.
	+ Cela nous a permis de simplement créer un pipeline github action qui lance la compilation du projet ainsi que les tests et envoie un mail en cas de problème.
- Interface graphique
	+ Nous avons réalisé deux interfaces graphiques.
		* Une première avec Swing qui a été abandonné a mis projet par la découverte d'un autre outil plus puissant
		* Une deuxième avec imGUI une librairie cpp à l'origine avec des binding java. C'est cette interface qui est privilégiée à ce jour.
			Elle a permis d'intégrer les multi-doku plus simplement

- Fichier de config et sauvegarde (un peu)
	+ Nous avons créé des methods permettant d'importer et exporter des sudokus ainsi que des multi-doku. Cela nous sert pour les tests ainsi que pour accélérer la génération.
	+ Cependant, ces fichiers sont intégré dans le fichier .jar ce qui ne permet pas simplement de les modifiers. et par manque de temps, nous n'avons pas intégré la possibilité de les charger depuis un autre endroit que les resources du jar.

- Grilles avec multiples solutions :
	+ La fonction permettant de trouver toutes les solutions existe cependant elle n'est pas utilisé, car cela aurait demandé de lourdes modifications dans les interfaces d'affichage.

- Rajout de contraintes :
	+ L'architecture de l'application est pensé pour pouvoir ajouter des contraintes simplement.
	+ Cela nous a permis d'ajouter une contrainte sur une liste de position qui est en quelque sorte une contrainte de bloc destructuré (les elements du bloc sont éclaté à travers la grille).

- Resolution par l'humain FAUT VOIR !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!


- Génération de grille optimal
	+ Vous pouvez générer une grille de sudoku de taille avec des performances raisonnables (6s) jusqu'à 25x25.
	+ Voir les diagrammes de générateur pour les détails sur la methode utilisé

- Résolution de grille
	+ Vous pouvez résoudre une grille de sudoku de taille avec des performances raisonnables jusqu'à 100x100.
	+ Vous pouvez également résoudre des multi-grid efficacement

- Consulter l'historique des modifications
	+ Vous pouvez consulter l'historique des modifications de la grille.
	Particulièrement utile pour les résolutions automatiques.

- Résolution manuelle
  + Vous pouvez résoudre une grille de sudoku de manière manuelle

- Interface graphique
  + Vous pouvez utiliser une interface graphique pour jouer au sudoku

- Interface en ligne de commande ergonomique
  + Vous pouvez utiliser une interface en ligne de commande pour jouer au sudoku avec une ergonomie proche de l'interface graphique