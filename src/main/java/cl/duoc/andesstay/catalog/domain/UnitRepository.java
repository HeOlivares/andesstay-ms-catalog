package cl.duoc.andesstay.catalog.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UnitRepository extends JpaRepository<Unit, Long> {
	Optional<Unit> findByCode(String code);
	boolean existsByCode(String code);
}
