package com.cursojava.backend.services;

import com.cursojava.backend.entities.WebPage;
import com.cursojava.backend.repositories.SearchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchService {

    @Autowired
    private SearchRepository searchRepository;

    public List<WebPage> search(String textSearch){
        return searchRepository.search(textSearch);
    }

    public void save(WebPage webPage){
        searchRepository.save(webPage);
    }

    public boolean exist(String link) {
        return searchRepository.exist(link);
    }

    public List<WebPage> getLinksToIndex(){
        return searchRepository.getLinksToIndex();
    }
}
