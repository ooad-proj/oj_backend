package com.ooad.oj_backend.controller.forum;

import com.ooad.oj_backend.service.contest.ContestService;
import com.ooad.oj_backend.service.forum.ForumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/")
public class ForumController {
    @Autowired
    private ForumService forumService;


    @RequestMapping(value = "/forum/{groupId}", method = RequestMethod.POST)
    public ResponseEntity<?> createPost(@PathVariable int groupId, String title, String content, Boolean goPublic, Boolean goMail) {
        return forumService.createPost(groupId,title,content,goPublic,goMail);
    }


    @RequestMapping(value = "/forum/post/{postId}", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<?> updatePost(@PathVariable int postId, String title, String content, Boolean goPublic) {
        return forumService.updatePost(postId, title, content, goPublic);
    }

    @RequestMapping(value = "/forum/post/{postId}", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<?> deletePost(@PathVariable int postId) {
        return forumService.deletePost(postId);
    }

    @RequestMapping(value = "/forum/{groupId}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getPostList ( @PathVariable int groupId , String search, int page, int itemsPerPage ) {
        return forumService.getPostList(groupId, search, page, itemsPerPage);
    }

    @RequestMapping(value = "/forum/post/{postId}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getPostInformation ( @PathVariable int postId ) {
        return forumService.getPostInformation(postId);
    }

    @RequestMapping(value = "/forum/post/{postId}/comment", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> addComment (  @PathVariable int postId, @RequestBody String comment ) {
        return forumService.addComment(postId, comment);
    }

    @RequestMapping(value = "/forum/comment/{commentId}", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<?> deleteComment (  @PathVariable int commentId ) {
        return forumService.deleteComment(commentId);
    }

    @RequestMapping(value = "/forum/post/{postId}/comment", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getCommentList (  @PathVariable int postId,int page, int itemsPerPage  ) {
        return forumService.getCommentList(postId,page,itemsPerPage);
    }

    @RequestMapping(value = "/forum/{groupId}/announcement", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getAnnouncement (  @PathVariable int groupId,int length  ) {
        return forumService.getAnnouncement(groupId, length);
    }
}
