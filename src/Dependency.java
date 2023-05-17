import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class Dependency {
	boolean applied = false;
	static abstract class Atom {}

	static class RelationalAtom extends Atom {
		/**
		 * String : nom de la colonne
		 * Boolean : true si valeur constante, false sinon
		 */
		Map<String, Boolean> colonnes; // seulement les colonnes qui rentrent en jeu dans la dependance
		Map<String, String> constants = new HashMap<>();
		Map<Integer, String> order = new HashMap<>(); // pour les non constantes
		Table table; // le reste des colonnes : generateNullValue()

		RelationalAtom(Table table, Map<String, Boolean> colonnes) {
			this.table = table;
			this.colonnes = colonnes;
		}

		RelationalAtom(Table table, Map<String, Boolean> colonnes, Map<String, String> constants) {
			this.table = table;
			this.colonnes = colonnes;
			this.constants = constants;
		}

		RelationalAtom(Table table, Map<String, Boolean> colonnes, Map<String, String> constants, Map<Integer, String> order) {
			this.table = table;
			this.colonnes = colonnes;
			this.constants = constants;
			this.order = order;
		}

		public String toString() {
			String s = table.getName() + "(";
			for(Map.Entry<String, String> entry : constants.entrySet()) s += entry.getKey() + "='" + entry.getValue() + "';";
			for(Map.Entry<Integer, String> entry : order.entrySet()) s += entry.getValue() + "=" + entry.getKey() + ";";
			s = s.substring(0, s.length() - 1);
			s += ")";
			return s;
		}
	}

	static class EqualityAtom extends Atom {
		String nomCol1, nomCol2;
		Table table1, table2;
		/**
		 * Boolean : true si la colonne est constante, false sinon
		 */
		boolean isConst1, isConst2;

		EqualityAtom(Table table1, String nomCol1, boolean isConst1, Table table2, String nomCol2, boolean isConst2) {
			this.table1 = table1;
			this.table2 = table2;
			this.nomCol1 = nomCol1;
			this.nomCol2 = nomCol2;
			this.isConst1 = isConst1;
			this.isConst2 = isConst2;
		}

		public String toString() {
			String s = "";
			if(isConst1) s += "'" + nomCol1 + "'=" + table2.getName() + "." + nomCol2;
			else {
				s += table1.getName() + "." + nomCol1 + "=";
				if(isConst2) s += "'" + nomCol2 + "'";
				else s += table2.getName() + "." + nomCol2;
			}
			return s;
		}
	}



	public boolean satisfyEGD(EGD dependancy, Statement st) {
		/**
		 * Construire la requête SQL
		 */
		return false;
	}

	public boolean satisfyTGD(TGD dependancy, Statement st, String table) {
		String query = "SELECT ";
		// Récupérer les colonnes de phi
		String phi = "";
		for (RelationalAtom atom : dependancy.phi) {
			// phi += atom.nomCol + ", ";
		}
		phi = phi.substring(0, query.length() - 2);
		query += phi + " FROM " + table + " GROUP BY " + phi + " HAVING ";
		// Récupérer les colonnes de psi
		for (RelationalAtom atom : dependancy.psi) {
			// query += "COUNT(DISTINCT " + atom.nomCol + ") * ";
		}
		query = query.substring(0, query.length() - 2);
		query += " <> COUNT(*);";
		ResultSet rs;
		try {
			rs = st.executeQuery(query);
			// Si le résultat est vide, renvoyer true
			if (!rs.next()) return true;
		} catch (SQLException e) {
			System.err.println("Error while executing query : " + query);
		}
		return false;
	}


	/* ANCIENNE IMPLEMENTATION SANS JDBC
	private ArrayList<String> leftMember, rightMember;

	public Dependency() {
		this.leftMember = new ArrayList<String>();
		this.rightMember = new ArrayList<String>();
	}

	public Dependency(ArrayList<String> leftMember, ArrayList<String> rightMember) {
		this.leftMember = leftMember;
		this.rightMember = rightMember;
	}

	void addLeftMember(String member) {
		this.leftMember.add(member);
	}

	void addRightMember(String member) {
		this.rightMember.add(member);
	}

	public ArrayList<String> getLeftMember() {
		return this.leftMember;
	}

	public ArrayList<String> getRightMember() {
		return this.rightMember;
	}

	@Override
	public String toString() {
		String s = "";
		for (String left : leftMember) s += left + " ";
		s += "-> ";
		for (String right : rightMember) s += right + " ";
		return s;
	}*/
}