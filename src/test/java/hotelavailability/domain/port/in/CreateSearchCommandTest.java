package hotelavailability.domain.port.in;

import com.mindata.hotelavailability.domain.port.in.CountSearchUseCase;
import com.mindata.hotelavailability.domain.port.in.CreateSearchUseCase.CreateSearchCommand;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CreateSearchCommandTest {

    @Test
    void defaultsNullAgesToEmptyAndCopiesInput() {
        CreateSearchCommand nulls = new CreateSearchCommand(
                "h", LocalDate.now(), LocalDate.now().plusDays(1), null);

        List<Integer> mutable = new ArrayList<>(Arrays.asList(1, 2));
        CreateSearchCommand cmd = new CreateSearchCommand(
                "h", LocalDate.now(), LocalDate.now().plusDays(1), mutable);
        mutable.add(99);

        assertAll(
                () -> assertEquals(List.of(), nulls.ages()),
                () -> assertEquals(List.of(1, 2), cmd.ages()),
                () -> assertThrows(UnsupportedOperationException.class, () -> cmd.ages().add(7))
        );
    }

    @Test
    void countResultHoldsValues() {
        CountSearchUseCase.CountResult r = new CountSearchUseCase.CountResult(null, 42);
        assertEquals(42, r.count());
    }
}
