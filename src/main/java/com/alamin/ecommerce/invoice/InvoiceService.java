package com.alamin.ecommerce.invoice;

//import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.font.FontConstants; 
import com.itextpdf.kernel.color.Color; 
import com.itextpdf.kernel.font.PdfFontFactory; 
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class InvoiceService {

    // Method to generate an invoice PDF
    public byte[] generateInvoicePdf(Long orderId, String customerName, List<String> productList, double totalAmount) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // Creating a PDF document
        Document document = new Document();
        PdfWriter.getInstance(document, baos);
        document.open();

        // Title
        document.add(new Paragraph("Invoice")
            .setTextAlignment(TextAlignment.CENTER)
            .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD, 16))
            .setSpacingAfter(20));

        // Customer information
        document.add(new Paragraph("Customer Name: " + customerName));
        document.add(new Paragraph("Order ID: " + orderId));
        document.add(new Paragraph("Date: " + java.time.LocalDate.now()));

        document.add(new Paragraph("Items Purchased:"));
        
        // Create table for products
        Table table = new Table(2); // 2 columns
        table.addCell("Product");
        table.addCell("Price");

        // Add products to the table
        for (String product : productList) {
            table.addCell(product);
            table.addCell("$25");  // Placeholder price, you can dynamically fetch the price from DB
        }
        document.add(table);

        // Add total amount
        document.add(new Paragraph("Total Amount: $" + totalAmount)
            .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD, 12))
            .setSpacingBefore(20)
            .setTextAlignment(TextAlignment.RIGHT));

        document.close();

        // Return the PDF as a byte array
        return baos.toByteArray();
    }
}
