package JDBCTools;
import java.sql.*;

public class TestJDBC {

	public static void main(String[] args) {
		
		try {
			Class.forName("org.postgresql.Driver");

			String url = "jdbc:postgresql://localhost/postgres?user=postgres&password=postgres&ssl=false";
			Connection conn = DriverManager.getConnection(url);
			
			Statement st = conn.createStatement();
			st.executeUpdate("create table JDBC_TEST(name varchar(255), age int);");
			st.executeUpdate("insert into JDBC_TEST(name, age) values ('Kim', 10);");
			st.executeUpdate("insert into JDBC_TEST(name, age) values ('Yun', 11);");
			st.executeUpdate("insert into JDBC_TEST(name, age) values ('Park', 11);");
			ResultSet rs = st.executeQuery("SELECT * FROM JDBC_TEST WHERE age = 11;");
			while(rs.next()) {
				System.out.println(rs.getString(1));
			}
			st.executeUpdate("drop table JDBC_TEST;");
			rs.close();

			/*
			ResultSet rs = st.executeQuery("SHOW ALL;");
			while (rs.next())
			{
			    System.out.print(rs.getString(1)+"\t");
			    System.out.print(rs.getString(2)+"\t");
			    System.out.println(rs.getString(3));
			}
			rs.close();
			 */
			st.close();

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

}
