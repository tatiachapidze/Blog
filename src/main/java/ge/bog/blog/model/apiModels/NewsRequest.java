package ge.bog.blog.model.apiModels;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class NewsRequest {
    private String accessKey;
    private String sources;
    private String categories;
    private String countries;
    private String languages;
    private String keywords;
    private String date;
    private String sort;
    private Integer limit;
    private Integer offset;
}