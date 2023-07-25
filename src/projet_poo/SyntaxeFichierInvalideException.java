package projet_poo;

/**
 * Cette exception est lancée à toute erreur de syntaxe détectée lors de la
 * lecture du fichier passé en paramètre au programme. Voir {@link Agglomeration#init(String)}
 * pour la liste des erreurs de syntaxe.
 * 
 * @author Zine Eddine BENZENATI, Nadir BORDJAH, Adrian HEOUAIRI (groupe 1)
 */
public class SyntaxeFichierInvalideException extends Exception {

	/**
	 * Attribut recommandé par l'IDE Eclipse. Non-utilisé ici.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Permet d'instancier cette classe avec un message précis qui sera affiché
	 * à l'utilisateur.
	 * 
	 * @param message : le message à afficher à l'utilisateur.
	 */
	public SyntaxeFichierInvalideException(String message) {
		super(message);
	}
	
}
