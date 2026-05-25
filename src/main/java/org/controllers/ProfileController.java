package org.controllers;

import org.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    @Autowired
    private UserService userService;

    @GetMapping("/password")
    public String passwordForm(Model model) {
        return "profile/password";
    }

    @PostMapping("/password")
    public String changePassword(
            @RequestParam String currentPassword,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            Authentication auth,
            RedirectAttributes flash) {

        if (!newPassword.equals(confirmPassword)) {
            flash.addFlashAttribute("error", "La nueva contraseña y su confirmación no coinciden.");
            return "redirect:/profile/password";
        }
        if (newPassword.length() < 6) {
            flash.addFlashAttribute("error", "La nueva contraseña debe tener al menos 6 caracteres.");
            return "redirect:/profile/password";
        }

        boolean changed = userService.changePassword(auth.getName(), currentPassword, newPassword);
        if (!changed) {
            flash.addFlashAttribute("error", "La contraseña actual es incorrecta.");
            return "redirect:/profile/password";
        }

        flash.addFlashAttribute("success", "Contraseña actualizada exitosamente.");
        return "redirect:/dashboard";
    }
}
