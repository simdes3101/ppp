import extensions.CSVFile;
import extensions.File;
class ChatVoyage extends Program{
    final CSVFile file = loadCSV("../ressources/data.csv");
    final CSVFile villes = loadCSV("../ressources/villes.csv");

    /*
    ----------------Fonctions moteurs du jeu----------------------
    */

    //Permet la création d'un joueur
    Joueur creerJoueur(String nom){
        Joueur j = new Joueur();
        j.nom = nom;
        j.score = 0;
        j.avancement = 0;
        j.tentatives = 0;
        j.difficulte = 0;
        return j;
    }

    void testCreerJoueur(){
        Joueur j1 = new Joueur();
        j1.nom = "test";
        Joueur j2 = creerJoueur("test");
        assertEquals(j1.nom, j2.nom);
    }

    //Permet la création d'une question
    Question creerQuestion(String question, String reponse, String choix1, String choix2, String choix3){
        Question q = new Question();
        q.q = question;
        q.reponse = reponse;
        q.choix1 = choix1;
        q.choix2 = choix2;
        q.choix3 = choix3;
        return q;
    }

    void testCreerQuestion(){
        Question q1 = new Question();
        q1.q = "Pourquoi ?";
        q1.reponse = "parce que";
        Question q2 = creerQuestion("Pourquoi ?", "parce que", "", "", "");
        assertEquals(q1.q, q2.q);
        assertEquals(q1.reponse, q2.reponse);
    }

    //Genere une question vide pour le monde
    Question GenererQuestion(Joueur j, int[] historique){
        Question q = creerQuestion("", "", "", "", "");
            int random = (int) (random()*2);
            if(random == 0){
                q = QuestionCapitale(j, historique);
            } else if(random ==1){
                q = QuestionPays(j, historique);
            }
        return q;
    }
    
    //Genere une question vide pour la France
    Question genererQuestionFrance(Joueur j){
        Question q = creerQuestion("","","","","");
        q = QuestionVille();
        return q;
    }

    //Genere une question où il faut trouver la capitale qui a comme pays
    Question QuestionCapitale(Joueur j, int[] historique) {
        Question q;
        int nb = 0;
        while (QuestionDejaPosee(historique, nb)){
            if(j.difficulte == 1){
                nb = (int) (random()*37) +1;
            } else if (j.difficulte == 2){
                nb = (int) (random()*30) +39;
            } else if (j.difficulte == 3){
                nb = (int) (random()*26) +68;
            } else if (j.difficulte == 4){
                nb = (int) (random()*rowCount(file)-1) +1;
            }
        }
        historique[j.avancement] = nb;
        String question = "Quelle est la capitale de " + getCell(file, nb, 0) + " ?";
        int[] faussesreponses = new int[]{-1, -1, -1};
        int indice = 0;
        while(faussesreponses[2] == -1){
            int random = (int) (random()*rowCount(file)-1) +1;
            if(random != nb && random != faussesreponses[0] && random != faussesreponses[1] && random != faussesreponses[2]){
                faussesreponses[indice] = random;
                indice = indice +1;
            }
        }
        q = creerQuestion(question, getCell(file, nb, 1), getCell(file, faussesreponses[0], 1), getCell(file, faussesreponses[1], 1), getCell(file, faussesreponses[2], 1));
        return q;
    }

    //Genere une question où il faut trouver le pays qui a comme capitale
    Question QuestionPays(Joueur j, int[] historique){
        Question q;
        int nb = 0;
        while (QuestionDejaPosee(historique, nb)){
            if(j.difficulte == 1){
                nb = (int) (random()*37) +1;
            } else if (j.difficulte == 2){
                nb = (int) (random()*30) +39;
            } else if (j.difficulte == 3){
                nb = (int) (random()*26) +68;
            } else if (j.difficulte == 4){
                nb = (int) (random()*rowCount(file)-1) +1;
            }
        }
        historique[j.avancement] = nb;
        String question = "Quel pays a pour capitale " + getCell(file, nb, 1) + " ?";
        int[] faussesreponses = new int[]{-1, -1, -1};
        int indice = 0;
        while(faussesreponses[2] == -1){
            int random = (int) (random()*rowCount(file)-1) +1;
            if(random != nb && random != faussesreponses[0] && random != faussesreponses[1] && random != faussesreponses[2]){
                faussesreponses[indice] = random;
                indice = indice +1;
            }
        }
        q = creerQuestion(question, getCell(file, nb, 0), getCell(file, faussesreponses[0], 0), getCell(file, faussesreponses[1], 0), getCell(file, faussesreponses[2], 0));
        return q;
    }

