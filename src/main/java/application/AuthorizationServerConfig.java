package application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
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
    @Autowired
    private UserDetailsService userDetailsService;

    @Bean
    public ClientDetailsService clientDetailsService(PasswordEncoder passwordEncoder)
    {
        return clientId ->
        {
            /**
            * 密码模式
            * 用户将用户名和密码发送第三方，第三方得到登录信息后加上自己的client_id和client_secret发送给授权服务器请求授权码
            * POST /oauth/token?grant_type=password&scope=all&client_id=client1&client_secret=123456&username=user&password=123456
            * */
            if ("client1".equals(clientId))
            {
                BaseClientDetails clientDetails = new BaseClientDetails();
                clientDetails.setClientId(clientId);
                clientDetails.setAuthorizedGrantTypes(Arrays.asList("password", "refresh_token"));
                clientDetails.setScope(Collections.singletonList("all"));
                clientDetails.setClientSecret(passwordEncoder.encode("123456"));
                return clientDetails;
            }

            /**
            * 客户模式
            * 第三方将自己的client_id和client_secret发送给授权服务器请求授权码
            * 得到的授权码一般用于一些公开接口的授权，如: /oauth/check_token
            * POST /oauth/token?grant_type=client_credentials&scope=all&client_id=client2&client_secret=123456
            * */
            if ("client2".equals(clientId))
            {
                BaseClientDetails clientDetails = new BaseClientDetails();
                clientDetails.setClientId(clientId);
                clientDetails.setAuthorizedGrantTypes(Arrays.asList("client_credentials", "refresh_token"));
                clientDetails.setScope(Collections.singletonList("all"));
                clientDetails.setClientSecret(passwordEncoder.encode("123456"));
                return clientDetails;
            }

            /**
            * 授权码模式
            * 第三方引导用户访问授权服务器
            * GET /oauth/authorize?response_type=code&client_id=client3&redirect_uri=http://localhost:8082/oauth
            * 用户在授权页面登录并且同意授权
            * 授权服务器得到用户同意后，会重定向到RegisteredRedirectUri，并带上请求码code
            * 请求码code的有效期很短，并且只能使用一次
            * RedirectUri http://localhost:8082/oauth?code=4WWRSE
            * 第三方得到请求码后，加上自己的client_id和client_secret发送给授权服务器请求授权码
            * POST /oauth/token?grant_type=authorization_code&client_id=client3&client_secret=123456&code=4WWRSE
            * */
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

            /**
             * 简化模式
             * 在浏览器向认证服务器请求token
             * GET /oauth/authorize?response_type=token&client_id=client4&redirect_uri=http://localhost:8082/oauth
             * 用户登录(如果之前没有登陆的话)
             * 用户授权
             * 授权完直接跳转到redirectUri并在url中携带access_token
             * redirectUri http://localhost:8082/oauth#access_token=6169f3b5-afb3-4e82-a0e9-d59a7126258a&token_type=bearer&expires_in=43193&scope=all
             */
            if ("client4".equals(clientId))
            {
                BaseClientDetails clientDetails = new BaseClientDetails();
                clientDetails.setClientId(clientId);
                clientDetails.setAuthorizedGrantTypes(Arrays.asList("implicit", "refresh_token"));
                clientDetails.setScope(Collections.singletonList("all"));
                clientDetails.setClientSecret(passwordEncoder.encode("123456"));
                clientDetails.setRegisteredRedirectUri(Collections.singleton("http://localhost:8082/oauth"));
                return clientDetails;
            }

            /**
             * 刷新授权码
             * POST /oauth/token?grant_type=refresh_token&client_id=client1&client_secret=123456&refresh_token=5d8b83bb-8939-4767-93b5-6f317113eaf2
             */

            throw new ClientRegistrationException("invalid client detail");
        };
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception
    {
        endpoints.tokenStore(new InMemoryTokenStore())
                .authenticationManager(authenticationManager)
                // 刷新授权码时需要设置 userDetailsService
                .userDetailsService(userDetailsService);
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception
    {
        oauthServer.allowFormAuthenticationForClients()
                .tokenKeyAccess("permitAll()")
                .checkTokenAccess("isAuthenticated()");
    }
}