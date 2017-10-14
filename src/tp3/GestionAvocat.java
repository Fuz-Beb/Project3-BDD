package tp3;

/**
 * Gestion des transactions de la table avocat.
 */
public class GestionAvocat
{
    private TableAvocat avocat;
    private Connexion cx;

    /**
     * Constructeur de confort
     * 
     * @param avocat
     */
    public GestionAvocat(TableAvocat avocat)
    {
        this.cx = avocat.getConnexion();
        this.avocat = avocat;
    }

    /**
     * Ajout d'un nouvelle avocat dans la base de données
     * 
     * @param id
     * @param prenom
     * @param nom
     * @param type
     * @throws Exception
     */
    public void ajouter(int id, String prenom, String nom, int type) throws Exception
    {
        try
        {
            // Vérifie si l'avocat existe déjà
            if (avocat.existe(id))
                throw new IFT287Exception("L'avocat existe déjà : " + id);

            avocat.ajouter(id, prenom, nom, type);

            cx.commit();
        }
        catch (Exception e)
        {
            cx.rollback();
            throw e;
        }
    }
}