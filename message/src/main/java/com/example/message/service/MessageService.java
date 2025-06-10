package com.example.message.service;

import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.dysmsapi20170525.models.SendSmsResponseBody;
import com.example.message.entity.MessageRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessageService {
    @Autowired
    private Client client;

    @Autowired
    private MessageRecordService messageRecordService;

    public String sendCode(String phone) throws Exception {
        String content = "{\"code\":\"1234\"}";
        SendSmsRequest sendSmsRequest = new com.aliyun.dysmsapi20170525.models.SendSmsRequest()
                .setPhoneNumbers(phone)
                .setSignName("阿里云短信测试").setTemplateCode("SMS_154950909")
                .setTemplateParam(content);
        SendSmsResponse sendSmsResponse = client.sendSms(sendSmsRequest);
        SendSmsResponseBody body = sendSmsResponse.getBody();
        String code = body.getCode();
        // 记录到数据表
        MessageRecord messageRecord = new MessageRecord();
        messageRecord.setPhone(phone);
        messageRecord.setContent(content);
        messageRecord.setCode(code);
        messageRecord.setReason(body.getMessage());

        messageRecordService.insert(messageRecord);
        return code;
    }
}
