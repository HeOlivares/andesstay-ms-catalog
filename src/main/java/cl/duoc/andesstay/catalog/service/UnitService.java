package cl.duoc.andesstay.catalog.service;

import cl.duoc.andesstay.catalog.domain.Unit;
import cl.duoc.andesstay.catalog.domain.UnitRepository;
import cl.duoc.andesstay.catalog.web.dto.UnitRequest;
import cl.duoc.andesstay.catalog.web.dto.UnitResponse;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class UnitService {

	private final UnitRepository unitRepository;

	public UnitService(UnitRepository unitRepository) {
		this.unitRepository = unitRepository;
	}

	@Transactional(readOnly = true)
	public List<UnitResponse> findAll() {
		return unitRepository.findAll().stream().map(this::toResponse).toList();
	}

	@Transactional(readOnly = true)
	public UnitResponse findById(Long id) {
		return toResponse(getOrThrow(id));
	}

	public UnitResponse create(UnitRequest request) {
		if (unitRepository.existsByCode(request.getCode())) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Unit code already exists: " + request.getCode());
		}
		Unit unit = new Unit();
		apply(unit, request);
		return toResponse(unitRepository.save(unit));
	}

	public UnitResponse update(Long id, UnitRequest request) {
		Unit unit = getOrThrow(id);
		unitRepository.findByCode(request.getCode())
				.filter(existing -> !existing.getId().equals(id))
				.ifPresent(existing -> {
					throw new ResponseStatusException(HttpStatus.CONFLICT, "Unit code already exists: " + request.getCode());
				});
		apply(unit, request);
		return toResponse(unitRepository.save(unit));
	}

	private Unit getOrThrow(Long id) {
		return unitRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unit not found: " + id));
	}

	private void apply(Unit unit, UnitRequest request) {
		unit.setCode(request.getCode().trim());
		unit.setName(request.getName().trim());
		unit.setRate(request.getRate());
		unit.setAvailable(Boolean.TRUE.equals(request.getAvailable()));
	}

	private UnitResponse toResponse(Unit unit) {
		return new UnitResponse(
				unit.getId(),
				unit.getCode(),
				unit.getName(),
				unit.getRate(),
				unit.isAvailable(),
				unit.getCreatedAt(),
				unit.getUpdatedAt()
		);
	}
}
