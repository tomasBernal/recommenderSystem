package recommendationSystem;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

//This class implements the necessary functions to apply the content based filtering
public class ContentBased {
		
	// This function creates the matrix of genres of all the movies
	// This function receives the list of films (movies) and the list of genres (genres)
	// This function returns a list of list (matrix) with the values of the genres for each movie
		// Each row represents a movie
		// Each column represents a genre
		// The value 0.0 represents false (the movie don't belongs to that genre)
		// The value 1.0 represents true (the movie belongs to that genre)
	public static void processGenresMatrix(List<LinkedList<String>> movies, List<String> genres, String path) {

		// Creating the matrix
		List<ArrayList<Double>> matrix = new LinkedList<>();

		// First the matrix is filled with the false value (0.0)
		for (int i = 0; i < movies.size(); i++) {

			// We create the row that represents each movie
			ArrayList<Double> row = new ArrayList<>();

			// For each genre 
			for (int j = 0; j < genres.size(); j++) {
				
				// We add the value 0.0
				row.add(0.0);

			}

			// We add the row to the matrix
			matrix.add(row);

		}

		// For each movie
		for (int i = 0; i < movies.size(); i++) {

			// We start iterating in 2 because the genres of the movie are from the third position of the list
			for (int j = 2; j < movies.get(i).size(); j++) {

				// First, we obtain the index in the list of genres using the actual genre movies.get(i).get(j)
				// Secondly, we set this genre to true (1.0) in the row of genres of the movie
				
				matrix.get(i).set(genres.indexOf(movies.get(i).get(j)), 1.0);

			}
		}

		// Here we store the cosine similarity in a file
		FileWriter file = null;
		PrintWriter pw = null;
        
        try
        {
        	file = new FileWriter(path);
            pw = new PrintWriter(file);

            for(ArrayList<Double> row : matrix) {
            	
            	pw.print(row.get(0));
            	
            	for(int i = 1; i < row.size(); i++) {
            		
            		pw.print("," + row.get(i));
            	}
            	
            	pw.println();
            }

        } catch (Exception e) {
        	
            e.printStackTrace();
            
        } finally {
        	
           try {
        	   
           // Closing the file
           if (null != file)
        	   file.close();
           } catch (Exception e2) {
              e2.printStackTrace();
           }
        }
		
	}
	
	public static List<ArrayList<Double>> getGenresMatrix(String path){
		
		List<ArrayList<Double>> genresMatrix = new ArrayList<>();
		
		// This variables are used when the file is read
		FileReader fr = null;
		BufferedReader br = null;
		
		try {
			
			// Creating the reader
			fr = new FileReader (path);
			br = new BufferedReader(fr);

			// Stores each line of the file
			String line;
			
			// Each line is splited using the divider
			String[] parts;
					
			ArrayList<Double> row = new ArrayList<Double> ();
			
			// Reading the file
			while((line=br.readLine())!= null){
				
				// We split each line
				parts  = line.split(",");
			
				row = new ArrayList<Double> ();
				
				for( String d : parts) {
					
					row.add(Double.parseDouble(d));
					
				}
				
				genresMatrix.add(row);
				
				
			}
		
				
		} catch(Exception e){
			
			e.printStackTrace();
			
		} finally{
			
			// Closing the reader and the file
			try{                    
				if( null != fr ){  
				
					fr.close();  
					
				}    
				
			}catch (Exception e2){ 
				e2.printStackTrace();
			}
		}

		return genresMatrix;
	}
	
