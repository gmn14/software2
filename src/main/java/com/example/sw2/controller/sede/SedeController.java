package com.example.sw2.controller.sede;

import com.example.sw2.constantes.AsignadosSedesId;
import com.example.sw2.constantes.VentasId;
import com.example.sw2.entity.*;
import com.example.sw2.repository.AsignadosSedesRepository;
import com.example.sw2.repository.InventarioRepository;
import com.example.sw2.repository.UsuariosRepository;
import com.example.sw2.repository.VentasRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;

@Controller
@RequestMapping("/sede")
public class SedeController {

    @Autowired
    AsignadosSedesRepository asignadosSedesRepository;

    @Autowired
    UsuariosRepository usuariosRepository;

    @Autowired
    InventarioRepository inventarioRepository;
    @Autowired
    VentasRepository ventasRepository;

    @GetMapping(value = {"/",""}) public String init(){
        return "redirect:/sede/tienda";}

    @GetMapping("productosPorConfirmar")
    public String productosPorConfirmar(HttpSession session, Model model){

        Usuarios sede = (Usuarios) session.getAttribute("usuario");

        model.addAttribute("listaProductosPorConfirmar",asignadosSedesRepository.buscarPorSede(sede.getIdusuarios()));
        return "sede/ListaProductosPorConfirmar";

    }

    @GetMapping("productosConfirmados")
    public String productosConfirmados(@ModelAttribute("venta") Ventas ventas,HttpSession session, Model model){

        Usuarios sede = (Usuarios) session.getAttribute("usuario");

        model.addAttribute("listaProductosConfirmados",asignadosSedesRepository.buscarPorSede(sede.getIdusuarios()));
        return "sede/ListaProductosConfirmados";

    }

    @PostMapping("registrarVenta")
    public String registrarVenta(@ModelAttribute("venta") @Valid Ventas ventas,
                          BindingResult bindingResult, RedirectAttributes attr, HttpSession session, Model model) {

        if(bindingResult.hasErrors()){
            Usuarios sede = (Usuarios) session.getAttribute("usuario");
            model.addAttribute("venta",ventasRepository.findById(new VentasId(ventas.getId().getTipodocumento(),ventas.getId().getNumerodocumento())));
            model.addAttribute("msgError", "ERROR");
            model.addAttribute("listaProductosConfirmados",asignadosSedesRepository.buscarPorSede(sede.getIdusuarios()));
            return "sede/ListaProductosConfirmados";
        }
        else {

            Optional<Ventas> optVenta = ventasRepository.findById(new VentasId(ventas.getId().getTipodocumento(),ventas.getId().getNumerodocumento()));

            if (optVenta.isPresent()) {
                Usuarios sede = (Usuarios) session.getAttribute("usuario");
                model.addAttribute("msgBoleta", "El codigo de esta venta ya ha sido registrada");
                model.addAttribute("msgError", "ERROR");
                model.addAttribute("listaProductosConfirmados",asignadosSedesRepository.buscarPorSede(sede.getIdusuarios()));
                return "sede/ListaProductosConfirmados";
            }
            else {
                Ventas venta = optVenta.get();
                attr.addFlashAttribute("msgExito", "Venta registrada exitosamente");
                ventasRepository.save(venta);
                return "redirect:/sede/productosConfirmados";
            }


        }
    }

    @PostMapping("confirmarRecepcion")
    public String confirmarRecepcion(@RequestParam(value = "idgestor") int idgestor,
                                       @RequestParam(value = "idsede") int idsede,
                                       @RequestParam(value = "idproductoinv") String idproductoinv,
                                       @RequestParam(value = "idestadoasign") int idestadoasign,
                                       @RequestParam(value = "idprecioventa") Float idprecioventa){

        Optional<AsignadosSedes> asignadosSedesOptional = asignadosSedesRepository.findById( new AsignadosSedesId(usuariosRepository.findById(idgestor).get(), usuariosRepository.findById(idsede).get(),
                inventarioRepository.findById(idproductoinv).get(),
                idestadoasign, idprecioventa));

        if (asignadosSedesOptional.isPresent()){
            AsignadosSedes asignadosSedes = asignadosSedesOptional.get();
            asignadosSedes.getId().setEstadoasignacion(2);
        }
            return "redirect:/sede/productosPorConfirmar";

    }

