import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class Dependency {
	//private ArrayList<String> leftMember, rightMember;

	abstract class Atom {
		boolean isConst;
	}

	class RelationalAtom extends Atom {
		String nomCol;
	}

	class EqualityAtom extends Atom {
		String nomCol1, nomCol2;
		boolean isConst2;
	}

	class EGD {
		List<Atom> phi;
		List<EqualityAtom> psi;
	}

	class TGD {
		List<RelationalAtom> phi, psi;
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
			phi += atom.nomCol + ", ";
		}
		phi = phi.substring(0, query.length() - 2);
		query += phi + " FROM " + table + " GROUP BY " + phi + " HAVING ";
		// Récupérer les colonnes de psi
		for (RelationalAtom atom : dependancy.psi) {
			query += "COUNT(DISTINCT " + atom.nomCol + ") * ";
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

	/*public Dependency() {
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