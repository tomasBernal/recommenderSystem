package recommendationSystem;

import java.io.BufferedReader;
import java.io.FileReader;
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


//This class implements the necessary functions to apply the collaborative filtering
public class CollaborativeFiltering {

	// This function makes the calculation of the length of a vector
	// ||V|| = sqrt(sum[1,n](pow(Vi,2))
	// This function receives the matrix of ratings (matrix) and the index of the row (v) that has to be calculated
	// This function returns the length of the vector
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
	
	
	// This function makes the calculation of the cosine similarity of a matrix
	// CosSim(A,B) = cos(0) = (A x B) / (||A|| x ||B||)
	// A x B = sum[1,n](Ai x Bi) = (A1 x B1) + (A2 x B2) + ... + (An x Bn)
	// ||A|| = sqrt(sum[1,n](pow(Ai,2))
	// ||B|| = sqrt(sum[1,n](pow(Bi,2))
	// This function receives the matrix of ratings (matrix) from which the calculations are to be carried out
	// This function returns the matrix given as a parameter but with the calculation of the cosine similarity	
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
	 * For this we have to get the row of the matrix of the given movie and multiply the row with the rating of the user minus the fixer (threshold)
	 * What this function returns is a list with the recommendations (these elements are returned sorted in decreasing order by their recommendation value)
	 * For positive ratings the first recommended movie is the rated movie (since this film is the one that most closely resembles itself)
	 */
	// This function receives the name of the rated movie (ratedMovie), the rating (rating), the fixer for the calculation (fixer), the matrix of ratings (matrix) and the list of movies (movies)
	// This function returns a map with the recommendations for the user
	public static  Map<LinkedList<String>, Double>  getSimilarMovies(String ratedMovie, double rating, double fixer, List<ArrayList<Double>> matrix, List<LinkedList<String>> movies) {
		
		// This map contains the recommendation list
		HashMap<LinkedList<String>, Double> similarMovies = new HashMap<>();
		
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
				
		// For each rating of the users
		// All the columns have the same size, so we use matrix.get(0).size()
		for (int j = 0; j < matrix.get(0).size(); j++) {
					
			// The new values (recommendations values) are the result of multiply the ratings of each movie
			// with the rating of the user minus a number call fixer (the threshold)
			// We put the name of the movie and the new value in the map
			similarMovies.put(movies.get(j), matrix.get(movieIndex).get(j) * (rating - fixer));
			
				
		}
				
		return similarMovies;
	}
	
	
	// This function creates the preprocesed file of the cosine similarity
	// This function need to be used each time we change the dataset
	public static void processCosine(String path, String path2) {
		
		// 1. We create a structure with the ratings of the users
		// Movies with no rating have been rated with 0.0
		List<ArrayList<Double>> matrix;
		
		matrix = ReaderOfFiles.readFile(path, ",");
		
		/*
		// This print checks the rating matrix
		System.out.println("");
		System.out.println("MATRIX:");
		MatrixFuncionality.printMatrix(matrix);
		*/
		
		// 2. We need to standardized the matrix
		// Standardized value = (value - mean value of the row) / ( maximum value of the row - minimal value of the row)
		List<ArrayList<Double>> mStandard = MatrixFuncionality.standardizeMatrix(matrix);

				
		/*
		// This print checks the standardized matrix
		System.out.println("");
		System.out.println("MATRIX STANDARD");
		MatrixFuncionality.printMatrix(mStandard);
		*/
				
				
		// 3. To apply the item-to-item approach we need to transpose the matrix
		// We need the movies to be in rows instead of columns
		List<ArrayList<Double>> matrixT = MatrixFuncionality.transpose(mStandard);

				
		/*
		// This print checks the transposed matrix
		System.out.println("");
		System.out.println("TRANSPOSED MATRIX");
		MatrixFuncionality.printMatrix(matrixT);
		*/
		
		// 4. We need to calculate the cosine similarity of each movie with the rest of movies 
		// As we have the transpose matrix we need to calculate the cosine similarity of each row with the others
		// To calculate the cosine similarity we use the dot product
		List<ArrayList<Double>> csMatrix = cosineSimilarity(matrixT);
		
		
		// Here we store the cosine similarity in a file
		FileWriter file = null;
		PrintWriter pw = null;
        
        try
        {
        	file = new FileWriter(path2);
            pw = new PrintWriter(file);

            for(ArrayList<Double> row : csMatrix) {
            	
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
	
	
	public static List<ArrayList<Double>> getCosine(String path){
	
		List<ArrayList<Double>> csMatrix = new ArrayList<>();
		
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
				
				csMatrix.add(row);
				
				
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

		return csMatrix;
	}



	// This function makes a recommendation based on the ratings of a user and the other user's tastes
	// This function implements the collaborative filtering
	// This function receives the map of the user ratings (userRatings), the list of movies (films) and the path of the preprocesedCosineMatrix (path)
	// This function returns a list of Ratings, that is, the recommendations (a list of objects with the movie name and the value of the recommendation)	
	public static LinkedList<LinkedList<String>> collaborativeFiltering(Map<String, Double> userRatings, List<LinkedList<String>> films, String path){
		
		
		// From the Toy Dataset
		//CollaborativeFiltering.processCosine(path, Hybrid.pathDatasets+"ToySet/cosine.txt");
		// From the MovieLens Dataset		
		//CollaborativeFiltering.processCosine(path, Hybrid.pathDatasets+"MovieLens/cosine.txt");
		
		// From the Toy Dataset
		//List<ArrayList<Double>> csMatrix = getCosine(Hybrid.pathDatasets+"ToySet/cosine.txt");	
		// From the MovieLens Dataset
		List<ArrayList<Double>> csMatrix = getCosine(Hybrid.pathDatasets+"MovieLens/cosine.txt");	
		
		/*
		// This print checks the cosine similarity matrix
		System.out.println("");
		System.out.println("COSINE SIMILARITY MATRIX");
		MatrixFuncionality.printMatrix(csMatrix);
		*/
		
		
		// We make the recommendations using the ratings of the user
		// This map will contain the recommendations for each rating of the user <movieName, recommendationValue>
		Map<String, Map<LinkedList<String>, Double>> ourRecommendations = new LinkedHashMap<>();
		
		// For each rating of the user
		for (String ratedMovie : userRatings.keySet()) {
					
			// Making the recommendations for each rating
			Map<LinkedList<String>, Double> rMovies = CollaborativeFiltering.getSimilarMovies(ratedMovie, userRatings.get(ratedMovie), 2.5, csMatrix, films);
					
			// Storing the recommendations for each rating 
			ourRecommendations.put(ratedMovie, rMovies);
		}
		
		
		/*
		 * We have the separate recommendations for each rating, now we have to add those values 
		 * to make a joint recommendation taking into account all the recommendations for each rating.
		 */
		
		// This map stores the results of the joint of all recommendations (the sum)
		Map<LinkedList<String>, Double> sumRecommendations = new LinkedHashMap<>();		
		
		// This set will contain the set of movies that appears in any recommendation for the user
		HashSet<LinkedList<String>> recommendedMovies = new HashSet<> ();
		
		// Adding the recommended movies to the set
		// For each list of recommendations of each rated movie
		for( Map<LinkedList<String>, Double> m : ourRecommendations.values()) {
			
			//We add the recommended movie to the set
			recommendedMovies.addAll(m.keySet());
			
		}
		
		
		// For each movie that appears in any of the recommendations
		// We create an entry on the map of joint recommendation
		for( LinkedList<String> m : recommendedMovies) {
			
			sumRecommendations.put(m, 0.0);
			
		}

	
		// For each recommendation given to the each user rating
		for (Map<LinkedList<String>, Double> userRate : ourRecommendations.values()) {
			
			// For each recommended movie
			for (LinkedList<String> movieName : userRate.keySet()) {
				
				// We make the sum of the value of recommendation for the movie
				// This variable stores the sum of the values of recommendation for each movie in all recommendations
				double sum = sumRecommendations.get(movieName) + userRate.get(movieName);
				
				// Here we put the value in the joint recommendation map
				sumRecommendations.put(movieName, sum);
				
			}
		}
		
		// This list contains the final recommendations (it will not contain the movies that the user have been seen (the rated movies))
		LinkedList<LinkedList<String>> finalRecommendation = new LinkedList<LinkedList<String>>();
		
		// We need to order the map in a descent order, so we use streams for it
		sumRecommendations.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).forEachOrdered(x -> finalRecommendation.add(x.getKey()));
		
		// This list will contain the list of movies seen (rated) by the user
		LinkedList<LinkedList<String>> seenMovies = new LinkedList<LinkedList<String>>();
		
		
		// The films that have been seen (rated) by the user must be ignored, so we remove that movies
		// For each seen (rated) movie
		for(String rm : userRatings.keySet()) {
					
			// For each recommendation
			for(LinkedList<String> m : finalRecommendation) {
								
				// We check if the recommended movie is the same that the seen (rated) movie
				if((rm.equals(m.get(1)))){
							
					// If yes
							
					// We add the movie to the list of seen movies
					seenMovies.add(m);
							
				}
			}
							
		}
				
		// We remove the seen movies
		finalRecommendation.removeAll(seenMovies);
		
		// We return the recommendation of the collaborative filtering
		return finalRecommendation;
		
	}
	
	
}
