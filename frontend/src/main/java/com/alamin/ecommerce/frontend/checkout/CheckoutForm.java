package com.alamin.ecommerce.frontend.checkout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Backing object for the checkout form posted to {@code POST /checkout}.
 */
public class CheckoutForm {

    private String firstName;
    private String lastName;
    private String contact;
    private String city;
    private String shipingPhone;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getShipingPhone() {
        return shipingPhone;
    }

    public void setShipingPhone(String shipingPhone) {
        this.shipingPhone = shipingPhone;
    }
}
