package tp3;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Permet d'effectuer les accès à la table juge.
 */
public class TableJuge
{
    private static PreparedStatement stmtExiste;
    private static PreparedStatement stmtInsert;
    private static PreparedStatement stmtSelect;
    private static PreparedStatement stmtRetirer;
    private static PreparedStatement stmtChangeDisponibilite;
    private Connexion cx;

    /**
     * Création d'une instance. Des énoncés SQL pour chaque requête sont
     * précompilés.
     * 
     * @param cx
     * @throws SQLException
     */
    public TableJuge(Connexion cx) throws SQLException
    {
        this.cx = cx;
        stmtSelect = cx.getConnection().prepareStatement("select * from \"Juge\" where \"disponible\" = true");
        stmtExiste = cx.getConnection().prepareStatement("select * from \"Juge\" where \"id\" = ?");
        stmtInsert = cx.getConnection()
                .prepareStatement("insert into \"Juge\" (\"id\", \"prenom\", \"nom\", \"age\") values (?,?,?,?)");
        stmtRetirer = cx.getConnection().prepareStatement(
                "update \"Juge\" set \"quitterJustice\" = true, \"disponible\" = false where \"id\" = ?");
        stmtChangeDisponibilite = cx.getConnection()
                .prepareStatement("update \"Juge\" set \"disponible\" = ? where \"id\" = ?");
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
     * Vérifie si le juge existe
     * 
     * @param idJuge
     * @return boolean
     * @throws SQLException
     */
    public boolean existe(int idJuge) throws SQLException
    {
        stmtExiste.setInt(1, idJuge);
        ResultSet rset = stmtExiste.executeQuery();
        boolean jugeExiste = rset.next();
        rset.close();
        return jugeExiste;
    }

    /**
     * Afficher la liste des juges actifs et disponibles
     * 
     * @return String
     * @throws SQLException
     */
    public String affichage() throws SQLException
    {
        String result = "\nListe des juges actifs et disponibles :\n";
        ResultSet rset = stmtSelect.executeQuery();

        if (rset.next())
        {
            do
            {
                result += rset.getInt(1) + "\t" + rset.getString(2) + "\t" + rset.getString(3) + "\t" + rset.getInt(4)
                        + "\t" + rset.getBoolean(5) + "\t" + rset.getBoolean(6) + "\n";
            }
            while (rset.next());
        }
        rset.close();
        return result;
    }

    /**
     * Ajout d'un nouveau juge dans la base de données
     * 
     * @param id
     * @param prenom
     * @param nom
     * @param age
     * @throws SQLException
     */
    public void ajouter(int id, String prenom, String nom, int age) throws SQLException
    {
        stmtInsert.setInt(1, id);
        stmtInsert.setString(2, prenom);
        stmtInsert.setString(3, nom);
        stmtInsert.setInt(4, age);
        stmtInsert.executeUpdate();
    }

    /**
     * Retirer le juge de la base de données
     * 
     * @param idJuge
     * @throws SQLException
     */
    public void retirer(int idJuge) throws SQLException
    {
        stmtRetirer.setInt(1, idJuge);
        stmtRetirer.executeUpdate();
    }

    /**
     * Changer la disponibilite d'un juge
     * 
     * @param idJuge
     * @param disponibilite
     * @throws SQLException
     */
    public void changerDisponibilite(boolean disponibilite, int idJuge) throws SQLException
    {
        stmtChangeDisponibilite.setBoolean(1, disponibilite);
        stmtChangeDisponibilite.setInt(2, idJuge);
        stmtChangeDisponibilite.executeUpdate();
    }
}