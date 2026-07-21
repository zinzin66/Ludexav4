# LUDEXA — État du projet et méthode de travail
(à coller intégralement en début de toute nouvelle session, Claude ou Gemini)

## Le projet
LUDEXA est un moteur de jeu 2D no-code en Java pour Android, avec système de
programmation visuelle par nœuds (Blueprint) — comparable au visual scripting
de Godot/Unity. L'utilisateur crée un jeu en plaçant des objets sur une scène
et en connectant des nœuds Événement/Condition/Action, sans écrire de code.

## Contraintes importantes
- L'utilisateur travaille UNIQUEMENT depuis une tablette, pas de PC.
- Développement via l'éditeur web/appli GitHub directement (pas d'IDE local).
- Test = compiler un APK via GitHub Actions à chaque push, télécharger et
  installer sur la tablette. Pas d'autre moyen de vérifier que le code marche.
- Dépôt : github.com/zinzin66/Ludexav4 (public), branche principale = main.

## Historique important — pourquoi on procède ainsi maintenant
Une première tentative (76 puis 102 fichiers) a échoué : plusieurs IA
(Gemini) ont régénéré les classes centrales (Port.java, NoeudBase.java) de
façon incompatible à chaque session, malgré une feuille de route et un
fichier de règles écrit. Résultat : rien ne compilait plus, trop de fichiers
à trier pour comprendre ce qui était sain. Décision : repartir de zéro avec
une méthode stricte de sessions courtes et testées.

## Méthode de travail (règles à respecter absolument)
1. **Fichiers figés, jamais réécrits sans le dire explicitement.** Port.java,
   NoeudBase.java, ObjetBase.java, Scene.java sont le contrat de base — ne
   jamais les modifier "en passant".
2. **Un seul écran/composant à la fois.** On construit d'abord toute
   l'interface graphique (les deux écrans ci-dessous) avec TOUS les boutons
   prévus visibles mais vides (pas de logique), puis on remplit un
   composant/menu par session.
3. **Sessions courtes : 1 à 3 fichiers maximum.**
4. **Toujours un test concret avant de continuer.** Jamais de "c'est fini"
   sans qu'un test précis ait été fait sur la tablette et confirmé.
5. Si l'utilisateur travaille avec Gemini en parallèle sur une branche
   séparée, ne jamais modifier main directement à sa place — proposer les
   fichiers, laisser l'utilisateur les intégrer.

## Architecture technique figée
- Package Java : `com.ludexa.moteur`
- Tous les fichiers .java dans : `app/src/main/java/com/ludexa/moteur/`
- Système nodal : `Port` (nom, type, valeurSaisie, connexion) + `NoeudBase`
  (id, nom, catégorie, portsEntree, portsSortie, ajouterPort, connecterPort,
  propagerExecution, executer abstrait)
- `ObjetBase` : id, nom, x, y, largeur, hauteur
- `Scene` : nom, liste d'objets, liste de nœuds logiques
- Deux écrans (Activity) : `EcranDemarrage` (point de lancement) et
  `InterfaceEditeur` (ouvert via bouton "Créer un projet")

## Plan des deux interfaces (liste figée, ne pas ajouter de boutons)

**EcranDemarrage** — mode paysage :
- Colonne gauche : logo, nom app, texte bienvenue, bouton langue
- Colonne droite : bouton Créer un projet, bouton Ouvrir un projet
  téléchargé, liste des projets existants (ouvrir/supprimer)

**InterfaceEditeur** — mode paysage :
- Bandeau haut : Quitter, nom du projet, Sauvegarde, Undo, Redo, Zoom -/+,
  Déplacer Scène, Blueprint (bascule), Build
- Centre : Canvas (rectangle rouge = caméra)
- Droite : menu Inspecteur masquable (réglages du composant sélectionné,
  bouton Supprimer)
- Gauche : menu coulissant accordéon avec 5 sections : Scènes
  (ajouter/supprimer/renommer), Objets à placer (texte, rond, carré, barre
  de défilement, entrée texte), Arborescence (ordre Z), Assets (import,
  sous-dossiers, supprimer, renommer), Variables (locales/globales : créer,
  renommer, changer valeur, supprimer)

## Ce qui est fait (validé par test réel sur tablette)
- ✅ Socle nodal Java pur : Port, NoeudBase, ObjetBase, Scene, NoeudEventStart
- ✅ Projet Android complet + GitHub Actions qui build l'APK à chaque push
- ✅ Chaîne Blueprint fonctionnelle : clic → NoeudEventStart → propagation →
  NoeudActionDeplacer → objet déplacé → réaffiché (testé avec l'ancien
  MainActivity/VueJeu, remplacé depuis par les 2 écrans ci-dessous)
- ✅ EcranDemarrage.java — testé, conforme à 100%
- ✅ InterfaceEditeur.java (bandeau du haut seulement, boutons vides, zone
  centrale provisoire "[ Canvas — à venir ]") — testé, conforme à 100%
- ✅ Navigation EcranDemarrage → InterfaceEditeur via bouton "Créer un projet"

## Prochaine étape (à faire dans la PROCHAINE session, une seule à la fois)
Choisir UN SEUL élément de l'InterfaceEditeur à construire ensuite, par
exemple : le menu Inspecteur à droite, OU le menu accordéon à gauche
(probablement à découper lui-même : commencer par une seule section, ex.
"Scènes"), OU le Canvas central avec le rectangle caméra.
→ Décider avec l'utilisateur en début de session laquelle avant de coder.
