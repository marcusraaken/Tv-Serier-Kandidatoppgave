package org.example;
import java.sql.*;

public class database {
    Connection conn = null;
    Statement stmt = null;
    public database() {

        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:mydatabase.db");
            stmt = conn.createStatement();

            String remove = "DROP TABLE IF EXISTS shows";
            stmt.executeUpdate(remove);

            String sql = "CREATE TABLE shows " +
                    "(id INTEGER PRIMARY KEY, " +
                    " name TEXT, " +
                    " rating REAL, " +
                    " imdb TEXT, " +
                    " network TEXT, " +
                    " genres TEXT, " +
                    " summary TEXT, " +
                    " bestEpisodeName TEXT, " +
                    " bestEpisodeRating REAL, " +
                    " bestEpisodeNo TEXT, " +
                    " episodeCount INTEGER, " +
                    " releasedEpisodeCount INTEGER)";
            stmt.executeUpdate(sql);

            stmt.close();
            conn.close();
        } catch (Exception e) {
        }
    }
    public void add(Integer id, String name, Double rating, String network, String imdbLink, String genres,
                    String summary, String bestEpisodeName, Double bestEpisodeRating,
                    String bestEpisodeNo, Integer episodeCount, Integer releasedEpisodeCount){

        try {
            conn = DriverManager.getConnection("jdbc:sqlite:mydatabase.db");
            PreparedStatement input = conn.prepareStatement(
                    "INSERT INTO shows (id, name, rating, network, imdb, genres, summary, bestEpisodeName, bestEpisodeRating, bestEpisodeNo, episodeCount, releasedEpisodeCount) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)");
            input.setInt(1, id);
            input.setString(2, name);
            input.setDouble(3, rating != null ? rating : 0.0);
            input.setString(4, network);
            input.setString(5, imdbLink);
            input.setString(6, genres);
            input.setString(7, summary);
            input.setString(8, bestEpisodeName);
            input.setDouble(9, bestEpisodeRating);
            input.setString(10, bestEpisodeNo);
            input.setInt(11, episodeCount);
            input.setInt(12, releasedEpisodeCount);
            input.executeUpdate();
            input.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}