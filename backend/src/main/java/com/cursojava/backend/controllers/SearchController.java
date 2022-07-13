package com.cursojava.backend.controllers;

import com.cursojava.backend.entities.WebPage;
import com.cursojava.backend.services.SearchService;
import com.cursojava.backend.services.SpiderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class SearchController {

    @Autowired
    private SearchService service;

    @Autowired
    private SpiderService spiderService;

    @CrossOrigin("*")
    @RequestMapping(value = "api/search", method = RequestMethod.GET)
    public List<WebPage> search(@RequestParam Map<String, String> params){
        // api/search?query=xd&lenguage=es
        String query = params.get("query");
        return service.search(query);
    }

    @CrossOrigin("*")
    @RequestMapping(value = "api/test", method = RequestMethod.GET)
    public void search() {
        spiderService.indexWebPages();
    }
}
