/**
 * 
 */
package Table;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import tp3.Connexion;

/**
 * @author Bebo
 *
 */
public class TablePartie
{
    private PreparedStatement stmtExistePartie;
    private PreparedStatement stmtInsertPartie;
    private Connexion cx;

    /**
     * Constructeur de confort. Creation d'une instance. Précompilation
     * d'énoncés SQL.
     * 
     * @param cx
     * @throws SQLException
     */
    public TablePartie(Connexion cx) throws SQLException
    {
        this.cx = cx;
        stmtExistePartie = cx.getConnection().prepareStatement("select * from \"Partie\" where \"id\" = ?");
        stmtInsertPartie = cx.getConnection().prepareStatement(
                "insert into \"Partie\" (\"id\", \"prenom\", \"nom\", \"Avocat_id\") values (?,?,?,?)");
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
     * Vérifie si un partie existe.
     * 
     * @param idPartie
     * @return boolean
     * @throws SQLException
     */
    public boolean existe(int idPartie) throws SQLException
    {
        stmtExistePartie.setInt(1, idPartie);
        ResultSet rset = stmtExistePartie.executeQuery();
        boolean partieExiste = rset.next();
        rset.close();
        return partieExiste;
    }

    /**
     * Ajout d'un nouveau partie
     * 
     * @param id
     * @param prenom
     * @param nom
     * @param avocat_id
     * @throws SQLException
     */
    public void ajout(int id, String prenom, String nom, int avocat_id) throws SQLException
    {
        /* Ajout du partie. */
        stmtInsertPartie.setInt(1, id);
        stmtInsertPartie.setString(2, prenom);
        stmtInsertPartie.setString(3, nom);
        stmtInsertPartie.setInt(4, avocat_id);
        stmtInsertPartie.executeUpdate();
    }
}
