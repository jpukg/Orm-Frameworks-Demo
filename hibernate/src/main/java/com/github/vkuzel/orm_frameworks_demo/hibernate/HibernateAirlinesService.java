package com.github.vkuzel.orm_frameworks_demo.hibernate;

import com.github.vkuzel.orm_frameworks_demo.hibernate.domain.Operator;
import com.github.vkuzel.orm_frameworks_demo.hibernate.domain.Plane;
import com.github.vkuzel.orm_frameworks_demo.hibernate.repository.OperatorRepository;
import com.github.vkuzel.orm_frameworks_demo.hibernate.repository.PlaneRepository;
import com.github.vkuzel.orm_frameworks_demo.hibernate.repository.RegistrationRepository;
import com.github.vkuzel.orm_frameworks_demo.service.AirlinesService;
import com.github.vkuzel.orm_frameworks_demo.transport.OperatorDetail;
import com.github.vkuzel.orm_frameworks_demo.transport.PlaneDetail;
import com.github.vkuzel.orm_frameworks_demo.transport.RegistrationDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HibernateAirlinesService implements AirlinesService {

    private final PlaneRepository planeRepository;
    private final OperatorRepository operatorRepository;
    private final RegistrationRepository registrationRepository;

    @Autowired
    public HibernateAirlinesService(PlaneRepository planeRepository, OperatorRepository operatorRepository, RegistrationRepository registrationRepository) {
        this.planeRepository = planeRepository;
        this.operatorRepository = operatorRepository;
        this.registrationRepository = registrationRepository;
    }

    @Override
    public PlaneDetail newPlaneDetailInstance() {
        return new Plane();
    }

    @Override
    @Transactional
    public PlaneDetail createPlane(PlaneDetail planeDetail) {
        return planeRepository.save((Plane) planeDetail);
    }

    @Override
    @Transactional
    public PlaneDetail updatePlane(PlaneDetail planeDetail) {
        return planeRepository.save((Plane) planeDetail);
    }

    @Override
    @Transactional
    public PlaneDetail updatePlaneTransactionalThatThrowsException(PlaneDetail plane) {
        planeRepository.save((Plane) plane);
        throw new IllegalStateException();
    }

    @Override
    public PlaneDetail findPlaneByName(String name) {
        return planeRepository.findByName(name);
    }

    @Override
    @Transactional(readOnly = true)
    public OperatorDetail findOperatorByName(String languageCode, String name) {
        return operatorRepository.findByName(languageCode, name);
    }

    @Override
    public Page<OperatorDetail> findAllOperators(String languageCode, Pageable pageable) {
        Sort.Order nameOrder = pageable.getSort().getOrderFor("name");
        if (nameOrder != null) {
            Iterator<Sort.Order> iterator = pageable.getSort().iterator();
            iterator.next();
            if (iterator.hasNext()) {
                throw new IllegalArgumentException("When sorted by name column no more columns can be used to sort!");
            }

            // Because name column is of type JSON and I'd like to sort it by
            // one of the JSON's values I have to create this custom
            // implementation.
            return findAllOperatorsOrderByName(languageCode, pageable, nameOrder.isAscending());
        } else {
            return findAllOperatorsOrdered(pageable);
        }
    }

    private Page<OperatorDetail> findAllOperatorsOrderByName(String languageCode, Pageable pageable, boolean ascending) {
        List<Operator> operators;
        if (ascending) {
            operators = operatorRepository.findAllOrderByNameAsc(languageCode, pageable.getOffset(), pageable.getPageSize());
        } else {
            operators = operatorRepository.findAllOrderByNameDesc(languageCode, pageable.getOffset(), pageable.getPageSize());
        }
        return new PageImpl<>(
                operators.stream().map(o -> (OperatorDetail) o).collect(Collectors.toList()),
                pageable,
                operatorRepository.count()
        );
    }

    private Page<OperatorDetail> findAllOperatorsOrdered(Pageable pageable) {
        Page<Operator> operatorPage = operatorRepository.findAll(pageable);
        return new PageImpl<>(
                operatorPage.getContent().stream().map(o -> (OperatorDetail) o).collect(Collectors.toList()),
                pageable,
                operatorPage.getTotalElements()
        );
    }

    @Override
    @Transactional
    public RegistrationDetail registerNewPlane(PlaneDetail planeDetail, OperatorDetail operatorDetail, String registrationNumber) {
        long registrationId = registrationRepository.registerNewPlane(planeDetail.getId(), operatorDetail.getId(), registrationNumber);
        return registrationRepository.findOne(registrationId);
    }

    @Override
    @Transactional(readOnly = true)
    public RegistrationDetail findRegistrationByRegistrationNumber(String registrationNumber) {
        return registrationRepository.findByRegistrationNumber(registrationNumber);
    }
}
