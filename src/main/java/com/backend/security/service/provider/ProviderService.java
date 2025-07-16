package com.backend.security.service.provider;
import com.backend.security.model.provider.Provider;
import com.backend.security.repository.provider.ProviderRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProviderService {

    private final ProviderRepository providerRepository;

    public List<Provider> getAllProviders() {
        return providerRepository.findAll();
    }

    public Optional<Provider> getProviderById(int id) {
        return providerRepository.findById(id);
    }

    public Provider createProvider(Provider provider) {
        return providerRepository.save(provider);
    }

    public Provider updateProvider(int id, Provider updatedProvider) {
        return providerRepository.findById(id)
                .map(existing -> {
                    existing.setName(updatedProvider.getName());
                    existing.setStatus(updatedProvider.getStatus());
                    return providerRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Provider not found with id: " + id));
    }

    public void deleteProvider(int id) {
        providerRepository.deleteById(id);
    }
}

