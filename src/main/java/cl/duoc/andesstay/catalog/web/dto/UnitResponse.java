package cl.duoc.andesstay.catalog.web.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record UnitResponse(
		Long id,
		String code,
		String name,
		BigDecimal rate,
		boolean available,
		Instant createdAt,
		Instant updatedAt
) {
}
