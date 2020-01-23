import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class TestUtil {

    public static String loadTestFixture() {
        InputStream in = TestUtil.class.getResourceAsStream("fixture.txt");
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        return reader.lines().collect(Collectors.joining());
    }
}
