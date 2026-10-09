package com.green.yp.security;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class XssServletRequestWrapperTest {

    @Test
    void sanitizeParameterValues() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        String[] parameters = new String[] {
                "<IMG SRC=javascript:alert(String.fromCharCode(88,83,83))>1",
                "data<iframe src=http://xss.rocks/scriptlet.html/>"
        };
        XSSRequestWrapper xssWrapper = new XSSRequestWrapper(request);
        Mockito.when(request.getParameterValues("parameter")).thenReturn(parameters);
        String[] sanitizedParams = xssWrapper.getParameterValues("parameter");
        assertArrayEquals(new String[] {"<img>1", "data"}, sanitizedParams);
    }
}
