# LUDEXA — Référence technique complète
Dernière mise à jour : 21/07/2026
À coller intégralement en tout début de toute session Gemini, avec les
fichiers .java concernés joints en pièce jointe réelle.

---

## 1. Le projet, en une phrase
Moteur de jeu 2D no-code en Java/Android, avec système de programmation
visuelle par nœuds (Blueprint), développé et testé uniquement depuis une
tablette (pas de PC), via l'éditeur web GitHub + GitHub Actions pour
compiler l'APK à chaque push.

Dépôt : github.com/zinzin66/Ludexav4 — branche principale : main

---

## 2. RÈGLES ABSOLUES (ne jamais déroger, même si ça semble une amélioration)

1. **Ne jamais réécrire un fichier "de mémoire".** Si tu dois modifier un
   fichier existant, pars du contenu exact fourni en pièce jointe ou
   décrit ci-dessous — jamais d'une reconstruction approximative.
2. **Ne jamais créer un fichier non demandé.** Si tu penses qu'un fichier
   supplémentaire est nécessaire, dis-le et attends confirmation.
3. **Une tâche = une session = 1 à 3 fichiers maximum.** Ne pas enchaîner
   sur "la suite logique" sans validation de l'utilisateur.
4. **Zéro coquille vide.** Toute méthode livrée doit contenir la logique
   réelle demandée, pas un commentaire "à implémenter" — sauf pour les
   boutons explicitement listés comme "non câblés" ci-dessous.
5. **Ne jamais modifier le système nodal figé** (Port.java, NoeudBase.java)
   ni ObjetBase.java/Scene.java sans que ce soit explicitement demandé.
6. **Toujours donner un test précis et concret** à faire sur la tablette
   après le fichier livré. Ne jamais déclarer "c'est fini" sans ce test.
7. **Travailler sur une branche séparée**, jamais directement sur `main`,
   sauf instruction contraire explicite de l'utilisateur.

---

