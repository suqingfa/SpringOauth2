package application;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
public class MainController
{
    @GetMapping("product")
    public Authentication product()
    {
        return SecurityContextHolder.getContext()
                .getAuthentication();
    }

    @GetMapping("order")
    public Authentication order()
    {
        return SecurityContextHolder.getContext()
                .getAuthentication();
    }
}
