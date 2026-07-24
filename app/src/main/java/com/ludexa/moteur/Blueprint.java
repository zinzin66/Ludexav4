package com.ludexa.moteur;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Blueprint {
    public List<NoeudBase> noeuds;
    public List<Lien> liens;
    
    public Map<String, Float> noeudsX;
    public Map<String, Float> noeudsY;

    public Blueprint() {
        this.noeuds = new ArrayList<>();
        this.liens = new ArrayList<>();
        this.noeudsX = new HashMap<>();
        this.noeudsY = new HashMap<>();
    }

    public void ajouterNoeud(NoeudBase noeud, float x, float y) {
        this.noeuds.add(noeud);
        this.noeudsX.put(noeud.id, x);
        this.noeudsY.put(noeud.id, y);
    }

    public void ajouterLien(NoeudBase depart, String portS, NoeudBase arrivee, String portE) {
        Lien l = new Lien(depart, portS, arrivee, portE);
        this.liens.add(l);
        depart.connecterPort(portS, arrivee, portE);
    }

    public static class Lien {
        public NoeudBase noeudDepart;
        public String portSortieNom;
        public NoeudBase noeudArrivee;
        public String portEntreeNom;
        
        public Lien(NoeudBase depart, String portS, NoeudBase arrivee, String portE) {
            this.noeudDepart = depart;
            this.portSortieNom = portS;
            this.noeudArrivee = arrivee;
            this.portEntreeNom = portE;
        }
    }

    public String toJson() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        BlueprintDTO dto = new BlueprintDTO();

        for (NoeudBase n : noeuds) {
            NoeudDTO ndto = new NoeudDTO();
            ndto.id = n.id;
            ndto.classeType = n.getClass().getName();
            ndto.x = noeudsX.containsKey(n.id) ? noeudsX.get(n.id) : 0f;
            ndto.y = noeudsY.containsKey(n.id) ? noeudsY.get(n.id) : 0f;

            // Sauvegarde des paramètres configurables (X, Y, etc.)
            if (n.aDesParametresEditables() && n.getNomsParametres() != null) {
                for (String paramNom : n.getNomsParametres()) {
                    ndto.parametres.put(paramNom, n.getValeurParametre(paramNom));
                }
            }
            
            // Sauvegarde de l'identifiant de la cible (son nom)
            if (n.requiertCibleObjet() && n.getCibleObjet() != null) {
                ndto.cibleNom = n.getCibleObjet().nom;
            }

            for (Port p : n.portsEntree) {
                if (p.valeurSaisie != null && !p.valeurSaisie.isEmpty()) {
                    PortDTO pdto = new PortDTO();
                    pdto.nom = p.nom;
                    pdto.valeurSaisie = p.valeurSaisie;
                    ndto.portsEntree.add(pdto);
                }
            }
            dto.noeuds.add(ndto);
        }

        for (Lien l : liens) {
            LienDTO ldto = new LienDTO();
            ldto.idDepart = l.noeudDepart.id;
            ldto.portDepart = l.portSortieNom;
            ldto.idArrivee = l.noeudArrivee.id;
            ldto.portArrivee = l.portEntreeNom;
            dto.liens.add(ldto);
        }

        return gson.toJson(dto);
    }

    // Nouvelle méthode acceptant la Scene pour pouvoir lier les cibles sauvegardées
    public static Blueprint fromJson(String json, Scene scene) {
        Gson gson = new Gson();
        BlueprintDTO dto = gson.fromJson(json, BlueprintDTO.class);
        Blueprint bp = new Blueprint();

        if (dto == null) return bp;

        Map<String, NoeudBase> dictionnaireNoeuds = new HashMap<>();

        for (NoeudDTO ndto : dto.noeuds) {
            try {
                Class<?> clazz = Class.forName(ndto.classeType);
                NoeudBase n = (NoeudBase) clazz.newInstance();
                n.id = ndto.id;

                // Restauration des paramètres dynamiques (comme X, Y)
                if (ndto.parametres != null) {
                    for (Map.Entry<String, String> entry : ndto.parametres.entrySet()) {
                        n.setValeurParametre(entry.getKey(), entry.getValue());
                    }
                }
                
                // Restauration de la cible en la cherchant dans la Scene
                if (ndto.cibleNom != null && scene != null && scene.objets != null) {
                    for (ObjetBase obj : scene.objets) {
                        if (ndto.cibleNom.equals(obj.nom)) {
                            n.setCibleObjet(obj);
                            break;
                        }
                    }
                }

                for (PortDTO pdto : ndto.portsEntree) {
                    for (Port p : n.portsEntree) {
                        if (p.nom.equals(pdto.nom)) {
                            p.valeurSaisie = pdto.valeurSaisie;
                            break;
                        }
                    }
                }

                bp.ajouterNoeud(n, ndto.x, ndto.y);
                dictionnaireNoeuds.put(n.id, n);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        for (LienDTO ldto : dto.liens) {
            NoeudBase dep = dictionnaireNoeuds.get(ldto.idDepart);
            NoeudBase arr = dictionnaireNoeuds.get(ldto.idArrivee);
            if (dep != null && arr != null) {
                bp.ajouterLien(dep, ldto.portDepart, arr, ldto.portArrivee);
            }
        }

        return bp;
    }

    // Surcharge pour garder la rétrocompatibilité si la scène n'est pas fournie
    public static Blueprint fromJson(String json) {
        return fromJson(json, null);
    }

    private static class BlueprintDTO {
        List<NoeudDTO> noeuds = new ArrayList<>();
        List<LienDTO> liens = new ArrayList<>();
    }

    private static class NoeudDTO {
        String id;
        String classeType;
        float x;
        float y;
        List<PortDTO> portsEntree = new ArrayList<>();
        
        // NOUVEAUX CHAMPS pour corriger le bug
        Map<String, String> parametres = new HashMap<>(); 
        String cibleNom; 
    }

    private static class PortDTO {
        String nom;
        String valeurSaisie;
    }

    private static class LienDTO {
        String idDepart;
        String portDepart;
        String idArrivee;
        String portArrivee;
    }
}
