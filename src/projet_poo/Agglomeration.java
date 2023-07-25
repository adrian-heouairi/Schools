package projet_poo;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.util.StringTokenizer;

/**
 * Ceci est une classe singleton (une seule instance de cette classe peut exister).
 * Un objet agglomération représente un ensemble de {@link Ville}s reliées entre
 * elles par des routes. Ceci correspond à un graphe simple non-orienté dans
 * lequel les sommets sont des villes et les arêtes sont des routes. On
 * représente ce graphe par une liste d'objets Ville et une matrice d'adjacence
 * de booléens.
 * 
 * @author Zine Eddine BENZENATI, Nadir BORDJAH, Adrian HEOUAIRI (groupe 1)
 */
public final class Agglomeration {

	/**
	 * Attribut d'une classe singleton : unique instance de la classe Agglomeration.
	 */
	private static Agglomeration INSTANCE;

	/**
	 * Cette liste contient l'ensemble des villes de l'agglomération.
	 */
	private ArrayList<Ville> villes;

	/**
	 * Contient le nombre de villes de l'agglomération, qui est fixé à la création
	 * et ne change plus. Il s'agit d'un alias car cette valeur peut également être
	 * obtenue avec villes.size() ou matriceAdjacence.length.
	 */
	private int nombreDeVilles;

	/**
	 * La matrice d'adjacence représentant les routes entre les villes de
	 * l'agglomération par des booléens. Si les villes d'indice i et j dans
	 * l'attribut villes sont voisines, on a matriceAdjacence[i][j] et
	 * matriceAdjacence[j][i] à true. Sinon, ces deux valeurs sont à false.
	 */
	private boolean[][] matriceAdjacence;

	/**
	 * Constructeur privé pour que cette classe soit un singleton.
	 * Construit une agglomération avec 0 villes. La matrice d'adjacence
	 * n'est pas créée ici car elle doit avoir la taille du nombre de villes final.
	 */
	private Agglomeration() {
		villes = new ArrayList<Ville>(0);
		nombreDeVilles = 0;
	}

	/**
	 * Attribut d'une classe singleton : charge l'unique instance de la
	 * classe en mémoire si ce n'est pas déja fait, et la retourne.
	 * 
	 * @return : l'unique instance de Agglomeration.
	 */
	public static Agglomeration getInstance() {
		if (INSTANCE == null)
			INSTANCE = new Agglomeration();

		return INSTANCE;
	}

