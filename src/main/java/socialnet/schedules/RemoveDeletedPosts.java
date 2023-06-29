package socialnet.schedules;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import socialnet.service.CommentService;
import socialnet.service.PostService;

@Component
@EnableAsync
@RequiredArgsConstructor
public class RemoveDeletedPosts {

    private final PostService postService;
    private final CommentService commentService;

    @Async
    @Scheduled(cron = "${schedules.deletePostsInterval}")
    public void removeDeletedPosts() {
        postService.hardDeletingPosts();
    }

    @Async
    @Scheduled(cron = "${schedules.deletePostsInterval}")
    public void removeDeletedComments() {
        commentService.hardDeleteComments();
    }

}
