/*******************************************************************************
 * Copyright (c) 2015 IBM Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *******************************************************************************/
package net.wasdev.gameon.room;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.Instant;
import java.util.Arrays;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.WriterInterceptor;
import javax.ws.rs.ext.WriterInterceptorContext;

public class GameOnHeaderAuthInterceptor extends GameOnHeaderAuth implements WriterInterceptor {
    /**
     * Constructor to be used by the client.
     * 
     * @param userId
     *            the userId making this request.
     * @param secret
     *            the shared secret to use.
     */
    public GameOnHeaderAuthInterceptor(String userId, String secret) {
        super(secret,userId);
        if (secret == null)       
            throw new RuntimeException("NULL secret");
    }
    
    @Override
    public void aroundWriteTo(WriterInterceptorContext context) throws IOException, WebApplicationException {      
        try{     
            System.out.println("Auth interceptor adding stuff.. userId:"+userId+" secret:"+secret);
            
            //read the body from the request.. 
            OutputStream old = context.getOutputStream();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            context.setOutputStream(baos);
            context.proceed();
            
            final byte[] body = baos.toByteArray();
            
            context.setOutputStream(old);
            
            //hash the body
            String bodyHash = buildHash(body);
           
            //create the timestamp
            Instant now = Instant.now();
            String dateValue = now.toString();
            
            //create the signature
            String hmac = buildHmac(Arrays.asList(new String[] {
                                       userId,
                                       dateValue,
                                       bodyHash
                                   }),secret);
            
            System.out.println("hmac "+hmac+" FROM "+userId+dateValue+bodyHash);
    
            MultivaluedMap<String, Object> headers = context.getHeaders();
            headers.add("gameon-id", userId);
            headers.add("gameon-date", dateValue);
            headers.add("gameon-sig-body", bodyHash);
            headers.add("gameon-signature", hmac);
            
            old.write(body);
        
        }catch(Exception e){
            System.out.println("Bad stuff happened .. "+e.getMessage());
            e.printStackTrace();
            throw new IOException(e);
        }
    }

}
