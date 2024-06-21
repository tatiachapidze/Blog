package ge.bog.blog.model.apiModels;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;



@Getter
@Setter
public class NewsResponse {
    @JsonProperty("pagination")
    public Pagination pagination;
    @JsonProperty("data")
    public List<NewsArticle> data;


    @Getter
    @Setter
    public static class Pagination {
        @JsonProperty("limit")
        public int limit;

        @JsonProperty("offset")
        public int offset;

        @JsonProperty("count")
        public int count;

        @JsonProperty("total")
        public int total;
    }


    @Getter
    @Setter
    public static class NewsArticle {
        @JsonProperty("author")
        public String author;

        @JsonProperty("title")
        public String title;

        @JsonProperty("description")
        public String description;

        @JsonProperty("url")
        public String url;

        @JsonProperty("image")
        public String image;

        @JsonProperty("category")
        public String category;

        @JsonProperty("language")
        public String language;

        @JsonProperty("country")
        public String country;

        @JsonProperty("published_at")
        public String publishedAt;

    }
}