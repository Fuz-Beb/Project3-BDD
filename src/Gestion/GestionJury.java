package Gestion;

import Table.TableJury;
import Table.TableProces;
import tp3.Connexion;
import tp3.IFT287Exception;

/**
 * Gestion des transactions de la table jury.
 */
public class GestionJury
{
    private TableJury jury;
    private TableProces proces;
    private Connexion cx;

    /**
     * Constructeur de confort
     * 
     * @param jury
     * @param proces
     * @throws IFT287Exception
     */
    public GestionJury(TableJury jury, TableProces proces) throws IFT287Exception
    {
        this.cx = jury.getConnexion();

        if (jury.getConnexion() != proces.getConnexion())
            throw new IFT287Exception(
                    "Les instances de juge et de proces n'utilisent pas la même connexion au serveur");

        this.jury = jury;
        this.proces = proces;
    }

    /**
     * Ajout d'une jury dans la base de données
     * 
     * @param nas
     * @param prenom
     * @param nom
     * @param sexe
     * @param age
     * @throws Exception
     */
    public void ajouter(int nas, String prenom, String nom, String sexe, int age) throws Exception
    {
        try
        {
            if (jury.existe(nas))
                throw new IFT287Exception("Jury existe déjà : " + nas);
            jury.ajouter(nas, prenom, nom, sexe, age);
        }
        catch (Exception e)
        {
            cx.rollback();
            throw e;
        }
    }

    /**
     * Afficher la liste des jurys
     * 
     * @throws Exception
     */
    public void affichage() throws Exception
    {
        try
        {
            System.out.println(jury.affichage());
        }
        catch (Exception e)
        {
            cx.rollback();
            throw e;
        }
    }

    /**
     * Assigner un proces à un jury
     * 
     * @param idProces
     * @param nas
     * @throws Exception
     */
    public void assignerProces(int nas, int idProces) throws Exception
    {
        try
        {
            if (!proces.existe(idProces))
                throw new IFT287Exception("Proces n'existe pas : " + idProces);
            if (!proces.devantJury(idProces))
                throw new IFT287Exception("Le proces " + idProces + "doit se tenir devant un juge seul");
            jury.assignerProces(nas, idProces);
        }
        catch (Exception e)
        {
            cx.rollback();
            throw e;
        }
    }
}