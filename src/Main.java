import java.sql.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
	public static void main(String[] args) {
		try{
			Class.forName("org.postgresql.Driver");
			Connection co = DriverManager.getConnection("jdbc:postgresql://localhost:5432/", "postgres", "postgres");
			Statement st = co.createStatement();
			ResultSet rs = st.executeQuery("SELECT * FROM tabletest");

			List<String[]> tableTestString = Chase.selectAll(co, new Table("tableteststring", List.of("a", "b", "c")));
			for(String[] tuple : tableTestString) {
				for(String s : tuple) {
					System.out.print(s + " ");
				}
				System.out.println();
			}

			/*Table r = new Table("X", List.of("a", "b", "x"));
			Table p = new Table("P", List.of("c", "d"));
			Table q = new Table("Q", List.of("e", "f", "g"));
			// R(x1, x2) -> Q(x1, x2, z1)
			Map<String, Boolean> cols = Map.of("a", false, "b", false, "x", true);
			TGD tgd = new TGD(
					List.of(new Dependency.RelationalAtom(r, cols)),
					List.of(new Dependency.RelationalAtom(q, Map.of("e", false, "f", false)))
					);
			tgd.phi.get(0).constants = Map.of("x", "azozo");
			tgd.phi.get(0).order = Map.of(1, "a", 2, "b");
			tgd.psi.get(0).order = Map.of(1, "e", 2, "f");

			System.out.println("-- corresponding columns from order --");
			System.out.println("Table R order :" + tgd.phi.get(0).order);
			System.out.println("Table Q order :" + tgd.psi.get(0).order);
			System.out.println("Resultats correspondance :" + tgd.getCorrespondingColumnsFromOrder());
			System.out.println("--------------------------------------");
			System.out.println("----------- chase example 1 ----------");
			//System.out.println("Dependances = " + tgd); // todo
			//System.out.println(Chase.satisfiesTGD(co, tgd));
			Chase.chase(co, List.of(tgd));
			System.out.println("--------------------------------------");

			// Q(y1, x1, y2) -> P(x1, z1)
			Map<String, Boolean> cols2 = Map.of("f", false);
			Map<String, Boolean> colsPsi2 = Map.of("c", false);
			TGD tgd2 = new TGD(
					List.of(new Dependency.RelationalAtom(q, cols2)),
					List.of(new Dependency.RelationalAtom(p, colsPsi2))
					);

			//   A   B   X    	 C   D    R.B = P.C-> R.A = P.D
			// R(x1, y1, z1) ^ P(y2, x2) ^ y1 = y2 -> x1 = x2

			System.out.println("----------- test EGD1 -----------");

			EGD egd = new EGD();
			Dependency.EqualityAtom equalityAtom = new Dependency.EqualityAtom(r,"a", false, p, "d", false);
			Dependency.RelationalAtom ra1 = new Dependency.RelationalAtom(r, Map.of("a", false, "b", false));
			Dependency.RelationalAtom ra2 = new Dependency.RelationalAtom(p, Map.of("c", false, "d", false));
			Dependency.EqualityAtom equalityAtom2 = new Dependency.EqualityAtom(r, "b", false, p, "c", false);
			egd.phi = List.of(ra1, ra2, equalityAtom2);
			egd.psi = List.of(equalityAtom);

			Chase.verifiesEGD(co, egd);

			System.out.println("--------------------------------------");

			Map<String, String> constants = new HashMap<>();
			constants.put("a", "a");
			constants.put("b", "b");*/
			//System.out.println(Arrays.stream(Chase.selectAllWithConstants(co, r, constants).get(0)).toList());

			/*System.out.println(Chase.canApply(co, tgd));
			System.out.println(Chase.canApply(co, tgd2));
			System.out.println(Chase.canApply(co, egd));*/

            System.out.println("----------- chase example 2 ----------");
            Table _r = new Table("_R", List.of("a", "b"));
            Table _p = new Table("_P", List.of("c", "d"));
            Table _q = new Table("_Q", List.of("e", "f", "g"));

            // c1 : R(x1, x2) -> Q(x1, x2, z1)
            Map<String, Boolean> cols_c1phi = Map.of("a", false, "b", false);
            Dependency.RelationalAtom ra_c1 = new Dependency.RelationalAtom(_r, cols_c1phi);
            ra_c1.order = Map.of(1, "a", 2, "b");

            Map<String, Boolean> cols_c1psi = Map.of("e", false, "f", false, "g", false);
            Dependency.RelationalAtom ra_c1psi = new Dependency.RelationalAtom(_q, cols_c1psi);
            ra_c1psi.order = Map.of(1, "e", 2, "f", 3, "g");

            TGD c1 = new TGD(List.of(ra_c1), List.of(ra_c1psi));

            // c2 : Q(y1, x1, y2) -> P(x1, z1)
            Map<String, Boolean> cols_c2phi = Map.of("e", false, "f", false, "g", false);
            Dependency.RelationalAtom ra_c2phi = new Dependency.RelationalAtom(_q, cols_c2phi);
            ra_c2phi.order = Map.of(1, "e", 2, "f", 3, "g");

            Map<String, Boolean> cols_c2psi = Map.of("c", false, "d", false);
            Dependency.RelationalAtom ra_c2psi = new Dependency.RelationalAtom(_p, cols_c2psi);
            ra_c2psi.order = Map.of(2, "c", 4, "d");

            TGD c2 = new TGD(List.of(ra_c2phi), List.of(ra_c2psi));

            // c3 : R(x1, y1) ^ P(y2, x2) ^ y1 = y2 -> x1 = x2
            Map<String, Boolean> cols_c3phi = Map.of("a", false, "b", false);
            Dependency.RelationalAtom ra_c3phi = new Dependency.RelationalAtom(_r, cols_c3phi);
            ra_c3phi.order = Map.of(1, "a", 2, "b");
            Map<String, Boolean> cols_c3phi2 = Map.of("c", false, "d", false);
            Dependency.RelationalAtom ra_c3phi2 = new Dependency.RelationalAtom(_p, cols_c3phi2);
            ra_c3phi2.order = Map.of(3, "c", 4, "d");
            Dependency.EqualityAtom ea_c3phi = new Dependency.EqualityAtom(_r, "b", false, _p, "c", false);

            Dependency.EqualityAtom ea_c3psi = new Dependency.EqualityAtom(_r, "a", false, _p, "d", false);

            EGD c3 = new EGD(List.of(ra_c3phi, ra_c3phi2, ea_c3phi), List.of(ea_c3psi));

            List<Dependency> dependencies = List.of(c1, c2, c3);

           /* dependencies.stream().forEach(d -> {
                if(d instanceof TGD) System.out.println(Chase.canApply(co, (TGD) d));
                else System.out.println(Chase.canApply(co, (EGD) d));
            });*/

            System.out.println(Chase.chase(co, dependencies) ? "BD reparee" : "BD cassee");

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
