package ru.vldf.sportsportal.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;
import ru.vldf.sportsportal.config.messages.MessageContainer;
import ru.vldf.sportsportal.config.security.components.AbstractTokenAuthenticationFilter;
import ru.vldf.sportsportal.config.security.components.AbstractTokenAuthenticationProvider;
import ru.vldf.sportsportal.config.security.components.basic.BasicAuthenticationFilter;
import ru.vldf.sportsportal.config.security.components.basic.BasicAuthenticationProvider;
import ru.vldf.sportsportal.config.security.components.jwt.access.AccessAuthenticationFilter;
import ru.vldf.sportsportal.config.security.components.jwt.access.AccessAuthenticationProvider;
import ru.vldf.sportsportal.config.security.components.jwt.refresh.RefreshAuthenticationFilter;
import ru.vldf.sportsportal.config.security.components.jwt.refresh.RefreshAuthenticationProvider;
import ru.vldf.sportsportal.config.security.routing.RightsDifferentiationRouter;
import ru.vldf.sportsportal.controller.advice.AdviseController;
import ru.vldf.sportsportal.service.security.AuthorizationProvider;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @author Namednev Artem
 */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final RightsDifferentiationRouter router;
    private final AuthorizationProvider authorizationProvider;

    private final AdviseController adviseController;
    private final MessageContainer messages;


    @Autowired
    public SecurityConfig(
            RightsDifferentiationRouter router,
            AuthorizationProvider authorizationProvider,
            AdviseController adviseController,
            MessageContainer messages
    ) {
        super();
        this.router = router;
        this.authorizationProvider = authorizationProvider;
        this.adviseController = adviseController;
        this.messages = messages;
    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(basicAuthenticationProvider());
        auth.authenticationProvider(accessAuthenticationProvider());
        auth.authenticationProvider(refreshAuthenticationProvider());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .addFilterBefore(basicAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(accessAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(refreshAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)

                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .exceptionHandling().accessDeniedHandler(accessDeniedHandler()).and()

                .httpBasic().disable()
                .formLogin().disable()
                .logout().disable()
                .csrf().disable()
                .cors();

        ExpressionUrlAuthorizationConfigurer.ExpressionInterceptUrlRegistry authorizeRequests = http.authorizeRequests();
        for (Map.Entry<String, RequestMatcher> entry : router.getSecurityRouteMap().entrySet()) {
            ((ExpressionUrlAuthorizationConfigurer.AuthorizedUrl) authorizeRequests.requestMatchers(entry.getValue())).hasRole(entry.getKey());
        }
    }


    @Bean
    public BasicAuthenticationProvider basicAuthenticationProvider() {
        return configureProvider(new BasicAuthenticationProvider());
    }

    @Bean
    public AccessAuthenticationProvider accessAuthenticationProvider() {
        return configureProvider(new AccessAuthenticationProvider());
    }

    @Bean
    public RefreshAuthenticationProvider refreshAuthenticationProvider() {
        return configureProvider(new RefreshAuthenticationProvider());
    }

    private <T extends AbstractTokenAuthenticationProvider> T configureProvider(T provider) {
        provider.setAuthorizationProvider(authorizationProvider);
        provider.setMessageContainer(messages);
        return provider;
    }


    @Bean
    public BasicAuthenticationFilter basicAuthenticationFilter() throws Exception {
        return configureFilter(new BasicAuthenticationFilter(router.getLoginPathRequestMatcher()));
    }

    @Bean
    public AccessAuthenticationFilter accessAuthenticationFilter() throws Exception {
        return configureFilter(new AccessAuthenticationFilter(router.getProtectedPathsRequestMatcher()));
    }

    @Bean
    public RefreshAuthenticationFilter refreshAuthenticationFilter() throws Exception {
        return configureFilter(new RefreshAuthenticationFilter(router.getRefreshPathRequestMatcher()));
    }

    private <T extends AbstractTokenAuthenticationFilter> T configureFilter(T filter) throws Exception {
        filter.setAuthenticationManager(authenticationManager());
        filter.setAuthenticationSuccessHandler(successHandler());
        filter.setAuthenticationFailureHandler(failureHandler());
        filter.setMessageContainer(messages);
        return filter;
    }


    @Bean
    public AuthenticationSuccessHandler successHandler() {
        final SimpleUrlAuthenticationSuccessHandler successHandler = new SimpleUrlAuthenticationSuccessHandler();
        successHandler.setRedirectStrategy((httpServletRequest, httpServletResponse, s) -> {
            // no redirect
        });
        return successHandler;
    }

    @Bean
    public AuthenticationFailureHandler failureHandler() {
        return (request, response, ex) -> {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
            ServletOutputStream out = response.getOutputStream();
            new ObjectMapper().writeValue(out, adviseController.warnDTO(ex, "Unauthorized access attempt"));
            out.flush();
        };
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, ex) -> {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
            ServletOutputStream out = response.getOutputStream();
            new ObjectMapper().writeValue(out, adviseController.warnDTO(ex, "Forbidden access attempt"));
            out.flush();
        };
    }
}
