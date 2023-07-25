package projet_poo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Le programme doit recevoir en argument le chemin vers un fichier texte décrivant une agglomération.
 * Le programme commence par un menu principal avec quatre choix : 1) résoudre manuellement,
 * 2) résoudre automatiquement, 3) sauvegarder et 4) quitter.
 * 
 * 1) Résolution manuelle : il s'agit de modifier manuellement le placement des écoles dans
 * l'agglomération fournie en paramètre, en respectant à tout moment la contrainte d'accesibilité.
 * Voir {@link #resolutionManuelle()}.
 * 
 * 2) Résolution automatique : le programme va modifier la configuration
 * des écoles dans l'agglomération pour donner une solution optimale au problème en utilisant un
 * algorithme. Le résultat sera affiché.
 * 
 * 3) Sauvegarder : le programme demande un chemin de fichier, absolu ou relatif au répertoire
 * dans lequel il s'exécute. L'agglomération donnée au départ et la configuration des écoles
 * effectuée dans le programme y seront sauvegardées.
 * 
 * 4) Quitter : le programme s'arrêtera sans sauvegarder. Cette action peut également être
 * effectuée à tout moment en appuyant sur Ctrl+C ou Ctrl+D.
 * 
 * @author Zine Eddine BENZENATI, Nadir BORDJAH, Adrian HEOUAIRI (groupe 1)
 */
public class Main {

	/**
	 * Il s'agit de l'objet utilisé tout au long du programme pour lire les entrées au clavier
	 * de l'utilisateur. On utilise sa méthode readLine() via le wrapper {@link #getLigneClavier(String)}.
	 */
	private static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	
	/**
	 * Cette variable va contenir une référence à l'agglomération générée depuis
	 * le fichier donné en argument. Elle contient tout au long du programme la même référence.
	 */
	private static Agglomeration agglomeration;
	
	/**
	 * Cette méthode permet de quitter le programme en fermant d'abord le BufferedReader {@link #br}
	 * utilisé pour lire les saisies de l'utilisateur. Le programme se termine avec le code
	 * de retour donné dans l'argument codeDeRetour.
	 * @param codeDeRetour : le code de retour du programme.
	 */
	private static void quitter(int codeDeRetour) {
		try {
			br.close();
		} catch (IOException e) {
			System.out.println("Erreur à la fermeture du BufferedReader : " + e.getLocalizedMessage());
		}
		
		System.exit(codeDeRetour);
	}
	
	/**
	 * Cette méthode est un "wrapper" pour la méthode readLine() du BufferedReader {@link #br}.
	 * Elle lit et renvoie sous forme de String une ligne saisie au clavier par l'utilisateur.
	 * Elle affiche juste avant le curseur un texte ("prompt") donné en argument.
	 * Elle gère l'exception IOException de readLine() et détecte une fin de fichier (éventuellement
	 * causée par un appui sur Ctrl+D). Une IOException provoque la fin du programme avec le code de
	 * retour 1 (indiquant une erreur). Une fin de fichier provoque la fin du programme avec
	 * le code de retour 0. Cette méthode empêche également de saisir une ligne vide en appuyant
	 * directement sur Entrée : tant qu'une ligne vide est saisie, la saisie recommence (avec
	 * réaffichage du prompt).
	 * @param prompt : le texte à afficher sur la ligne du terminal où l'utilisateur effectue
	 * sa saisie, juste avant le curseur.
	 * @return la chaine de caractères saisie, garantie non-null et non-vide.
	 */
	private static String getLigneClavier(String prompt) {
		String ligne = "";
		
		do {
			System.out.print(prompt);
			try {
				ligne = br.readLine();
			} catch (IOException e) {
				System.out.println("Erreur lors de la lecture du clavier : " + e.getLocalizedMessage());
				quitter(1);
			}
		} while (ligne != null && "".equals(ligne));
		
		if (ligne == null) {
			System.out.println("Fin de fichier rencontrée (Ctrl+D a probablement été saisi). Fin du programme.");
			quitter(0);
		}
		return ligne;
	}
	
