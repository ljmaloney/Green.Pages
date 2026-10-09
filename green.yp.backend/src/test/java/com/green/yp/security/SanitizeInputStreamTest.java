package com.green.yp.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.junit.jupiter.api.Test;
import org.springframework.util.ResourceUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SanitizeInputStreamTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void testSimpleJsonDocument() throws Exception {
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("propertyOne", "SomeStringProperty");
        jsonMap.put("integerProperty", 100);
        jsonMap.put("xssString1", "CE<script></script>");
        jsonMap.put("xssString2", "<IMG SRC=javascript:alert(String.fromCharCode(88,83,83))>1");
        jsonMap.put("xssString3", "data<iframe src=http://xss.rocks/scriptlet.html/>");

        SanitizeInputStream sanitizeInputStream = new SanitizeInputStream(new ByteArrayInputStream(mapper.writeValueAsBytes(jsonMap)));
        Map<?, ?> cleanedJsonMap = new ObjectMapper().readValue(sanitizeInputStream.toString(), Map.class);

        assertEquals(jsonMap.get("propertyOne"), cleanedJsonMap.get("propertyOne"));
        assertEquals("CE", cleanedJsonMap.get("xssString1"));
        assertEquals("<img>1", cleanedJsonMap.get("xssString2"));
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
        jsonSubMap.put("xssString2", "<IMG SRC=javascript:alert(String.fromCharCode(88,83,83))>1");
        jsonSubMap.put("xssString3", "data<iframe src=http://xss.rocks/scriptlet.html/>");

        SanitizeInputStream sanitizeInputStream = new SanitizeInputStream(new ByteArrayInputStream(mapper.writeValueAsBytes(jsonMap)));
        Map<?, ?> cleanedJsonMap = new ObjectMapper().readValue(sanitizeInputStream.toString(), Map.class);

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

        SanitizeInputStream sanitizeInputStream = new SanitizeInputStream(new ByteArrayInputStream(mapper.writeValueAsBytes(jsonMap)));
        Map<?, ?> cleanedJsonMap = new ObjectMapper().readValue(sanitizeInputStream.toString(), Map.class);

        assertEquals(jsonMap.get("propertyOne"), cleanedJsonMap.get("propertyOne"));
        assertEquals("CE", cleanedJsonMap.get("xssString1"));
        assertEquals(jsonMap.get("integerProperty"), cleanedJsonMap.get("integerProperty"));
        assertEquals("<img>1", getSubDocumentValue(cleanedJsonMap, 0, "subDocumentList", "xssString2"));
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

        SanitizeInputStream sanitizeInputStream = new SanitizeInputStream(new ByteArrayInputStream(mapper.writeValueAsBytes(jsonMap)));
        Map<?, ?> cleanedJsonMap = new ObjectMapper().readValue(sanitizeInputStream.toString(), Map.class);

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

        SanitizeInputStream sanitizeInputStream = new SanitizeInputStream(new ByteArrayInputStream(mapper.writeValueAsBytes(jsonList)));
        List<?> cleanedList = new ObjectMapper().readValue(sanitizeInputStream.toString(), List.class);

        assertEquals(1, cleanedList.size());
        Map<?, ?> firstItem = (Map<?, ?>) cleanedList.get(0);
        assertEquals(jsonMap.get("propertyOne"), firstItem.get("propertyOne"));
        assertEquals("CE", firstItem.get("xssString1"));
        assertEquals(jsonMap.get("integerProperty"), firstItem.get("integerProperty"));
        assertEquals(List.of("Tortuga", "alpha", "<img>omega"), firstItem.get("stringArray"));
        assertEquals("<img>1", getSubDocumentValue(firstItem, 0, "subDocumentList", "xssString2"));
        assertEquals("data", getSubDocumentValue(firstItem, 0, "subDocumentList", "xssString3"));
    }

    @Test
    void sanitizeChildInfoEvent() throws Exception {
        String uncleanJson = loadJsonString("child-event-info.json");
        SanitizeInputStream sanitizeInputStream = new SanitizeInputStream(new ByteArrayInputStream(uncleanJson.getBytes()));
        String sanitizedJson = sanitizeInputStream.toString();
        Map<?, ?> parsedJsonMap = new ObjectMapper().readValue(sanitizedJson, HashMap.class);

        assertNotNull(sanitizedJson);
        assertTrue(MapUtils.isNotEmpty(parsedJsonMap));
        assertTrue(parsedJsonMap.get("description").toString().contains("\n"));
        assertTrue(CollectionUtils.isNotEmpty((List<?>) parsedJsonMap.get("eligibleRpcApc")));
        assertThat(((String) parsedJsonMap.get("description"))).contains("</p>\n<p>Aenean leo ligula");
    }

    @Test
    void sanitizeDocumentWithURLs() throws Exception {
        String uncleanJson = loadJsonString("image-gallery.json");
        SanitizeInputStream sanitizeInputStream = new SanitizeInputStream(new ByteArrayInputStream(uncleanJson.getBytes()));
        String sanitizedJson = sanitizeInputStream.toString();
        Map<?, ?> parsedJsonMap = new ObjectMapper().readValue(sanitizedJson, HashMap.class);

        assertNotNull(sanitizedJson);
        assertTrue(MapUtils.isNotEmpty(parsedJsonMap));
        assertEquals("\\2023_CE_CUL_imgs_te\\e2e06d0cfa954eecbdb8493cbf06edd7_IMAGE.jpeg", parsedJsonMap.get("imageUrl"));
    }

    @Test
    void sanitizeEventDescription() throws Exception {
        String uncleanJson = loadJsonString("event_description.json");
        SanitizeInputStream sanitizeInputStream = new SanitizeInputStream(new ByteArrayInputStream(uncleanJson.getBytes()));
        String sanitizedJson = sanitizeInputStream.toString();

        assertNotNull(sanitizedJson);
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

    private String loadJsonString(String fileName) throws Exception {
        File jsonFile = ResourceUtils.getFile(String.format("classpath:%s", fileName));
        return Files.readString(jsonFile.toPath());
    }
}
