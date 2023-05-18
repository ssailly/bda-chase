import java.util.List;

public class Table {
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

    public String toString() {
        return this.name + "(" + String.join(";", this.columns) + ")";
    }
}
