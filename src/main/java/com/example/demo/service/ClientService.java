package com.example.demo.service;

import com.example.demo.dto.ClientRequest;
import com.example.demo.dto.ClientResponse;
import com.example.demo.entity.Client;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.repository.ClientRepository;
import com.example.demo.repository.OrderInfoRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.UserRepository;
import java.util.List;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ClientService {

    private final ClientRepository clientRepository;
    private final OrderInfoRepository orderInfoRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public ClientService(
            ClientRepository clientRepository,
            OrderInfoRepository orderInfoRepository,
            ProductRepository productRepository,
            UserRepository userRepository
    ) {
        this.clientRepository = clientRepository;
        this.orderInfoRepository = orderInfoRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public ClientResponse createClient(ClientRequest request, long actorId) {
        requireClientManager(actorId);

        if (clientRepository.findByEmailIgnoreCase(request.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Client email is already registered");
        }

        try {
            Client client = clientRepository.save(new Client(
                    request.getName(),
                    request.getOrganisationName(),
                    request.getOrganisationAddress(),
                    request.getEmail(),
                    request.getPhone()
            ));
            return ClientResponse.from(client);
        } catch (DataIntegrityViolationException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Client email is already registered");
        }
    }

    @Transactional(readOnly = true)
    public List<ClientResponse> getClients(long actorId) {
        requireClientManager(actorId);
        return clientRepository.findAll()
                .stream()
                .map(ClientResponse::from)
                .toList();
    }

    @Transactional
    public ClientResponse updateClient(long clientId, ClientRequest request, long actorId) {
        requireClientManager(actorId);

        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Client not found"));

        clientRepository.findByEmailIgnoreCase(request.getEmail())
                .filter(existing -> existing.getId() != clientId)
                .ifPresent(existing -> {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Client email is already registered");
                });

        client.update(
                request.getName(),
                request.getOrganisationName(),
                request.getOrganisationAddress(),
                request.getEmail(),
                request.getPhone()
        );

        try {
            return ClientResponse.from(clientRepository.save(client));
        } catch (DataIntegrityViolationException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Client email is already registered");
        }
    }

    @Transactional
    public void deleteClient(long clientId, long actorId) {
        requireClientManager(actorId);

        if (!clientRepository.existsById(clientId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Client not found");
        }

        if (productRepository.existsByClientId(clientId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Client cannot be deleted because products are assigned to it");
        }

        if (orderInfoRepository.existsByClientId(clientId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Client cannot be deleted because orders are assigned to it");
        }

        clientRepository.deleteById(clientId);
    }

    private User requireClientManager(long actorId) {
        User actor = getActor(actorId);
        if (actor.getRole() != Role.ASSOCIATE && actor.getRole() != Role.ADMIN && actor.getRole() != Role.SUPER_ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Client management is not allowed");
        }

        return actor;
    }

    private User getActor(long actorId) {
        return userRepository.findById(actorId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Login is required"));
    }
}
