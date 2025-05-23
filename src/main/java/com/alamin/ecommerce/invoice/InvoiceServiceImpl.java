package com.alamin.ecommerce.invoice;

import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class InvoiceServiceImpl implements InvoiceService {

    // Method to generate an invoice PDF
    @Override
    public byte[] generateInvoicePdf(Long orderId, String customerName, List<String> productList, double totalAmount) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
/*
        // Creating a PDF document
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // Title
        document.add(new Paragraph("Invoice")
            .setTextAlignment(TextAlignment.CENTER)
            .setFont(PdfFontFactory.createFont(FontConstants.HELVETICA_BOLD))
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
            .setFont(PdfFontFactory.createFont(FontConstants.HELVETICA_BOLD))
            .setSpacingBefore(20)
            .setTextAlignment(TextAlignment.RIGHT));

        document.close();
*/
        // Return the PDF as a byte array
        return baos.toByteArray();
    }
}
