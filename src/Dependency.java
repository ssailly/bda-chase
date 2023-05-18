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
}