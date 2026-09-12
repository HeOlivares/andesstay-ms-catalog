package cl.duoc.andesstay.catalog.web;

import cl.duoc.andesstay.catalog.service.UnitService;
import cl.duoc.andesstay.catalog.web.dto.UnitRequest;
import cl.duoc.andesstay.catalog.web.dto.UnitResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/catalog")
@Tag(name = "Catalog", description = "CRUD de unidades habitacionales")
public class UnitController {

	private final UnitService unitService;

	public UnitController(UnitService unitService) {
		this.unitService = unitService;
	}

	@GetMapping("/health")
	@Operation(summary = "Health del microservicio catálogo")
	public Map<String, String> health() {
		return Map.of("status", "UP", "service", "andesstay-ms-catalog");
	}

	@GetMapping("/units")
	@Operation(summary = "Listar unidades")
	public List<UnitResponse> list() {
		return unitService.findAll();
	}

	@GetMapping("/units/{id}")
	@Operation(summary = "Obtener unidad por id")
	public UnitResponse get(@PathVariable Long id) {
		return unitService.findById(id);
	}

	@PostMapping("/units")
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "Crear unidad")
	public UnitResponse create(@Valid @RequestBody UnitRequest request) {
		return unitService.create(request);
	}

	@PutMapping("/units/{id}")
	@Operation(summary = "Actualizar unidad (tarifa / disponibilidad)")
	public UnitResponse update(@PathVariable Long id, @Valid @RequestBody UnitRequest request) {
		return unitService.update(id, request);
	}
}
