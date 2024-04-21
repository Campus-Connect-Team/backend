package com.campusconnect.backend.board.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class BoardController {

    // Token이 존재하지 않는다면 접속 불가능하다.
    @GetMapping("/boards")
    public String accessBoard() {
        return "판매 게시판 접속 성공";
    }
}
