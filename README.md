# Daar-projet1

- [ ] Ploting Stuff
- [ ] Commentaires

## Comment lancer le projet
1. Ce placer a la racine du projet
2. Lancer la commande suivante
```bash
    regEx/*.java regEx/Helpers/*.java  -cp regEx/Libs/junit-4.10.jar
```
### Execution
```bash
    java regEx/RegEx "<RegExp>" <TextPath> -<Flag> -<Flag>
```
#### Flags
- -t : Affiche le temps d'execution
- -d : Affiche l'automate généré au format .jpg dans le dossier Visual/

#### Exemple d'execution
```bash
    java regEx/RegEx "(S|s)argon" Tests/Books/sargon.txt -t -d
```

 