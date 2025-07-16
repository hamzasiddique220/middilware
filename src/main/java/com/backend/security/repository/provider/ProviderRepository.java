package com.backend.security.repository.provider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.backend.security.model.provider.Provider;

@Repository
public interface ProviderRepository extends JpaRepository<Provider, Integer> {
    // You can add custom queries here if needed
}

