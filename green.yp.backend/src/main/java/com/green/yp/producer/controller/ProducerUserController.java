package com.green.yp.producer.controller;

import com.green.yp.api.apitype.producer.ProducerCredentialsResponse;
import com.green.yp.api.apitype.producer.ProducerUserResponse;
import com.green.yp.api.apitype.producer.UserCredentialsRequest;
import com.green.yp.common.controller.BaseRestController;
import com.green.yp.common.dto.ResponseApi;
import com.green.yp.producer.service.ProducerUserService;
import com.green.yp.util.RequestUtil;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.UUID;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@Validated
@RequestMapping("producer")
public class ProducerUserController extends BaseRestController {

    private final ProducerUserService userService;

    public ProducerUserController(ProducerUserService userService) {
        this.userService = userService;
    }

    @PostMapping(path = "/{producerId}/authorize/user",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseApi<ProducerCredentialsResponse> createAuthorizedUser(@NotNull @NonNull @PathVariable("producerId") UUID producerId,
                                                                         @NotNull @NonNull @Valid @RequestBody UserCredentialsRequest credentialsRequest)
            throws NoSuchAlgorithmException {
        return new ResponseApi<>(userService.createUserCredentials(credentialsRequest,
                null,
                producerId,
                null,
                RequestUtil.getRequestIP()), null);
    }

    @GetMapping(path = "/{producerId}/search/users",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseApi<List<ProducerUserResponse>> findProducerUsers(@PathVariable UUID producerId,
                                                                     @RequestParam("firstName") String firstName,
                                                                     @RequestParam("lastName") String lastName) {
        log.info("Search producerUsers by firstname : {} and lastName: {}", firstName, lastName);
        return userService.findUsers(producerId, firstName, lastName);
    }

}
