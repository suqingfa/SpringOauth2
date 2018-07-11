package application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;

import java.util.Arrays;
import java.util.Collections;

@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter
{
    @Autowired
    private AuthenticationManager authenticationManager;

    @Bean
    public ClientDetailsService clientDetailsService(PasswordEncoder passwordEncoder)
    {
        return clientId ->
        {
            if ("client1".equals(clientId))
            {
                BaseClientDetails clientDetails = new BaseClientDetails();
                clientDetails.setClientId(clientId);
                clientDetails.setAuthorizedGrantTypes(Arrays.asList("password", "refresh_token"));
                clientDetails.setScope(Collections.singletonList("all"));
                clientDetails.setClientSecret(passwordEncoder.encode("123456"));
                return clientDetails;
            }

            if ("client2".equals(clientId))
            {
                BaseClientDetails clientDetails = new BaseClientDetails();
                clientDetails.setClientId(clientId);
                clientDetails.setAuthorizedGrantTypes(Arrays.asList("client_credentials", "refresh_token"));
                clientDetails.setScope(Collections.singletonList("all"));
                clientDetails.setClientSecret(passwordEncoder.encode("123456"));
                return clientDetails;
            }

            if ("client3".equals(clientId))
            {
                BaseClientDetails clientDetails = new BaseClientDetails();
                clientDetails.setClientId(clientId);
                clientDetails.setAuthorizedGrantTypes(Arrays.asList("authorization_code", "refresh_token"));
                clientDetails.setScope(Collections.singletonList("all"));
                clientDetails.setClientSecret(passwordEncoder.encode("123456"));
                clientDetails.setRegisteredRedirectUri(Collections.singleton("http://localhost:8082/oauth"));
                return clientDetails;
            }

            throw new ClientRegistrationException("invalid client detail");
        };
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception
    {
        endpoints.tokenStore(new InMemoryTokenStore())
                .authenticationManager(authenticationManager);
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception
    {
        oauthServer.allowFormAuthenticationForClients()
                .tokenKeyAccess("permitAll()")
                .checkTokenAccess("isAuthenticated()");
    }
}