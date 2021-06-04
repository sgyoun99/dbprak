/**
 * read the reviews from file and write in Table Reviews in DB
 * @version 21-06-02
 */
package csv;


import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.Date;

import java.util.HashMap;

import JDBCTools.JDBCTool;
import main.ErrorLogger;
import main.ErrType;



public class Review{

    private CSV csvFile;
    private HashMap<String, Double> ratingHM = new HashMap<>();;

    public Review() {
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
                    String sql = "INSERT INTO review(review_id, item_id, customer, review_date, summary, content, rating) values (DEFAULT,?,?,?,?,?,?)";
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
                //System.out.println("SQL_Exception while writing Review to Table: " + review[0]);
                ErrorLogger.write("Review", review[0], ErrType.SQL, "", sqle,"SQL_Exception while writing Review to Table" + review[0]);
            }catch(Exception e){
                //System.out.println("Other Exception while writing Review to Table");
                ErrorLogger.write("Review", review[0], ErrType.PROGRAM, "", e, "Other Exception while writing Review to Table" + review[0]);
            }
        }
        System.out.println("\033[1;34m*\033[35m*\033[33m*\033[32m* \033[91m reviews fully written \033[32m*\033[33m*\033[35m*\033[34m*\033[0m");
    }
    
    /**
     * Superfunction for getting ratings from the DB table review and adding them to items
     */
    public void addRatings(){
        getRating();
        setRating();
    }

    /**
     * Function to get teh averages of the ratings from the DB (review) and add them to the Review HashMap
     */
    private void getRating() {
        try{
            Connection con = JDBCTool.getConnection();
            con.setAutoCommit(false);
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("Select item_id, CAST(ROUND(AVG(rating),1) AS DEC(10,1)) rr FROM review GROUP BY item_id ORDER BY item_id");
         
            while(rs.next()){
                ratingHM.put(rs.getString("item_id"), rs.getDouble("rr"));
            }
            con.close();
        
        }catch(SQLException e){
            //System.out.println("Exception");
            ErrorLogger.write("Review", "", ErrType.SQL, "", e, "SQL_Exception while getting Rating from DB");
        }
    }

    /**
     * add Ratings from Review HashMap to DB item
     */
    private void setRating(){
        for(HashMap.Entry<String,Double> set : ratingHM.entrySet()){
            try{
                JDBCTool.executeUpdate((con, st) ->	{
                    String sql = "UPDATE item SET rating = ? WHERE item_id = ?";
                    PreparedStatement ps = con.prepareStatement(sql);
                    ps.setDouble(1, set.getValue()); 
                    ps.setString(2, set.getKey()); 
                    ps.executeUpdate();		
                    ps.close();
                });
            }catch(SQLException sqle){
                //System.out.println("SQL_Exception while writing Rating to Item");
                ErrorLogger.write("Review", set.getKey(), ErrType.SQL, "", sqle, "SQL_Exception while writing Rating to Item");
            }catch(Exception e){
                //System.out.println("Other Exception while writing Rating to Item");
                ErrorLogger.write("Review", set.getKey(), ErrType.PROGRAM, "", e, "other Exception while writing Rating to Item");
            }
        }
        System.out.println("\033[1;34m*\033[35m*\033[33m*\033[32m* \033[91m Rating added to items \033[32m*\033[33m*\033[35m*\033[34m*\033[0m");
    }
    

    public static void main(String[] args){
        Review newReview = new Review();
        newReview.writeReviewInDB();
    }


}