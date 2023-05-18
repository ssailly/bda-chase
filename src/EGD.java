import java.util.List;

class EGD extends Dependency {
    List<Atom> phi;
    List<EqualityAtom> psi;

    public String toString() {
        String s = "";
        for(Atom atom : phi) s += atom + ",";
        s = s.substring(0, s.length() - 1);
        s += "->";
        for(EqualityAtom atom : psi) s += atom + ",";
        s = s.substring(0, s.length() - 1);
        return s;
    }
}