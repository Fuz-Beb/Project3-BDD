/**
 * 
 */
package Gestion;

import java.sql.Date;

import Table.TableProces;
import Table.TableSeance;
import tp3.Connexion;
import tp3.IFT287Exception;

/**
 * @author Bebo
 *
 */
public class GestionSeance
{
    private TableSeance seance;
    private TableProces proces;
    private Connexion cx;

    /**
     * Constructeur de confort
     * 
     * @param seance
     * @param proces
     * @throws IFT287Exception
     */
    public GestionSeance(TableSeance seance, TableProces proces) throws IFT287Exception
    {
        this.cx = seance.getConnexion();
        if (seance.getConnexion() != proces.getConnexion())
            throw new IFT287Exception(
                    "Les instances de TableSeance et de TableProces n'utilisent pas la même connexion au serveur");
        this.seance = seance;
        this.proces = proces;
    }

    /**
     * Ajout d'une nouvelle seance dans la base de données. S'il existe déjà,
     * une exception est levée.
     * 
     * @param idSeance
     * @param idProces
     * @param dateSeance
     * @throws Exception
     */
    public void ajout(int idSeance, int idProces, Date dateSeance) throws Exception
    {
        try
        {
            // Vérification si la seance existe deja
            if (seance.existe(idSeance))
                throw new IFT287Exception("La seance existe deja: " + idSeance);

            // Verification si le proces existe
            if (!proces.existe(idProces))
                throw new IFT287Exception("Le proces " + idProces + " n'existe pas.");

            // Verification si le proces specifie n'est pas termine
            if (!proces.verifierProcesTermine(idProces))
                throw new IFT287Exception("Le proces " + idProces + " est termine.");

            seance.ajout(idSeance, idProces, dateSeance);

            cx.commit();
        }
        catch (Exception e)
        {
            cx.rollback();
            throw e;
        }
    }

    /**
     * Supprimer une seance
     * 
     * @param idSeance
     * @throws Exception
     */
    public void supprimer(int idSeance) throws Exception
    {
        try
        {
            // Vérification si la seance existe
            if (!seance.existe(idSeance))
                throw new IFT287Exception("La seance n'existe pas : " + idSeance);

            // Vérification que la seance n'est pas encore passée
            if (seance.seancePassee(idSeance))
                throw new IFT287Exception("La seance " + idSeance + " est déjà passée.");

            seance.supprimer(idSeance);

            cx.commit();
        }
        catch (Exception e)
        {
            cx.rollback();
            throw e;
        }
    }
}