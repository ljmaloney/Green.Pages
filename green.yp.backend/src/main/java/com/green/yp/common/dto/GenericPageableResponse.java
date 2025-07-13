package com.green.yp.common.dto;

import java.util.List;

public record GenericPageableResponse<T>(List<T> responseList,
                                         Integer totalCount,
                                         Integer currentPage,
                                         Integer totalPages) {}
