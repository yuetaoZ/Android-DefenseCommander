package com.example.defensecommander;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class TopScoreDatabaseHandler implements Runnable {

    private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";

    private final MainActivity context;
    private static String dbURL;
    private Connection conn;
    private static final String SCORES_TABLE = "AppScores";
    private final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd HH:mm", Locale.getDefault());

    private final String initials;
    private final int score;
    private final int level;

    TopScoreDatabaseHandler(MainActivity ctx, String initials, int score, int level) {
        context = ctx;
        this.initials = initials;
        this.score = score;
        this.level = level;
        dbURL = "jdbc:mysql://christopherhield.com:3306/chri5558_missile_defense";
    }

    public void run() {

        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(dbURL, "chri5558_student", "ABC.123");

            StringBuilder sb = new StringBuilder();

            if (level == -1) {
                sb.append(getLowestScore());
                context.updateScore(sb.toString());
            } else {
                Statement stmt = conn.createStatement();

                String sql = "insert into " + SCORES_TABLE + " values (" +
                        System.currentTimeMillis() + ", '" + initials + "', " + score + ", " +
                        level + ")";

                int result = stmt.executeUpdate(sql);

                stmt.close();

                String response = "Player " + initials + " added (" + result + " record)\n\n";

                sb.append(response);
                sb.append(getAll());

                context.setResults(sb.toString());
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private String getAll() throws SQLException {
        Statement stmt = conn.createStatement();

        String sql = "select * from " + SCORES_TABLE + "  ORDER BY Score DESC LIMIT 10";

        StringBuilder sb = new StringBuilder();

        ResultSet rs = stmt.executeQuery(sql);
        int rank = 1;
        while (rs.next()) {
            long millis = rs.getLong(1);
            String initials = rs.getString(2);
            int score = rs.getInt(3);
            int level = rs.getInt(4);
            sb.append(String.format(Locale.getDefault(),
                    "%-10d %-12s %8s %8s %12s%n", rank++, initials, level, score, sdf.format(new Date(millis))));
        }
        rs.close();
        stmt.close();

        return sb.toString();
    }

    private String getLowestScore() throws SQLException {
        Statement stmt = conn.createStatement();

        String sql = "select * from " + SCORES_TABLE + "  ORDER BY Score DESC LIMIT 10";

        StringBuilder sb = new StringBuilder();

        ResultSet rs = stmt.executeQuery(sql);
        int rank = 1;
        int lowestScore;
        while (rs.next()) {
            rank++;
            int score = rs.getInt(3);
            if (rank == 10) {
                lowestScore = score;
                sb.append(lowestScore);
            }
        }
        rs.close();
        stmt.close();

        return sb.toString();
    }

}