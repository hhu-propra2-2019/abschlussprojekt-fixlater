package mops.termine2;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class Termine2Controller {
    @GetMapping("/")
    String index(Model m) {
        return "index";
    }

    @GetMapping("/umfragen-abstimmung")
    String umfragenAbstimmung(Model m) {
        return "umfragen-abstimmung";
    }

}
