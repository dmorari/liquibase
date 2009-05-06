package liquibase.statement.generator;

import liquibase.database.Database;
import liquibase.database.DerbyDatabase;
import liquibase.database.HsqlDatabase;
import liquibase.database.MSSQLDatabase;
import liquibase.database.structure.Column;
import liquibase.database.structure.Table;
import liquibase.statement.AddAutoIncrementStatement;
import liquibase.statement.syntax.Sql;
import liquibase.statement.syntax.UnparsedSql;
import liquibase.exception.ValidationErrors;
import liquibase.exception.ValidationErrors;

public class AddAutoIncrementGenerator implements SqlGenerator<AddAutoIncrementStatement> {

    public int getSpecializationLevel() {
        return SPECIALIZATION_LEVEL_DEFAULT;
    }

    public boolean isValidGenerator(AddAutoIncrementStatement statement, Database database) {
        return (database.supportsAutoIncrement()
                && !(database instanceof DerbyDatabase)
                && !(database instanceof MSSQLDatabase)
                && !(database instanceof HsqlDatabase));
    }

    public ValidationErrors validate(AddAutoIncrementStatement statement, Database database) {
        ValidationErrors validationErrors = new ValidationErrors();
        if (database instanceof MSSQLDatabase) {
            validationErrors.addError("Cannot make the column as auto-increment.");
        }
        return validationErrors;
    }

    public Sql[] generateSql(AddAutoIncrementStatement statement, Database database) {
        String sql = "ALTER TABLE "
                + database.escapeTableName(statement.getSchemaName(), statement.getTableName())
                + " MODIFY " + database.escapeColumnName(statement.getSchemaName(), statement.getTableName(), statement.getColumnName())
                + " " + database.getColumnType(statement.getColumnDataType(), true)
                + " AUTO_INCREMENT";

        return new Sql[]{
                new UnparsedSql(sql, new Column()
                        .setTable(new Table(statement.getTableName()).setSchema(statement.getSchemaName()))
                        .setName(statement.getColumnName()))
        };
    }
}