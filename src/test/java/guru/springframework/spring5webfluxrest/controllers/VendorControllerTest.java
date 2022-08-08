package guru.springframework.spring5webfluxrest.controllers;

import guru.springframework.spring5webfluxrest.domain.Vendor;
import guru.springframework.spring5webfluxrest.repositories.VendorRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.reactivestreams.Publisher;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

public class VendorControllerTest {

    WebTestClient webTestClient;
    VendorRepository vendorRepository;
    VendorController vendorController;


    @Before
    public void setUp() {
        vendorRepository = Mockito.mock(VendorRepository.class);
        vendorController = new VendorController(vendorRepository);
        webTestClient = WebTestClient.bindToController(vendorController).build();
    }

    @Test
    public void list() {
        given(vendorRepository.findAll())
                .willReturn(Flux.just(
                        Vendor.builder().firstName("Fred").lastName("Flinstone").build(),
                        Vendor.builder().firstName("Barney").lastName("Rubble").build()
                ));

        webTestClient.get()
                .uri("/api/v1/vendors/")
                .exchange()
                .expectBodyList(Vendor.class)
                .hasSize(2);
    }

    @Test
    public void getById() {
        given(vendorRepository.findById(any(String.class)))
                .willReturn(Mono.just(
                        Vendor.builder().firstName("Jimmy").lastName("John").build()));

        webTestClient.get()
                .uri("/api/v1/vendors/someid")
                .exchange()
                .expectBody(Vendor.class);
    }

    @Test
    public void testCreateVendor() {
        given(vendorRepository.saveAll(any(Publisher.class)))
                .willReturn(Flux.just(
                        Vendor.builder().firstName("firstname").lastName("lastname").build()));

        Mono<Vendor> vendorToSaveMono = Mono.just(
                Vendor.builder().firstName("firstname").lastName("lastname").build());

        webTestClient.post()
                .uri("/api/v1/vendors/")
                .body(vendorToSaveMono, Vendor.class)
                .exchange()
                .expectStatus().isCreated();
    }

    @Test
    public void testUpdate() {
        given(vendorRepository.save(any(Vendor.class)))
                .willReturn(Mono.just(
                        Vendor.builder().firstName("firstname").lastName("lastname").build()));

        Mono<Vendor> vendorToSaveMono = Mono.just(
                Vendor.builder().firstName("firstname").lastName("lastname").build());

        webTestClient.put()
                .uri("/api/v1/vendors/someid")
                .body(vendorToSaveMono, Vendor.class)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    public void testPatchWithChanges() {
        given(vendorRepository.findById(anyString()))
                .willReturn(Mono.just(
                        Vendor.builder().firstName("firstname").lastName("lastname").build()));
        given(vendorRepository.save(any(Vendor.class)))
                .willReturn(Mono.just(
                        Vendor.builder().build()));

        Mono<Vendor> vendorToSaveMono = Mono.just(
                Vendor.builder().firstName("firstname 2").lastName("lastname 2").build());

        webTestClient.patch()
                .uri("/api/v1/vendors/someid")
                .body(vendorToSaveMono, Vendor.class)
                .exchange()
                .expectStatus().isOk();

        verify(vendorRepository).save(any());
    }

    @Test
    public void testPatchWithNoChanges() {
        given(vendorRepository.findById(anyString()))
                .willReturn(Mono.just(
                        Vendor.builder().firstName("firstname1").build()));
        given(vendorRepository.save(any(Vendor.class)))
                .willReturn(Mono.just(
                        Vendor.builder().build()));

        Mono<Vendor> vendorToSaveMono = Mono.just(
                Vendor.builder().firstName("firstname1").build());

        webTestClient.patch()
                .uri("/api/v1/vendors/someid")
                .body(vendorToSaveMono, Vendor.class)
                .exchange()
                .expectStatus().isOk();

        verify(vendorRepository, never()).save(any());
    }
}