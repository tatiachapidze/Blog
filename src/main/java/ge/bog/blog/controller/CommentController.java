package ge.bog.blog.controller;

import ge.bog.blog.model.AddCommentReq;
import ge.bog.blog.service.CommentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
@Validated
public class CommentController {
    private final CommentService commentService;

    @PostMapping(value = "/add-comment", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addAd(@RequestBody @Valid AddCommentReq comment){
        long id = commentService.addComment(comment.getContent(), comment.getUserId(),comment.getBlogId());
        return ResponseEntity.ok(id);
    }

    @DeleteMapping(value = "{commentId}/delete/{userId}")
    public ResponseEntity<?> deleteComment(@PathVariable @Positive long commentId,
                                           @PathVariable @Positive long userId) {
        commentService.deleteComment(commentId, userId);
        return ResponseEntity.ok().body("Comment with ID: " + commentId + " has been deleted");
    }
}