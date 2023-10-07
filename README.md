# Daar-projet1
Projet 1 de l'UE DAAR
## Description de l'arborescence

- **regEx** : Contient le code source du projet
- **Tests** : Contient les fichiers de tests
- **Visual** : Contient les images générées par le programme

## Comment lancer le projet
1. Se placer a la racine du projet

### Compilation
2. Lancer la commande suivante
```bash
    javac regEx/*.java regEx/Helpers/*.java  -cp regEx/Libs/junit-4.10.jar
```
### Execution
3. Lancer la commande suivante
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

 
 ## Dependances
- Java 8
  
- Graphviz (Non obligatoire, permet de générer l'automate au format .jpg)
  - Linux : `sudo apt-get install graphviz`
  - Mac Brew: `brew install graphviz`
  - Mac MacPort: `sudo port install graphviz`
  - Windows : [Télécharger](https://graphviz.gitlab.io/_pages/Download/Download_windows.html)