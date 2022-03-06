package com.example.jwt_service.controller;

import com.example.jwt_service.model.User;
import com.example.jwt_service.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.JsonPath;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class RestApiControllerTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Test
    public void 권한_체크하지않는_URL_호출() throws Exception {
        //when
        mockMvc.perform(
                get("/home")
        )
        .andDo(print())
        .andExpect(status().isOk())
        ;
    }

    @Test
    public void post_authorization_header_없는경우() throws Exception {
        //when
        mockMvc.perform(
                post("/token")
        )
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().string("token must be \"test\"\n"))
        ;
    }

    @Test
    public void post_authorization_header_있는경우() throws Exception {
        //when
        mockMvc.perform(
                post("/token")
                .header(HttpHeaders.AUTHORIZATION, "test")
        )
        .andDo(print())
        .andExpect(status().isOk())
        ;
    }

    @Test
    public void login_filter_authorization_없을때_403에러() throws Exception {
        //given
        User user = User.builder()
                .name("name1")
                .password("1234")
                .roles("ROLE_USER")
                .build();
        userRepository.save(user);

        //when
        mockMvc.perform(
                post("/login")
                //.header(HttpHeaders.AUTHORIZATION, "test")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user))
        )
        .andDo(print())
        .andExpect(status().isForbidden())
        ;
    }

}