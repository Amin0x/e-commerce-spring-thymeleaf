import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
