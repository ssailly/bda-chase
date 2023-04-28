import java.util.ArrayList;

public class Column<T> {
	// assurer CRUD ?
	private ArrayList<T> values;

	public Column() {
		this.values = new ArrayList<T>();
	}

	public Column(ArrayList<T> values) {
		this.values = values;
	}

	public void addValue(T value) {
		this.values.add(value);
	}

	public void addValues(ArrayList<T> values) {
		this.values.addAll(values);
	}

	public ArrayList<T> getValues() {
		return this.values;
	}

	public int size() {
		return this.values.size();
	}

	@Override
	public String toString() {
		String s = "";
		for (T value : this.values) s += value + " ; ";
		return s;
	}
}
