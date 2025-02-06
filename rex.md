---
title: "Projet APO Sudoku (et variantes) : résolution et génération"
author: Thibaut Laracine, Guillaume Calderon, Eymeric Dechelette
titlepage: true
---

##  Méthodologie

> Vous expliquerez votre méthodologie de travail (articulation entre conception et codage, déroulé temporel
du projet, ...) et la répartition des tâches entre les différents membres du groupe.

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

