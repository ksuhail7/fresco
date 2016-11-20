package com.suhailkandanur.messaging;

import java.io.IOException;

/**
 * Created by suhail on 2016-11-19.
 */
public interface Publisher {

    void publishBroadcast(String exchange, byte[] body) throws IOException;
    void publishDirect(String exchange, String routingKey, byte[] body) throws IOException;

}
