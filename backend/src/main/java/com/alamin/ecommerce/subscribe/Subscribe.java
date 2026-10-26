package com.alamin.ecommerce.subscribe;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@ToString
@Entity(name = "tbl_subscribe")
@Table(indexes = {
        @Index(name = "idx_subscribe_id_email", columnList = "id, email")
})
public class Subscribe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
    @Column(unique = true, nullable = false)
    private String email;
    @Column(nullable = false, length = 100)
    private String name;
    @Column(nullable = false, length = 20)
    private String status;
    @Column(nullable = false)
    private String verificationToken;
    @Column(nullable = false)
    private LocalDateTime tokenExpiration;
    private LocalDateTime EmailDate;
    private LocalDateTime createdAt;

    public Subscribe() {
    }

    public Subscribe(String email, String name, String status, String verificationToken,
                     LocalDateTime tokenExpiration, LocalDateTime EmailDate, LocalDateTime createdAt) {
        this.email = email;
        this.name = name;
        this.status = status;
        this.verificationToken = verificationToken;
        this.tokenExpiration = tokenExpiration;
        this.EmailDate = EmailDate;
        this.createdAt = createdAt;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Subscribe subscribe = (Subscribe) o;
        return getId() != null && Objects.equals(getId(), subscribe.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
