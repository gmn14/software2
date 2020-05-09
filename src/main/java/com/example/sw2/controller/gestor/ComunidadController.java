package com.example.sw2.controller.gestor;

import com.example.sw2.entity.Categorias;
import com.example.sw2.entity.Comunidades;
import com.example.sw2.repository.ComunidadesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Optional;

@Controller
@RequestMapping("/gestor/comunidad")
public class ComunidadController {

    @Autowired
    ComunidadesRepository comunidadesRepository;

    @GetMapping(value = {"", "/"})
    public String listCom(@ModelAttribute("comunidad") Categorias cat, Model model) {
        model.addAttribute("lista", comunidadesRepository.findAll());
        return "gestor/comunidades";
    }

    @PostMapping("/save")
    public String editCom(@ModelAttribute("comunidad") @Valid Comunidades comunidades,
                          BindingResult bindingResult, RedirectAttributes attr, Model model) {
        if(bindingResult.hasErrors()){
            model.addAttribute("lista", comunidadesRepository.findAll());
            model.addAttribute("msg", "ERROR");
            return "gestor/comunidades";
        }
        else {
            Optional<Comunidades> optionalComunidades = comunidadesRepository.findById(comunidades.getCodigo());
            if (optionalComunidades.isPresent()) {
                Comunidades com = optionalComunidades.get();
                System.out.println(com.getCodigo());
                comunidades.setFechamodificacion(LocalDateTime.now());
                comunidades.setFechacreacion(com.getFechacreacion());
                attr.addFlashAttribute("msg", "Comunidad actualizada exitosamente");
            }
            else {
                comunidades.setFechacreacion(LocalDateTime.now());
                attr.addFlashAttribute("msg", "Comunidad creada exitosamente");
            }
            comunidadesRepository.save(comunidades);
            return "redirect:/gestor/comunidad";
        }
    }

    @GetMapping("/delete")
    public String deleteCom(Model model,
                            @RequestParam("codigo") String id,
                            RedirectAttributes attr) {
        Optional<Comunidades> c = comunidadesRepository.findById(id);
        if (c.isPresent()) {
            comunidadesRepository.deleteById(id);
            attr.addFlashAttribute("msg","Comunidad borrada exitosamente");
        }
        return "redirect:/gestor/comunidad";
    }

    //Web service
    @ResponseBody
    @GetMapping(value = "/get",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Optional<Comunidades>> getCom(@RequestParam(value = "id") String id){
        return new ResponseEntity<>(comunidadesRepository.findById(id), HttpStatus.OK);
    }
}