package ge.bog.blog.service;

import ge.bog.blog.exceptions.InvalidInputException;
import ge.bog.blog.model.apiModels.NewsRequest;
import ge.bog.blog.model.apiModels.NewsResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;
import java.util.Set;

@Service
public class NewsApiService {
    @Value("${mediastack.apikey}")
    private String apiKey;

    private final String apiUrl = "http://api.mediastack.com/v1/news";

    private static final Set<String> VALID_LANGUAGES = Set.of("ar", "de", "en", "es", "fr", "he", "it", "nl", "no", "pt", "ru", "se", "zh");

    private static final Set<String> VALID_COUNTRIES = Set.of("ar", "au", "at", "be", "br", "bg", "ca", "cn", "co", "cz", "eg", "fr", "de", "gr", "hk", "hu", "in", "id", "ie", "il", "it", "jp", "lv", "lt", "my", "mx", "ma", "nl", "nz", "ng", "no", "ph", "pl", "pt", "ro", "sa", "rs", "sg", "sk", "si", "za", "kr", "se", "ch", "tw", "th", "tr", "ae", "ua", "gb", "us", "ve");

    private static final Set<String> VALID_CATEGORIES = Set.of("general", "business", "entertainment", "health", "science", "sports", "technology");

    public NewsResponse getNews(NewsRequest newsRequest) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(apiUrl)
                .queryParam("access_key", apiKey);

        if (newsRequest.getLanguages() != null) {
            if(!areValidLanguages(newsRequest.getLanguages())){
                throw new InvalidInputException("Invalid Language");
            }
            builder.queryParam("languages", newsRequest.getLanguages());
        }

        if (newsRequest.getCountries() != null) {
            if(!areValidCountries(newsRequest.getCountries())){
                throw new InvalidInputException("Invalid Country");
            }
            builder.queryParam("countries", newsRequest.getCountries());
        }

        if (newsRequest.getCategories() != null) {
            if(!areValidCategories(newsRequest.getCategories())){
                throw new InvalidInputException("Invalid Category");
            }
            builder.queryParam("categories", newsRequest.getCategories());
        }

        if (newsRequest.getSources() != null) {
            builder.queryParam("sources", newsRequest.getSources());
        }

        if (newsRequest.getDate() != null) {
            builder.queryParam("date", newsRequest.getDate());
        }

        if (newsRequest.getSort() != null) {
            builder.queryParam("sort", newsRequest.getSort());
        }

        if (newsRequest.getLimit() != null) {
            builder.queryParam("limit", newsRequest.getLimit());
        }

        if (newsRequest.getOffset() != null) {
            builder.queryParam("offset", newsRequest.getOffset());
        }

        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(builder.toUriString(), NewsResponse.class);
    }

    private boolean areValidLanguages(String languages) {
        return Arrays.stream(languages.split(","))
                .allMatch(lang -> VALID_LANGUAGES.contains(lang.trim().toLowerCase()));
    }

    public boolean areValidCountries(String countries) {
        return Arrays.stream(countries.split(","))
                .allMatch(country -> VALID_COUNTRIES.contains(country.trim().toLowerCase()));
    }

    public boolean areValidCategories(String categories) {
        return Arrays.stream(categories.split(","))
                .allMatch(category -> VALID_CATEGORIES.contains(category.trim().toLowerCase()));
    }
}