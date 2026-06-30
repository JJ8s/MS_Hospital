package hospital.ms_user.service;

import java.text.Normalizer;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import hospital.ms_user.dto.UsuarioCreateDTO;
import hospital.ms_user.dto.UsuarioResponseDTO;
import hospital.ms_user.exception.BadRequestException;
import hospital.ms_user.mapper.UsuarioMapper;
import hospital.ms_user.model.Rol;
import hospital.ms_user.model.Usuario;
import hospital.ms_user.repository.RolRepository;
import hospital.ms_user.repository.UsuarioRepository;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final UsuarioMapper usuarioMapper;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UsuarioService(
            UsuarioRepository usuarioRepository,
            RolRepository rolRepository,
            UsuarioMapper usuarioMapper
    ) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.usuarioMapper = usuarioMapper;
    }

    public List<UsuarioResponseDTO> obtenerTodos() {
        return usuarioRepository.findAll()
                .stream()
                .map(usuarioMapper::toResponseDTO)
                .toList();
    }

    public Optional<Usuario> obtenerEntidadPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    public Optional<Usuario> obtenerEntidadPorAutoEmail(String autoEmail) {
        return usuarioRepository.findByAutoEmail(autoEmail);
    }

    public UsuarioResponseDTO obtenerPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("El usuario con ID " + id + " no existe"));

        return usuarioMapper.toResponseDTO(usuario);
    }

    public UsuarioResponseDTO crearUsuario(UsuarioCreateDTO dto) {

        if (usuarioRepository.existsByTelefono(dto.getTelefono().trim())) {
            throw new BadRequestException("El número de teléfono ya está en uso");
        }

        Usuario usuario = new Usuario();

        usuario.setNombre(dto.getNombre().trim());
        usuario.setApellido(dto.getApellido().trim());
        usuario.setTelefono(dto.getTelefono().trim());
        usuario.setContrasena(passwordEncoder.encode(dto.getContrasena()));
        usuario.setActive(true);

        String correoGenerado = generarCorreoUnico(dto.getNombre(), dto.getApellido());
        usuario.setAutoEmail(correoGenerado);

        String nombreRol = normalizarRol(dto.getRole());

        Rol rol = rolRepository.findByName(nombreRol)
                .orElseThrow(() -> new BadRequestException("El rol " + nombreRol + " no existe en la base de datos"));

        Set<Rol> roles = new HashSet<>();
        roles.add(rol);
        usuario.setRoles(roles);

        Usuario usuarioGuardado = usuarioRepository.save(usuario);

        return usuarioMapper.toResponseDTO(usuarioGuardado);
    }

    @Transactional
    public void eliminarPorAutoEmail(String autoEmail) {
        if (!usuarioRepository.existsByAutoEmail(autoEmail)) {
            throw new BadRequestException("El usuario con correo " + autoEmail + " no existe");
        }

        usuarioRepository.deleteByAutoEmail(autoEmail);
    }

    public void eliminarPorId(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new BadRequestException("El usuario con ID " + id + " no existe");
        }

        usuarioRepository.deleteById(id);
    }

    private String generarCorreoUnico(String nombre, String apellido) {

        String nombreLimpio = limpiarTexto(nombre);
        String apellidoLimpio = limpiarTexto(apellido);

        if (nombreLimpio.length() < 3) {
            throw new BadRequestException("El nombre debe tener al menos 3 caracteres para generar el correo institucional");
        }

        int letrasNombre = 3;
        int intento = 0;

        while (true) {
            String parteNombre;

            if (letrasNombre + intento <= nombreLimpio.length()) {
                parteNombre = nombreLimpio.substring(0, letrasNombre + intento);
            } else {
                parteNombre = nombreLimpio + intento;
            }

            String correo = parteNombre + "." + apellidoLimpio + "@hospital.com";

            if (!usuarioRepository.existsByAutoEmail(correo)) {
                return correo;
            }

            intento++;
        }
    }

    private String limpiarTexto(String texto) {
        String normalizado = Normalizer.normalize(texto, Normalizer.Form.NFD);

        return normalizado
                .replaceAll("\\p{M}", "")
                .toLowerCase()
                .trim()
                .replaceAll("\\s+", "");
    }

    private String normalizarRol(String role) {
        if (role == null || role.isBlank()) {
            return "ROLE_ADMIN";
        }

        String limpio = role.trim().toUpperCase();

        if (!limpio.startsWith("ROLE_")) {
            limpio = "ROLE_" + limpio;
        }

        return limpio;
    }
}