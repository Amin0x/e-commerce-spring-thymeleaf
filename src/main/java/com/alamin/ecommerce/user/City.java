package com.alamin.ecommerce.user;

import jakarta.persistence.*;

@Entity
@Table(name = "tbl_cities")
public class City {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String nameAr;
    private String estimatedDelivery;
    private String estimatedDeliveryUnit;
    private int deliveryPriority;
    private int deliveryPrice;
    
    @ManyToOne(fetch = FetchType.LAZY)
    private State state;

    public City() {
    }

    public City(Integer id, String name, String nameAr, State state) {
        this.id = id;
        this.name = name;
        this.state = state;
        this.nameAr = nameAr;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNameAr() {
        return nameAr;
    }

    public void setNameAr(String nameAr) {
        this.nameAr = nameAr;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public String getEstimatedDelivery() {
        return estimatedDelivery;
    }

    public void setEstimatedDelivery(String estimatedDelivery) {
        this.estimatedDelivery = estimatedDelivery;
    }

    public String getEstimatedDeliveryUnit(){
        return estimatedDeliveryUnit;
    }

    public void setEstimatedDeliveryUnit(String estimatedDeliveryUnit) {
        this.estimatedDeliveryUnit = estimatedDeliveryUnit;
    }

    public int getDeliveryPriority() {
        return deliveryPriority;
    }

    public void setDeliveryPriority(int deliveryPriority) {
        this.deliveryPriority = deliveryPriority;
    }

    public int getDeliveryPrice() {
        return deliveryPrice;
    }

    public void setDeliveryPrice(int deliveryPrice) {
        this.deliveryPrice = deliveryPrice;
    }

    @Override
    public String toString() {
        return "City [id=" + id + ", name=" + name + ", nameAr=" + nameAr + ", estimatedDelivery=" + estimatedDelivery
                + ", estimatedDeliveryUnit=" + estimatedDeliveryUnit + ", deliveryPriority=" + deliveryPriority
                + ", deliveryPrice=" + deliveryPrice + ", state=" + state + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((nameAr == null) ? 0 : nameAr.hashCode());
        result = prime * result + ((estimatedDelivery == null) ? 0 : estimatedDelivery.hashCode());
        result = prime * result + ((estimatedDeliveryUnit == null) ? 0 : estimatedDeliveryUnit.hashCode());
        result = prime * result + deliveryPriority;
        result = prime * result + deliveryPrice;
        result = prime * result + ((state == null) ? 0 : state.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        City other = (City) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (nameAr == null) {
            if (other.nameAr != null)
                return false;
        } else if (!nameAr.equals(other.nameAr))
            return false;
       
               
        return true;
    }

    

    
}