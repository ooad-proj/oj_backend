package com.ooad.oj_backend.contorller;

import com.ooad.oj_backend.mapper.GroupMapper;
import com.ooad.oj_backend.mapper.UserMapper;
import com.ooad.oj_backend.service.GroupService;
import com.ooad.oj_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/")
public class GroupController {
    @Autowired
    private GroupMapper groupMapper;
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

    @RequestMapping(value = "group/{groupId}/member/{memberId}", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<?> addUserToGroup(@RequestParam(value = "name",required = false)int groupId,@RequestParam(value = "name",required = false) String memberId) {
        return groupService.addUserToGroup(groupId,memberId);
    }

//    public ResponseEntity<?> addBatchUserToGroup(){
//
//    }

    @RequestMapping(value = "group/{groupId}/member/{memberId}",method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<?> deleteUserInGroup(@PathVariable int groupId,String memberId) {
        return groupService.deleteUserInGroup(groupId,memberId);
    }

    @RequestMapping(value = "group/{groupId}/assistant/{assistantId}", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<?> addAssistantToGroup(@RequestParam(value = "name",required = false)int groupId,@RequestParam(value = "name",required = false) String assistantId) {
        return groupService.addAssistantToGroup(groupId,assistantId);
    }

    @RequestMapping(value = "group/{groupId}/assistant/{assistantId}",method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<?> deleteAssistantInGroup(@PathVariable int groupId,String memberId) {
        return groupService.deleteAssistantInGroup(groupId,memberId);
    }

    @RequestMapping(value = "group/{groupId}/assistants",method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getAssistantInGroup(@PathVariable int groupId) {
        return groupService.getAssistantInGroup(groupId);
    }

    @RequestMapping(value = "group/{groupId}/assistant/{assistantId}",method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getUsersInGroup(@PathVariable int groupId) {
        return groupService.getUsersInGroup(groupId);
    }
}
