package db;

import manager.RequestTask;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

import java.io.PrintStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBHandler {

    private static Whitelist whitelist = new Whitelist();
    private static Logger log = Logger.getLogger(DBHandler.class.getName());
    private static Connection conn;

    static {
        try {
            conn = DriverManager.getConnection("jdbc:h2:C:/Developers/amazon_parser/cache");
            conn.setAutoCommit(true);

            String[] tags = new String[] {"a", "abbr", "address", "area", "article", "aside", "audio", "b", "base", "bdi", "bdo", "blockquote", "body", "br", "button", "canvas", "caption", "cite", "code", "col", "colgroup", "data", "datalist", "dd", "del", "details", "dfn", "dialog", "div", "dl", "dt", "em", "embed", "fieldset", "figcaption", "figure", "footer", "form", "h1", "h2", "h3", "h4", "h5", "h6", "head", "header", "hgroup", "hr", "html", "i", "iframe", "img", "input", "ins", "kbd", "keygen", "label", "legend", "li", "main", "map", "mark", "math", "menu", "menuitem", "meter", "nav", "noscript", "object", "ol", "optgroup", "option", "output", "p", "param", "picture", "pre", "progress", "q", "rb", "rp", "rt", "rtc", "ruby", "s", "samp", "section", "select", "slot", "small", "source", "span", "strong", "sub", "summary", "sup", "svg", "table", "tbody", "td", "template", "textarea", "tfoot", "th", "thead", "time", "title", "tr", "track", "u", "ul", "var", "video", "wbr"};
            String[] attrs = new String[] {"id", "class", "data-asin", "alt", "href", "content"};

            whitelist.addTags(tags);
            for (String tag : tags)
                whitelist.addAttributes(tag, attrs);
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
                insertStatement.setString(2, Jsoup.clean(item.getHtml(), whitelist));
                insertStatement.addBatch();
            }

            insertStatement.executeBatch();
        } catch (Exception e) {
            log.info("-------------------------------------------------");
            log.log(Level.SEVERE, "Не удалось занести Amazon Items в базу");
            log.log(Level.SEVERE, "Exception: " + e.getMessage());
//            e.printStackTrace();
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

    public static void addEbayItems(ArrayList<RequestTask> items) {

        if (items.size() == 0) return;

        PreparedStatement insertStatement = null;
        try {
            String sql = "INSERT INTO EBAY_ITEMS (ASIN, HTML) values (?, ?)";
            insertStatement = conn.prepareStatement(sql);

            for (RequestTask item : items) {

                insertStatement.setString(1, item.getId());
//                insertStatement.setString(2, compressor.compress(item.getHtml()));
                insertStatement.setString(2, Jsoup.clean(item.getHtml(), whitelist));
                insertStatement.addBatch();
            }

            insertStatement.executeBatch();
        } catch (Exception e) {
//            log.info("-------------------------------------------------");
//            log.log(Level.SEVERE, "Ошибка во время выполнения таска, закрываем такс");
//            log.log(Level.SEVERE, "Exception: " + e.getMessage());
//            e.printStackTrace();
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
            String sql = "INSERT INTO EBAY_SEARCH (ASIN, HTML) values (?, ?)";
            insertStatement = conn.prepareStatement(sql);

            for (RequestTask item : items) {
                insertStatement.setString(1, item.getId());
                insertStatement.setString(2, Jsoup.clean(item.getHtml(), whitelist));
                insertStatement.addBatch();
            }

            insertStatement.executeBatch();
        } catch (Exception e) {
//            log.info("-------------------------------------------------");
//            log.log(Level.SEVERE, "Ошибка во время выполнения таска, закрываем такс");
//            log.log(Level.SEVERE, "Exception: " + e.getMessage());
//            e.printStackTrace();
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

    public static void addEbaySearch(ArrayList<RequestTask> items) {

        if (items.size() == 0) return;

        PreparedStatement insertStatement = null;
        try {
            String sql = "INSERT INTO EBAY_SEARCH (ASIN, HTML) values (?, ?)";
            insertStatement = conn.prepareStatement(sql);

            for (RequestTask item : items) {
                insertStatement.setString(1, item.getId());
                insertStatement.setString(2, Jsoup.clean(item.getHtml(), whitelist));
                insertStatement.addBatch();
            }

            insertStatement.executeBatch();
        } catch (Exception e) {
//            log.info("-------------------------------------------------");
//            log.log(Level.SEVERE, "Ошибка во время выполнения таска, закрываем такс");
//            log.log(Level.SEVERE, "Exception: " + e.getMessage());
//            e.printStackTrace();
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
//                insertStatement.setString(2, compressor.compress(item.getHtml()));
                insertStatement.setString(2, Jsoup.clean(item.getHtml(), whitelist));
                insertStatement.addBatch();
            }

            insertStatement.executeBatch();
        } catch (Exception e) {
//            log.info("-------------------------------------------------");
//            log.log(Level.SEVERE, "Ошибка во время выполнения таска, закрываем такс");
//            log.log(Level.SEVERE, "Exception: " + e.getMessage());
//            e.printStackTrace();
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

    public static void addAmazonPages(ArrayList<RequestTask> items) {

        if (items.size() == 0) return;

        PreparedStatement insertStatement = null;
        try {
            String sql = "INSERT INTO AMAZON_PAGES (PAGE_ID, HTML) values (?, ?)";
            insertStatement = conn.prepareStatement(sql);

            for (RequestTask item : items) {

                insertStatement.setString(1, item.getId());
//                insertStatement.setString(2, compressor.compress(item.getHtml()));
                insertStatement.setString(2, Jsoup.clean(item.getHtml(), whitelist));
                insertStatement.addBatch();
            }

            insertStatement.executeBatch();
        } catch (Exception e) {
//            log.info("-------------------------------------------------");
//            log.log(Level.SEVERE, "Ошибка во время выполнения таска, закрываем такс");
//            log.log(Level.SEVERE, "Exception: " + e.getMessage());
//            e.printStackTrace();
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

    public static List<RequestTask> selectAllEbayItems() {

        Statement statement = null;
        List<RequestTask> result = new ArrayList<>();
        try {
            statement = conn.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet rs = statement.executeQuery("SELECT * FROM EBAY_ITEMS");

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

    public static List<RequestTask> selectAllPages() {

        Statement statement = null;
        List<RequestTask> result = new ArrayList<>();
        try {
            statement = conn.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet rs = statement.executeQuery("SELECT * FROM AMAZON_PAGES");

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

    public static List<RequestTask> selectAllEbaySearchResults() {

        Statement statement = null;
        List<RequestTask> result = new ArrayList<>();
        try {
            statement = conn.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet rs = statement.executeQuery("SELECT * FROM EBAY_SEARCH");

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

    public static void clearAll() {
        Statement statement = null;

        try {
            // ITEMS
            statement = conn.createStatement();

            statement.executeUpdate("DELETE FROM AMAZON_ITEMS");
            statement.executeUpdate("DELETE FROM AMAZON_OFFERS");
            statement.executeUpdate("DELETE FROM AMAZON_PAGES");
            statement.executeUpdate("DELETE FROM AMAZON_SEARCH");

            statement.executeUpdate("DELETE FROM EBAY_SEARCH");
            statement.executeUpdate("DELETE FROM EBAY_ITEMS");
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
