package recommendationSystem;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;

public class CollaborativeFiltering {

	// This function standardized a matrix
	// Standardized value = (value - rowMeanValue) / (rowMax - rowMin)
	// The comments are checks
	public static List<ArrayList<Double>> standarize(List<ArrayList<Double>> matrix) {
		
		// New matrix (standardized)
		ArrayList<ArrayList<Double>> standarizedMatrix = new ArrayList<>();
		
		// Needed variables
		double mean, maximum, minimum, total;
		
		//System.out.println("STANDARIZE DATA:");
		
		for (int i = 0; i < matrix.size(); i++) {
			
			// Initialization of the variables
			minimum = matrix.get(i).get(0);
			maximum = matrix.get(i).get(0);
			total = matrix.get(i).get(0);
			
			for (int j = 0; j < matrix.get(i).size(); j++) {
				
				// We need this for the calculation of the mean
				total += matrix.get(i).get(j);
				
				// Checking the maximum
				if (matrix.get(i).get(j) > maximum){
					maximum = matrix.get(i).get(j);
				}
					
				
				// Checking the minimum
				if (matrix.get(i).get(j) < minimum) {
					minimum = matrix.get(i).get(j);
				}
					
			}
			
			// Calculating the mean
			mean = total / matrix.get(i).size();
			
			//System.out.println("Row " + i + ":");
			//System.out.println("Mean = " + mean + ", Maximum = " + maximum + ", Minimum = " + minimum);
			
			// New standardized row
			ArrayList<Double> standarizedRow = new ArrayList<>();
			
			// Calculating the new standardized row
			for (int j = 0; j < matrix.get(i).size(); j++) {
				
				// Standardized value = (value - rowMeanValue) / (rowMax - rowMin)
				standarizedRow.add((matrix.get(i).get(j) - mean) / (maximum - minimum));
			}
			
			standarizedMatrix.add(standarizedRow);
			
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
	
	// Thus function makes the calculation of the length of a vector
	// ||V|| = sqrt(sum[1,n](pow(Vi,2))
	
	public static double vectorLength(List<ArrayList<Double>> matrix, int v) {
		
		double sum = 0;
		
		// All the columns have the same size, so we use matrix.get(0).size()
		int size = matrix.get(0).size();
				
		// First the summation
		for (int j = 0; j < size; j++) {
			
			sum += Math.pow(matrix.get(v).get(j), 2);
			
		}
		
		sum = Math.sqrt(sum);
		
		return sum;
	}
	
	// This function makes the calculation of the cosine similarity in a matrix
	// CosSim(A,B) = cos(0) = (A x B) / (||A|| x ||B||)
	// A x B = sum[1,n](Ai x Bi) = (A1 x B1) + (A2 x B2) + ... + (An x Bn)
	// ||A|| = sqrt(sum[1,n](pow(Ai,2))
	// ||B|| = sqrt(sum[1,n](pow(Bi,2))
	public static List<ArrayList<Double>> cosineSimilarity(List<ArrayList<Double>> matrix) {
		
		List<ArrayList<Double>> csMatrix = new ArrayList<>();
	
		// All the columns have the same size, so we use matrix.get(0).size()
		int size = matrix.get(0).size();
				
		// A: row that we are calculating the similarity with the other rows
		for (int a = 0; a < matrix.size(); a++) {
			
			ArrayList<Double> row = new ArrayList<>();
			
			// B: the other row
			for (int b = 0; b < size; b++) {
				
				double dotProduct = 0;
				
				// calculation of the dot product between the 2 rows
				// A x B = sum[1,n](Ai x Bi) = (A1 x B1) + (A2 x B2) + ... + (An x Bn)
				for (int j = 0; j < size; j++) {
					
					dotProduct += matrix.get(a).get(j) * matrix.get(b).get(j);
					
				}
				
				// Calculating ||A|| x ||B||
				
				// Calculating ||A|| = sqrt(sum[1,n](pow(Ai,2))
				
				double A = vectorLength(matrix, a);
				
				// Calculating ||B|| = sqrt(sum[1,n](pow(Bi,2))
				double B = vectorLength(matrix, b);
				
				
				// Then we calculate ||A|| x ||B||
				double AxB = A * B;
				
				// And finally the cosine similarity
				double cos = dotProduct / AxB;
				
				
				row.add(cos);
			}
			
			csMatrix.add(row);
			
		}
		
		return csMatrix;

	}

	
	// This function gives recommendations based on one rating of a user for a given movie
	/*
	 * For this we have to get the row of the matrix of the given movie and multiply the row with the rating minus the fixer (mean)
	 * What this function returns is a list with the recommendations (these elements are returned sorted in decreasing order by their rating)
	 * For positive ratings the first recommended movie is the rated movie (since this film is the one that most closely resembles itself)
	 */
	public static  Map<String, Double>  getSimilarMovies(String ratedMovie, double rating, double fixer, List<ArrayList<Double>> matrix, List<LinkedList<String>> movies, HashSet<String> knowledgeGenres) {
		
		// This map contains the recommendation list
		HashMap<String, Double> similarMovies = new HashMap<>();
		
		// This map contains each movie with it's genres
		HashMap<String, LinkedList<String>> movieGenres = new HashMap<> ();
		
		// We search the index in the table of the given movie
		int movieIndex = 0;
				
		// We check which movie of the list is the rated movie
		// The second node of the list has the name of the movie
		while(movieIndex < movies.size() && !movies.get(movieIndex).get(1).equals(ratedMovie)) {
			movieIndex++;
		}
				
		if(movieIndex == movies.size()) {
			return null;
		}
				
		// For each rating of the user
		// All the columns have the same size, so we use matrix.get(0).size()
		for (int j = 0; j < matrix.get(0).size(); j++) {
					
			// The new values (new ratings) is the result of multiply the value of each rated movie
			// with the rating of the user minus a number call fixer (the mean of the possible rating values)
			// We get the name of the movie and the new rating
			similarMovies.put(movies.get(j).get(1), matrix.get(movieIndex).get(j) * (rating - fixer));
			
			// We need to get the genres of each recommended movie
			LinkedList<String> g = new LinkedList<> ();
			
			// We add the genres
			for(int i = 2; i < movies.get(j).size(); i++) {
				
				g.add(movies.get(j).get(i));
			}
			
			// We store the movie with it's genres in the map
			movieGenres.put(movies.get(j).get(1), g);
				
		}
				
		LinkedHashMap<String, Double> recommendation = new LinkedHashMap<>();
				
		// We need the map in a descent order, so we use streams for it
		// For positive recommendations first one is the same film so we don't have to look to it, the recommendation would be the second one.
		similarMovies.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).forEachOrdered(x -> recommendation.put(x.getKey(), x.getValue()));

		similarMovies = Knowledge.filterGenres(recommendation, movieGenres, knowledgeGenres);
		
		return similarMovies;
	}

	
	// Show a matrix in the console
	public static void printMatrix(List<ArrayList<Double>> matrix) {
			
		for (int i = 0; i < matrix.size(); i++) {

			System.out.print(matrix.get(i).get(0));
				
			for (int j = 1; j < matrix.get(i).size(); j++) {
					
				System.out.print(", " + matrix.get(i).get(j));
					
			}
			
			System.out.println();
		}
			 
	}
	
	
	public static void main(String[] args) {
		
		// Creating the scanner that we will use in the knowledge filtering and for getting the user ratings
		Scanner keyboard = new Scanner(System.in);
		
		
		// 1. We store a list with the id, the name and the genres of the movies
		// The first node stores the id of the movie, the second the name and the rest the genres of the movie
		// The index of each node is the index of the movie in the matrix
		
		List<LinkedList<String>> films;
		
		// ToySet
		//films = ReaderOfFiles.getMovies("/Users/tomas/Desktop/AI/Implementacion/DataSets/MoviesDataset/toySet/toy_set_movies.csv", ",");
		// Big csv
		films = ReaderOfFiles.getMovies("/Users/tomas/Desktop/AI/Implementacion/DataSets/MoviesDataset/movies.csv", ",");
				
		// This is a pre-procesing of the csv file
		// ToySet
		//ReaderOfFiles.extractContent("/Users/tomas/Desktop/AI/Implementacion/DataSets/MoviesDataset/toySet/toy_set_ratings.csv", ",", films);
		// Big csv
		ReaderOfFiles.extractContent("/Users/tomas/Desktop/AI/Implementacion/DataSets/MoviesDataset/ratings.csv", ",", films);
					
		/*
		// This print checks the function getmovies()
		System.out.println("FILMS : ");
		
		for( LinkedList<String> m : films) {
		
			for(String s : m) {
				System.out.print(s + " ");
			}
			System.out.println();
		}
		*/
		
		// For the knowledge filtering we need to know all the genres of the movies
		
		HashSet<String> genres = new HashSet<String> ();
				
		for(LinkedList<String> movie : films) {
			
			for(int i = 2; i < movie.size(); i++) {
						
				genres.add(movie.get(i));
			}
		}
				
		/*
		// This print checks the genres of the movie
		System.out.println();
		System.out.println("Genres : ");
		for(String m : genres) {
			System.out.println(m);
		}
		System.out.println();
		*/
		
		// We ask the user the genres that he don't like
		genres = Knowledge.getGenres(genres, keyboard);
		
		/*
		// This print checks the genres that the user don't like
		System.out.println("Genres : ");
		for(String m : genres) {
			System.out.println(m);
		}
		*/
		
		// 2. We create a structure with the ratings of the users
		// Movies with no rating have been rated with 0.0
		List<ArrayList<Double>> matrix;
		
		// ToySet
		//matrix = ReaderOfFiles.readFile("/Users/tomas/Desktop/AI/Implementacion/DataSets/MoviesDataset/toySet/toy_set_pre-procesed.txt", ",");		
		// Big csv
		matrix = ReaderOfFiles.readFile("/Users/tomas/Desktop/AI/Implementacion/DataSets/MoviesDataset/pre-procesed.txt", ",");		
				
		/*
		// This print checks the rating matrix
		System.out.println("");
		System.out.println("MATRIX:");
		printMatrix(matrix);
		*/
		
		// 3. We need to standardized the matrix
		// Standardized value = (value - mean value of the row) / ( maximum value of the row - minimal value of the row)
		List<ArrayList<Double>> mStandard = standarize(matrix);

		
		/*
		// This print checks the standardized matrix
		System.out.println("");
		System.out.println("MATRIX STANDARD");
		printMatrix(mStandard);
		*/
		
		
		// 4. To apply the item-to-item approach we need to transpose the matrix
		// We need the movies to be in rows instead of columns
		List<ArrayList<Double>> matrixT = transpose(mStandard);

		
		/*
		// This print checks the transposed matrix
		System.out.println("");
		System.out.println("TRANSPOSED MATRIX");
		printMatrix(matrixT);
		*/
		
		
		// 5. We need to calculate the cosine similarity of each movie with the rest of movies 
		// As we have the transpose matrix we need to calculate the cosine similarity of each row with the others
		// To calculate the cosine similarity we use the dot product
		List<ArrayList<Double>> csMatrix = cosineSimilarity(matrixT);
		
		/*
		// This print checks the cosine similarity matrix
		System.out.println("");
		System.out.println("COSINE SIMILARITY MATRIX");
		printMatrix(csMatrix);
		*/
		
		// 6. The function getSimilarMovies (simulate a movie seen by a user) uses a given movie and a given rating 
		// and makes recommendations based on the rating
		
		// Name of the movie
		String movie = "Toy Story (1995)";
		// Rating of the user
		double rating = 5.0;
		// We need the average of the possible rating values by the user (0.5,1.0,...,4.5,5.0)
		double meanOfRatingValues = 2.5;
		
		Map<String, Double> similarMovies = getSimilarMovies(movie, rating, meanOfRatingValues, csMatrix, films, genres);
		
		
		// This print checks the getSimilarMovies() function
		System.out.println("");
		System.out.println("Recommendation for movie " + movie + " with a rating of " + rating + " : ");
		System.out.println();
		
		//For positive ratings the first recommended movie is the rated movie (since this film is the one that most closely resembles itself)
		for( String name : similarMovies.keySet()) {
			
			System.out.println(name + " " + similarMovies.get(name));
		}
		
		
		
		
		// 7. Testing how the recommendation system works, a user rates several movies and receives a recommendation
		
		// Asking the user the movie and the rating
		System.out.println("");
		System.out.println("Write the name of the movie of the catalog that you have seen and a rating between 1.0 and 5.0 (use 0.5 increments)");
		
		// This variable is used to indicate if the user wants to rate more movies or not
		Boolean finish = false;
		
		// This variable is used to check the movie given by the user
		Boolean correctFilm = false;
		
		// This map stores the new ratings of the user
		Map<String, Double> userRatings = new LinkedHashMap<>();
		
		
		// Printing the catalog of movies
		System.out.println();
		System.out.println("Catalog of movies : ");
		
		for(LinkedList<String> row : films){
			System.out.println(row.get(1));
		}
		
			
			do {
				
				// Asking for the movie
				System.out.println();
				System.out.println("Write the movie : ");
				String film = keyboard.nextLine();
				
				// We check if the movie belongs to the catalog
				while (!correctFilm) {
						
					int movieIndex = 0;
						
					// We check if the movie is in the catalog
					while(movieIndex < films.size() && !films.get(movieIndex).get(1).equals(film)) {
						movieIndex++;
					}
				
					// We check if the movie is in the catalog
					if(movieIndex == films.size()){
							
						// If not we ask again
						System.out.println("The movie must be in the catalog");
							
						// Asking for the movie
						System.out.println("Write the movie : ");
						film = keyboard.nextLine();
				
					} else{
							
						// If yes we change the flag to break the loop
						correctFilm = true;
					}
						
						
				}
					
				// Asking for the rating
				System.out.println("Write your rating : ");
				String rat = keyboard.nextLine();
				
				// Checks if the rating is correct (between 1.0 and 5.0 (0.5 increments)
				while ((!Pattern.matches("[12345]", rat)) && (!Pattern.matches("[1234]\\.[05]", rat)) && (!Pattern.matches("5\\.0", rat))) {	
					
					System.out.println("You must write a number between 1.0 and 5.0 (you can use 0.5 increments)");
						
					// Asking for the rating
					System.out.println("Write your rating : ");
					rat = keyboard.nextLine();

				}
					
				// Asking if the user wants to rate more movies
				System.out.println("Press y to continue and n to finish");
					
				// Storing the rating of the user
				userRatings.put(film, Double.parseDouble(rat));

				// Checking if the user wants to rate more movies
				if (keyboard.nextLine().equals("n")) {
					finish = true;
				}
			
			// Repeat until the user doesn't want to rate more movies
			} while (!finish);
		
		// This print checks the ratings of the user
		System.out.println("Movies rated by the user : ");
		
		for( String name : userRatings.keySet()) {
			
			System.out.println(name + " " + userRatings.get(name));
		}
		
		
		// We make the recommendations using the ratings of the user
		// This map will contain the recommendations for each rated movie
		Map<String, Map<String, Double>> ourRecommendations = new LinkedHashMap<>();
		
		for (String ratedMovie : userRatings.keySet()) {
			
			// Making the recommendations
			Map<String, Double> rMovies = CollaborativeFiltering.getSimilarMovies(ratedMovie, userRatings.get(ratedMovie), 2.5, csMatrix, films, genres);
			
			// Storing the recommendations for each movie
			ourRecommendations.put(ratedMovie, rMovies);
		}	
		
		/*
		 * We have the separate recommendations for each movie, now we have to add those values 
		 * to make a joint recommendation taking into account all the recommendations for each movie.
		 */
		
		// This map stores the results of the joint of all recommendations (the sum)
		Map<String, Double> sumRecommendations = new LinkedHashMap<>();		
		
		// This set will contain the set of movies that appears in any recommendation for the user
		HashSet<String> recommendedMovies = new HashSet<> ();
		
		// Adding the recommended movies to the set
		// For each list of recommendations of each rated movie
		for( Map<String, Double> m : ourRecommendations.values()) {
			
			recommendedMovies.addAll(m.keySet());
			
		}
		
		// For each movie that appears in any of the recommendations for
		// user-rated movies we create an entry in the map
		for( String m : recommendedMovies) {
			
			sumRecommendations.put(m, 0.0);
			
		}

		
		
		// For each recommendation given to the user ratings
		for (Map<String, Double> userRate : ourRecommendations.values()) {
			
			// For each recommended movie
			for (String movieName : userRate.keySet()) {
				
				// We make the sum of the values of the recommendation
				// This variable stores the sum of the values for each movie in each recommendation
				double sum = sumRecommendations.get(movieName) + userRate.get(movieName);
				
				// Here we change this value in the final recommendation map
				sumRecommendations.put(movieName, sum);
				
			}
		}
		
		// This list contains the final recommendations (it not contains the movies that the user have been seen (the rated movies)
		LinkedHashMap<String, Double> finalRecommendation = new LinkedHashMap<>();
		
		// We need the map in a descent order, so we use streams for it
		sumRecommendations.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).forEachOrdered(x -> finalRecommendation.put(x.getKey(), x.getValue()));
				
		// The films that have been seen by the user must be ignored, so we remove that movies
		for(String movieUse : userRatings.keySet()) {
			
			finalRecommendation.remove(movieUse);
			
		}
		
		
		// This print checks the recommendation system
		System.out.println("");
		
		System.out.println("Movies rated by the user : ");
		for( String name : userRatings.keySet()) {
			
			System.out.println(name + " " + userRatings.get(name));
		}
		
		System.out.println("");
		System.out.println("Recommendations for the user based on the given rates : ");
		for( String name : finalRecommendation.keySet()) {
			
			System.out.println(name + " " + finalRecommendation.get(name));
		}
		
		
		
		// This part store the recommendations in a file
		
		// Variables for writing the new text file
		FileWriter file = null;
        PrintWriter pw = null;
        
        try
        {
        	// Toy set
        	//file = new FileWriter("/Users/tomas/Desktop/AI/Implementacion/DataSets/MoviesDataset/toySet/toy_set_recommendations.txt");
        	// Big csv
        	file = new FileWriter("/Users/tomas/Desktop/AI/Implementacion/DataSets/MoviesDataset/recommendations.txt");
            
        	pw = new PrintWriter(file);

            // We write every recommendation
            for( String name : finalRecommendation.keySet()) {
    			
            	pw.println(name + " " + finalRecommendation.get(name));
    		}

        } catch (Exception e) {
        	
            e.printStackTrace();
            
        } finally {
        	
           try {
        	   
	           // Closing the file
	           if (null != file) {
	        	   
	        	   file.close();
	        	   
	           }  
	           
           } catch (Exception e2) {
        	   
              e2.printStackTrace();
              
           }
        }
		
		
	}

}
