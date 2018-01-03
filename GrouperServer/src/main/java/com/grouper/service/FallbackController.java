package com.grouper.service;

import com.grouper.models.Message;
import org.springframework.web.bind.annotation.*;

@RestController
public class FallbackController {

    @RequestMapping(value = "*")
    @ResponseBody
    public Message fallback() {
        return new Message.MessageBuilder(Message.NOT_FOUND_STATUS)
            .build();
    }
}
