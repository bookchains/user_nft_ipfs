package com.example.blockchain.Controller;


import com.example.blockchain.Domain.Dto.EmailVerificationResult;
import com.example.blockchain.Domain.Dto.EmailVerifyRequestDto;
import com.example.blockchain.Domain.Dto.MemberEmailRequestDto;
import com.example.blockchain.Domain.Dto.SingleResponseDto;
import com.example.blockchain.Services.MailService;
import com.example.blockchain.Services.MemberService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class EmailController {

    @Autowired
    private MemberService memberService;
    @Autowired
    private MailService mailService;

    @PostMapping("/emails/verification-requests")
    public ResponseEntity sendMessage(@RequestBody MemberEmailRequestDto dto  ) throws MessagingException {
        memberService.sendCodeToEmail(dto.getEmail());

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/emails/verifications")
    public ResponseEntity verificationEmail(@RequestBody EmailVerifyRequestDto dto) {
        EmailVerificationResult response = memberService.verifiedCode(dto.getEmail(), dto.getCode());

        return new ResponseEntity<>(new SingleResponseDto<>(response), HttpStatus.OK);
    }
}
