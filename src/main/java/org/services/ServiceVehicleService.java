package org.services;

import org.models.Client;
import org.models.ServiceVehicle;
import org.repositories.ClientRepository;
import org.repositories.ServiceVehicleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class ServiceVehicleService {

    private final ServiceVehicleRepository vehicleRepository;
    private final ClientRepository clientRepository;

    public ServiceVehicleService(ServiceVehicleRepository vehicleRepository,
                                 ClientRepository clientRepository) {
        this.vehicleRepository = vehicleRepository;
        this.clientRepository = clientRepository;
    }

    public Optional<ServiceVehicle> findByPlaca(String placa) {
        return vehicleRepository.findByPlacaIgnoreCase(placa);
    }

    public Optional<ServiceVehicle> findById(Long id) {
        return vehicleRepository.findById(id);
    }

    @Transactional
    public ServiceVehicle saveWithClient(ServiceVehicle vehicle, Client client) {
        Client savedClient = clientRepository.save(client);
        vehicle.setClient(savedClient);
        return vehicleRepository.save(vehicle);
    }

    @Transactional
    public ServiceVehicle save(ServiceVehicle vehicle) {
        return vehicleRepository.save(vehicle);
    }

    public boolean existsByPlaca(String placa) {
        return vehicleRepository.existsByPlacaIgnoreCase(placa);
    }
}