	/**
	 * Initialisation de cette agglomération en ajoutant les villes, routes et écoles présentes
	 * dans le fichier donné en argument. Si les écoles ne satisfont pas la contrainte d'accessibilité,
	 * la {@link #solutionNaive()} est utilisée. Crée d'abord les villes, puis la matrice d'adjacence à
	 * la bonne taille, puis ajoute les routes dans la matrice, et enfin ajoute les écoles et le vérifie.
	 * Toute erreur de syntaxe est repérée et lève l'exception {@link SyntaxeFichierInvalideException}
	 * dont le message explique l'erreur en détail.
	 * 
	 * Liste des erreurs de syntaxe :
	 * - aucune ville présente ;
	 * - ligne vide ou ne correspondant pas exactement à "ville(...)", "route(...,...)"
	 * ou "ecole(...)" avec "..." non-vide ;
	 * - ville après une route ou une école ;
	 * - route après une école ;
	 * - route d'une ville vers elle-même ;
	 * - route ou école avec une ville qui n'existe pas ;
	 * - route ou école présente en double.
	 * 
	 * @param cheminDuFichier : chemin relatif ou absolu du fichier décrivant une agglomération.
	 * 
	 * @throws IOException : lancée pour toute erreur d'accès ou de lecture sur le fichier cheminDuFichier.
	 * @throws SyntaxeFichierInvalideException : lancée pour toute erreur de syntaxe dans le fichier
	 * cheminDuFichier. Possèdera un message qui décrit l'erreur en détail.
	 */
	public void init(String cheminDuFichier) throws IOException, SyntaxeFichierInvalideException {
		File fichier = new File(cheminDuFichier);
		FileReader fr = new FileReader(fichier);
		BufferedReader contenu = new BufferedReader(fr);
		
		boolean ecolesDansFichier = false;
		boolean stop = false;
		do {
			String line = contenu.readLine();
			StringTokenizer st;
			
			if (line == null) {
				stop = true;
			}
			else if (line.matches("ville\\(.+\\)\\.?")) {
				if (matriceAdjacence != null) {
					contenu.close();
					throw new SyntaxeFichierInvalideException("Il ne doit pas y avoir de ville après une route");
				}
				else if (ecolesDansFichier) {
					contenu.close();
					throw new SyntaxeFichierInvalideException("Il ne doit pas y avoir de ville après une école");
				}
				
				line = line.split("ville")[1];
				st = new StringTokenizer(line, "()");
				line = st.nextToken();
				villes.add(new Ville(line));
			}
			else if (line.matches("route\\(.+,.+\\)\\.?")) {
				if (ecolesDansFichier) {
					contenu.close();
					throw new SyntaxeFichierInvalideException("Il ne doit pas y avoir de route après une école");
				}
				
				if (matriceAdjacence == null)
					matriceAdjacence = new boolean[villes.size()][villes.size()];
				st = new StringTokenizer(line.split("route")[1], "(,)");
				String nomVille1 = "", nomVille2 = "";
				try {
					nomVille1 = st.nextToken();
					nomVille2 = st.nextToken();
				} catch (NoSuchElementException e) {
					contenu.close();
					throw new SyntaxeFichierInvalideException("Il faut donner deux villes pour une route : "
					+ line);
				}
				
				if (nomVille1.equals(nomVille2)) {
					contenu.close();
					throw new SyntaxeFichierInvalideException("Une route ne doit pas être d'une ville"
					+ " vers elle-même : " + line);
				}
				
				boolean retourAjoutRoute = ajouterRoute(nomVille1, nomVille2);
				if (!retourAjoutRoute) {
					contenu.close();
					throw new SyntaxeFichierInvalideException("Route invalide : " + line);
				}
			}
			else if (line.matches("ecole\\(.+\\)\\.?")) {
				ecolesDansFichier = true;
				line = line.split("ecole")[1];
				st = new StringTokenizer(line, "()");
				if (!ajouterEcole(st.nextToken())) {
					contenu.close();
					throw new SyntaxeFichierInvalideException("École invalide : " + line);
				}
			}
			else {
				if ("".equals(line)) {
					contenu.close();
					throw new SyntaxeFichierInvalideException("Il ne doit pas y avoir de ligne vide");
				} else {
					contenu.close();
					throw new SyntaxeFichierInvalideException("Ligne inattendue : " + line);
				}
			}
		} while (!stop);

		contenu.close();

		nombreDeVilles = villes.size();
		if (nombreDeVilles == 0) {
			throw new SyntaxeFichierInvalideException("Il faut au moins une ville");
		}

		if (matriceAdjacence == null) // S'il n'y a aucune route dans le fichier
			matriceAdjacence = new boolean[nombreDeVilles][nombreDeVilles];

		for (int i = 0; i < nombreDeVilles; i++) { // Vérifie la contrainte d'accessibilité
			if (villes.get(i).getPossedeEcole())
				continue;

			boolean villeRespecteAccessibilite = false;
			for (int j = 0; j < nombreDeVilles; j++)
				if (matriceAdjacence[i][j])
					if (villes.get(j).getPossedeEcole()) {
						villeRespecteAccessibilite = true;
						break;
					}

			if (!villeRespecteAccessibilite) {
				System.out.println("La configuration des écoles est invalide. Utilisation de la solution naïve.");
				solutionNaive();
				break;
			}
		}
	}

	/**
	 * Connecte par une route, si elles existent toutes les deux et ne sont pas déjà
	 * connectées, les villes de nom nomVille1 et nomVille2 dans cette agglomération.
	 * 
	 * @param nomVille1 : le nom de la première ville.
	 * @param nomVille2 : le nom de la deuxième ville.
	 * 
	 * @return true en cas de succès, et false en cas d'échec, c'est-à-dire si la route
	 * existe déjà ou qu'une des deux villes n'existe pas.
	 */
	public boolean ajouterRoute(String nomVille1, String nomVille2) {
		int indice1 = getIndiceVille(nomVille1);
		int indice2 = getIndiceVille(nomVille2);
		if (indice1 < 0 || indice2 < 0)
			return false;

		if (!matriceAdjacence[indice1][indice2]) {
			matriceAdjacence[indice1][indice2] = true;
			matriceAdjacence[indice2][indice1] = true;
			return true;
		} else {
			System.out.println("Erreur : cette route existe déjà");
			return false;
		}
	}

