/**
 * 
 */
package tp3;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Bebo
 *
 */
public class TableProces
{
    private PreparedStatement stmtExisteProces;
    private PreparedStatement stmtInsertProces;
    private PreparedStatement stmtSelectProcesNonTermine;
    private PreparedStatement stmtTerminerProces;
    private PreparedStatement stmtVerificationProcesDecision;
    private PreparedStatement stmtProcesJugeEnCours;
    private PreparedStatement stmtVerificationProcesDevantJury;
    private PreparedStatement stmtSelectJugeDansProces;
    private Connexion cx;

    /**
     * Constructeur de confort. Creation d'une instance. Précompilation
     * d'énoncés SQL.
     * 
     * @param cx
     * @throws SQLException
     */
    public TableProces(Connexion cx) throws SQLException
    {
        this.cx = cx;
        stmtExisteProces = cx.getConnection().prepareStatement("select * from \"Proces\" where \"id\" = ?");
        stmtSelectProcesNonTermine = cx.getConnection()
                .prepareStatement("select * from \"Proces\" where \"id\" = ? and \"date\" < current_date");
        stmtTerminerProces = cx.getConnection()
                .prepareStatement("update \"Proces\" set \"decision\" = ? where \"id\" = ?");
        stmtVerificationProcesDecision = cx.getConnection()
                .prepareStatement("select * from \"Proces\" where \"id\" = ? and \"decision\" is null");
        stmtInsertProces = cx.getConnection().prepareStatement(
                "insert into \"Proces\" (\"id\", \"Juge_id\", \"date\", \"devantJury\", \"PartieDefenderesse_id\", \"PartiePoursuivant_id\") "
                        + "values (?,?,?,?,?,?)");
        stmtProcesJugeEnCours = cx.getConnection()
                .prepareStatement("select * from \"Proces\" where \"Juge_id\" = ? and \"decision\" is null");
        stmtVerificationProcesDevantJury = cx.getConnection()
                .prepareStatement("select from \"Proces\" where \"id\" = ? and \"devantJury\" = 1");
        stmtSelectJugeDansProces = cx.getConnection()
                .prepareStatement("select \"Juge_id\" from \"Proces\" where \"id\" = ?");
    }

    /**
     * Retourner la connexion associée.
     * 
     * @return Connexion
     */
    public Connexion getConnexion()
    {
        return cx;
    }

    /**
     * Verification de l'existance d'un proces
     * 
     * @param id
     * @return boolean
     * @throws SQLException
     */
    public boolean existe(int id) throws SQLException
    {
        stmtExisteProces.setInt(1, id);
        ResultSet rset = stmtExisteProces.executeQuery();
        boolean procesExiste = rset.next();
        rset.close();
        return procesExiste;
    }

    /**
     * Affichage des elements de proces
     * 
     * @param id
     * @return
     * @throws SQLException
     */
    public String affichage(int id) throws SQLException
    {
        String result = "\n\nAffichage du proces " + id + " :";

        stmtExisteProces.setInt(1, id);
        ResultSet rset = stmtExisteProces.executeQuery();

        if (rset.next())
        {
            do
            {
                result += "\n" + rset.getInt(1) + "\t" + rset.getInt(2) + "\t" + rset.getString(3) + "\t" + rset.getInt(4)
                        + "\t" + rset.getInt(5) + "\t" + rset.getInt(6) + "\t"
                        + (rset.getObject(7) != null ? rset.getInt(7) : "non termine");
            }
            while (rset.next());
        }

        rset.close();
        return result;
    }

    /**
     * Vérification que le proces a atteint sa date initiale
     * 
     * @param id
     * @return boolean
     * @throws SQLException
     */
    public boolean compareDate(int id) throws SQLException
    {
        stmtSelectProcesNonTermine.setInt(1, id);
        ResultSet rset = stmtSelectProcesNonTermine.executeQuery();
        boolean compareDate = rset.next();
        rset.close();
        return compareDate;
    }

    /**
     * Terminer le proces
     * 
     * @param decisionProces
     * @param id
     * @throws SQLException
     */
    public void terminer(int decisionProces, int id) throws SQLException
    {
        stmtTerminerProces.setInt(1, decisionProces);
        stmtTerminerProces.setInt(2, id);
        stmtTerminerProces.executeUpdate();
    }

    /**
     * Rendre le juge disponible si il n'a plus de proces en cours
     * 
     * @param id
     * @return int
     * @throws SQLException
     */
    public int changeJugeStatut(int id) throws SQLException
    {
        int idJuge = 0;

        stmtSelectJugeDansProces.setInt(1, id);
        ResultSet rset = stmtSelectJugeDansProces.executeQuery();

        if (rset.next())
        {
            idJuge = rset.getInt(1);
        }

        rset.close();

        return idJuge;
    }

    /**
     * Verifier si un juge a des proces en cours
     * 
     * @param id
     * @return boolean
     * @throws SQLException
     */
    public boolean jugeEnCours(int id) throws SQLException
    {
        stmtProcesJugeEnCours.setInt(1, id);
        ResultSet rset = stmtProcesJugeEnCours.executeQuery();

        if (rset.next())
        {
            return true;
        }
        
        rset.close();

        return false;
    }

    /**
     * Ajout du proces
     * 
     * @param idProces
     * @param idJuge
     * @param dateInitiale
     * @param devantJury
     * @param idPartieDefenderesse
     * @param idPartiePoursuivante
     * @throws SQLException
     */
    public void creer(int idProces, int idJuge, Date dateInitiale, int devantJury, int idPartieDefenderesse,
            int idPartiePoursuivante) throws SQLException
    {
        stmtInsertProces.setInt(1, idProces);
        stmtInsertProces.setInt(2, idJuge);
        stmtInsertProces.setDate(3, dateInitiale);
        stmtInsertProces.setInt(4, devantJury);
        stmtInsertProces.setInt(5, idPartieDefenderesse);
        stmtInsertProces.setInt(6, idPartiePoursuivante);
        stmtInsertProces.executeUpdate();
    }

    /**
     * Verification si le proces specifie n'est pas termine
     * 
     * @param idProces
     * @return boolean
     * @throws SQLException
     */
    public boolean verifierProcesTermine(int idProces) throws SQLException
    {
        stmtVerificationProcesDecision.setInt(1, idProces);
        ResultSet rset = stmtVerificationProcesDecision.executeQuery();
        boolean procesTermine = rset.next();
        rset.close();
        return procesTermine;
    }

    /**
     * Permet de savoir si un proces est devant un jury ou juge seul ou les deux
     * 
     * @param id
     * @return boolean
     * @throws SQLException
     */
    public boolean devantJury(int id) throws SQLException
    {
        stmtVerificationProcesDevantJury.setInt(1, id);
        ResultSet rset = stmtVerificationProcesDevantJury.executeQuery();
        boolean devantJury = rset.next();
        rset.close();
        return devantJury;
    }
}
