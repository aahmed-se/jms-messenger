package com.idea.tools.dto;

import com.intellij.util.xmlb.annotations.Transient;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
@ToString(exclude = "queue")
@EqualsAndHashCode(exclude = "queue")
public class MessageDto {

    private String messageID;
    private String correlationId;
    private Long timestamp;
    private String jmsType;
    private ContentType type;
    private String payload;
    private Integer deliveryMode;
    private Integer priority;
    private Long expiration;
    private QueueDto queue;
    private List<HeaderDto> headers = new ArrayList<>();

    @Transient
    public QueueDto getQueue() {
        return queue;
    }
}
