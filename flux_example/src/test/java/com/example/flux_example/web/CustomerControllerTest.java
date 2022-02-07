package com.example.flux_example.web;

import com.example.flux_example.domain.Customer;
import com.example.flux_example.domain.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataR2dbcTest
class CustomerControllerTest {

    @Autowired
    CustomerRepository customerRepository;

    @Test
    public void 한건찾기_테스트() throws Exception {
        Mono<Customer> customer = customerRepository.findById(1L);

    }

}