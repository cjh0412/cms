package com.cjhdev.cms.user.client.mailgun;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
// 무분별한 setter 사용 방지, ToString을 통한 순환 참조 문제 방지를 위해 사용x
//@Data

public class SendMailForm {
    private String from;
    private String to;
    private String subject;
    private String text;

}
