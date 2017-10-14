package tp3;

import java.sql.SQLException;

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
     */
    public GestionJury(TableJury jury, TableProces proces)
    {
        this.cx = jury.getConnexion();
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
    public void ajouter(int nas, String prenom, String nom, char sexe, int age) throws Exception
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
    public void assignerProces(int idProces, int nas) throws Exception
    {
        try
        {
            if (!proces.existe(idProces))
                throw new IFT287Exception("Proces n'existe pas : " + idProces);
            if (!proces.devantJury(idProces))
                throw new IFT287Exception("Le proces " + idProces + "doit se tenir devant un juge seul");
            jury.assignerProces(idProces, nas);
        }
        catch (Exception e)
        {
            cx.rollback();
            throw e;
        }
    }
}