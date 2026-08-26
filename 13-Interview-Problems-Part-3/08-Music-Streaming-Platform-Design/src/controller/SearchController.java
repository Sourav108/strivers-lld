package controller;

import service.SearchService;

import java.util.Map;

public class SearchController {
    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    public Map<String, Object> search(String query, String type) {
        return searchService.search(query, type);
    }
}