	/**
	 * Ajoute une école dans la ville de nom nomVille, si elle existe et ne contient
	 * pas d'école.
	 * 
	 * @param nomVille : le nom de la ville.
	 * 
	 * @return true si l'école est ajoutée, et false si elle était déjà présente.
	 */
	public boolean ajouterEcole(String nomVille) {
		Ville ville = getVille(nomVille);
		if (ville == null)
			return false;

		if (!ville.getPossedeEcole()) {
			ville.setPossedeEcole(true);
			return true;
		} else {
			System.out.println("Erreur : cette ville possède déja une école");
			return false;
		}
	}

	/**
	 * Retire l'école de la ville nommée nomVille si elle existe et que cela ne
	 * viole pas la contrainte d'accessibilité : il faut que la ville aie parmi ses
	 * voisins une ville avec une école. Il faut également que sans l'école de
	 * nomVille, chaque voisin aie soit sa propre école, soit une école parmi ses
	 * voisins.
	 * 
	 * @param nomVille : le nom de la ville.
	 */
	public void retirerEcole(String nomVille) {
		Ville ville = getVille(nomVille);
		if (ville == null)
			return;
		if (!ville.getPossedeEcole()) {
			System.out.println("Erreur : cette ville est déjà sans école");
			return;
		}

		int indice = getIndiceVille(nomVille);

		boolean voisinPossedeEcole = false;
		for (int i = 0; i < nombreDeVilles; i++)
			if (matriceAdjacence[indice][i] && villes.get(i).getPossedeEcole()) {
				voisinPossedeEcole = true;
				break;
			}
		if (!voisinPossedeEcole) {
			System.out.println("Erreur : la ville " + nomVille + " n'a aucun voisin qui possède une école");
			return;
		}

		boolean tousLesVoisinsAurontAccessibiliteApres = true;
		ville.setPossedeEcole(false); // On enlève l'école temporairement pour tester
		for (int i = 0; i < nombreDeVilles; i++) {
			if (matriceAdjacence[indice][i]) {
				if (villes.get(i).getPossedeEcole())
					continue;

				boolean voisinDeVoisinPossedeEcole = false;
				for (int j = 0; j < nombreDeVilles; j++)
					if (matriceAdjacence[i][j] && villes.get(j).getPossedeEcole()) {
						voisinDeVoisinPossedeEcole = true;
						break;
					}
				if (!voisinDeVoisinPossedeEcole) {
					tousLesVoisinsAurontAccessibiliteApres = false;
					System.out.println("Erreur : la ville " + villes.get(i).getNom()
							+ " n'aurait plus d'école sans l'école de " + ville.getNom());
				}
			}
		}
		if (!tousLesVoisinsAurontAccessibiliteApres)
			ville.setPossedeEcole(true);
	}

	/**
	 * Affiche sur une ligne le nom des villes qui possèdent une école.
	 */
	public void afficherEcoles() {
		System.out.print("Villes qui possèdent des écoles | ");
		for (Ville ville : villes)
			if (ville.getPossedeEcole())
				System.out.print(ville.getNom() + " | ");
		System.out.println("");
	}

	/**
	 * Remet une école dans chaque ville.
	 */
	public void solutionNaive() {
		for (Ville ville : villes)
			ville.setPossedeEcole(true);
	}

	/**
	 * Affiche pour chaque ville son nom, suivi des noms des villes auxquelles elle
	 * est connectée par une route, tout ceci sur une seule ligne.
	 */
	public void afficherVoisins() {
		System.out.print("Voisins de chaque ville | ");
		for (int i = 0; i < nombreDeVilles; i++) {
			System.out.print(villes.get(i).getNom() + " : ");
			for (int j = 0; j < nombreDeVilles; j++)
				if (matriceAdjacence[i][j])
					System.out.print(villes.get(j).getNom() + ", ");
			System.out.print("| ");
		}
		System.out.println("");
	}

