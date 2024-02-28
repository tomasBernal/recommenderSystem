package recommendationSystem;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// This class contains all the functions related to reading and writing of files
public abstract class ReaderOfFiles {

	// This variable is the reader that will be used to read the file
	private static BufferedReader reader;

	// This variable will store each line of the file (every row of the csv)
	private static String line;

	// This array will store each line split in words
	private static String parts[];

	// This function extract the name of the movies with the id and the genres (Format : movieId,name,genre:genre...)
	// This function receives the name of the file (fileName) and the divider used in the file (divider)
	// This function returns a list of list, each node represents a movie
	// Each node of the list contains the id [0], the name [1] and the genres [2-last] of the movie
	public static List<LinkedList<String>> getMovies(String fileName, String divider) {

		// Creating the list of list
		LinkedList<LinkedList<String>> movies = new LinkedList<>();

		// This array will contain the genres of the movie
		String genres[];

		// This regular expressions are used for the movies with "" in his name
		String regularExpressionLine = "(\".+,\")(\".+\")(\",.+\")"; //Full line
		String regularExpresionId = "\"(.+),\""; // id of the movie
		String regularExpresionGenres = "\",(.+)\""; // genres of the movie

		try {

			// Creation of the reader
			reader = new BufferedReader(new FileReader(fileName));

			// We read the first line of the file (header)
			line = reader.readLine();

			// We check if is possible to open the file or not
			if (line == null) {

				// If not
				
				// We close the reader
				reader.close();
				
				return null;

			} else {

				// If it's possible

				// The file is read line by line
				while ((line = reader.readLine()) != null) {

					// This list will contain the id, the name and the genres of each movie
					LinkedList<String> row = new LinkedList<>();

					// We need to check the format of the line
					if (line.matches(regularExpressionLine)) {
						
						// If the movie has "" in his name

						// We use this regular expression to get the movie id, name and genres (Full line)
						Pattern pat = Pattern.compile(regularExpressionLine);
						Matcher mat = pat.matcher(line);
						// We use the matcher
						mat.find();

						// We use this regular expression to get the movie id
						Pattern pat2 = Pattern.compile(regularExpresionId);
						Matcher mat2 = pat2.matcher(mat.group(1));
						// We use the matcher
						mat2.find();

						// We use this regular expression to get the movie genres
						Pattern pat3 = Pattern.compile(regularExpresionGenres);
						Matcher mat3 = pat3.matcher(mat.group(3));
						// We use the matcher
						mat3.find();

						// We add the id of the movie to the list
						row.add(mat2.group(1));

						// We add the name of the movie to the list
						row.add(mat.group(2));

						// We store the part with genres of the movie
						String genresWithoutSplit = mat3.group(1);

						// We obtain the genres of the movie
						genres = genresWithoutSplit.split(":");

						// We add the genres to the list
						for (String g : genres) {
							
							row.add(g);
							
						}

					} else {
						// If not

						// We split the line in parts
						parts = line.split(divider);

						// We split the genres
						genres = parts[2].split(":");

						// We add the id to the list
						row.add(parts[0]);

						// We add the name to the list
						row.add(parts[1]);

						// We add the genres to the list
						for (String g : genres) {
							
							row.add(g);
							
						}

					}

					// We add the list (movie data) to the list of list
					movies.add(row);

				}

			}

			// Closing the reader
			reader.close();

			return movies;

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	
	// This function extract the ratings of each user from the preprocesed file (Format: userId,rating,rating...)
	// This function is used to create the matrix of ratings of the users for the movies
	// This function receives the name of the file (fileName) and the divider used in the file (divider)
	// This function return a list of list (matrix), each row represents the ratings of a user for all the movies
	// Unrated movies have a rating of 0.0
		// Each row represents an user
		// Each column represent a movie
	public static List<ArrayList<Double>> readFile(String fileName, String divider) {

		// This structure contains the ratings of each user for all the movies
		ArrayList<ArrayList<Double>> matrix = new ArrayList<>();

		// This variable stores the id of the last user that is rating movies
		int lastUser = 0;

		try {

			// Creation of the reader
			reader = new BufferedReader(new FileReader(fileName));

			// This row contains the ratings of each user for all the movies
			ArrayList<Double> row = new ArrayList<>();

			// We need to analyze the first line separately because we need to know who is the first user
			
			line = reader.readLine();
			
			// We check if is possible to open the file or not
			if (line == null) {

				//If not
				
				// We close the reader
				reader.close();
				
				return null;

			}

			// Each line is splited using the divider
			parts = line.split(divider);

			// We store the first user
			lastUser = Integer.parseInt(parts[0]);

			// 2 is rating in the file (the rating of the user)
			// Checking if the rating is correct

			if (Pattern.matches("[12345]", parts[2]) || Pattern.matches("[1234]\\.[05]", parts[2])
					|| Pattern.matches("5\\.0", parts[2])) {

				// Conversion of the string with the rate to double
				row.add(Double.parseDouble(parts[2]));

			} else {

				// If it is not we can consider that there is no rating
				row.add(0.0);
			}

			// The file is read line by line
			while ((line = reader.readLine()) != null) {

				// Each line is splited using the divider
				parts = line.split(divider);

				// We check if the actual rating is from the same user that the last
				if (lastUser != Integer.parseInt(parts[0])) {

					// If the actual user is a new user

					// We store the row from the last user
					matrix.add(row);

					// We create the row for the new user
					row = new ArrayList<>();

					// We change the info about the last user
					lastUser = Integer.parseInt(parts[0]);

				}

				// 2 is rating in the file (the rating of the user)
				// Checking if the rating is correct
				if (Pattern.matches("[12345]", parts[2]) || Pattern.matches("[1234]\\.[05]", parts[2])
						|| Pattern.matches("5\\.0", parts[2])) {

					// Conversion of the string with the rate to double
					row.add(Double.parseDouble(parts[2]));

				} else {

					// If it is not we can consider that there is no rating
					row.add(0.0);
				}

			}

			// We store the row from the last user
			matrix.add(row);

			// The reader is closed
			reader.close();

			return matrix;

		} catch (Exception e) {

			e.printStackTrace();
			return null;

		}

	}

	
	// This function creates a preprocesed file that has the ratings of each user for all movies in the same line
	// Its the first step for obtain the matrix of ratings
	// This function extract the ratings of the user from a file (Format : userId,movieId,rating)
	// This function receives the name of the file (fileName), the divider used in the file (divider), the list of movies (movies) and the path for the file (path)
	// This function creates the pre-procesed file with the following format (Format: userId,rating,rating...)
	public static void extractContent(String fileName, String divider, List<LinkedList<String>> movies, String path) {

		// This list is of UserRating object
		// The UserRating object contains the userId and a list of Ratings objects
		// Each Rating object contains the movieId and the rating for the movie
		LinkedList<UserRating> matrix = new LinkedList<>();

		// This variable contains the id of the last user
		int lastUser = 0;

		// This variable contains the index in the matrix of the last user
		int actualUser = 0;

		try {

			// Creation of the reader
			reader = new BufferedReader(new FileReader(fileName));

			// The header row is readed
			line = reader.readLine();

			// We check if is possible to open the file or not
			if (line == null) {

				// If not
				
				// We close the reader
				reader.close();

				// We stop reading
				return;

			}

			// We need to analyze the first line separately because we need to know who is the first user
			line = reader.readLine();

			// This variable stores the rating of the user for the actual movie
			Double rating;

			// Each line is splited using the divider
			parts = line.split(divider);

			// 2 is rating in the file (the rating of the user)
			// Checking if the rating is correct
			if (Pattern.matches("[12345]", parts[2]) || Pattern.matches("[1234]\\.[05]", parts[2])
					|| Pattern.matches("5\\.0", parts[2])) {

				// Conversion of the string with the rate to double
				rating = Double.parseDouble(parts[2]);

			} else {

				// If it is not we can consider that there is no rating
				rating = 0.0;
			}

			// We create the list of ratings of the user
			LinkedList<Rating> movieRating = new LinkedList<>();

			// We add the first rating to the list
			Rating newRating = new Rating(parts[1], rating);
			movieRating.add(newRating);

			// We add the user to the structure
			matrix.add(new UserRating(Integer.parseInt(parts[0]), movieRating));

			// We store the id of the first user
			lastUser = Integer.parseInt(parts[0]);

			// The file is read line by line
			while ((line = reader.readLine()) != null) {

				// Each line is splited using the divider
				parts = line.split(divider);

				// 2 is rating in the file (the rating of the user)
				// Checking if the rating is correct
				if (Pattern.matches("[12345]", parts[2]) || Pattern.matches("[1234]\\.[05]", parts[2])
						|| Pattern.matches("5\\.0", parts[2])) {

					// Conversion of the string with the rate to double
					rating = Double.parseDouble(parts[2]);

				} else {

					// If it is not we can consider that there is no rating
					rating = 0.0;
				}

				// We check if the actual rating is from the same user than the last
				if (lastUser != Integer.parseInt(parts[0])) {

					// If the actual user is a new user of our structure

					// We create the list of ratings of the user
					movieRating = new LinkedList<>();
					
					// We add the actual rating to the list
					newRating = new Rating(parts[1], rating);
					movieRating.add(newRating);

					// We add the user to the structure
					matrix.add(new UserRating(Integer.parseInt(parts[0]), movieRating));

					// We change the info about the last user
					lastUser = Integer.parseInt(parts[0]);

					// We change the index that represents the last user in the matrix
					actualUser++;

				} else {
					// If the user has not changed

					// First we get the row of the actual user (using the index)
					// Secondly we get the list of ratings
					// Finally we add the new rating
					matrix.get(actualUser).getRatingList().add(new Rating(parts[1], rating));

				}

			}

			// Variables for writing the new preprocesed file
			FileWriter file = null;
			PrintWriter pw = null;

			try {
				
				file = new FileWriter(path);
				
				pw = new PrintWriter(file);

				// For each user (row of the matrix)
				for (UserRating u : matrix) {

					// We obtain his list of ratings
					LinkedList<Rating> list = u.getRatingList();

					// For each movie (of the list of movies)
					for (LinkedList<String> movie : movies) {

						boolean flag = false;

						// We check if the movie is in the list (if the user has a rating)
						int index = 0;

						// This loop scroll through the list of ratings
						while (index < list.size() && flag != true) {
							
							// We obtain each rating of the list
							Rating r = list.get(index);

							// We check the movie rated
							if (r.getMovieName().equals(movie.get(0))) {

								// If the movie is in the list of movies
								flag = true;
								
								// We break the loop
								break;
							}

							index++;
						}

						// We check if the rating for that movie exist
						if (flag == true) {
							
							// If yes, we add the rating of the user
							pw.println(u.getUserId() + "," + movie.get(0) + "," + list.get(index).getMovieRate());
							
						} else {
							
							// If not we add a rating of 0.0
							pw.println(u.getUserId() + "," + movie.get(0) + ",0.0");
							
						}
					}

				}

			} catch (Exception e) {

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

			// The reader is closed
			reader.close();

		} catch (Exception e) {

			e.printStackTrace();

		}
	}
	
	
	// This function ask the user his/her ratings (Its interactive)
	// This function receives the list of films (films) and the scanner needed to get user data (keyboard)
	// This function returns a map with the ratings of the user
	public static Map<String, Double> getRatingsUser(Scanner keyboard, List<LinkedList<String>> films){
		
		// This map will contain the ratings of the user
		Map<String, Double> userRatings = new LinkedHashMap<>();
		
		// This variable is used to indicate if the user wants to rate more movies or not
		Boolean finish = false;
		
		// This variable is used to check the movie given by the user
		Boolean correctFilm = false;
		
		
		do {
			
			// Asking for the movie
			System.out.println();
			System.out.println("Write the movie that you want to rate : ");
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
					System.out.println("Write the movie that you want to rate : ");
					
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
				keyboard.close();
			}
		
		// Repeat until the user doesn't want to rate more movies
		} while (!finish);
		
		return userRatings;
	}
		
		
	// This function obtains the user ratings from a file (Its not interactive)
	// This function receives the list of films (films) and path of the file with the user ratings (path)
	// This function returns a map with the ratings of the user
	public static Map<String, Double> getRatingsFile(List<LinkedList<String>> films, String path){
		
		// This map will contain the user ratings
		Map<String, Double> userRatings = new LinkedHashMap<>();
		
		// This variables are used when the file is read
		File file = null;
		FileReader fr = null;
		BufferedReader br = null;

		try {
			// Opening the file
			file = new File (path);
			
			// Creating the reader
			fr = new FileReader (file);
			br = new BufferedReader(fr);

			// Stores each line of the file
			String line;
			
			// Each line is splited using the divider
			String[] parts;
						
			// Reading the file
			while((line=br.readLine())!= null){
				
				// We split each line
				parts  = line.split("<");
				
				// This variable is used to search the film in the catalog
				int movieIndex = 0;
				
				// We check if the movie is in the catalog
				while(movieIndex < films.size() && !films.get(movieIndex).get(1).equals(parts[0])) {
					movieIndex++;
				}
				
				// If the movie is in the catalog
				if(movieIndex < films.size() ) {
					
					// We check if the rating is correct
					if (Pattern.matches("[12345]", parts[1]) || Pattern.matches("[1234]\\.[05]", parts[1])
							|| Pattern.matches("5\\.0", parts[1])) {

						// Conversion of the string with the rate to double
						userRatings.put(parts[0], Double.parseDouble(parts[1]));

					}
				}
				
				
			}
				
		}
		catch(Exception e){
			
			e.printStackTrace();
			
		}finally{
			
			// Closing the reader and the file
			try{                    
				if( null != fr ){  
				
					fr.close();  
					
				}    
				
			}catch (Exception e2){ 
				e2.printStackTrace();
			}
		}
		
		return userRatings;
	}

	
	// This function generates a random ratings file
	// This function receives the list of films (films) and the number of ratings to be created (numberOfRatings)
	public static void generateRatings(List<LinkedList<String>> films, int numberOfRatings, String path) {
		// Here we store the genres that the user hates
		FileWriter file = null;
        PrintWriter pw = null;
        
        // This set will contain the movies added to the rating
        HashSet<Integer> addedMovies = new HashSet<Integer>();
        
        try
        {
        	// Creating the file
        	file = new FileWriter(path);
            pw = new PrintWriter(file);

            // For the number of wanted ratings
            for(int i = 0; i < numberOfRatings; i++) {
            	
            	// First we choose a movie randomly
            	int movieId = (int)(Math.random()*films.size());
                
            	// If the movie is not rated yet
            	if(!addedMovies.contains(movieId)) {
            		
            		// We choose a rating
                    int rating = (int)(Math.random()*5+1);
                    
                    // We choose if the rating is .0 or .5
                    int rating2 = (int)(Math.random()*2+1);
                    
                    // We add the rating to the file
                    if(rating == 5) {
                    	pw.println(films.get(movieId).get(1)+"<"+rating+".0");
                    } else if (rating2 % 2 == 0) {
                    	pw.println(films.get(movieId).get(1)+"<"+rating+".0");
                    } else {
                    	pw.println(films.get(movieId).get(1)+"<"+rating+".5");
                    }
                    
                    // We add the movie to the set
                    addedMovies.add(movieId);
                    
            	} else {
            		
            		i--;
            		
            	}
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

	
	
	// This function generates a random genres file
	// This function receives the list of genres (genres) and the number of genres to be created (numberOfGenres)	
	public static void generateGenres(LinkedList<String> genres, int numberOfGenres, String path) {
		
		// Here we store the genres that the user hates
		FileWriter file = null;
        PrintWriter pw = null;
        
        // This set will contain the genres added to the file
        HashSet<String> addedGenres = new HashSet<String> ();
        
        try
        {
        	// Creating the file
        	file = new FileWriter(path);
            pw = new PrintWriter(file);

            // For the number of wanted genres
            for(int i = 0; i < numberOfGenres; i++) {
            	
            	// First we choose a genre randomly
            	int genre = (int)(Math.random()*genres.size());
                
            	// If the genre is not hated yet
            	if(!addedGenres.contains(genres.get(genre))) {
            		
            		//We add the genre to the file
            		pw.println(genres.get(genre));
            		
            		//We add the genre to the set
            		addedGenres.add(genres.get(genre));
            		
            	} else {
            		i--;
            	}
            	
               
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
}