package net.wasdev.gameon.room;

import java.io.IOException;
import java.time.Instant;
import java.util.Arrays;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.core.MultivaluedMap;

public class GameOnHeaderAuthFilter extends GameOnHeaderAuth implements ClientRequestFilter {
    
    public GameOnHeaderAuthFilter(String userId, String secret) {
        super(secret,userId);
        if (secret == null)       
            throw new RuntimeException("NULL secret");
    }
    
    @Override
    public void filter(ClientRequestContext context) throws IOException {
        try {
            // create the timestamp
            Instant now = Instant.now();
            String dateValue = now.toString();

            // create the signature
            String hmac = buildHmac(Arrays.asList(new String[] { userId, dateValue }), secret);

            MultivaluedMap<String, Object> headers = context.getHeaders();
            headers.add("gameon-id", userId);
            headers.add("gameon-date", dateValue);
            headers.add("gameon-signature", hmac);

        } catch (Exception e) {
            System.out.println("Bad stuff happened .. " + e.getMessage());
            e.printStackTrace();
            throw new IOException(e);
        }
    }
}
