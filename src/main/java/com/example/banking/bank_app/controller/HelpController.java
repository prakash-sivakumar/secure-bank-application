package com.example.banking.bank_app.controller;

import com.example.banking.bank_app.model.Help;
import com.example.banking.bank_app.model.User;
import com.example.banking.bank_app.service.HelpService;
import com.example.banking.bank_app.service.LogService;
import com.example.banking.bank_app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RestController
@RequestMapping(value="/help")
public class HelpController {

    @Autowired
    private HelpService helpService;

    @Autowired
    private LogService logService;

    @Autowired
    private UserService userService;

    @RequestMapping(value="/list/{page}", method= RequestMethod.GET)
    public ModelAndView list(@PathVariable("page") int page) {
        ModelAndView modelAndView = new ModelAndView("help_list");
        PageRequest pageable = PageRequest.of(page - 1, 15);
        Page<Help> helpPage = helpService.getPaginated(pageable);
        int totalPages = helpPage.getTotalPages();
        if(totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1,totalPages).boxed().collect(Collectors.toList());
            modelAndView.addObject("pageNumbers", pageNumbers);
        }
        modelAndView.addObject("activeCheckList", true);
        modelAndView.addObject("helpList", helpPage.getContent());
        return modelAndView;
    }

    @RequestMapping(value = "/helpform", method = RequestMethod.GET)
    public ModelAndView AddHelpForm(@ModelAttribute("message") String message) {
        ModelAndView modelAndView = new ModelAndView();
        Help help = new Help();
        modelAndView.addObject("help", help);
        modelAndView.setViewName("help");
        modelAndView.addObject("message",message);
        return modelAndView;
    }


    @RequestMapping(value="/helpform", method= RequestMethod.POST)
    public ModelAndView issue(@Valid Help help, BindingResult bindingResult, RedirectAttributes redirectAttributes, Authentication authentication) {
        if(bindingResult.hasErrors())
        {
            redirectAttributes.addFlashAttribute("message","Please correct the errors!");
            return new ModelAndView("redirect:/help/helpform");
        }
        Long id =  userService.findUserByEmail(authentication.getName());
        User user = userService.getUserByUserId(id);
        help.setEmail(user.getEmailId());
        help.setMobile(user.getContact());
        help.setAuth_user_id(user.getUserId());
        if(help.getShortdescription() == null || help.getShortdescription().equals("")){
            redirectAttributes.addFlashAttribute("message","Description cannot be empty!");
            return new ModelAndView("redirect:/help/helpform");
        }
        if(help.getTitle() == null || help.getTitle().equals("")){
            redirectAttributes.addFlashAttribute("message","Title cannot be empty!");
            return new ModelAndView("redirect:/help/helpform");
        }
        helpService.saveOrUpdate(help);
        redirectAttributes.addFlashAttribute("message","Submitted! Bank representative will get back you soon!");
        logService.saveLog(authentication.getName(),"Submitted help query");
        return new ModelAndView("redirect:/help/helpform");
    }

//    @GetMapping("/helps")
//    public String helpform(Model model) {
//
//        Help help = new Help();
//        model.addAttribute("help", help);
//        return "help";
//    }
//
//    @RequestMapping(value = "/helps", method = RequestMethod.POST)
//    public String formSubmit(@Valid Help help, BindingResult bindingResult, Model model) {
//        //check for errors
//        if (bindingResult.hasErrors()) {
//            return "help";
//        }
//
//        //if there are no errors, show form success screen
//        return "help_success";
//    }
}
