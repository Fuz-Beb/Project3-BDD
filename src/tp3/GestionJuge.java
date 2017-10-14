package tp3;

import java.sql.SQLException;

/**
 * Gestion des transaction de la table juge.
 */
public class GestionJuge
{
    private TableJuge juge;
    private TableProces proces;
    private Connexion cx;

    /**
     * Constructeur de confort
     * 
     * @param juge
     * @param proces 
     * @throws IFT287Exception
     */
    public GestionJuge(TableJuge juge, TableProces proces) throws IFT287Exception
    {
        this.cx = juge.getConnexion();
        
        if (juge.getConnexion() != proces.getConnexion())
            throw new IFT287Exception(
                    "Les instances de juge et de proces n'utilisent pas la même connexion au serveur");
        
        this.juge = juge;
        this.proces = proces;
    }

    /**
     * Ajout d'un nouveau juge dans la base de données
     * 
     * @param id
     * @param prenom
     * @param nom
     * @param age
     * @throws Exception
     */
    public void ajouter(int id, String prenom, String nom, int age) throws Exception
    {
        try
        {
            if (juge.existe(id))
                throw new IFT287Exception("Le juge existe déjà : " + id);

            juge.ajouter(id, prenom, nom, age);
        }
        catch (Exception e)
        {
            cx.rollback();
            throw e;
        }
    }

    /**
     * Afficher la liste des juges actifs et disponibles
     * 
     * @throws SQLException
     */
    public void affichage() throws SQLException
    {
        try
        {
            System.out.println(juge.affichage());
        }
        catch (Exception e)
        {
            cx.rollback();
            throw e;
        }
    }

    /**
     * Retirer un juge
     * 
     * @param idJuge
     * @throws Exception
     */
    public void retirer(int idJuge) throws Exception
    {
        try
        {
            if (juge.existe(idJuge))
                throw new IFT287Exception("Juge inexistant : " + idJuge);
            if (proces.jugeEnCours(idJuge))
                throw new IFT287Exception("Le juge " + idJuge + " n'a pas terminé tout ses procès");
            juge.retirer(idJuge);
        }
        catch (Exception e)
        {
            cx.rollback();
            throw e;
        }
    }
}