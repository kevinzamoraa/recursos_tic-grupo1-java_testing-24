package com.grupo1.recursos_tic.controller;

import com.grupo1.recursos_tic.model.ResourceList;
import com.grupo1.recursos_tic.service.ResourceListsService;

import com.grupo1.recursos_tic.service.ResourceService;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.Setter;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.NoSuchElementException;
import java.util.Objects;

import static com.grupo1.recursos_tic.util.Utility.*;
import static com.grupo1.recursos_tic.util.Utility.userAuth;

@Controller
@AllArgsConstructor
public class ResourceListsController {

    private ResourceListsService resourceListsService;
    private ResourceService resourceService;

    private final String idMsg = "Falta el id o no es un entero positivo";
    private final String notIdMsg = "La lista de recursos no existe";
    private final String dataMsg = "Los datos recibidos no son válidos";
    private final String delMsg = "Error al borrar todas las listas de recursos";

//    @Setter
//    private Long userId;
//
//    @PostConstruct
//    public void init() {
//        if (isAuth()) setUserId(userAuth().get().getId());
//    }

    @GetMapping("resourcelists")
    public String findAll(Model model) {
        model.addAttribute("resourcelists", resourceListsService.findAll(userAuth().get().getId()));
        return "resourcelists/list";
    }

    @GetMapping("resourcelists/{id}")
    public String findById(Model model, @PathVariable Long id) {
        if (invalidIntPosNumber(id) || id == 0)
            throw new NoSuchElementException(idMsg);

        if (!resourceListsService.existsById(id))
            throw new NoSuchElementException(notIdMsg);

        ResourceList resourceList = resourceListsService.findById(id).get();
        if (!Objects.equals(resourceList.getOwner().getId(), userAuth().get().getId()))
            throw new NoSuchElementException(dataMsg);

        model.addAttribute("resourcelist", resourceList);
        model.addAttribute("resources", resourceList.getResources());

        return "resourcelists/detail";
    }

    @GetMapping("resourcelists/create")
    public String getFormToCreate(Model model) {
        ResourceList resourcelist = new ResourceList();
        resourcelist.setOwner(userAuth().get());
        model.addAttribute("resourcelist", resourcelist);
        return "resourcelists/form";
    }

    @GetMapping("resourcelists/update/{id}")
    public String getFormToUpdate(Model model, @PathVariable Long id) {
        if (invalidIntPosNumber(id) || id == 0)
            throw new NoSuchElementException(idMsg);

        if (!resourceListsService.existsById(id))
            throw new NoSuchElementException(notIdMsg);

        ResourceList resourceList = resourceListsService.findById(id).get();
        if (!Objects.equals(resourceList.getOwner().getId(), userAuth().get().getId()))
            throw new NoSuchElementException(dataMsg);

        model.addAttribute("resourcelist", resourceList);
        return "resourcelists/form";
    }

    @PostMapping("resourcelists")
    public String save(Model model, @ModelAttribute ResourceList resourcelist) {
        if (resourcelist == null) throw new NoSuchElementException(dataMsg);
        String error = formValidation(resourcelist);
        if (error != null) throw new NoSuchElementException(error);

        if (resourcelist.getId() == null) { // crear
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

        if (!resourceListsService.existsById(id))
            throw new NoSuchElementException(notIdMsg);

        ResourceList resourceList = resourceListsService.findById(id).get();
        if (!Objects.equals(resourceList.getOwner().getId(), userAuth().get().getId()))
            throw new NoSuchElementException(dataMsg);

        resourceListsService.deleteById(resourceList.getId());
        return "redirect:/resourcelists";
    }

    @GetMapping("resourcelists/delete")
    public String deleteAll(Model model) {
        Long id = userAuth().get().getId();
        resourceListsService.deleteAll(id);
        if (resourceListsService.count(id) != 0)
            throw new NoSuchElementException(delMsg);
        return "redirect:/resourcelists";
    }

    public static String formValidation(ResourceList resourceLists) {
        if (stringIsEmpty(resourceLists.getName())) return "Falta el nombre";
        return null;
    }

}