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
public class TableSeance
{
    private PreparedStatement stmtExisteSeance;
    private PreparedStatement stmtInsertSeance;
    private PreparedStatement stmtExisteProcesDansSeance;
    private PreparedStatement stmtSupprimerSeancesProcesTermine;
    private PreparedStatement stmtSeanceNonTerminee;
    private PreparedStatement stmtSupprimerSeance;
    private Connexion cx;

    /**
     * Constructeur de confort. Creation d'une instance. Précompilation
     * d'énoncés SQL.
     * 
     * @param cx
     * @throws SQLException
     */
    public TableSeance(Connexion cx) throws SQLException
    {
        this.cx = cx;
        stmtExisteSeance = cx.getConnection().prepareStatement("select * from \"Seance\" where id = ?");
        stmtExisteProcesDansSeance = cx.getConnection()
                .prepareStatement("select * from \"Seance\" where \"Proces_id\" = ?");
        stmtSupprimerSeancesProcesTermine = cx.getConnection()
                .prepareStatement("select * from \"Seance\" where \"Proces_id\" = ? and date > current_date");
        stmtSeanceNonTerminee = cx.getConnection()
                .prepareStatement("select * from \"Seance\" where \"id\" = ? and \"date\" < current_date");
        stmtSupprimerSeance = cx.getConnection().prepareStatement("delete from \"Seance\" where \"id\" = ?");
        stmtInsertSeance = cx.getConnection()
                .prepareStatement("insert into \"Seance\" (\"id\", \"Proces_id\", \"date\") values (?,?,?)");

    }

    /**
     * Affichage des seances lie a un proces
     * 
     * @param id
     * @throws SQLException
     */
    public String affichage(int id) throws SQLException
    {
        String result = "";
        stmtExisteProcesDansSeance.setInt(1, id);
        ResultSet rset = stmtExisteProcesDansSeance.executeQuery();

        if (rset.next())
        {
            result += "\nListe des seances liees au proces " + id + " :";

            // Affichage des seances liees au proces
            do
            {
                result += rset.getInt(1) + "\t" + rset.getInt(2) + "\t" + rset.getString(3);
            }
            while (rset.next());

            rset.close();
        }
        else
        {
            result += "Aucune seance n'est liee au proces " + id;
            rset.close();
        }
        rset.close();
        return result;
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
     * Suppresion des seances prevues du proces
     * 
     * @param id
     * @throws SQLException
     * @throws IFT287Exception
     */
    public void supprimerSeancesProcesTermine(int id) throws SQLException, IFT287Exception
    {
        stmtSupprimerSeancesProcesTermine.setInt(1, id);
        ResultSet rset = stmtSupprimerSeancesProcesTermine.executeQuery();

        // Suppression des seances une a une
        while (rset.next())
        {
            supprimer(rset.getInt(1));
        }

        rset.close();
    }

    /**
     * Methode de traitement pour effectuerSupprimerSeance
     * 
     * @param id
     * 
     * @throws IFT287Exception
     * @throws SQLException
     */
    public void supprimer(int id) throws IFT287Exception, SQLException
    {
        if (!existe(id))
            throw new IFT287Exception("La seance: " + id + " n'existe pas.");

        if (seancePassee(id))
            throw new IFT287Exception("La seance: " + id + " est deja passe.");

        stmtSupprimerSeance.setInt(1, id);
        stmtSupprimerSeance.executeUpdate();
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
        stmtExisteSeance.setInt(1, id);
        ResultSet rset = stmtExisteSeance.executeQuery();
        boolean seanceExiste = rset.next();
        rset.close();
        return seanceExiste;
    }

    /**
     * Vérification que la seance n'est pas encore passee
     * 
     * @param id
     * @return boolean
     * @throws SQLException
     */
    public boolean seancePassee(int id) throws SQLException
    {
        stmtSeanceNonTerminee.setInt(1, id);
        ResultSet rset = stmtSeanceNonTerminee.executeQuery();
        boolean seancePassee = rset.next();
        rset.close();
        return seancePassee;
    }

    /**
     * Ajout de la seance
     * 
     * @param idSeance
     * @param idProces
     * @param dateSeance
     * @throws SQLException
     */
    public void ajout(int idSeance, int idProces, Date dateSeance) throws SQLException
    {
        stmtInsertSeance.setInt(1, idSeance);
        stmtInsertSeance.setInt(2, idProces);
        stmtInsertSeance.setDate(3, dateSeance);
        stmtInsertSeance.executeUpdate();
    }
}
