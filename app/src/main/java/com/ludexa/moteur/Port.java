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
