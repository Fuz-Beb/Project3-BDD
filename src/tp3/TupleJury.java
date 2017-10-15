package tp3;

/**
 * Permet de représenter un tuple de la table jury.
 */
public class TupleJury
{
    private int nas;
    private String prenom;
    private String nom;
    private char sexe;
    private int age;
    private int proces_id;

    /**
     * Constructeur par défaut
     */
    public TupleJury()
    {
    }

    /**
     * Constructeur de confort
     * 
     * @param nas
     * @param prenom
     * @param nom
     * @param sexe
     * @param age
     * @param proces_id
     */
    public TupleJury(int nas, String prenom, String nom, char sexe, int age, int proces_id)
    {
        super();
        this.nas = nas;
        this.prenom = prenom;
        this.nom = nom;
        this.sexe = sexe;
        this.age = age;
        this.proces_id = proces_id;
    }

    /**
     * @return the nas
     */
    public int getNas()
    {
        return nas;
    }

    /**
     * @param nas
     *            the nas to set
     */
    public void setNas(int nas)
    {
        this.nas = nas;
    }

    /**
     * @return the prenom
     */
    public String getPrenom()
    {
        return prenom;
    }

    /**
     * @param prenom
     *            the prenom to set
     */
    public void setPrenom(String prenom)
    {
        this.prenom = prenom;
    }

    /**
     * @return the nom
     */
    public String getNom()
    {
        return nom;
    }

    /**
     * @param nom
     *            the nom to set
     */
    public void setNom(String nom)
    {
        this.nom = nom;
    }

    /**
     * @return the sexe
     */
    public char getSexe()
    {
        return sexe;
    }

    /**
     * @param sexe
     *            the sexe to set
     */
    public void setSexe(char sexe)
    {
        this.sexe = sexe;
    }

    /**
     * @return the age
     */
    public int getAge()
    {
        return age;
    }

    /**
     * @param age
     *            the age to set
     */
    public void setAge(int age)
    {
        this.age = age;
    }

    /**
     * @return the proces_id
     */
    public int getProces_id()
    {
        return proces_id;
    }

    /**
     * @param proces_id
     *            the proces_id to set
     */
    public void setProces_id(int proces_id)
    {
        this.proces_id = proces_id;
    }
}