package com.example.ludogoriesoft.lukeriaerpapi.services;

import com.example.ludogoriesoft.lukeriaerpapi.dtos.ClientDTO;
import com.example.ludogoriesoft.lukeriaerpapi.models.Client;
import com.example.ludogoriesoft.lukeriaerpapi.repository.ClientRepository;
import io.micrometer.common.util.StringUtils;
import jakarta.validation.ValidationException;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ClientService {
    private final ClientRepository clientRepository;
    private final ModelMapper modelMapper;
    private static String REGEX_FOR_ENGLISH_FIELDS = "^[a-zA-Z0-9\s!@#$%^&*()-_=+'\"]*$";

    public List<ClientDTO> getAllClients() {
        List<Client> clients = clientRepository.findByDeletedFalse();
        return clients.stream().map(client -> modelMapper.map(client, ClientDTO.class)).toList();
    }

    public ClientDTO getClientById(Long id) throws ChangeSetPersister.NotFoundException {
        Client client = clientRepository.findByIdAndDeletedFalse(id).orElseThrow(ChangeSetPersister.NotFoundException::new);
        return modelMapper.map(client, ClientDTO.class);
    }

    public ClientDTO createClient(ClientDTO clientDTO) {
        if (StringUtils.isBlank(clientDTO.getBusinessName())) {
            throw new ValidationException("Business name is required!");
        }
        if (!clientDTO.getIdNumEIK().matches("\\d{5,}")) {
            throw new ValidationException("The EIK number should contain at least 5 numbers!");
        }
        if (StringUtils.isBlank(clientDTO.getAddress())) {
            throw new ValidationException("Address is required!");
        }
        if (StringUtils.isBlank(clientDTO.getEnglishAddress())) {
            throw new ValidationException("Address in english is required!");
        }
        if (StringUtils.isBlank(clientDTO.getEnglishBusinessName())) {
            throw new ValidationException("Business name is english is required!");
        }
        if (!clientDTO.getEnglishBusinessName().matches(REGEX_FOR_ENGLISH_FIELDS)) {
            throw new ValidationException("English name can contain only letters in English");
        }
        if (!clientDTO.getEnglishMol().matches(REGEX_FOR_ENGLISH_FIELDS)) {
            throw new ValidationException("English MOL can contain only letters in English");
        }
        if (!clientDTO.getEnglishAddress().matches(REGEX_FOR_ENGLISH_FIELDS)) {
            throw new ValidationException("English address can contain only letters in English");
        }
        Client clientEntity = clientRepository.save(modelMapper.map(clientDTO, Client.class));
        return modelMapper.map(clientEntity, ClientDTO.class);
    }

    public ClientDTO updateClient(Long id, ClientDTO clientDTO) throws ChangeSetPersister.NotFoundException {
        Client existingClient = clientRepository.findByIdAndDeletedFalse(id).orElseThrow(ChangeSetPersister.NotFoundException::new);
        if (StringUtils.isBlank(clientDTO.getBusinessName())) {
            throw new ValidationException("Business name is required!");
        }
        if (!clientDTO.getIdNumEIK().matches("\\d{5,}")) {
            throw new ValidationException("The EIK number should contain at least 5 numbers!");
        }
        if (StringUtils.isBlank(clientDTO.getAddress())) {
            throw new ValidationException("Address is required!");
        }
        if (StringUtils.isBlank(clientDTO.getEnglishBusinessName())) {
            throw new ValidationException("Business name is english is required!");
        }
        if (!clientDTO.getEnglishBusinessName().matches(REGEX_FOR_ENGLISH_FIELDS)) {
            throw new ValidationException("English name can contain only letters in English");
        }
        if (!clientDTO.getEnglishAddress().matches(REGEX_FOR_ENGLISH_FIELDS)) {
            throw new ValidationException("English address can contain only letters in English");
        }
        if (!clientDTO.getEnglishMol().matches(REGEX_FOR_ENGLISH_FIELDS)) {
            throw new ValidationException("English MOL can contain only letters in English");
        }
        if (StringUtils.isBlank(clientDTO.getEnglishAddress())) {
            throw new ValidationException("Address in english is required!");
        }
        existingClient.setBusinessName(clientDTO.getBusinessName());
        existingClient.setIdNumEIK(clientDTO.getIdNumEIK());
        existingClient.setHasIdNumDDS(clientDTO.isHasIdNumDDS());
        existingClient.setAddress(clientDTO.getAddress());
        existingClient.setBulgarianClient(clientDTO.isBulgarianClient());
        existingClient.setMol(clientDTO.getMol());
        existingClient.setEnglishAddress(clientDTO.getEnglishAddress());
        existingClient.setEnglishBusinessName(clientDTO.getEnglishBusinessName());
        existingClient.setEnglishMol(clientDTO.getEnglishMol());
        Client updatedClient = clientRepository.save(existingClient);
        updatedClient.setId(id);
        return modelMapper.map(updatedClient, ClientDTO.class);
    }

    public void deleteClient(Long id) throws ChangeSetPersister.NotFoundException {
        Client client = clientRepository.findByIdAndDeletedFalse(id).orElseThrow(ChangeSetPersister.NotFoundException::new);
        client.setDeleted(true);
        clientRepository.save(client);
    }
}

