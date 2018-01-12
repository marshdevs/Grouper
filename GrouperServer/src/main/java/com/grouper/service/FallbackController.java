package com.grouper.service;

import com.grouper.models.Message;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class FallbackController {

    /**
     * Fallback endpoint. Bad gateway.
     *
     * @return Message(status, description, field, value)
     *          status: 404
     *          description: "Endpoint not found."
     *          field: NO_FIELD
     *          value: NO_VALUE
     */
    @RequestMapping(value = "*")
    @ResponseBody
    public ResponseEntity<Message> fallback() {
        return new ResponseEntity<Message>(new Message.MessageBuilder(Message.NOT_FOUND_STATUS)
            .build(), HttpStatus.BAD_GATEWAY);
    }
}
