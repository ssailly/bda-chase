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
     * Regarder si pour chaque atome relationnel, la table n'est pas vide
     * @param conn
     * @param tgd
     * @return true if the tgd can be applied on the database (all tables are not empty)
     */
    public static boolean canApply(Connection conn, TGD tgd) {
        for(Dependency.RelationalAtom atom : tgd.phi) {
            if(selectColumns(conn, atom.table, atom.order.values().stream().toList()).size() == 0) return false;
            if(atom.constants != null &&
               atom.constants.size() > 0 &&
               selectAllWithConstants(conn, atom.table, atom.constants).size() == 0) return false;
        }
        return true;
    }

    /**
     * Regarder si pour chaque atome relationnel ou d'egalite, les tables ne sont pas vides
     * @param conn
     * @param egd
     * @return
     */
    public static boolean canApply(Connection conn, EGD egd) {
        for(Dependency.Atom atom : egd.phi) {
            if(atom instanceof Dependency.EqualityAtom) {
                Dependency.EqualityAtom equalityAtom = (Dependency.EqualityAtom) atom;
                if((!equalityAtom.isConst1 && selectColumn(conn, equalityAtom.table1, equalityAtom.nomCol1).size() == 0)
                || (!equalityAtom.isConst2 && selectColumn(conn, equalityAtom.table2, equalityAtom.nomCol2).size() == 0)) return false;
            }
            else {
                Dependency.RelationalAtom relationalAtom = (Dependency.RelationalAtom) atom;
                if(selectColumns(conn, relationalAtom.table, relationalAtom.order.values().stream().toList()).size() == 0) return false;
                if(relationalAtom.constants != null &&
                   relationalAtom.constants.size() > 0 &&
                   selectAllWithConstants(conn, relationalAtom.table, relationalAtom.constants).size() == 0) return false;
            }
        }
        return true;
    }

    /*****************************************************
     *               TRAITEMENT DES EGD                  *
     *****************************************************/

    public static void verifiesEGD(Connection conn, EGD egd){
        /**
         * On cree la condition de mise a jour i.e il faut que
         * le corps de la dependance soit verifiee (phi)
         *
         * WHERE en 2 parties :
         * 1. la satisfaction de phi => construction dans la methode verifiesEGD
         * 2. pour tout equalityAtom dans psi,
         *      equalityAtom.col1 <> equalityAtom.col2
         *      OR equalityAtom.col1 IS NULL
         *      OR equalityAtom.col2 IS NULL
         *    => construction dans la methode egalize
         */

        String cstPhi = "WHERE "; // par exemple si on a y1 = y2
        String tablesPhi = ""; // tables utilisees dans phi
        String colPhi = ""; // colonnes utilisees dans phi

        /**
         * On construit la suite de conjonction dans le where
         */
        for(Dependency.Atom atomPhi: egd.phi) {
            if(atomPhi instanceof Dependency.EqualityAtom) {
                Dependency.EqualityAtom equalityAtom = (Dependency.EqualityAtom) atomPhi;
                String table1Name = equalityAtom.table1.getName();
                String table2Name = equalityAtom.table2.getName();
                if(!tablesPhi.contains(table1Name)) tablesPhi += equalityAtom.table1.getName() + ",";
                if(!tablesPhi.contains(table2Name)) tablesPhi += equalityAtom.table2.getName() + ",";
                if(equalityAtom.isConst1) {
                    cstPhi += equalityAtom.table2.getName() + "." + equalityAtom.nomCol2 + " = '" + equalityAtom.nomCol1 + "' AND ";
                } else if (equalityAtom.isConst2) {
                    cstPhi += equalityAtom.table1.getName() + "." + equalityAtom.nomCol1 + " = '" + equalityAtom.nomCol2 + "' AND ";
                } else {
                    cstPhi += equalityAtom.table1.getName() + "." + equalityAtom.nomCol1 + " = " + equalityAtom.table2.getName() + "." + equalityAtom.nomCol2 + " AND ";
                }
            }
            else {
                Dependency.RelationalAtom relationalAtom = (Dependency.RelationalAtom) atomPhi;
                String tableName = relationalAtom.table.getName();
                if(!tablesPhi.contains(tableName)) tablesPhi += tableName + ",";
                for(String col: relationalAtom.constants.keySet()) {
                    cstPhi += relationalAtom.table.getName() + "." + col + " = '" + relationalAtom.constants.get(col) + "' AND ";
                }
                for(String col: relationalAtom.colonnes.keySet()) colPhi += relationalAtom.table.getName() + "." + col + ",";
            }
        }
        tablesPhi = tablesPhi.substring(0, tablesPhi.length() - 1); // remove last comma
        cstPhi = cstPhi.substring(0, cstPhi.length() - 5); // remove last AND
        colPhi = colPhi.substring(0, colPhi.length() - 1); // remove last comma

        /**
         * La deuxieme partie du where s'assure l'existence de tuples
         * pour les colonnes utilisees dans phi
         */
        String wherePart = cstPhi + " AND EXISTS (SELECT " + colPhi + " FROM " + tablesPhi + " LIMIT 1)";

        /**
         * On egalise les colonnes de psi, un atome d'egalite a la fois
         */
         for (Dependency.EqualityAtom atom : egd.psi) {
            egalize(conn, atom, wherePart);
         }
    }

    private static void egalize(Connection conn, Dependency.EqualityAtom atom, String phiCondition) {
        /**
         * T1 et T2 les tables de l'atome d'egalite
         *
         * 1) UNE DES VALEURS EST UNE CONSTANTE :
         *
         * UPDATE T1 SET col1 = 'valConst'
         * WHERE phiCondition
         * AND col1 <> 'valConst' OR col1 IS NULL;
         *
         * 2) AUCUNE VALEUR N'EST UNE CONSTANTE :
         *
         * UPDATE T1 SET col1 = T2.col2
         * FROM T2
         * WHERE phiCondition
         * AND (T1.col1 <> T2.col2 OR T1.col1 IS NULL)
         * AND T2.col2 IS NOT NULL;
         *
         * et meme requete en inversant T1(col1) et T2(col2)
         */

        if (atom.isConst1) {
            updateTableWithConstant(conn, atom.table2.getName(), atom.nomCol2, atom.nomCol1, phiCondition);
        }
        else if (atom.isConst2){
            updateTableWithConstant(conn, atom.table1.getName(), atom.nomCol1, atom.nomCol2, phiCondition);
        }
        else {
            updateTable(
                    conn,
                    atom.table2.getName(),
                    atom.table1.getName(),
                    atom.nomCol2,
                    atom.nomCol1,
                    phiCondition
            );
            updateTable(
                    conn,
                    atom.table1.getName(),
                    atom.table2.getName(),
                    atom.nomCol1,
                    atom.nomCol2,
                    phiCondition
            );
        }
    }

    private static void updateTable(Connection conn, String tableToUpdate, String refTable, String colToUpdate, String refCol, String wherePart) {
        String updateQuery =
                "UPDATE " + tableToUpdate
                + " SET " + colToUpdate + " = " + refTable + "." + refCol
                + " FROM " + refTable + " " + wherePart
                + " AND (" + refTable + "." + refCol + "<>" + tableToUpdate+ "." + colToUpdate
                + " OR " + tableToUpdate+ "." + colToUpdate + " IS NULL)"
                + " AND " + refTable + "." + refCol + " IS NOT NULL;";
        System.out.println("Mise a jour ->\n\t" + updateQuery);

        try (Statement statement = conn.createStatement()) {
            statement.executeUpdate(updateQuery);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private static void updateTableWithConstant(Connection conn, String tableToUpdate, String colToUpdate, String constant, String wherePart) {
        String updateQuery =
                "UPDATE " + tableToUpdate
                        + " SET " + colToUpdate + " = '" + constant + "'"
                        + " " + wherePart
                        + " AND (" + tableToUpdate+ "." + colToUpdate + "<>'" + constant + "'"
                        + " OR " + tableToUpdate+ "." + colToUpdate + " IS NULL);";
        System.out.println("Mise a jour ->\n\t" + updateQuery);

        try (Statement statement = conn.createStatement()) {
            statement.executeUpdate(updateQuery);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean satisfiesEGD(Connection co, EGD egd) {
        String cstPhi = "WHERE "; // par exemple si on a y1 = y2
        String tablesPhi = ""; // tables utilisees dans phi
        String colPhi = ""; // colonnes utilisees dans phi

        /**
         * On construit la suite de conjonction dans le where
         */
        for(Dependency.Atom atomPhi: egd.phi) {
            if(atomPhi instanceof Dependency.EqualityAtom) {
                Dependency.EqualityAtom equalityAtom = (Dependency.EqualityAtom) atomPhi;
                String table1Name = equalityAtom.table1.getName();
                String table2Name = equalityAtom.table2.getName();
                if(!tablesPhi.contains(table1Name)) tablesPhi += equalityAtom.table1.getName() + ",";
                if(!tablesPhi.contains(table2Name)) tablesPhi += equalityAtom.table2.getName() + ",";
                if(equalityAtom.isConst1) {
                    cstPhi += equalityAtom.table2.getName() + "." + equalityAtom.nomCol2 + " = '" + equalityAtom.nomCol1 + "' AND ";
                } else if (equalityAtom.isConst2) {
                    cstPhi += equalityAtom.table1.getName() + "." + equalityAtom.nomCol1 + " = '" + equalityAtom.nomCol2 + "' AND ";
                } else {
                    cstPhi += equalityAtom.table1.getName() + "." + equalityAtom.nomCol1 + " = " + equalityAtom.table2.getName() + "." + equalityAtom.nomCol2 + " AND ";
                }
            }
            else {
                Dependency.RelationalAtom relationalAtom = (Dependency.RelationalAtom) atomPhi;
                String tableName = relationalAtom.table.getName();
                if(!tablesPhi.contains(tableName)) tablesPhi += tableName + ",";
                for(String col: relationalAtom.constants.keySet()) {
                    cstPhi += relationalAtom.table.getName() + "." + col + " = '" + relationalAtom.constants.get(col) + "' AND ";
                }
                for(String col: relationalAtom.colonnes.keySet()) colPhi += relationalAtom.table.getName() + "." + col + ",";
            }
        }
        tablesPhi = tablesPhi.substring(0, tablesPhi.length() - 1); // remove last comma
        cstPhi = cstPhi.substring(0, cstPhi.length() - 5); // remove last AND
        colPhi = colPhi.substring(0, colPhi.length() - 1); // remove last comma

        /**
         * La deuxieme partie du where s'assure l'existence de tuples
         * pour les colonnes utilisees dans phi
         */
        String wherePart = cstPhi + " AND EXISTS (SELECT " + colPhi + " FROM " + tablesPhi + " LIMIT 1)";

        for(Dependency.EqualityAtom atom: egd.psi) {
            String query = "SELECT COUNT(*) FROM " + tablesPhi + " " +
                    wherePart + " AND " + atom.table1.getName() + "." + atom.nomCol1 + " <> " + atom.table2.getName() + "." + atom.nomCol2 + " " +
                    "OR " + atom.table1.getName() + "." + atom.nomCol1 + " IS NULL " +
                    "OR " + atom.table2.getName() + "." + atom.nomCol2 + " IS NULL;";
            System.out.println("Verification finale EGD : " + query);
            try (Statement statement = co.createStatement()) {
                ResultSet rs = statement.executeQuery(query);
                rs.next();
                if(rs.getInt(1) > 0) return false;

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    /*****************************************************
     *             FIN TRAITEMENT DES EGD                *
     *****************************************************/


    /*****************************************************
     *               TRAITEMENT DES TGD                  *
     *****************************************************/

    /**
     * select * from LEFT_TABLES
     * where LEFT_CSTS
     * AND not exists (select * from RIGHT_TABLES where RIGHT_CSTS and CONSTRAINTS (r.a = q.e));
     * */
    public static List<Map<String, String>> verifiesTGD(Connection conn, TGD tgd){ // pas besoin de verifier l'applicabilite
        String cstPhi = "WHERE ", cstPsi = "WHERE ";
        String tablesPhi = "", tablesPsi = "";

        for(Dependency.RelationalAtom atomPhi: tgd.phi) {
            String tableName = atomPhi.table.getName();
            if(!tablesPhi.contains(tableName)) tablesPhi += tableName + ",";
            // constantes phi
            for(String col: atomPhi.constants.keySet()) {
                cstPhi += atomPhi.table.getName() + "." + col + " = '" + atomPhi.constants.get(col) + "' AND ";
            }
        }
        tablesPhi = tablesPhi.substring(0, tablesPhi.length() - 1); // remove last comma

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
                    for (Dependency.RelationalAtom atomPsi : tgd.psi) {
                        String colPsi = atomPsi.order.get(order);
                        if (colPsi != null) {
                            query += atomPhi.table.getName() + "." + colPhi + " = " + atomPsi.table.getName() + "." + colPsi + " AND ";
                        }
                    }
                    order--;
                }
            }
        }
        query = query.substring(0, query.length() - 5);
        query += ");";

        System.out.println("\nOn verifie : " + query + "\n");

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

    private static int getMaxOrder(TGD tgd) {
        int max = 0;
        for(Dependency.RelationalAtom atom: tgd.phi) {
            for(int order: atom.order.keySet()) {
                if(order > max) max = order;
            }
        }
        return max;
    }

    public static String generateNullValue(String colName){
        String res = "null_" + colName + nullCounter;
        nullCounter++;
        return res;
    }



    /**
     * 1. Pour chaque table de psi, on recupere les colonnes de la table
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

    public static void insertTuple(Connection conn, Table table, Map<String, String> tuple) {
        String query = "INSERT INTO " + table.getName() + " VALUES (";
        for(String col : table.getColumns()){
            query += "'" + tuple.get(col) + "', ";
        }
        query = query.substring(0, query.length() - 2); // remove last comma
        query += ");";
        System.out.println("\t-> " +query);
        try (Statement statement = conn.createStatement()) {
            statement.executeUpdate(query);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    /*****************************************************
     *             FIN TRAITEMENT DES TGD                *
     *****************************************************/



    /**
     * Standard chase
     * @param conn
     * @param dependencies
     * @return
     * @throws SQLException
     */
    public static boolean chase(Connection conn, List<Dependency> dependencies) throws SQLException {
        int applied = 0;
        /**
         * Tant que toutes les dependances n'ont pas ete appliquees
         * (chaque dependance est appliquee au plus une fois)
         */
        while(applied != dependencies.size()) {
            for(Dependency dependency : dependencies) {
                if(!dependency.applied) {
                    if (dependency instanceof TGD) {
                        TGD tgd = (TGD) dependency;
                        if (canApply(conn, tgd)) {
                            System.out.println("\nOn applique la TGD " + tgd);
                            /**
                             * On recupere les tuples qui ne satisfont pas la TGD
                             */
                            List<Map<String, String>> tuplesNotSatisfied = verifiesTGD(conn, tgd);
                            Map<String, String> correspondingCols = tgd.getCorrespondingColumnsFromOrder();
                            /**
                             * Si la liste n'est pas vide i.e la TGD n'est pas satisfaite,
                             * on ajoute les tuples
                             */
                            if (tuplesNotSatisfied.size() != 0) {// la tgd n'est pas satisfaite
                                System.out.println("TGD NON SATISFAITE ! \nA inserer pour satisfaire la " +
                                        "TGD = " + tuplesNotSatisfied);
                                for (Map<String, String> toAdd : tuplesNotSatisfied) {
                                    insertTuple(conn, toAdd, correspondingCols, tgd.tablesPsi);
                                }
                            }
                            else {
                                System.out.println("TGD " + tgd + " SATISFAITE !");
                            }
                            dependency.applied = true;
                            applied++;
                        }
                    } else {
                        EGD egd = (EGD) dependency;
                        if (canApply(conn, egd)) {
                            System.out.println("\nOn applique l'EGD " + egd);
                            /**
                             * On fait directement le scan des attributs dans psi et
                             * on egalise si besoin
                             */
                            verifiesEGD(conn, egd);
                            dependency.applied = true;
                            applied++;
                        }
                    }
                }
            }
        }
        for(Dependency dependency : dependencies) {
            if (dependency instanceof TGD) {
                TGD tgd = (TGD) dependency;
                if (canApply(conn, tgd)) {
                    List<Map<String, String>> tuplesNotSatisfied = verifiesTGD(conn, tgd);
                    Map<String, String> correspondingCols = tgd.getCorrespondingColumnsFromOrder();
                    if (tuplesNotSatisfied.size() != 0) {
                        return false;
                    }
                }
            } else {
                EGD egd = (EGD) dependency;
                if (canApply(conn, egd)) {
                    satisfiesEGD(conn, egd);
                }
            }
        }
        return true;
    }
}
