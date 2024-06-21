package ge.bog.blog.controller;

import ge.bog.blog.model.*;
import ge.bog.blog.service.BlogService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/blogs")
@RequiredArgsConstructor
@Validated
public class BlogController {
    private final BlogService blogService;

    @PostMapping(value = "/add-blog", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addBlog( @RequestBody @Valid AddBlogReq blog) {
        long id = blogService.addBlog(blog);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping(value = "{blogId}/delete/{userId}")
    public ResponseEntity<?> deleteBlog(@PathVariable @Positive long blogId,
                                        @PathVariable @Positive long userId) {
        blogService.deleteBlog(blogId, userId);
        return ResponseEntity.ok().body("Blog with ID: " + blogId + " has been deleted");
    }

    @GetMapping(value = "/find-blog", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findBlog(@RequestParam @Positive @NotNull Long id) {

        BlogDto blog = blogService.findBlogById(id);
        return ResponseEntity.ok(blog);
    }

    @PutMapping (value = "/update-blog", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateBlog(@RequestBody @Valid UpdateBlogReq newBlog) {
        long id = blogService.updateBlog(newBlog);
        return ResponseEntity.ok(id);
    }

    @GetMapping(value = "/{blogId}/get-subscribe-requests/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getSubscribeRequests(@PathVariable @Positive long blogId,
                                                  @PathVariable @Positive long userId) {
        List<SubscriptionDto> requestedSubscribes = blogService.reviewBlogSubscriptionRequests(blogId, userId);
        return ResponseEntity.ok(requestedSubscribes);
    }

    @PutMapping(value = "/{blogId}/respond-subscribe-requests/{userId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> respondSubscribeRequests(@PathVariable @Positive long blogId,
                                                      @PathVariable @Positive long userId,
                                                      @RequestBody @Valid SubscribeRequestResponse subscribeRequestResponse) {
        int respondedRequests = blogService.respondBlogSubscriptionRequests(blogId, userId, subscribeRequestResponse.getUsersRequested(), subscribeRequestResponse.getStatus());
        return ResponseEntity.ok(respondedRequests);
    }
    @GetMapping(value = "/{blogId}/view_blog/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> viewBlog(@PathVariable @Positive long blogId,
                                      @PathVariable @Positive long userId) {
        ViewBlogDto viewBlogDto = blogService.viewBlog(blogId, userId);
        return ResponseEntity.ok(viewBlogDto);
    }
    @GetMapping(value ="/search")
    public ResponseEntity<?> searchBlogs(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) List<String> tags,
            @RequestParam @NotNull @Positive long userId) {
        List<SearchBlogDto> result = blogService.searchBlog(title, tags, userId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/popular")
    public ResponseEntity<?> getPopularBlogs() {
        List<SearchBlogDto> popularBlogs = blogService.getPopularBlogs();
        return ResponseEntity.ok(popularBlogs);
    }
    @PostMapping("/{blogId}/moderators/{userId}")
    public ResponseEntity<?> makeUserModerator(@PathVariable @Positive long blogId,
                                               @RequestParam @Positive @NotNull long authorId,
                                               @PathVariable @Positive long userId) {
        boolean success = blogService.makeUserModerator(blogId, authorId, userId);

        if (success) {
            return ResponseEntity.ok().body("User successfully made a moderator");
        } else {
            return ResponseEntity.badRequest().body("Failed to make user a moderator");
        }
    }
    @PostMapping("/{blogId}/block-user/{actingUserId}")
    public ResponseEntity<?> blockUserForBlog(@PathVariable @Positive long blogId,
                                              @PathVariable @Positive long actingUserId,
                                              @RequestParam @Positive @NotNull long targetUserId) {
        boolean success = blogService.blockUserForBlog(blogId,actingUserId,targetUserId);
        if (success) {
            return ResponseEntity.ok().body("User successfully blocked for the blog");
        } else {
            return ResponseEntity.badRequest().body("User block was not successful");
        }
    }
    @PostMapping("/{userId}/like-blog/{blogId}")
    public ResponseEntity<?> likeBlog(@PathVariable @Positive long userId,
                                      @PathVariable @Positive long blogId) {
        boolean success = blogService.likeBlog(blogId, userId);
        if (success) {
            return ResponseEntity.ok().body("Blog successfully liked.");
        } else {
            return ResponseEntity.badRequest().body("You have already liked blog");
        }
    }
    @PostMapping("/{userId}/subscribe/{blogId}")
    public ResponseEntity<String> subscribeToBlog(@PathVariable @Positive long userId,
                                                  @PathVariable @Positive long blogId) {
        String response = blogService.subscribeBlog(userId, blogId);
        return ResponseEntity.ok(response);
    }

}
