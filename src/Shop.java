import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Shop {
	

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			Class.forName("org.postgresql.Driver");

			String url = "jdbc:postgresql://localhost/postgres?user=postgres&password=postgres&ssl=false";
			Connection conn = DriverManager.getConnection(url);
			
			Statement st = conn.createStatement();
			//st.executeQuery("insert into author(name, test) values('Kim', 30)");
			//st.executeQuery("drop table Author");
			/*
			ResultSet rs = st.executeQuery("show all");
			while (rs.next())
			{
			    //System.out.print("Column 1 returned ");
			    System.out.print(rs.getString(1));
			    System.out.print(rs.getString(2));
			    System.out.println(rs.getString(3));
			}
			rs.close();
			 */
			st.close();

		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
