package com.grouper.service;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;
import com.grouper.models.Message;
import com.grouper.models.SkillSet;
import com.grouper.models.User;
import com.grouper.objectcache.GroupObjectCache;
import com.grouper.objectcache.UserObjectCache;
import com.grouper.requestmodels.CreateUserRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
public class UserRequestController {

    @RequestMapping(value="/getUser", method=RequestMethod.GET)
    @ResponseBody
    public Message getUser(@RequestParam(value = "userId", defaultValue = "00000000") String userId) {

        User user = GrouperServiceApplication.userObjectCache.getObject(userId);

        return new Message.MessageBuilder(200)
            .withField("User")
            .withValue(user)
            .build();
    }

    @RequestMapping(value = "/createUser", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
        consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public ResponseEntity<Message> createUser(@RequestBody CreateUserRequest request) {

        String userId = GrouperServiceApplication.hashids.encode(Instant.now().toEpochMilli());
        User newUser = new User.UserBuilder(userId)
            .withUserName(request.getName())
            .withUserOccupation(request.getOccupation())
            .withUserSkillSet(new SkillSet(request.getSkills()))
            .withUserEvent(request.getEventId())
            .build();

        GrouperServiceApplication.userObjectCache.putObject(newUser);

        return new ResponseEntity<Message>(new Message.MessageBuilder(200)
            .withField("User")
            .withValue(newUser)
            .build(), HttpStatus.OK);
    }


}
