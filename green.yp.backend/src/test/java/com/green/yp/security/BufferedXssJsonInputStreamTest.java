package com.green.yp.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class BufferedXssJsonInputStreamTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void testSimpleJsonDocument() throws Exception {
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("propertyOne", "SomeStringProperty");
        jsonMap.put("integerProperty", 100);
        jsonMap.put("xssString1", "CE<script></script>");
        jsonMap.put("xssString2", "<img src=javascript:alert(String.fromCharCode(88,83,83))>1");
        jsonMap.put("xssString3", "data<iframe src=http://xss.rocks/scriptlet.html/>");

        BufferedXssJsonInputStream jsonInputStream =
                new BufferedXssJsonInputStream(new TestServletInputStream() {
                    private final ByteArrayInputStream byteArrayStream = new ByteArrayInputStream(mapper.writeValueAsBytes(jsonMap));

                    @Override
                    public int read() throws IOException {
                        return byteArrayStream.read();
                    }
                }, null);

        Map<?, ?> cleanedJsonMap = new ObjectMapper().readValue(jsonInputStream, Map.class);

        assertEquals(jsonMap.get("propertyOne"), cleanedJsonMap.get("propertyOne"));
        assertEquals("CE", cleanedJsonMap.get("xssString1"));
        assertFalse(((String) cleanedJsonMap.get("xssString2")).contains("src=javascript:alert(String.fromCharCode(88,83,83))"));
        assertEquals("data", cleanedJsonMap.get("xssString3"));
    }

    @Test
    void testJsonDocumentWithSubDocument() throws Exception {
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("propertyOne", "SomeStringProperty");
        jsonMap.put("integerProperty", 100);
        Map<String, Object> jsonSubMap = new HashMap<>();
        jsonMap.put("subDocument", jsonSubMap);
        jsonMap.put("xssString1", "CE<script></script>");
        jsonSubMap.put("xssString2", "<img src=javascript:alert(String.fromCharCode(88,83,83))>1");
        jsonSubMap.put("xssString3", "data<iframe src=http://xss.rocks/scriptlet.html/>");

        BufferedXssJsonInputStream jsonInputStream =
                new BufferedXssJsonInputStream(new TestServletInputStream() {
                    private final ByteArrayInputStream byteArrayStream = new ByteArrayInputStream(mapper.writeValueAsBytes(jsonMap));

                    @Override
                    public int read() throws IOException {
                        return byteArrayStream.read();
                    }
                }, 512, XSSRequestWrapper::cleanXSS);

        Map<?, ?> cleanedJsonMap = new ObjectMapper().readValue(jsonInputStream, Map.class);

        assertEquals(jsonMap.get("propertyOne"), cleanedJsonMap.get("propertyOne"));
        assertEquals("CE", cleanedJsonMap.get("xssString1"));
        assertEquals("<img>1", ((Map<?, ?>) cleanedJsonMap.get("subDocument")).get("xssString2"));
        assertEquals("data", ((Map<?, ?>) cleanedJsonMap.get("subDocument")).get("xssString3"));
    }

    @Test
    void testJsonDocumentWithListOfSubDocuments() throws Exception {
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("propertyOne", "SomeStringProperty");
        jsonMap.put("integerProperty", 100);
        jsonMap.put("xssString1", "CE<script></script>");

        Map<String, Object> jsonSubMap = new HashMap<>();
        jsonSubMap.put("xssString2", "<IMG SRC=javascript:alert(String.fromCharCode(88,83,83))>1");
        jsonSubMap.put("xssString3", "data<iframe src=http://xss.rocks/scriptlet.html/>");

        List<Map<String, Object>> subDocList = new ArrayList<>();
        subDocList.add(jsonSubMap);
        jsonMap.put("subDocumentList", subDocList);

        BufferedXssJsonInputStream jsonInputStream =
                new BufferedXssJsonInputStream(new TestServletInputStream() {
                    private final ByteArrayInputStream byteArrayStream = new ByteArrayInputStream(mapper.writeValueAsBytes(jsonMap));

                    @Override
                    public int read() throws IOException {
                        return byteArrayStream.read();
                    }
                }, 512, XSSRequestWrapper::cleanXSS);

        Map<?, ?> cleanedJsonMap = new ObjectMapper().readValue(jsonInputStream, Map.class);

        assertEquals(jsonMap.get("propertyOne"), cleanedJsonMap.get("propertyOne"));
        assertEquals("CE", cleanedJsonMap.get("xssString1"));
        assertEquals(jsonMap.get("integerProperty"), cleanedJsonMap.get("integerProperty"));
        assertFalse(getSubDocumentValue(cleanedJsonMap, 0, "subDocumentList", "xssString2")
                .contains("src=javascript:alert(String.fromCharCode(88,83,83))"));
        assertEquals("data", getSubDocumentValue(cleanedJsonMap, 0, "subDocumentList", "xssString3"));
    }

    @Test
    void testComplexJsonDocument() throws Exception {
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("propertyOne", "SomeStringProperty");
        jsonMap.put("integerProperty", 100);
        jsonMap.put("xssString1", "CE<script></script>");

        Map<String, Object> jsonSubMap = new HashMap<>();
        jsonSubMap.put("xssString2", "<IMG SRC=javascript:alert(String.fromCharCode(88,83,83))>1");
        jsonSubMap.put("xssString3", "data<iframe src=http://xss.rocks/scriptlet.html/>");

        jsonMap.put("stringArray", List.of("Tortuga",
                "alpha<iframe src=http://xss.rocks/scriptlet.html/>",
                "<IMG SRC=javascript:alert(String.fromCharCode(88,83,83))>omega"));

        List<Map<String, Object>> subDocList = new ArrayList<>();
        subDocList.add(jsonSubMap);
        jsonMap.put("subDocumentList", subDocList);

        BufferedXssJsonInputStream jsonInputStream =
                new BufferedXssJsonInputStream(new TestServletInputStream() {
                    private final ByteArrayInputStream byteArrayStream = new ByteArrayInputStream(mapper.writeValueAsBytes(jsonMap));

                    @Override
                    public int read() throws IOException {
                        return byteArrayStream.read();
                    }
                }, 512, XSSRequestWrapper::cleanXSS);

        Map<?, ?> cleanedJsonMap = new ObjectMapper().readValue(jsonInputStream, Map.class);

        assertEquals(jsonMap.get("propertyOne"), cleanedJsonMap.get("propertyOne"));
        assertEquals("CE", cleanedJsonMap.get("xssString1"));
        assertEquals(jsonMap.get("integerProperty"), cleanedJsonMap.get("integerProperty"));
        assertEquals(List.of("Tortuga", "alpha", "<img>omega"), cleanedJsonMap.get("stringArray"));
        assertEquals("<img>1", getSubDocumentValue(cleanedJsonMap, 0, "subDocumentList", "xssString2"));
        assertEquals("data", getSubDocumentValue(cleanedJsonMap, 0, "subDocumentList", "xssString3"));
    }

    @Test
    void testJsonListDocument() throws Exception {
        List<Object> jsonList = new ArrayList<>();

        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("propertyOne", "SomeStringProperty");
        jsonMap.put("integerProperty", 100);
        jsonMap.put("xssString1", "CE<script></script>");
        jsonList.add(jsonMap);

        Map<String, Object> jsonSubMap = new HashMap<>();
        jsonSubMap.put("xssString2", "<IMG SRC=javascript:alert(String.fromCharCode(88,83,83))>1");
        jsonSubMap.put("xssString3", "data<iframe src=http://xss.rocks/scriptlet.html/>");

        jsonMap.put("stringArray", List.of("Tortuga",
                "alpha<iframe src=http://xss.rocks/scriptlet.html/>",
                "<IMG SRC=javascript:alert(String.fromCharCode(88,83,83))>omega"));

        List<Map<String, Object>> subDocList = new ArrayList<>();
        subDocList.add(jsonSubMap);
        jsonMap.put("subDocumentList", subDocList);

        BufferedXssJsonInputStream jsonInputStream =
                new BufferedXssJsonInputStream(new TestServletInputStream() {
                    private final ByteArrayInputStream byteArrayStream = new ByteArrayInputStream(mapper.writeValueAsBytes(jsonList));

                    @Override
                    public int read() throws IOException {
                        return byteArrayStream.read();
                    }
                }, 1024, XSSRequestWrapper::cleanXSS);

        List<?> cleanedList = new ObjectMapper().readValue(jsonInputStream, List.class);

        assertEquals(1, cleanedList.size());
        Map<?, ?> firstItem = (Map<?, ?>) cleanedList.get(0);
        assertEquals(jsonMap.get("propertyOne"), firstItem.get("propertyOne"));
        assertEquals("CE", firstItem.get("xssString1"));
        assertEquals(jsonMap.get("integerProperty"), firstItem.get("integerProperty"));
        assertEquals(List.of("Tortuga", "alpha", "<img>omega"), firstItem.get("stringArray"));
        assertEquals("<img>1", getSubDocumentValue(firstItem, 0, "subDocumentList", "xssString2"));
        assertEquals("data", getSubDocumentValue(firstItem, 0, "subDocumentList", "xssString3"));
    }

    @SuppressWarnings("unchecked")
    private String getSubDocumentValue(Map<?, ?> jsonMap, int index, String subDocKey, String key) {
        Object subDoc = jsonMap.get(subDocKey);
        if (subDoc instanceof Map) {
            Object val = ((Map<?, ?>) subDoc).get(key);
            return val != null ? val.toString() : "";
        }
        if (subDoc instanceof List) {
            Map<String, Object> subDocMap = ((List<Map<String, Object>>) subDoc).get(index);
            Object val = subDocMap.get(key);
            return val != null ? val.toString() : "";
        }
        return "";
    }

    private static abstract class TestServletInputStream extends ServletInputStream {
        @Override
        public boolean isFinished() {
            return false;
        }

        @Override
        public boolean isReady() {
            return false;
        }

        @Override
        public void setReadListener(ReadListener listener) {
        }
    }
}
