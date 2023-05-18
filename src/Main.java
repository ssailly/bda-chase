import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

public class Main {
	public static void main(String[] args) throws FileNotFoundException {
		try{
			Class.forName("org.postgresql.Driver");
			Connection co = DriverManager.getConnection("jdbc:postgresql://localhost:5432/", "postgres", "postgres");
			List<Dependency> dependencies = Parser.parse("datasets/dependencies");
      System.out.println(Chase.chase(co, dependencies) ? "BD reparee" : "BD cassee");

			//rs.close();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
}
