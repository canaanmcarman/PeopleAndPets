package com.example.springboot_security403v2;

import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.jws.WebParam;
import javax.validation.Valid;
import java.io.IOException;
import java.security.Principal;
import java.util.Map;

@Controller
public class HomeController {
    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    CloudinaryConfig cloudc;

    @Autowired
    PersonRepository personRepository;

    @Autowired
    PetRepository petRepository;

    @RequestMapping("/")
    public String index(Model model) {
        model.addAttribute("people", personRepository.findAll());
        return "index";
    }

    @GetMapping("/addperson")
    public String addPerson(Model model) {
        model.addAttribute("person", new Person());
        return "personform";
    }
    @PostMapping("/processperson")
    public String processPerson(@ModelAttribute Person person) {
       personRepository.save(person);
       return "redirect:/";
    }

    @GetMapping("/addpet")
    public String addPet(Model model) {
        model.addAttribute("pet", new Pet());
        model.addAttribute("people", personRepository.findAll());
        return "petform";
    }

    @PostMapping("/processpet")
    public String processPet(@ModelAttribute Pet pet) {
        petRepository.save(pet);
        return "redirect:/";
    }

    @RequestMapping("/updateperson/{id}")
    public String updatePerson(@PathVariable("id") long id, Model model) {
       model.addAttribute("person", personRepository.findById(id).get());
       return "personform";
    }
    @RequestMapping("/deleteperson/{id}")
    public String deletePerson(@PathVariable("id") long id) {
        personRepository.deleteById(id);
        return "redirect:/";

    }

    @RequestMapping("updatepet/{id}")
    public String updatePet(@PathVariable("id") long id, Model model) {
        model.addAttribute("pet", petRepository.findById(id).get());
        model.addAttribute("people", personRepository.findAll());
        return "petform";
    }
    @RequestMapping("/deletepet/{id}")
    public String deletePet(@PathVariable("id") long id) {
        petRepository.deleteById(id);
        return "redirect:/";
    }

    @GetMapping("/register")
    public String showRegistrationPage(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/processregister")
    public String processRegistrationPage(@Valid @ModelAttribute("user") User user, BindingResult result, Model model) {
        if (result.hasErrors()) {
            user.clearPassword();
            model.addAttribute("user", user);
            return "register";
        } else {
            model.addAttribute("user", user);
            model.addAttribute("message", "New user account created");
            user.setEnabled(true);
            userRepository.save(user);

            Role role = new Role(user.getUsername(), "ROLE_USER");
            roleRepository.save(role);
        }
        return "redirect:/";

    }

    @RequestMapping("/secure")
    public String secure(Principal principal, Model model) {
        String username = principal.getName();
        model.addAttribute("user", userRepository.findByUsername(username));
        return "secure";

    }

    @RequestMapping("/login")
    public String login() {
        return "login";
    }

    @RequestMapping("/logout")
    public String logout() {
        return  "redirect:/login?logout=true";
    }
}
