/**
 * 
 */
package tp3;

import java.sql.SQLException;

/**
 * @author Bebo
 *
 */
public class GestionPartie
{
    private TablePartie partie;
    private TableAvocat avocat;
    private Connexion cx;

    /**
     * Constructeur de confort
     * 
     * @param cx
     * @param partie
     * @param avocat
     * @throws IFT287Exception
     */
    public GestionPartie(Connexion cx, TablePartie partie, TableAvocat avocat) throws IFT287Exception
    {
        this.cx = partie.getConnexion();
        if (partie.getConnexion() != avocat.getConnexion())
            throw new IFT287Exception(
                    "Les instances de TablePartie et de TableAvocat n'utilisent pas la même connexion au serveur");
        this.partie = partie;
        this.avocat = avocat;
    }

    /**
     * Ajout d'un nouveau partie dans la base de données. S'il existe déjà, une
     * exception est levée.
     * 
     * @param id
     * @param prenom
     * @param nom
     * @param avocat_id
     * @throws SQLException
     * @throws IFT287Exception
     * @throws Exception
     */
    public void ajout(int id, String prenom, String nom, int avocat_id) throws SQLException, IFT287Exception, Exception
    {
        try
        {
            // Vérifie si le partie existe déjà
            if (partie.existe(id))
                throw new IFT287Exception("Partie existe déjà: " + id);

            // Vérifie si l'avocat existe
            if (!avocat.existe(avocat_id))
                throw new IFT287Exception("L'avocat " + avocat_id + "n'existe pas.");

            // Ajout du partie
            partie.ajout(id, prenom, nom, avocat_id);

            // Commit
            cx.commit();
        }
        catch (Exception e)
        {
            cx.rollback();
            throw e;
        }
    }
}
