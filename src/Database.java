import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Database {
	// assurer \forall c1, c2 \in rows, c1.size() == c2.size()
	private int columnSize;
	private HashMap<String, Column> rows;
	private ArrayList<Dependency> dependencies = new ArrayList<Dependency>();
	
	public Database() {
		this.columnSize = -1;
		this.rows = new HashMap<String, Column>();
	}
	
	public Database(HashMap<String, Column> rows) throws ColumnSizeException {
		int columnSize = rows.get(rows.keySet().toArray()[0]).size();
		for (Column column : rows.values()) {
			if (column.size() != columnSize) throw new ColumnSizeException(column);
		}
		this.columnSize = columnSize;
		this.rows = rows;
	}

	public void addColumn(String columnName, Column column) throws ColumnSizeException {
		if (this.columnSize == -1) this.columnSize = column.size();
		else if (column.size() != this.columnSize) throw new ColumnSizeException(column);
		this.rows.put(columnName, column);
	}

	public HashMap<String, Column> getRows() {
		return this.rows;
	}

	public Column getColumn(String columnName) {
		return this.rows.get(columnName);
	}

	public void addDependency(Dependency dependency) {
		this.dependencies.add(dependency);
	}

	private Object[] sortedRowsKeySet() {
		Object[] rowsKeySet = this.rows.keySet().toArray();
		Arrays.sort(rowsKeySet);
		return rowsKeySet;
	}

	@Override
	public String toString() {
		String s = "";
		for (Object columnName : this.sortedRowsKeySet()) {
			s += columnName + " : " + this.rows.get(columnName) + "\n";
		}
		if (this.dependencies.size() > 0) {
			s += "Dependencies : \n";
			for (Dependency dependency : this.dependencies) s += dependency + "\n";
		}
		return s;
	}

	private class ColumnSizeException extends IllegalArgumentException {
		private static final long serialVersionUID = 1L;
		private static final String message = "Error: Database columns must have the same size - ";

		public ColumnSizeException(Column column) {
			super(message + columnSize + " expected but has " + column.size() + ".");
		}
	}
}