    //Permet de générer une question pour la carte de la France
    Question QuestionVille() {
        Question q;
        int nb = 0;
        nb = (int) (random()*rowCount(villes)-1) +1;
        String question = "Sur la carte, quelle ville se situe au numéro : " + getCell(villes, nb, 1);
        int[] faussesreponses = new int[]{-1, -1, -1};
        int indice = 0;
        while(faussesreponses[2] == -1){
            int random = (int) (random()*rowCount(villes)-1) +1;
            if(random != nb && random != faussesreponses[0] && random != faussesreponses[1] && random != faussesreponses[2]){
                faussesreponses[indice] = random;
                indice = indice +1;
            }
        }
        q = creerQuestion(question, getCell(villes, nb, 0), getCell(villes, faussesreponses[0], 0), getCell(villes, faussesreponses[1], 0), getCell(villes, faussesreponses[2], 0));
        return q;
    }

    //Calcul le score du joueur
    int calculScore(Joueur j){
        int score = 20;
        for(int i=0; i<j.tentatives; i=i+1){
            score = (int) (score /2);
        }
        return j.score + score;
    }

    void testCalculScore(){
        Joueur j1 = creerJoueur("Nom");
        j1.tentatives = 1;
        j1.score = calculScore(j1);
        assertEquals(j1.score, 10);
        j1.tentatives = 0;
        j1.score = calculScore(j1);
        assertEquals(j1.score, 30);
        j1.tentatives = 2;
        j1.score = calculScore(j1);
        assertEquals(j1.score, 35);
    }

    //Vérifie si la partie est finie
    boolean fini(Joueur j){
        if(j.avancement == 10){
            return true;
        }
        return false;
    }

    void testFini(){
        Joueur j1 = creerJoueur("Nom");
        assertFalse(fini(j1));
        j1.avancement = 10;
        assertTrue(fini(j1));
    }

    //Convertit un String en int, si la saisie est bonne
    int Saisie(String saisie, int min, int max){
        if(VerifierSaisie(saisie, min, max)){
            char chiffre = charAt(saisie, 0);
            return (int) (chiffre - '0');
        }
        return -1;
    }

    void testSaisie(){
        assertEquals(Saisie("5", 1, 4), -1);
        assertEquals(Saisie("0", 2, 3), -1);
        assertEquals(Saisie("3", 1, 4), 3);
    }

    //Vérifie si la saisie est valide
    boolean VerifierSaisie(String saisie, int min, int max){
        if(length(saisie) == 1){
            char chiffre = charAt(saisie, 0);
            int tmp = (int) (chiffre - '0');
            if(tmp>=min && tmp<=max){
                return true;
            }
        }
        return false;
    }

    void testVerifierSaisie(){
        assertFalse(VerifierSaisie("l", 0, 3));
        assertTrue(VerifierSaisie("2", 1, 4));
    }

    //Si la bonne réponse a été choisie, un affichage a lieu et les informations du joueur sont mis à jour
    void bonnereponse(int indicereponse, int saisie, Joueur j){
        if(indicereponse == saisie){
            println(ANSI_GREEN + "Bonne réponse !" + ANSI_TEXT_DEFAULT_COLOR);
            j.avancement = j.avancement +1;
            j.score = calculScore(j);
            j.tentatives = 0;
        } else {
            println(ANSI_RED + "Mauvaise réponse !" + ANSI_TEXT_DEFAULT_COLOR);
            j.tentatives = j.tentatives +1;
        }
    }

    //Renvoie true si une question pays/capitale a déjà été posée
    boolean QuestionDejaPosee(int[] tab, int ligne){
        for(int i=0; i<length(tab); i++){
            if(tab[i] == ligne){
                return true;
            }
        }
        return false;
    }

    void testQuestionDejaPosee(){
        int[] tab = new int[]{6,2,3,8};
        assertTrue(QuestionDejaPosee(tab, 6));
        assertFalse(QuestionDejaPosee(tab, 4));
        assertTrue(QuestionDejaPosee(tab, 3));
    }

    boolean eteindreJeu(int saisie){
        if(saisie == 1){
            return true;
        }
        return false;
    }

    /*
    -----------Fonctions d'affichage du jeu-------------
    */

