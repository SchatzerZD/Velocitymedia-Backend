package no.velocitymedia.velocitymedia_backend.model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "projects")
public class ProjectEntity {
    

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    private String name;
    
    private String invoiceURL;

    private Boolean contractSigned;
    private String contractPath;

    @ElementCollection(targetClass = VideoFlag.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "project_flags", joinColumns = @JoinColumn(name = "project_id"))
    @Column(name = "flag")
    private Set<VideoFlag> flags = new HashSet<>();

    private long projectCreatedAt;

    public ProjectEntity() {
        contractSigned = false;
    }

    public ProjectEntity(UserEntity user, String name) {
        super();
        this.user = user;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getContractSigned() {
        return contractSigned;
    }

    public void setContractSigned(Boolean contractSigned) {
        this.contractSigned = contractSigned;
    }

    public String getContractPath() {
        return contractPath;
    }

    public void setContractPath(String contractPath) {
        this.contractPath = contractPath;
    }

    public void addFlag(VideoFlag flag) {
        this.flags.add(flag);
    }
    
    public void removeFlag(VideoFlag flag) {
        this.flags.remove(flag);
    }

    public void setFlags(List<VideoFlag> flagList) {
        this.flags.clear();
        flagList.forEach(flag -> this.addFlag(flag));
    }

    public Set<VideoFlag> getFlags() {
        return this.flags;
    }

    public long getProjectCreatedAt() {
        return projectCreatedAt;
    }

    public void setProjectCreatedAt(long projectCreatedAt) {
        this.projectCreatedAt = projectCreatedAt;
    }


    public String getInvoiceURL() {
        return invoiceURL;
    }

    public void setInvoiceURL(String invoiceURL) {
        this.invoiceURL = invoiceURL;
    }


    

    

}
