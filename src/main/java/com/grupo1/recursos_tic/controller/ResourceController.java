package com.grupo1.recursos_tic.controller;

import com.grupo1.recursos_tic.model.Resource;
import com.grupo1.recursos_tic.service.ResourceService;

import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.NoSuchElementException;

import static com.grupo1.recursos_tic.util.Utility.*;

@Controller
@AllArgsConstructor
public class ResourceController {

    private ResourceService resourceService;

    private final String idMsg = "Falta el id o no es un entero positivo";
    private final String notIdMsg = "El recurso no existe";
    private final String dataMsg = "Los datos recibidos no son válidos";
    private final String delMsg = "Error al borrar todos los recursos";


    @GetMapping("resources")
    public String findAll(Model model) {
        model.addAttribute("resources", resourceService.findAll());
        return "resource/list";
    }

    @GetMapping("resources/{id}")
    public String findById(Model model, @PathVariable Long id) {
        if (invalidIntPosNumber(id) || id == 0)
            throw new NoSuchElementException(idMsg);

        return resourceService.findById(id).map(resource -> {
            model.addAttribute("resource", resource);
            return "resource/detail";
        }).orElseThrow(() -> new NoSuchElementException(notIdMsg));
    }

    @GetMapping("resources/create")
    public String getFormToCreate(Model model) {
        model.addAttribute("resource", new Resource());
        return "resource/form";
    }

    @GetMapping("resources/update/{id}")
    public String getFormToUpdate(Model model, @PathVariable Long id) {
        if (invalidIntPosNumber(id) || id == 0)
            throw new NoSuchElementException(idMsg);

        return resourceService.findById(id).map(resource -> {
            model.addAttribute("resource", resource);
            return "resource/form";
        }).orElseThrow(() -> new NoSuchElementException(notIdMsg));
    }

    @PostMapping("resources")
    public String save(Model model, @ModelAttribute Resource resource) {
        if (resource == null) throw new NoSuchElementException(dataMsg);
        String error = formValidation(resource);
        if (error != null) throw new NoSuchElementException(error);

        if (resource.getId() == null) { // crear
            resourceService.save(resource);
            return "redirect:/resources/" + resource.getId();
        } else { // editar
            return resourceService.findById(resource.getId()).map(optResource -> {
                BeanUtils.copyProperties(resource, optResource);
                resourceService.save(optResource);
                return "redirect:/resources/" + optResource.getId();
            }).orElseThrow(() -> new NoSuchElementException(notIdMsg));
        }
    }

    @GetMapping("resources/delete/{id}")
    public String deleteById(Model model, @PathVariable Long id) {
        if (invalidIntPosNumber(id) || id == 0)
            throw new NoSuchElementException(idMsg);
        return resourceService.findById(id).map(resource -> {
            resourceService.deleteById(resource.getId());
            return "redirect:/resources";
        }).orElseThrow(() -> new NoSuchElementException(notIdMsg));
    }

    @GetMapping("resources/delete")
    public String deleteAll(Model model) {
        resourceService.deleteAll();
        if (resourceService.count() != 0)
            throw new NoSuchElementException(delMsg);
        return "redirect:/resources";
    }

    public static String formValidation(Resource resource) {
        if (stringIsEmpty(resource.getTitle())) return "Falta el título";
        if (stringIsEmpty(resource.getUrl())) return "Faltan la URL";
        if (invalidIntPosNumber((long) resource.getType().ordinal()))
            return "Falta el tipo de recurso";
        return null;
    }

}