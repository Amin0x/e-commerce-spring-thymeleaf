package com.alamin.ecommerce.invoice;

import java.io.IOException;
import java.util.List;

public interface InvoiceService {
    // Method to generate an invoice PDF
    byte[] generateInvoicePdf(Long orderId, String customerName, List<String> productList, double totalAmount) throws IOException;
}
