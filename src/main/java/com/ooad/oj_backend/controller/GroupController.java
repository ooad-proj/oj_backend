package com.ooad.oj_backend.controller;

import com.ooad.oj_backend.mapper.GroupMapper;
import com.ooad.oj_backend.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/")
public class GroupController {
    @Autowired
    private GroupMapper groupMapper;
    @Autowired
    private GroupService groupService;

    @PostMapping("group/{name}")
    @ResponseBody
    public ResponseEntity<?> addGroup(int id, @RequestParam(value = "name", required = false) String name) {
        return groupService.addGroup(id, name);
    }

    @RequestMapping(value = "group/{groupId}", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<?> updateGroup(@PathVariable int id, @RequestParam(value = "newName", required = false) String newName) {
        return groupService.addGroup(id, newName);
    }

    @RequestMapping(value = "group/{groupId}", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<?> deleteGroup(@PathVariable int id) {
        return groupService.deleteGroup(id);
    }

    @PostMapping(value = "group/{groupId}/member/{memberId}")
    @ResponseBody
    public ResponseEntity<?> addUserToGroup(@PathVariable int groupId ,@PathVariable String memberId) {

        return groupService.addUserToGroup(groupId,memberId);
    }

    @PostMapping(value = "group/{groupId}/member/add/batch")
    @ResponseBody
    public ResponseEntity<?> addBatchUserToGroup(@PathVariable("groupId")int groupId, MultipartFile file){
        return groupService.addBatchUserToGroup(groupId,file);
    }

    @RequestMapping(value = "group/{groupId}/member/{memberId}",method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<?> deleteUserInGroup(@PathVariable int groupId, @PathVariable String memberId) {
        return groupService.deleteUserInGroup(groupId,memberId);
    }

    @RequestMapping(value = "group/{groupId}/assistant/{assistantId}", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<?> addAssistantToGroup(@PathVariable int groupId,@PathVariable  String assistantId) {
        return groupService.addAssistantToGroup(groupId,assistantId);
    }

    @RequestMapping(value = "group/{groupId}/assistant/{assistantId}",method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<?> deleteAssistantInGroup(@PathVariable int groupId, @PathVariable String assistantId) {
        return groupService.deleteAssistantInGroup(groupId,assistantId);
    }

    @RequestMapping(value = "group/{groupId}/assistants",method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getAssistantInGroup(@PathVariable int groupId) {
        return groupService.getAssistantInGroup(groupId);
    }

    @RequestMapping(value = "group/{groupId}/members",method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getUsersInGroup(@PathVariable int groupId , int page,int itemsPerPage,String search) {
        return groupService.getUsersInGroup(groupId, page, itemsPerPage, search);
    }

    @RequestMapping(value = "group",method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getAllGroup( int page,int itemsPerPage,String search) {
        return groupService.getAllGroup( page, itemsPerPage, search);
    }
    @RequestMapping(value = "group/{groupId}",method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getOneGroup(@PathVariable int groupId) {
        return groupService.getOneGroup(groupId);
    }
}
