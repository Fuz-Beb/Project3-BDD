/**
 * 
 */
package Gestion;

import java.sql.Date;

import Table.TableJuge;
import Table.TablePartie;
import Table.TableProces;
import Table.TableSeance;
import tp3.Connexion;
import tp3.IFT287Exception;

/**
 * @author Bebo
 *
 */
public class GestionProces
{
    private Connexion cx;
    private TableProces proces;
    private TableSeance seance;
    private TableJuge juge;
    private TablePartie partie;

    /**
     * Constructeur de confort
     * 
     * @param proces
     * @param seance
     * @param juge
     * @param partie
     * @throws IFT287Exception
     */
    public GestionProces(TableProces proces, TableSeance seance, TableJuge juge, TablePartie partie)
            throws IFT287Exception
    {
        this.cx = proces.getConnexion();
        if (proces.getConnexion() != seance.getConnexion())
            throw new IFT287Exception(
                    "Les instances de TableProces et TableSeance n'utilisent pas la même connexion au serveur");
        if (proces.getConnexion() != juge.getConnexion())
            throw new IFT287Exception(
                    "Les instances de TableProces et TableJuge n'utilisent pas la même connexion au serveur");
        if (proces.getConnexion() != partie.getConnexion())
            throw new IFT287Exception(
                    "Les instances de TableProces et TablePartie n'utilisent pas la même connexion au serveur");

        this.proces = proces;
        this.seance = seance;
        this.juge = juge;
        this.partie = partie;
    }

    /**
     * Methode d'affichage d'un proces
     * 
     * @param id
     * @throws Exception
     */
    public void affichage(int id) throws Exception
    {
        try
        {
            if (!proces.existe(id))
                throw new IFT287Exception("Le proces " + id + "n'existe pas");

            System.out.println(proces.affichage(id));
            System.out.println(seance.affichage(id));

            cx.commit();
        }
        catch (Exception e)
        {
            cx.rollback();
            throw e;
        }
    }

    /**
     * Methode de traitement pour effectuerTerminerProces
     * 
     * @param id
     * @param decisionProces
     * @throws Exception
     */
    public void terminer(int id, int decisionProces) throws Exception
    {
        try
        {
            // Verification de la valeur de la decision
            if (decisionProces != 0 && decisionProces != 1)
                throw new IFT287Exception(
                        "Impossible de terminer le proces " + id + "car la valeur de la decision n'est ni 0 ni 1.");

            // Vérification que le proces existe
            if (!proces.existe(id))
                throw new IFT287Exception("Le proces " + id + "n'existe pas.");

            // Vérification que le proces a atteint sa date initiale
            if (!proces.compareDate(id))
                throw new IFT287Exception("Le proces " + id + "n'a pas atteint sa date initiale.");

            proces.terminer(decisionProces, id);
            proces.changeJugeStatut(id);

            if (proces.jugeEnCours(id))
                juge.changerDisponibilite(true, id);

            seance.supprimerSeancesProcesTermine(id);

            cx.commit();
        }
        catch (Exception e)
        {
            cx.rollback();
            throw e;
        }
    }

    /**
     * Permet de creer un proces
     * 
     * @param idProces
     * @param idJuge
     * @param dateInitiale
     * @param devantJury
     * @param idPartieDefenderesse
     * @param idPartiePoursuivante
     * @throws Exception
     */
    public void creer(int idProces, int idJuge, Date dateInitiale, int devantJury, int idPartieDefenderesse,
            int idPartiePoursuivante) throws Exception
    {
        try
        {
            if (devantJury != 0 && devantJury != 1)
                throw new IFT287Exception("Impossible de creer le proces " + idProces
                        + "car le champ devantJury ne peut être que 0 ou 1");

            // Vérification que le proces n'existe pas déjà
            if (proces.existe(idProces))
                throw new IFT287Exception("Le proces " + idProces + "existe déjà.");
            // Vérification que l'id du juge est correcte
            if (!juge.existe(idJuge))
                throw new IFT287Exception("Le juge " + idJuge + "n'existe pas.");
            if (!partie.existe(idPartieDefenderesse))
                throw new IFT287Exception("La partie defenderesse " + idPartieDefenderesse + "n'existe pas.");
            if (!partie.existe(idPartiePoursuivante))
                throw new IFT287Exception("La partie poursuivante " + idPartiePoursuivante + "n'existe pas.");

            proces.creer(idProces, idJuge, dateInitiale, devantJury, idPartieDefenderesse, idPartiePoursuivante);

            // Rendre le juge non disponible
            juge.changerDisponibilite(false, idJuge);
            cx.commit();
        }
        catch (Exception e)
        {
            cx.rollback();
            throw e;
        }
    }
}
