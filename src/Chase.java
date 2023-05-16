import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class Chase {
    private static int nullCounter = 0;
    /**
     * JDBC :
     * statement = simple requete
     * prepared statement = requete paramétrée
     * callable statement = requete avec fonction stockée
     *
     * ResultSet = résultat d'une requete
     * parcours avec cursor java
     */


    /**
     * Select all tuples from a table
     * @param conn
     * @param table
     * @return
     */
    public static List<String[]> selectAll(Connection conn, Table table) {
        List<String[]> tuples = new ArrayList<>(); // premiere liste pour les noms des colonnes
        String query = "SELECT * FROM " + table.getName() + ";";
        try (Statement statement = conn.createStatement();
             ResultSet res = statement.executeQuery(query)) {
            while (res.next()) {
                String[] tuple = new String[table.getNbColumns()];
                for(String col : table.getColumns()) {
                    tuple[table.getColumns().indexOf(col)] = res.getString(col);
                }
                tuples.add(tuple);
            }
        } catch ( SQLException e) {
            System.err.println(e.getMessage());
        }
        return tuples;
    }

    public static List<String[]> selectColumns(Connection conn, Table table, List<String> columns) {
        List<String[]> tuples = new ArrayList<>(); // premiere liste pour les noms des colonnes
        String query = "SELECT ";
        for(String col : columns) {
            query += col + ", ";
        }
        query = query.substring(0, query.length() - 2); // remove last comma
        query += " FROM " + table.getName() + ";";
        try (Statement statement = conn.createStatement();
             ResultSet res = statement.executeQuery(query)) {
            while (res.next()) {
                String[] tuple = new String[columns.size()];
                for(String col : columns) {
                    tuple[columns.indexOf(col)] = res.getString(col);
                }
                tuples.add(tuple);
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return tuples;
    }

    public static List<String> selectColumn(Connection conn, Table table, String column) {
        List<String> tuples = new ArrayList<>();
        String query = "SELECT " + column + " FROM " + table.getName() + ";";
        try (Statement statement = conn.createStatement();
             ResultSet res = statement.executeQuery(query)) {
            while (res.next()) {
                tuples.add(res.getString(column));
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return tuples;
    }

    public static List<String[]> selectAllWithConstants(Connection conn, Table table, Map<String, String> constants) {
        List<String[]> tuples = new ArrayList<>(); // premiere liste pour les noms des colonnes
        String query = "SELECT * FROM " + table.getName();
        if(constants.size() != 0) {
            query += " WHERE ";
            for(String col : constants.keySet()) {
                query += col + " = '" + constants.get(col) + "' AND ";
            }
            query = query.substring(0, query.length() - 5); // remove last AND
        }
        query += ";";
        try (Statement statement = conn.createStatement();
             ResultSet res = statement.executeQuery(query)) {
            while (res.next()) {
                String[] tuple = new String[table.getNbColumns()];
                for(String col : table.getColumns()) {
                    tuple[table.getColumns().indexOf(col)] = res.getString(col);
                }
                tuples.add(tuple);
            }
        } catch ( SQLException e) {
            System.err.println(e.getMessage());
        }
        return tuples;
    }

    /**
     * @param conn
     * @param tgd
     * @return true if the tgd can be applied on the database (all tables are not empty)
     */
    public static boolean canApply(Connection conn, TGD tgd) {
        for(Dependency.RelationalAtom atom : tgd.phi) {
            if(selectColumns(conn, atom.table, atom.colonnes.keySet().stream().toList()).size() == 0) return false;
            if(atom.constants != null && selectAllWithConstants(conn, atom.table, atom.constants).size() == 0) return false;
        }
        return true;
    }

    public static boolean canApply(Connection conn, EGD egd) {
        for(Dependency.Atom atom : egd.phi) {
            if(atom instanceof Dependency.EqualityAtom) {
                Dependency.EqualityAtom equalityAtom = (Dependency.EqualityAtom) atom;
                if(selectColumn(conn, equalityAtom.table1, equalityAtom.nomCol1).size() == 0 || selectColumn(conn, equalityAtom.table2, equalityAtom.nomCol2).size() == 0) return false;
            }
            else {
                Dependency.RelationalAtom relationalAtom = (Dependency.RelationalAtom) atom;
                if(selectColumns(conn, relationalAtom.table, relationalAtom.colonnes.keySet().stream().toList()).size() == 0) return false;
                if(relationalAtom.constants != null && selectAllWithConstants(conn, relationalAtom.table, relationalAtom.constants).size() == 0) return false;
            }
        }
        return true;
    }

    public static String generateNullValue(String colName){
        String res = "null_" + colName + nullCounter;
        nullCounter++;
        return res;
    }

    public static void insertTuple(Connection conn, Table table, Map<String, String> tuple) {
        String query = "INSERT INTO " + table.getName() + " VALUES (";
        for(String col : table.getColumns()){
            query += "'" + tuple.get(col) + "', ";
        }
        query = query.substring(0, query.length() - 2); // remove last comma
        query += ");";
        System.out.println("-> " +query);
        try (Statement statement = conn.createStatement()) {
            statement.executeUpdate(query);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    private static int getMaxOrder(TGD tgd) {
        int max = 0;
        for(Dependency.RelationalAtom atom: tgd.phi) {
            for(int order: atom.order.keySet()) {
                if(order > max) max = order;
            }
        }
        return max;
    }


    /*select * from LEFT_TABLES where LEFT_CSTS AND not exists (select * from RIGHT_TABLES where RIGHT_CSTS and CONSTRAINTS (r.a = q.e));*/
    public static List<Map<String, String>> satisfiesTGD(Connection conn, TGD tgd){ // pas besoin de verifier l'applicabilite
        String cstPhi = "WHERE ", cstPsi = "WHERE ";
        String tablesPhi = "", tablesPsi = "";

        for(Dependency.RelationalAtom atomPhi: tgd.phi) {
            tablesPhi += atomPhi.table.getName() + ",";
            // constantes phi
            for(String col: atomPhi.constants.keySet()) {
                cstPhi += atomPhi.table.getName() + "." + col + " = '" + atomPhi.constants.get(col) + "' AND ";
            }
        }
        tablesPhi = tablesPhi.substring(0, tablesPhi.length() - 1);

        for(Dependency.RelationalAtom atomPsi: tgd.psi) {
            tablesPsi += atomPsi.table.getName() + ",";
            // constantes psi
            for(String col: atomPsi.constants.keySet()) {
                cstPsi += atomPsi.table.getName() + "." + col + " = '" + atomPsi.constants.get(col) + "' AND ";
            }
        }
        tablesPsi = tablesPsi.substring(0, tablesPsi.length() - 1);

        String query = "SELECT ";
        // ne selectionner que les colonnes de phi qui sont impliquer
        for(Dependency.RelationalAtom atomPhi: tgd.phi) {
            for(String col: atomPhi.colonnes.keySet()) {
                query += atomPhi.table.getName() + "." + col + ", ";
            }
        }
        query = query.substring(0, query.length() - 2); // remove last comma
        query += " FROM " + tablesPhi + " " + cstPhi + "NOT EXISTS (SELECT * FROM " + tablesPsi + " " + cstPsi;

        int order = getMaxOrder(tgd);
        while(order > 0) {
            for (Dependency.RelationalAtom atomPhi : tgd.phi) {
                String colPhi = atomPhi.order.get(order);
                if (colPhi != null) {
                    query += atomPhi.table.getName() + "." + colPhi;
                    for (Dependency.RelationalAtom atomPsi : tgd.psi) {
                        String colPsi = atomPsi.order.get(order);
                        if (colPsi != null) {
                            query += " = " + atomPsi.table.getName() + "." + colPsi + " AND ";
                        }
                    }
                    order--;
                }
            }
        }
        query = query.substring(0, query.length() - 5);
        query += ");";

        System.out.println("On verifie : " + query + "\n");

        try (Statement statement = conn.createStatement()) {
            statement.executeQuery(query);
            ResultSet rs = statement.getResultSet();
            /**
             * key = colonne de phi
             * value = valeur de la colonne de phi
             */
            List<Map<String, String>> tuplesNotSatisfied = new ArrayList<>();
            while(rs.next()) {
                Map<String, String> tuple = new HashMap<>();
                for(Dependency.RelationalAtom atomPhi: tgd.phi) {
                    for(String col: atomPhi.colonnes.keySet()) {
                        tuple.put(
                                atomPhi.table.getName()+"."+col,
                                rs.getString(col)
                        );
                    }
                }
                tuplesNotSatisfied.add(tuple);
            }
            return tuplesNotSatisfied;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return null;
    }


    /**
     * 1. Pour chaque table de psi (rappel : phi et psi ne contiennent que des tables
     * effectivement affectees par la TGD), on recupere les colonnes de la table
     * 2. Pour chaque colonne de la table
     *  -  si elle est impliquee dans la TGD, on lui donne la valeur de la
     *     colonne correspondante dans phi
     *  -  sinon, on lui donne une valeur avec generateNullValue
     */
    private static void insertTuple(Connection co, Map<String, String> toAdd, Map<String, String> correspondingCols, List<Table> tablesPsi) {
        Map<String, String> newTuple = new HashMap<>();
        for(Table tablePsi: tablesPsi) {
            for(String colPsi: tablePsi.getColumns()) {
                String fullCol = tablePsi.getName()+"."+colPsi;
                if(correspondingCols.containsKey(fullCol)) {
                    String colPhi = correspondingCols.get(fullCol);
                    newTuple.put(colPsi, toAdd.get(colPhi));
                } else {
                    newTuple.put(colPsi, generateNullValue(colPsi));
                }
            }
            System.out.println("Nouvelle insertion");
            insertTuple(co, tablePsi, newTuple);
        }
    }

    /**
     * Standard chase
     * @param conn
     * @param dependencies
     * @return
     * @throws SQLException
     */
    public static boolean chase(Connection conn, List<Dependency> dependencies) throws SQLException {
        int applied = 0;
        while(applied != dependencies.size()) {
            for(Dependency dependency : dependencies) {
                if(!dependency.applied) {
                    if (dependency instanceof TGD) {
                        TGD tgd = (TGD) dependency;
                        if (canApply(conn, tgd)) {
                            List<Map<String, String>> tuplesNotSatisfied = satisfiesTGD(conn, tgd);
                            Map<String, String> correspondingCols = tgd.getCorrespondingColumnsFromOrder();
                            if (tuplesNotSatisfied.size() != 0) {// la tgd n'est pas satisfaite
                                System.out.println("TGD NON SATISFAITE ! \nA inserer pour satisfaire la TGD = " + tuplesNotSatisfied);
                                for (Map<String, String> toAdd : tuplesNotSatisfied) {
                                    insertTuple(conn, toAdd, correspondingCols, tgd.tablesPsi);
                                }
                            }
                        }
                    } else {
                        EGD egd = (EGD) dependency;
                        if (canApply(conn, egd)) {
                            /*for (String[] tuple : selectAll(conn, egd.phi.get(0).table)) {
                                if (!tupleAlreadyExists(conn, egd.phi.get(1).table, tuple)) {
                                    insertTuple(conn, egd.phi.get(1).table, tuple);
                                }
                            }*/
                        }
                    }
                    dependency.applied = true;
                    applied++;
                }
            }
        }
        return false;
    }
}