    //Affichage de la question avec les réponses proposées
    void toString(Question q, Joueur j){
        String question = q.q;
        int random = (int) (random()*4);
        println(question);
        println(ANSI_RED + "Entrez le numéro associé" + ANSI_TEXT_DEFAULT_COLOR);
        int indicereponse = -1;
        //L'aléatoire choisit la place de la bonne réponse
        if(random==0){
            println("1." + q.choix1 + "    2." + q.choix2 + "\n" + "3." + q.reponse + "    4." + q.choix3);
            indicereponse = 3;
        } else if (random == 1){
            println("1." + q.reponse + "    2." + q.choix2 + "\n" + "3." + q.choix1 + "    4." + q.choix3);
            indicereponse = 1;
        } else if (random == 2){
            println("1." + q.choix1 + "    2." + q.reponse + "\n" + "3." + q.choix2 + "    4." + q.choix3);
            indicereponse = 2;
        } else if (random == 3){
            println("1." + q.choix1 + "    2." + q.choix2 + "\n" + "3." + q.choix3 + "    4." + q.reponse);
            indicereponse = 4;
        }
        int tmp = Saisie(readString(), 1, 4);
        clearScreen();
        bonnereponse(indicereponse, tmp, j);
    }

    //Affichage du logo
    void afficherLogo(){
        File logo = newFile("../ressources/affichageMenu");
        while(ready(logo)){
            println(readLine(logo));
        }
    }

    //Affiche la carte du monde en fonction de l'avancement du chat
    void afficherCarte(Joueur j){
        File carte = newFile("../ressources/cartes/" + j.avancement);
        while(ready(carte)){
            println(readLine(carte));
        }
    }

    //Affiche la carte de la France
    void afficherCarteFrance(){
        File carteFrance = newFile("../ressources/carteVille");
        while(ready(carteFrance)){
            println(readLine(carteFrance));
        }
    }

    //Permet de voir l'avancement du chat
    void afficherChat(Joueur j){
        File chat = newFile("../ressources/chats/" + j.avancement);
        while(ready(chat)){
            println(readLine(chat));
        }
    }

    //Affiche les informations du joueur
    String afficherJoueur(Joueur j){
        String joueur = j.nom + "  |  " + ANSI_PURPLE + " Score : " + j.score + ANSI_TEXT_DEFAULT_COLOR;
        return joueur;
    }


    //Affichage du tableau des scores
    void toString(String[][] tab){
        println("AFFICHAGE TABLEAU SCORE");
        for(int i=0; i<length(tab, 1); i++){
            for(int j=0; j<length(tab, 2); j++){
                print(tab[i][j] + "");
                print("         ");
            }
            println();
        }
        println(AffichageMax(Actualiser()));
    }

    //Trouve dans le CSV la personne qui a eu le plus de points
    String AffichageMax(CSVFile file){
        if(rowCount(file) != 1){
            int max = 0;
            int indicemax = -1;
            for(int i=1; i<rowCount(file); i++){
                String tmp = getCell(file, i, 2);
                int scoreCase = 0;
                for(int j = 0; j<length(tmp); j=j+1){
                    char chiffre = charAt(tmp, j);
                    if(chiffre != '0'){
                        if(j==0){
                            scoreCase = scoreCase + ((int) (chiffre - '0') * 100);
                        } else if(j==1){
                            scoreCase = scoreCase + ((int) (chiffre - '0') * 10);
                        } else {
                            scoreCase = scoreCase + (int) (chiffre - '0');
                        }
                    }
                }
                if(scoreCase >= max){
                    max = scoreCase;
                    indicemax = i;
                }
            }
            return "Le score maximal a été obtenu par " + ANSI_GREEN + getCell(file, indicemax, 0) + ANSI_TEXT_DEFAULT_COLOR + " avec " + ANSI_GREEN + getCell(file, indicemax, 1) + " points" + ANSI_TEXT_DEFAULT_COLOR;
        } else {
            return ANSI_RED + "Pas de score enregistré ! :(" + ANSI_TEXT_DEFAULT_COLOR;
        }
    }

    //Affichage pour la fin de partie
    void MessageFin(Joueur j){
        if(j.score == 200){
            if(j.difficulte == -1){
                println("Bravo ! Tu as le score maximal !");
            } else if(j.difficulte<=2){
                println("Bravo ! Tu as le score maximal !  Essaie un niveau plus dur !");
            } else {
                println("Bravo ! Tu as le score maximal !");
            }
        } else if (j.score >= 150){
            println("Bravo ! Tu as eu beaucoup de bonnes réponses mais pas encore le score maximal !");
        } else {
            println("Allez ! Encore un petit effort !");
        }
        println("Veux tu sauvegarder ton score ? \n0. Oui\n 1.Non");
        if(Saisie(readString(), 0, 1) == 0){
            Sauvegarder(Charger(Actualiser()), j);
        }
        toString(Charger(Actualiser()));
        println(afficherJoueur(j));
        println("Appuie sur \"Entrée\" pour continuer");
        readString();
    }

