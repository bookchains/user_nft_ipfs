package com.example.blockchain.Domain.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Users {

    @Id
    @Column
    private String walletAccount;

    @Column
    private String name;

    @Column
    private String nickName;

    @Column
    private String phone;

    @Column
    private String address;

    @Column(unique = true)
    private String email;

    @Column
    private String userImg;

}
