package com.cursojava.backend.services;

import com.cursojava.backend.entities.WebPage;
import com.cursojava.backend.repositories.SearchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hibernate.internal.util.StringHelper.isBlank;

@Service
public class SpiderService {

    @Autowired
    SearchRepository searchRepository;

    @Autowired
    SearchService searchService;

    public void indexWebPages() {
        List<WebPage> linksToIndex = searchService.getLinksToIndex();
        linksToIndex.stream().parallel().forEach(webPage -> {
            try{
                indexWebPage(webPage);
            } catch (Exception err){
                System.out.println(err.getMessage());
            }
        });
    }

    private void indexWebPage(WebPage webPage) throws Exception {
        String url = webPage.getUrl();
        String content = getWebContent(url);
        if(isBlank(content)) { return; }

        indexAndSaveWebPage(webPage, content);
        saveLinks(getDomain(url), content);
    }

    private String getDomain(String url) {
        String[] aux = url.split("/");
        return aux[0] + "//" + aux[1];
    }

    private void saveLinks(String domain, String content){
        List<String> links = getLinks(domain, content);
        links.stream()
                .filter(link -> !searchService.exist(link))
                .map(link -> new WebPage(link))
                .forEach(webPage -> searchService.save(webPage));
    }

    private List<String> cleanLinks(String domain, List<String> links){
        String[] excludeExtensions = new String[]{"css", "js", "json", "jpg", "png", "woff2"};
        List<String> resultLinks = links.stream()
                .filter(link -> !Arrays.stream(excludeExtensions).noneMatch(link::endsWith))
                .map(link -> link.startsWith("/") ? domain + link : link)
                .collect(Collectors.toList());

        List<String> uniqueLinks = new ArrayList<String>();
        uniqueLinks.addAll(new HashSet<String>(resultLinks));

        return uniqueLinks;
    }

    public List<String> getLinks (String domain, String content){
        List<String> links = new ArrayList<>();

        String[] splitHref = content.split("href=\"");
        List<String> listSplitHref = Arrays.asList(splitHref);
        // listSplitHref.remove(0);

        listSplitHref.forEach(str -> {
            String[] aux = str.split("\"");
            links.add(aux[0]);
        });
        cleanLinks(domain, links);
        return links;
    }

    public void indexAndSaveWebPage(WebPage webPage, String content){
        String title = getTitle(content);
        String description = getDescription(content);

        webPage.setTitle(title);
        webPage.setDescription(description);
        searchRepository.save(webPage);
    }

    public String getTitle (String content){
        String[] aux = content.split("<title>");
        String[] aux2 = aux[1].split("</title>");
        return aux2[0];
    }

    public String getDescription (String content){
        String[] aux = content.split("<meta name=\"description\" content=\"");
        String[] aux2 = aux[1].split("\">");
        return aux2[0];
    }

    private static String getWebContent(String link){
        try {
            URL url = new URL(link);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            String enconding = conn.getContentEncoding();

            InputStream input = conn.getInputStream();

            Stream<String> lines = new BufferedReader(new InputStreamReader(input))
                    .lines();

            return lines.collect(Collectors.joining());
        } catch (IOException ex){
            System.out.println(ex.getMessage());
        }

        return "";
    }
}
