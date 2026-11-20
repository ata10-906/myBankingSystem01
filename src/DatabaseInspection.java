import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseInspection {

    public static void main(String[] args) {

        String[] tables = {
                "customers",
                "credentials",
                "accounts",
                "transactions"
        };

        try (Connection connection =
                     DatabaseConnection.getConnection()) {

            DatabaseMetaData metadata =
                    connection.getMetaData();

            for (String table : tables) {

                System.out.println("\n================================");
                System.out.println("TABLE: " + table);
                System.out.println("================================");

                // Inspect columns and data types
                System.out.println("\nCOLUMNS:");

                try (ResultSet columns =
                             metadata.getColumns(null, null, table, null)) {

                    while (columns.next()) {
                        System.out.println(
                                columns.getString("COLUMN_NAME")
                                        + " | Type: "
                                        + columns.getString("TYPE_NAME")
                                        + " | Nullable: "
                                        + columns.getString("IS_NULLABLE")
                        );
                    }
                }

                // Inspect primary keys
                System.out.println("\nPRIMARY KEYS:");

                try (ResultSet primaryKeys =
                             metadata.getPrimaryKeys(null, null, table)) {

                    while (primaryKeys.next()) {
                        System.out.println(
                                primaryKeys.getString("COLUMN_NAME")
                        );
                    }
                }

                // Inspect foreign keys
                System.out.println("\nFOREIGN KEYS:");

                try (ResultSet foreignKeys =
                             metadata.getImportedKeys(null, null, table)) {

                    while (foreignKeys.next()) {
                        System.out.println(
                                foreignKeys.getString("FKCOLUMN_NAME")
                                        + " references "
                                        + foreignKeys.getString("PKTABLE_NAME")
                                        + "."
                                        + foreignKeys.getString("PKCOLUMN_NAME")
                        );
                    }
                }
            }

        } catch (SQLException e) {
            System.out.println(
                    "Database inspection failed: " + e.getMessage()
            );
            e.printStackTrace();
        }
    }
}