## 3. Architecture — package et emplacement
- Package Java unique : `com.ludexa.moteur`
- Tous les fichiers .java dans : `app/src/main/java/com/ludexa/moteur/`
- Config Android : `app/build.gradle`, `app/src/main/AndroidManifest.xml`
- Config Gradle racine : `build.gradle`, `settings.gradle`, `gradle.properties`
- Build automatique : `.github/workflows/build.yml` (déclenché à chaque
  push sur main, APK récupérable dans l'onglet Actions → artifacts)

---

## 4. LE SYSTÈME NODAL — FIGÉ, NE JAMAIS MODIFIER

### Port.java
```java
package com.ludexa.moteur;

public class Port {
    public static final String TYPE_EXECUTION_ENTREE = "EXECUTION_ENTREE";
    public static final String TYPE_EXECUTION_SORTIE = "EXECUTION_SORTIE";
    public static final String TYPE_DONNEE_ENTREE   = "DONNEE_ENTREE";
    public static final String TYPE_DONNEE_SORTIE   = "DONNEE_SORTIE";

    public String nom;
    public String type;
    public String valeurSaisie;
    public NoeudBase noeudDestination;
    public Port portDestination;

    public Port(String nom, String type) {
        this.nom = nom;
        this.type = type;
        this.valeurSaisie = "";
        this.noeudDestination = null;
        this.portDestination = null;
    }
}
```

### NoeudBase.java
```java
package com.ludexa.moteur;

import java.util.ArrayList;
import java.util.UUID;

public abstract class NoeudBase {
    public String id;
    public String nom;
    public String categorie;
    public ArrayList<Port> portsEntree;
    public ArrayList<Port> portsSortie;

    public NoeudBase(String id, String nom, String categorie) {
        this.id = id;
        this.nom = nom;
        this.categorie = categorie;
        this.portsEntree = new ArrayList<>();
        this.portsSortie = new ArrayList<>();
    }

    public void ajouterPort(Port port) {
        if (port.type.equals(Port.TYPE_EXECUTION_ENTREE) || port.type.equals(Port.TYPE_DONNEE_ENTREE)) {
            this.portsEntree.add(port);
        } else {
            this.portsSortie.add(port);
        }
    }

    public void connecterPort(String nomPortSortie, NoeudBase noeudArrivee, String nomPortEntree) {
        Port portSortie = trouverPort(this.portsSortie, nomPortSortie);
        Port portEntree = trouverPort(noeudArrivee.portsEntree, nomPortEntree);
        if (portSortie != null && portEntree != null) {
            portSortie.noeudDestination = noeudArrivee;
            portSortie.portDestination = portEntree;
        }
    }

    protected Port trouverPort(ArrayList<Port> listePorts, String nomPort) {
        for (Port p : listePorts) {
            if (p.nom.equals(nomPort)) return p;
        }
        return null;
    }

    protected void propagerExecution(String nomPortSortie) {
        Port port = trouverPort(this.portsSortie, nomPortSortie);
        if (port != null && port.noeudDestination != null) {
            port.noeudDestination.executer();
        }
    }

    protected static String genererId() {
        return UUID.randomUUID().toString();
    }

    public abstract void executer();
}
```

**Règle de création d'un nouveau nœud** : toujours hériter de NoeudBase avec
`super(genererId(), "Nom Affiché", "Catégorie")`, ajouter les ports avec
`this.ajouterPort(new Port("Nom", Port.TYPE_XXX))`, et dans `executer()`
finir par `propagerExecution("NomPortSortie")` si le nœud continue la chaîne.

### NoeudEventStart.java (seul nœud existant à ce jour)
```java
package com.ludexa.moteur;

public class NoeudEventStart extends NoeudBase {
    public NoeudEventStart() {
        super(genererId(), "Au Démarrage", "Événement");
        this.ajouterPort(new Port("Suivant", Port.TYPE_EXECUTION_SORTIE));
    }

    @Override
    public void executer() {
        propagerExecution("Suivant");
    }
}
```

### ObjetBase.java
```java
package com.ludexa.moteur;

import java.util.UUID;

public class ObjetBase {
    public String id;
    public String nom;
    public float x;
    public float y;
    public float largeur;
    public float hauteur;

    public ObjetBase(String nom, float x, float y, float largeur, float hauteur) {
        this.id = UUID.randomUUID().toString();
        this.nom = nom;
        this.x = x;
        this.y = y;
        this.largeur = largeur;
        this.hauteur = hauteur;
    }
}
```

### Scene.java
```java
package com.ludexa.moteur;

import java.util.ArrayList;
import java.util.List;

public class Scene {
    public String nom;
    public List<ObjetBase> objets;
    public List<NoeudBase> noeudsLogique;

    public Scene(String nom) {
        this.nom = nom;
        this.objets = new ArrayList<>();
        this.noeudsLogique = new ArrayList<>();
    }

    public void ajouterObjet(ObjetBase objet) {
        this.objets.add(objet);
    }

    public void ajouterNoeud(NoeudBase noeud) {
        this.noeudsLogique.add(noeud);
    }
}
```

---

## 5. LES ÉCRANS (Activity) — état exact au 21/07/2026

### EcranDemarrage.java
- Point de lancement de l'app (déclaré comme LAUNCHER dans le Manifest)
- Layout horizontal, mode paysage forcé
- Colonne gauche : logo (texte "[LOGO]"), nom "LUDEXA", texte de bienvenue,
  bouton "Langue : Français" (**non câblé**, commentaire "à implémenter")
- Colonne droite :
  - bouton "Créer un projet" → **câblé**, lance `InterfaceEditeur` via Intent
  - bouton "Ouvrir un projet téléchargé" (**non câblé**)
  - `ListView` "Projets existants" (**vide, pas d'adapter branché**)

### InterfaceEditeur.java
- Rôle de "chef d'orchestre" : crée la Scene active et instancie les
  composants, ne contient pas la logique métier elle-même
- Crée `Scene sceneActive = new Scene("SceneDepart")` avec un objet de
  test ("Carré" à 300,300, 80x80) à chaque ouverture (pas de persistance)
- Bandeau haut (de gauche à droite) :
  - Quitter → **câblé** (`finish()`, retour à EcranDemarrage)
  - TextView nom du projet ("Projet sans nom", texte fixe)
  - Sauvegarde → **non câblé**
  - Undo → **non câblé**
  - Redo → **non câblé**
  - Zoom [-] / [[]] (reset) / [+] → **câblés**, appellent
    `canvasEditeur.zoomMoins()/zoomReset()/zoomPlus()`
  - Déplacer Scène → **câblé**, bascule `canvasEditeur.setPanMode(bool)`
    et change le texte du bouton
  - "Node Editor" → **câblé**, lance `InterfaceBlueprint` via Intent
    (contenu de InterfaceBlueprint.java non documenté ici — à vérifier
    avant d'y toucher)
  - Build → **non câblé**
- Zone milieu (horizontal) : PanneauRessources (gauche) — CanvasEditeur
  (centre, poids 1) — InspecteurProprietes (droite)
- Instanciation exacte :
  ```java
  PanneauRessources panneauRessources = new PanneauRessources(this, sceneActive, canvasEditeur);
  InspecteurProprietes menuInspecteur = new InspecteurProprietes(this, sceneActive, canvasEditeur);
  canvasEditeur.setInspecteur(menuInspecteur);
  ```

### CanvasEditeur.java (extends View)
Champs : `paintGrille, paintCamera, paintObjet, paintSelection` (Paint),
`cameraX, cameraY, lastTouchX, lastTouchY, niveauZoom` (float), `isPanMode`
(boolean), `sceneActive` (Scene), `objetSelectionne` (ObjetBase),
`inspecteurLie` (InspecteurProprietes).

Méthodes publiques :
- `setScene(Scene)` — définit la scène affichée, invalidate()
- `getObjetSelectionne()` — retourne l'objet actuellement sélectionné
- `setInspecteur(InspecteurProprietes)` — lie le Canvas à l'Inspecteur
  pour le notifier des sélections
- `deselectionner()` — vide objetSelectionne à null (sans invalidate,
  l'appelant doit le faire)
- `setPanMode(boolean)` / `isPanMode()` — mode déplacement de caméra
- `zoomPlus()` / `zoomMoins()` (×1.25 / ÷1.25) / `zoomReset()` (=1.0)

Comportement `onDraw` : dessine une grille grise infinie (recalculée
selon cameraX/Y et zoom), un rectangle rouge fixe représentant la caméra
(200,200 à 600,500 + offset caméra), puis chaque `ObjetBase` de
`sceneActive.objets` en rectangle bleu, avec un contour jaune en plus si
l'objet == objetSelectionne.

Comportement tactile (`onTouchEvent`) :
- ACTION_DOWN : si pas en PanMode, cherche l'objet sous le doigt
  (méthode privée `trouverObjetSousToucher`, tient compte du zoom/caméra
  via `ecranVersScene`), met à jour objetSelectionne, notifie
  inspecteurLie.afficherObjet(...), invalidate()
- ACTION_MOVE : si en PanMode, déplace cameraX/Y proportionnellement au
  zoom, invalidate()
- **Pas encore implémenté** : déplacement d'un objet sélectionné par
  glisser (drag), redimensionnement, multi-sélection

### PanneauRessources.java (extends ScrollView)
Constructeur : `PanneauRessources(Context context, Scene scene, CanvasEditeur canvas)`
Fond gris foncé (#333333), largeur fixe 400dp. Contient un accordéon de
5 sections empilées verticalement, chacune un bouton-titre (▼/▶ selon
état) + un contenu masquable (View.GONE / View.VISIBLE) :

1. **Scènes** — bouton "Créer une scène" (ouvre popup nom → **Toast
   seulement, ne crée pas de vraie Scene**), un item fixe codé en dur
   "Niveau_1" avec boutons Renommer/Supprimer (popups → **Toast
   seulement, aucune vraie donnée modifiée**)
2. **Objets à placer** :
   - "+ Ajouter un Carré" → **câblé et fonctionnel** : crée un vrai
     `ObjetBase("Carré", 150f, 150f, 80f, 80f)`, l'ajoute à
     `sceneActive`, appelle `canvasEditeur.invalidate()`, Toast confirmation
   - "+ Ajouter un Texte" / "+ Ajouter un Rond" → **Toast seulement, pas
     de vraie création** (pas de classe Texte/Rond distincte pour l'instant,
     tout objet créé est un ObjetBase générique — pas de sous-classes)
3. **Arborescence** — section générique, affiche juste un texte statique
   "[ Gestion de l'ordre Z ]", **aucune fonction réelle**
4. **Assets** — un item fixe codé en dur "Sprite_Joueur" avec
   Renommer/Supprimer (**Toast seulement**), pas d'import de fichier réel
5. **Variables** — bouton "Créer une variable" (**Toast seulement**), un
   item fixe codé en dur "scoreJoueur" avec Supprimer (**Toast seulement**)

**Point important** : aucune des listes (Scènes, Assets, Variables) n'est
une vraie liste dynamique (pas de ArrayList + adapter) — tout est un
unique item statique en dur dans le layout. Toute future implémentation
réelle devra remplacer ces items fixes par des ListView/RecyclerView
alimentées par de vraies données.

Méthodes privées utilitaires (popups génériques réutilisées partout) :
`afficherPopupCreer(Context, String type)`, `afficherPopupRenommer(Context,
String nomActuel)`, `afficherPopupConfirmation(Context, String message)` —
toutes utilisent `android.app.Dialog` (pas AlertDialog ici, contrairement
à InspecteurProprietes qui utilise AlertDialog — incohérence mineure
existante, pas bloquante).

Note : la toute fin du fichier a une indentation un peu inhabituelle
(l'accolade fermant la classe est indentée à 4 espaces au lieu de 0),
mais c'est purement cosmétique — vérifié sans impact sur la compilation,
le fichier est correct tel quel.

### InspecteurProprietes.java (extends LinearLayout)
Constructeur : `InspecteurProprietes(Context context, Scene scene, CanvasEditeur canvas)`
Fond gris clair (#E0E0E0), largeur 450dp ouvert / WRAP_CONTENT fermé.

Structure : en-tête (titre "Inspecteur" + bouton masquer `>`/`<`) puis
ScrollView contenant soit un texte d'info ("Sélectionnez un objet..."),
soit (si un objet est sélectionné) un bloc de champs Nom/X/Y + bouton
"Supprimer l'objet".

Méthode publique clé : `afficherObjet(ObjetBase objet)` — appelée par
CanvasEditeur à chaque sélection tactile. Si `objet == null`, montre le
texte d'info et cache le bloc propriétés. Sinon, remplit champNom/champX/
champY avec les valeurs réelles de l'objet (utilise un flag
`miseAJourEnCours` pour éviter que remplir les champs ne redéclenche les
TextWatcher).

Synchronisation temps réel champs → objet (TextWatcher sur chaque champ) :
- champNom → `objetCourant.nom = texte`
- champX / champY → parse en float (try/catch NumberFormatException
  silencieux si texte invalide), puis `canvasEditeur.invalidate()`

Bouton Supprimer : AlertDialog de confirmation → si confirmé, retire
l'objet de `sceneActive.objets`, appelle `canvasEditeur.deselectionner()`,
`afficherObjet(null)`, `canvasEditeur.invalidate()`, Toast confirmation.

### InterfaceBlueprint.java
**Existe dans le projet** (accessible via bouton "Node Editor" de
InterfaceEditeur) mais **son contenu n'a jamais été vérifié/documenté**
dans cette référence. Avant toute modification liée au Blueprint visuel,
demander ce fichier à l'utilisateur pour le documenter d'abord.

-----------------------------------------------------------------------------
-----------------------------------------------------------------------------

## 6. Ce qui reste à faire (backlog, par ordre de priorité suggéré)

Chaque ligne = une session Gemini distincte, testée avant de passer à
la suivante. Pour chaque tâche, joindre à Gemini CE DOCUMENT + les
fichiers listés dans "Fichiers à joindre".

**1. Bouton "Créer une scène" → vraie création + bascule entre scènes**
Fichiers à joindre : `PanneauRessources.java`, `Scene.java`,
`InterfaceEditeur.java`

**2. Liste de scènes dynamique (remplacer l'item "Niveau_1" fixe)**
Fichiers à joindre : `PanneauRessources.java`, `Scene.java`
(dépend de la tâche 1 — à faire après, ou en même temps si Gemini s'en
sort bien sur les deux d'un coup)

**3. Assets et Variables — vraies structures de données + UI dynamique**
Fichiers à joindre : `PanneauRessources.java`, `ObjetBase.java` (pour
référence de style), et préciser à Gemini qu'il doit d'abord créer une
petite classe `Asset` et une classe `VariableProjet` (nom + valeur) avant
de connecter l'UI — nouveaux fichiers à valider avant de les coller

**4. Sous-classes ObjetBase (Texte, Rond) ou champ type générique**
Fichiers à joindre : `ObjetBase.java`, `PanneauRessources.java`,
`CanvasEditeur.java` (car onDraw devra dessiner différemment selon le type)
→ Discuter d'abord avec Claude ou trancher soi-même l'approche avant de
lancer Gemini dessus, cette décision structure tout ce qui suit

**5. Déplacement d'un objet sélectionné par glisser sur le Canvas**
Fichiers à joindre : `CanvasEditeur.java`, `ObjetBase.java`

**6. Sauvegarde JSON réelle (Gson)**
Fichiers à joindre : `Scene.java`, `ObjetBase.java`, `InterfaceEditeur.java`
+ préciser à Gemini qu'il faudra ajouter la dépendance Gson dans
`app/build.gradle` (fichier à joindre aussi)

**7. Undo/Redo (Command Pattern)**
Fichiers à joindre : `InterfaceEditeur.java`, `Scene.java`,
`ObjetBase.java` — nouveau fichier à créer : une interface `Commande`
(executer/annuler), à faire valider par Claude avant de lancer Gemini
dessus vu que c'est une nouvelle pièce du contrat

**8. Documenter puis connecter InterfaceBlueprint.java**
Fichiers à joindre : `InterfaceBlueprint.java` (à donner à Claude
d'abord pour le documenter dans ce fichier, avant toute tâche Gemini)

8.1 — Afficher les nœuds existants sur le canvas
Tâche : dans CanvasBlueprint, faire en sorte qu'il lise le Blueprint associé à la Scene et dessine chaque NoeudEventStart déjà présent (rectangle avec titre, ports d'entrée/sortie visibles) à ses coordonnées stockées. Pas de création/déplacement pour l'instant, juste l'affichage.
Fichiers à fournir : Port.java, NoeudBase.java, Blueprint.java, NoeudEventStart.java, CanvasBlueprint.java

8.2 — Lister les vrais nœuds dans PanneauNoeuds
Tâche : remplacer le contenu placeholder des sections Événements/Actions par la vraie liste des classes de nœuds disponibles (NoeudEventStart, NoeudActionDeplacer), chacune affichée comme un item cliquable/glissable.
Fichiers à fournir : PanneauNoeuds.java, NoeudEventStart.java, NoeudActionDeplacer.java, NoeudBase.java

8.3 — Glisser un nœud du panneau vers le canvas pour le créer
Tâche : gérer le drag depuis un item de PanneauNoeuds jusqu'au CanvasBlueprint ; au relâchement, instancier le bon type de NoeudBase dans le Blueprint de la Scene, aux coordonnées du point de dépôt, et rafraîchir l'affichage.
Fichiers à fournir : PanneauNoeuds.java, CanvasBlueprint.java, Blueprint.java, NoeudBase.java

8.4 — Déplacer un nœud posé sur le canvas
Tâche : détection tactile sur un nœud déjà placé, suivi du doigt pour le repositionner, mise à jour de ses coordonnées stockées dans le Blueprint, redessin en continu.
Fichiers à fournir : CanvasBlueprint.java, NoeudBase.java, Blueprint.java

8.5 — Tracer une connexion visuelle entre deux ports
Tâche : détecter le tap/drag depuis un port de sortie vers un port d'entrée compatible, créer un objet Lien dans le Blueprint, dessiner la courbe de connexion entre les deux nœuds sur le canvas.
Fichiers à fournir : Port.java, Lien.java (si existant, sinon à créer), Blueprint.java, CanvasBlueprint.java, NoeudBase.java


**9. Bouton Build (export réel)**
Ne pas commencer avant que tout le reste soit stable — pas de fichiers
à préparer pour l'instant

---

## 7. Comment travailler avec Gemini, concrètement

1. Créer une nouvelle branche depuis `main` à jour (sélecteur de branche
   sur GitHub → taper un nom → "Create branch")
2. Ouvrir une conversation Gemini, coller CE DOCUMENT en entier
3. Joindre en pièce jointe réelle les fichiers `.java` que Gemini va lire
   ou modifier (minimum : les fichiers concernés par la tâche du jour)
4. Donner UNE SEULE tâche précise, tirée de la liste de la section 6
5. Une fois le code reçu, le coller sur GitHub sur la branche de travail
6. Vérifier le build (onglet Actions)
7. Vérifier le diff (comparer la branche à main) — s'assurer qu'aucun
   fichier de la section 4 (système figé) n'apparaît dans les changements
8. Tester l'APK sur la tablette selon le test précis demandé à Gemini
9. Si tout est bon → Pull Request → Merge dans main → supprimer la branche
10. Mettre à jour ce document (section 5 et 6) avant la prochaine session

