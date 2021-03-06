package com.github.vkuzel.orm_frameworks_demo.openjpa.domain;

import com.github.vkuzel.orm_frameworks_demo.transport.RegistrationDetail;

import javax.persistence.*;

@Entity(name = "registrations")
public class Registration implements RegistrationDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "registrations_seq_gen")
    @SequenceGenerator(name = "registrations_seq_gen", sequenceName = "registrations_id_seq")
    private Long id;
    @Column(name = "plane_id")
    private long planeId;
    @Column(name = "operator_id")
    private long operatorId;
    @Column(name = "registration_number")
    private String registrationNumber;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public long getPlaneId() {
        return planeId;
    }

    @Override
    public void setPlaneId(long planeId) {
        this.planeId = planeId;
    }

    @Override
    public long getOperatorId() {
        return operatorId;
    }

    @Override
    public void setOperatorId(long operatorId) {
        this.operatorId = operatorId;
    }

    @Override
    public String getRegistrationNumber() {
        return registrationNumber;
    }

    @Override
    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }
}
