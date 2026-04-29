package com.company.onboarding.entity;

import io.jmix.core.annotation.DeletedBy;
import io.jmix.core.annotation.DeletedDate;
import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.metamodel.annotation.JmixEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.UUID;

@JmixEntity
@Table(name = "MONITOR")
@Entity
public class Monitor {
    @JmixGeneratedValue
    @Column(name = "ID", nullable = false)
    @Id
    private UUID id;

    @Column(name = "VERSION", nullable = false)
    @Version
    private Integer version;

    @DeletedBy
    @Column(name = "DELETED_BY")
    private String deletedBy;

    @DeletedDate
    @Column(name = "DELETED_DATE")
    private OffsetDateTime deletedDate;

    @NotNull
    @Column(name = "DATE_TEST", nullable = false)
    private LocalDate dateTest;

    @Column(name = "TIME_TEST")
    private LocalTime timeTest;

    @NotNull
    @Column(name = "LOWPRES", nullable = false)
    private Integer lowpres;

    @Column(name = "UPPERPRES", nullable = false)
    @NotNull
    private Integer upperpres;

    @Column(name = "PULSE", nullable = false)
    @NotNull
    private Integer pulse;

    @Column(name = "STATUS")
    private Integer status;

    public DirHealth getStatus() {
        return status == null ? null : DirHealth.fromId(status);
    }

    public void setStatus(DirHealth status) {
        this.status = status == null ? null : status.getId();
    }

    public LocalTime getTimeTest() {
        return timeTest;
    }

    public void setTimeTest(LocalTime timeTest) {
        this.timeTest = timeTest;
    }

    public void setDateTest(LocalDate dateTest) {
        this.dateTest = dateTest;
    }

    public LocalDate getDateTest() {
        return dateTest;
    }

    public Integer getPulse() {
        return pulse;
    }

    public void setPulse(Integer pulse) {
        this.pulse = pulse;
    }

    public Integer getUpperpres() {
        return upperpres;
    }

    public void setUpperpres(Integer upperpres) {
        this.upperpres = upperpres;
    }

    public void setLowpres(Integer lowpres) {
        this.lowpres = lowpres;
    }

    public Integer getLowpres() {
        return lowpres;
    }

    public OffsetDateTime getDeletedDate() {
        return deletedDate;
    }

    public void setDeletedDate(OffsetDateTime deletedDate) {
        this.deletedDate = deletedDate;
    }

    public String getDeletedBy() {
        return deletedBy;
    }

    public void setDeletedBy(String deletedBy) {
        this.deletedBy = deletedBy;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

}