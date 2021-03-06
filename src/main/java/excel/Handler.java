package excel;

import db.DBHandler;
import org.apache.poi.hssf.record.HCenterRecord;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTColor;
import parser.AmazonItem;
import parser.EbayItem;
import parser.ItemShortInfo;
import parser.Offer;

import java.awt.Color;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Handler {

    private static Logger log = Logger.getLogger(Handler.class.getName());

    public static List<String> readListOfAsin(String pathToListing) {

        List<String> result = new ArrayList<>();

        try {
            XSSFWorkbook myExcelBook = new XSSFWorkbook(new FileInputStream( new File(pathToListing)));
            XSSFSheet myExcelSheet = myExcelBook.getSheet("Лист1");
//            result = Files.readAllLines(Paths.get(pathToListing));
            for (Integer i = 0 ; myExcelSheet.getLastRowNum() >= i ; i++) {
                XSSFRow row = myExcelSheet.getRow(i);
                String val = row.getCell(0).getStringCellValue();
                if (!val.isEmpty())
                    result.add(val);
            }
        } catch (Exception e) {
            System.err.println("Не удалось загрузить листинг");
            e.printStackTrace();
        }

        return result;
    }

    public static void main(String[] args) {

        List<AmazonItem> result = new ArrayList<>();
        AmazonItem item = new AmazonItem();

        item.setAsin("B01M7W3L8U");
        item.setAvailability(true);
        item.setPromoOffer(false);
        item.setVendor("Le Creuset");
        item.setProductName("Le Creuset 7 1/4 Qt. Cast Iron Round French Oven - Dune");
        item.setBuyBoxSeller("setBuyBoxSeller");
        item.setBuyBoxPrice(7.95);
        item.setBuyBoxShipping("& FREE shipping on orders over $25.00 shipped by Amazon.");
        item.setPriceShipping(100.00);
        item.setBrand("Clorox");
        item.setPartNumber("KJDPV");
        item.setItemModelNumber("060740-002-0000");
        item.setAsinDomin("B01K7MXF0W");
        item.setRating(4.4);
        item.setQuantity("36");
        item.setbSR(27791);
        item.setbSRCategory("Beauty & Personal Care");
        item.setDateFirstAvailable(new Date());

        // OFFERS
        List<Offer> offers = new ArrayList<>();

        // First
        Offer firstOffer = new Offer();
        firstOffer.setPrice("$429.99");
        firstOffer.setPriceShipingInfo(550.99);
        firstOffer.setShipingInfo("+ $4.49shipping");
        firstOffer.setSeller("Premium Pots");
        offers.add(firstOffer);

        item.setOffers(offers);

        // Second
        Offer secondOffer = new Offer();
        secondOffer.setPrice("$87.10");
        secondOffer.setPriceShipingInfo(550.99);
        secondOffer.setShipingInfo("& FREE Shipping");
        secondOffer.setSeller("BBBProducts");
        offers.add(secondOffer);

        item.setOffers(offers);

        // SEARCH INFO
        List<ItemShortInfo> searchInfo = new ArrayList<>();
        ItemShortInfo info = new ItemShortInfo();
        info.setAsin("B07892Z5SH");
        info.setAvailability(true);
        info.setFirstOffer(firstOffer);
        searchInfo.add(info);

        item.setSearchInfo(searchInfo);

        // Ebay
        List<EbayItem> ebayItems = new ArrayList<>();
        EbayItem eItem1 = new EbayItem();
        eItem1.setItemNumber("164843134");
        eItem1.setPrice(0.0);
        eItem1.setPriceShipping(5.0);
        eItem1.setSeller("Seller !");
        eItem1.setShipping("$47,5");
        ebayItems.add(eItem1);

        EbayItem eItem2 = new EbayItem();
        eItem2.setItemNumber("164843134");
        eItem2.setPrice(0.0);
        eItem2.setPriceShipping(5.0);
        eItem2.setSeller("Seller !");
        eItem2.setShipping("$47,5");
        ebayItems.add(eItem2);

        item.setEbayItems(ebayItems);

        result.add(item);
        result.add(item);


        writeResult(result);
    }

    private static XSSFCellStyle createCellWithStyle(XSSFWorkbook workbook, Color color) {
        XSSFCellStyle style = workbook.createCellStyle();

        style.setFillForegroundColor(getXSSFColor(color));
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment  .CENTER);


        return style;
    }

    private static XSSFColor getXSSFColor(Color color) {

        CTColor ctColor = CTColor.Factory.newInstance();

        byte[] rgb = new byte[3];
        rgb[0] = (byte) color.getRed();
        rgb[1] = (byte) color.getGreen();
        rgb[2] = (byte) color.getBlue();

        ctColor.setRgb(rgb);
        return new XSSFColor(ctColor);
    }

    public static void writeResult(List<AmazonItem> result) {


        final String FILE_NAME = "MyFirstExcel.xlsx";
        // TODO Нужно сформировать и сохранить Excel файл

        try {
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet("Datatypes in Java");

            int rowNum = 0;
            for (AmazonItem item : result) {

                List<Row> rows = new ArrayList<>();
                for (int i = 0; i < 6; i++) {
                    XSSFRow row = sheet.createRow(rowNum++);
                    for (int j = 0; j < 19; j++)
                        row.createCell(j).setCellValue("N/A");

                    rows.add(row);
                }
                try {
                    rows.get(0).createCell(0).setCellValue(String.valueOf(item.getAvailability()));
                    rows.get(0).createCell(1).setCellValue(String.valueOf(item.getPromoOffer()));
                    rows.get(0).createCell(2).setCellValue(item.getAsin());
                    rows.get(0).createCell(3).setCellValue(item.getProductName());

                    int offerCell = 1;
                    for (Offer offer : item.getOffers()) {
                        rows.get(offerCell).createCell(4).setCellValue(String.format("%.2f" , offer.getPriceShipingInfo()));
                        rows.get(offerCell).createCell(5).setCellValue(offer.getSeller());
                        rows.get(offerCell).createCell(6).setCellValue(offer.getPrice().substring(1).replace("." , ","));
                        rows.get(offerCell++).createCell(7).setCellValue(offer.getShipingInfo());
                    }
                    rows.get(0).createCell(4).setCellValue(String.format("%.2f" , item.getPriceShipping()));
                    rows.get(0).createCell(5).setCellValue(item.getBuyBoxSeller());
                    rows.get(0).createCell(6).setCellValue(item.getBuyBoxPrice());
                    rows.get(0).createCell(7).setCellValue(item.getBuyBoxShipping());

                    offerCell = 0;
                    for (ItemShortInfo info : item.getSearchInfo()) {
                        rows.get(offerCell).createCell(8).setCellValue(info.getAsin());
                        if (info.getFirstOffer() != null) {
                            rows.get(offerCell).createCell(9).setCellValue(info.getFirstOffer().getPriceShipingInfo());
                        }
                        rows.get(offerCell++).createCell(10).setCellValue(String.valueOf(info.getAvailability()));
                    }

                    offerCell = 3;
                    for (EbayItem eIttem : item.getEbayItems()) {
                        rows.get(offerCell).createCell(8).setCellValue(eIttem.getItemNumber());
                        rows.get(offerCell).createCell(9).setCellValue(eIttem.getPriceShipping());
                        rows.get(offerCell++).createCell(10).setCellValue(eIttem.getSeller());
                    }

                    rows.get(0).createCell(11).setCellValue(item.getVendor());
                    rows.get(0).createCell(12).setCellValue(item.getBrand());
                    rows.get(0).createCell(13).setCellValue(item.getPartNumber());
                    rows.get(0).createCell(14).setCellValue(item.getItemModelNumber());
                    rows.get(0).createCell(15).setCellValue(item.getAsinDomin());
                    rows.get(0).createCell(16).setCellValue(item.getRating());
                    rows.get(0).createCell(17).setCellValue(item.getQuantity());
                    rows.get(0).createCell(18).setCellValue(item.getbSR());
                    rows.get(0).createCell(19).setCellValue(item.getbSRCategory());
                    rows.get(0).createCell(20).setCellValue(new SimpleDateFormat("yyyy.MM.dd").format(item.getDateFirstAvailable()));
                } catch (Exception e) {
                }
            }
            int numberColor = 1;
            int curRow = 1;
            boolean flag = false;
            for (Row row : sheet) {
                XSSFCellStyle style = flag ?
                        createCellWithStyle(workbook, new Color(208, 208, 208)) :
                        createCellWithStyle(workbook, Color.WHITE);

                for (Cell cell : row) {
                    if (cell.getColumnIndex() >= 8 && cell.getColumnIndex() <= 10) {
                        Color color = color = new Color(225, 255, 188);
                        if ((((cell.getRowIndex()) / 3) + 1) % 2 == 0) color = new Color(255, 247, 205);
                        cell.setCellStyle(createCellWithStyle(workbook, color));
                    } else {
                        cell.setCellStyle(style);
                    }
                    numberColor++;
                }


                if ((curRow++ % 6) == 0) {
                    flag = !flag;

                    for (int i = 0; i <= 20; i++) {
                        if (!(i > 3 && i < 11))
                            sheet.addMergedRegion(new CellRangeAddress(curRow - 7, curRow - 2, i, i));
                    }
                }
            }

            FileOutputStream outputStream = new FileOutputStream(FILE_NAME);
            workbook.write(outputStream);
            workbook.close();
        } catch (Exception e) {
            log.info("-------------------------------------------------");
            log.log(Level.SEVERE, "Не удалось сохранить Excel таблицу");
            log.log(Level.SEVERE, "Exception: " + e.getMessage());
        }


        System.out.println("Done");
    }
}