	/**
	 * Donne l'indice (le numéro) d'une ville dans la liste des villes de
	 * l'agglomération.
	 * 
	 * @param nomVille : le nom de la ville.
	 * @return l'indice de la ville, ou -1 si la ville nommée nomVille n'existe pas.
	 */
	private int getIndiceVille(String nomVille) {
		Ville ville = getVille(nomVille);
		if (ville == null)
			return -1;
		return villes.indexOf(ville);
	}

	/**
	 * Retourne la ville nommée nomVille dans les villes de cette agglomération.
	 * Affiche une erreur et retourne null si la ville n'existe pas.
	 * 
	 * @param nomVille : le nom de la ville.
	 * @return la ville nommée nomVille, ou null si elle n'existe pas.
	 */
	private Ville getVille(String nomVille) {
		for (Ville ville : villes)
			if (ville.getNom().equals(nomVille))
				return ville;
		System.out.println("Erreur : la ville " + nomVille + " n'existe pas");
		return null;
	}

	/**
	 * Permet de débugger le programme en affichant les données de cette agglomération :
	 * liste des villes, matrice d'adjacence et écoles.
	 */
	public void debug() {
		System.out.println("Villes :");
		for (int i = 0; i < villes.size(); i++) {
			System.out.println("Ville " + i + " : " + villes.get(i).getNom());
		}

		System.out.println("\n\n\nMatrice d'adjacence : \n");
		for (int i = 0; i < villes.size(); i++) {
			for (int j = 0; j < villes.size(); j++) {
				if (matriceAdjacence[i][j])
					System.out.print(1 + " ");
				else
					System.out.print(0 + " ");
				if (j == villes.size() - 1) {
					System.out.print("\n\n");
				}
			}
		}

		afficherEcoles();
	}

