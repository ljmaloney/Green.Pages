package com.green.yp.api.apitype;

import java.util.List;

public record PageableResponse<T>(List<T> pageableResults,
                                  Integer totalCount,
                                  Integer currentPage,
                                  Integer totalPages) {
}
