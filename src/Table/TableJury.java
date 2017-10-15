package Table;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import tp3.Connexion;

/**
 * Permet d'effectuer les accès à la table jury.
 */
public class TableJury
{
    private static PreparedStatement stmtExiste;
    private static PreparedStatement stmtInsert;
    private static PreparedStatement stmtSelect;
    private static PreparedStatement stmtInsertProcesDansJury;
    private Connexion cx;

    /**
     * Création d'une instance. Des énoncés SQL pour chaque requête sont
     * précompilés.
     * 
     * @param cx
     * @throws SQLException
     */
    public TableJury(Connexion cx) throws SQLException
    {
        this.cx = cx;
        stmtSelect = cx.getConnection().prepareStatement("select * from \"Jury\" where \"Proces_id\" is null");
        stmtInsertProcesDansJury = cx.getConnection()
                .prepareStatement("update \"Jury\" set \"Proces_id\" = ? where \"nas\" = ?");
        stmtExiste = cx.getConnection().prepareStatement("select * from \"Jury\" where \"nas\" = ?");
        stmtInsert = cx.getConnection().prepareStatement(
                "insert into \"Jury\" (\"nas\", \"prenom\", \"nom\", \"sexe\", \"age\", \"Proces_id\") "
                        + "values (?,?,?,?,?,null)");
    }

    /**
     * Retourne la commande associée
     * 
     * @return Connexion
     */
    public Connexion getConnexion()
    {
        return cx;
    }

    /**
     * Vérifie si le jury existe
     * 
     * @param nas
     * @return boolean
     * @throws SQLException
     */
    public boolean existe(int nas) throws SQLException
    {
        stmtExiste.setInt(1, nas);
        ResultSet rset = stmtExiste.executeQuery();
        boolean juryExiste = rset.next();
        rset.close();
        return juryExiste;
    }

    /**
     * Affiche la liste des jurys
     * 
     * @return String
     * @throws SQLException
     */
    public String affichage() throws SQLException
    {
        String result = "\nListe des jurys : \n";
        ResultSet rset = stmtSelect.executeQuery();

        if (rset.next())
        {
            do
            {
                result += rset.getInt(1) + "\t" + rset.getString(2) + "\t" + rset.getString(3) + "\t"
                        + rset.getString(4) + "\t" + rset.getInt(5) + "\t"
                        + (rset.getObject(6) != null ? rset.getInt(6) : "nul");
            }
            while (rset.next());
        }
        rset.close();
        return result;
    }

    /**
     * Ajout d'un nouveau jury dans la base de données
     * 
     * @param nas
     * @param prenom
     * @param nom
     * @param sexe
     * @param age
     * @throws SQLException
     */
    public void ajouter(int nas, String prenom, String nom, String sexe, int age) throws SQLException
    {
        stmtInsert.setInt(1, nas);
        stmtInsert.setString(2, prenom);
        stmtInsert.setString(3, nom);
        stmtInsert.setObject(4, sexe);
        stmtInsert.setInt(5, age);
        stmtInsert.executeUpdate();
    }

    /**
     * Assigner un proces à un jury
     * 
     * @param idProces
     * @param nas
     * @throws SQLException
     */
    public void assignerProces(int nas, int idProces) throws SQLException
    {
        stmtInsertProcesDansJury.setInt(1, idProces);
        stmtInsertProcesDansJury.setInt(2, nas);
        stmtInsertProcesDansJury.executeUpdate();
    }
}