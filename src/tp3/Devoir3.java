// Travail fait par :
// Pierrick BOBET - 17 131 792
// Rémy BOUTELOUP - 17 132 265

package tp3;

import java.io.*;
import java.util.StringTokenizer;
import java.sql.*;

/**
 * Fichier de base pour le TP2 du cours IFT287
 *
 * <pre>
 * 
 * Vincent Ducharme
 * Universite de Sherbrooke
 * Version 1.0 - 7 juillet 2016
 * IFT287 - Exploitation de BD relationnelles et OO
 * 
 * Ce programme permet d'appeler des transactions d'un systeme
 * de gestion utilisant une base de donnees.
 *
 * Paramètres du programme
 * 0- site du serveur SQL ("local" ou "dinf")
 * 1- nom de la BD
 * 2- user id pour etablir une connexion avec le serveur SQL
 * 3- mot de passe pour le user id
 * 4- fichier de transaction [optionnel]
 *           si non spécifié, les transactions sont lues au
 *           clavier (System.in)
 *
 * Pré-condition
 *   - La base de donnees doit exister
 *
 * Post-condition
 *   - Le programme effectue les mises à jour associees à chaque
 *     transaction
 * </pre>
 */
public class Devoir3
{
    private static Connexion cx;

    // Proces
    private static PreparedStatement stmtExisteProces;
    private static PreparedStatement stmtInsertProces;
    private static PreparedStatement stmtSelectProcesNonTermine;
    private static PreparedStatement stmtTerminerProces;
    private static PreparedStatement stmtVerificationProcesDecision;
    private static PreparedStatement stmtProcesJugeEnCours;
    private static PreparedStatement stmtVerificationProcesDevantJury;
    private static PreparedStatement stmtSelectJugeDansProces;

    // Seance
    private static PreparedStatement stmtExisteSeance;
    private static PreparedStatement stmtInsertSeance;
    private static PreparedStatement stmtExisteProcesDansSeance;
    private static PreparedStatement stmtSupprimerSeancesProcesTermine;
    private static PreparedStatement stmtSeanceNonTerminee;
    private static PreparedStatement stmtSupprimerSeance;

    // Partie
    private static PreparedStatement stmtExistePartie;
    private static PreparedStatement stmtInsertPartie;

    /**
     * La fonction principale
     * 
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception
    {
        if (args.length < 4)
        {
            System.out.println("Usage: java tp2.Devoir2 <serveur> <bd> <user> <password> [<fichier-transactions>]");
            return;
        }

        cx = null;

        try
        {
            cx = new Connexion(args[0], args[1], args[2], args[3]);
            initialiseStatements();
            BufferedReader reader = ouvrirFichier(args);
            String transaction = lireTransaction(reader);
            while (!finTransaction(transaction))
            {
                executerTransaction(transaction);
                transaction = lireTransaction(reader);
            }
        }
        finally
        {
            if (cx != null)
                cx.fermer();
        }
    }

    /**
     * @throws SQLException
     * 
     */
    private static void initialiseStatements() throws SQLException
    {
        // Proces
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

        // Seance
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
        
        // Partie
        stmtExistePartie = cx.getConnection().prepareStatement("select * from \"Partie\" where \"id\" = ?");
        stmtInsertPartie = cx.getConnection().prepareStatement(
                "insert into \"Partie\" (\"id\", \"prenom\", \"nom\", \"Avocat_id\") values (?,?,?,?)");
    }

