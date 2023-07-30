package org.trungdd.virtualfilesystem;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class ManualController {

    @RequestMapping(value = "/manual", method = RequestMethod.GET)
    public String serveRequest() {
        return "manual";
    }
}
