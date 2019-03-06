package ru.vldf.sportsportal.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.vldf.sportsportal.dto.sectional.common.UserDTO;
import ru.vldf.sportsportal.dto.security.JwtPairDTO;
import ru.vldf.sportsportal.service.AuthService;
import ru.vldf.sportsportal.service.generic.ResourceCannotCreateException;
import ru.vldf.sportsportal.service.generic.ResourceCannotUpdateException;
import ru.vldf.sportsportal.service.generic.ResourceNotFoundException;
import ru.vldf.sportsportal.service.generic.UnauthorizedAccessException;

import javax.validation.constraints.NotBlank;

import static ru.vldf.sportsportal.util.ResourceLocationBuilder.buildURL;

/**
 * @author Namednev Artem
 */
@RestController
@Api(tags = {"Authentication"})
@RequestMapping("${api.path.common.auth}")
public class AuthController {

    private final AuthService authService;

    @Value("${api.path.common.user}")
    private String userPath;


    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }


    /**
     * Returns user token pair (access and refresh).
     *
     * @return token pair.
     * @throws UnauthorizedAccessException if user authorization is missing.
     */
    @GetMapping({"/login", "/refresh"})
    @ApiOperation("получить пару токенов")
    public JwtPairDTO login() throws UnauthorizedAccessException {
        return authService.login();
    }


    /**
     * Register new user and returns its location.
     *
     * @param userDTO the created user details.
     * @return created user location.
     * @throws ResourceCannotCreateException if user cannot be created.
     */
    @PostMapping("/register")
    @ApiOperation("регистрация")
    public ResponseEntity<Void> register(
            @RequestBody @Validated(UserDTO.CreateCheck.class) UserDTO userDTO
    ) throws ResourceCannotCreateException {
        return ResponseEntity.created(buildURL(userPath, authService.register(userDTO))).build();
    }

    /**
     * Initiate user confirmation and send confirmation email.
     *
     * @param id     the user identifier.
     * @param origin the confirmation link origin.
     * @return no content.
     * @throws ResourceNotFoundException     if user could not found.
     * @throws ResourceCannotUpdateException if could not sent email.
     */
    @PutMapping("/confirm/{id}")
    @ApiOperation("отправить письмо для подтверждения электронной почты")
    public ResponseEntity<Void> confirm(
            @PathVariable int id, @RequestParam(required = false) String origin
    ) throws ResourceNotFoundException, ResourceCannotUpdateException {
        authService.initConfirmation(id, origin);
        return ResponseEntity.noContent().build();
    }

    /**
     * Confirm user (by PUT method).
     *
     * @param token the user confirmation token.
     * @return no content.
     * @throws ResourceNotFoundException if user not found by confirm code.
     */
    @PutMapping("/confirm")
    @ApiOperation("подтвердить пользователя")
    public ResponseEntity<Void> putConfirm(
            @RequestBody @Validated @NotBlank String token
    ) throws ResourceNotFoundException {
        authService.confirm(token);
        return ResponseEntity.noContent().build();
    }

    /**
     * Confirm user (by GET method) and redirect to main page.
     *
     * @param token the user confirmation token.
     * @return redirect to main page.
     * @throws ResourceNotFoundException if user not found by confirm code.
     */
    @GetMapping("/confirm")
    @ApiOperation("подтвердить пользователя")
    public ResponseEntity<Void> getConfirm(
            @RequestParam @Validated @NotBlank String token
    ) throws ResourceNotFoundException {
        authService.confirm(token);
        return ResponseEntity.status(HttpStatus.SEE_OTHER).header(HttpHeaders.LOCATION, buildURL().toString()).build();
    }
}
