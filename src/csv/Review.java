/**
 * read the reviews from file and write in Table Reviews in DB
 * @version 21-06-02
 */
package csv;


import java.sql.PreparedStatement;
import java.sql.SQLException;

import java.io.FileNotFoundException;
import java.sql.Date;

import JDBCTools.JDBCTool;



public class Review{

    private CSV csvFile;

    public Review() throws FileNotFoundException{
        csvFile = new CSV();
        csvFile.readFile();
    }

    /**
     * Function to write every String[] aus csvFile in die DB Tabelle
     */
    public void writeReviewInDB(){
        for(String[] review : csvFile.getFile()){
            try{
                JDBCTool.executeUpdate((con, st) ->	{
                    String sql = "INSERT INTO review(item_id, customer_id, review_date, summary, content, rating) values (?,?,?,?,?,?)";
                    PreparedStatement ps = con.prepareStatement(sql);
                    ps.setString(1, review[0]); 
                    ps.setString(2, review[4]); 
                    ps.setDate(3, Date.valueOf(review[3])); 
                    ps.setString(4, review[5]); 
                    ps.setString(5, review[6]); 
                    ps.setInt(6, Integer.valueOf(review[1]));
                    ps.executeUpdate();		
                    ps.close();
                });
            }catch(SQLException sqle){
                System.out.println("SQL_Exception while writing Review to Table: " + review[0]);
            }catch(Exception e){
                System.out.println("Other Exception while writing Review to Table");
            }
        }
        System.out.println("\033[1;34m*\033[35m*\033[33m*\033[32m* \033[91m reviews fully written \033[32m*\033[33m*\033[35m*\033[34m*\033[0m");
    }
    

    public static void main(String[] args){
        try{
            Review newReview = new Review();
            newReview.writeReviewInDB();
        }catch(FileNotFoundException f){
            
        }
    }


}