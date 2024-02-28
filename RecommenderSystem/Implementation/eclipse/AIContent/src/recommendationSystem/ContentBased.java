package recommendationSystem;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class ContentBased {
	
	// Show a matrix in the console
	public static <T> void printMatrix(List<ArrayList<T>> matrix) {

		for (int i = 0; i < matrix.size(); i++) {

			System.out.print(matrix.get(i).get(0));

			for (int j = 1; j < matrix.get(i).size(); j++) {

				System.out.print(", " + matrix.get(i).get(j));

			}

			System.out.println();
		}

	}

	
	// This function standardized a row
	// Standardized value = (value - rowMeanValue) / (rowMax - rowMin)
	public static List<Double> standardizeRow(List<Double> row) {
		
		// New row (standardized)
		ArrayList<Double> standarizedRow = new ArrayList<>();
				
		// Needed variables
		double mean, maximum, minimum, total;
		
		// Initialization of the variables
		minimum = row.get(0);
		maximum = row.get(0);
		total = row.get(0);

		for (int j = 0; j < row.size(); j++) {

			// We need this for the calculation of the mean
			total += row.get(j);

			// Checking the maximum
			if (row.get(j) > maximum) {
				maximum = row.get(j);
			}

			// Checking the minimum
			if (row.get(j) < minimum) {
				minimum = row.get(j);
			}

		}

		// Calculating the mean
		mean = total / row.size();

		// System.out.println("Row " + i + ":");
		// System.out.println("Mean = " + mean + ", Maximum = " + maximum + ", Minimum = " + minimum);

		// Calculating the new standardized row
		for (int j = 0; j < row.size(); j++) {

			// Standardized value = (value - rowMeanValue) / (rowMax - rowMin)
			standarizedRow.add((row.get(j) - mean) / (maximum - minimum));
		}
		
		return standarizedRow;

	}

	
	// This function standardized a matrix
	// Standardized value = (value - rowMeanValue) / (rowMax - rowMin)
	// The comments are checks
	public static List<ArrayList<Double>> standardizeMatrix(List<ArrayList<Double>> matrix) {

		// New matrix (standardized)
		ArrayList<ArrayList<Double>> standarizedMatrix = new ArrayList<>();

		// Standardizing each row
		for (int i = 0; i < matrix.size(); i++) {

			standarizedMatrix.add((ArrayList<Double>) standardizeRow(matrix.get(i)));

		}
		
		return standarizedMatrix;
	}

	
	// This function transpose a matrix
	public static List<ArrayList<Double>> transpose(List<ArrayList<Double>> matrix) {

		// New matrix (transposed)
		ArrayList<ArrayList<Double>> transposed = new ArrayList<>();

		// All the columns have the same size, so we use matrix.get(0).size()
		int size = matrix.get(0).size();

		for (int x = 0; x < size; x++) {

			// New row (transposed)
			ArrayList<Double> transposedCol = new ArrayList<>();

			// For each row
			for (ArrayList<Double> row : matrix) {

				// We add the corresponding value in the new row
				transposedCol.add(row.get(x));

			}

			transposed.add(transposedCol);
		}

		return transposed;
	}

	
	// This function calculate the User Profile of one user standarized
	public static void userProfileCalculation(ArrayList<Double> userRatings, List<ArrayList<Double>> matrixGenres) {

		// This list will contain the user profile
		ArrayList<Double> userProfile = new ArrayList<>();
		
		// This list will contain the user matrix of genres
		List<ArrayList<Double>> userMatrixGenres = new ArrayList<ArrayList<Double>>();
		
		// For each row
		for (int i = 0; i < userRatings.size(); i++) {
			
			// We calculate the new value using the rating of the user and the matrix of genres of all movies
			ArrayList<Double> row = new ArrayList<Double>();
			
			for (int j = 0; j < matrixGenres.get(0).size(); j++) {
				
				row.add(userRatings.get(i) * matrixGenres.get(i).get(j));
				
			}
			
			// This new values are added to the user matrix of genres
			userMatrixGenres.add(row);
		}
		
		/*
		// This print checks the matrix of genres of the user
		System.out.println("");
		System.out.println("Matrix of genres for this user");
		
		printMatrix(userMatrixGenders);
		*/
		
		
		// We calculate the value of a genre with the summation of the values of the column that represents that genre
		for (int c = 0; c < userMatrixGenres.get(0).size(); c++) {
			
			double sum = 0;
			
			for (int i = 0; i < userMatrixGenres.size(); i++) {
				
				sum += userMatrixGenres.get(i).get(c);
				
			}
			
			userProfile.add(sum);
		}
		
		
		// Variables for writing the new txt file
		FileWriter file = null;
        PrintWriter pw = null;
        
        try {
        	
        	file = new FileWriter("/Users/tomas/Desktop/AI/Implementacion/DataSets/MoviesDataset/userProfile.txt");
			
			pw = new PrintWriter(file);
	        
			for(double value : userProfile) {
				
				pw.println(value);
				
			}
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
        	
	           try {
	        	   
	           // We close the file
	           if (null != file) {
	        	   
	        	   file.close();
	        	   
	           }
	              
	           } catch (Exception e2) {
	        	   
	              e2.printStackTrace();
	              
	           }
	        }
       
	}

	
	/*
	 * Funcion que hace una recomendacion para un perfil de un usuario
	 * verdaderamente esto es lo que hace el conten. Para eso recorro el perfil del
	 * usuario que me dice su puntuacion para cada género y lo multiplico por cada
	 * género de la matriz de géneros (columnas de la matriz de generos) para sacar
	 * en cada fila de la matriz de generos la puntuacion de cada peli para cada
	 * genero, luego sumamos la fila y nos da el valor para cada peli, después
	 * ordenamos las pelis y devolvemos el mapa ordenado por valor. A partir de la
	 * lista movies rated puedo saber que pelis ya ha visto para no recomendarselas
	 */
	
	// This function makes a recommendation based on a user profile
	public static LinkedHashMap<String, Double> contentRecommendation(ArrayList<Double> userProfile, List<ArrayList<Double>> matrixGenders, List<LinkedList<String>> films, ArrayList<String> moviesRated) {
		
		// This list will contain the recommendation for the user
		LinkedHashMap<String, Double> recommendation = new LinkedHashMap<>();
		
		// Voy a guardar las valoracioens por filas en vez de por columnas asi que luego
		// tendre que transponerla
		// This matrix will contain the value of each 
		List<ArrayList<Double>> matrixGendersWeigthTransposed = new ArrayList<ArrayList<Double>>();
		
		// Multiplico cada elemento de la lista del perfil del usuario que contiene la
		// ountuacion para un genero por la columna de la matriz de generos donde esta
		// ese genero
		for (int j = 0; j < userProfile.size(); j++) {
			
			ArrayList<Double> row = new ArrayList<>();
			
			for (int i = 0; i < matrixGenders.size(); i++) {
				
				row.add(userProfile.get(j) * matrixGenders.get(i).get(j));
				
			}
			
			matrixGendersWeigthTransposed.add(row);
		}
		
		// La transpongo para coincidir bien la matriz las valoraciones de las pelis
		// segun sus generos
		List<ArrayList<Double>> matrixGendersWeigth = transpose(matrixGendersWeigthTransposed);

		// Multiplico cada fila para sacar el valor de cada peli
		// Utilizo la estructura donde esten las pelis para saber a que peli corresponde
		// cada fila de la matriz de generos

		for (int i = 0; i < matrixGendersWeigth.size(); i++) {
			
			double sum = 0;
			
			for (int j = 0; j < matrixGendersWeigth.get(0).size(); j++) {
				
				sum += matrixGenders.get(i).get(j);
				
			}
			
			// En la posicion 1 de la estructura esta el nombre de la peli
			recommendation.put(films.get(i).get(1), sum);
		}
		
		// This list contains the final recommendations (it not contains the movies that
		// the user have been seen (the rated movies)
		LinkedHashMap<String, Double> finalRecommendation = new LinkedHashMap<>();

		// We need the map in a descent order, so we use streams for it
		recommendation.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).forEachOrdered(x -> finalRecommendation.put(x.getKey(), x.getValue()));
		
		// The films that have been seen by the user must be ignored, so we remove that
		// movies
		for (String movieUse : moviesRated) {

			finalRecommendation.remove(movieUse);

		}
		
		return finalRecommendation;
	}

	
	/*
	 * Para cada usuario (fila) del usersProfile llamamos a la funcion content
	 * Recommendation a la que le pasamos su perfil es decir su fila y las pelis que
	 * hay en el catalogo y las peliculas vistas por cada usuario. Para eso a esta
	 * funcion le pasamos la matriz con el perfil de los usuarios la matriz con los
	 * generos de las pelis, el catalogo de pelis y las pelis valoradas por el
	 * usuario y devuelvo un mapa con el usuario y las pelis recomendadas con la
	 * valoracion de cada una
	 */
	public static LinkedHashMap<String, LinkedHashMap<String, Double>> contentRecommendationUsers( List<ArrayList<Double>> usersProfile, List<ArrayList<Double>> matrixGenders, List<LinkedList<String>> films,List<ArrayList<String>> usersMoviesRated) {

		
		LinkedHashMap<String, LinkedHashMap<String, Double>> recommendation = new LinkedHashMap<String, LinkedHashMap<String, Double>>();
		
		for (int i = 0; i < usersProfile.size(); i++) {
			
			recommendation.put("User" + (i+1), contentRecommendation(usersProfile.get(i), matrixGenders, films, usersMoviesRated.get(i)));
			
		}
		
		LinkedHashMap<String, LinkedHashMap<String, Double>> finalrecommendation = new LinkedHashMap<String, LinkedHashMap<String, Double>>();

		
		return recommendation;
		
	}
	
	
	// This function returns the name of the movies rated by an user
	public static ArrayList<String> moviesRatedUser(ArrayList<Double> userRates, List<LinkedList<String>> films) {
		
		// This list will contain the list of movies
		ArrayList<String> moviesRated = new ArrayList<>();
		
		// For each movie
		for (int i = 0; i < films.size(); i++) {
			
			// We check if the user has rated that movie
			if (userRates.get(i) != 0) {
				
				moviesRated.add(films.get(i).get(1));
				
			}
				
		}
		
		return moviesRated;
	}

	
	public static void main(String[] args) {

		// We store a list with the id, the name and the genres of the movies
		// The first node stores the id of the movie, the second the name and the rest
		// the genres of the movie
		// The index of each node is the index of the movie in the matrix
		List<LinkedList<String>> films;
		
		// ToySet
		//films = ReaderOfFiles.getMovies("/Users/tomas/Desktop/AI/Implementacion/DataSets/MoviesDataset/toySet/Toy_Movies.csv", ",");
		//Big csv
		films = ReaderOfFiles.getMovies("/Users/tomas/Desktop/AI/Implementacion/DataSets/MoviesDataset/movies.csv", ",");

		
		// This is a pre-procesing of the csv file
		
		// ToySet
		//ReaderOfFiles.extractContent("/Users/tomas/Desktop/AI/Implementacion/DataSets/MoviesDataset/toySet/Toy_Ratings.csv", ",", films);
		//Big csv
		//ReaderOfFiles.extractContent("/Users/tomas/Desktop/AI/Implementacion/DataSets/MoviesDataset/ratings.csv", ",", films);
		
		/*
		// This print checks the function getMovies()
		System.out.println("FILMS : ");

		for (LinkedList<String> m : films) {

			for (String s : m) {
				System.out.print(s + " ");
			}
			System.out.println();
		}
		*/
		
		// We obtain the genres of the films
		ArrayList<String> genres = ReaderOfFiles.getGenres(films);
		
		/*
		// This print checks the function getGenres()
		System.out.println("");
		System.out.println("Genres of the films:");
		
		for(String g : genres) {
			System.out.println(g);
		}
		*/
		
		// We obtain the matrix of genres
		List<ArrayList<Double>> matrixGenres = ReaderOfFiles.getGenresMatrix(films, genres);
		
		/*
		// This print checks the matrix of genres
		System.out.println("");
		System.out.println("Matrix of Genres:");
		printMatrix(matrixGenres);
		*/
	
				
		/****************** RECOMMENDATION FOR ONE USER BASED ON HIS USER PROFILE *************************/
		

		System.out.println("");
		System.out.println("RECOMMENDATION FOR ONE USER : ");
		
		
		// This list store the ratings for the user for all the movies of the catalog
		// 0.0 means that the user has not seen the movie
		ArrayList<Double> userRates = new ArrayList<>();
		
		// Initialization of the list
		for(int i = 0; i < films.size(); i++) {
			
			userRates.add(0.0);
			
		}
		
		
		// We rate some of the films
		userRates.set(0, 3.0); // Jurasic Park action horror
		userRates.set(1, 5.0); // Jumanji action
		userRates.set(5, 2.0); // Scary Movie horror comedy
		userRates.set(6, 5.0); // White Chicks comedy
		
		/*
		// This print checks the ratings of the user
		System.out.println();
		System.out.println("User Duki ratings : ");
		System.out.println(userRates);
		*/
		
		// We calculate the profile of the user
		userProfileCalculation(userRates, matrixGenres);
		
		
		// We read the user profile from the txt file
		ArrayList<Double> userProfile = ReaderOfFiles.getUserProfile("/Users/tomas/Desktop/AI/Implementacion/DataSets/MoviesDataset/userProfile.txt");
		

		/*
		// This print checks the function userProfileCalculation()
		System.out.println("");
		System.out.println("Genres of the films:");
		System.out.println(genres);
		
		System.out.println("");
		System.out.println("User Duki profile:");
		System.out.println(userProfile);
		*/
		
		ArrayList<Double> userProfileStandarized = (ArrayList<Double>) standardizeRow(userProfile);
		

		/*
		// This print checks the function standardize()
		System.out.println("");
		System.out.println("User Duki profile standarized:");
		System.out.println(userProfileStandarized);
		*/
		
		// This list contains the movies rated by the user
		ArrayList<String> moviesRated = moviesRatedUser(userRates, films);
		

		/*
		// This print checks the function moviesRatedUser()
		System.out.println("");
		System.out.println("Films rated by the user Duki:");		
		System.out.println(moviesRated);
		*/
		
		// Here we make the recommendations for the user based on his profile
	    LinkedHashMap<String, Double> recommendationForUser = contentRecommendation(userProfileStandarized, matrixGenres, films, moviesRated);
	   
	    
	    // This print checks the function that makes recommendations
	    System.out.println("");
		System.out.println("Recommendation for Duki:");
	    System.out.println(recommendationForUser);
		
	    
	    
	   
		
	}
}
