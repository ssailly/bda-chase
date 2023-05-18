import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class Parser {
	static HashMap<String, Table> tables = new HashMap<>();

	// forme d'une entrée de tables : "R(A;B;C),P(D;E;F)"
	private static void parseTables(String s) {
		String[] tables = s.split(",");
		for(String table : tables) {
			String tableName = table.substring(0, table.indexOf("("));
			String[] cols = table.substring(table.indexOf("(") + 1, table.indexOf(")")).split(";");
			Parser.tables.put(tableName, new Table(tableName, Arrays.asList(cols)));
		}
	}

	// forme d'une d'EGD : "R1(A=x1;B='cst1'),R2(C=z1;D=x2),R1.A=R2.C->R3.E='cst2',R3.F=R2.D"
	private static EGD parseEGD(String s) {
		EGD egd = new EGD();
		
		ArrayList<Dependency.Atom> phi = new ArrayList<>();
		ArrayList<Dependency.EqualityAtom> psi = new ArrayList<>();
		ArrayList<ArrayList<String>> splitted = splitPhiPsi(s);
		ArrayList<String> phiList = splitted.get(0), psiList = splitted.get(1);
		HashMap<String, Integer> atomsOrder = new HashMap<>();

		for(String phiAtom : phiList) {
			if(phiAtom.charAt(phiAtom.length() - 1) == ')') phi.add(createRelationalAtom(phiAtom, atomsOrder));
			else phi.add(createEqualityAtom(phiAtom));
		}
		for(String psiAtom : psiList) psi.add(createEqualityAtom(psiAtom));

		egd.phi = phi;
		egd.psi = psi;

		return egd;
	}

	// forme d'une TGD : "R1(A=x1;B='cst'),R2(C=z1;D=x2)->R3(E=x1;F=x2;G='cst')"
	private static TGD parseTGD(String s) {
		ArrayList<Dependency.RelationalAtom> phi = new ArrayList<>(), psi = new ArrayList<>();
		ArrayList<ArrayList<String>> splitted = splitPhiPsi(s);
		ArrayList<String> phiList = splitted.get(0), psiList = splitted.get(1);
		HashMap<String, Integer> atomsOrder = new HashMap<>();

		for(String phiAtom : phiList) phi.add(createRelationalAtom(phiAtom, atomsOrder));
		for(String psiAtom : psiList) psi.add(createRelationalAtom(psiAtom, atomsOrder));

		return new TGD(phi, psi);
	}

	private static ArrayList<ArrayList<String>> splitPhiPsi(String s) {
		String[] sArray = s.split("->");
		String[] phiArray = sArray[0].split(",");
		String[] psiArray = sArray[1].split(",");
		ArrayList<String> phi = new ArrayList<>(), psi = new ArrayList<>();
		for(String phiMember : phiArray) phi.add(phiMember);
		for(String psiMember : psiArray) psi.add(psiMember);
		ArrayList<ArrayList<String>> res = new ArrayList<>();
		res.add(phi);
		res.add(psi);
		return res;
	}

	private static Dependency.RelationalAtom createRelationalAtom(String atom, Map<String,Integer> otherAtomsOrder) {
		String table = atom.substring(0, atom.indexOf("("));
		String[] cols = atom.substring(atom.indexOf("(") + 1, atom.indexOf(")")).split(";");
		
		HashMap<String, Boolean> colonnes = new HashMap<>();
		HashMap<String, String> constants = new HashMap<>();
		HashMap<Integer, String> order = new HashMap<>();

		int i = otherAtomsOrder.isEmpty() ? 1 : otherAtomsOrder.values().stream().max(Integer::compare).get() + 1;
		for(String col : cols) {
			Set<String> keySet = otherAtomsOrder.keySet();
			String[] splittedCol = col.split("=");
			String nomCol = splittedCol[0], value = splittedCol[1];
			if(value.charAt(0) == '\'' && value.charAt(value.length() - 1) == '\'') {
				colonnes.put(nomCol, true);
				constants.put(nomCol, value.substring(1, value.length() - 1));
			} else {
				colonnes.put(nomCol, false);
				if(keySet.contains(value)) order.put(otherAtomsOrder.get(value), nomCol);
				else {
					otherAtomsOrder.put(value, i);
					order.put(i, nomCol);
					i++;
				}
			}
		}
		return new Dependency.RelationalAtom(tables.get(table), colonnes, constants, order);
	}

	// on suppose que les constantes, s'il y en a, sont à droite
	private static Dependency.EqualityAtom createEqualityAtom(String atom) {
		String[] splittedPhiAtom = atom.split("=");
	
		String[] phiAtomLeft = splittedPhiAtom[0].split("\\.");
		if(splittedPhiAtom[1].charAt(0) == '\'') {
			return new Dependency.EqualityAtom(tables.get(phiAtomLeft[0]), phiAtomLeft[1], false, null, splittedPhiAtom[1].substring(1, splittedPhiAtom[1].length() - 1), true);
		}
		String[] phiAtomRight = splittedPhiAtom[1].split("\\.");
		return new Dependency.EqualityAtom(tables.get(phiAtomLeft[0]), phiAtomLeft[1], false, tables.get(phiAtomRight[0]), phiAtomRight[1], false);
	}
	
	public static List<Dependency> parse(String filepath) throws FileNotFoundException {
		ArrayList<Dependency> res = new ArrayList<>();
		File file = new File(filepath);
		Scanner sc = new Scanner(file);
		while (sc.hasNextLine()) {
			String line = sc.nextLine(), toParse = line.substring(4);
			if (line.startsWith("TAB")) parseTables(toParse);
			else if (line.startsWith("EGD")) res.add(parseEGD(toParse));
			else if (line.startsWith("TGD"))res.add(parseTGD(toParse));
			else System.err.println("Error: " + line + " is not a valid line.");
		}
		sc.close();
		return res;
	}

	public static void main(String[] args) throws FileNotFoundException {
		List<Dependency> dependencies = parse("datasets/dependencies");
		System.out.println("Tables:");
		for(Table t : tables.values()) System.out.println(t);
		System.out.println("---------------");
		System.out.println("Dependencies:");
		for(Dependency d : dependencies) System.out.println(d);
	}
}
