package excel;

import org.apache.poi.hssf.record.HCenterRecord;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTColor;
import parser.AmazonItem;
import parser.ItemShortInfo;
import parser.Offer;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class Handler {

    public static List<String> readListOfAsin(String pathToListing) {

        List<String> result = new ArrayList<>();
        try {
            result = Files.readAllLines(Paths.get(pathToListing));
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

        result.add(item);
        result.add(item);


//        writeResult(result);
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

    public static void writeResult(List<AmazonItem> result) throws IOException {


        final String FILE_NAME = "MyFirstExcel.xlsx";
        // TODO Нужно сформировать и сохранить Excel файл

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
                    rows.get(offerCell).createCell(4).setCellValue(offer.getPriceShipingInfo());
                    rows.get(offerCell).createCell(5).setCellValue(offer.getSeller());
                    rows.get(offerCell).createCell(6).setCellValue(offer.getPrice());
                    rows.get(offerCell++).createCell(7).setCellValue(offer.getShipingInfo());
                }
                rows.get(0).createCell(4).setCellValue(item.getPriceShipping());
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

                rows.get(0).createCell(11).setCellValue(item.getVendor());
                rows.get(0).createCell(12).setCellValue(item.getBrand());
                rows.get(0).createCell(13).setCellValue(item.getPartNumber());
                rows.get(0).createCell(14).setCellValue(item.getItemModelNumber());
                rows.get(0).createCell(14).setCellValue(item.getAsinDomin());
                rows.get(0).createCell(15).setCellValue(item.getRating());
                rows.get(0).createCell(16).setCellValue(item.getQuantity());
                rows.get(0).createCell(17).setCellValue(item.getbSR());
                rows.get(0).createCell(18).setCellValue(item.getbSRCategory());
                rows.get(0).createCell(19).setCellValue(item.getDateFirstAvailable());
            } catch (Exception e) {}
        }

        int curRow = 1;
        boolean flag = false;
        for (Row row : sheet) {
            XSSFCellStyle style = flag ?
                    createCellWithStyle(workbook, new Color(208, 208, 208)) :
                    createCellWithStyle(workbook, Color.WHITE);


            for(Cell cell : row) {
                if (cell.getColumnIndex() >= 8 && cell.getColumnIndex() <= 10) {
                    cell.setCellStyle(createCellWithStyle(workbook, new Color(198, 255, 193)));
                } else {
                    cell.setCellStyle(style);
                }
            }




            if ((curRow++ % 6) == 0) {
                flag = !flag;

                for (int i = 0; i <= 19; i++) {
                    if (!(i > 3 && i < 11))
                        sheet.addMergedRegion(new CellRangeAddress(curRow - 7,curRow - 2,i,i));
                }
            }
        }

            FileOutputStream outputStream = new FileOutputStream(FILE_NAME);
            workbook.write(outputStream);
            workbook.close();


        System.out.println("Done");
    }
}
