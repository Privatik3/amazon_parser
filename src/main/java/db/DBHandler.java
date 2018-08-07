package db;

import com.googlecode.htmlcompressor.compressor.HtmlCompressor;
import manager.RequestTask;

import java.io.PrintStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class DBHandler {

    private static HtmlCompressor compressor = new HtmlCompressor();
    private static Connection conn;

    static {
        try {
            conn = DriverManager.getConnection("jdbc:h2:C:/Developers/amazon_parser/cache");
            conn.setAutoCommit(true);

            compressor.setRemoveComments(true);
            compressor.setRemoveMultiSpaces(true);
            compressor.setRemoveIntertagSpaces(true);
            compressor.setRemoveQuotes(true);
            compressor.setRemoveScriptAttributes(true);
            compressor.setRemoveStyleAttributes(true);
            compressor.setRemoveLinkAttributes(true);
            compressor.setRemoveFormAttributes(true);
            compressor.setRemoveInputAttributes(true);
            compressor.setRemoveJavaScriptProtocol(true);
            compressor.setRemoveHttpProtocol(true);
            compressor.setRemoveHttpsProtocol(true);

            compressor.setCompressCss(true);
            compressor.setCssCompressor(s -> "");

            compressor.setCompressJavaScript(true);
            compressor.setJavaScriptCompressor(s -> "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void addAmazonItems(ArrayList<RequestTask> items) {

        if (items.size() == 0) return;

        PreparedStatement insertStatement = null;
        try {
            String sql = "INSERT INTO AMAZON_ITEMS (ASIN, HTML) values (?, ?)";
            insertStatement = conn.prepareStatement(sql);

            for (RequestTask item : items) {

                insertStatement.setString(1, item.getId());
                insertStatement.setString(2, compressor.compress(item.getHtml()));
                insertStatement.addBatch();
            }

            insertStatement.executeBatch();
        } catch (Exception e) {
//            System.setErr(err);
            e.printStackTrace();
//            System.setErr(null);
        } finally {
            items.clear();
            items = null;

            try {
                if (insertStatement != null) {
                    insertStatement.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void addAmazonSearch(ArrayList<RequestTask> items) {

        if (items.size() == 0) return;

        PreparedStatement insertStatement = null;
        try {
            String sql = "INSERT INTO AMAZON_SEARCH (ASIN, HTML) values (?, ?)";
            insertStatement = conn.prepareStatement(sql);

            for (RequestTask item : items) {

                insertStatement.setString(1, item.getId());
                insertStatement.setString(2, compressor.compress(item.getHtml()));
                insertStatement.addBatch();
            }

            insertStatement.executeBatch();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            items.clear();
            items = null;

            try {
                if (insertStatement != null) {
                    insertStatement.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void addAmazonOffers(ArrayList<RequestTask> items) {

        if (items.size() == 0) return;

        PreparedStatement insertStatement = null;
        try {
            String sql = "INSERT INTO AMAZON_OFFERS (ASIN, HTML) values (?, ?)";
            insertStatement = conn.prepareStatement(sql);

            for (RequestTask item : items) {

                insertStatement.setString(1, item.getId());
                insertStatement.setString(2, compressor.compress(item.getHtml()));
                insertStatement.addBatch();
            }

            insertStatement.executeBatch();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            items.clear();
            items = null;

            try {
                if (insertStatement != null) {
                    insertStatement.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static List<RequestTask> selectAllItems() {

        Statement statement = null;
        List<RequestTask> result = new ArrayList<>();
        try {
            statement = conn.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet rs = statement.executeQuery("SELECT * FROM AMAZON_ITEMS");

            while (rs.next()) {
                String asin = rs.getString(2);
                String html = rs.getString(3);

                result.add(new RequestTask(asin, html));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    public static List<RequestTask> selectAllOffers() {

        Statement statement = null;
        List<RequestTask> result = new ArrayList<>();
        try {
            statement = conn.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet rs = statement.executeQuery("SELECT * FROM AMAZON_OFFERS");

            while (rs.next()) {
                String asin = rs.getString(2);
                String html = rs.getString(3);

                result.add(new RequestTask(asin, html));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    public static List<RequestTask> selectAllSearchResults() {

        Statement statement = null;
        List<RequestTask> result = new ArrayList<>();
        try {
            statement = conn.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet rs = statement.executeQuery("SELECT * FROM AMAZON_SEARCH");

            while (rs.next()) {
                String asin = rs.getString(2);
                String html = rs.getString(3);

                result.add(new RequestTask(asin, html));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    public static void clearAmazonItems() {
        Statement statement = null;
        try {
            statement = conn.createStatement();
            statement.executeUpdate("DELETE FROM AMAZON_ITEMS");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void close() {
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
