package ge.bog.blog.controller;

import ge.bog.blog.model.apiModels.NewsRequest;
import ge.bog.blog.service.NewsApiService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/news")
@AllArgsConstructor
public class NewsApiController {
    private NewsApiService newsService;

    @GetMapping
    public ResponseEntity<?> getNews( NewsRequest newsRequest) {
        return ResponseEntity.ok(newsService.getNews(newsRequest));
    }

}