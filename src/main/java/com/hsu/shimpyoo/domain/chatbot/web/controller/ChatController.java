package com.hsu.shimpyoo.domain.chatbot.web.controller;

import com.hsu.shimpyoo.domain.chatbot.service.ChatRoomService;
import com.hsu.shimpyoo.domain.chatbot.web.dto.ChatQuestionDto;
import com.hsu.shimpyoo.domain.chatbot.service.ChatService;
import com.hsu.shimpyoo.domain.chatbot.web.dto.ModifyChatRoomTitleDto;
import com.hsu.shimpyoo.global.response.CustomAPIResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final ChatRoomService chatRoomService;

    // 채팅방 생성
    @PostMapping("/makeChatRoom")
    public ResponseEntity<CustomAPIResponse<?>> makeChatRoom(){
        ResponseEntity<CustomAPIResponse<?>> response =chatRoomService.makeChatRoom();
        return response;
    }

    // 채팅방 제목 수정
    @PutMapping("/modifyChatRoomTitle")
    public ResponseEntity<CustomAPIResponse<?>> modifyChatRoomTitle(@RequestBody @Valid ModifyChatRoomTitleDto modifyChatRoomTitleDto){
        ResponseEntity<CustomAPIResponse<?>> response=chatRoomService.modifyChatRoomTitle(modifyChatRoomTitleDto);
        return response;
    }

    // 사용자 입력 메시지를 받아서 처리
    @PostMapping("/ask")
    public ResponseEntity<CustomAPIResponse<?>> askChat(@RequestBody @Valid ChatQuestionDto chatQuestionDto) {
        try {
            // ChatService를 호출하여 메시지 처리
            ResponseEntity<CustomAPIResponse<?>> response = chatService.askForChat(chatQuestionDto);

            // 성공 시 응답 반환
            return response;
        } catch (Exception e) {
            // 오류 발생 시 500 Internal Server Error와 함께 메시지 반환
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"API 호출 중 오류가 발생했습니다.");
        }
    }

    // 채팅방 목록 조회
    @GetMapping("/getAllChatRoom")
    public ResponseEntity<CustomAPIResponse<?>> getAllChatRoom(){
        ResponseEntity<CustomAPIResponse<?>> response=chatRoomService.getAllChatRooms();
        return response;
    }

    // 채팅방 대화 내역 상세 조회
    @GetMapping("/getChat")
    public ResponseEntity<CustomAPIResponse<?>> getChat(@RequestParam Long chatRoomId){
        ResponseEntity<CustomAPIResponse<?>> response=chatRoomService.getChat(chatRoomId);
        return response;
    }

    // 키워드로 채팅방 검색
    @GetMapping("/getChatRoomByKeyword")
    public ResponseEntity<CustomAPIResponse<?>> getChatRoomByKeyword(@RequestParam String keyword){
        ResponseEntity<CustomAPIResponse<?>> response=chatRoomService.getChatRoomByKeyword(keyword);
        return response;
    }
}

