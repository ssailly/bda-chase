import java.util.ArrayList;

public class Dependency {
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
	}
}