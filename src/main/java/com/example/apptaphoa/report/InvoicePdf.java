package com.example.apptaphoa.report;

import com.example.apptaphoa.model.ChiTietHoaDon;
import com.example.apptaphoa.model.HoaDon;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.text.NumberFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

/** Xuất một hóa đơn ra file PDF (iText). Font nhúng từ resources để hiển thị đủ tiếng Việt. */
public final class InvoicePdf {
    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final NumberFormat MONEY = NumberFormat.getIntegerInstance(new Locale("vi", "VN"));

    private InvoicePdf() {}

    public static void write(Path file, HoaDon invoice, List<ChiTietHoaDon> lines)
            throws IOException, DocumentException {
        BaseFont base = loadBaseFont();
        Font title = new Font(base, 18, Font.BOLD, BaseColor.BLUE);
        Font normal = new Font(base, 11);
        Font bold = new Font(base, 11, Font.BOLD);

        Document doc = new Document(PageSize.A5);
        try (var out = new FileOutputStream(file.toFile())) {
            PdfWriter.getInstance(doc, out);
            doc.open();

            Paragraph heading = new Paragraph("HÓA ĐƠN BÁN HÀNG", title);
            heading.setAlignment(Element.ALIGN_CENTER);
            heading.setSpacingAfter(12);
            doc.add(heading);

            doc.add(new Paragraph("Mã hóa đơn: #" + invoice.getMaHD(), normal));
            doc.add(new Paragraph("Thời gian: " + format(invoice.getThoiGian()), normal));
            doc.add(new Paragraph("Nhân viên: " + invoice.getMaNV(), normal));
            doc.add(new Paragraph("Thanh toán: " + invoice.getHinhThucTT(), normal));
            doc.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(new float[]{4, 1.2f, 2.2f, 2.6f});
            table.setWidthPercentage(100);
            for (String header : new String[]{"Sản phẩm", "SL", "Đơn giá", "Thành tiền"}) {
                PdfPCell cell = new PdfPCell(new Phrase(header, bold));
                cell.setBackgroundColor(new BaseColor(0xE2E8F0));
                table.addCell(cell);
            }
            for (ChiTietHoaDon line : lines) {
                table.addCell(new PdfPCell(new Phrase(line.getTenSanPham(), normal)));
                table.addCell(cell(String.valueOf(line.getSoLuong()), normal, Element.ALIGN_CENTER));
                table.addCell(cell(money(line.getDonGia()), normal, Element.ALIGN_RIGHT));
                table.addCell(cell(money(line.getThanhTien()), normal, Element.ALIGN_RIGHT));
            }
            doc.add(table);

            Paragraph total = new Paragraph("Tổng cộng: " + money(invoice.getTongTien()), bold);
            total.setAlignment(Element.ALIGN_RIGHT);
            total.setSpacingBefore(12);
            doc.add(total);
            doc.close();
        }
    }

    private static BaseFont loadBaseFont() throws IOException, DocumentException {
        byte[] bytes;
        try (InputStream in = InvoicePdf.class.getResourceAsStream("/fonts/consola.ttf")) {
            if (in == null) throw new IOException("Thiếu font /fonts/consola.ttf trong resources");
            bytes = in.readAllBytes();
        }
        return BaseFont.createFont("consola.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, true, bytes, null);
    }

    private static PdfPCell cell(String text, Font font, int align) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(align);
        return cell;
    }

    private static String format(java.util.Date date) {
        return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).format(DATE_TIME);
    }

    private static String money(double value) {
        return MONEY.format(Math.round(value)) + " đ";
    }
}
