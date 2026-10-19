package cl.duoc.andesstay.catalog.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
class UnitAvailabilityTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void listsCreatesAndUpdatesUnitsWithRateAndAvailability() throws Exception {
		mockMvc.perform(get("/api/catalog/units"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").isArray());

		MvcResult created = mockMvc.perform(post("/api/catalog/units")
						.contentType(MediaType.APPLICATION_JSON)
						.content(unit("CAB-01", "45000.00", true)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.code").value("CAB-01"))
				.andExpect(jsonPath("$.rate").value(45000.00))
				.andExpect(jsonPath("$.available").value(true))
				.andReturn();

		Number id = com.jayway.jsonpath.JsonPath.read(created.getResponse().getContentAsString(), "$.id");

		mockMvc.perform(put("/api/catalog/units/" + id)
						.contentType(MediaType.APPLICATION_JSON)
						.content(unit("CAB-01", "52000.00", true)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.rate").value(52000.00))
				.andExpect(jsonPath("$.available").value(true));
	}

	@Test
	void rejectsUnitWithoutRateOrAvailability() throws Exception {
		mockMvc.perform(post("/api/catalog/units")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"code\":\"CAB-02\",\"name\":\"Cabaña 2\",\"available\":true}"))
				.andExpect(status().isBadRequest());

		mockMvc.perform(post("/api/catalog/units")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"code\":\"CAB-03\",\"name\":\"Cabaña 3\",\"rate\":10000}"))
				.andExpect(status().isBadRequest());

		mockMvc.perform(post("/api/catalog/units")
						.contentType(MediaType.APPLICATION_JSON)
						.content(unit("CAB-04", "-1", true)))
				.andExpect(status().isBadRequest());
	}

	@Test
	void confirmationMakesTheUnitUnavailable() throws Exception {
		MvcResult created = mockMvc.perform(post("/api/catalog/units")
						.contentType(MediaType.APPLICATION_JSON)
						.content(unit("CAB-10", "30000.00", true)))
				.andExpect(status().isCreated())
				.andReturn();
		Number id = com.jayway.jsonpath.JsonPath.read(created.getResponse().getContentAsString(), "$.id");

		mockMvc.perform(post("/api/catalog/units/" + id + "/confirmations"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.available").value(false));

		mockMvc.perform(get("/api/catalog/units/" + id))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.available").value(false));

		mockMvc.perform(post("/api/catalog/units/" + id + "/confirmations"))
				.andExpect(status().isConflict());
	}

	@Test
	void confirmationOfUnknownUnitIsNotFound() throws Exception {
		mockMvc.perform(post("/api/catalog/units/9999/confirmations"))
				.andExpect(status().isNotFound());
	}

	private static String unit(String code, String rate, boolean available) {
		return "{\"code\":\"" + code + "\",\"name\":\"Cabaña " + code + "\",\"rate\":" + rate + ",\"available\":" + available + "}";
	}
}
