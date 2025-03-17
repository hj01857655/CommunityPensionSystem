package com.example.service;

import com.example.entity.ServiceEntity;

import java.util.List;

public interface ServiceService {
    List<ServiceEntity> getAllServices();
    ServiceEntity getServiceById(Long id);
    void addService(ServiceEntity service);
    void updateService(ServiceEntity service);
    void deleteService(Long id);
}