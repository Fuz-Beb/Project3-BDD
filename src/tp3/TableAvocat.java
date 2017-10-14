package tp3;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Permet d'effectuer les accès à la table avocat.
 */
public class TableAvocat
{
    private static PreparedStatement stmtExiste;
    private static PreparedStatement stmtInsert;
    private Connexion cx;

    /**
     * Création d'une instance. Des énoncés SQL pour chaque requête sont
     * précompilés.
     * 
     * @param cx
     * @throws SQLException
     */
    public TableAvocat(Connexion cx) throws SQLException
    {
        this.cx = cx;
        stmtExiste = cx.getConnection().prepareStatement("select * from \"Avocat\" where \"id\" = ?");
        stmtInsert = cx.getConnection()
                .prepareStatement("insert into \"Avocat\" (id, prenom, nom, type) values (?,?,?,?)");
    }

    /**
     * Retourner la connexion associée
     * 
     * @return Connexion
     */
    public Connexion getConnexion()
    {
        return cx;
    }

    /**
     * Vérifie si l'avocat existe
     * 
     * @param idAvocat
     * @return boolean
     * @throws SQLException
     */
    public boolean existe(int idAvocat) throws SQLException
    {
        stmtExiste.setInt(1, idAvocat);
        ResultSet rset = stmtExiste.executeQuery();
        boolean avocatExiste = rset.next();
        rset.close();
        return avocatExiste;
    }

    /**
     * Ajout d'un nouvelle avocat dans la base de données
     * 
     * @param id
     * @param prenom
     * @param nom
     * @param type
     * @throws SQLException
     */
    public void ajouter(int id, String prenom, String nom, int type) throws SQLException
    {
        stmtInsert.setInt(1, id);
        stmtInsert.setString(2, prenom);
        stmtInsert.setString(3, nom);
        stmtInsert.setInt(4, type);
        stmtInsert.executeQuery();
    }
}