	/**
	 * Modifie la configuration des écoles de l'agglomération pour donner une des solutions
	 * optimales au problème.
	 * 
	 * On commence par enlever toutes les écoles. Pour chaque ville, on calcule son nombre
	 * de voisins (degré). On met une école dans toutes les villes de degré 0. Ensuite, tant
	 * que toutes les villes ne remplissent pas la contrainte d'accessibilité, on trouve parmi
	 * les villes de plus grand degré ne possédant pas d'école celle pour laquelle lui ajouter
	 * une école va faire respecter la contrainte d'accessibilité au plus grand nombre
	 * de villes voisines.
	 */
	public void solutionOptimale() {
		for (int i = 0; i < villes.size(); i++) { // Enlève toutes les écoles
			villes.get(i).setPossedeEcole(false);
		}

		/**
		 * Classe interne qui stocke pour chaque ville toute les informations
		 * nécessaires au fonctionnement de l'algorithme.
		 */
		class Criteres {
			/**
			 * Nombre de villes voisines de la ville.
			 */
			private int degre;
			
			/**
			 * true si cette ville est voisine d'une ville qui possède une école, false sinon.
			 */
			private boolean couverte;
			
			/**
			 * true si cette ville possède une école, false sinon.
			 */
			private boolean colore = false;

			public Criteres(int degre) {
				setDegre(degre);
				this.setColore(false);
				this.setCouverte(false);
			}

			public int getDegre() {
				return degre;
			}

			public void setDegre(int degre) {
				this.degre = degre;
			};

			public boolean isCouverte() {
				return couverte;
			}

			public void setCouverte(boolean couverte) {
				this.couverte = couverte;
			}

			public boolean isColore() {
				return colore;
			}

			public void setColore(boolean colore) {
				this.colore = colore;
			}

		}

		// Crée la liste des critères pour chaque ville (position du critère = position
		// de la ville dans la liste des villes)
		ArrayList<Criteres> criteres = new ArrayList<Criteres>(0);

		// Liste des numéros de villes candidates pour avoir une école
		ArrayList<Integer> candidates = new ArrayList<Integer>(0);

		// Attribution du degré (nombre de villes relié avec une route) de chaque ville
		for (int i = 0; i < nombreDeVilles; i++) {
			int res = 0;
			for (int j = 0; j < nombreDeVilles; j++) {
				if (matriceAdjacence[i][j])
					res++;
			}
			Criteres c = new Criteres(res);
			if (res == 0) {
				c.setColore(true);
				c.setCouverte(true);
				ajouterEcole(villes.get(i).getNom());
			}
			criteres.add(c);
		}

		// Signal d'arrêt de la boucle
		boolean stop = false;

		// Partie concrète de l'algorithme
		while (!stop) {

			int toColor = 0; // Le numéro de la ville dans laquelle on va construire une école (la colorer),
							 // initialement la ville 0
			candidates.add(toColor);
			// Parcourir la liste pour trouver les villes avec le plus de villes voisines
			// (plus grand degré)
			for (int i = 1; i < criteres.size(); i++) {
				// Si la ville est déjà colorée, on passe
				if (!criteres.get(i).isColore()) {
					// 2 valeurs max donc apparition d'une autre candidate
					if (criteres.get(i).getDegre() == criteres.get(toColor).getDegre()) {
						candidates.add(i);
					}

					// Si le max change, la liste est réinitialisée
					if (criteres.get(i).getDegre() > criteres.get(toColor).getDegre()) {
						toColor = i;
						candidates.clear();
						candidates.add(i);
					}
				}
			}

			// Choisir la candidate qui pourrait couvrir le plus de villes non couvertes
			int score = 0;
			// À cause des points négatifs, il serait logique de mettre une marge (pire des
			// cas : lié a n villes colorées)
			int max = (-1) * villes.size();
			toColor = 0;
			for (int i = 0; i < candidates.size(); i++) {
				for (int j = 0; j < nombreDeVilles; j++) {
					if (matriceAdjacence[candidates.get(i)][j]) {
						if (!criteres.get(j).isCouverte())
							score++;
						if (criteres.get(j).isColore())
							score--; // Être relié à une ville colorée donne moins de crédibilité
					}
				}
				if (score > max && !criteres.get(candidates.get(i)).isColore()) {
					max = score;
					toColor = i;
				}
				score = 0;
			}

			// Construire une école dans cette ville

			ajouterEcole(villes.get(candidates.get(toColor)).getNom());
			criteres.get(candidates.get(toColor)).setColore(true);
			criteres.get(candidates.get(toColor)).setCouverte(true);

			// Marquer les villes voisines de la ville colorée comme couvertes
			for (int i = 0; i < nombreDeVilles; i++) {
				if (matriceAdjacence[candidates.get(toColor)][i]) {
					criteres.get(i).setCouverte(true);
				}
			}
			// Vider la liste des candidates
			candidates.clear();
			// Si les villes sont toutes couvertes, retourner le résultat
			stop = true;
			for (int i = 0; i < criteres.size(); i++) {
				if (!criteres.get(i).isCouverte()) {
					stop = false;
				}
			}
		}

	}

	/**
	 * Sauvegarde les villes, routes et écoles de l'agglomération actuelle dans le fichier
	 * donné en argument. Crée le fichier si nécessaire, et le vide avant d'écrire dedans
	 * s'il était déjà présent. Si une erreur est rencontrée lors de la création ou de
	 * l'écriture, celle-ci sera affichée.
	 * 
	 * @param nomDeFichier : le chemin du fichier, relatif ou absolu, dans lequel écrire.
	 */
	public void sauvegardeFichier(String nomDeFichier) {
		try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(nomDeFichier)))) {
			for (Ville ville : villes) {
				pw.print("ville(");
				pw.print(ville.getNom());
				pw.println(")");
			}

			for (int i = 0; i < nombreDeVilles - 1; i++)
				for (int j = i + 1; j < nombreDeVilles; j++)
					if (matriceAdjacence[i][j]) {
						pw.print("route(");
						pw.print(villes.get(i).getNom());
						pw.print(",");
						pw.print(villes.get(j).getNom());
						pw.println(")");
					}

			for (Ville ville : villes)
				if (ville.getPossedeEcole()) {
					pw.print("ecole(");
					pw.print(ville.getNom());
					pw.println(")");
				}
		} catch (IOException e) {
			System.out.println("Erreur lors de la sauvegarde : " + e.getLocalizedMessage());
		}
	}
}
