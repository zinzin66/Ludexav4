1----
Récapitulatif technique pour tes notes de projet
Afin de conserver une trace claire de ce fonctionnement pour tes futures étapes :
Encapsulation : InspecteurProprietes étend directement LinearLayout.
En l'instanciant avec new InspecteurProprietes(this), la classe gère seule son interface, 
ses dimensions dynamiques (paramsOuvert / paramsFerme) et ses événements sans encombrer l'éditeur principal.
Modularité : Le fichier InterfaceEditeur agit désormais uniquement comme un "chef d'orchestre" qui positionne 
les grands blocs (Bandeau, Canvas, Inspecteur). Nous pourrons reproduire ce même schéma avec le menu accordéon de gauche.
Sécurité des données : L'utilisation de AlertDialog.Builder maintient la robustesse demandée en imposant une validation 
explicite avant toute action destructrice sur la scène.
2 ----
Encapsulation des transformations : La méthode onDraw utilise canvas.save(), modifie l'échelle avec canvas.scale(), 
puis restaure l'état avec canvas.restore(). Cela permet d'agrandir ou de rétrécir la vue sans altérer les véritables 
coordonnées spatiales du jeu.
Calcul de la grille étendu : Le dessin des lignes est calculé sur une zone proportionnellement plus grande (limiteMax) 
afin que les bords de la grille ne disparaissent pas à l'écran quand on dézoome fortement.
Vitesse de déplacement dynamique : Lors d'un glissement tactile (ACTION_MOVE), la distance captée par le doigt est
divisée par le niveauZoom. Ainsi, lorsqu'on est très proche (zoom fort), le déplacement de la scène est ajusté pour 
coller parfaitement au mouvement physique du doigt.
3 -----

