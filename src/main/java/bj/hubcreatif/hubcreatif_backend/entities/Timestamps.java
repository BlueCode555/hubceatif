package bj.hubcreatif.hubcreatif_backend.entities;

import bj.hubcreatif.hubcreatif_backend.utils.AppUtil;
import jakarta.persistence.*;
import org.hibernate.envers.Audited;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Audited
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class Timestamps {

    @Column(name = "uuid", nullable = true, unique = true, updatable = false, length = 36)
    protected String uuid = UUID.randomUUID().toString();


    @Column(name = "modifier_par", length = 36)
    protected String updatedBy;

    @Column(name = "modifier_le")
    @LastModifiedDate
    protected LocalDateTime updatedAt;

    @PrePersist
    protected void onPersist() {
        if (this.uuid == null) {
            this.uuid = UUID.randomUUID().toString();
        }
        onUpdate();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
        this.updatedBy = AppUtil.connectedUserKeycloakUuid();
    }

    public abstract Integer getId();

    public String getUuid()            { return uuid; }
    public String getUpdatedBy()       { return updatedBy; }
    public LocalDateTime getUpdatedAt(){ return updatedAt; }

    public Timestamps setUuid(String uuid)               { this.uuid = uuid; return this; }
    public Timestamps setUpdatedBy(String updatedBy)     { this.updatedBy = updatedBy; return this; }
    public Timestamps setUpdatedAt(LocalDateTime updatedAt){ this.updatedAt = updatedAt; return this; }
}