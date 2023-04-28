import java.sql.*;

public class Main {
	public static void main(String[] args) {
		try{
			Class.forName("org.postgresql.Driver");
			Connection co = DriverManager.getConnection("jdbc:postgresql://localhost:5432/", "postgres", "postgres");
			Statement st = co.createStatement();
			ResultSet rs = st.executeQuery("SELECT * FROM tabletest");

			while (rs.next()) {
				System.out.println(rs.getInt("entier"));
			}

			rs.close();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}

		/*Column<String> column1 = new Column<String>();
		column1.addValue("a");
		column1.addValue("b");
		column1.addValue("c");
		column1.addValue("d");
		Column<String> column2 = new Column<String>();
		column2.addValue("1");
		column2.addValue("2");
		column2.addValue("3");
		column2.addValue("4");
		Column<String> column3 = new Column<String>();
		column3.addValue("A");
		column3.addValue("B");
		column3.addValue("C");
		column3.addValue("D");
		Table t1 = new Table("table1", new String[] {"column1", "column2"});
		t1.addColumn("column1", column1);
		t1.addColumn("column2", column2);
		t1.addColumn("column3", column3);
		column3.addValue("E"); //! problematique
		Database database = new Database();
		database.addTable(t1);
		System.out.println(database);*/
	}
}
