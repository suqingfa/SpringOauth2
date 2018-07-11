package application;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;

@Controller
@RequestMapping
public class MainController
{
    @RequestMapping("/")
    @ResponseBody
    public Principal index(Principal principal)
    {
        return principal;
    }
}
