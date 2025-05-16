package org.example.final_graduation.controllers.admin.orders;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendConfirmationEmail(String toEmail, String fullName, String orderDetails) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Xác nhận đơn hàng");
        message.setText("Chào " + fullName + ",\n\n"
                + "Cảm ơn bạn đã đặt hàng tại cửa hàng của chúng tôi.\n"
                + "Thông tin đơn hàng:\n" + orderDetails + "\n\n"
                + "Trân trọng,\nCửa hàng giày thể thao");

        mailSender.send(message);
    }
}


