package com.nulhart.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;


@Entity
@Data
@RequiredArgsConstructor
public class MALToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(columnDefinition = "TEXT")
    private String access_token;
    @Column(columnDefinition = "TEXT")
    private String refresh_token;
    private Integer expires;
    private String username;
}
