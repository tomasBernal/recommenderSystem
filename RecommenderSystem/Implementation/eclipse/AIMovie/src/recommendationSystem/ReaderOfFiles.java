package recommendationSystem;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

public abstract class ReaderOfFiles {
	
	// This variable is the reader that will be used to read the file
	private static BufferedReader reader;
	
	// This variable will store each line of the file (every row of the csv)
	private static String line;
	
	// This array will store each line split in words
	private static String parts[];
	
	// This function extract the name of the movies with the id
	public static List<LinkedList<String>> getMovies(String fileName, String divider){
		
		// Stores the name and the id of the movies
		LinkedList<LinkedList<String>> movies = new LinkedList<>();
		
		// This array contains the genres of the movie
		String genres[];
		
		try {
			
			// Creation of the reader
			reader = new BufferedReader(new FileReader(fileName));
			
			// We read the first line (header)
			line = reader.readLine();
	    	
			// We check if the file is full or not
			if( line == null) {
				
				return null;
				
			} else { 
	    				
				// If the file is not empty
				
				// The file is read line by line
		        while ((line = reader.readLine()) != null) {
		        	
		        	// This list contains the name and the genres of each movie
		        	LinkedList<String> row = new LinkedList<>();
		        	
		        	// We split the line in parts
		    		parts = line.split(divider);
		    		
		    		// We split the genres
		    		genres = parts[2].split(":");
		    		
		    		// We add the id
		    		row.add(parts[0]);
		    		
		    		// We add the name
		    		row.add(parts[1]);
		    		
		    		// We add the genres
		    		for(String g : genres) {
		    			row.add(g);
		    		}
		    		
		    		
		    		// We add the name of the movie and the genres to the list of movies
		    		movies.add(row);
		    		
		    		
		        }
		        
	    	}
			
			// Clossing the reader
	    	reader.close();
	    	
	    	return movies;
	    	
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	// This function extracts the content of a csv file in full lines
	public static List<ArrayList<Double>> readFile(String fileName, String divider) {

			// This structure contains the ratings of each user in rows
			ArrayList<ArrayList<Double>> matrix = new ArrayList<>();
			
			int lastUser = 0;
			
			try {
				
				// Creation of the reader
		    	reader = new BufferedReader(new FileReader(fileName));
		    	
		    	// This row contains the ratings of each user
	        	ArrayList<Double> row = new ArrayList<>();
	        	
		    	// We need to analize the first line separately because we need to know
		    	// who is the first user for the checking of the last user
		    	line = reader.readLine();
		    	
		    	// Each line is splited using the divider
				parts = line.split(divider);
				
				// We store the first user
				lastUser = Integer.parseInt(parts[0]);
				
				// 2 is rating in the file (the rating of the user)
				// Checking if it is an integer or a double	
				
				if(Pattern.matches("[12345]", parts[2]) || Pattern.matches("[1234]\\.[05]", parts[2]) || Pattern.matches("5\\.0", parts[2])) {
							   		   
					//Conversion of the string with the rate to double
				    row.add(Double.parseDouble(parts[2]));
								        	   
				}else {
							        		   
				    //If it is not we can consider that there is no rating
				   row.add(0.0);
				}
				
		        // The file is read line by line
		        while ((line = reader.readLine()) != null) {
		        	
		        	// Each line is splited using the divider
		        	parts = line.split(divider);
		           
		        	// We check if the actual rating is from the same user that the last
				   	if(lastUser != Integer.parseInt(parts[0])) {
				   		
				   		// If the actual user is a new user
				   		
				   		// We store the row from the last user
				   		matrix.add(row);
				   		   
				   		// We create the row for the user
				        row = new ArrayList<>();
						   		
					   	// We change the info about the actual user
				        lastUser = Integer.parseInt(parts[0]);
					   	
				   	}
				   	   
		        	// 2 is rating in the csv (the rating of the user)
		        	// Checking if it is an integer or a double 
				   	if(Pattern.matches("[12345]", parts[2]) || Pattern.matches("[1234]\\.[05]", parts[2]) || Pattern.matches("5\\.0", parts[2])) {
							   
						//Conversion of the string with the rate to double
					    row.add(Double.parseDouble(parts[2]));
									        	   
					}else {
								        		   
					    //If it is not we can consider that there is no rating
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
	
	// This function extract the ratings of a csv file, its the first step
	// for obtain the matrix of ratings
	public static void extractContent(String fileName, String divider, List<LinkedList<String>> movies) {
		
		// This structure contains all the users and all the movies, it contains the ratings of each user
		// or 0.0 if the user has not rated a movie
		LinkedList<UserRating> matrix = new LinkedList<>();
					
		// This variable contains the id of the last user
		int lastUser = 0;
					
		// This variable contains the index in the structure of the last user
		int actualUser = 0;
					
		try {
						
		// Creation of the reader
	   	reader = new BufferedReader(new FileReader(fileName));
				    		    	
	   	// The header row is readed
	  	line = reader.readLine();
				    	
	  		if( line == null) {
				    	
	  			// We close the reader
	  			reader.close();
	  			
	  			// We stop reading
	    		return;
				    		
	    	}			    	
				    	
	  		// We need to analize the first line separately because we need to know
	    	// who is the first user for the checking of the last user
	    	line = reader.readLine();
				    	
	    	// This variable stores the rating of the user for the actual movie
	    	Double rating;
				    	
	    	// Each line is splited using the divider
			parts = line.split(divider);
				           
			// 2 is rating in the csv (the rating of the user)
			// Checking if it is an integer or a double 
			if(Pattern.matches("[12345]", parts[2]) || Pattern.matches("[1234]\\.[05]", parts[2]) || Pattern.matches("5\\.0", parts[2])) {
					   
				//Conversion of the string with the rate to double
			    rating = Double.parseDouble(parts[2]);
							        	   
			}else {
						        		   
			    //If it is not we can consider that there is no rating
			    rating = 0.0;
			}
						   
			  	// We create the first node of the list of ratings of the user
			   	LinkedList<Rating> movieRating = new LinkedList<> ();
					   	
			   	Rating newRating = new Rating(parts[1], rating);
			   	movieRating.add(newRating);
					   	
			  	// We add the node of the user to the structure
			  	matrix.add(new UserRating(Integer.parseInt(parts[0]), movieRating));
					   				   	
				lastUser = Integer.parseInt(parts[0]);
					   	   
			    // The file is read line by line
			    while ((line = reader.readLine()) != null) {
				        	
			    	// Each line is splited using the divider
				    parts = line.split(divider);
				           
				    // 2 is rating in the csv (the rating of the user)
					// Checking if it is an integer or a double 
				    if(Pattern.matches("[12345]", parts[2]) || Pattern.matches("[1234]\\.[05]", parts[2]) || Pattern.matches("5\\.0", parts[2])) {
						   		   
						//Conversion of the string with the rate to double
						rating = Double.parseDouble(parts[2]);
							        	   
					}else {
						        		   
				       //If it is not we can consider that there is no rating
				       rating = 0.0;
				    }

			   	   // We check if the actual rating is from the same user that the last
			   	   if(lastUser != Integer.parseInt(parts[0])) {
					   
			   		   // If the actual user is a new user of our structure
					   		   
			   		   // We create the first node of the list of ratings of the user
			   		   movieRating = new LinkedList<> ();
			   		   movieRating.add(new Rating(parts[1], rating));
							   	
			   		   // We add the node of the user to the structure
			   		   matrix.add(new UserRating(Integer.parseInt(parts[0]), movieRating));
						   	
			   		   // We change the info about the actual user
			   		   lastUser = Integer.parseInt(parts[0]);
						   	
			   		   actualUser++;
					   	   
			   	   } else {
			   		   // If the user has not changed
					   		   
			   		   // First we get the node of the actual user
			   		   // Secondly we get the list of ratings
			   		   // Finally we add the rating
			   		   matrix.get(actualUser).getRatingList().add(new Rating(parts[1], rating));
							   						   
			   	   }
				      	   
			    }
				
			    // Variables for writing the new csv file
			    FileWriter file = null;
		        PrintWriter pw = null;
		        
		        try
		        {
		        	// Toy set
		        	//file = new FileWriter("/Users/tomas/Desktop/AI/Implementacion/DataSets/MoviesDataset/toySet/toy_set_pre-procesed.txt");
		        	// Big csv
		            file = new FileWriter("/Users/tomas/Desktop/AI/Implementacion/DataSets/MoviesDataset/pre-procesed.txt");
		           
		            pw = new PrintWriter(file);

		         // For each user
				 for(UserRating u : matrix) {
				       	    	
					 // We obtain his list of ratings
					 LinkedList<Rating> list = u.getRatingList();
					        	
					 // For each movie
					 for(LinkedList<String> movie : movies) {
					      
						 boolean flag = false;
						 
						 // We check if the movie is in the list
						 int index = 0;
						 
						 while(index < list.size() && flag != true) {
							 
							 Rating r = list.get(index);
							 
							// If not we need to add the rating for that movie
							 if(r.getMovieName().equals(movie.get(0))) {
				        				
								 // So we use this variable
								 flag = true;
								 break;
				      		}
							 
							index++;
						 }
						
						// We check if the rating exist
						if(flag == true) {
							pw.println(u.getUserId() +","+movie.get(0)+","+list.get(index).getMovieRate());
						} else {
							pw.println(u.getUserId() +","+movie.get(0)+",0.0");
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
	
	// This function print a line of a file with a format
	public static void printLine(ArrayList<Double> row) {
		
		//we print every part dividing it by | 
		for(int i = 0; i < row.size(); i++) {
			System.out.print(row.get(i) + "\t | \t");
		}
		//We make a jump to the next line
		System.out.println();
	}
	
	// This function print the header of a file
	public static void printHeader() {
		
		//we print every part dividing it by | 
		System.out.print("              ");
		for(int i = 0; i < parts.length; i++) {
			System.out.print(parts[i] + "\t | ");
		}
		//We make a jump to the next line
		System.out.println();
	}
}