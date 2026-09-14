package cl.duoc.andesstay.catalog.web.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public class UnitRequest {

	@NotBlank
	@Size(max = 50)
	private String code;

	@NotBlank
	@Size(max = 120)
	private String name;

	@NotNull
	@DecimalMin(value = "0.0", inclusive = true)
	private BigDecimal rate;

	@NotNull
	private Boolean available;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BigDecimal getRate() {
		return rate;
	}

	public void setRate(BigDecimal rate) {
		this.rate = rate;
	}

	public Boolean getAvailable() {
		return available;
	}

	public void setAvailable(Boolean available) {
		this.available = available;
	}
}
