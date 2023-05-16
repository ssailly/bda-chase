import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Table {
    /*// assurer \forall c1, c2 \in rows, c1.size() == c2.size()
    private int columnSize;
    private HashMap<String, Column> rows;
    private String[] primaryKey;
    private String name;

    public Table(String name){ // par défaut mettre une primary key serial
        this.name = name;
        this.columnSize = -1;
        this.rows = new HashMap<String, Column>();
        this.rows.put("idSerial", new Column<Integer>());
        this.primaryKey = new String[]{ "idSerial" };
    }

    public Table(String name, String[] primaryKey) {
        this.name = name;
        this.columnSize = -1;
        this.rows = new HashMap<String, Column>();
        this.primaryKey = primaryKey; // impossibilité de définir à l'avance leurs types..
    }

    public Table(String name, String[] primaryKey, HashMap<String, Column> rows) throws ColumnSizeException {
        this.name = name;
        for(String key : primaryKey) {
            if (!rows.containsKey(key)) throw new MissingPrimaryKeyException();
        }
        int columnSize = rows.get(rows.keySet().toArray()[0]).size();
        for (Column column : rows.values()) {
            if (column.size() != columnSize) throw new ColumnSizeException(column);
        }
        this.columnSize = columnSize;
        this.rows = rows;
    }

    public void addColumn(String columnName, Column column) throws ColumnSizeException {
        if (this.columnSize == -1) this.columnSize = column.size();
        else if (column.size() != this.columnSize) throw new ColumnSizeException(column); //? inclut le cas où c'est une colonne cle primaire?
        this.rows.put(columnName, column);
    }

    public HashMap<String, Column> getRows() {
        return this.rows;
    }

    public Column getColumn(String columnName) {
        return this.rows.get(columnName);
    }

    private Object[] sortedRowsKeySet() {
        Object[] rowsKeySet = this.rows.keySet().toArray();
        Arrays.sort(rowsKeySet);
        return rowsKeySet;
    }

    @Override
    public String toString() {
        String s = "Table " + this.name + " ----------- \n";
        for (Object columnName : this.sortedRowsKeySet()) {
            s += columnName + " : " + this.rows.get(columnName) + "\n";
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

    private class MissingPrimaryKeyException extends IllegalArgumentException {
        private static final long serialVersionUID = 1L;
        private static final String message = "Error: Primary key cannot be null.";

        public MissingPrimaryKeyException() {
            super(message);
        }
    }*/

    private String name;
    private int nbColumns;
    private List<String> columns;


    public Table(String name, List<String> columns) {
        this.name = name;
        this.nbColumns = columns.size();
        this.columns = columns;
    }

    public String getName() {
        return this.name;
    }

    public int getNbColumns() {
        return this.nbColumns;
    }

    public List<String> getColumns() {
        return this.columns;
    }
}
