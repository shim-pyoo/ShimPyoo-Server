package com.hsu.shimpyoo.domain.chatbot.service;

import com.hsu.shimpyoo.domain.chatbot.entity.Chat;
import com.hsu.shimpyoo.domain.chatbot.entity.ChatRoom;
import com.hsu.shimpyoo.domain.chatbot.repository.ChatRepository;
import com.hsu.shimpyoo.domain.chatbot.repository.ChatRoomRepository;
import com.hsu.shimpyoo.domain.chatbot.web.dto.ChatListDto;
import com.hsu.shimpyoo.domain.chatbot.web.dto.ChatRoomListDto;
import com.hsu.shimpyoo.domain.chatbot.web.dto.ModifyChatRoomTitleDto;
import com.hsu.shimpyoo.domain.user.entity.User;
import com.hsu.shimpyoo.domain.user.repository.UserRepository;
import com.hsu.shimpyoo.global.response.CustomAPIResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatRoomServiceImpl implements ChatRoomService {
    private final ChatRepository chatRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;

    // 채팅방 생성
    @Override
    public ResponseEntity<CustomAPIResponse<?>> makeChatRoom(){
        // 현재 인증된 사용자의 로그인 아이디를 가져옴 (getName은 loginId를 가져오는 것)
        String loginId= SecurityContextHolder.getContext().getAuthentication().getName();

        // 사용자 존재 여부 확인
        Optional<User> isExistUser=userRepository.findByLoginId(loginId);
        if(isExistUser.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"존재하지 않는 사용자입니다.");
        }

        ChatRoom chatRoom = ChatRoom.
                builder()
                .chatTitle("채팅방 제목")
                .userId(isExistUser.get())
                .build();

        chatRoomRepository.save(chatRoom);

        ChatRoomListDto chatRoomResponseDto=new ChatRoomListDto();
        chatRoomResponseDto.setChatRoomId(chatRoom.getChatRoomId());
        chatRoomResponseDto.setChatRoomTitle(chatRoom.getChatTitle());


        CustomAPIResponse<Object> res=CustomAPIResponse.createSuccess(200, chatRoomResponseDto, "채팅방이 생성되었습니다.");
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    // 채팅방 제목 수정
    @Override
    public ResponseEntity<CustomAPIResponse<?>> modifyChatRoomTitle(ModifyChatRoomTitleDto requestDto) {
        // 현재 인증된 사용자의 로그인 아이디를 가져옴 (getName은 loginId를 가져오는 것)
        String loginId= SecurityContextHolder.getContext().getAuthentication().getName();

        // 사용자 존재 여부 확인
        Optional<User> isExistUser=userRepository.findByLoginId(loginId);
        if(isExistUser.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"존재하지 않는 사용자입니다.");
        }

        Optional<ChatRoom> isExistChatRoom = chatRoomRepository.findById(requestDto.getChatRoomId());

        if(isExistChatRoom.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"존재하지 않는 채팅방입니다.");
        }

        // 채팅방이 현재 로그인한 사용자의 채팅방이 아니라면
        if(isExistChatRoom.get().getUserId()!=isExistUser.get()){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"채팅방 제목 수정 권한이 없습니다.");
        }

        isExistChatRoom.get().setChatTitle(requestDto.getTitle());
        chatRoomRepository.save(isExistChatRoom.get());

        CustomAPIResponse<Object> res = CustomAPIResponse.createSuccess(200, null ,
                "채팅방 제목이 수정되었습니다.");
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    // 모든 채팅방 목록 조회
    @Override
    public ResponseEntity<CustomAPIResponse<?>> getAllChatRooms() {
        // 현재 인증된 사용자의 로그인 아이디를 가져옴 (getName은 loginId를 가져오는 것)
        String loginId= SecurityContextHolder.getContext().getAuthentication().getName();

        // 사용자 존재 여부 확인
        Optional<User> isExistUser=userRepository.findByLoginId(loginId);
        if(isExistUser.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"존재하지 않는 사용자입니다.");
        }

        // 사용자 아이디로 채팅방 검색
        List<ChatRoom> chatRoomList= chatRoomRepository.findChatRoomByUserId(isExistUser.get());

        // 채팅방 정보 목록을 담을 리스트 생성
        List<ChatRoomListDto> chatRoomListDtos = new ArrayList<>();

        // 각 채팅방에 대해 마지막 메시지 조회
        for (ChatRoom chatRoom : chatRoomList) {
            // 마지막 메시지를 ChatRepository를 통해 조회
            Optional<Chat> lastChat = chatRepository.findTopByUserIdAndChatRoomIdOrderByCreatedAtDesc(isExistUser.get(), chatRoom);

            // dto의 마지막 메시지와 마지막 시간 정보 추가
            String lastChatMessage = lastChat.map(Chat::getContent).orElse("메시지가 존재하지 않습니다.");
            String lastChatAt = lastChat.map(chat -> chat.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm")))
                    .orElse("시간이 존재하지 않습니다.");

            // ChatRoomListDto에 값을 채워서 리스트에 추가
            ChatRoomListDto chatRoomListDto = new ChatRoomListDto(
                    chatRoom.getChatRoomId(),  // 채팅방 ID
                    chatRoom.getChatTitle(),  // 채팅방 제목
                    lastChatMessage,  // 마지막 메시지
                    lastChatAt  // 마지막 대화 시간
            );

            chatRoomListDtos.add(chatRoomListDto);
        }

        CustomAPIResponse<List<ChatRoomListDto>> response = CustomAPIResponse.createSuccess(200, chatRoomListDtos, "채팅방 목록 조회에 성공하였습니다.");
        return ResponseEntity.ok(response);

    }

    @Override
    public ResponseEntity<CustomAPIResponse<?>> getChat(Long chatRoomId) {
        // 현재 인증된 사용자의 로그인 아이디를 가져옴 (getName은 loginId를 가져오는 것)
        String loginId= SecurityContextHolder.getContext().getAuthentication().getName();

        // 사용자 존재 여부 확인
        Optional<User> isExistUser=userRepository.findByLoginId(loginId);
        if(isExistUser.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"존재하지 않는 사용자입니다.");
        }

        Optional<ChatRoom> isExistChatRoom = chatRoomRepository.findById(chatRoomId);

        if(isExistChatRoom.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"존재하지 않는 채팅방입니다.");
        }

        // 채팅방이 현재 로그인한 사용자의 채팅방이 아니라면
        if(isExistChatRoom.get().getUserId()!=isExistUser.get()){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"채팅방 조회 권한이 없습니다.");
        }

        List<Chat> chatList=chatRepository.findChatByUserIdAndChatRoomId(isExistUser.get(),isExistChatRoom.get());

        // 대화 내역 dto를 담을 배열 생성
        List<ChatListDto> chatListDtos = new ArrayList<>();

        for (Chat chat : chatList) {
            ChatListDto chatListDto = new ChatListDto(
              chat.getContent(),
              chat.getIsSend()
            );
            chatListDtos.add(chatListDto);
        }

        CustomAPIResponse<List<ChatListDto>> response = CustomAPIResponse.createSuccess(200, chatListDtos, "대화 내역 조회에 성공하였습니다.");
        return ResponseEntity.ok(response);
    }


    // 채팅방 검색 기능
    @Override
    public ResponseEntity<CustomAPIResponse<?>> getChatRoomByKeyword(String keyword) {
        // 현재 인증된 사용자의 로그인 아이디를 가져옴 (getName은 loginId를 가져오는 것)
        String loginId= SecurityContextHolder.getContext().getAuthentication().getName();

        // 사용자 존재 여부 확인
        Optional<User> isExistUser=userRepository.findByLoginId(loginId);
        if(isExistUser.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"존재하지 않는 사용자입니다.");
        }

        // 사용자 아이디와 키워드로 채팅방 검색
        List<ChatRoom> chatRoomList= chatRoomRepository.findChatRoomByChatTitleContainingAndUserId(keyword, isExistUser.get());

        // 채팅방 정보 목록을 담을 리스트 생성
        List<ChatRoomListDto> chatRoomListDtos = new ArrayList<>();

        // 각 채팅방에 대해 마지막 메시지 조회
        for (ChatRoom chatRoom : chatRoomList) {
            // 마지막 메시지를 ChatRepository를 통해 조회
            Optional<Chat> lastChat = chatRepository.findTopByUserIdAndChatRoomIdOrderByCreatedAtDesc(isExistUser.get(), chatRoom);

            // dto의 마지막 메시지와 마지막 시간 정보 추가
            String lastChatMessage = lastChat.map(Chat::getContent).orElse("메시지가 존재하지 않습니다.");
            String lastChatAt = lastChat.map(chat -> chat.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm")))
                    .orElse("시간이 존재하지 않습니다.");

            // ChatRoomListDto에 값을 채워서 리스트에 추가
            ChatRoomListDto chatRoomListDto = new ChatRoomListDto(
                    chatRoom.getChatRoomId(),  // 채팅방 ID
                    chatRoom.getChatTitle(),  // 채팅방 제목
                    lastChatMessage,  // 마지막 메시지
                    lastChatAt  // 마지막 대화 시간
            );

            chatRoomListDtos.add(chatRoomListDto);
        }

        CustomAPIResponse<List<ChatRoomListDto>> response = CustomAPIResponse.createSuccess(200, chatRoomListDtos, "채팅방 목록 조회에 성공하였습니다.");
        return ResponseEntity.ok(response);
    }
}
