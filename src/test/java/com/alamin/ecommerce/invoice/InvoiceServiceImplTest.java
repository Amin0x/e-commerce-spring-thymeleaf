package com.alamin.ecommerce.invoice;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import java.io.IOException;
import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InvoiceServiceImplTest {

    @InjectMocks
    private InvoiceServiceImpl invoiceService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGenerateInvoicePdf() throws IOException {
        // Arrange
        Long orderId = 12345L;
        String customerName = "John Doe";
        var productList = Arrays.asList("Product A", "Product B", "Product C");
        double totalAmount = 100.0;

        // Act
        byte[] pdfBytes = invoiceService.generateInvoicePdf(orderId, customerName, productList, totalAmount);

        // Assert
        assertNotNull(pdfBytes, "The generated PDF byte array should not be null");
        assertTrue(pdfBytes.length > 0, "The generated PDF byte array should not be empty");
    }
}