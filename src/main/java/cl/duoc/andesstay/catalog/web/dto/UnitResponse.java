package cl.duoc.andesstay.catalog.web.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record UnitResponse(
		Long id,
		String code,
		String name,
		BigDecimal rate,
		boolean available,
		String description,
		String location,
		Integer maxGuests,
		String imageUrl,
		String amenities,
		Integer bedrooms,
		Integer bathrooms,
		Instant createdAt,
		Instant updatedAt
) {
}
