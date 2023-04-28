import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Database {
	private ArrayList<Dependency> dependencies = new ArrayList<Dependency>();
	private ArrayList<Table> tables = new ArrayList<Table>();

	public void addTable(Table table) { this.tables.add(table); }

	public void addDependency(Dependency dependency) {
		this.dependencies.add(dependency);
	}

	@Override
	public String toString() {
		String s = "";
		for (Table t : this.tables) {
			s += t + "\n";
		}
		if (this.dependencies.size() > 0) {
			s += "Dependencies : \n";
			for (Dependency dependency : this.dependencies) s += dependency + "\n";
		}
		return s;
	}
}
