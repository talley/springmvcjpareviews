package com.talleyouro.springmvcjpademo;


import jakarta.persistence.*;
import java.time.LocalDate;
import org.springframework.data.domain.Persistable;

@Entity
@Table(name = "shippers")
public class Shipper implements Persistable<Short> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    @Column(name = "shipper_id")
    private Short shipperId;

    @Column(name = "company_name", length = 40)
    private String companyName;

    @Column(name = "phone", length = 24)
    private String phone;

    @Column(name = "addedon")
    private LocalDate addedOn;

    @Column(name = "addedby")
    private String addedBy;

    @Column(name = "editedon")
    private LocalDate editedOn;

    @Column(name = "editedby")
    private String editedBy;

    public Shipper() {}

    public Shipper(String companyName, String phone, String addedBy) {
        this.companyName = companyName;
        this.phone = phone;
        this.addedBy = addedBy;
        this.addedOn = LocalDate.now();
    }

    public Short getShipperId() { return shipperId; }
    public void setShipperId(Short shipperId) { this.shipperId = shipperId; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public LocalDate getAddedOn() { return addedOn; }
    public void setAddedOn(LocalDate addedOn) { this.addedOn = addedOn; }

    public String getAddedBy() { return addedBy; }
    public void setAddedBy(String addedBy) { this.addedBy = addedBy; }

    public LocalDate getEditedOn() { return editedOn; }
    public void setEditedOn(LocalDate editedOn) { this.editedOn = editedOn; }

    public String getEditedBy() { return editedBy; }
    public void setEditedBy(String editedBy) { this.editedBy = editedBy; }

    @Override
    public String toString() {
        return "Shipper [id=" + shipperId + ", company=" + companyName + ", phone=" + phone
                + ", addedOn=" + addedOn + ", addedBy=" + addedBy
                + ", editedOn=" + editedOn + ", editedBy=" + editedBy + "]";
    }
    
    @Transient
    private boolean isNew = true;

    @Override
    public Short getId() {
        return shipperId;
    }

    @Override
    public boolean isNew() {
        return isNew;
    }

    @PostLoad
    @PostPersist
    void markNotNew() {
        this.isNew = false;
    }
}