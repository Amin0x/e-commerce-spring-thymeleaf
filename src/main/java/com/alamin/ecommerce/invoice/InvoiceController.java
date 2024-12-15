package com.alamin.ecommerce.invoice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    @Autowired
    private InvoiceService invoiceService;

    // Endpoint to generate and download invoice PDF
    @GetMapping("/{orderId}")
    public ResponseEntity<byte[]> generateInvoice(@PathVariable Long orderId) {
        try {
            // Dummy data for demo purposes
            String customerName = "John Doe";
            List<String> products = List.of("Product A", "Product B", "Product C");
            double totalAmount = 75.00;

            // Generate invoice PDF
            byte[] pdfContents = invoiceService.generateInvoicePdf(orderId, customerName, products, totalAmount);

            // Set response headers to indicate a PDF file download
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=invoice_" + orderId + ".pdf");

            return new ResponseEntity<>(pdfContents, headers, HttpStatus.OK);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    
    // Method to generate an invoice PDF
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
