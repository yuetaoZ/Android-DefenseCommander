package com.example.defensecommander;

import android.os.Handler;
import android.os.Looper;

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

            if (level != -1) {
                Statement stmt = conn.createStatement();

                String sql = "insert into " + SCORES_TABLE + " values (" +
                        System.currentTimeMillis() + ", '" + initials + "', " + score + ", " +
                        level + ")";

                stmt.executeUpdate(sql);

                stmt.close();

                TopPlayerInfo topInfo = getAll();
                context.setResults(topInfo.getTopPlayerInfo());
            } else {
                TopPlayerInfo topInfo = getAll();
                new Handler(Looper.getMainLooper()).post(() -> context.reportAndUpdateScore(topInfo));
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private TopPlayerInfo getAll() throws SQLException {
        TopPlayerInfo topInfo = new TopPlayerInfo();

        Statement stmt = conn.createStatement();

        String sql = "select * from " + SCORES_TABLE + "  ORDER BY Score DESC LIMIT 10";

        StringBuilder sb = new StringBuilder();

        String response = String.format(Locale.getDefault(),
                "%5s %7s %7s %7s %15s %n", "#", "Init", "Level", "Score", "Date/Time");
        sb.append(response);

        ResultSet rs = stmt.executeQuery(sql);
        int rank = 1;
        int lowestScore = 0;
        while (rs.next()) {
            long millis = rs.getLong(1);
            String initials = rs.getString(2);
            int score = rs.getInt(3);
            int level = rs.getInt(4);
            lowestScore = score;
            sb.append(String.format(Locale.getDefault(),
                    "%5s %7S %7s %7s %15s %n", rank++, initials.trim(), level, score, sdf.format(new Date(millis))));
        }
        rs.close();
        stmt.close();

        String topPlayerInfo = sb.toString();
        topInfo.setLowestScore(lowestScore);
        topInfo.setTopPlayerInfo(topPlayerInfo);

        return topInfo;
    }

}