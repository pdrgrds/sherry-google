package com.cursojava.backend.repositories;

import com.cursojava.backend.entities.WebPage;
import java.util.List;

public interface SearchRepository {
    public List<WebPage> search(String textSearch);

    void save(WebPage webPage);

    boolean exist(String link);

    WebPage getByUrl(String url);

    List<WebPage> getLinksToIndex();
}
