package org.example.itemtrade.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.itemtrade.domain.ChatMessage;
import org.example.itemtrade.domain.ChatRoom;
import org.example.itemtrade.domain.ItemPost;
import org.example.itemtrade.domain.Member;
import org.example.itemtrade.dto.ChatRoomDto;
import org.example.itemtrade.enums.TradeStatus;
import org.example.itemtrade.repository.ChatMessageRepository;
import org.example.itemtrade.repository.ChatRoomRepository;
import org.example.itemtrade.repository.ItemPostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class ChatroomService {

  private final ChatRoomRepository chatroomRepository;
  private final ItemPostRepository itemPostRepository;
  private final ChatMessageRepository chatMessageRepository;
  private final MemberService memberService;

  // 채팅방 생성
  public ChatRoom createChatRoom(Member buyer, Long postId) {

    ItemPost post = itemPostRepository.findById(postId).orElseThrow(() ->
        new IllegalArgumentException("게시글을 찾을 수 없습니다.")); // 게시글이 존재하는지 확인

    Member seller = post.getSeller(); // 판매자 정보 가져오기

    return chatroomRepository.findByItemPostAndBuyerAndSeller(post, buyer, seller)
        .orElseGet(() -> {
          ChatRoom chatRoom = ChatRoom.of(buyer, seller, post);
          chatRoom.setTradeStatus(TradeStatus.TRADE);
          return chatroomRepository.save(chatRoom);
        });
  }

  // 채팅방 목록
  public List<ChatRoomDto> getChatRoomsForMember(Member member) {
    if (member == null) {
      throw new IllegalStateException("로그인한 사용자만 채팅 목록을 조회할 수 있습니다.");
    }
//    List<ChatRoom> asBuyer = chatroomRepository.findAllByBuyerAndDeletedByBuyerFalse(member);
//    List<ChatRoom> asSeller = chatroomRepository.findAllBySellerAndDeletedBySellerFalse(member);
    List<ChatRoom> chatRooms = chatroomRepository.findAllByMemberOrderByLastMessage(member);

    // 구매자 채팅방과 판매자 채팅방을 합쳐서 하나의 리스트로 반환
    return chatRooms.stream()
        .distinct()
        .filter(room -> {
          Member opponent = room.getOpponent(member);
          // 차단된 회원의 채팅방은 제외
          return !memberService.isMemberBlocked(member, opponent) // 내가 상대를 차단 안함
          && !memberService.isMemberBlocked(opponent, member); // 상대가 나를 차단 안함
        })
        .map(room -> getListChatRoom(member, room))
        .toList();
  }

  // 메소드 분리
  private ChatRoomDto getListChatRoom(Member currentUser, ChatRoom room) {
    Member opponent = room.getOpponent(currentUser);
    boolean isBlocked = memberService.isMemberBlocked(currentUser, opponent);

    ChatMessage lastMessage = chatMessageRepository.findTopByChatRoomIdOrderByCreatedAtDesc(room.getId()).orElse(null);
    String lastContent = (lastMessage != null) ? lastMessage.getContent() : "";
    Long unreadCount = chatMessageRepository.countByChatRoomAndSenderNotAndIsReadFalse(room, currentUser);

    return ChatRoomDto.from(room, unreadCount, lastContent,opponent.getId() ,isBlocked);
  }


  // 단일 채팅방 조회
  public ChatRoomDto getChatRoomById(Long id, Member member) {
    return chatroomRepository.findById(id)
        .map(room -> {
          return getListChatRoom(member, room);
        })
        .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다."));
  }

  // 채팅방 삭제
  public void deleteChatRoom(Long id, Member user) {
    // 채팅방이 존재하는지 확인
    ChatRoom chatRoom = chatroomRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다."));

    boolean isUser = chatRoom.getBuyer().getId().equals(user.getId()) || chatRoom.getSeller().getId().equals(user.getId());

    if(!isUser) {
      throw new IllegalArgumentException("채팅방 삭제 권한이 없습니다.");
    }
    chatRoom.deleted(user);
    chatroomRepository.save(chatRoom);
  }

  // 거래 완료
  public void completeTrade(Long id, Member user) {
    // 채팅방이 존재하는지 확인
    ChatRoom chatRoom = chatroomRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다."));

    // 거래 완료 권한 체크
    if(user.equals(chatRoom.getBuyer())) {
      chatRoom.setTradeBuyerComplete(true);
    } else if (user.equals(chatRoom.getSeller())) {
      chatRoom.setTradeSellerComplete(true);
    } else {
      throw new IllegalArgumentException("거래 완료 권한이 없습니다.");
    }

    // 둘 다 true일 경우 완료 처리
    if(chatRoom.isTradeSellerComplete() && chatRoom.isTradeBuyerComplete()) {
      chatRoom.setTradeStatus(TradeStatus.COMPLETE);
      chatRoom.getItemPost().setStatus(TradeStatus.COMPLETE);
      chatRoom.getItemPost().setSold(true);
    }
  }

  // 거래 취소
  public void cancelTrade(Long id, Member user) {
    // 채팅방이 존재하는지 확인
    ChatRoom chatRoom = chatroomRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다."));

    // 취소 권한 체크
    if(!chatRoom.getBuyer().equals(user) && !chatRoom.getSeller().equals(user)) {
      throw new IllegalArgumentException("권한이 없습니다.");
    }

    if (chatRoom.getTradeStatus() == TradeStatus.CANCEL){
      throw new IllegalArgumentException("이미 거래가 취소되었습니다.");
    }
    chatRoom.setTradeStatus(TradeStatus.CANCEL);

    ItemPost post = chatRoom.getItemPost();
    post.setStatus(TradeStatus.CANCEL);

  }

  // 거래 재시도
  public void reopenTrade(Long id, Member user) {
    // 채팅방이 존재하는지 확인
    ChatRoom chatRoom = chatroomRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다."));

    // 취소 권한 체크
    if(!chatRoom.getBuyer().equals(user) && !chatRoom.getSeller().equals(user)) {
      throw new IllegalArgumentException("권한이 없습니다.");
    }

    if (chatRoom.getTradeStatus() != TradeStatus.CANCEL){
      throw new IllegalArgumentException("취소된 거래만 재거래가 가능합니다.");
    }
    chatRoom.setTradeStatus(TradeStatus.TRADE);

    ItemPost post = chatRoom.getItemPost();
    post.setStatus(TradeStatus.TRADE);
    chatRoom.setTradeSellerComplete(false);
    chatRoom.setTradeBuyerComplete(false);

  }
}
