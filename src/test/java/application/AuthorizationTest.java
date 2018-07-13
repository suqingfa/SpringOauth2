package application;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Collections;

@Slf4j
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class AuthorizationTest
{
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void passwordTest() throws Exception
    {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.put("username", Collections.singletonList("user"));
        map.put("password", Collections.singletonList("123456"));
        map.put("grant_type", Collections.singletonList("password"));
        map.put("scope", Collections.singletonList("all"));

        RequestBuilder builder = MockMvcRequestBuilders.post("/oauth/token")
                .header("Authorization", "Basic " + Base64.encodeBase64String("client1:123456".getBytes()))
                .params(map)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON);

        MockHttpServletResponse response = mockMvc.perform(builder)
                .andReturn()
                .getResponse();

        log.info(response.getContentAsString());
        Assert.assertEquals(response.getStatus(), 200);
    }

    @Test
    public void clientTest() throws Exception
    {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.put("grant_type", Collections.singletonList("client_credentials"));
        map.put("scope", Collections.singletonList("all"));

        RequestBuilder builder = MockMvcRequestBuilders.post("/oauth/token")
                .header("Authorization", "Basic " + Base64.encodeBase64String("client2:123456".getBytes()))
                .params(map)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON);

        MockHttpServletResponse response = mockMvc.perform(builder)
                .andReturn()
                .getResponse();

        log.info(response.getContentAsString());
        Assert.assertEquals(response.getStatus(), 200);
    }
}
