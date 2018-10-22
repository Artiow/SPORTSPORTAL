package ru.vldf.sportsportal.controller.advice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.vldf.sportsportal.config.SwaggerConfig;
import ru.vldf.sportsportal.config.messages.MessageContainer;
import ru.vldf.sportsportal.service.generic.ForbiddenAccessException;
import ru.vldf.sportsportal.service.generic.HandlerNotFoundException;
import ru.vldf.sportsportal.service.generic.UnauthorizedAccessException;
import springfox.documentation.annotations.ApiIgnore;
import springfox.documentation.service.ApiInfo;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import java.util.Locale;
import java.util.Optional;

@ApiIgnore
@Controller
public class SupportingController implements ErrorController {

    private SwaggerConfig swaggerConfig;
    private MessageContainer messages;

    @Autowired
    public void setSwaggerConfig(SwaggerConfig swaggerConfig) {
        this.swaggerConfig = swaggerConfig;
    }

    @Autowired
    public void setMessages(MessageContainer messages) {
        this.messages = messages;
    }

    /**
     * Returns information about api and database version.
     *
     * @return object {@link Object} with api info
     */
    @ResponseBody
    @GetMapping("/info")
    public Object getAppInfo() {
        return new Object() {

            ApiInfo apiInfo = swaggerConfig.apiInfo();
            Locale locale = messages.getLocale();

            public ApiInfo getApiInfo() {
                return apiInfo;
            }

            public Locale getLocale() {
                return locale;
            }
        };
    }

    /**
     * Returns CSRF state message.
     *
     * @return object {@link Object} with message
     */
    @ResponseBody
    @GetMapping("/csrf")
    public Object toCsrf() {
        return new Object() {

            private String message = "CSRF protection is disabled as unnecessary";

            public String getMessage() {
                return message;
            }
        };
    }

    /**
     * Returns redirect to swagger page from associated paths.
     *
     * @return redirect to swagger page
     */
    @GetMapping({"/", "/swagger", "/swagger/", "/swagger-ui", "/swagger-ui/", "/swagger-ui.html/"})
    public String toSwagger() {
        return "redirect:/swagger-ui.html";
    }

    /**
     * Unexpected error handler.
     *
     * @param request {@link HttpServletRequest} that contains error status
     * @throws Exception that corresponds to an error
     */
    @GetMapping("/error")
    public void handleError(HttpServletRequest request) throws Exception {
        HttpStatus status = Optional.ofNullable(request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE))
                .map(Object::toString)
                .map(Integer::valueOf)
                .map(HttpStatus::resolve)
                .orElse(HttpStatus.INTERNAL_SERVER_ERROR);
        String message = String.format("%s. Error request uri: %s", status.getReasonPhrase(),
                Optional.ofNullable(request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI)).map(Object::toString).orElse("undefined")
        );
        switch (status) {
            case NOT_FOUND:
                throw new HandlerNotFoundException(message);
            case UNAUTHORIZED:
                throw new UnauthorizedAccessException(message);
            case FORBIDDEN:
                throw new ForbiddenAccessException(message);
            default:
                throw new Exception(message);
        }
    }

    /**
     * Returns error api path.
     *
     * @return {@link String} error path
     */
    @Override
    public String getErrorPath() {
        return "/error";
    }
}