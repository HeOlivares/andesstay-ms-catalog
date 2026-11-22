package cl.duoc.andesstay.catalog.web;

import static org.hamcrest.Matchers.hasItem;
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

	@Test
	void createsGetsAndUpdatesOptionalProfileFields() throws Exception {
		String body = """
				{"code":"CAB-20","name":"Cabaña del lago","rate":61000.00,"available":true,\
				"description":"Vista al lago","location":"Pucón","maxGuests":4,\
				"imageUrl":"https://cdn.andesstay.local/cab-20.jpg",\
				"amenities":"wifi, cocina, estacionamiento","bedrooms":2,"bathrooms":1}
				""";

		MvcResult created = mockMvc.perform(post("/api/catalog/units")
						.contentType(MediaType.APPLICATION_JSON)
						.content(body))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.description").value("Vista al lago"))
				.andExpect(jsonPath("$.location").value("Pucón"))
				.andExpect(jsonPath("$.maxGuests").value(4))
				.andExpect(jsonPath("$.imageUrl").value("https://cdn.andesstay.local/cab-20.jpg"))
				.andExpect(jsonPath("$.amenities").value("wifi, cocina, estacionamiento"))
				.andExpect(jsonPath("$.bedrooms").value(2))
				.andExpect(jsonPath("$.bathrooms").value(1))
				.andReturn();
		Number id = com.jayway.jsonpath.JsonPath.read(created.getResponse().getContentAsString(), "$.id");

		mockMvc.perform(get("/api/catalog/units/" + id))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.description").value("Vista al lago"))
				.andExpect(jsonPath("$.maxGuests").value(4))
				.andExpect(jsonPath("$.amenities").value("wifi, cocina, estacionamiento"));

		String updated = """
				{"code":"CAB-20","name":"Cabaña del lago","rate":64000.00,"available":true,\
				"description":"Vista al lago y al volcán","location":"Pucón","maxGuests":6,\
				"imageUrl":"https://cdn.andesstay.local/cab-20b.jpg",\
				"amenities":"wifi, cocina","bedrooms":3,"bathrooms":2}
				""";
		mockMvc.perform(put("/api/catalog/units/" + id)
						.contentType(MediaType.APPLICATION_JSON)
						.content(updated))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.description").value("Vista al lago y al volcán"))
				.andExpect(jsonPath("$.maxGuests").value(6))
				.andExpect(jsonPath("$.bedrooms").value(3))
				.andExpect(jsonPath("$.bathrooms").value(2));
	}

	@Test
	void listsLegacyUnitsThatOmitProfileFields() throws Exception {
		MvcResult created = mockMvc.perform(post("/api/catalog/units")
						.contentType(MediaType.APPLICATION_JSON)
						.content(unit("CAB-21", "18000.00", true)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.description").isEmpty())
				.andExpect(jsonPath("$.maxGuests").isEmpty())
				.andReturn();
		Number id = com.jayway.jsonpath.JsonPath.read(created.getResponse().getContentAsString(), "$.id");

		mockMvc.perform(get("/api/catalog/units"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[?(@.code == 'CAB-21')].code", hasItem("CAB-21")));

		mockMvc.perform(get("/api/catalog/units/" + id))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.description").isEmpty())
				.andExpect(jsonPath("$.location").isEmpty())
				.andExpect(jsonPath("$.maxGuests").isEmpty())
				.andExpect(jsonPath("$.imageUrl").isEmpty())
				.andExpect(jsonPath("$.amenities").isEmpty())
				.andExpect(jsonPath("$.bedrooms").isEmpty())
				.andExpect(jsonPath("$.bathrooms").isEmpty());
	}

	@Test
	void rejectsProfileFieldsOutsideTheirLimits() throws Exception {
		mockMvc.perform(post("/api/catalog/units")
						.contentType(MediaType.APPLICATION_JSON)
						.content(unit("CAB-22", "18000.00", true).replace("}", ",\"maxGuests\":0}")))
				.andExpect(status().isBadRequest());

		String oversized = "x".repeat(1001);
		mockMvc.perform(post("/api/catalog/units")
						.contentType(MediaType.APPLICATION_JSON)
						.content(unit("CAB-23", "18000.00", true).replace("}", ",\"description\":\"" + oversized + "\"}")))
				.andExpect(status().isBadRequest());
	}

	@Test
	void confirmationDoesNotClearProfileFields() throws Exception {
		MvcResult created = mockMvc.perform(post("/api/catalog/units")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"code":"CAB-24","name":"Cabaña 24","rate":22000.00,"available":true,\
								"description":"Se mantiene","location":"Villarrica","maxGuests":2}
								"""))
				.andExpect(status().isCreated())
				.andReturn();
		Number id = com.jayway.jsonpath.JsonPath.read(created.getResponse().getContentAsString(), "$.id");

		mockMvc.perform(post("/api/catalog/units/" + id + "/confirmations"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.available").value(false))
				.andExpect(jsonPath("$.description").value("Se mantiene"))
				.andExpect(jsonPath("$.location").value("Villarrica"))
				.andExpect(jsonPath("$.maxGuests").value(2));
	}

	private static String unit(String code, String rate, boolean available) {
		return "{\"code\":\"" + code + "\",\"name\":\"Cabaña " + code + "\",\"rate\":" + rate + ",\"available\":" + available + "}";
	}
}
