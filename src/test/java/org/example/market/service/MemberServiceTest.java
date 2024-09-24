package org.example.market.service;

import org.example.market.controller.dto.RegisterRequest;
import org.example.market.domain.Member;
import org.example.market.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.example.market.domain.Member.*;
import static org.example.market.domain.Member.Role.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MemberServiceTest {
    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private MemberService memberService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);  // Mockito 초기화
    }

    @Test
    @DisplayName("회원가입")
    void saveMemberTest() {
        // given
        RegisterRequest registerRequest = new RegisterRequest("newUser", "password123", BUYER);
        Member expectedMember = new Member(1L, "newUser", "password123", BUYER);

        when(memberRepository.save(any(Member.class))).thenReturn(expectedMember);

        // when
        Member savedMember = memberService.save(registerRequest);

        // then
        assertNotNull(savedMember);
        assertEquals("newUser", savedMember.getUsername());
        assertEquals("password123", savedMember.getPassword());
        assertEquals(BUYER, savedMember.getRole());
        verify(memberRepository, times(1)).save(any(Member.class));
    }

    @Test
    @DisplayName("로그인 성공")
    void findByUsernameTest() {
        // given
        String username = "existingUser";
        Member expectedMember = new Member(1L, username, "password123", SELLER);
        when(memberRepository.findByUsername(username)).thenReturn(Optional.of(expectedMember));

        // when
        Optional<Member> memberOptional = memberService.findByUsername(username);

        // then
        assertTrue(memberOptional.isPresent());
        assertEquals(expectedMember.getUsername(), memberOptional.get().getUsername());
        assertEquals(expectedMember.getPassword(), memberOptional.get().getPassword());
        verify(memberRepository, times(1)).findByUsername(username);
    }

    @Test
    @DisplayName("로그인 실패")
    void findByUsernameNotFoundTest() {
        // given
        String username = "nonExistingUser";
        when(memberRepository.findByUsername(username)).thenReturn(Optional.empty());

        // when
        Optional<Member> memberOptional = memberService.findByUsername(username);

        // then
        assertFalse(memberOptional.isPresent());
        verify(memberRepository, times(1)).findByUsername(username);
    }

    @Test
    @DisplayName("loadUserByUsername 성공")
    void loadUserByUsernameTest() {
        // given
        String username = "user123";
        Member member = new Member(1L, username, "password123", BUYER);
        when(memberRepository.findByUsername(username)).thenReturn(Optional.of(member));

        // when
        UserDetails userDetails = memberService.loadUserByUsername(username);

        // then
        assertNotNull(userDetails);
        assertEquals(member.getUsername(), userDetails.getUsername());
        verify(memberRepository, times(1)).findByUsername(username);
    }

    @Test
    @DisplayName("loadUserByUsername 실패")
    void loadUserByUsernameNotFoundTest() {
        // given
        String username = "nonExistingUser";
        when(memberRepository.findByUsername(username)).thenReturn(Optional.empty());

        // when & then
        assertThrows(UsernameNotFoundException.class, () -> memberService.loadUserByUsername(username));
        verify(memberRepository, times(1)).findByUsername(username);
    }
}