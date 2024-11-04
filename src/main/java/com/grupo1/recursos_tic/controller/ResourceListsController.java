package com.grupo1.recursos_tic.controller;

import com.grupo1.recursos_tic.model.ResourceList;
import com.grupo1.recursos_tic.repository.ResourceListsRepo;
import com.grupo1.recursos_tic.repository.UserRepo;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.NoSuchElementException;

import static com.grupo1.recursos_tic.util.Validation.invalidIntPosNumber;

@Controller
@AllArgsConstructor
public class ResourceListsController {

    private ResourceListsRepo resourceListsRepository;

    // TODO añadido temporalmente
    private UserRepo userRepository;

    private final String idMsg = "Falta el id o no es un entero positivo";
    private final String notIdMsg = "La lista de recursos no existe";
    private final String dataMsg = "Los datos recibidos no son válidos";
    private final String delMsg = "Error al borrar todas las listas de recursos";


    @GetMapping("resourcelists")
    public String findAll(Model model) {
        model.addAttribute("resourcelists", resourceListsRepository.findAll());
        return "resourcelists/list";
    }

    @GetMapping("resourcelists/{id}")
    public String findById(Model model, @PathVariable Long id) {
        if (invalidIntPosNumber(id) || id == 0)
            throw new NoSuchElementException(idMsg);

        return resourceListsRepository.findById(id).map(resourceList -> {
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

        return resourceListsRepository.findById(id).map(resourceList -> {
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
            resourceListsRepository.save(resourcelist);
            return "redirect:/resourcelists/" + resourcelist.getId();
        } else { // editar
            return resourceListsRepository.findById(resourcelist.getId()).map(optResourceList -> {
                BeanUtils.copyProperties(resourcelist, optResourceList);
                resourceListsRepository.save(optResourceList);
                return "redirect:/resourcelists/" + optResourceList.getId();
            }).orElseThrow(() -> new NoSuchElementException(notIdMsg));
        }
    }

    @GetMapping("resourcelists/delete/{id}")
    public String deleteById(Model model, @PathVariable Long id) {
        if (invalidIntPosNumber(id) || id == 0)
            throw new NoSuchElementException(idMsg);
        return resourceListsRepository.findById(id).map(resourceList -> {
            resourceListsRepository.deleteById(resourceList.getId());
            return "redirect:/resourcelists";
        }).orElseThrow(() -> new NoSuchElementException(notIdMsg));
    }

    @GetMapping("resourcelists/delete")
    public String deleteAll(Model model) {
        resourceListsRepository.deleteAll();
        if (resourceListsRepository.count() != 0)
            throw new NoSuchElementException(delMsg);
        return "redirect:/resourcelists";
    }

    // TODO Revisar la incorporación de este método
    public static String formValidation(ResourceList resourceLists) {
//        if (stringIsEmpty(resourceLists.getNombre())) return "Falta el nombre";
//        if (stringIsEmpty(resourceLists.getApellido())) return "Faltan los apellidos";
//        if (stringIsEmpty(resourceLists.getEmail())) return "Falta el email";
//        if (invalidIntPosNumber((long) resourceLists.getEdad()))
//            return "Falta la edad o no es un número entero positivo";
//        if (resourceLists.getEdad() <= 0) return "La edad debe ser mayor que cero";
        return null;
    }

}