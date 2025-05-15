package com.example.demo.API;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@Tag(name = "Middleware", description = "Point d'entrée front vers les services Films et Sessions")
public class ApiGatewayController {

    private final WebClient webClient;

    private final String serviceLecture = "http://localhost:8080";
    private final String serviceEcriture = "http://localhost:8081";

    public ApiGatewayController(WebClient webClient) {
        this.webClient = webClient;
    }

    @GetMapping("/movies")
    @Operation(summary = "Récupérer les films", description = "Délègue au service de lecture")
    public Mono<ResponseEntity<String>> getFilms() {
        return webClient.get()
                .uri(serviceLecture + "/movies")
                .retrieve()
                .toEntity(String.class);
    }
    //get Movie by id
    @GetMapping("/movies/{id}")
    @Operation(summary = "Récupérer un film par ID", description = "Délègue au service de lecture")
    public Mono<ResponseEntity<String>> getFilm(@PathVariable Integer id) {
        return webClient.get()
                .uri(serviceLecture + "/movies/" + id)
                .retrieve()
                .toEntity(String.class);
    }

    //get Session
    @GetMapping("/session")
    @Operation(summary = "Récupérer les sessions", description = "Délègue au service de lecture")
    public Mono<ResponseEntity<String>> getSessions() {
        return webClient.get()
                .uri(serviceLecture + "/session")
                .retrieve()
                .toEntity(String.class);
    }
    //get Session by id
    @GetMapping("/session/{id}")
    @Operation(summary = "Récupérer une session par ID", description = "Délègue au service de lecture")
    public Mono<ResponseEntity<String>> getSessionById(@PathVariable String id) {
        return webClient.get()
                .uri(serviceLecture + "/session/" + id)
                .retrieve()
                .toEntity(String.class);
    }
    //get Session by MovieId
    @GetMapping("/session/movie/{id}")
    @Operation(summary = "Récupérer une session par ID de film", description = "Délègue au service de lecture")
    public Mono<ResponseEntity<String>> getSessionByMovieId(@PathVariable String id) {
        return webClient.get()
                .uri(serviceLecture + "/session/movie/" + id)
                .retrieve()
                .toEntity(String.class);
    }
    //get les movietheaters
    @GetMapping("/movietheaters")
    @Operation(summary = "Récupérer les salles de cinéma", description = "Délègue au service de lecture")
    public Mono<ResponseEntity<String>> getMovieTheaters() {
        return webClient.get()
                .uri(serviceLecture + "/movietheaters")
                .retrieve()
                .toEntity(String.class);
    }

    @GetMapping("/movies/search")
    @Operation(summary = "Rechercher un film", description = "Délègue au service de lecture")
    public Mono<ResponseEntity<String>> searchMovies(
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) Integer minDuration,
            @RequestParam(required = false) Integer maxDuration,
            @RequestParam(required = false) String minCreationDate,
            @RequestParam(required = false) String maxCreationDate,
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String sessionDate
    ) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder
                .fromHttpUrl(serviceLecture + "/movies/search");
        if (genre != null) uriBuilder.queryParam("genre", genre);
        if (minDuration != null) uriBuilder.queryParam("minDuration", minDuration);
        if (maxDuration != null) uriBuilder.queryParam("maxDuration", maxDuration);
        if (minCreationDate != null) uriBuilder.queryParam("minCreationDate", minCreationDate);
        if (maxCreationDate != null) uriBuilder.queryParam("maxCreationDate", maxCreationDate);
        if (searchTerm != null) uriBuilder.queryParam("searchTerm", searchTerm);
        if (city != null) uriBuilder.queryParam("city", city);
        if (sessionDate != null) uriBuilder.queryParam("sessionDate", sessionDate);

        WebClient.RequestHeadersSpec<?> request = webClient.get()
                .uri(uriBuilder.build().toUri());


        return request.retrieve().toEntity(String.class);
    }

    @GetMapping("/users")
    @Operation(summary = "Récupérer les utilisateurs", description = "Délègue au service de lecture")
    public Mono<ResponseEntity<String>> getUsers(
            @RequestHeader(value = "Authorization", required = false) String authorization) {

        WebClient.RequestHeadersSpec<?> request = webClient.get()
                .uri(serviceEcriture + "/users");

        if (authorization != null && !authorization.isEmpty()) {
            request = request.header("Authorization", authorization);
        }

        return request
                .retrieve()
                .toEntity(String.class);
    }


    @PostMapping("/session/add")
    @Operation(summary = "Créer une session", description = "Délègue au service d’écriture")
    public Mono<ResponseEntity<String>> createSession(
            @RequestBody Map<String, Object> session,
            @RequestHeader(value = "Authorization") String authorization) {

        WebClient.RequestHeadersSpec<?> request = webClient.post()
                .uri(serviceEcriture + "/session/add")
                .bodyValue(session);

        if (authorization != null && !authorization.isEmpty()) {
            request = request.header("Authorization", authorization);
        }

        return request
                .retrieve()
                .toEntity(String.class);
    }


    @PostMapping("/movies/add")
    @Operation(summary = "Créer un film", description = "Délègue au service d’écriture")
    public Mono<ResponseEntity<String>> createFilm(
            @RequestBody Map<String, Object> movie,
            @RequestHeader(value = "Authorization") String authorization) {
        WebClient.RequestHeadersSpec<?> request = webClient.post()
                .uri(serviceEcriture + "/movies/add")
                .bodyValue(movie);
        if (authorization != null && !authorization.isEmpty()) {
            request = request.header("Authorization", authorization);
        }

        return request
                .retrieve()
                .toEntity(String.class);
    }

    @DeleteMapping("/movies/delete/{id}")
    @Operation(summary = "Supprimer un film", description = "Délègue au service d’écriture")
    public Mono<ResponseEntity<String>> deleteFilm(
            @RequestHeader(value = "Authorization") String authorization,
            @PathVariable String id) {
        WebClient.RequestHeadersSpec<?> request = webClient.delete()
                .uri(serviceEcriture + "/movies/delete/" + id);
        if (authorization != null && !authorization.isEmpty()) {
            request = request.header("Authorization", authorization);
        }
        return request
                .retrieve()
                .toEntity(String.class);
    }



    @PutMapping("/movies/update/{id}")
    @Operation(summary = "Mettre à jour un film", description = "Délègue au service d’écriture")
    public Mono<ResponseEntity<String>> updateFilm(
            @PathVariable String id,
            @RequestBody Map<String, Object> movie,
            @RequestHeader(value = "Authorization") String authorization) {
        WebClient.RequestHeadersSpec<?> request = webClient.put()
                .uri(serviceEcriture + "/movies/update/" + id)
                .bodyValue(movie);
        if (authorization != null && !authorization.isEmpty()) {
            request = request.header("Authorization", authorization);
        }
        return request
                .retrieve()
                .toEntity(String.class);
    }




}