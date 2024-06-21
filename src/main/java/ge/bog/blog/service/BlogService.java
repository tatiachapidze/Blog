package ge.bog.blog.service;

import ge.bog.blog.entity.*;
import ge.bog.blog.exceptions.DeniedAccessException;
import ge.bog.blog.exceptions.ResourceNotFoundException;
import ge.bog.blog.model.*;
import ge.bog.blog.model.enums.SubscriptionStatus;
import ge.bog.blog.repository.*;
import ge.bog.blog.util.AccessControl;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BlogService {
    private final BlogRepository blogRepository;
    private final UserRepository userRepository;
    private final TagRepository tagRepository;
    private final AdRepository adRepository;
    private final AccessControl accessControl;
    private final SubscriptionRepository subscriptionRepository;

    @Scheduled(cron = "0 0 0 1 * ?")
    //@Scheduled(fixedRate = 5000)
    @Transactional
    public void archiveOldBlogs() {
        LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);
        blogRepository.archiveBlogsNotUpdatedSince(oneMonthAgo);
    }

    //run every night at 03:00, as the exact time wasn't specified in given instructions
    @Scheduled(cron = "0 0 3 * * ?")
    @Transactional
    public void deleteOldViews() {
        LocalDate thirtyOneDaysAgo = LocalDate.now().minusDays(31);
        List<BlogEntity> blogs = blogRepository.findBlogsWithOldViews(thirtyOneDaysAgo);
        blogs.forEach(blog -> removeOlderViewCount(blog, thirtyOneDaysAgo));
    }
    @Transactional
    public long addBlog(AddBlogReq blog) {
        BlogEntity blogEntity = new BlogEntity();
        configureBlogEntity(blogEntity, blog.getTitle(), blog.getContent(), blog.getModeratorIds(), blog.getTagIds(), blog.getAdIds(), blog.isLocked());
        blogEntity.setViews(0);
        blogEntity.setCreateDate(LocalDateTime.now());
        UserEntity author = userRepository.findById(blog.getAuthorId()).orElseThrow(()-> new ResourceNotFoundException("author by id "+ blog.getAuthorId()+ " not exists"));
        blogEntity.setAuthor(author);
        blogRepository.save(blogEntity);
        return blogEntity.getId();
    }
    @Transactional
    public void deleteBlog(long blogId, long userId) {
        if (!blogRepository.existsById(blogId)) {
            throw new IllegalArgumentException("Blog with ID: " + blogId + " not found");
        }
        if (!blogRepository.checkBlogAuthor(blogId, userId)) {
            throw new DeniedAccessException("User han no authority to delete the blog");
        }
        blogRepository.deleteById(blogId);
    }
    //just read from CRUD operation, it,s just reading the whole blog .
    // it's not what users see while viewing blog, for that  I have different method viewBlog
    @Transactional(readOnly = true)
    public BlogDto findBlogById(long blogId) {
        BlogEntity blogEntity = blogRepository.findById(blogId).orElseThrow(()-> new ResourceNotFoundException("Blog by id " + blogId + " not exists"));
        return mapToBlogDto(blogEntity);
    }

    @Transactional
    public long updateBlog(UpdateBlogReq newBlog){
        BlogEntity blog = blogRepository.findById(newBlog.getBlogId()).orElseThrow(()-> new ResourceNotFoundException("Blog by id " + newBlog.getBlogId() + " not exists"));
        if (!blogRepository.checkBlogAuthor(newBlog.getBlogId(), newBlog.getUserId())) {
            throw new DeniedAccessException("User han no authority to update the blog");
        }
        configureBlogEntity(blog, newBlog.getTitle(), newBlog.getContent(), newBlog.getModeratorIds(), newBlog.getTagIds(), newBlog.getAdIds(), newBlog.isLocked());
        if (newBlog.getBlockedUserIds() != null) {
            Set<Long> existingUserIds = userRepository.findExistingIds(newBlog.getBlockedUserIds());
            Set<Long> filteredUserIds = existingUserIds.stream()
                    .filter(userId -> !accessControl.checkUserOwnershipToBlog(newBlog.getBlogId(), userId))
                    .collect(Collectors.toSet());
            blog.setBlockedUsers(filteredUserIds);
        }
        blogRepository.save(blog);
        return blog.getId();
    }
    private void removeOlderViewCount(BlogEntity blog, LocalDate targetDate) {
        Map<LocalDate, Integer> viewCounts = blog.getViewCounts();
        boolean isModified = false;

        Iterator<Map.Entry<LocalDate, Integer>> it = viewCounts.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<LocalDate, Integer> entry = it.next();
            if (!entry.getKey().isAfter(targetDate)) {
                blog.setViews(blog.getViews() - entry.getValue());
                it.remove();
                isModified = true;
            }
        }

        if (isModified) {
            blogRepository.save(blog);
        }
    }
    private BlogDto mapToBlogDto(BlogEntity blogEntity) {
        BlogDto blogDto = new BlogDto();
        List<TagDto> tags = blogEntity.getTags().stream().map(this::mapToTagDto).collect(Collectors.toList());
        List<AdDto> ads = blogEntity.getAds().stream().map(this::mapToAdDto).collect(Collectors.toList());
        List<CommentDto> comments = blogEntity.getComments().stream().map(this::mapToCommentDto).collect(Collectors.toList());
        List<SubscriptionDto> subscriptions = blogEntity.getSubscriptions().stream().map(this::mapToSubscriptionDto).collect(Collectors.toList());
        List<UserDto> moderators = blogEntity.getModerators().stream().map(this::mapToUserDto).collect(Collectors.toList());

        blogDto.setId(blogEntity.getId());
        blogDto.setTitle(blogEntity.getTitle());
        blogDto.setContent(blogEntity.getContent());
        blogDto.setCreateDate(blogEntity.getCreateDate());
        blogDto.setLastUpdated(blogEntity.getLastUpdated());
        blogDto.setLocked(blogEntity.isLocked());
        blogDto.setArchived(blogEntity.isArchived());
        blogDto.setViews(blogEntity.getViews());
        blogDto.setLikes(blogEntity.getLikes());
        blogDto.setAuthor(mapToUserDto(blogEntity.getAuthor()));
        blogDto.setTags(tags);
        blogDto.setAds(ads);
        blogDto.setModerators(moderators);
        blogDto.setComments(comments);
        blogDto.setSubscriptions(subscriptions);

        return blogDto;

    }

    private UserDto mapToUserDto(UserEntity userEntity) {
        if (userEntity == null) {
            return null;
        }
        return new UserDto(userEntity.getId(), userEntity.getUserName(), userEntity.getEmail());
    }

    private TagDto mapToTagDto(TagEntity tagEntity) {
        if (tagEntity == null) {
            return null;
        }
        return new TagDto(tagEntity.getId(), tagEntity.getName());
    }

    private AdDto mapToAdDto(AdEntity adEntity) {
        if (adEntity == null) {
            return null;
        }
        return new AdDto(adEntity.getId(), adEntity.getTitle(), adEntity.getContent(), adEntity.getRpm(), adEntity.getCompany());
    }

    private CommentDto mapToCommentDto(CommentEntity commentEntity) {
        if (commentEntity == null) {
            return null;
        }
        CommentDto commentDto = new CommentDto();
        commentDto.setId(commentEntity.getId());
        commentDto.setContent(commentEntity.getContent());
        commentDto.setPostedAt(commentEntity.getPostedAt());
        commentDto.setBlogId(commentEntity.getBlog().getId());
        if (commentEntity.getUser() != null) {
            commentDto.setUserName(commentEntity.getUser().getUserName());
        }

        return commentDto;
    }

    private SubscriptionDto mapToSubscriptionDto(SubscriptionEntity subscriptionEntity) {
        if (subscriptionEntity == null) {
            return null;
        }
        return new SubscriptionDto(subscriptionEntity.getId(), subscriptionEntity.getUser().getId(), subscriptionEntity.getBlog().getId(), subscriptionEntity.getStatus());
    }
    private void configureBlogEntity(BlogEntity blog, String title, String content, List<Long> moderatorIds, List<Long> tagIds, List<Long> adIds, boolean isLocked) {
        if (title != null && !title.isEmpty()) {
            blog.setTitle(title);
        }
        if (content != null && !content.isEmpty()) {
            blog.setContent(content);
        }
        boolean isAuthorNotNull = blog.getAuthor() != null;
        boolean areBlockedUsersNotNull = blog.getBlockedUsers() != null;
        long authorId = isAuthorNotNull ? blog.getAuthor().getId() : -1;
        if (moderatorIds != null) {
            List<UserEntity> moderators = moderatorIds.stream()
                    .filter(moderatorId ->
                            (!isAuthorNotNull || moderatorId != authorId) &&
                            (!areBlockedUsersNotNull || !blog.getBlockedUsers().contains(moderatorId)))
                    .map(userRepository::getReferenceById)
                    .collect(Collectors.toList());
            blog.setModerators(moderators);
        }

        // while updating if I want to delete tags I will pass empty list
        if (tagIds != null) {
            List<TagEntity> tags = tagIds.stream()
                    .map(tagRepository::getReferenceById)
                    .collect(Collectors.toList());
            blog.setTags(tags);
        }

        // while updating if I want to delete ads I will pass empty list
        if (adIds != null) {
            List<AdEntity> ads = adIds.stream()
                    .map(adRepository::getReferenceById)
                    .collect(Collectors.toList());
            blog.setAds(ads);
        }
        blog.setLastUpdated(LocalDateTime.now());
        blog.setLocked(isLocked);
        blog.setArchived(false);
    }
    @Transactional(readOnly = true)
    public List<SubscriptionDto> reviewBlogSubscriptionRequests(long blogId, long userId) {
        if (!accessControl.checkUserOwnershipToBlog(blogId, userId)) {
           throw new DeniedAccessException("User has no ownership of Blog");
        }
        List<SubscriptionEntity> requestedSubscriptions = subscriptionRepository.findByBlogIdAndStatus(blogId, SubscriptionStatus.REQUESTED);
        return requestedSubscriptions.stream()
                .map(this::mapToSubscriptionDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public int respondBlogSubscriptionRequests(long blogId, long userId, List<Long> usersRequested, SubscriptionStatus status) {
        if (!accessControl.checkUserOwnershipToBlog(blogId, userId)) {
            throw new DeniedAccessException("User has no ownership of Blog");
        }
        //accessControl.checkUserOwnershipToBlog(blogId, userId);
        BlogEntity blog = blogRepository.findById(blogId).orElseThrow(()-> new ResourceNotFoundException("Blog by id " + blogId + " not exists"));
        List<SubscriptionEntity> affectedSubscriptions = subscriptionRepository.findByBlogIdAndUserIdIn(blogId, usersRequested);
        int count = 0;
        for (SubscriptionEntity subscription : affectedSubscriptions) {
            if (subscription.getStatus() != status && !blog.getBlockedUsers().contains(subscription.getUser().getId())) {
                subscription.setStatus(status);
                count++;
            }
        }
        subscriptionRepository.saveAll(affectedSubscriptions);
        return count;
    }

    @Transactional
    public ViewBlogDto viewBlog(long blogId, long userId) {
        BlogEntity blog = blogRepository.findById(blogId).orElseThrow(()-> new ResourceNotFoundException("Blog by id " + blogId + " not exists"));
        accessControl.checkUserAccessToBlog(blog, userId);
        if(blog.getAuthor().getId() != userId) {
        blog.setViews(blog.getViews() + 1);
        LocalDate day = LocalDateTime.now().toLocalDate().atStartOfDay().toLocalDate();
        Map<LocalDate, Integer> viewCounts = blog.getViewCounts();
        viewCounts.put(day, viewCounts.getOrDefault(day, 0) + 1);
        }
        blogRepository.save(blog);
        return createBlogViewDto(blog);
    }

    private ViewBlogDto createBlogViewDto(BlogEntity blog) {
        ViewBlogDto blogViewDto = new ViewBlogDto();
        blogViewDto.setId(blog.getId());
        blogViewDto.setTitle(blog.getTitle());
        blogViewDto.setContent(blog.getContent());
        blogViewDto.setCreateDate(blog.getCreateDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")));
        blogViewDto.setLastUpdated(blog.getLastUpdated().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")));
        blogViewDto.setViews(blog.getViews());
        blogViewDto.setLikes(blog.getLikes());
        blogViewDto.setAuthorName(blog.getAuthor().getUserName());

        List<String> tagNames = blog.getTags().stream()
                .map(TagEntity::getName)
                .collect(Collectors.toList());
        blogViewDto.setTagNames(tagNames);

        List<ViewAdDto> ads = blog.getAds().stream()
                .map(ad -> new ViewAdDto(ad.getTitle(), ad.getContent(), ad.getCompany()))
                .collect(Collectors.toList());
        blogViewDto.setAds(ads);

        List<ViewCommentDto> comments = blog.getComments().stream()
                .map(comment -> new ViewCommentDto(comment.getContent(), comment.getPostedAt(), comment.getUser().getUserName()))
                .collect(Collectors.toList());
        blogViewDto.setComments(comments);

        return blogViewDto;
    }
    //search blog by title and/or tags
    @Transactional(readOnly = true)
    public List<SearchBlogDto> searchBlog(String title, List<String> tags, long userId) {
        String searchTitle = (title != null && !title.isBlank()) ? title.toLowerCase() : null;
        List<String> lowerCaseTagNames = (tags != null && !tags.isEmpty()) ?
                tags.stream().map(String::toLowerCase).collect(Collectors.toList()) : null;
        List<BlogEntity> blogEntities = blogRepository.searchByTitleAndTags(searchTitle, lowerCaseTagNames);
        return blogEntities.stream()
                .filter(blogEntity -> !blogEntity.getBlockedUsers().contains(userId))
                .map(this::mapToSearchBlogDto)
                .collect(Collectors.toList());
    }
    @Transactional
    public boolean likeBlog(long blogId, long userId) {
        BlogEntity blog = blogRepository.findById(blogId).orElseThrow(()-> new ResourceNotFoundException("Blog by id " + blogId + " not exists"));
        accessControl.checkUserAccessToBlog(blog,userId);
        if (blog.getLikedByUsers().contains(userId)) {
            return false;
        }
        blog.setLikes(blog.getLikes() + 1);
        blog.getLikedByUsers().add(userId);
        blogRepository.save(blog);
        return true;
    }
    @Transactional
    public boolean makeUserModerator(long blogId, long authorId, long userId){
        BlogEntity blog = blogRepository.findById(blogId).orElseThrow(()-> new ResourceNotFoundException("Blog by id " + blogId + " not exists"));
        UserEntity user = userRepository.findById(userId).orElseThrow(()-> new ResourceNotFoundException("User by id " + userId + " not exists"));
        if(blog.getAuthor().getId()!=authorId || blog.getAuthor().getId() == userId){
            return false;
        }
        if (!blog.getModerators().contains(user)) {
            if(blog.getBlockedUsers().contains(userId)){
                blog.getBlockedUsers().remove(userId);
            }
            blog.getModerators().add(user);
        }
        blogRepository.save(blog);
        return true;
    }
    private SearchBlogDto mapToSearchBlogDto(BlogEntity blog) {
        SearchBlogDto searchBlogDto = new SearchBlogDto();
        searchBlogDto.setId(blog.getId());
        searchBlogDto.setTitle(blog.getTitle());
        searchBlogDto.setLastUpdated(blog.getLastUpdated());
        searchBlogDto.setViews(blog.getViews());
        searchBlogDto.setLikes(blog.getLikes());
        searchBlogDto.setAuthor(blog.getAuthor().getUserName());
        List<String> tagNames = blog.getTags().stream()
                .map(TagEntity::getName)
                .collect(Collectors.toList());
        searchBlogDto.setTagNames(tagNames);
        searchBlogDto.setLocked(blog.isLocked());
        return searchBlogDto;
    }

    @Transactional(readOnly = true)
    public List<SearchBlogDto> getPopularBlogs() {
        List<BlogEntity> entities = blogRepository.findByIsArchivedFalseOrderByViewsDesc();
        return entities.stream()
                .limit(10)
                .map(this::mapToSearchBlogDto)
                .collect(Collectors.toList());
    }
    @Transactional
    public boolean blockUserForBlog(long blogId, long actingUserId, long targetUserId){
        BlogEntity blog = blogRepository.findById(blogId).orElseThrow(()-> new ResourceNotFoundException("Blog by id " + blogId + " not exists"));
        if(userRepository.isUserOwnerOrModeratorOfBlog(blogId, targetUserId)){
            return false;
        }
        if (!accessControl.checkUserOwnershipToBlog(blogId, actingUserId)) {
            throw new DeniedAccessException("User has no ownership of Blog");
        }
        //accessControl.checkUserOwnershipToBlog(blogId,actingUserId);
        if(actingUserId==targetUserId){
           return false;
        }
        if(blog.getBlockedUsers().contains(targetUserId)){
            return false;
        }
        List<Long> targetUserList = Collections.singletonList(targetUserId);
        respondBlogSubscriptionRequests(blogId,actingUserId,targetUserList,SubscriptionStatus.REJECTED);
        blog.getBlockedUsers().add(targetUserId);
        blogRepository.save(blog);
        return true;
    }
    @Transactional
    public String subscribeBlog(long userId, long blogId){
        UserEntity user = userRepository.findById(userId).orElseThrow(()-> new ResourceNotFoundException("User` by id " + userId + " not exists"));

        BlogEntity blog = blogRepository.findById(blogId).orElseThrow(()-> new ResourceNotFoundException("Blog by id " + blogId + " not exists"));


        if(!blog.isLocked()){
            return "Blog is not locked, no need for subscription";
        }
        if (blog.getAuthor().equals(user) || blog.getModerators().contains(user)) {
            return "Authors and moderators cannot subscribe to the blog";
        }
        if (blog.getBlockedUsers().contains(user.getId())) {
            return "Blocked users cannot subscribe to the blog";
        }
        List<SubscriptionEntity> existingSubscriptions = subscriptionRepository.findAllByUserAndBlog(user, blog);
        if (!existingSubscriptions.isEmpty()) {
            return "A subscription request already exists";
        }
        SubscriptionEntity subscription = new SubscriptionEntity();
        subscription.setUser(user);
        subscription.setBlog(blog);
        subscription.setStatus(SubscriptionStatus.REQUESTED);

        subscriptionRepository.save(subscription);

        return "Subscription request sent";
    }
}