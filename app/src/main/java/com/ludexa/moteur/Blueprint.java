// haut 1
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

    // NOUVEAU : Centralise la création d'un lien et le câblage mémoire (utile pour l'éditeur Blueprint)
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

    // --- PARTIE SAUVEGARDE JSON (GSON) ---

    public String toJson() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        BlueprintDTO dto = new BlueprintDTO();

        for (NoeudBase n : noeuds) {
            NoeudDTO ndto = new NoeudDTO();
            ndto.id = n.id;
            ndto.classeType = n.getClass().getName();
            ndto.x = noeudsX.containsKey(n.id) ? noeudsX.get(n.id) : 0f;
            ndto.y = noeudsY.containsKey(n.id) ? noeudsY.get(n.id) : 0f;

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

    public static Blueprint fromJson(String json) {
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
                // Utilisation de la nouvelle méthode pour rétablir le lien et la connexion
                bp.ajouterLien(dep, ldto.portDepart, arr, ldto.portArrivee);
            }
        }

        return bp;
    }

    // --- CLASSES DTO INTERNES POUR GSON ---

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
// bas 1
