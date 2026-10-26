package com.alamin.ecommerce.invoice;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import lombok.*;

@Data
@Entity
@Table(name = "tbl_invoices")
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String orderId;
    private String orderNumber;
    private String orderStatus;
    private String orderDate;
    private String orderCurrency;
    private String orderTotalAmount;
    private String orderShippingAmount;
    private String orderTaxAmount;
    private String invoiceDate;
    private String invoiceDueDate;
    private String invoiceStatus;
    private String invoiceNumber;
    private String customerEmail;
    private String customerPhone;
    private String customerAddress;
    private String customerCity;
    private String customerState;
    private String customerZipCode;
    private String customerCountry;
    private String customerCompanyName;
    private String customerTaxId;
    private String customerFirstName;
    private String customerLastName;
    private String customerFullName;
    private String customerCompanyAddress;
    private String customerCompanyCity;
    private String customerCompanyState;
    private String customerCompanyZipCode;
    private String customerCompanyCountry;
    private String customerCompanyPhone;
    private String customerName;
    private double totalAmount;

    private LocalDateTime created;
    private LocalDateTime updated;
    private LocalDateTime deleted;

    // Getters and Setters
}