    @PostMapping("registrarProblema")
    public String registrarProblema(@RequestParam(value = "mensaje") String mensaje,
                                    @RequestParam(value = "idgestor") int idgestor,
                                    @RequestParam(value = "idsede") int idsede,
                                    @RequestParam(value = "idproductoinv") String idproductoinv,
                                    @RequestParam(value = "idestadoasign") int idestadoasign,
                                    @RequestParam(value = "idprecioventa") Float idprecioventa){

        Optional<AsignadosSedes> asignadosSedesOptional = asignadosSedesRepository.findById( new AsignadosSedesId(usuariosRepository.findById(idgestor).get(), usuariosRepository.findById(idsede).get(),
                inventarioRepository.findById(idproductoinv).get(),
                idestadoasign, idprecioventa));

        if (asignadosSedesOptional.isPresent()){
            AsignadosSedes asignadosSedes = asignadosSedesOptional.get();
            asignadosSedes.getId().setEstadoasignacion(3);
            asignadosSedes.setMensaje(mensaje);
        }
        return "redirect:/sede/productosPorConfirmar";

    }

    //Web service
    @ResponseBody
    @GetMapping(value = {"/productosPorConfirmar/get","registrarProblema/get"},produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Optional<AsignadosSedes>> getAsignsede(@RequestParam(value = "idgestor") int idgestor,
                                                                 @RequestParam(value = "idsede") int idsede,
                                                                 @RequestParam(value = "idproductoinv") String idproductoinv,
                                                                 @RequestParam(value = "idestadoasign") int idestadoasign,
                                                                 @RequestParam(value = "idprecioventa") Float idprecioventa){


        return new ResponseEntity<>(asignadosSedesRepository.findById(new AsignadosSedesId(usuariosRepository.findById(idgestor).get(),
                                                                        usuariosRepository.findById(idsede).get(),
                                                                        inventarioRepository.findById(idproductoinv).get(),
                                                                        idestadoasign, idprecioventa)), HttpStatus.OK);
    }


    //Web service
    @ResponseBody
    @PostMapping(value = "/productosPorConfirmar/post",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<HashMap<String,String>> getAsignsedePost(@RequestBody AsignadosSedesId asignadosSedesId){

        return new ResponseEntity<>(new HashMap<String,String>(){{
            asignadosSedesId.setProductoinventario(inventarioRepository.findByCodigoinventario(asignadosSedesId.getProductoinventario().getCodigoinventario()));
            asignadosSedesRepository.findAll();
            AsignadosSedes asignadosSedes = asignadosSedesRepository.findById(asignadosSedesId).orElse(null);
            put("idgestor",Integer.toString(asignadosSedesId.getSede().getIdusuarios()));
            put("idsede",Integer.toString(asignadosSedesId.getGestor().getIdusuarios()));
            put("idproductoinv",asignadosSedesId.getProductoinventario().getCodigoinventario());
            put("idestadoasign", Integer.toString(asignadosSedesId.getEstadoasignacion()));
            put("idprecioventa", Float.toString(asignadosSedesId.getPrecioventa()));
            put("fechaenvio", asignadosSedes!=null? asignadosSedes.getFechaenvio().toString():null);
            put("producto", asignadosSedesId.getProductoinventario().getProductos().getNombre());
            put("precioventa",  asignadosSedes!=null? Float.toString(asignadosSedesId.getPrecioventa()):null);
            put("color",asignadosSedesId.getProductoinventario().getColor());
            put("tamanho", asignadosSedesId.getProductoinventario().getTamanho());
            put("stock", asignadosSedes!=null? String.valueOf(asignadosSedes.getStock()):null);
            put("foto",asignadosSedesId.getProductoinventario().getFoto());
            put("comunidades",asignadosSedesId.getProductoinventario().getComunidades().getNombre());
        }},
                HttpStatus.OK);
    }

}
    