    /*
    ------- Fonctions de sauvegarde de score -------
    */

    //Sauvegarde le score dans le CSV dans les formats convenus
    void Sauvegarder(String[][] score, Joueur j){
        String[][] result = new String[length(score, 1)+1][length(score, 2)];
        for(int i = 0; i<length(score, 2); i++){
            for(int k = 0; k<length(score, 1); k++){
                result[k][i] = score[k][i];
            } 
        }
        int indice  = length(result, 1)-1;
        result[indice][0] = j.nom;
        if(j.score<10){
            result[indice][1] = "00" + j.score; 
            int mode = Saisie(readString(), 1, 2);
            if(mode == 1){
                modeMonde(j);
            } else {
                modeFrance(j);
                j.difficulte = -1;
            }
            result[indice][1] = "" + j.score;
        }
        if(j.difficulte == 1){
            result[indice][2] = "Facile";
        } else if(j.difficulte == 2){
            result[indice][2] = "Normale";
        } else if(j.difficulte == 3){
            result[indice][2] = "Difficile";
        } else if(j.difficulte == -1){
            result[indice][2] = "France";
        } else {
            result[indice][2] = "Tous modes";
        }
        saveCSV(result, "../ressources/score.csv");
    }

    //Permet l'actualisation du CSV
    CSVFile Actualiser(){
        CSVFile score = loadCSV("../ressources/score.csv");
        return score;
    }

    //Charge le CSV en tableau
    String[][] Charger(CSVFile file){
        int lignes = rowCount(file);
        int colonnes = columnCount(file);
        String[][] result = new String[lignes][colonnes];
        for(int i = 0; i<lignes; i++){
            for(int j = 0; j<colonnes; j++){
                result[i][j] = getCell(file, i, j);
            } 
        }
        return result;
    }

    /*
    ------ Fonctions principales du jeu ------
    */
    
    void algorithm(){
        boolean eteindre = false;
        clearScreen();
        afficherLogo();
        while(!eteindre){
            println("Le but du jeu est de faire avancer le chat dans son voyage, et pour ça,\n il faut que tu répondes correctement à 10 questions");
            println("D'abord, comment t'appelles tu ?");
            Joueur j = creerJoueur(readString());
            println("Quel mode voulez-vous jouer ?\n 1.Monde\n 2.France");
            int mode = Saisie(readString(), 1, 2);
            if(mode == 1){
                modeMonde(j);
            } else {
                modeFrance(j);
                j.difficulte = -1;
            }
            MessageFin(j);
            println("Que veux-tu faire ?\n0. Commencer une nouvelle partie\n1. Quitter le jeu");
            eteindre = eteindreJeu(Saisie(readString(),0,1));
        }
    }

    void modeMonde(Joueur j){
        println("Quel niveau de difficulté, veux tu ? \n 1. Facile \n 2. Moyen \n 3. Difficile \n 4. Tous modes de difficulté confondus");
        println(ANSI_RED + "Entrez le numéro associé" + ANSI_TEXT_DEFAULT_COLOR);
        String diff = "";
        int[] historique = new int[11];
        historique[10] = 0; //Eviter que la valeur de défaut de nb soit utiliser en tant que question
        diff = readString();
        while(!VerifierSaisie(diff, 1, 4)){
            diff = readString();
            println(ANSI_RED + "Saisie invalide" + ANSI_TEXT_DEFAULT_COLOR);
        }
        j.difficulte = Saisie(diff, 1, 4);
        while(!fini(j)){
            afficherCarte(j);
            afficherChat(j);
            Question question = GenererQuestion(j, historique);
            toString(question, j);
            println(afficherJoueur(j));
        }
        afficherCarte(j);
        afficherChat(j);
    }

    void modeFrance(Joueur j){
        println("Le chat prépare son voyage en France mais il est un peu perdu... \n Réponds à une série de questions sur les plus grandes ville de France pour l'aider !");
        while (!fini(j)){
            afficherCarteFrance();
            afficherChat(j);
            Question question = genererQuestionFrance(j);
            toString(question, j);
            println(afficherJoueur(j));        
        }
    }
}