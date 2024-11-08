package com.grupo1.recursos_tic.controller;

import com.grupo1.recursos_tic.repository.UserRepo;
import com.grupo1.recursos_tic.model.ResourceList;
import com.grupo1.recursos_tic.service.ResourceListsService;

import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.NoSuchElementException;

import static com.grupo1.recursos_tic.util.Validation.*;

@Controller
@AllArgsConstructor
public class ResourceListsController {

    private ResourceListsService resourceListsService;

    // TODO añadido temporalmente
    private UserRepo userRepository;

    private final String idMsg = "Falta el id o no es un entero positivo";
    private final String notIdMsg = "La lista de recursos no existe";
    private final String dataMsg = "Los datos recibidos no son válidos";
    private final String delMsg = "Error al borrar todas las listas de recursos";


    @GetMapping("resourcelists")
    public String findAll(Model model) {
        model.addAttribute("resourcelists", resourceListsService.findAll());
        return "resourcelists/list";
    }

    @GetMapping("resourcelists/{id}")
    public String findById(Model model, @PathVariable Long id) {
        if (invalidIntPosNumber(id) || id == 0)
            throw new NoSuchElementException(idMsg);

        return resourceListsService.findById(id).map(resourceList -> {
            model.addAttribute("resourcelist", resourceList);
            return "resourcelists/detail";
        }).orElseThrow(() -> new NoSuchElementException(notIdMsg));
    }

    @GetMapping("resourcelists/create")
    public String getFormToCreate(Model model) {
        model.addAttribute("resourcelist", new ResourceList());
        return "resourcelists/form";
    }

    @GetMapping("resourcelists/update/{id}")
    public String getFormToUpdate(Model model, @PathVariable Long id) {
        if (invalidIntPosNumber(id) || id == 0)
            throw new NoSuchElementException(idMsg);

        return resourceListsService.findById(id).map(resourceList -> {
            model.addAttribute("resourcelist", resourceList);
            return "resourcelists/form";
        }).orElseThrow(() -> new NoSuchElementException(notIdMsg));
    }

    @PostMapping("resourcelists")
    public String save(Model model, @ModelAttribute ResourceList resourcelist) {
        if (resourcelist == null) throw new NoSuchElementException(dataMsg);
        String error = formValidation(resourcelist);
        if (error != null) throw new NoSuchElementException(error);

        if (resourcelist.getId() == null) { // crear

            // TODO añadido owner temporalmente
            resourcelist.setOwner(userRepository.findById(1L).get());

            resourceListsService.save(resourcelist);
            return "redirect:/resourcelists/" + resourcelist.getId();
        } else { // editar
            return resourceListsService.findById(resourcelist.getId()).map(optResourceList -> {
                BeanUtils.copyProperties(resourcelist, optResourceList);
                resourceListsService.save(optResourceList);
                return "redirect:/resourcelists/" + optResourceList.getId();
            }).orElseThrow(() -> new NoSuchElementException(notIdMsg));
        }
    }

    @GetMapping("resourcelists/delete/{id}")
    public String deleteById(Model model, @PathVariable Long id) {
        if (invalidIntPosNumber(id) || id == 0)
            throw new NoSuchElementException(idMsg);
        return resourceListsService.findById(id).map(resourceList -> {
            resourceListsService.deleteById(resourceList.getId());
            return "redirect:/resourcelists";
        }).orElseThrow(() -> new NoSuchElementException(notIdMsg));
    }

    @GetMapping("resourcelists/delete")
    public String deleteAll(Model model) {
        resourceListsService.deleteAll();
        if (resourceListsService.count() != 0)
            throw new NoSuchElementException(delMsg);
        return "redirect:/resourcelists";
    }

    public static String formValidation(ResourceList resourceLists) {
        if (stringIsEmpty(resourceLists.getName())) return "Falta el nombre";
        return null;
    }

}