	// This function calculate the user profile of one user
	// This function receives the map of ratings of the user (userRatings) and the matrix of genres of the movies (matrixGenres)
	// This function returns a list with the values of the genres for this user (based on his ratings)
	public static ArrayList<Double> userProfileCalculation(HashMap<Integer, Double> userRatings, List<ArrayList<Double>> matrixGenres) {

		// This list will contain the user profile
		ArrayList<Double> userProfile = new ArrayList<>();
		
		// This matrix will contain the user matrix of genres
		// This matrix is calculated using the ratings of the user and the genres of the rated movies
		List<ArrayList<Double>> userMatrixGenres = new ArrayList<ArrayList<Double>>();
		
		// For each rated movie
		for(int index : userRatings.keySet()) {
			
			
			// This row represent the rated movie in the matrix of genres of the user
			ArrayList<Double> row = new ArrayList<Double>();
			
			// For each genre
			for (int j = 0; j < matrixGenres.get(0).size(); j++) {
				
				// For each rated movie
				// We multiply the user's rating by the value of the genre of the movie in the genres matrix
				row.add(userRatings.get(index) * matrixGenres.get(index).get(j));
				
			}
			
			// The row with the new values are added to the user matrix of genres
			userMatrixGenres.add(row);
			
		}
		
		
		/*
		// This print checks the matrix of genres of the user
		System.out.println("");
		System.out.println("Matrix of genres for this user");
		
		MatrixFuncionality.printMatrix(userMatrixGenres);
		*/
		
		
		// Here we make the sum of the values of each genre
		
		// For each column (genre)
		for (int c = 0; c < userMatrixGenres.get(0).size(); c++) {
			
			// This variable store the sum of the genre
			double sum = 0;
			
			// For each row (movie)
			for (int i = 0; i < userMatrixGenres.size(); i++) {
				
				// We sum the values of each genre for all the movies
				sum += userMatrixGenres.get(i).get(c);
				
			}
			
			// We add the value of that genre to the user profile
			userProfile.add(sum);
		}
		
		return userProfile;
       
	}

	
	// This function makes a recommendation based on a user profile using the content-based filtering
	// This function receives the list of the user profile (userProfile), the matrix of genres of the movies (matrixGenres), the list of films (films) and the list of rated movies (moviesRated)
	// This function returns a list of Ratings, that is, the recommendations (a list of objects with the movie name and the value of the recommendation)	
	public static LinkedList<LinkedList<String>> contentRecommendation(ArrayList<Double> userProfile, List<ArrayList<Double>> matrixGenres, List<LinkedList<String>> films, Set<String> moviesRated) {
		
		// This map will contain the recommendation for the user <movieName, recommendationValue>
		LinkedHashMap<LinkedList<String>, Double> recommendation = new LinkedHashMap<>();
		
		
		// Matrix of weights of each genre in each movie based on the user profile
		// This matrix will contain the weight (preference) of each genre for each movie for the user
		// It will contain the result of the multiply of each value of a genre for the user (userProfile)
		// by the value of each genre for a movie (matrixGenres)
			// Each row contains represents a genre
			// Each column represents a movie
		List<ArrayList<Double>> matrixGenresWeigthUser = new ArrayList<ArrayList<Double>>();
		
		
		// For each genre (column of the userProfile or the matrix of genres)
		for (int j = 0; j < userProfile.size(); j++) {
			
			ArrayList<Double> row = new ArrayList<>();
			
			// For each row of the matrix of genres (movie)
			for (int i = 0; i < matrixGenres.size(); i++) {
				
				// We multiply the user's profile value for a genre by the value of that genre for the movie
				row.add(userProfile.get(j) * matrixGenres.get(i).get(j));
				
			}
			
			// We add the row to the weighted matrix
			matrixGenresWeigthUser.add(row);
		}
		

		// Each column of the matrix of weighs (movie)
		for (int i = 0; i < matrixGenresWeigthUser.get(0).size(); i++) {
			
			// This variable store the sum of the movie
			double sum = 0;
			
			// For each row of the matrix of weighs (genre)
			for (int j = 0; j < matrixGenresWeigthUser.size(); j++) {
				
				// We sum the values of all genres (row) for each movie
				sum += matrixGenresWeigthUser.get(j).get(i);
				
			}
			
			
			// We add the movie and the value to the map
			// The second node of the list of movies contains the name of the movie
			recommendation.put(films.get(i), sum);
		}
		
		// This list contains the recommendations minus the movies that the user has seen (the rated movies)
		LinkedList<LinkedList<String>> finalRecommendation = new LinkedList<LinkedList<String>>();

		// We need the map ordered in a descent order, so we use streams for it
		recommendation.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).forEachOrdered(x -> finalRecommendation.add(x.getKey()));
		
