package hotelavailability.infrastructure.adapter.in.rest;

import com.mindata.hotelavailability.domain.exception.SearchNotFoundException;
import com.mindata.hotelavailability.domain.model.HotelSearch;
import com.mindata.hotelavailability.domain.port.in.CountSearchUseCase;
import com.mindata.hotelavailability.domain.port.in.CreateSearchUseCase;
import com.mindata.hotelavailability.infrastructure.adapter.in.rest.GlobalExceptionHandler;
import com.mindata.hotelavailability.infrastructure.adapter.in.rest.SearchController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SearchController.class)
@Import(GlobalExceptionHandler.class)
class SearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CreateSearchUseCase createSearchUseCase;

    @MockitoBean
    private CountSearchUseCase countSearchUseCase;

    @Test
    void postSearchReturns202WithGeneratedId() throws Exception {
        given(createSearchUseCase.createSearch(any())).willReturn("id-xyz");

        String body = """
                {"hotelId":"1234aBc","checkIn":"29/12/2023","checkOut":"31/12/2023","ages":[30,29,1,3]}
                """;

        mockMvc.perform(post("/search").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.searchId").value("id-xyz"));

        verify(createSearchUseCase).createSearch(any());
    }

    @Test
    void postSearchReturns400WhenFieldsMissing() throws Exception {
        String body = """
                {"hotelId":"","ages":[]}
                """;

        mockMvc.perform(post("/search").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"));
    }

    @Test
    void postSearchReturns400WhenCheckInNotBeforeCheckOut() throws Exception {
        String body = """
                {"hotelId":"h","checkIn":"02/01/2024","checkOut":"01/01/2024","ages":[1]}
                """;

        mockMvc.perform(post("/search").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details",
                        hasItem("checkIn must be strictly before checkOut")));
    }

    @Test
    void postSearchReturns400WhenAgeNegative() throws Exception {
        String body = """
                {"hotelId":"h","checkIn":"01/01/2024","checkOut":"02/01/2024","ages":[1,-2]}
                """;

        mockMvc.perform(post("/search").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void postSearchReturns400WhenJsonMalformed() throws Exception {
        mockMvc.perform(post("/search").contentType(MediaType.APPLICATION_JSON).content("{oops"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(notNullValue()));
    }

    @Test
    void getCountReturnsPayload() throws Exception {
        HotelSearch search = new HotelSearch(
                "id-1", "h", LocalDate.of(2023, 12, 29), LocalDate.of(2023, 12, 31), List.of(30, 29, 1, 3));
        given(countSearchUseCase.countBySearchId("id-1"))
                .willReturn(new CountSearchUseCase.CountResult(search, 100));

        mockMvc.perform(get("/count").param("searchId", "id-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.searchId").value("id-1"))
                .andExpect(jsonPath("$.count").value(100))
                .andExpect(jsonPath("$.search.hotelId").value("h"))
                .andExpect(jsonPath("$.search.checkIn").value("29/12/2023"));
    }

    @Test
    void getCountReturns404WhenUseCaseThrowsNotFound() throws Exception {
        given(countSearchUseCase.countBySearchId("missing"))
                .willThrow(new SearchNotFoundException("missing"));

        mockMvc.perform(get("/count").param("searchId", "missing"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"));
    }

    @Test
    void getCountReturns400WhenSearchIdMissing() throws Exception {
        mockMvc.perform(get("/count"))
                .andExpect(status().isBadRequest());
    }
}
