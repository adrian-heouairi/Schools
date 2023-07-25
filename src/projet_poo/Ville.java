package projet_poo;

/**
 * Représente une ville, qui correspond à un sommet d'un graphe qui modélise une agglomération.
 * 
 * @author Zine Eddine BENZENATI, Nadir BORDJAH, Adrian HEOUAIRI (groupe 1)
 */
public class Ville {

	/**
	 * Le nom de la ville.
	 */
	private String nom;
	/**
	 * Booléen qui indique si la ville possède une école.
	 */
	private boolean possedeEcole = false;

	/**
	 * Crée une ville de nom nom.
	 * @param nom : le nom de la ville.
	 */
	public Ville(String nom) {
		this.nom = nom;
	}

	/**
	 * @return le nom de la ville.
	 */
	@Override
	public String toString() {
		return nom;
	}

	/**
	 * @return le nom de la ville.
	 */
	public String getNom() {
		return nom;
	}

	/**
	 * @return true si la ville possède une école, false sinon.
	 */
	public boolean getPossedeEcole() {
		return possedeEcole;
	}

	/**
	 * Permet de mettre ou retirer une école dans une ville.
	 * @param possedeEcole : true pour mettre une école, false pour l'enlever.
	 */
	public void setPossedeEcole(boolean possedeEcole) {
		this.possedeEcole = possedeEcole;
	}
}
