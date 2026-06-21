package com.example.apptaphoa.controller;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import javax.swing.*;
import java.io.FileOutputStream;

public class DoanhThuPDF {

    public static void exportDoanhThu(JTable table, String filePath) {
        Document document = new Document(PageSize.A4.rotate()); // tạo pdf a4 Nằm ngang
        try {
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();
            
            BaseFont bf = BaseFont.createFont("/home/qtriz/Workspace/FPT_Workspace/DuAn1_Java/AppTapHoa/src/main/java/com/example/apptaphoa/views/consola.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            // Tiêu đề lớn
            Font titleFont = new Font(bf, 20, Font.BOLD, BaseColor.BLUE);
            Paragraph title = new Paragraph("BÁO CÁO DOANH THU", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            // Bảng PDF
            PdfPTable pdfTable = new PdfPTable(table.getColumnCount());
            pdfTable.setWidthPercentage(100);

            // Header
            Font headerFont = new Font(bf, 12, Font.BOLD, BaseColor.WHITE);
            BaseColor headerColor = new BaseColor(0, 102, 204);
            for (int i = 0; i < table.getColumnCount(); i++) {
                PdfPCell cell = new PdfPCell(new Phrase(table.getColumnName(i), headerFont));
                cell.setBackgroundColor(headerColor);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                pdfTable.addCell(cell);
            }

            // Dữ liệu
            double tongDoanhThu = 0;
            for (int row = 0; row < table.getRowCount(); row++) {
                for (int col = 0; col < table.getColumnCount(); col++) {
                    Object value = table.getValueAt(row, col);
                    String text = value != null ? value.toString() : "";
                    PdfPCell cell = new PdfPCell(new Phrase(text));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    pdfTable.addCell(cell);

                    // Nếu là cột "Tổng Doanh Thu" thì cộng lại
                    if (col == table.getColumnCount() - 1 && value != null) {
                        try {
                            tongDoanhThu += Double.parseDouble(value.toString());
                        } catch (NumberFormatException ignored) {
                        }
                    }
                }
            }

            document.add(pdfTable);

            // Tổng doanh thu
            Paragraph total = new Paragraph("Tổng Doanh Thu: " + tongDoanhThu + " VND",
                    new Font(bf, 14, Font.BOLD, BaseColor.RED));
            total.setAlignment(Element.ALIGN_RIGHT);
            total.setSpacingBefore(20);
            document.add(total);

            // Ngày xuất
            Paragraph date = new Paragraph("Ngày xuất: " + java.time.LocalDate.now(),
            		new Font(bf, 10, Font.ITALIC));
            date.setSpacingBefore(10);
            document.add(date);

            JOptionPane.showMessageDialog(null, "Xuất PDF thành công!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Lỗi khi xuất PDF: " + e.getMessage());
            e.printStackTrace();
        } finally {
            document.close();
        }
    }
}
    