	/**
	 * Cette méthode est lancée lorsque l'utilisateur choisit "1) Résolution manuelle" dans le
	 * menu principal. Le but est d'enlever le plus d'écoles possible, en respectant à tout moment
	 * la contrainte d'accessibilité (voir {@link Agglomeration#retirerEcole(String)}).
	 * L'utilisateur peut ajouter une école, enlever une école, revenir au menu principal,
	 * remettre des écoles dans toutes les villes ou afficher les villes voisines de chaque ville.
	 * L'utilisateur ajoute ou retire des écoles en donnant le nom de la ville concernée. Avant chaque
	 * saisie, la configuration actuelle des écoles est affichée.
	 */
	private static void resolutionManuelle() {
		while (true) {
			System.out.println("===== 1 : Ajouter une école | 2 : Retirer une école | 3 : Retour au menu principal");
			System.out.println("===== 4 : Remettre des écoles partout | 5 : Afficher les routes");
			agglomeration.afficherEcoles();
			String choix = getLigneClavier("> ");
			String nomVille;
			switch (choix) {

			case "1":
				System.out.println("Entrez le nom de la ville dans laquelle ajouter une école :");
				nomVille = getLigneClavier(">> ");
				agglomeration.ajouterEcole(nomVille);
				break;

			case "2":
				System.out.println("Entrez le nom de la ville dans laquelle retirer une école :");
				nomVille = getLigneClavier(">> ");
				agglomeration.retirerEcole(nomVille);
				break;

			case "3":
				return;

			case "4":
				agglomeration.solutionNaive();
				break;

			case "5":
				agglomeration.afficherVoisins();
				break;

			default:
				System.out.println("Erreur : Choix invalide, veuillez réessayer");
				break;
			}
		}
	}
	
	/**
	 * Le point d'entrée du programme. D'abord, un objet agglomération est généré à partir du
	 * fichier passé dans le premier argument args[0]. Ensuite, on rentre dans une boucle
	 * qui affiche les choix du menu principal et demande à l'utilisateur de choisir une option.
	 * @param args : les arguments donnés au programme. Le premier argument doit être un nom de fichier
	 * relatif ou absolu décrivant textuellement une agglomération, et éventuellement ses écoles.
	 */
	public static void main(String[] args) {
		if (args.length == 0) {
			System.out.println("Erreur : veuillez donner un fichier en argument");
			quitter(1);
		}
		
		agglomeration = Agglomeration.getInstance();
		
		try {
			agglomeration.init(args[0]);
		} catch (SyntaxeFichierInvalideException e) {
			System.out.println("Erreur dans la syntaxe du fichier : " + e.getLocalizedMessage());
			quitter(1);
		} catch (IOException e) {
			System.out.println("Erreur lors de la lecture du fichier : " + e.getLocalizedMessage());
			quitter(1);
		}
		
		while (true) {
			System.out.println("===== 1 : Résoudre manuellement | 2 : Résoudre automatiquement | 3 : Sauvegarder | 4 : Quitter");
			String choix = getLigneClavier("> ");
			
			switch (choix) {
			
			case "1":
				resolutionManuelle();
				break;
				
			case "2":
				System.out.println("Avant application de l'algorithme :");
				agglomeration.afficherEcoles();
				agglomeration.solutionOptimale();
				System.out.println("Après application de l'algorithme (actuellement) :");
				agglomeration.afficherEcoles();
				break;
			
			case "3":
				System.out.println("Veuillez saisir le nom du fichier dans lequel sauvegarder (relatif ou absolu) :");
				String nomDeFichier = getLigneClavier(">> ");
				agglomeration.sauvegardeFichier(nomDeFichier);
				break;
				
			case "4":
				quitter(0);
				break;
			
			default:
				System.out.println("Erreur : Choix invalide, veuillez réessayer");
				break;
			}
		}
	}

}
