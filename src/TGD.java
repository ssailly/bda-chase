import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class TGD extends Dependency {
    /**
     * Comme on ne garde que les attributs qui rentrent effectivement en jeu dans la dépendance, l'ordre de phi et psi traduit les egalites de la dependance. Pour les autre colonnes, on utilise generateNullValue() en cas d'insertion.
     * Exemple : R(A,B), Q(E,F,G), R(x1,x2) -> Q(x1,x2,z1) devient phi = R(A,B), psi = Q(E,F), et Q.G null en cas d'insertion.
     */
    List<RelationalAtom> phi, psi;
    List<Table> tablesPhi;
    List<Table> tablesPsi;

    public TGD(List<RelationalAtom> phi, List<RelationalAtom> psi) {
        this.phi = phi;
        this.psi = psi;
        tablesPhi = getTables(phi);
        tablesPsi = getTables(psi);
    }

    private static List<Table> getTables(List<RelationalAtom> atoms) {
        List<Table> res = new ArrayList<>();
        for (RelationalAtom atom: atoms) {
            res.add(atom.table);
        }
        return res;
    }

    /**
     * calcule les paires de colonnes correspondantes entre phi et psi en
     * utilisant leur ordre
     * key = colonne de psi
     * value = colonne de phi
     */
    public Map<String, String> getCorrespondingColumnsFromOrder() {
        	Map<String, String> res = new HashMap<>();
            for (RelationalAtom l: phi){
                for (RelationalAtom r: psi){
                    for(Map.Entry<Integer, String> orderPhi: l.order.entrySet()){
                        for(Map.Entry<Integer, String> orderPsi: r.order.entrySet()){
                            if(orderPhi.getKey() == orderPsi.getKey()){
                                res.put(
                                        r.table.getName() + "." + orderPsi.getValue(),
                                        l.table.getName() + "." + orderPhi.getValue()
                                );
                            }
                        }
                    }
                }
            }
            return res;
    }

    public String toString() {
        String s = "";
        for(RelationalAtom atom : phi) s += atom + ",";
        s = s.substring(0, s.length() - 1);
        s += "->";
        for(RelationalAtom atom : psi) s += atom + ",";
        s = s.substring(0, s.length() - 1);
        return s;
    }
}