    /**
     * Decodage et traitement d'une transaction
     */
    static void executerTransaction(String transaction) throws Exception, IFT287Exception
    {
        try
        {
            System.out.print(transaction);
            // Decoupage de la transaction en mots
            StringTokenizer tokenizer = new StringTokenizer(transaction, " ");
            if (tokenizer.hasMoreTokens())
            {
                String command = tokenizer.nextToken();
                // Vous devez remplacer la chaine "commande1" et "commande2" par
                // les commandes de votre programme. Vous pouvez ajouter autant
                // de else if que necessaire. Vous n'avez pas a traiter la
                // commande "quitter".
                if (command.equals("ajouterPartie"))
                {
                    // Lecture des parametres
                    int idPartie = readInt(tokenizer);
                    String prenomPartie = readString(tokenizer);
                    String nomPartie = readString(tokenizer);
                    int idAvocat = readInt(tokenizer);

                    // Appel de la methode qui traite la transaction specifique
                    effectuerAjouterPartie(idPartie, prenomPartie, nomPartie, idAvocat);
                }
                else if (command.equals("creerProces"))
                {
                    // Lecture des parametres
                    int idProces = readInt(tokenizer);
                    int idJuge = readInt(tokenizer);
                    Date dateInitiale = readDate(tokenizer);
                    int devantJury = readInt(tokenizer);
                    int idPartieDefenderesse = readInt(tokenizer);
                    int idPartiePoursuivante = readInt(tokenizer);

                    // Appel de la methode qui traite la transaction specifique
                    effectuerCreerProces(idProces, idJuge, dateInitiale, devantJury, idPartieDefenderesse,
                            idPartiePoursuivante);
                }
                else if (command.equals("ajouterSeance"))
                {
                    // Lecture des parametres
                    int idSeance = readInt(tokenizer);
                    int idProces = readInt(tokenizer);
                    Date dateSeance = readDate(tokenizer);

                    // Appel de la methode qui traite la transaction specifique
                    effectuerAjouterSeance(idSeance, idProces, dateSeance);
                }
                else if (command.equals("supprimerSeance"))
                {
                    // Lecture des parametres
                    int idSeance = readInt(tokenizer);

                    // Appel de la methode qui traite la transaction specifique
                    effectuerSupprimerSeance(idSeance);
                }
                else if (command.equals("terminerProces"))
                {
                    // Lecture des parametres
                    int idProces = readInt(tokenizer);
                    int decisionProces = readInt(tokenizer);

                    // Appel de la methode qui traite la transaction specifique
                    effectuerTerminerProces(idProces, decisionProces);
                }
                else if (command.equals("afficherProces"))
                {
                    // Lecture des parametres
                    int idProces = readInt(tokenizer);

                    // Appel de la methode qui traite la transaction specifique
                    effectuerAfficherProces(idProces);
                }
                else
                {
                    System.out.println(" : Transaction non reconnue");
                }
            }
        }
        catch (Exception e)
        {
            System.out.println(" " + e.toString());
            cx.rollback();
        }
    }

