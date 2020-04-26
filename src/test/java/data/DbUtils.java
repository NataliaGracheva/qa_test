package data;

import lombok.val;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.DriverManager;
import java.sql.SQLException;

public class DbUtils {
    public DbUtils() {
    }

    public static void clearTables() throws SQLException {
        String url = System.getProperty("url");
        val deleteOrderEntity = "DELETE FROM order_entity;";
        val deletePaymentEntity = "DELETE FROM payment_entity;";
        val deleteCreditRequestEntity = "DELETE FROM credit_request_entity;";
        val countSQL = "SELECT COUNT(*) FROM order_entity;";

        val runner = new QueryRunner();

        try (
//                val conn = DriverManager.getConnection(
//                        "jdbc:postgresql://localhost:5432/app", "app", "pass"
//                );
                val conn = DriverManager.getConnection(
                        url, "app", "pass"
                );
        ) {
            runner.update(conn, deleteOrderEntity);
            runner.update(conn, deletePaymentEntity);
            runner.update(conn, deleteCreditRequestEntity);
             val count = runner.query(conn, countSQL, new ScalarHandler<>());
            System.out.println(count);

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static String findPaymentStatus() throws SQLException {
        val statusSQL = "SELECT status FROM payment_entity;";
        return getData(statusSQL);
    }

    public static String findCreditStatus() throws SQLException {
        val statusSQL = "SELECT status FROM credit_request_entity;";
        return getData(statusSQL);
    }

    public static String countRecords() throws SQLException {
        val countSQL = "SELECT COUNT(*) FROM order_entity;";
        return getData(countSQL);
    }

    static String getData(String query) throws SQLException {
        String url = System.getProperty("url");
        System.out.println(url);
        val runner = new QueryRunner();
        String data = "";

        try (
//                val conn = DriverManager.getConnection(
//                        "jdbc:postgresql://localhost:5432/app", "app", "pass"
//                );
                val conn = DriverManager.getConnection(
                        url, "app", "pass"
                );
        ) {
            data = runner.query(conn, query, new ScalarHandler<>());
            System.out.println(data);
        }

        return data;
    }
}
