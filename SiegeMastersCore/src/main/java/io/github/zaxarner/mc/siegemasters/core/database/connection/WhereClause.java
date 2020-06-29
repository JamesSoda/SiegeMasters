package io.github.zaxarner.mc.siegemasters.core.database.connection;

/**
 * Created on 5/25/2020.
 */
public class WhereClause {

    String columnName;
    Object columnValue;

    /**
     * Pairing of column name and expected column value for WHERE clauses
     * @param columnName
     * @param columnValue
     */
    public WhereClause(String columnName, String columnValue) {
        this.columnName = columnName;
        this.columnValue = columnValue;
    }

    public WhereClause(String columnName, Double columnValue) {
        this.columnName = columnName;
        this.columnValue = columnValue;
    }

    public String getColumnName() {
        return columnName;
    }

    public Object getColumnValue() {
        return columnValue;
    }
}
