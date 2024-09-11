package com.swd.notification_service.services;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailDetail {
    private String recipient;
    private String name;
    private String subject;
    private String msgBody;
    private String attachment;
}
