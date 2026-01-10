package hwan.diary.domain.user.repository;

import hwan.diary.domain.user.entity.SnsAccount;
import hwan.diary.domain.user.values.Provider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SnsAccountRepository extends JpaRepository<SnsAccount, Long> {

    Optional<SnsAccount> findByProviderAndProviderId(Provider provider, String providerID);
}
