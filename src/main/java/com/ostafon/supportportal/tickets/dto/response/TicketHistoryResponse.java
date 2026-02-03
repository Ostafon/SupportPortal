package com.ostafon.supportportal.tickets.dto.response;


import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class TicketHistoryResponse {

    private Long id;

    private Long ticketId;

    private Long changedBy;

    private String field;

    private String oldValue;

    private String newValue;

    private LocalDateTime createdAt;
}
