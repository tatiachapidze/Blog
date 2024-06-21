package ge.bog.blog.service;

import ge.bog.blog.entity.BlogEntity;
import ge.bog.blog.entity.CommentEntity;
import ge.bog.blog.entity.UserEntity;
import ge.bog.blog.exceptions.DeniedAccessException;
import ge.bog.blog.exceptions.ResourceNotFoundException;
import ge.bog.blog.repository.BlogRepository;
import ge.bog.blog.repository.CommentRepository;
import ge.bog.blog.repository.UserRepository;
import ge.bog.blog.util.AccessControl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final BlogRepository blogRepository;
    private final AccessControl accessControl;

    @Transactional
    public long addComment(String content, long userId, long blogId) {
        BlogEntity blog = blogRepository.findById(blogId).orElseThrow(()-> new ResourceNotFoundException("Blog by id " + blogId + " not exists"));
        accessControl.checkUserAccessToBlog(blog,userId);
        CommentEntity commentEntity = new CommentEntity();
        commentEntity.setContent(content);
        commentEntity.setPostedAt(LocalDateTime.now());
        UserEntity user = userRepository.findById(userId).orElseThrow(()-> new ResourceNotFoundException("User by id " + userId + " not exists"));;
        commentEntity.setUser(user);
        commentEntity.setBlog(blog);
        commentRepository.save(commentEntity);
        return commentEntity.getId();
    }

    @Transactional
    public void deleteComment(long commentId, long userId){
        CommentEntity comment = commentRepository.findById(commentId).orElseThrow(()-> new ResourceNotFoundException("Comment by id " + commentId + " not exists"));
        if (!accessControl.checkUserOwnershipToBlog(comment.getBlog().getId(), userId)) {
            throw new DeniedAccessException("User has no ownership of Blog");
        }
        commentRepository.delete(comment);
    }
}