/**
 * Created by matthewmcguire on 11/24/14.
 */
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ServerTest {
    @Test
    public void testValidPort() {
        Server server = new Server();
        assertEquals(9999, server.validPort("9999"));
    }
}
