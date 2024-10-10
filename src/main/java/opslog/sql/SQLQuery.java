package opslog.sql;

public class SQLQuery {
    private final String tableName;
    private String columns;
    private String whereClause;
    private String orderByClause;
    private String limitClause;
    private String setClause;
    private String insertValues;
    private boolean isSelect = false;
    private boolean isUpdate = false;
    private boolean isInsert = false;
    private boolean isDelete = false;
    private boolean returningId = false;

    public SQLQuery(String tableName) {
        this.tableName = tableName;
    }

    // The column or row(id number) that the query is targeting
    public SQLQuery where(String whereClause) {
        this.whereClause = whereClause;
        return this;
    }

    // Used to sort the data before it is returned
    // It will order by the column in either Asc or Dsc
    // orderByClause = "columnName ASC|DESC"
    public SQLQuery orderBy(String orderByClause) {
        this.orderByClause = orderByClause;
        return this;
    }

    // Used to limit the number of values returned by query
    public SQLQuery limit(String limitClause) {
        this.limitClause = limitClause;
        return this;
    }
    /*
        Used to return the new id for data INSERTS

        Example Return:
        SQLQuery insertQuery = new SQLQuery("users");
        insertQuery.insert("name, email", "'John Doe', 'john.doe@example.com'")
                    .returningId(); // Indicate we want to return the ID

        String sql = insertQuery.build();
        System.out.println(sql);
        // Output: INSERT INTO users (name, email) VALUES ('John Doe', 'john.doe@example.com') RETURNING id;
    */
    public SQLQuery returningId() {
        this.returningId = true; // Set the flag to return the ID
        return this;
    }

    /*
        Example SELECT:

        SQLQuery selectQuery = new SQLQuery("users");
        selectQuery.select("id, name, email")           // Selecting specific columns
                   .where("age > 25")                    // Filtering with WHERE clause
                   .orderBy("name ASC")                  // Sorting by name in ascending order
                   .limit("10");                         // Limiting the number of results to 10
        String sql = selectQuery.build();
        System.out.println(sql);

        Output: SELECT id, name, email FROM users WHERE age > 25 ORDER BY name ASC LIMIT 10;
     */
    public SQLQuery select(String columns) {
        this.columns = columns;
        this.isSelect = true;
        return this;
    }

    /*
        Example UPDATE:

        SQLQuery updateQuery = new SQLQuery("users");
        updateQuery.set("email = 'new.email@example.com'")  // Setting a new value for email
                   .where("id = 123");                      // Filtering by user ID
        String sql = updateQuery.build();
        System.out.println(sql);

        Output: UPDATE users SET name = 'Jane Doe', email = 'jane.doe@example.com' WHERE id = 123;
    */
    public SQLQuery set(String setClause) {
        this.setClause = setClause;
        this.isUpdate = true;
        return this;
    }

    /*
        Example INSERT:

        SQLQuery insertQuery = new SQLQuery("users");
        insertQuery.insert("name, email", "'John Doe', 'john.doe@example.com'");  // Inserting values into columns
        String sql = insertQuery.build();
        System.out.println(sql);

        Output: INSERT INTO users (name, email) VALUES ('John Doe', 'john.doe@example.com');
    */
    public SQLQuery insert(String columns, String values) {
        this.columns = columns; // Make sure columns are set
        this.insertValues = values;
        this.isInsert = true;
        return this;
    }

    /*
        Example DELETE:

        SQLQuery deleteQuery = new SQLQuery("users");
        deleteQuery.where("id = 123");  // Deleting a user with a specific ID

        String sql = deleteQuery.build();
        System.out.println(sql);

        Output: DELETE FROM users WHERE id = 123;
    */
    public SQLQuery delete() {
        this.isDelete = true;
        return this;
    }

    // Build the SQL query string
    public String build() {
        StringBuilder query = new StringBuilder();

        if (isUpdate) {
            query.append("UPDATE ").append(tableName).append(" SET ").append(setClause);
            if (whereClause != null) {
                query.append(" WHERE ").append(whereClause);
            }
        } else if (isInsert) {
            query.append("INSERT INTO ").append(tableName)
                    .append(" (").append(columns).append(") ")
                    .append("VALUES (").append(insertValues).append(")");
            if (returningId) {
                query.append(" RETURNING id"); // Append RETURNING clause for the ID
            }
        } else if (isSelect) {
            query.append("SELECT ").append(columns).append(" FROM ").append(tableName);
            if (whereClause != null) {
                query.append(" WHERE ").append(whereClause);
            }
            if (orderByClause != null) {
                query.append(" ORDER BY ").append(orderByClause);
            }
            if (limitClause != null) {
                query.append(" LIMIT ").append(limitClause);
            }
        } else if (isDelete) {
            query.append("DELETE FROM ").append(tableName);
            if (whereClause != null) {
                query.append(" WHERE ").append(whereClause);
            }
        }

        return query.toString();
    }
}