    /**
     * Methode d'affichage des proces
     * 
     * @param idProces
     * @throws SQLException,
     *             IFT287Exception
     */
    private static void effectuerAfficherProces(int idProces) throws SQLException, IFT287Exception
    {
        try
        {
            // Verification du numero du proces
            stmtExisteProces.setInt(1, idProces);
            ResultSet rsetProces = stmtExisteProces.executeQuery();

            if (!rsetProces.next())
            {
                rsetProces.close();
                throw new IFT287Exception("Le proces " + idProces + "n'existe pas");
            }

            System.out.println("\n\nAffichage du proces " + idProces + " :");

            // Affichage des elements du proces conernes
            System.out.println(rsetProces.getInt(1) + "\t" + rsetProces.getInt(2) + "\t" + rsetProces.getString(3)
                    + "\t" + rsetProces.getInt(4) + "\t" + rsetProces.getInt(5) + "\t" + rsetProces.getInt(6) + "\t"
                    + (rsetProces.getObject(7) != null ? rsetProces.getInt(7) : "non termine"));

            rsetProces.close();

            // Récupération des seances liees au proces
            stmtExisteProcesDansSeance.setInt(1, idProces);
            rsetProces = stmtExisteProcesDansSeance.executeQuery();

            if (rsetProces.next())
            {
                System.out.println("\nListe des seances liees au proces " + idProces + " :");

                // Affichage des seances liees au proces
                do
                {
                    System.out.println(
                            rsetProces.getInt(1) + "\t" + rsetProces.getInt(2) + "\t" + rsetProces.getString(3));
                }
                while (rsetProces.next());

                rsetProces.close();
            }
            else
            {
                System.out.println("Aucune seance n'est liee au proces " + idProces);
                rsetProces.close();
            }

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
     * @param idProces
     * @param decisionProces
     * @throws SQLException,
     *             IFT287Exception
     */
    private static void effectuerTerminerProces(int idProces, int decisionProces) throws SQLException, IFT287Exception
    {
        int idJuge = 0;

        try
        {
            // Verification de la valeur de la decision
            if (decisionProces != 0 && decisionProces != 1)
                throw new IFT287Exception("Impossible de terminer le proces " + idProces
                        + "car la valeur de la decision n'est ni 0 ni 1.");

            // Vérification que le proces existe
            stmtExisteProces.setInt(1, idProces);
            ResultSet rsetTermineProces = stmtExisteProces.executeQuery();

            if (!rsetTermineProces.next())
            {
                rsetTermineProces.close();
                throw new IFT287Exception("Le proces " + idProces + "n'existe pas.");
            }
            rsetTermineProces.close();

            // Vérification que le proces a atteint sa date initiale
            stmtSelectProcesNonTermine.setInt(1, idProces);
            rsetTermineProces = stmtSelectProcesNonTermine.executeQuery();

            if (!rsetTermineProces.next())
            {
                rsetTermineProces.close();
                throw new IFT287Exception("Le proces " + idProces + "n'a pas atteint sa date initiale.");
            }

            rsetTermineProces.close();

            // Terminer le proces
            stmtTerminerProces.setInt(1, decisionProces);
            stmtTerminerProces.setInt(2, idProces);
            stmtTerminerProces.executeUpdate();

            // Rendre le juge disponible si il n'a plus de proces en cours
            stmtSelectJugeDansProces.setInt(1, idProces);
            rsetTermineProces = stmtSelectJugeDansProces.executeQuery();

            if (rsetTermineProces.next())
            {
                idJuge = rsetTermineProces.getInt(1);
            }

            rsetTermineProces.close();

            stmtProcesJugeEnCours.setInt(1, idJuge);
            rsetTermineProces = stmtProcesJugeEnCours.executeQuery();

            if (!rsetTermineProces.next())
            {
                stmtChangeDisponibiliteJuge.setBoolean(1, true);
                stmtChangeDisponibiliteJuge.setInt(2, idJuge);
                stmtChangeDisponibiliteJuge.executeUpdate();
            }

            rsetTermineProces.close();

            // Suppresion des seances prevues du proces
            stmtSupprimerSeancesProcesTermine.setInt(1, idProces);
            rsetTermineProces = stmtSupprimerSeancesProcesTermine.executeQuery();

            // Suppression des seances une a une
            while (rsetTermineProces.next())
            {
                effectuerSupprimerSeance(rsetTermineProces.getInt(1));
            }

            rsetTermineProces.close();

            cx.commit();
        }
        catch (Exception e)
        {
            cx.rollback();
            throw e;
        }
    }

    /**
     * Methode de traitement pour effectuerSupprimerSeance
     * 
     * @param idSeance
     * @throws SQLException,
     *             IFT287Exception
     */
    private static void effectuerSupprimerSeance(int idSeance) throws SQLException, IFT287Exception
    {
        try
        {
            // Vérification de l'id de la seance
            stmtExisteSeance.setInt(1, idSeance);
            ResultSet rsetSupprimerSeance = stmtExisteSeance.executeQuery();

            if (!rsetSupprimerSeance.next())
            {
                rsetSupprimerSeance.close();
                throw new IFT287Exception("La seance: " + idSeance + " n'existe pas.");
            }

            rsetSupprimerSeance.close();

            // Vérification que la seance n'est pas encore passee
            stmtSeanceNonTerminee.setInt(1, idSeance);
            rsetSupprimerSeance = stmtSeanceNonTerminee.executeQuery();

            if (rsetSupprimerSeance.next())
            {
                rsetSupprimerSeance.close();
                throw new IFT287Exception("La seance: " + idSeance + " est deja passe.");
            }

            rsetSupprimerSeance.close();

            // Suppression de la seance
            stmtSupprimerSeance.setInt(1, idSeance);
            stmtSupprimerSeance.executeUpdate();

            cx.commit();
        }
        catch (Exception e)
        {
            cx.rollback();
            throw e;
        }
    }

    /**
     * Methode de traitement pour effectuerAjouterSeance
     * 
     * @param idSeance
     * @param idProces
     * @param dateSeance
     * @throws SQLException,
     *             IFT287Exception
     */
    private static void effectuerAjouterSeance(int idSeance, int idProces, Date dateSeance)
            throws SQLException, IFT287Exception
    {
        try
        {
            // Vérification si la seance existe deja
            stmtExisteSeance.setInt(1, idSeance);
            ResultSet rsetAjouterSeance = stmtExisteSeance.executeQuery();

            if (rsetAjouterSeance.next())
            {
                rsetAjouterSeance.close();
                throw new IFT287Exception("La seance existe deja: " + idSeance);
            }
            rsetAjouterSeance.close();

            // Verification si le proces existe
            stmtExisteProces.setInt(1, idProces);
            rsetAjouterSeance = stmtExisteProces.executeQuery();

            if (!rsetAjouterSeance.next())
            {
                rsetAjouterSeance.close();
                throw new IFT287Exception("Le proces " + idProces + " n'existe pas.");
            }
            rsetAjouterSeance.close();

            // Verification si le proces specifie n'est pas termine
            stmtVerificationProcesDecision.setInt(1, idProces);
            rsetAjouterSeance = stmtVerificationProcesDecision.executeQuery();

            if (!rsetAjouterSeance.next())
            {
                rsetAjouterSeance.close();
                throw new IFT287Exception("Le proces " + idProces + " est termine.");
            }
            rsetAjouterSeance.close();

            // Ajout de la seance
            stmtInsertSeance.setInt(1, idSeance);
            stmtInsertSeance.setInt(2, idProces);
            stmtInsertSeance.setDate(3, dateSeance);
            stmtInsertSeance.executeUpdate();

            cx.commit();
        }
        catch (Exception e)
        {
            cx.rollback();
            throw e;
        }
    }

    /**
     * Methode de traitement pour effectuerCreerProces
     * 
     * @param idProces
     * @param idJuge
     * @param dateInitiale
     * @param devantJury
     * @param idPartieDefenderesse
     * @param idPartiePoursuivante
     * @throws SQLException,
     *             IFT287Exception
     */
    private static void effectuerCreerProces(int idProces, int idJuge, Date dateInitiale, int devantJury,
            int idPartieDefenderesse, int idPartiePoursuivante) throws SQLException, IFT287Exception
    {
        try
        {
            if (devantJury != 0 && devantJury != 1)
                throw new IFT287Exception("Impossible de creer le proces " + idProces
                        + "car le champ devantJury ne peut être que 0 ou 1");

            // Verification que le proces n'existe pas deja
            stmtExisteProces.setInt(1, idProces);
            ResultSet rsetCreerProces = stmtExisteProces.executeQuery();

            if (rsetCreerProces.next())
            {
                rsetCreerProces.close();
                throw new IFT287Exception("Proces existe deja: " + idProces);
            }
            rsetCreerProces.close();

            // Vérification que l'id du juge est correcte
            stmtExisteJuge.setInt(1, idJuge);
            rsetCreerProces = stmtExisteJuge.executeQuery();

            if (!rsetCreerProces.next())
            {
                rsetCreerProces.close();
                throw new IFT287Exception("Le juge " + idJuge + "n'existe pas.");
            }
            rsetCreerProces.close();

            // Verification que l'id des parties sont correctes -
            // PartieDefenderesse
            stmtExistePartie.setInt(1, idPartieDefenderesse);
            rsetCreerProces = stmtExistePartie.executeQuery();

            if (!rsetCreerProces.next())
            {
                rsetCreerProces.close();
                throw new IFT287Exception("La partie defenderesse " + idPartieDefenderesse + "n'existe pas.");
            }
            rsetCreerProces.close();

            // Verification que l'id des parties sont correctes -
            // PartiePoursuivante
            stmtExistePartie.setInt(1, idPartiePoursuivante);
            rsetCreerProces = stmtExistePartie.executeQuery();

            if (!rsetCreerProces.next())
            {
                rsetCreerProces.close();
                throw new IFT287Exception("La partie poursuivante " + idPartiePoursuivante + "n'existe pas.");
            }
            rsetCreerProces.close();

            // Ajout du proces
            stmtInsertProces.setInt(1, idProces);
            stmtInsertProces.setInt(2, idJuge);
            stmtInsertProces.setDate(3, dateInitiale);
            stmtInsertProces.setInt(4, devantJury);
            stmtInsertProces.setInt(5, idPartieDefenderesse);
            stmtInsertProces.setInt(6, idPartiePoursuivante);
            stmtInsertProces.executeUpdate();

            // Rendre le juge non disponible
            stmtChangeDisponibiliteJuge.setBoolean(1, false);
            stmtChangeDisponibiliteJuge.setInt(2, idJuge);
            stmtChangeDisponibiliteJuge.executeUpdate();

            cx.commit();
        }
        catch (Exception e)
        {
            cx.rollback();
            throw e;
        }
    }

    /**
     * Methode de traitement pour effectuerAjouterPartie
     * 
     * @param idPartie
     * @param prenomPartie
     * @param nomPartie
     * @param idAvocat
     * @throws SQLException,
     *             IFT287Exception
     */
    private static void effectuerAjouterPartie(int idPartie, String prenomPartie, String nomPartie, int idAvocat)
            throws SQLException, IFT287Exception
    {
        try
        {
            // Verification que la partie mentionnee n'existe pas deja.
            stmtExistePartie.setInt(1, idAvocat);
            ResultSet rsetAjouterPartie = stmtExistePartie.executeQuery();

            if (rsetAjouterPartie.next())
            {
                rsetAjouterPartie.close();
                throw new IFT287Exception("Partie existe deja: " + idPartie);
            }
            rsetAjouterPartie.close();

            // Verification que l'id de l'avocat est correcte
            stmtExisteAvocat.setInt(1, idAvocat);
            rsetAjouterPartie = stmtExisteAvocat.executeQuery();

            if (!rsetAjouterPartie.next())
            {
                rsetAjouterPartie.close();
                throw new IFT287Exception("L'avocat " + idAvocat + "n'existe pas.");
            }
            rsetAjouterPartie.close();

            // Ajout du partie
            stmtInsertPartie.setInt(1, idPartie);
            stmtInsertPartie.setString(2, prenomPartie);
            stmtInsertPartie.setString(3, nomPartie);
            stmtInsertPartie.setInt(4, idAvocat);
            stmtInsertPartie.executeUpdate();

            cx.commit();
        }
        catch (Exception e)
        {
            cx.rollback();
            throw e;
        }
    }

    // ****************************************************************
    // * Les methodes suivantes n'ont pas besoin d'etre modifiees *
    // ****************************************************************

    /**
     * Ouvre le fichier de transaction, ou lit à partir de System.in
     * 
     * @param args
     * @return BufferedReader
     * @throws FileNotFoundException
     */
    public static BufferedReader ouvrirFichier(String[] args) throws FileNotFoundException
    {
        if (args.length < 5)
            // Lecture au clavier
            return new BufferedReader(new InputStreamReader(System.in));
        else
            // Lecture dans le fichier passe en parametre
            return new BufferedReader(new InputStreamReader(new FileInputStream(args[4])));
    }

    /**
     * Lecture d'une transaction
     */
    static String lireTransaction(BufferedReader reader) throws IOException
    {
        return reader.readLine();
    }

    /**
     * Verifie si la fin du traitement des transactions est atteinte.
     */
    static boolean finTransaction(String transaction)
    {
        // fin de fichier atteinte
        return (transaction == null || transaction.equals("quitter"));
    }

    /** Lecture d'une chaine de caracteres de la transaction entree a l'ecran */
    static String readString(StringTokenizer tokenizer) throws Exception
    {
        if (tokenizer.hasMoreElements())
            return tokenizer.nextToken();
        else
            throw new Exception("Autre parametre attendu");
    }

    /**
     * Lecture d'un int java de la transaction entree a l'ecran
     */
    static int readInt(StringTokenizer tokenizer) throws Exception
    {
        if (tokenizer.hasMoreElements())
        {
            String token = tokenizer.nextToken();
            try
            {
                return Integer.valueOf(token).intValue();
            }
            catch (NumberFormatException e)
            {
                throw new Exception("Nombre attendu a la place de \"" + token + "\"");
            }
        }
        else
            throw new Exception("Autre parametre attendu");
    }

    static Date readDate(StringTokenizer tokenizer) throws Exception
    {
        if (tokenizer.hasMoreElements())
        {
            String token = tokenizer.nextToken();
            try
            {
                return Date.valueOf(token);
            }
            catch (IllegalArgumentException e)
            {
                throw new Exception("Date dans un format invalide - \"" + token + "\"");
            }
        }
        else
            throw new Exception("Autre parametre attendu");
    }

}