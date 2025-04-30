package com.green.yp.api.apitype;

import java.util.Map;

public record PatchRequest(Map<String, Object> patchParameters) {}
