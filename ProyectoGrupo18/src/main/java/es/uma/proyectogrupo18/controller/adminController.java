package es.uma.proyectogrupo18.controller;

import es.uma.proyectogrupo18.dao.*;
import es.uma.proyectogrupo18.entity.*;

import es.uma.proyectogrupo18.ui.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class adminController {

    @Autowired

    protected TipoEjercicioRepository tipoEjercicioRepository;
    @Autowired
    protected AdministradorRepository administradorRepository;

    @Autowired
    protected RolTrabajadorRepository rolTrabajadorRepository;

    @Autowired
    protected TrabajadorRepository trabajadorRepository;

    @Autowired
    protected UsuarioRepository usuarioRepository;

    @Autowired
    protected ClienteRepository clienteRepository;

    @Autowired
    protected ComidaRepository comidaRepository;

    @Autowired
    protected EjercicioRepository ejercicioRepository;

    @Autowired
    protected SesionDeEjercicioRepository sesionDeEjercicioRepository;


    @GetMapping("/")
    public String adminHome(HttpSession httpSession) {
        if (!"admin".equals(httpSession.getAttribute("tipo"))) {
            return "sinPermiso";
        } else {
            return "adminHome";
        }
    }
    //////// CRUD USUARIOS  //////////////////////////////////////////////////////////////////////////
    @GetMapping("/Usuarios")
    public String adminUser(@RequestParam(name = "ID", required = false) String id,
                            @RequestParam(name = "usuario", required = false) String usuario,
                            @RequestParam(name = "Nombre", required = false) String nombre,
                            @RequestParam(name = "Apellidos", required = false) String apellidos,
                            @RequestParam(name = "DNI", required = false) String dni,
                            @RequestParam(name = "Edad", required = false) String edad,
                            @RequestParam(name = "Sexo", required = false) String sexo,
                            Model model, HttpSession httpSession) {
        if (!"admin".equals(httpSession.getAttribute("tipo"))) {
            return "sinPermiso";
        }
        Integer ID = null;
        if(id != null && !id.equals("null") && !edad.isEmpty()){
            ID = Integer.parseInt(id);
        }

        Integer Edad = null;
        if(edad != null && !edad.equals("null") && !edad.isEmpty()){
            Edad = Integer.valueOf(edad);
        }

        if(sexo!= null){
            if(sexo.equals("Cualquiera")){
                sexo = null;
            }
        }
        if(dni!= null){
            if(dni.equals("vacio")){
                dni = null;
            }
        }

        if(usuario!= null){
            if(usuario.equals("vacio")){
                usuario = null;
            }
        }

        if(nombre!= null){
            if(nombre.equals("vacio")){
                nombre = null;
            }
        }

        if(apellidos!= null){
            if(apellidos.equals("vacio")){
                apellidos = null;
            }
        }


        List<UsuarioEntity> usuariosRaw = usuarioRepository.findByFiltro(ID, usuario, nombre, apellidos, dni, Edad, sexo);

        List<FiltroUsuario> usuarios = new ArrayList<>();
        for (UsuarioEntity user : usuariosRaw) {
            String Rol  ="";
            if(user.getAdministrador()!=null){
                Rol = "Admin";
            }
            if(user.getCliente()!=null){
                Rol = "Cliente";
            }
            if(user.getTrabajador()!=null){
                Rol = "Trabajador";
            }
            usuarios.add(new FiltroUsuario(user.getId(), user.getUsuario(), user.getNombre(), user.getApellidos(), user.getDni(), user.getEdad(), user.getSexo(), Rol));
        }
        model.addAttribute("usuarios", usuarios);
        model.addAttribute("filtro", new FiltroUsuario());

        return "adminUsuarios";
    }

    @RequestMapping("/filtrar")
    public String doFiltrar(@ModelAttribute("filtro") FiltroUsuario filtro, HttpSession httpSession){
        if (!"admin".equals(httpSession.getAttribute("tipo"))) {
            return "sinPermiso";
        }

        String usuario = null,nombre=null,apellido=null,dni=null,sexo;
        if(filtro.getUsuario().isEmpty()) {
            usuario = "vacio";
        }else{
            usuario = filtro.getUsuario();
        }
        if(filtro.getNombre().isEmpty()) {
            nombre = "vacio";
        }else{
            nombre = filtro.getNombre();
        }
        if(filtro.getApellidos().isEmpty()) {
            apellido = "vacio";
        }else{
            apellido = filtro.getApellidos();
        }
        if(filtro.getDNI().isEmpty()){
            dni = "vacio";
        } else{
            dni = filtro.getDNI();
        }
        if(!filtro.getSexo().equals("Cualquiera")){
            sexo = filtro.getSexo();
        }else {
            sexo="Cualquiera";
        }
        return "redirect:/admin/Usuarios?ID=" + filtro.getID() + "&usuario=" + usuario + "&Nombre=" + nombre + "&Apellidos=" + apellido + "&DNI=" + dni + "&Edad=" + filtro.getEdad() + "&Sexo=" + sexo;
    }
    @GetMapping("/Crear")
    public String doNuevo(Model model, HttpSession session) {
        String strTo = "adminUsuario";
        if (!"admin".equals(session.getAttribute("tipo"))) {
            return "sinPermiso";
        } else {
            UsuarioEntity usuario = new UsuarioEntity();
            usuario.setId(-1);
            List<String> roles = new ArrayList<>();
            roles.add("Cliente");
            roles.add("Admin");
            roles.add("Dietista");
            roles.add("Entrenador Bodybuilding");
            roles.add("Entrenador Cross-training");
            model.addAttribute("roles", roles);
            model.addAttribute("usuario", usuario);
        }

        return strTo;
    }
    @GetMapping("/modificar")
    public String doEditar (@RequestParam("id") Integer id, Model model, HttpSession session) {
        String strTo = "adminUsuario";
        if (!"admin".equals(session.getAttribute("tipo"))) {
            return "sinPermiso";
        } else {

            UsuarioEntity usuario = this.usuarioRepository.findById(id).orElse(null);
            model.addAttribute("usuario", usuario);

            List<String> roles = new ArrayList<>();
            roles.add("Cliente");
            roles.add("Admin");
            roles.add("Dietista");
            roles.add("Entrenador Bodybuilding");
            roles.add("Entrenador Cross-training");
            model.addAttribute("roles", roles);

        }

        return strTo;
    }

    @PostMapping("/guardar")
    public String doGuardar (@RequestParam("id") Integer id,
                             @RequestParam("usuarioName") String usuarioName,
                             @RequestParam("Nombre") String Nombre,
                             @RequestParam("Apellidos") String Apellidos,
                             @RequestParam("DNI") String DNI,
                             @RequestParam("sexo") String sexo,
                             @RequestParam("edad") Integer edad,
                             @RequestParam("Rol") String Rol,
                             @RequestParam("RolPre") String RolPre,
                             HttpSession session) {

        String strTo = "redirect:/admin/Usuarios";
        if (!"admin".equals(session.getAttribute("tipo"))) {
            return "sinPermiso";
        } else {
            UsuarioEntity usuario = this.usuarioRepository.findById(id).orElse(new UsuarioEntity());
            usuario.setUsuario(usuarioName);
            usuario.setNombre(Nombre);
            usuario.setApellidos(Apellidos);
            usuario.setDni(DNI);
            usuario.setSexo(sexo);
            usuario.setEdad(edad);

            if(RolPre.equals("Cliente")) {
                this.clienteRepository.delete(this.clienteRepository.findById(id).orElse(null));
                usuario.setCliente(null);
            }
            if(RolPre.equals("Admin")) {
                this.administradorRepository.delete(this.administradorRepository.findById(id).orElse(null));
                usuario.setAdministrador(null);
            }
            if(RolPre.equals( "Dietista") || RolPre.equals("Entrenador Cross-training") || RolPre.equals("Entrenador Bodybuilding")) {
                usuario.setTrabajador(null);
                this.trabajadorRepository.delete(this.trabajadorRepository.findById(id).orElse(null));

            }

            if(Rol.equals("Admin")){
                this.usuarioRepository.saveAndFlush(usuario);
                UsuarioEntity usuarioC = this.usuarioRepository.findById(usuario.getId()).orElse(null);

                AdministradorEntity administrador = new AdministradorEntity();
                administrador.setId(usuarioC.getId());
                administrador.setUsuario(usuarioC);
                usuarioC.setAdministrador(administrador);

                this.usuarioRepository.saveAndFlush(usuarioC);
                this.administradorRepository.saveAndFlush(administrador);
            }



            if(Rol.equals("Cliente")){
                this.usuarioRepository.saveAndFlush(usuario);
                UsuarioEntity usuarioC = this.usuarioRepository.findById(usuario.getId()).orElse(null);

                ClienteEntity cliente = new ClienteEntity();
                cliente.setId(usuarioC.getId());
                cliente.setUsuario(usuarioC);
                usuarioC.setCliente(cliente);

                this.usuarioRepository.saveAndFlush(usuarioC);
                this.clienteRepository.saveAndFlush(cliente);


            }
            if(Rol.equals( "Dietista") || Rol.equals("Entrenador Cross-training") || Rol.equals("Entrenador Bodybuilding")){
                Integer rolId = Rol.equals( "Dietista")?1:Rol.equals("Entrenador Cross-training")?2:3;
                this.usuarioRepository.saveAndFlush(usuario);
                UsuarioEntity usuarioC = this.usuarioRepository.findById(usuario.getId()).orElse(null);

                TrabajadorEntity trabajador = new TrabajadorEntity();
                trabajador.setId(usuarioC.getId());
                trabajador.setUsuario(usuarioC);
                trabajador.setRol(this.rolTrabajadorRepository.findById(rolId).orElse(null));
                usuarioC.setTrabajador(trabajador);

                this.usuarioRepository.saveAndFlush(usuarioC);
                this.trabajadorRepository.saveAndFlush(trabajador);

            }

        }

        return strTo;
    }

    @GetMapping("/eliminar")
    public String doBorrar (@RequestParam("id") Integer id, HttpSession session) {
        String strTo = "redirect:/admin/Usuarios";
        if (!"admin".equals(session.getAttribute("tipo"))) {
            return "sinPermiso";
        } else {
            this.usuarioRepository.deleteById(id);
        }
        return strTo;
    }

    //////// AUTENTICAR USUARIOS  //////////////////////////////////////////////////////////////////////////

    @GetMapping("/autenticar")
    public String doAutenticar (Model model, HttpSession session){
        String strTo = "adminAutenticar";
        if (!"admin".equals(session.getAttribute("tipo"))) {
            return "sinPermiso";
        } else {

            List<UsuarioEntity> usuarios = (List<UsuarioEntity>) this.usuarioRepository.findBySinPassword();
            model.addAttribute("usuarios", usuarios);


        }

        return strTo;

    }

    @GetMapping("/autenticacion")
    public String doAutenticacion (@RequestParam("id") Integer id, Model model, HttpSession session){
        String strTo = "adminAutenticacion";
        if (!"admin".equals(session.getAttribute("tipo"))) {
            return "sinPermiso";
        } else {
            UsuarioEntity usuario = this.usuarioRepository.findById(id).orElse(null);
            model.addAttribute("usuario",usuario);
        }

        return strTo;
    }

    @PostMapping("/autenticado")
    public String doAutenticado (@RequestParam("id") Integer id,
                             @RequestParam("passw") String passw,
                             HttpSession session) {

        String strTo = "redirect:/admin/autenticar";
        if (!"admin".equals(session.getAttribute("tipo"))) {
            return "sinPermiso";
        } else {
            UsuarioEntity usuario = this.usuarioRepository.findById(id).orElse(new UsuarioEntity());
            usuario.setContrasena(passw);
            this.usuarioRepository.saveAndFlush(usuario);
        }
        return strTo;
    }
    //////// ASIGNAR USUARIOS  //////////////////////////////////////////////////////////////////////////

    @GetMapping("/asignarLista")
    public String doListaAsignar (Model model, HttpSession session){
        String strTo = "adminAsignarLista";
        if (!"admin".equals(session.getAttribute("tipo"))) {
            return "sinPermiso";
        } else {
            List<UsuarioEntity> usuarios = (List<UsuarioEntity>) this.usuarioRepository.findUsuariosClientes();
            model.addAttribute("usuarios", usuarios);
        }
        return strTo;
    }

    @GetMapping("/asignar")
    public String doAsignar (@RequestParam("id") Integer id, Model model, HttpSession session){
        String strTo = "adminAsignar";
        if (!"admin".equals(session.getAttribute("tipo"))) {
            return "sinPermiso";
        } else {
            ClienteEntity cliente = this.clienteRepository.findById(id).orElse(null);
            model.addAttribute("cliente",cliente);
            List<UsuarioEntity> entrenadores = this.usuarioRepository.findByEntrenadores();
            model.addAttribute("entrenadores",entrenadores);
            List<UsuarioEntity> diestistas = this.usuarioRepository.findByDietista();
            model.addAttribute("diestistas",diestistas);
        }
        return strTo;
    }

    @PostMapping("/asignado")
    public String doAsigado (@RequestParam("id") Integer id,
                             @RequestParam("entrenadorPre") int entrenadorPre,
                             @RequestParam("dietistaPre") int dietistaPre,
                             @RequestParam("entrenador") int entrenador,
                             @RequestParam("dietista") int dietista,
                                 HttpSession session) {

        String strTo = "redirect:/admin/asignarLista";
        if (!"admin".equals(session.getAttribute("tipo"))) {
            return "sinPermiso";
        } else {
            ClienteEntity cliente = this.clienteRepository.findById(id).orElse(null);
            if(entrenadorPre != 0 && entrenadorPre!=entrenador && entrenador == 0){
                cliente.setEntrenador(null);
                this.clienteRepository.saveAndFlush(cliente);
                TrabajadorEntity entrenadorP = this.trabajadorRepository.findById(entrenadorPre).orElse(null);
                entrenadorP.getClientesEntrenador().remove(cliente);
                this.trabajadorRepository.saveAndFlush(entrenadorP);
            }
            if(entrenadorPre!=0&&entrenador!=0&&entrenadorPre!=entrenador){
                TrabajadorEntity entrenadorN = this.trabajadorRepository.findById(entrenador).orElse(null);
                cliente.setEntrenador(entrenadorN);
                this.clienteRepository.saveAndFlush(cliente);
                entrenadorN.getClientesEntrenador().add(cliente);
                this.trabajadorRepository.saveAndFlush(entrenadorN);
                TrabajadorEntity entrenadorP = this.trabajadorRepository.findById(entrenadorPre).orElse(null);
                entrenadorP.getClientesEntrenador().remove(cliente);
                this.trabajadorRepository.saveAndFlush(entrenadorP);
            }
            if(entrenadorPre == 0 && entrenador!=0&&entrenadorPre!=entrenador){
                TrabajadorEntity entrenadorN = this.trabajadorRepository.findById(entrenador).orElse(null);
                cliente.setEntrenador(entrenadorN);
                this.clienteRepository.saveAndFlush(cliente);
                entrenadorN.getClientesEntrenador().add(cliente);
                this.trabajadorRepository.saveAndFlush(entrenadorN);
            }

            cliente = this.clienteRepository.findById(id).orElse(null);
            if(dietistaPre != 0 && dietistaPre!=dietista && dietista == 0){
                cliente.setDietista(null);
                this.clienteRepository.saveAndFlush(cliente);
                TrabajadorEntity dietistaP = this.trabajadorRepository.findById(dietistaPre).orElse(null);
                dietistaP.getClientesDietista().remove(cliente);
                this.trabajadorRepository.saveAndFlush(dietistaP);
            }
            if(dietistaPre!=0&&dietista!=0&&dietistaPre!=dietista){
                TrabajadorEntity dietistaN = this.trabajadorRepository.findById(dietista).orElse(null);
                cliente.setDietista(dietistaN);
                this.clienteRepository.saveAndFlush(cliente);
                dietistaN.getClientesDietista().add(cliente);
                this.trabajadorRepository.saveAndFlush(dietistaN);
                TrabajadorEntity dietistaP = this.trabajadorRepository.findById(dietistaPre).orElse(null);
                dietistaP.getClientesDietista().remove(cliente);
                this.trabajadorRepository.saveAndFlush(dietistaP);
            }
            if(dietistaPre == 0 && dietista!=0&&dietistaPre!=dietista){
                TrabajadorEntity dietistaN = this.trabajadorRepository.findById(dietista).orElse(null);
                cliente.setDietista(dietistaN);
                this.clienteRepository.saveAndFlush(cliente);
                dietistaN.getClientesDietista().add(cliente);
                this.trabajadorRepository.saveAndFlush(dietistaN);
            }
        }
        return strTo;
    }

    //////// CRUD   //////////////////////////////////////////////////////////////////////////


    @GetMapping("/ListaCRUD")
    public String doListaCRUD(
            @RequestParam(name = "ifEj", required = false, defaultValue = "true") Boolean ifEj,
            @RequestParam(name = "ifComida", required = false, defaultValue = "true") Boolean ifComida,
            @RequestParam(name = "ifSe", required = false, defaultValue = "true") Boolean ifSe,
            @RequestParam(name = "comidaNombre", required = false, defaultValue = "vacio") String comidaNombre,
            @RequestParam(name = "comidaCal", required = false, defaultValue = "-1") Integer comidaCal,
            @RequestParam(name = "ejTipo", required = false, defaultValue = "vacio") String ejTipo,
            @RequestParam(name = "ejNombre", required = false, defaultValue = "vacio") String ejNombre,
            @RequestParam(name = "seRep", required = false, defaultValue = "vacio") String seRep,
            @RequestParam(name = "seCan", required = false, defaultValue = "vacio") String seCan,
            @RequestParam(name = "seEj", required = false, defaultValue = "vacio") String seEj,
            Model model, HttpSession session) {

        if (!"admin".equals(session.getAttribute("tipo"))) {
            return "sinPermiso";
        }

        // Validaciones y asignaciones para comida
        if ("vacio".equals(comidaNombre)) {
            comidaNombre = null;
        }

        // Validaciones y asignaciones para ej
        if ("vacio".equals(ejNombre)) {
            ejNombre = null;
        }

        // Validaciones y asignaciones para se
        if ("vacio".equals(seCan)) {
            seCan = null;
        }
        if ("vacio".equals(seEj)) {
            seEj = null;
        }

        if(comidaCal == -1){
            comidaCal = null;
        }
        if(ejTipo.equals("vacio")){
            ejTipo = null;
        }
        if(seRep.equals("vacio")){
            seRep = null;
        }

        // Realizar las búsquedas de acuerdo a los filtros
        List<ComidaEntity> comidasRaw = comidaRepository.findByFiltro(comidaNombre, comidaCal);
        List<EjercicioEntity> ejerciciosRaw = ejercicioRepository.findByFiltro(ejTipo, ejNombre);
        List<SesionDeEjercicioEntity> sesionesRaw = sesionDeEjercicioRepository.findByFiltro(seRep, seCan, seEj);

        // Convertir las entidades a objetos de filtro si es necesario
        List<FiltroCRUD> comidas = new ArrayList<>();
        for (ComidaEntity c : comidasRaw) {
            comidas.add(new FiltroCRUD(c.getId(),1, c.getNombre(), c.getKilocaloriasTotales()));
        }

        List<FiltroCRUD> ejercicios = new ArrayList<>();
        for (EjercicioEntity e : ejerciciosRaw) {
            ejercicios.add(new FiltroCRUD(e.getId(),2, e.getTipo().getTipo(), e.getNombre()));
        }

        List<FiltroCRUD> sesiones = new ArrayList<>();
        for (SesionDeEjercicioEntity s : sesionesRaw) {
            sesiones.add(new FiltroCRUD(s.getId(),3, s.getRepeticiones(), s.getCantidad(), s.getEjercicio().getNombre()));
        }

        // Añadir los resultados al modelo
        model.addAttribute("comidas", comidas);
        model.addAttribute("ejercicios", ejercicios);
        model.addAttribute("sesiones", sesiones);
        model.addAttribute("filtro", new FiltroCRUD(0,0,ifComida,ifEj,ifSe,comidaNombre,comidaCal,ejTipo,ejNombre,seRep,seCan,seEj));
        return "adminListaCRUD";
    }


    @RequestMapping("/filtrarCRUD")
    public String doFiltrarCRUD(@ModelAttribute("filtro") FiltroCRUD filtro, HttpSession session) {
        if (!"admin".equals(session.getAttribute("tipo"))) {
            return "sinPermiso";
        }

        String comidaNombre = "vacio";
        // Preparar los parámetros para la redirección de Comida
        if (filtro.getComidaNombre() != null) {
            comidaNombre = filtro.getComidaNombre();
        }
        String ejNombre = "vacio";
        // Preparar los parámetros para la redirección de Ejercicio
        if (filtro.getejNombre() != null) {
            ejNombre = filtro.getejNombre();
        }
        String ejTipo = "vacio";
        // Preparar los parámetros para la redirección de Ejercicio
        if (filtro.getejTipo() != null) {
            ejTipo = filtro.getejTipo();
        }
        String seCan = "vacio";
        // Preparar los parámetros para la redirección de Sesion
        if (filtro.getseCantidad() != null) {
            seCan = filtro.getseCantidad();
        }
        String seEj = "vacio";
        // Preparar los parámetros para la redirección de Sesion
        if (filtro.getseEjercicio() != null) {
            seEj = filtro.getseEjercicio();
        }
        String seRep = "vacio";
        // Preparar los parámetros para la redirección de Sesion
        if (filtro.getseRepeticiones() != null) {
            seRep = filtro.getseRepeticiones();
        }

        Integer comidaCal = -1;
        if(filtro.getComidaCalorias()!=null){
            comidaCal = filtro.getComidaCalorias();
        }

        // Redireccionar con los parámetros preparados
        return "redirect:/admin/ListaCRUD?ifEj=" + filtro.getIfEj() + "&ifComida=" + filtro.getIfComida() + "&ifSe=" + filtro.getIfSe()
                + "&comidaNombre=" + comidaNombre + "&comidaCal=" + comidaCal
                + "&ejTipo=" + ejTipo + "&ejNombre=" + ejNombre
                + "&seRep=" + seRep + "&seCan=" + seCan + "&seEj=" + seEj ;
    }
    @GetMapping("/modificarCRUD")
    public String domodificarCRUD (@RequestParam("id") Integer id,@RequestParam("tipo") Integer tipo,Model model, HttpSession session) {
        String strTo = "adminCRUDnew";
        if (!"admin".equals(session.getAttribute("tipo"))) {
            return "sinPermiso";
        } else {
            FiltroCRUD filtroMod = null;
            if(tipo==1){
                ComidaEntity comida = this.comidaRepository.findById(id).orElse(null);
                filtroMod = new FiltroCRUD(comida.getId(),1,comida.getNombre(),comida.getKilocaloriasTotales());
            }else if(tipo == 2){
                EjercicioEntity ejercicio = this.ejercicioRepository.findById(id).orElse(null);
                filtroMod = new FiltroCRUD(ejercicio.getId(),2,ejercicio.getTipo().getTipo(),ejercicio.getNombre());
            }else if(tipo == 3){
                SesionDeEjercicioEntity sesion = this.sesionDeEjercicioRepository.findById(id).orElse(null);
                filtroMod = new FiltroCRUD(sesion.getId(),3,sesion.getRepeticiones(),sesion.getCantidad(),sesion.getEjercicio().getNombre());
            }
            model.addAttribute("filtroMod", filtroMod);
            List<TipoEjercicioEntity> tiposEj = this.tipoEjercicioRepository.findAll();
            model.addAttribute("tiposEj",tiposEj);
            List<EjercicioEntity> ejercicios = this.ejercicioRepository.findAll();
            model.addAttribute("ejercicios",ejercicios);
        }

        return strTo;
    }

    @GetMapping("/CrearCRUD")
    public String doCrearCRUD (@RequestParam("tipo") Integer tipo,Model model, HttpSession session) {
        String strTo = "adminCRUDnew";
        if (!"admin".equals(session.getAttribute("tipo"))) {
            return "sinPermiso";
        } else {
            FiltroCRUD filtroMod = null;
            if(tipo==1){
                ComidaEntity comida = new ComidaEntity();
                filtroMod = new FiltroCRUD(comida.getId(),1,comida.getNombre(),comida.getKilocaloriasTotales());
            }else if(tipo == 2){
                EjercicioEntity ejercicio = new EjercicioEntity();
                filtroMod = new FiltroCRUD(ejercicio.getId(),2,null,ejercicio.getNombre());
            }else if(tipo == 3){
                SesionDeEjercicioEntity sesion = new SesionDeEjercicioEntity();
                filtroMod = new FiltroCRUD(sesion.getId(),3,sesion.getRepeticiones(),sesion.getCantidad(),null);
            }
            model.addAttribute("filtroMod", filtroMod);
            List<TipoEjercicioEntity> tiposEj = this.tipoEjercicioRepository.findAll();
            model.addAttribute("tiposEj",tiposEj);
            List<EjercicioEntity> ejercicios = this.ejercicioRepository.findAll();
            model.addAttribute("ejercicios",ejercicios);
        }
System.out.println("///////////////////////////////////////" + tipo);

        return strTo;
    }

    @PostMapping("/guardarCRUD")
    public String doGuardar (@RequestParam(name = "id", required = false) Integer id,
                             @RequestParam("tipo") Integer tipo,
                             @RequestParam(name = "ComidaName", required = false) String ComidaName,
                             @RequestParam(name = "ComidaCal", required = false) Integer ComidaCal,
                             @RequestParam(name = "EjName", required = false) String EjName,
                             @RequestParam(name = "EjTipo", required = false) String EjTipo,
                             @RequestParam(name = "SeRep", required = false) String SeRep,
                             @RequestParam(name = "SeCan", required = false) String SeCan,
                             @RequestParam(name = "SeEj", required = false) Integer SeEj,
                             HttpSession session) {

        String strTo = "redirect:/admin/ListaCRUD";
        if (!"admin".equals(session.getAttribute("tipo"))) {
            return "sinPermiso";
        } else {
            if (tipo == 1) {
                ComidaEntity comida = this.comidaRepository.findById(id).orElse(new ComidaEntity());
                comida.setNombre(ComidaName);
                comida.setKilocaloriasTotales(ComidaCal);
                this.comidaRepository.saveAndFlush(comida);
            } else if (tipo == 2) {
                EjercicioEntity ejercicio = this.ejercicioRepository.findById(id).orElse(new EjercicioEntity());
                ejercicio.setNombre(EjName);
                ejercicio.setTipo(this.tipoEjercicioRepository.findByNombre(EjTipo));
                this.ejercicioRepository.saveAndFlush(ejercicio);
            } else if (tipo == 3) {
                SesionDeEjercicioEntity sesion = this.sesionDeEjercicioRepository.findById(id).orElse(new SesionDeEjercicioEntity());
                sesion.setCantidad(SeCan);
                sesion.setRepeticiones(SeRep);
                sesion.setEjercicio(this.ejercicioRepository.findById(SeEj).orElse(null));
                this.sesionDeEjercicioRepository.saveAndFlush(sesion);

                EjercicioEntity ejercicio = this.ejercicioRepository.findById(SeEj).orElse(null);
                if (ejercicio != null) {
                    ejercicio.getSesionDeEjercicios().add(sesion);
                    this.ejercicioRepository.saveAndFlush(ejercicio);
                }
            }
        }
        return strTo;
    }


    @GetMapping("/eliminarCRUD")
    public String doBorrarCRUD (@RequestParam("id") Integer id,@RequestParam("tipo") Integer tipo, HttpSession session) {
        String strTo = "redirect:/admin/ListaCRUD";
        if (!"admin".equals(session.getAttribute("tipo"))) {
            return "sinPermiso";
        } else {
            if(tipo==1){
                this.comidaRepository.deleteById(id);
            }else if(tipo == 2){
                this.ejercicioRepository.deleteById(id);
            }else if(tipo == 3){
                this.sesionDeEjercicioRepository.deleteById(id);
            }
        }
        return strTo;
    }
}
