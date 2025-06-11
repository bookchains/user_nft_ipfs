package com.example.blockchain.Controller;

import com.example.blockchain.Domain.Dto.*;
import com.example.blockchain.Domain.Entity.Users;
import com.example.blockchain.Repository.UsersRepository;
import com.example.blockchain.Services.NFTServices;
import com.example.blockchain.Services.UserService;
import com.example.blockchain.Status.StatusCode;
import com.example.blockchain.jwt.JwtProperties;
import com.example.blockchain.jwt.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private JwtProperties jwtProperties;
    @Autowired
    private NFTServices nftServices;


    //로그인
    @PostMapping("/login")
    public ResponseEntity<TokenResponseDTO> login(@RequestBody LoginRequestDTO loginRequestDTO){

        String accessToken = jwtUtil.generateAccessToken(loginRequestDTO.getUsername(), loginRequestDTO.getAddress(), loginRequestDTO.getPrivKey(),"USER");
        String refreshToken = jwtUtil.generateRefreshToken(loginRequestDTO.getUsername(), loginRequestDTO.getAddress(), loginRequestDTO.getPrivKey(), "USER");


        redisTemplate.opsForValue().set(loginRequestDTO.getUsername(), refreshToken);

        TokenResponseDTO dto = new TokenResponseDTO(accessToken, refreshToken);

        return ResponseEntity.ok(dto);
    }

    //로그아웃
    @PostMapping("/logout")
    public ResponseEntity<Integer> logout(HttpServletRequest request){
        String token = jwtUtil.resolveToken(request);
        String username = jwtUtil.getUsername(token);

        long remaining = jwtUtil.getRemainingMillis(token);
        redisTemplate.opsForValue().set("BLACKLIST:" + token, "logout", remaining, TimeUnit.MILLISECONDS);

        redisTemplate.delete(username);

        return ResponseEntity.ok(StatusCode.OK);
    }




    //사용자 추가 정보 저장
    @PostMapping(value = "/register", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Users> registerUser(@RequestPart("dto") UsersRegisterDto dto, @RequestPart("image") MultipartFile imageFile) throws IOException {
        // 1. 이미지 저장
        String savedImagePaths;
        String uploadDir = "/your/upload/path";

        String originalFilename = imageFile.getOriginalFilename();
        String newFileName = UUID.randomUUID() + "_" + originalFilename;
        File dest = new File(uploadDir, newFileName);
        imageFile.transferTo(dest);

        Users user = Users.builder()
                .walletAccount(dto.getWalletAccount())
                .phone(dto.getPhone())
                .name(dto.getName())
                .address(dto.getAddress())
                .email(dto.getEmail())
                .nickName(dto.getNickName())
                .userImg("/uploads/" + newFileName).build();

        return ResponseEntity.ok(userService.registerUser(user));
    }




    //사용자 추가 정보 가져오기
    @PostMapping("/getUserData")
    public ResponseEntity<Users> getUSerData(@RequestBody GetUserDataDto dto){

        return ResponseEntity.ok(userService.findUsersByAccount(dto.getAccount()));
    }

    @PostMapping("/check")
    public ResponseEntity<Boolean> checkAccount(@RequestBody CheckAccountDto dto){

        boolean check = usersRepository.existsById(dto.getAccount());

        return ResponseEntity.ok(!check);
    }

    @GetMapping("/checkBalance")
    public ResponseEntity<BigDecimal> checkBalance(HttpServletRequest request) throws Exception {
        String token = jwtUtil.resolveToken(request);
        String address = jwtUtil.getAddress(token);

        BigDecimal balance = nftServices.getBalanceInEther(address);

        return ResponseEntity.ok(balance);
    }



}
