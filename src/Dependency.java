import java.sql.Statement;
import java.util.ArrayList;
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