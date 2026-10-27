package com.alamin.ecommerce.invoice;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.io.IOException;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;





class InvoiceControllerTest {

    @Mock
    private InvoiceService invoiceService;

    @InjectMocks
    private InvoiceController invoiceController;

    public InvoiceControllerTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGenerateInvoice_Success() throws IOException {
        // Arrange
        Long orderId = 1L;
        String customerName = "John Doe";
        List<String> products = List.of("Product A", "Product B", "Product C");
        double totalAmount = 75.00;
        byte[] mockPdfContents = new byte[]{1, 2, 3};

        when(invoiceService.generateInvoicePdf(orderId, customerName, products, totalAmount)).thenReturn(mockPdfContents);

        // Act
        ResponseEntity<byte[]> response = invoiceController.generateInvoice(orderId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockPdfContents, response.getBody());
        assertEquals("attachment; filename=invoice_" + orderId + ".pdf", response.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION));

        verify(invoiceService, times(1)).generateInvoicePdf(orderId, customerName, products, totalAmount);
    }

    @Test
    void testGenerateInvoice_InternalServerError() throws IOException {
        // Arrange
        Long orderId = 1L;
        String customerName = "John Doe";
        List<String> products = List.of("Product A", "Product B", "Product C");
        double totalAmount = 75.00;

        when(invoiceService.generateInvoicePdf(orderId, customerName, products, totalAmount)).thenThrow(new IOException("Error generating PDF"));

        // Act
        ResponseEntity<byte[]> response = invoiceController.generateInvoice(orderId);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(null, response.getBody());

        verify(invoiceService, times(1)).generateInvoicePdf(orderId, customerName, products, totalAmount);
    }
}