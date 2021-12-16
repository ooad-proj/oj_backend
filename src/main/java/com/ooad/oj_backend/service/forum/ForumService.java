package com.ooad.oj_backend.service.forum;

import cn.dev33.satoken.stp.StpUtil;
import com.ooad.oj_backend.Response;
import com.ooad.oj_backend.mapper.forum.ForumMapper;
import com.ooad.oj_backend.mybatis.entity.Announcement;
import com.ooad.oj_backend.mybatis.entity.CommentByPage;
import com.ooad.oj_backend.mybatis.entity.PostByPage;
import com.ooad.oj_backend.mybatis.entity.PostInformation;
import com.ooad.oj_backend.service.user.AuthService;
import com.ooad.oj_backend.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ForumService {
//    @Autowired
//    private ContestMapper contestMapper;
//    @Autowired
//    private UserMapper userMapper;
//    @Autowired
//    private ProblemMapper problemMapper;
    @Autowired
    private AuthService authService;
    @Autowired
    private UserService userService;

    @Autowired
    private ForumMapper forumMapper;

    public ResponseEntity<?> createPost(int groupId,String title,String content, Boolean goPublic,Boolean goMail) {
        if(!StpUtil.isLogin()){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        if(goPublic && goMail){
            ResponseEntity responseEntity1 = authService.checkPermission("1-0");
            if(responseEntity1!=null){
                ResponseEntity responseEntity2 = authService.checkPermission( "1-" + groupId);
                if(responseEntity2!=null){
                    return responseEntity2;
                }
            }
        }
        String userId = (String) StpUtil.getLoginId();
        long modifyTime = System.currentTimeMillis();
        forumMapper.createPost(groupId,userId,title,content,modifyTime,goPublic,goMail);
        Response response=new Response();

        response.setCode(0);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    public ResponseEntity<?> updatePost(int postId,String title,String content, Boolean goPublic) {
        if(!StpUtil.isLogin()){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        Response response=new Response();
        int groupId = forumMapper.getGroupId(postId);
        String userId = (String) StpUtil.getLoginId();
        Boolean isPublic = forumMapper.getGoPublic(postId);
        if(isPublic || goPublic){
            ResponseEntity responseEntity1 = authService.checkPermission("1-0");
            if(responseEntity1!=null){
                ResponseEntity responseEntity2 = authService.checkPermission("1-" + groupId);
                if(responseEntity2!=null){
                    response.setCode(-1);
                    return new ResponseEntity<>(response,HttpStatus.OK);
                }
            }
        }else {
            if (!userId.equals(forumMapper.getPostCreatorId(postId)) ){
                response.setCode(-1);
                return new ResponseEntity<>(response,HttpStatus.OK);
            }
        }

        long modifyTime = System.currentTimeMillis();
        forumMapper.updatePost(postId, title, content, goPublic,modifyTime);
        response.setCode(0);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }


    public ResponseEntity<?> deletePost(int postId) {
        if(!StpUtil.isLogin()){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        int groupId = forumMapper.getGroupId(postId);
        String userId = (String) StpUtil.getLoginId();
        Boolean isPublic = forumMapper.getGoPublic(postId);
        Response response=new Response();
        if(isPublic){
            ResponseEntity responseEntity1 = authService.checkPermission("1-0");
            if(responseEntity1!=null){
                ResponseEntity responseEntity2 = authService.checkPermission("1-" + groupId);
                if(responseEntity2!=null){
                    response.setCode(-1);
                    return new ResponseEntity<>(response,HttpStatus.OK);
                }
            }
        }else {
            if(!userId.equals(forumMapper.getPostCreatorId(postId))){
                ResponseEntity responseEntity1 = authService.checkPermission("1-0");
                if(responseEntity1!=null){
                    ResponseEntity responseEntity2 = authService.checkPermission("1-" + groupId);
                    if(responseEntity2!=null && !forumMapper.getPostCreatorId(postId).equals(userId)){
                        response.setCode(-1);
                        return new ResponseEntity<>(response,HttpStatus.OK);
                    }
                }
            }
        }
        forumMapper.delete(postId);
        response.setCode(0);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }


    public ResponseEntity<?> getPostList(int groupId,String search,int page, int itemsPerPage) {
        if(!StpUtil.isLogin()){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        List<PostByPage> postByPage= forumMapper.getPostByPage(groupId,(page - 1) * itemsPerPage,itemsPerPage,search);
        int totalAmount = forumMapper.getPostByPageTotalAmount(groupId,search);
        int totalPage = (totalAmount/ itemsPerPage) + (((totalAmount % itemsPerPage) == 0) ? 0 : 1);
        for(int i =0 ; i<postByPage.size();i++){
            if( postByPage.get(i).getPreview().length()>20){
                postByPage.get(i).setPreview(postByPage.get(i).getPreview().substring(0,20));
            }

        }
        HashMap<String,Object>hashMap=new HashMap<>();
        hashMap.put("list",postByPage);
        hashMap.put("totalPage",totalPage);
        hashMap.put("totalAmount",totalAmount);
        Response response=new Response();
        response.setContent(hashMap);
        response.setCode(0);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    public ResponseEntity<?> getPostInformation(int postId) {
        if(!StpUtil.isLogin()){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        PostInformation postInformation = forumMapper.getPostInformation(postId);
        HashMap<String,Object>hashMap=new HashMap<>();
        hashMap.put("userName",postInformation.getUserName());
        hashMap.put("userId",postInformation.getUserId());
        hashMap.put("modifyTime",postInformation.getModifyTime());
        hashMap.put("title",postInformation.getTitle());
        hashMap.put("content",postInformation.getContent());
        hashMap.put("goPublic",postInformation.getGoPublic());
        hashMap.put("deleteable",true);
        hashMap.put("modifyable",true);
        Response response=new Response();
        int groupId = postInformation.getGroupId();
        String userId = (String) StpUtil.getLoginId();
        if(postInformation.getGoPublic()){
            ResponseEntity responseEntity1 = authService.checkPermission("1-0");
            if(responseEntity1!=null){
                ResponseEntity responseEntity2 = authService.checkPermission("1-" + groupId);
                if(responseEntity2!=null){
                    hashMap.put("deleteable",false);
                }
            }
        }else {
            if(!userId.equals(forumMapper.getPostCreatorId(postId))){
                ResponseEntity responseEntity1 = authService.checkPermission("1-0");
                if(responseEntity1!=null){
                    ResponseEntity responseEntity2 = authService.checkPermission("1-" + groupId);
                    if(responseEntity2!=null &&!userId.equals(forumMapper.getPostCreatorId(postId))){
                        hashMap.put("deleteable",false);
                    }
                }
            }
        }

        if(postInformation.getGoPublic()){
            ResponseEntity responseEntity1 = authService.checkPermission("1-0");
            if(responseEntity1!=null){
                ResponseEntity responseEntity2 = authService.checkPermission("1-" + groupId);
                if(responseEntity2!=null){
                    hashMap.put("modifyable",false);
                }
            }
        }else {
            if (!userId.equals(forumMapper.getPostCreatorId(postId)) ){
                hashMap.put("modifyable",false);
            }
        }

        response.setContent(hashMap);
        response.setCode(0);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    public ResponseEntity<?> addComment(int postId,String comment) {
        if(!StpUtil.isLogin()){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        int floorId;
        if(forumMapper.getFloorNum(postId)==null){
            floorId = 0;
        }else {
            floorId  = Integer.parseInt(forumMapper.getFloorNum(postId))+1;
        }

        String userId = (String) StpUtil.getLoginId();
        long modifyTime = System.currentTimeMillis();
        forumMapper.addComment(postId,floorId,userId,comment,modifyTime);
        Response response=new Response();
        response.setCode(0);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    public ResponseEntity<?> deleteComment(int commentId) {
        if(!StpUtil.isLogin()){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        int postId = forumMapper.getPostId(commentId);
        int groupId = forumMapper.getGroupId(postId);
        String userId = (String) StpUtil.getLoginId();
//        Boolean isPublic = forumMapper.getGoPublic(postId);
        Response response=new Response();

        ResponseEntity responseEntity = authService.checkPermission("1-0");
        if(responseEntity!=null){
            ResponseEntity responseEntity1 = authService.checkPermission("1-"+groupId);
            if(responseEntity1!=null){
                if (!userId.equals(forumMapper.getCommentCreatorId(commentId))){
                    response.setCode(-1);
                    return new ResponseEntity<>(response,HttpStatus.OK);
                }
            }
        }

        forumMapper.deleteComment(commentId);
        response.setCode(0);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    public ResponseEntity<?> getCommentList(int postId,int page, int itemsPerPage) {
        if(!StpUtil.isLogin()){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        List<CommentByPage> commentByPage= forumMapper.getCommentByPage(postId,(page - 1) * itemsPerPage,itemsPerPage);
        int totalAmount = forumMapper.getCommentByPageTotalAmount(postId);
        int totalPage = (totalAmount/ itemsPerPage) + (((totalAmount % itemsPerPage) == 0) ? 0 : 1);

        for(int i =0 ; i<commentByPage.size();i++){
            if(StpUtil.getLoginId().toString().equals(commentByPage.get(i).getUserId())){
                commentByPage.get(i).setDeleteable(true);
            }
        }
        HashMap<String,Object>hashMap=new HashMap<>();
        hashMap.put("list",commentByPage);
        hashMap.put("totalPage",totalPage);
        hashMap.put("totalAmount",totalAmount);
        Response response=new Response();
        response.setContent(hashMap);
        response.setCode(0);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    public ResponseEntity<?> getAnnouncement(int groupId,int length) {
        Response response=new Response();
        List<Announcement> announcements = forumMapper.getAnnouncement(groupId,length);
        for(int i =0;i<announcements.size();i++){
            if(announcements.get(i).getPreview().length()>20){
                announcements.get(i).setPreview(announcements.get(i).getPreview().substring(0,20));
            }
        }
        response.setContent(announcements);
        response.setCode(0);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
}
