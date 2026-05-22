package db.migration;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class V1__migrate_librarian_to_student extends BaseJavaMigration {
    @Override
    public void migrate(Context context) throws Exception {
        Connection connection = context.getConnection();
        DatabaseMetaData meta = connection.getMetaData();

        // Check for users table existence in a DB-agnostic way
        String tableName = "users";
        boolean usersExists = false;
        try (ResultSet rs = meta.getTables(null, null, tableName, new String[]{"TABLE"})) {
            if (rs.next()) usersExists = true;
        }
        // Some databases store uppercase table names
        if (!usersExists) {
            try (ResultSet rs = meta.getTables(null, null, tableName.toUpperCase(), new String[]{"TABLE"})) {
                if (rs.next()) usersExists = true;
            }
        }

        if (!usersExists) {
            // nothing to do
            return;
        }

        // Ensure the role column exists before updating
        boolean hasRoleColumn = false;
        try (ResultSet rs = meta.getColumns(null, null, tableName, "role")) {
            if (rs.next()) hasRoleColumn = true;
        }
        if (!hasRoleColumn) {
            try (ResultSet rs = meta.getColumns(null, null, tableName.toUpperCase(), "ROLE")) {
                if (rs.next()) hasRoleColumn = true;
            }
        }

        if (!hasRoleColumn) return;

        // Perform update safely
        try (PreparedStatement ps = connection.prepareStatement(
                "UPDATE users SET role = 'STUDENT' WHERE role = 'LIBRARIAN'")) {
            ps.executeUpdate();
        }
    }
}
