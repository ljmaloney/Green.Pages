package com.green.yp.reference;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.*;
import org.junit.jupiter.api.Test;

public class DecodeTest {

    private DecodeMessage decodeMessage = new DecodeMessage();

    @Test
    public void testDecodeMessage() throws IOException {
        String decoded = decodeMessage.decode(getTestString());
        assertThat(decoded).isEqualTo("I love computers");
    }

    @Test
    public void testDecodedFile() throws IOException {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("encodedMessage.txt");
        InputStreamReader reader = new InputStreamReader(inputStream);
        String decodedMessage = decodeMessage.decode(new LineNumberReader(reader));
        assertThat(decodedMessage).isNotNull();
        System.out.println(decodedMessage);
    }

    StringReader getTestString(){
        StringBuilder input = new StringBuilder();
        input.append("3 love\n")
                .append("6 computers\n")
                .append("2 dogs\n")
                .append("4 cats\n")
                .append("1 I\n")
                .append("5 you\n");
        return new StringReader(input.toString());
    }
}