		// This list will contain the list of movies seen by the user (the rated movies)
		LinkedList<LinkedList<String>> seenMovies = new LinkedList<LinkedList<String>>();
				
		// The films that have been seen by the user must be ignored, so we remove that movies
		// For each rated movie
		for(String rm : moviesRated) {
			
			// For each recommendation
			for(LinkedList<String> m : finalRecommendation) {
						
				// We check if the recommended movie is the same that the rated movie
				if((rm.equals(m.get(1)))){
					
					// If yes
					
					// We add the movie to the list of seen movies
					seenMovies.add(m);
					
				}
			}
					
		}
				
		// We remove the seen movies
		finalRecommendation.removeAll(seenMovies);
		
		return finalRecommendation;
	}

	
	// This function this function allows a recommendation based on content based filtering, to do so, it calls the other functions of class
	// This function implements the content-based filtering
	// This function receives the list of films (films), the list of genres (genres) and the map of the user rates (userRatings)
	// This function returns a list of Ratings, that is, the recommendations (a list of objects with the movie name and the value of the recommendation)	
	public static LinkedList<LinkedList<String>> contentFiltering(List<LinkedList<String>> films, List<String> genres, Map<String, Double> userRatings){
	
		
		// From the Toy Dataset
		//ContentBased.processGenresMatrix(films, genres, Hybrid.pathDatasets+"ToySet/genres.txt");
		// From the MovieLens Dataset	
		//ContentBased.processGenresMatrix(films, genres, Hybrid.pathDatasets+"MovieLens/genres.txt");
		
		// We obtain the matrix of genres of the movies
		// From the Toy Dataset
		//List<ArrayList<Double>> matrixGenres = getGenresMatrix(Hybrid.pathDatasets+"ToySet/genres.txt");
		// From the MovieLens Dataset
		List<ArrayList<Double>> matrixGenres = getGenresMatrix(Hybrid.pathDatasets+"MovieLens/genres.txt");
		
		/*
		// This print checks the matrix of genres
		System.out.println("");
		System.out.println("Matrix of Genres:");
		MatrixFuncionality.printMatrix(matrixGenres);
		*/
		
	
		// This map will store the ratings of the user with the index of the movie in the matrix
		// 0.0 means that the user has not rated (seen) the movie
		HashMap<Integer, Double> userRates = new HashMap<>();
	
		
		// We need to store the id of the rated movies by the user
		
		// For each movie rated by the user
		for( String name : userRatings.keySet()) {
			
			// We search the index in the matrix of the given movie
			int movieIndex = 0;
					
			// We check which movie of the list is the rated movie
			// The second node of the list has the name of the movie
			while(movieIndex < films.size() && !films.get(movieIndex).get(1).equals(name)) {
				movieIndex++;
			}
				
			// We check if we found the movie
			if(movieIndex == films.size()) {
				return null;
			}
			
			// We store the rate of the movie with his id
			userRates.put(movieIndex, userRatings.get(name));
		}
				
		
		// We calculate the profile of the user
		ArrayList<Double> userProfile = userProfileCalculation(userRates, matrixGenres);
				
		
		/*
		// This print checks the function userProfileCalculation()
		System.out.println("");
		System.out.println("Genres of the films:");
		System.out.println(genres);
				
		System.out.println("");
		System.out.println("User Duki profile:");
		System.out.println(userProfile);
		*/
		
		
		// We standardized the profile of the user
		ArrayList<Double> userProfileStandarized = MatrixFuncionality.standardizeRow(userProfile);
		
		
		/*
		// This print checks the function standardize()
		System.out.println("");
		System.out.println("User Duki profile standarized:");
		System.out.println(userProfileStandarized);
		*/
		
		
		
		// Here we make the recommendations for the user based on his profile
		// To obtain the names of the movies rated by the user we use userRatings.keySet()
	    LinkedList<LinkedList<String>> recommendationForUser = contentRecommendation(userProfileStandarized, matrixGenres, films, userRatings.keySet());

	    
	    return recommendationForUser;
	}
	

}
