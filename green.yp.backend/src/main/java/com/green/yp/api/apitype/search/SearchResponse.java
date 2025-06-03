package com.green.yp.api.apitype.search;

import java.util.List;

public record SearchResponse(List<ProducerSearchResponse> producerSearchResults,
                             Integer totalCount,
                             Integer currentPage,
                             Integer totalPages) {
}
