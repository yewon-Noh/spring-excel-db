package spring.exceldb.exhibition;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import spring.exceldb.exhibition.service.ExhibitionService;

@Controller
public class ExhibitionController {

    private final ExhibitionService service;

    @Autowired
    ExhibitionController(ExhibitionService service) {
        this.service = service;
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String main(Model model) {
        model.addAttribute("ALL", service.findAll());
        model.addAttribute("SPECIAL", service.findByCategory("특별전"));
        model.addAttribute("THEME", service.findByCategory("테마전"));
        return "main";
    }
}
