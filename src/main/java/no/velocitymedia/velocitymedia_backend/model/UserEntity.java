package no.velocitymedia.velocitymedia_backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users", schema = "dbo")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;
    private String accountId;
    private String invoiceId;

    public UserEntity(){

    }


    public UserEntity(String username, String password) {
        super();
        this.username = username;
        this.password = password;
    }


    public Long getId() {
        return id;
    }


    public String getUsername() {
        return username;
    }


    public void setUsername(String username) {
        this.username = username;
    }


    public String getPassword() {
        return password;
    }


    public void setPassword(String password) {
        this.password = password;
    }


    public String getAccountId() {
        return accountId;
    }


    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }


    public String getInvoiceId() {
        return invoiceId;
    }


    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }


    
    


    


}
