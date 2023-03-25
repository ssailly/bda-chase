public class Main {
	public static void main(String[] args) {
		Column<String> column1 = new Column<String>();
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
		Database database = new Database();
		database.addColumn("column1", column1);
		database.addColumn("column2", column2);
		database.addColumn("column3", column3);
		System.out.println(database);
	}
}
