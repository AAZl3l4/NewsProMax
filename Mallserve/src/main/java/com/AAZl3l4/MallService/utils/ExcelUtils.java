package com.AAZl3l4.MallService.utils;

import com.AAZl3l4.MallService.pojo.OrderItem;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.util.List;

public class ExcelUtils {

    public static void exportOrderItem(List<OrderItem> list, HttpServletResponse response) throws IOException {
        String fileName = "商品订单报表_" + LocalDate.now() + ".xlsx";
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition",
                "attachment; filename=" + URLEncoder.encode(fileName, "UTF-8"));

        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("订单明细");

        // 1. 标题样式
        CellStyle headStyle = wb.createCellStyle();
        Font headFont = wb.createFont();
        headFont.setBold(true);
        headStyle.setFont(headFont);
        headStyle.setAlignment(HorizontalAlignment.CENTER);
        headStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        // 2. 数据样式（金额右对齐）
        CellStyle numStyle = wb.createCellStyle();
        numStyle.setAlignment(HorizontalAlignment.RIGHT);
        DataFormat fmt = wb.createDataFormat();
        numStyle.setDataFormat(fmt.getFormat("#,##0.00"));

        // 3. 创建表头
        String[] headers = {"订单号","商品ID","商品名称","图片","单价","数量","金额"};
        Row headRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headStyle);
        }

        // 4. 填充数据
        int rowIdx = 1;
        for (OrderItem o : list) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(o.getOrderId());
            row.createCell(1).setCellValue(o.getProductId());
            row.createCell(2).setCellValue(o.getProductName());
            row.createCell(3).setCellValue(o.getProductImage());

            Cell priceCell = row.createCell(4);
            priceCell.setCellValue(o.getUnitPrice());
            priceCell.setCellStyle(numStyle);

            row.createCell(5).setCellValue(o.getQuantity());

            Cell amtCell = row.createCell(6);
            amtCell.setCellValue(o.getAmount());
            amtCell.setCellStyle(numStyle);
        }

        // 5. 自适应列宽
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
            // 中文再宽一点
            sheet.setColumnWidth(i, sheet.getColumnWidth(i) * 12 / 10);
        }

        wb.write(response.getOutputStream());
        wb.close();
    }
}